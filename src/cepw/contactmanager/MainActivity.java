package cepw.contactmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
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
		SortingDialog.OnCompleteListener {
	
	private static final String LOG = "MainActivity";

	// Request code
	private static final int CREATE_CONTACT_REQUEST = 1;
	private static final int EDIT_CONTACT_REQUEST = 2;
	private static final int CONTACT_INFO_REQUEST = 3;

	// A variable to store the sorting type
	private static String SORTBY = "SORT_TYPE";
	private static SortType CURRENT_SORT_OPTION;

	private DatabaseHandler db;
	
	// Individual components and list to store contacts.
	private List<Contact> contacts; // The actual list that stores contacts
	private List<Contact> contactList; // The list for display
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

		contacts = db.getAllContacts();
		contactList = new ArrayList<Contact>();
		list = (ListView) findViewById(R.id.listview_contact_list);
		adapter = new ContactListAdapter(MainActivity.this, contacts);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ListItemClickedListener());
		list.setOnItemLongClickListener(new ListItemClickedListener());
//		MainActivity.CURRENT_SORT_OPTION = SortType.SORT_BY_FIRST_NAME;
		sortList(contacts, CURRENT_SORT_OPTION);
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

		if (requestCode == CREATE_CONTACT_REQUEST) {
			if (resultCode == RESULT_OK) {

				Contact c = (Contact) data.getExtras().getParcelable(
						"NEW_CONTACT");
				contacts.add(c);
				sortList(contacts, CURRENT_SORT_OPTION);
				adapter.notifyDataSetChanged();
				int position = contacts.indexOf(c);
				Intent i = new Intent(getApplicationContext(),
						InfoActivity.class);
				i.putExtra("SELECTED_CONTACT", contacts.get(position));
				i.putExtra("POSITION", position);
				startActivityForResult(i, CONTACT_INFO_REQUEST);

			}
		}

		if (requestCode == CONTACT_INFO_REQUEST) {
			if (resultCode == RESULT_OK) {

				String action = data.getStringExtra("ACTION");
				int pos = data.getExtras().getInt("POSITION");

				if (action.equals("EDIT_CONTACT")) {

					Intent intent = new Intent(this, EditActivity.class);
					intent.putExtra("POSITION", pos);
					intent.putExtra("SELECTED_CONTACT", contacts.get(pos));
					startActivityForResult(intent, EDIT_CONTACT_REQUEST);
					
				} else if (action.equals("MODIFIED_CONTACT")) {
					contacts.set(pos, (Contact)data.getExtras().getParcelable("MODIFIED_CONTACT"));
					adapter.notifyDataSetChanged();
				} else if (action.equals("DELETE_CONTACT")) {
					db.deleteContact(contacts.get(pos).getID());
					Log.d(LOG, "Contacts Removed from DB");
					contacts.remove(pos);
					Toast.makeText(MainActivity.this, "Contact Removed!",
							Toast.LENGTH_SHORT).show();
					sortList(contacts, CURRENT_SORT_OPTION);
					adapter.notifyDataSetChanged();

				}
			}
		}
		
		if (requestCode == EDIT_CONTACT_REQUEST) {
			if (resultCode == RESULT_OK) {
				
				Contact c = (Contact) data.getExtras().getParcelable("EDITED_CONTACT");
				contacts.set(data.getExtras().getInt("POSITION"), c);
				sortList(contacts, CURRENT_SORT_OPTION);
				adapter.notifyDataSetChanged();
				int position = contacts.indexOf(c);
				Intent i = new Intent(getApplicationContext(),
						InfoActivity.class);
				i.putExtra("SELECTED_CONTACT", contacts.get(position));
				i.putExtra("POSITION", position);
				startActivityForResult(i, CONTACT_INFO_REQUEST);
			}
		}
		
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
			contactList = contacts;
		}

		/**
		 * @see android.widget.ArrayAdapter#notifyDataSetChanged()
		 */
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if (contacts.isEmpty()) {
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

			Contact curContact = contactList.get(position);
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
			return contactList.size();
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
					results.values = contacts;
					results.count = contacts.size();
				} else {
					constraint = constraint.toString().trim()
							.replaceAll("[\\s+]", " ");
					String[] splitted = constraint.toString().split(" ");

					// Filter name
					List<Contact> nContacts = new ArrayList<Contact>();
					for (Contact c : contacts) {
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
				contactList = (List<Contact>) results.values;
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
			

			Toast.makeText(getApplicationContext(), "Position: "+ position + "\nTODO: Dialog",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(getApplicationContext(), InfoActivity.class);
			int realPosition = contacts.indexOf(contactList.get(position));
			i.putExtra("SELECTED_CONTACT", contacts.get(realPosition));
			i.putExtra("POSITION", realPosition);
			startActivityForResult(i, CONTACT_INFO_REQUEST);
		}
	}

	/**
	 * Custom TextWatcher when text changes on the searchbar
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

	/**
	 * This onComplete method is for SortingDialog. It will react according
	 * to what the user selected in the list dialog.
	 */
	@Override
	public void onComplete(SortType sortType) {
		MainActivity.CURRENT_SORT_OPTION = sortType;
		sortList(contacts, sortType);
		adapter.notifyDataSetChanged();
	}
	
	
	protected void onPause() {
		super.onPause();
		saveDataToSharedPref();
	}
	
	protected void onResume() {
		super.onResume();
		loadDataFromSharedPref();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		saveDataToSharedPref();
		db.closeDB();
	}
	
	protected void onExit() {
		saveDataToSharedPref();
		db.closeDB();
		finish();
	}
	
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
	
	private void loadDataFromSharedPref() {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		CURRENT_SORT_OPTION = SortType.values()[pref.getInt(SORTBY, 0)];
	}
	
}
