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
import android.graphics.BitmapFactory;
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

		// TESTING PURPOSE
/*		
 		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Add dummy contacts for testing purpose?")
				.setTitle("ADD TESTING OBJECTS")
				.setNegativeButton("No", null)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									createDummyObject();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
		AlertDialog alert = builder.create();
		alert.show(); 
*/

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

	/**
	 * Create dummy objects
	 * @throws Exception
	 */
	private void createDummyObject() throws Exception {

		Name c01 = new Name("I-Yang", "Bert", "Huang", "Software Student");
		Photo c02 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c03 = new ArrayList<Phone>();
		c03.add(new Phone("Mobile", "0123456789", true));
		List<Email> c04 = new ArrayList<Email>();
		c04.add(new Email("Home", "ihua164@aucklanduni.ac.nz"));
		List<Address> c05 = new ArrayList<Address>();
		c05.add(new Address("Home", "01 Random Road"));
		DateOfBirth c06 = new DateOfBirth("");
		Contact c00 = new Contact(c01, c02, c03, c04, c05, c06);
		
		Name c11 = new Name("Bert", "", "Huang", "Meh");
		Photo c12 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c13 = new ArrayList<Phone>();
		c13.add(new Phone("Mobile", "0210000000", true));
		List<Email> c14 = new ArrayList<Email>();
		c14.add(new Email("Home", "bert_huang@gmail.com"));
		List<Address> c15 = new ArrayList<Address>();
		c15.add(new Address("Home", "01 Random Road"));
		DateOfBirth c16 = new DateOfBirth("27-01-1993");
		Contact c10 = new Contact(c11, c12, c13, c14, c15, c16);

		Name c21 = new Name("Lucy", "", "Huang", "Doctor");
		Photo c22 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c23 = new ArrayList<Phone>();
		c23.add(new Phone("Mobile", "0220000000", true));
		List<Email> c24 = new ArrayList<Email>();
		c24.add(new Email("Home", "lucy_huang@gmail.com"));
		List<Address> c25 = new ArrayList<Address>();
		c25.add(new Address("Home", "01 Random Road"));
		DateOfBirth c26 = new DateOfBirth("01-10-1991");
		Contact c20 = new Contact(c21, c22, c23, c24, c25, c26);

		Name c31 = new Name("Simon", "", "Huang", "Optom");
		Photo c32 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c33 = new ArrayList<Phone>();
		c33.add(new Phone("Mobile", "0230000000", true));
		List<Email> c34 = new ArrayList<Email>();
		c34.add(new Email("Home", "simon_huang@gmail.com"));
		List<Address> c35 = new ArrayList<Address>();
		c35.add(new Address("Home", "01 Random Road"));
		DateOfBirth c36 = new DateOfBirth("05-07-1990");
		Contact c30 = new Contact(c31, c32, c33, c34, c35, c36);

		Name c41 = new Name("Akshay", "", "Kalyan", "Software Student");
		Photo c42 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c43 = new ArrayList<Phone>();
		c43.add(new Phone("Mobile", "0240000000", true));
		List<Email> c44 = new ArrayList<Email>();
		c44.add(new Email("Work", "akshay_kalyan@aucklanduni.ac.nz"));
		List<Address> c45 = new ArrayList<Address>();
		c45.add(new Address("Home", "20 Genius Cave, Not from Earth"));
		DateOfBirth c46 = new DateOfBirth("25-12-1993");
		Contact c40 = new Contact(c41, c42, c43, c44, c45, c46);

		Name c51 = new Name("Devon", "", "Ahmu", "Civil Engineer");
		Photo c52 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c53 = new ArrayList<Phone>();
		c53.add(new Phone("Mobile", "0250000000", true));
		List<Email> c54 = new ArrayList<Email>();
		c54.add(new Email("Work", "devon_ahmu@aucklanduni.ac.nz"));
		List<Address> c55 = new ArrayList<Address>();
		c55.add(new Address("Home", "20 Man Cave, NZ"));
		DateOfBirth c56 = new DateOfBirth("18-06-1992");
		Contact c50 = new Contact(c51, c52, c53, c54, c55, c56);

		Name c61 = new Name("Matthew", "", "Wang", "Civil Engineer");
		Photo c62 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c63 = new ArrayList<Phone>();
		c63.add(new Phone("Mobile", "0260000000", true));
		List<Email> c64 = new ArrayList<Email>();
		c64.add(new Email("Work", "matthew_wang@aucklanduni.ac.nz"));
		List<Address> c65 = new ArrayList<Address>();
		c65.add(new Address("Home", "20 IDK, NZ"));
		DateOfBirth c66 = new DateOfBirth("02-03-1992");
		Contact c60 = new Contact(c61, c62, c63, c64, c65, c66);

		Name c71 = new Name("Richard", "", "Dyer", "Civil Engineer");
		Photo c72 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c73 = new ArrayList<Phone>();
		c73.add(new Phone("Mobile", "0270000000", true));
		List<Email> c74 = new ArrayList<Email>();
		c74.add(new Email("Work", "richard_dyer@aucklanduni.ac.nz"));
		List<Address> c75 = new ArrayList<Address>();
		c75.add(new Address("Home", "10 IDK, NZ"));
		DateOfBirth c77 = new DateOfBirth("14-11-1994");
		Contact c70 = new Contact(c71, c72, c73, c74, c75, c77);

		Name c81 = new Name("Toby", "", "Jackson", "Biomedical Engineer");
		Photo c82 = new Photo(BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face));
		List<Phone> c83 = new ArrayList<Phone>();
		c83.add(new Phone("Mobile", "0280000000", true));
		List<Email> c84 = new ArrayList<Email>();
		c84.add(new Email("Work", "toby_jackson@aucklanduni.ac.nz"));
		List<Address> c85 = new ArrayList<Address>();
		c85.add(new Address("Home", "30 Genius Cave, Not from Earth"));
		DateOfBirth c88 = new DateOfBirth("09-01-1992");
		Contact c80 = new Contact(c81, c82, c83, c84, c85, c88);

		contacts.add(c00);
		contacts.add(c10);
		contacts.add(c20);
		contacts.add(c30);
		contacts.add(c40);
		contacts.add(c50);
		contacts.add(c60);
		contacts.add(c70);
		contacts.add(c80);
		sortList(contacts, CURRENT_SORT_OPTION);
		adapter.notifyDataSetChanged();

	}

	
}
