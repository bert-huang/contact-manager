package cepw.contactmanager;

import java.util.Collections;
import java.util.List;

import cepw.contact.Address;
import cepw.contact.Contact;
import cepw.contact.Email;
import cepw.contact.Phone;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is an activity that display the information of a contact.
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class InfoActivity extends Activity {
	
	// Action identifier 
	private static final String NONE = "NONE";
	private static final String DELETE_CONTACT = "DELETE_CONTACT";
	private static final String EDIT_CONTACT = "EDIT_CONTACT";
	private static String ACTION;
	
	// Request code
	private static final int EDIT_CONTACT_REQUEST = 1;

	// Bundle objects
	private Contact contact = null;
	private int position;

	// Individual components
	private ScrollView scrollView;
	private ImageView image;
	private TextView firstName, middleName, lastName, suffix, dobDate;
	private LinearLayout phoneInfo, emailInfo, addressInfo, dobInfo;
	private ListView phoneList, emailList, addressList;
	private ArrayAdapter<Phone> phoneAdapter;
	private ArrayAdapter<Email> emailAdapter;
	private ArrayAdapter<Address> addressAdapter;

	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		// Initialize action done to the contact to NONE
		ACTION = NONE;

		// Getting Data
		Bundle extras = getIntent().getExtras();
		
		// If nothing is passed in, display error, and close activity
		if (extras == null){
			Toast.makeText(InfoActivity.this, 
					"Error loading this contact!", Toast.LENGTH_LONG).show();
			finish();
		}else{
			// Get the contact and the position of the contact
			contact = extras.getParcelable("SELECTED_CONTACT");
			position = extras.getInt("POSITION");
		}

		// Initializing
		scrollView = (ScrollView) findViewById(R.id.scrollview_info);

		image = (ImageView) findViewById(R.id.info_display_image);
		firstName = (TextView) findViewById(R.id.info_first_name);
		middleName = (TextView) findViewById(R.id.info_middle_name);
		lastName = (TextView) findViewById(R.id.info_last_name);
		suffix = (TextView) findViewById(R.id.info_name_suffix);
		dobDate = (TextView) findViewById(R.id.info_dob);

		phoneInfo = (LinearLayout) findViewById(R.id.layout_phone_info);
		emailInfo = (LinearLayout) findViewById(R.id.layout_email_info);
		addressInfo = (LinearLayout) findViewById(R.id.layout_address_info);
		dobInfo = (LinearLayout) findViewById(R.id.layout_dob_info);

		phoneList = (ListView) findViewById(R.id.listview_phone_info);
		emailList = (ListView) findViewById(R.id.listview_email_info);
		addressList = (ListView) findViewById(R.id.listview_address_info);

		// Invoke the populateData method to populate the data into components
		populateData();
	}

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}

	/**
	 * @see android.app.Activity#onOptionsItemSelected(MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// Home button pressed will be the same as pressing back button
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;

		// Delete button will display the delete dialog
		case R.id.action_contact_remove:
			showDeleteDialog();
			return true;
			
		// Edit button will cause the ACTION flag to turn into MODIFIED_CONTACT
		// and will pass the contact object over to the EditActivity class so modification
		// can be applied.
		case R.id.action_contact_edit:
			ACTION = EDIT_CONTACT;
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("ACTION", ACTION);
			intent.putExtra("POSITION", position);
			setResult(RESULT_OK, intent);
			finish();
			/*Intent intent = new Intent(this, EditActivity.class);
			intent.putExtra("SELECTED_CONTACT", contact);
			startActivityForResult(intent, EDIT_CONTACT_REQUEST);*/
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	/**
	 * @see android.app.Activity#onActivityResult(int, int, Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		// If the request code is EDIT_CONTACT_REQUEST, fetch contact object from data intent
		// then re populate the updated values
		if (requestCode == EDIT_CONTACT_REQUEST && resultCode == RESULT_OK) {
			if (data != null) {
				contact = (Contact) data.getExtras().getParcelable("EDITED_CONTACT");
				populateData();
			}
		}
	}
	
	/**
	 * Shows a dialog that prompt for deletion when the delete button is clicked
	 */
	public void showDeleteDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to delete this contact?")
				.setTitle("Delete?")
				.setNegativeButton("Cancel", null)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent();
								ACTION = DELETE_CONTACT;
								intent.putExtra("ACTION", ACTION);
								intent.putExtra("POSITION", position);
								setResult(RESULT_OK, intent);
								finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	/**
	 * Invoked when the back button on the phone is pressed.
	 * Checks for the actions applied to the contact object and close this activity.
	 * If contact has been modified, ACTION will be MODIFIED_CONTACT,
	 * If contact is left untouched, ACTION will be NONE.
	 * 
	 * Untouched object will have a RESULT_CANCELED flag.
	 */
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("ACTION", ACTION);
		
		// If contact has been modified, ACTION will be MODIFIED_CONTACT
		if (ACTION.equals(EDIT_CONTACT)){
			intent.putExtra(EDIT_CONTACT, contact);
			intent.putExtra("POSITION", position);
			setResult(RESULT_OK, intent);
			
		// If contact is left untouched, ACTION will be NONE
		}else if (ACTION.equals(NONE)){
			setResult(RESULT_CANCELED, intent);
		}
		finish();
	}

	/**
	 * Populate the data from the contact object into individual components
	 */
	private void populateData() {
		// Set image
		image.setImageBitmap(contact.getPhoto().getImage());
	
		// Set name
		if (!contact.getName().getFirstName().equals("")) {
			firstName.setText(contact.getName().getFirstName());
		} else {
			firstName.setText("(Unknown)");
		}

		if (!contact.getName().getMiddleName().equals("")) {
			middleName.setVisibility(View.VISIBLE);
			middleName.setText(contact.getName().getMiddleName());
		} else {
			middleName.setVisibility(View.GONE);
		}

		if (!contact.getName().getLastName().equals("")) {
			lastName.setVisibility(View.VISIBLE);
			lastName.setText(contact.getName().getLastName());
		} else {
			lastName.setVisibility(View.GONE);
		}

		if (!contact.getName().getSuffix().equals("")) {
			suffix.setVisibility(View.VISIBLE);
			suffix.setText("[ " + contact.getName().getSuffix() + " ]");
		} else {
			suffix.setVisibility(View.GONE);
		}

		// Set phone
		List<Phone> phones = contact.getPhones();
		if (phones.isEmpty()) {
			phoneInfo.setVisibility(View.GONE);
		} else {
			phoneInfo.setVisibility(View.VISIBLE);
			Collections.sort(phones, new Phone.PhoneComparator());
			phoneAdapter = new PhoneListAdapter(InfoActivity.this, phones);
			phoneList.setAdapter(phoneAdapter);
			phoneList.setOnItemLongClickListener(new ListItemClickedListener());
			Utilities.setNoCollapseListView(phoneList);
		}

		// Set email
		List<Email> emails = contact.getEmails();
		if (emails.isEmpty()) {
			emailInfo.setVisibility(View.GONE);
		} else {
			emailInfo.setVisibility(View.VISIBLE);
			emailAdapter = new EmailListAdapter(InfoActivity.this, emails);
			emailList.setAdapter(emailAdapter);
			emailList.setOnItemLongClickListener(new ListItemClickedListener());
			Utilities.setNoCollapseListView(emailList);
		}

		// Set address
		List<Address> addresses = contact.getAddresses();
		if (addresses.isEmpty()) {
			addressInfo.setVisibility(View.GONE);
		} else {
			addressInfo.setVisibility(View.VISIBLE);
			addressAdapter = new AddressListAdapter(InfoActivity.this,
					addresses);
			addressList.setAdapter(addressAdapter);
			addressList
					.setOnItemLongClickListener(new ListItemClickedListener());
			Utilities.setNoCollapseListView(addressList);
		}

		// Set Date of Birth
		if (contact.getDateOfBirth().getValue().equals("")) {
			dobInfo.setVisibility(View.GONE);
		} else {
			dobInfo.setVisibility(View.VISIBLE);
			dobDate.setText(contact.getDateOfBirth().getValue());
		}

		// Scroll to top
		scrollView.smoothScrollTo(0, 0);
	}
	
	// CLASSES
	/**
	 * Adapter for the list view for phone numbers
	 */
	private class PhoneListAdapter extends ArrayAdapter<Phone> {

		private Context context;
		private List<Phone> phones;

		public PhoneListAdapter(Context context, List<Phone> phones) {
			super(context, android.R.layout.simple_expandable_list_item_1,
					phones);

			this.context = context;
			this.phones = phones;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// Create a layout inflater to inflate our xml layout for each item
			// in the list
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Inflate the list item layout. Keep a reference to the inflated
			// view
			ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.info_item,
					null);

			Phone curPhone = phones.get(position);

			TextView type = (TextView) vg.getChildAt(0);
			TextView number = (TextView) vg.getChildAt(1);

			if (curPhone.isDefault()) {
				type.setText(curPhone.getType() + " - Default");
			} else {
				type.setText(curPhone.getType());
			}
			number.setText(curPhone.getNumber());

			return (View) vg;

		}
	}

	/**
	 * Adapter for the list view for email addresses
	 */
	private class EmailListAdapter extends ArrayAdapter<Email> {

		private Context context;
		private List<Email> emails;

		public EmailListAdapter(Context context, List<Email> emails) {
			super(context, android.R.layout.simple_expandable_list_item_1,
					emails);
			this.context = context;
			this.emails = emails;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// Create a layout inflater to inflate our xml layout for each item
			// in the list
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Inflate the list item layout. Keep a reference to the inflated
			// view
			ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.info_item,
					null);

			Email curEmail = emails.get(position);

			TextView type = (TextView) vg.getChildAt(0);
			TextView email = (TextView) vg.getChildAt(1);

			type.setText(curEmail.getType());
			email.setText(curEmail.getEmail());

			return (View) vg;

		}
	}

	/**
	 * Adapter for the list view for physical addresses
	 */
	private class AddressListAdapter extends ArrayAdapter<Address> {

		private Context context;
		private List<Address> addresses;

		public AddressListAdapter(Context context, List<Address> addresses) {
			super(context, android.R.layout.simple_expandable_list_item_1,
					addresses);

			this.context = context;
			this.addresses = addresses;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// Create a layout inflater to inflate our xml layout for each item
			// in the list
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// Inflate the list item layout. Keep a reference to the inflated
			// view
			ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.info_item,
					null);

			Address curAddr = addresses.get(position);

			TextView type = (TextView) vg.getChildAt(0);
			TextView address = (TextView) vg.getChildAt(1);

			type.setText(curAddr.getType());
			address.setText(curAddr.getAddress());

			return (View) vg;

		}
	}

	/**
	 * Actions for the list view when items within the list is clicked / long clicked
	 */
	private class ListItemClickedListener implements AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			switch (parent.getId()) {
			case R.id.listview_phone_info:
				Phone p = contact.getPhones().get(position);
				if (p.isDefault()) {
					Toast.makeText(getApplicationContext(), "Default",
							Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(getApplicationContext(), "Not default",
							Toast.LENGTH_SHORT).show();
				}
				Toast.makeText(getApplicationContext(), "Phone. TODO: Dialog",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.listview_email_info:
				Toast.makeText(getApplicationContext(), "Email. TODO: Dialog",
						Toast.LENGTH_SHORT).show();
				ClipboardManager clipboard = (ClipboardManager)
				        getSystemService(Context.CLIPBOARD_SERVICE);
				Utilities.copyStringToClipboard(clipboard, contact.getEmails().get(position).getEmail());
				break;
			case R.id.listview_address_info:
				Toast.makeText(getApplicationContext(), "Address. TODO: Dialog",
						Toast.LENGTH_SHORT).show();
				break;
			}

			return true;
		}
	}


}
