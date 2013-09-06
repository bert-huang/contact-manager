package cepw.contactmanager;

import android.os.Bundle;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;

public class NewContactActivity extends Activity {

	private ImageButton expandName, collapseName;
	private EditText fullName, namePrefix, givenName, middleName, lastName,
			nameSuffix;
	LinearLayout dynamicPhoneLinLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_contact);
		// Show the Up button in the action bar.
		setupActionBar();
		setupNameFields();

		dynamicPhoneLinLayout = (LinearLayout) findViewById(R.id.layout_dynamic_phonefield);
		createNewPhoneField();
		
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
		namePrefix = (EditText) findViewById(R.id.textfield_name_prefix);
		givenName = (EditText) findViewById(R.id.textfield_name_given);
		middleName = (EditText) findViewById(R.id.textfield_name_middle);
		lastName = (EditText) findViewById(R.id.textfield_name_last);
		nameSuffix = (EditText) findViewById(R.id.textfield_name_suffix);

		collapseName.setVisibility(View.GONE);
		namePrefix.setVisibility(View.GONE);
		givenName.setVisibility(View.GONE);
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

		fullName.setVisibility(View.GONE);
		namePrefix.setVisibility(View.VISIBLE);
		nameSuffix.setVisibility(View.VISIBLE);
		givenName.setVisibility(View.VISIBLE);
		middleName.setVisibility(View.VISIBLE);
		lastName.setVisibility(View.VISIBLE);
		
		givenName.setFocusable(true);
		givenName.requestFocus();
	}

	public void collapseNameField(View v) {
		expandName.setVisibility(View.VISIBLE);
		collapseName.setVisibility(View.GONE);

		fullName.setVisibility(View.VISIBLE);
		namePrefix.setVisibility(View.GONE);
		nameSuffix.setVisibility(View.GONE);
		givenName.setVisibility(View.GONE);
		middleName.setVisibility(View.GONE);
		lastName.setVisibility(View.GONE);
		
		fullName.setFocusable(true);
		fullName.requestFocus();
	}

	public void addPhoneField(View v) {
		createNewPhoneField();
	}

	public void removePhoneField(View v) {
		ViewGroup view2rm = (ViewGroup) v.getParent();
		ViewGroup parent = (ViewGroup) view2rm.getParent();
		parent.removeView(view2rm);

	}
	
	public void openDatePickerDialog(View v) {
		DialogFragment newFragment = new NewFieldCategoryDialogFragment();
	    newFragment.show(getFragmentManager(), "newfield");
	}
	
	public void openNewFieldCategoryDialog(View v) {
		DialogFragment newFragment = new NewFieldCategoryDialogFragment();
	    newFragment.show(getFragmentManager(), "newfield");
	}

	private void createNewPhoneField() {

		ViewGroup phoneFieldInfo = (ViewGroup) getLayoutInflater().inflate(
				R.layout.phone_item, dynamicPhoneLinLayout, false);

		Spinner spinner = (Spinner) phoneFieldInfo.getChildAt(0);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.phone_type, R.layout.spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		dynamicPhoneLinLayout.addView(phoneFieldInfo);
	}

}
