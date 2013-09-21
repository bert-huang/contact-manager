package cepw.contactmanager;

import java.util.Collections;
import java.util.List;

import cepw.contact.Address;
import cepw.contact.Contact;
import cepw.contact.Email;
import cepw.contact.Phone;
import cepw.contact.Utilities;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

public class InfoActivity extends Activity {

	static final String NONE = "NONE";
	static final String DELETE_CONTACT = "DELETE_CONTACT";
	static final String EDIT_CONTACT = "DELETE_CONTACT";
	static final String MODIFIED_CONTACT = "DELETE_CONTACT";

	private Contact contact = null;
	private int position;
	private String ACTION;

	private ScrollView scrollView;
	private ImageView image;
	private TextView firstName, middleName, lastName, suffix, dobDate;
	private LinearLayout phoneInfo, emailInfo, addressInfo, dobInfo;
	private ListView phoneList, emailList, addressList;
	private ArrayAdapter<Phone> phoneAdapter;
	private ArrayAdapter<Email> emailAdapter;
	private ArrayAdapter<Address> addressAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		setupActionBar();
		ACTION = "NONE";

		// Getting Data
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras == null)
				finish();
			else
				contact = extras.getParcelable("SELECTED_CONTACT");
			position = extras.getInt("POSITION");
		} else {
			contact = (Contact) savedInstanceState
					.getSerializable("SELECTED_CONTACT");
			position = (Integer) savedInstanceState.getSerializable("POSITION");
		}

		// Initialise
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

		// Set image
		image.setImageBitmap(contact.getImage());

		// Set name
		if (!contact.getName().getFirstName().equals("")) {
			firstName.setText(contact.getName().getFirstName());
		} else {
			firstName.setText("(Unknown)");
		}

		if (!contact.getName().getMiddleName().equals("")) {
			middleName.setText(contact.getName().getMiddleName());
		} else {
			middleName.setVisibility(View.GONE);
		}

		if (!contact.getName().getLastName().equals("")) {
			lastName.setText(contact.getName().getLastName());
		} else {
			lastName.setVisibility(View.GONE);
		}

		if (!contact.getName().getSuffix().equals("")) {
			suffix.setText("[ " + contact.getName().getSuffix() + " ]");
		} else {
			suffix.setVisibility(View.GONE);
		}

		// Set phone
		List<Phone> phones = contact.getPhones();
		if (phones.isEmpty()) {
			phoneInfo.setVisibility(View.GONE);
		} else {
			Collections.sort(phones);
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
			addressAdapter = new AddressListAdapter(InfoActivity.this,
					addresses);
			addressList.setAdapter(addressAdapter);
			addressList
					.setOnItemLongClickListener(new ListItemClickedListener());
			Utilities.setNoCollapseListView(addressList);
		}

		// Set Date of Birth

		if (contact.getDateOfBirth().equals("")) {
			dobInfo.setVisibility(View.GONE);
		} else {
			dobDate.setText(contact.getDateOfBirth());
		}

		scrollView.smoothScrollTo(0, 0);
	}

	private void setupActionBar() {
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;

		case R.id.action_contact_remove:
			Intent intent = new Intent();
			ACTION = DELETE_CONTACT;
			intent.putExtra("ACTION", ACTION);
			intent.putExtra("POSITION", position);
			setResult(RESULT_OK, intent);
			Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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

			type.setText(curPhone.getType());
			number.setText(curPhone.getNumber());

			return (View) vg;

		}
	}

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

	private class ListItemClickedListener implements
			AdapterView.OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			switch (parent.getId()) {
			case R.id.listview_phone_info:
				Toast.makeText(getApplicationContext(), "Phone list popup!",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.listview_email_info:
				Toast.makeText(getApplicationContext(), "Email list popup!",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.listview_address_info:
				Toast.makeText(getApplicationContext(), "Address list popup!",
						Toast.LENGTH_SHORT).show();
				break;
			}

			return true;
		}
	}


}
