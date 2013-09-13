package cepw.contactmanager;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class NewContactActivity extends Activity {

	private enum FieldType {
		PHONE, EMAIL, ADDRESS
	};

	private ImageButton expandName, collapseName;
	private EditText fullName, firstName, middleName, lastName, nameSuffix;

	private LinearLayout phoneLinLayout, emailLinLayout, addressLinLayout,
			dobLinLayout;
	private LinearLayout dynamicPhoneLinLayout, dynamicEmailLinLayout,
			dynamicAddressLinLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_contact);
		// Show the Up button in the action bar.
		setupActionBar();
		setupNameFields();

		phoneLinLayout = (LinearLayout) findViewById(R.id.layout_phonefields);
		dynamicPhoneLinLayout = (LinearLayout) findViewById(R.id.layout_dynamic_phonefield);
		emailLinLayout = (LinearLayout) findViewById(R.id.layout_emailfields);
		dynamicEmailLinLayout = (LinearLayout) findViewById(R.id.layout_dynamic_emailfield);
		addressLinLayout = (LinearLayout) findViewById(R.id.layout_addressfields);
		dynamicAddressLinLayout = (LinearLayout) findViewById(R.id.layout_dynamic_addressfield);
		dobLinLayout = (LinearLayout) findViewById(R.id.layout_dobfields);

		emailLinLayout.setVisibility(View.GONE);
		addressLinLayout.setVisibility(View.GONE);
		dobLinLayout.setVisibility(View.GONE);

		createNewField(FieldType.PHONE);
		createNewField(FieldType.EMAIL);
		createNewField(FieldType.ADDRESS);

		fullName.setFocusable(true);
		fullName.requestFocus();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

	}

	private void setupNameFields() {
		expandName = (ImageButton) findViewById(R.id.button_name_expand);
		collapseName = (ImageButton) findViewById(R.id.button_name_collapse);
		fullName = (EditText) findViewById(R.id.textfield_name_full);
		firstName = (EditText) findViewById(R.id.textfield_name_given);
		middleName = (EditText) findViewById(R.id.textfield_name_middle);
		lastName = (EditText) findViewById(R.id.textfield_name_last);
		nameSuffix = (EditText) findViewById(R.id.textfield_name_suffix);
		
		collapseName.setVisibility(View.GONE);
		firstName.setVisibility(View.GONE);
		middleName.setVisibility(View.GONE);
		lastName.setVisibility(View.GONE);
		nameSuffix.setVisibility(View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_contact, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.action_create_discard:
			NavUtils.navigateUpFromSameTask(this);
			return true;

		case R.id.action_create_done:
			// TODO
		}
		return super.onOptionsItemSelected(item);
	}

	public void expandNameField(View v) {
		expandName.setVisibility(View.GONE);
		collapseName.setVisibility(View.VISIBLE);

		String[] splits = ContactName.ParseName(
				fullName.getText().toString(),
				firstName.getText().toString(),
				middleName.getText().toString(),
				lastName.getText().toString(),
				nameSuffix.getText().toString());
		fullName.setText("");
		firstName.setText(splits[0]);
		middleName.setText(splits[1]);
		lastName.setText(splits[2]);
		nameSuffix.setText(splits[3]);

		fullName.setVisibility(View.GONE);
		nameSuffix.setVisibility(View.VISIBLE);
		firstName.setVisibility(View.VISIBLE);
		middleName.setVisibility(View.VISIBLE);
		lastName.setVisibility(View.VISIBLE);

		firstName.setFocusable(true);
		firstName.requestFocus();
	}

	public void collapseNameField(View v) {
		expandName.setVisibility(View.VISIBLE);
		collapseName.setVisibility(View.GONE);

		String[] combine = ContactName.ParseName(
				fullName.getText().toString(), 
				firstName.getText().toString(), 
				middleName.getText().toString(),
				lastName.getText().toString(),
				nameSuffix.getText().toString());
		fullName.setText(combine[0]);
		firstName.setText("");
		middleName.setText("");
		lastName.setText("");
		nameSuffix.setText("");
		
		fullName.setVisibility(View.VISIBLE);
		nameSuffix.setVisibility(View.GONE);
		firstName.setVisibility(View.GONE);
		middleName.setVisibility(View.GONE);
		lastName.setVisibility(View.GONE);
		
		fullName.setFocusable(true);
		fullName.requestFocus();
	}

	public void addNewField(View v) {
		ViewGroup vg = (ViewGroup) v.getParent().getParent();
		FieldType ft = null;
		switch (vg.getId()) {
		case R.id.layout_phonefields:
			ft = FieldType.PHONE;
			break;
		case R.id.layout_emailfields:
			ft = FieldType.EMAIL;
			break;
		case R.id.layout_addressfields:
			ft = FieldType.ADDRESS;
			break;
		default:
			return;
		}
		createNewField(ft);
	}

	public void removeCurrentField(View v) {
		ViewGroup view2rm = (ViewGroup) v.getParent();
		ViewGroup parent = (ViewGroup) view2rm.getParent();
		parent.removeView(view2rm);

	}

	public void openDatePickerDialog(View v) {
		DialogFragment newFragment = new DatePickerDialogFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	public void openNewFieldCategoryDialog(View v) {
		DialogFragment newFragment = new NewFieldCategoryDialogFragment();
		newFragment.show(getFragmentManager(), "newfield");
	}

	private void createNewField(FieldType ft) {

		int infl = 0;
		int charSeq = 0;
		LinearLayout ll = null;
		switch (ft) {
		case PHONE:
			infl = R.layout.phone_item;
			ll = dynamicPhoneLinLayout;
			charSeq = R.array.phone_type;
			break;
		case EMAIL:
			infl = R.layout.email_item;
			ll = dynamicEmailLinLayout;
			charSeq = R.array.email_type;
			break;
		case ADDRESS:
			infl = R.layout.address_item;
			ll = dynamicAddressLinLayout;
			charSeq = R.array.address_type;
			break;
		default:
			return;
		}

		ViewGroup fieldInfo = (ViewGroup) getLayoutInflater().inflate(infl, ll,
				false);

		Spinner spinner = (Spinner) fieldInfo.getChildAt(0);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, charSeq, R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		ll.addView(fieldInfo);
	}

	public void clearField(View v) {
		TextView tv = (TextView) findViewById(R.id.textview_dob);
		tv.setText("");
	}

}
