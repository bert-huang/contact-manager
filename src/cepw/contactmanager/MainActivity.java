package cepw.contactmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cepw.contact.*;
import cepw.contactmanager.ContactPopupDialog.ContactAction;
import cepw.contactmanager.SortingDialog.SortType;
import cepw.contactmanager.database.DatabaseHandler;

/**
 * The Main Activity of the contact manager. Displays the contact list and
 * provide search function. User can sort the list by first name, last name, or
 * by phone through the action bar. Tapping on a contact will display the
 * information of the contact.
 * 
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class MainActivity extends Activity implements
		SortingDialog.OnCompleteListener,
		ContactPopupDialog.OnCompleteListener{
	
	private static final String LOG = "MainActivity";

	// Request code
	private static final int CREATE_CONTACT_REQUEST = 1;
	private static final int EDIT_CONTACT_REQUEST = 2;
	private static final int CONTACT_INFO_REQUEST = 3;

	// A variable to store the sorting type
	private static String SORTBY = "SORT_TYPE";
	private static SortType CURRENT_SORT_OPTION;

	// Bundle
	protected static final String CONTACT_NAME = "contactName";
	protected static final String SELECTED_POS = "selectedPosition";
	
	// Database
	private DatabaseHandler db;
	
	// Individual components and list to store contacts.
	private List<Contact> contactsList; // The actual list that stores contacts
	private List<Contact> displayList; // The list for display
	private ListView list;
	private ArrayAdapter<Contact> adapter;
	private EditText searchbar;
	private ImageView searchIcon;
	private TextView foreverAlone;
	private View divider;

	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		getActionBar().setDisplayShowTitleEnabled(false);
		
		db = new DatabaseHandler(getApplicationContext());
		loadDataFromSharedPref();

		foreverAlone = (TextView) findViewById(R.id.textview_no_friend);
		divider = (View) findViewById(R.id.main_activity_separator_1);
		divider.setVisibility(View.GONE);

		searchbar = (EditText) findViewById(R.id.textfield_searchbar);
		searchbar.addTextChangedListener(new OnSearchListener());
		searchbar.setFocusable(false);

		searchIcon = (ImageView) findViewById(R.id.imageview_ic_searchbar);
		searchIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				searchbar.setText("");
				InputMethodManager imm = (InputMethodManager) v.getContext()
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				Utilities.unFocusEditText(searchbar);
			}
		});

		contactsList = db.getAllContacts();
		displayList = new ArrayList<Contact>();
		list = (ListView) findViewById(R.id.listview_contact_list);
		adapter = new ContactListAdapter(MainActivity.this, contactsList);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ListItemClickedListener());
		list.setOnItemLongClickListener(new ListItemClickedListener());
//		MainActivity.CURRENT_SORT_OPTION = SortType.SORT_BY_FIRST_NAME;
		sortList(contactsList, CURRENT_SORT_OPTION);
		adapter.notifyDataSetChanged();

	}

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * @see android.app.Activity#onOptionsItemSelected(MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add_new:
			Intent i = new Intent(getApplicationContext(), EditActivity.class);
			startActivityForResult(i, CREATE_CONTACT_REQUEST);
			return true;

		case R.id.action_sort_options:
			DialogFragment sortDialog = new SortingDialog();
			sortDialog.show(getFragmentManager(), "Sort Option");
			return true;

		case R.id.action_exit:
			onExit();
			return true;

		default:
			return false;
		}
	}

	/**
	 * @see android.app.Activity#onActivityResult(int, int, Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		switch (requestCode) {

		// When coming back from adding a NEW contact
		case CREATE_CONTACT_REQUEST:
			if (resultCode == RESULT_OK) {

				Contact c = (Contact) data.getExtras().getParcelable(
						"NEW_CONTACT");
				contactsList.add(c);
				sortList(contactsList, CURRENT_SORT_OPTION);
				adapter.notifyDataSetChanged();
				int position = contactsList.indexOf(c);
				Intent i = new Intent(getApplicationContext(),
						InfoActivity.class);
				i.putExtra("SELECTED_CONTACT", contactsList.get(position));
				i.putExtra("POSITION", position);
				startActivityForResult(i, CONTACT_INFO_REQUEST);

			}
			break;
		
		// When coming back from InfoActivity
		case CONTACT_INFO_REQUEST:
			if (resultCode == RESULT_OK) {

				String action = data.getStringExtra("ACTION");
				int pos = data.getExtras().getInt("POSITION");

				if (action.equals(InfoActivity.EDIT_CONTACT)) {
					contactsList.set(pos, (Contact)data.getExtras().getParcelable(InfoActivity.MODIFIED_CONTACT));
					Intent intent = new Intent(this, EditActivity.class);
					intent.putExtra("POSITION", pos);
					intent.putExtra("SELECTED_CONTACT", contactsList.get(pos));
					startActivityForResult(intent, EDIT_CONTACT_REQUEST);
					
				} else if (action.equals(InfoActivity.MODIFIED_CONTACT)) {
					contactsList.set(pos, (Contact)data.getExtras().getParcelable(InfoActivity.MODIFIED_CONTACT));
					sortList(contactsList, CURRENT_SORT_OPTION);
					adapter.notifyDataSetChanged();
				} else if (action.equals(InfoActivity.DELETE_CONTACT)) {
					db.deleteContact(contactsList.get(pos).getID());
					Log.d(LOG, "Contacts Removed from DB");
					contactsList.remove(pos);
					Toast.makeText(MainActivity.this, "Contact Removed!",
							Toast.LENGTH_SHORT).show();
					sortList(contactsList, CURRENT_SORT_OPTION);
					adapter.notifyDataSetChanged();

				}
			}
			break;
		
		// When coming back from EditActivity
		case EDIT_CONTACT_REQUEST:
			Intent i = new Intent(getApplicationContext(), InfoActivity.class);
			int position = 0;
			
			switch(resultCode) {
			
				case RESULT_OK:
					Contact c = (Contact) data.getExtras().getParcelable("EDITED_CONTACT");
					
					contactsList.set(data.getExtras().getInt("POSITION"), c);
					sortList(contactsList, CURRENT_SORT_OPTION);
					adapter.notifyDataSetChanged();
					position = contactsList.indexOf(c);
					i.putExtra("SELECTED_CONTACT", contactsList.get(position));
					i.putExtra("POSITION", position);
					startActivityForResult(i, CONTACT_INFO_REQUEST);
					break;
					
				case RESULT_CANCELED:
					position = data.getExtras().getInt("POSITION");
					i.putExtra("SELECTED_CONTACT", contactsList.get(position));
					i.putExtra("POSITION", position);
					startActivityForResult(i, CONTACT_INFO_REQUEST);
					break;
			}
			break;
		}
		
		// Perform search again
		searchbar.setText(searchbar.getText().toString());
	}

	/**
	 * A method that takes in a list and sort it on different comparator
	 * 
	 * @param list list you want to sort
	 * @param sortType the sort type for the list (SortType enum)
	 */
	private void sortList(List<Contact> list, SortType sortType) {
		switch (sortType) {
		case SORT_BY_FIRST_NAME:
			Collections.sort(list,
					new Contact.Comparators.FirstNameComparator());
			break;
		case SORT_BY_LAST_NAME:
			Collections
					.sort(list, new Contact.Comparators.LastNameComparator());
			break;
		case SORT_BY_PHONE:
			Collections.sort(list, new Contact.Comparators.NumberComparator());
			break;
		default:
			Collections.sort(list,
					new Contact.Comparators.FirstNameComparator());
			break;
		}
	}

	/**
	 * This onComplete method is for SortingDialog. It will react according
	 * to what the user selected in the list dialog.
	 */
	@Override
	public void onComplete(SortType sortType) {
		MainActivity.CURRENT_SORT_OPTION = sortType;
		sortList(contactsList, sortType);
		sortList(displayList, sortType);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * This onComplete method is for ContactPopupDialog. It will react according
	 * to what the user selected in the list dialog.
	 */
	@Override
	public void onComplete(ContactAction action, int position) {
		switch(action){
		case SELECTED_DELETE:
			final int pos = position;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete " 
			+ contactsList.get(pos).getName().getFullName() + "?")
					.setTitle("Delete?")
					.setNegativeButton("Cancel", null)
					.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							db.deleteContact(contactsList.get(pos).getID()); // Update database
							contactsList.remove(pos); // simply remove
							adapter.notifyDataSetChanged(); 
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			break;
		case SELECTED_EDIT:
			Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra("POSITION", position);
			intent.putExtra("SELECTED_CONTACT", contactsList.get(position));
			startActivityForResult(intent, EDIT_CONTACT_REQUEST);
			break;
		
		}
	}
	
	/**
	 * @see android.app.Activity#onPause()
	 */
	protected void onPause() {
		super.onPause();
		//Save data to shared preference
		saveDataToSharedPref();
	}
	
	/**
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		//Load data from shared preference
		loadDataFromSharedPref();
	}
	
	/**
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//Save data to shared preference
		saveDataToSharedPref();
		//Close database
		db.closeDB();
	}
	
	/**
	 * Method that saves data to shared preference, closes the database and then exits the app
	 */
	protected void onExit() {
		//Save data to shared preference
		saveDataToSharedPref();
		//Close database
		db.closeDB();
		
		// Destroy the activity
		finish();
	}
	
	/**
	 * Method that handles all data that will be saved to shared preference
	 */
	private void saveDataToSharedPref() {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		
		// Save sort type
		switch (CURRENT_SORT_OPTION) {
		case SORT_BY_FIRST_NAME:
			editor.putInt(SORTBY, 0); break;
		case SORT_BY_LAST_NAME:
			editor.putInt(SORTBY, 1); break;
		case SORT_BY_PHONE:
			editor.putInt(SORTBY, 2); break;
		}
		
		editor.apply();
	}
	
	/**
	 * Method that handles all data that will be loaded from shared preference
	 */
	private void loadDataFromSharedPref() {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		
		// Load sort option
		CURRENT_SORT_OPTION = SortType.values()[pref.getInt(SORTBY, 0)];
	}

	// CLASS
	/**
	 * Custom Adapter for the contact list view
	 */
	private class ContactListAdapter extends ArrayAdapter<Contact> {

		private Context context;
		private ContactFilter contactFilter;

		/**
		 * Constructor of the ArrayAdapter
		 */
		public ContactListAdapter(Context context, List<Contact> contacts) {
			super(context, android.R.layout.simple_expandable_list_item_1,
					contacts);

			this.context = context;
			displayList = contacts;
		}

		/**
		 * @see android.widget.ArrayAdapter#notifyDataSetChanged()
		 */
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if (contactsList.isEmpty()) {
				searchbar.setFocusable(false);
				divider.setVisibility(View.GONE);
				foreverAlone.setVisibility(View.VISIBLE);
			} else {
				searchbar.setFocusableInTouchMode(true);
				divider.setVisibility(View.VISIBLE);
				foreverAlone.setVisibility(View.GONE);
			}
		}

		/**
		 * @see android.widget.ArrayAdapter#getView(int, View, ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Create a layout inflater to inflate our xml layout for each item
			// in the list
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Inflate the list item layout. Keep a reference to the inflated
			// view
			ViewGroup vg = (ViewGroup) inflater.inflate(
					R.layout.contact_list_item, null);

			Contact curContact = displayList.get(position);
			String fullName = Name.parseName(null, curContact.getName()
					.getFirstName(), curContact.getName().getMiddleName(),
					curContact.getName().getLastName(), curContact.getName()
							.getSuffix())[0];

			ImageView iv = (ImageView) vg.getChildAt(0);
			TextView nameTag = (TextView) vg.getChildAt(1);

			iv.setImageBitmap(curContact.getPhoto().getImage());

			if (fullName.equals("")) {
				nameTag.setText("(Unknown)");
			} else {
				nameTag.setText(fullName);
			}

			return (View) vg;

		}

		/**
		 * @see android.widget.ArrayAdapter#getFilter()
		 */
		@Override
		public Filter getFilter() {
			if (contactFilter == null)
				contactFilter = new ContactFilter();
			return contactFilter;
		}

		/**
		 * @see android.widget.ArrayAdapter#getCount()
		 */
		@Override
		public int getCount() {
			return displayList.size();
		}

		/**
		 * Custom filter for the adapter
		 */
		private class ContactFilter extends Filter {

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults();
				// We implement here the filter logic
				if (constraint == null || constraint.length() == 0) {
					// No filter implemented we return all the list
					results.values = contactsList;
					results.count = contactsList.size();
				} else {
					constraint = constraint.toString().trim()
							.replaceAll("[\\s+]", " ");
					String[] splitted = constraint.toString().split(" ");

					// Filter name
					List<Contact> nContacts = new ArrayList<Contact>();
					for (Contact c : contactsList) {
						String fullName = Name.parseName(null, c.getName()
								.getFirstName(), c.getName().getMiddleName(), c
								.getName().getLastName(), c.getName()
								.getSuffix())[0];

						for (String s : splitted) {
							if (fullName.toUpperCase(Locale.US).contains(
									(s.toUpperCase(Locale.US)))
									&& !nContacts.contains(c)) {
								nContacts.add(c);
							}
						}
					}

					results.values = nContacts;
					results.count = nContacts.size();

				}
				return results;
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				// Now we have to inform the adapter about the new list filtered
				displayList = (List<Contact>) results.values;
				notifyDataSetChanged();
			}
		}
	}

	/**
	 * ListItemClickedListener for the list view when an element within is clicked/long clicked
	 */
	private class ListItemClickedListener implements
			AdapterView.OnItemLongClickListener,
			AdapterView.OnItemClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			
			DialogFragment contactDialog = new ContactPopupDialog();
			Bundle args = new Bundle();
			args.putString(CONTACT_NAME, contactsList.get(position).getName().getFullName());
			args.putInt(SELECTED_POS, position);
			contactDialog.setArguments(args);
			contactDialog.show(getFragmentManager(), "Contact Options");
			return true;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(getApplicationContext(), InfoActivity.class);
			int realPosition = contactsList.indexOf(displayList.get(position));
			i.putExtra("SELECTED_CONTACT", contactsList.get(realPosition));
			i.putExtra("POSITION", realPosition);
			startActivityForResult(i, CONTACT_INFO_REQUEST);
		}
	}

	/**
	 * Custom TextWatcher when text changes on the search bar
	 */
	private class OnSearchListener implements TextWatcher {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			MainActivity.this.adapter.getFilter().filter(s.toString());
		}

		@Override
		public void afterTextChanged(Editable s) {
		}

	}

}
