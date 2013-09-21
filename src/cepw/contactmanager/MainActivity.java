package cepw.contactmanager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cepw.contact.*;

public class MainActivity extends Activity implements SortingDialog.OnCompleteListener {

	static final int SORT_BY_FIRST_NAME = 1;
	static final int SORT_BY_LAST_NAME = 2;
	static final int SORT_BY_PHONE = 3;
	
	static final int CREATE_CONTACT_REQUEST = 1; // The request code
	static final int CONTACT_INFO_REQUEST = 2;

	private List<Contact> contacts;
	private ListView list;
	private ArrayAdapter<Contact> adapter;
	private int sortBy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		forceCreateOverflow();
		getActionBar().setDisplayShowTitleEnabled(false);

		sortBy = SORT_BY_FIRST_NAME;
		contacts = new ArrayList<Contact>();
		list = (ListView) findViewById(R.id.listview_contact_list);
		adapter = new ContactListAdapter(MainActivity.this, contacts);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ListItemClickedListener());
		list.setOnItemLongClickListener(new ListItemClickedListener());
		
		try {
			createDummyObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_add_new:
			gotoCreateNewContact();
			return true;

		case R.id.action_sort_options:
			DialogFragment sortDialog = new SortingDialog();
			sortDialog.show(getFragmentManager(), "Sort Option");
			return true;
			
		case R.id.action_exit:
			finish();
			return true;

		default:
			return false;
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CREATE_CONTACT_REQUEST) {
			if (resultCode == RESULT_OK) {

				Contact contact = (Contact) data.getExtras().getParcelable("NEW_CONTACT");
				contacts.add(contact);
				sortList(contacts, sortBy);
				adapter.notifyDataSetChanged();
				int position = contacts.indexOf(contact);
				gotoContactInfo(position);
				
			}
		}
		
		if (requestCode == CONTACT_INFO_REQUEST) {
			if (resultCode == RESULT_OK) {

				String action = data.getStringExtra("ACTION");
				if (action.equals("MODIFIED_CONTACT")){
					
					contacts.set(data.getExtras().getInt("POSITION"), 
							(Contact) data.getExtras().getParcelable("MOD_CONTACT"));
					
					sortList(contacts, sortBy);
					adapter.notifyDataSetChanged();
					
				} else if (action.equals("DELETE_CONTACT")){
					contacts.remove(data.getExtras().getInt("POSITION"));
					sortList(contacts, sortBy);
					adapter.notifyDataSetChanged();
					
				}
			}
		}
	}
	
	// Method to other activities
	private void gotoCreateNewContact() {
		Intent i = new Intent(getApplicationContext(), EditActivity.class);
		startActivityForResult(i, CREATE_CONTACT_REQUEST);
	}
	
	private void gotoContactInfo(int position) {
		Intent i = new Intent(getApplicationContext(), InfoActivity.class);
		i.putExtra("SELECTED_CONTACT", contacts.get(position));
		i.putExtra("POSITION", position);
		startActivityForResult(i, CONTACT_INFO_REQUEST);
	}

	private void sortList(List<Contact> list, int SORT_TYPE) {
		switch(SORT_TYPE) {
		case SORT_BY_FIRST_NAME:
			Collections.sort(list, new Contact.Comparators.CompareByFirstName());
			break;
		case SORT_BY_LAST_NAME:
			Collections.sort(list, new Contact.Comparators.CompareByLastName());
			break;
		case SORT_BY_PHONE:
			Collections.sort(list, new Contact.Comparators.CompareByPhone());
			break;
		default:
			Collections.sort(list, new Contact.Comparators.CompareByFirstName());
			break;
		}
	}
	
	private void forceCreateOverflow() {
		// Trick device that have menu button to also have overflow button
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// List View Listener and Adapter
	private class ContactListAdapter extends ArrayAdapter<Contact> {

		private Context context;
		private List<Contact> contacts;

		public ContactListAdapter(Context context, List<Contact> contacts) {
			super(context, android.R.layout.simple_expandable_list_item_1,
					contacts);

			this.context = context;
			this.contacts = contacts;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// Create a layout inflater to inflate our xml layout for each item
			// in the list
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Inflate the list item layout. Keep a reference to the inflated
			// view
			ViewGroup vg = (ViewGroup) inflater.inflate(
					R.layout.contact_list_item, null);

			Contact curContact = contacts.get(position);
			String fullName = Name.ParseName("", curContact.getName()
					.getFirstName(), curContact.getName().getMiddleName(),
					curContact.getName().getLastName(), curContact.getName()
							.getSuffix())[0];

			ImageView iv = (ImageView) vg.getChildAt(0);
			TextView nameTag = (TextView) vg.getChildAt(1);

//			iv.setImageBitmap(Bitmap.createScaledBitmap(curContact.getImage(),
//					Utilities.dpToPx(MainActivity.this, 100), 
//					Utilities.dpToPx(MainActivity.this, 100), false));
			
			iv.setImageBitmap(curContact.getImage());
			
			if (fullName.equals("")){
				nameTag.setText("(Unknown)");
			}else {
				nameTag.setText(fullName);
			}

			return (View) vg;

		}
	}

	private class ListItemClickedListener implements
			AdapterView.OnItemLongClickListener,
			AdapterView.OnItemClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			contacts.remove(position);
			adapter.notifyDataSetChanged();
			Toast.makeText(getApplicationContext(), "Contact Removed!",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			gotoContactInfo(position);
		}
	}

	
	//SortingDialog, OnCompleteListener
	@Override
	public void onComplete(int sortType) {
		this.sortBy = sortType;
		sortList(contacts, sortType);
		adapter.notifyDataSetChanged();
	}
	
	//DUMMY OBJECT
	private void createDummyObject() throws Exception{
		
		Name c11 = new Name("Bert", "Dendeer", "Huang", "Software Engineer");
		Photo c12 = new Photo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face));
		List<Phone> c13 = new ArrayList<Phone>();	
		c13.add(new Phone("Mobile", "0210000000", true));
		List<Email> c14 = new ArrayList<Email>();	
		c14.add(new Email("Home", "dendeer82@gmail.com"));
		List<Address> c15 = new ArrayList<Address>();	
		c15.add(new Address("Home", "51 Evelyn Road, Cockle Bay, Auckland"));
		DateOfBirth c16 = new DateOfBirth("27-01-1993");
		Contact c10 = new Contact(c11, c12, c13, c14, c15, c16);
		
		Name c21 = new Name("Lucy", "Dendeer", "Huang", "Doctor");
		Photo c22 = new Photo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face));
		List<Phone> c23 = new ArrayList<Phone>();	
		c23.add(new Phone("Mobile", "0220000000", true));
		List<Email> c24 = new ArrayList<Email>();	
		c24.add(new Email("Home", "lucy_huang@gmail.com"));
		List<Address> c25 = new ArrayList<Address>();	
		c25.add(new Address("Home", "51 Evelyn Road, Cockle Bay, Auckland"));
		DateOfBirth c26 = new DateOfBirth("01-10-1991");
		Contact c20 = new Contact(c21, c22, c23, c24, c25, c26);
		
		Name c31 = new Name("Simon", "Dendeer", "Huang", "Optom");
		Photo c32 = new Photo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face));
		List<Phone> c33 = new ArrayList<Phone>();	
		c33.add(new Phone("Mobile", "0230000000", true));
		List<Email> c34 = new ArrayList<Email>();	
		c34.add(new Email("Home", "narwhals_republic@gmail.com"));
		List<Address> c35 = new ArrayList<Address>();	
		c35.add(new Address("Home", "51 Evelyn Road, Cockle Bay, Auckland"));
		DateOfBirth c36 = new DateOfBirth("05-07-1990");
		Contact c30 = new Contact(c31, c32, c33, c34, c35, c36);
		
		Name c41 = new Name("Akshay", "", "Kalayn", "Software Engineer");
		Photo c42 = new Photo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face));
		List<Phone> c43 = new ArrayList<Phone>();	
		c43.add(new Phone("Mobile", "0240000000", true));
		List<Email> c44 = new ArrayList<Email>();	
		c44.add(new Email("Work", "akal881@aucklanduni.ac.nz"));
		List<Address> c45 = new ArrayList<Address>();	
		c45.add(new Address("Home", "20 Genius Cave, Not from Earth"));
		DateOfBirth c46 = new DateOfBirth("25-12-1993");
		Contact c40 = new Contact(c41, c42, c43, c44, c45, c46);
		
		Name c51 = new Name("Devon", "", "Ahmu", "Civil Engineer");
		Photo c52 = new Photo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face));
		List<Phone> c53 = new ArrayList<Phone>();	
		c53.add(new Phone("Mobile", "0250000000", true));
		List<Email> c54 = new ArrayList<Email>();	
		c54.add(new Email("Work", "dahm410@aucklanduni.ac.nz"));
		List<Address> c55 = new ArrayList<Address>();	
		c55.add(new Address("Home", "20 Man Cave, NZ"));
		DateOfBirth c56 = new DateOfBirth("18-06-1992");
		Contact c50 = new Contact(c51, c52, c53, c54, c55, c56);
		
		Name c61 = new Name("Matthew", "", "Wang", "Civil Engineer");
		Photo c62 = new Photo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face));
		List<Phone> c63 = new ArrayList<Phone>();	
		c63.add(new Phone("Mobile", "0260000000", true));
		List<Email> c64 = new ArrayList<Email>();	
		c64.add(new Email("Work", "bwan210@aucklanduni.ac.nz"));
		List<Address> c65 = new ArrayList<Address>();	
		c65.add(new Address("Home", "20 IDK, NZ"));
		DateOfBirth c66 = new DateOfBirth("02-03-1992");
		Contact c60 = new Contact(c61, c62, c63, c64, c65, c66);
		
		Name c71 = new Name("Richard", "", "Dyer", "Civil Engineer");
		Photo c72 = new Photo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face));
		List<Phone> c73 = new ArrayList<Phone>();	
		c73.add(new Phone("Mobile", "0270000000", true));
		List<Email> c74 = new ArrayList<Email>();	
		c74.add(new Email("Work", "rdye057@aucklanduni.ac.nz"));
		List<Address> c75 = new ArrayList<Address>();	
		c75.add(new Address("Home", "10 IDK, NZ"));
		DateOfBirth c77 = new DateOfBirth("14-11-1994");
		Contact c70 = new Contact(c71, c72, c73, c74, c75, c77);
		
		Name c81 = new Name("Toby", "", "Jackson", "Civil Engineer");
		Photo c82 = new Photo(BitmapFactory.decodeResource(getResources(), R.drawable.ic_face));
		List<Phone> c83 = new ArrayList<Phone>();	
		c83.add(new Phone("Mobile", "0280000000", true));
		List<Email> c84 = new ArrayList<Email>();	
		c84.add(new Email("Work", "tjac799@aucklanduni.ac.nz"));
		List<Address> c85 = new ArrayList<Address>();	
		c85.add(new Address("Home", "30 Genius Cave, Not from Earth"));
		DateOfBirth c88 = new DateOfBirth("09-01-1992");
		Contact c80 = new Contact(c81, c82, c83, c84, c85, c88);
		
		contacts.add(c10);
		contacts.add(c20);
		contacts.add(c30);
		contacts.add(c40);
		contacts.add(c50);
		contacts.add(c60);
		contacts.add(c70);
		contacts.add(c80);
		adapter.notifyDataSetChanged();
		
	}
}
