package cepw.contactmanager;

import java.util.Collections;
import java.util.List;

import cepw.contact.Address;
import cepw.contact.Contact;
import cepw.contact.Email;
import cepw.contact.Phone;
import cepw.contactmanager.AddressPopupDialog.AddressAction;
import cepw.contactmanager.EmailPopupDialog.EmailAction;
import cepw.contactmanager.PhonePopupDialog.PhoneAction;
import cepw.contactmanager.database.DatabaseHandler;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
 * 
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class InfoActivity extends Activity implements
		PhonePopupDialog.OnCompleteListener,
		EmailPopupDialog.OnCompleteListener,
		AddressPopupDialog.OnCompleteListener {

	// Action identifier
	protected static final String NONE = "NONE";
	protected static final String DELETE_CONTACT = "DELETE_CONTACT";
	protected static final String EDIT_CONTACT = "EDIT_CONTACT";
	protected static final String MODIFIED_CONTACT = "MODIFIED_CONTACT";
	private static String ACTION;

	// Bundle objects
	protected static final String PH_NUMBER = "phoneNumber";
	protected static final String PH_TYPE = "phoneType";
	protected static final String EMAIL_ADDRESS = "emailAddress";
	protected static final String PHY_ADDRESS = "physicalAddress";
	protected static final String IS_DEFAULT = "isDefault";
	protected static final String SELECTED_POS = "selectedPosition";
	private Contact contact = null;
	private int position;
	
	// Database
	private DatabaseHandler db;

	// Individual components
	private ScrollView scrollView;
	private ImageView image;
	private TextView firstName, middleName, lastName, suffix, dobDate;
	private LinearLayout phoneInfoLayout, emailInfoLayout, addressInfoLayout, dobInfoLayout;
	private ListView phoneListView, emailListView, addressListView;
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

		db = new DatabaseHandler(getApplicationContext());
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

		// Initialize action done to the contact to NONE
		ACTION = NONE;

		// Getting Data
		Bundle extras = getIntent().getExtras();

		// If nothing is passed in, display error, and close activity
		if (extras == null) {
			Toast.makeText(InfoActivity.this, "Error loading this contact",
					Toast.LENGTH_LONG).show();
			finish();
		} else {
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

		phoneInfoLayout = (LinearLayout) findViewById(R.id.layout_phone_info);
		emailInfoLayout = (LinearLayout) findViewById(R.id.layout_email_info);
		addressInfoLayout = (LinearLayout) findViewById(R.id.layout_address_info);
		dobInfoLayout = (LinearLayout) findViewById(R.id.layout_dob_info);

		phoneListView = (ListView) findViewById(R.id.listview_phone_info);
		emailListView = (ListView) findViewById(R.id.listview_email_info);
		addressListView = (ListView) findViewById(R.id.listview_address_info);

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

			// Edit button will cause the ACTION flag to turn into
			// MODIFIED_CONTACT
			// and will pass the contact object over to the EditActivity class
			// so modification
			// can be applied.
		case R.id.action_contact_edit:
			ACTION = EDIT_CONTACT;
			Intent intent = new Intent(this, MainActivity.class);
			intent.putExtra("MODIFIED_CONTACT", contact);
			intent.putExtra("ACTION", ACTION);
			intent.putExtra("POSITION", position);
			setResult(RESULT_OK, intent);
			finish();
			/*
			 * Intent intent = new Intent(this, EditActivity.class);
			 * intent.putExtra("SELECTED_CONTACT", contact);
			 * startActivityForResult(intent, EDIT_CONTACT_REQUEST);
			 */

		}
		return super.onOptionsItemSelected(item);
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
					public void onClick(DialogInterface dialog,
							int which) {
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
	 * Invoked when the back button on the phone is pressed. Checks for the
	 * actions applied to the contact object and close this activity. If contact
	 * has been modified, ACTION will be MODIFIED_CONTACT, If contact is left
	 * untouched, ACTION will be NONE.
	 * 
	 * Untouched object will have a RESULT_CANCELED flag.
	 */
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra("ACTION", ACTION);

		// If contact has been modified, ACTION will be MODIFIED_CONTACT
		if (ACTION.equals(EDIT_CONTACT)) {
			// intent.putExtra(EDIT_CONTACT, contact);
			intent.putExtra("POSITION", position);
			setResult(RESULT_OK, intent);
		} else if (ACTION.equals(MODIFIED_CONTACT)) {
			intent.putExtra(MODIFIED_CONTACT, contact);
			intent.putExtra("POSITION", position);
			setResult(RESULT_OK, intent);

			// If contact is left untouched, ACTION will be NONE
		} else if (ACTION.equals(NONE)) {
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
		List<Phone> phoneList = contact.getPhones();
		if (phoneList.isEmpty()) {
			phoneInfoLayout.setVisibility(View.GONE);
		} else {
			phoneInfoLayout.setVisibility(View.VISIBLE);
			Collections.sort(phoneList, new Phone.PhoneComparator());
			phoneAdapter = new PhoneListAdapter(InfoActivity.this, phoneList);
			phoneListView.setAdapter(phoneAdapter);
			phoneListView.setOnItemLongClickListener(new ListItemClickedListener());
			phoneListView.setOnItemClickListener(new ListItemClickedListener());
			Utilities.setNoCollapseListView(phoneListView);
		}

		// Set email
		List<Email> emailList = contact.getEmails();
		if (emailList.isEmpty()) {
			emailInfoLayout.setVisibility(View.GONE);
		} else {
			emailInfoLayout.setVisibility(View.VISIBLE);
			emailAdapter = new EmailListAdapter(InfoActivity.this, emailList);
			emailListView.setAdapter(emailAdapter);
			emailListView.setOnItemLongClickListener(new ListItemClickedListener());
			emailListView.setOnItemClickListener(new ListItemClickedListener());
			Utilities.setNoCollapseListView(emailListView);
		}

		// Set address
		List<Address> addressList = contact.getAddresses();
		if (addressList.isEmpty()) {
			addressInfoLayout.setVisibility(View.GONE);
		} else {
			addressInfoLayout.setVisibility(View.VISIBLE);
			addressAdapter = new AddressListAdapter(InfoActivity.this,
					addressList);
			addressListView.setAdapter(addressAdapter);
			addressListView.setOnItemLongClickListener(new ListItemClickedListener());
			addressListView.setOnItemClickListener(new ListItemClickedListener());
			Utilities.setNoCollapseListView(addressListView);
		}

		// Set Date of Birth
		if (contact.getDateOfBirth().getValue().equals("")) {
			dobInfoLayout.setVisibility(View.GONE);
		} else {
			dobInfoLayout.setVisibility(View.VISIBLE);
			dobDate.setText(contact.getDateOfBirth().getValue());
		}

		// Scroll to top
		scrollView.smoothScrollTo(0, 0);
	}

	/**
	 * Method that is invoked when an PhonePopupDialog option is chosen
	 * and will perform the selection accordingly.
	 */
	@Override
	public void onComplete(PhoneAction action, int position) {
		String number = contact.getPhones().get(position).getNumber();
		switch (action) {
		
		case SELECTED_CALL:
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:" + number));
			startActivity(callIntent);
			break;
		case SELECTED_MESSAGE:
			Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", number, null));
			startActivity(smsIntent);
			break;
		case SELECTED_COPY:
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			Utilities.copyStringToClipboard(clipboard, number);
			Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
			break;
		case SELECTED_DELETE:
			final int pos = position;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete " 
			+ contact.getPhones().get(pos).getNumber() + "?")
					.setTitle("Delete?")
					.setNegativeButton("Cancel", null)
					.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// If removed the primary number
							if(pos == 0) {
								contact.getPhones().remove(pos); // Remove the phone
								if(!contact.getPhones().isEmpty()){ // If the phone list is not empty
									contact.getPhones().get(0).setPrimary(); // Set the next one as primary
								}
							}else { // If not the primary number
								contact.getPhones().remove(pos); // simply remove
							}
							phoneAdapter.notifyDataSetChanged(); 
							Utilities.setNoCollapseListView(phoneListView); // Resize list view
							new UpdateContactDbTask().execute(contact); // Update database
							
							// If the contact have no more phone, hide the phoneInfoLayout 
							if(contact.getPhones().isEmpty()){ phoneInfoLayout.setVisibility(View.GONE); } 
							ACTION = MODIFIED_CONTACT;
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			break;
		case SELECTED_SET_PRIMARY:
			for (Phone p : contact.getPhones())
				p.unsetPrimary();
			contact.getPhones().get(position).setPrimary();
			Collections.sort(contact.getPhones(), new Phone.PhoneComparator());
			phoneAdapter.notifyDataSetChanged();
			
			ACTION = MODIFIED_CONTACT;
			new UpdateContactDbTask().execute(contact);
			
			Toast.makeText(getApplicationContext(), number+" set as primary.", Toast.LENGTH_SHORT).show();
			break;
		}

	}

	/**
	 * Method that is invoked when an EmailPopupDialog option is chosen
	 * and will perform the selection accordingly.
	 */
	@Override
	public void onComplete(EmailAction action, int position) {
		String email = contact.getEmails().get(position).getEmail();
		switch (action) {
		case SELECTED_MAIL:
			invokeEmailIntent(email);
			break;
		case SELECTED_COPY:
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			Utilities.copyStringToClipboard(clipboard, email);
			Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
			break;
		case SELECTED_DELETE:
			final int pos = position;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete " 
			+ contact.getEmails().get(pos).getEmail() + "?")
					.setTitle("Delete?")
					.setNegativeButton("Cancel", null)
					.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							contact.getEmails().remove(pos); // simply remove
							new UpdateContactDbTask().execute(contact);  // Update database
							phoneAdapter.notifyDataSetChanged(); 
							Utilities.setNoCollapseListView(emailListView); // Resize list view
							// If the contact have no more phone, hide the phoneInfoLayout 
							if(contact.getEmails().isEmpty()){ emailInfoLayout.setVisibility(View.GONE); } 
							ACTION = MODIFIED_CONTACT;
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			break;
		}

	}

	/**
	 * Method that is invoked when an AddressPopupDialog option is chosen
	 * and will perform the selection accordingly.
	 */
	@Override
	public void onComplete(AddressAction action, int position) {
		String address = contact.getAddresses().get(position).getAddress();
		switch (action) {
		case SELECTED_MAP:
			invokeMapIntent(address);
			break;
		case SELECTED_COPY:
			ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
			Utilities.copyStringToClipboard(clipboard, address);
			Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
			break;
		case SELECTED_DELETE:
			final int pos = position;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Are you sure you want to delete " 
			+ contact.getAddresses().get(pos).getAddress() + "?")
					.setTitle("Delete?")
					.setNegativeButton("Cancel", null)
					.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {

							contact.getAddresses().remove(pos); // simply remove
							new UpdateContactDbTask().execute(contact); // Update database
							phoneAdapter.notifyDataSetChanged(); 
							Utilities.setNoCollapseListView(addressListView); // Resize list view
							// If the contact have no more phone, hide the phoneInfoLayout 
							if(contact.getAddresses().isEmpty()){ addressInfoLayout.setVisibility(View.GONE); } 
							ACTION = MODIFIED_CONTACT;
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
			break;
		}

	}
	
	/**
	 * Opens up the Email Intent
	 * @param emailAddress the receiver for the email
	 */
	private void invokeEmailIntent(String emailAddress){
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("message/rfc822");
		emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { emailAddress });
		try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(),
					"There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * Opens up the Map Intent
	 * @param physicalAddress The physical address to search for
	 */
	private void invokeMapIntent(String physicalAddress){
		String uri = "geo:0,0?q="+physicalAddress;
		try {
			startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(getApplicationContext(),
					"There are no map clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	// CLASSES
	/**
	 * Adapter for the list view for phone numbers
	 */
	private class PhoneListAdapter extends ArrayAdapter<Phone> {

		private Context context;
		private List<Phone> phoneList;

		public PhoneListAdapter(Context context, List<Phone> phoneList) {
			super(context, android.R.layout.simple_expandable_list_item_1,
					phoneList);

			this.context = context;
			this.phoneList = phoneList;
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

			Phone curPhone = phoneList.get(position);

			TextView type = (TextView) vg.getChildAt(0);
			TextView number = (TextView) vg.getChildAt(1);

			if (curPhone.isPrimary()) {
				type.setText(curPhone.getType() + " - Primary");
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
		private List<Email> emailList;

		public EmailListAdapter(Context context, List<Email> emailList) {
			super(context, android.R.layout.simple_expandable_list_item_1,
					emailList);
			this.context = context;
			this.emailList = emailList;
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

			Email curEmail = emailList.get(position);

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
		private List<Address> addressList;

		public AddressListAdapter(Context context, List<Address> addressList) {
			super(context, android.R.layout.simple_expandable_list_item_1,
					addressList);

			this.context = context;
			this.addressList = addressList;
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

			Address curAddr = addressList.get(position);

			TextView type = (TextView) vg.getChildAt(0);
			TextView address = (TextView) vg.getChildAt(1);

			type.setText(curAddr.getType());
			address.setText(curAddr.getAddress());

			return (View) vg;

		}
	}

	/**
	 * Actions for the list view when items within the list is clicked / long
	 * clicked
	 */
	private class ListItemClickedListener implements
			AdapterView.OnItemLongClickListener,
			AdapterView.OnItemClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			Bundle args;

			switch (parent.getId()) {
			case R.id.listview_phone_info:
				Phone p = contact.getPhones().get(position);
				DialogFragment phoneDialog = new PhonePopupDialog();
				args = new Bundle();
				args.putString(PH_NUMBER, p.getNumber());
				args.putString(PH_TYPE, p.getType());
				args.putBoolean(IS_DEFAULT, p.isPrimary());
				args.putInt(SELECTED_POS, position);
				phoneDialog.setArguments(args);
				phoneDialog.show(getFragmentManager(), "Phone Options");
				break;
			case R.id.listview_email_info:
				Email e = contact.getEmails().get(position);
				DialogFragment emailDialog = new EmailPopupDialog();
				args = new Bundle();
				args.putString(EMAIL_ADDRESS, e.getEmail());
				args.putInt(SELECTED_POS, position);
				emailDialog.setArguments(args);
				emailDialog.show(getFragmentManager(), "Email Options");
				break;
			case R.id.listview_address_info:
				Address a = contact.getAddresses().get(position);
				DialogFragment addressDialog = new AddressPopupDialog();
				args = new Bundle();
				args.putString(PHY_ADDRESS, a.getAddress());
				args.putInt(SELECTED_POS, position);
				addressDialog.setArguments(args);
				addressDialog.show(getFragmentManager(), "Address Options");

				break;
			}

			return true;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, 
				int position,long id) {
			
			switch (parent.getId()) {
			
			case R.id.listview_phone_info:
				String number = contact.getPhones().get(position).getNumber();
				Intent dialIntent = new Intent(Intent.ACTION_DIAL);
				dialIntent.setData(Uri.parse("tel:" + number));
				startActivity(dialIntent);
				break;
			case R.id.listview_email_info:
				String email = contact.getEmails().get(position).getEmail();
				invokeEmailIntent(email);
				break;
			case R.id.listview_address_info:
				String address = contact.getAddresses().get(position).getAddress();
				invokeMapIntent(address);
				break;
			}			
		}
	}

	/**
	 * Async task for updating database to prevent stall. Provides smoother user experience.
	 *
	 */
	private class UpdateContactDbTask extends AsyncTask<Contact, Void, Void>{

		@Override
		protected Void doInBackground(Contact... params) {
			for(Contact c : params)
			db.updateContact(c);
			return null;
		}
	}

}
