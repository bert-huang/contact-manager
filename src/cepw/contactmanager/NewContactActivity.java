package cepw.contactmanager;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.support.v4.app.NavUtils;

public class NewContactActivity extends Activity {

	private ImageButton expandName, collapseName;
	private EditText fullName, namePrefix, givenName, middleName, lastName, nameSuffix;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_contact);
		// Show the Up button in the action bar.
		setupActionBar();
		
		expandName = (ImageButton) findViewById(R.id.button_name_expand);
		collapseName = (ImageButton) findViewById(R.id.button_name_collapse);
		fullName = (EditText) findViewById(R.id.textfield_name_full);
		namePrefix = (EditText) findViewById(R.id.textfield_name_prefix);
		givenName = (EditText) findViewById(R.id.textfield_name_given);
		middleName = (EditText) findViewById(R.id.textfield_name_middle);
		lastName = (EditText) findViewById(R.id.textfield_name_surname);
		nameSuffix = (EditText) findViewById(R.id.textfield_name_suffix);
		
		collapseName.setVisibility(View.GONE);
		namePrefix.setVisibility(View.GONE);
		givenName.setVisibility(View.GONE);
		middleName.setVisibility(View.GONE);
		lastName.setVisibility(View.GONE);
		nameSuffix.setVisibility(View.GONE);
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

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
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
			
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
	}


}
