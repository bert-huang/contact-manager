package cepw.contactmanager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.TextView;

import cepw.contact.*;

public class MainActivity extends Activity {

	static final int CREATE_CONTACT_REQUEST = 1;  // The request code
	private List<Contact> contacts;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		forceCreateOverflow();
		getActionBar().setDisplayShowTitleEnabled(false);
		
		contacts = new ArrayList<Contact>();
		
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
		
		case R.id.action_exit:
			finish();
			
		default:
			return true;
		}
	}

	
	private void gotoCreateNewContact() {
		Intent i = new Intent(getApplicationContext(),
				EditActivity.class);
		startActivityForResult(i, CREATE_CONTACT_REQUEST);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

	    if (requestCode == CREATE_CONTACT_REQUEST) {
	         if(resultCode == RESULT_OK){
	        	 
//	        	 contacts.add((Contact)data.getExtras().getParcelable("NEW_CONTACT"));
	        	 
	        	 // TESTING!
	        	 contacts.clear();
	        	 contacts.add((Contact)data.getExtras().getParcelable("NEW_CONTACT"));

	        	 LinearLayout testLayout = (LinearLayout)findViewById(R.id.testing_layout);
	        	 testLayout.removeAllViews();
	        	 
	        	 TextView firstName = new TextView(getApplicationContext());
	        	 firstName.setText("First Name: " + contacts.get(0).getName().getFirstName());
	        	 testLayout.addView(firstName);
	        	 
	        	 TextView middleName = new TextView(getApplicationContext());
	        	 middleName.setText("Middle Name: " + contacts.get(0).getName().getMiddleName());
	        	 testLayout.addView(middleName);
	        	 
	        	 TextView lastName = new TextView(getApplicationContext());
	        	 lastName.setText("Last Name: " + contacts.get(0).getName().getLastName());
	        	 testLayout.addView(lastName);
	        	 
	        	 TextView suffix = new TextView(getApplicationContext());
	        	 suffix.setText("Suffix: " + contacts.get(0).getName().getSuffix());
	        	 testLayout.addView(suffix);
	        	 
	        	 TextView box = new TextView(getApplicationContext());
	        	 testLayout.addView(box);
	        	 
	        	 for(int i = 0; i < contacts.get(0).getPhones().size(); i++) {
	        		 TextView phoneNum = new TextView(getApplicationContext());
	        		 phoneNum.setText("Phone " + (i+1) + ": " + contacts.get(0).getPhones().get(i).getType() + 
	        				 " - " + contacts.get(0).getPhones().get(i).getNumber());
	        		 testLayout.addView(phoneNum);
	        	 }
	        	 
	        	 box = new TextView(getApplicationContext());
	        	 testLayout.addView(box);
	        	 
	        	 for(int i = 0; i < contacts.get(0).getEmails().size(); i++) {
	        		 TextView email = new TextView(getApplicationContext());
	        		 email.setText("Email " + (i+1) + ": " + contacts.get(0).getEmails().get(i).getType() + 
	        				 " - " + contacts.get(0).getEmails().get(i).getEmail());
	        		 testLayout.addView(email);
	        	 }
	        	 
	        	 box = new TextView(getApplicationContext());
	        	 testLayout.addView(box);
	        	 
	        	 for(int i = 0; i < contacts.get(0).getAddresses().size(); i++) {
	        		 TextView address = new TextView(getApplicationContext());
	        		 address.setText("Address " + (i+1) + ": " + contacts.get(0).getAddresses().get(i).getType() + 
	        				 " - " + contacts.get(0).getAddresses().get(i).getAddress());
	        		 testLayout.addView(address);
	        	 }
	        	 
	        	 box = new TextView(getApplicationContext());
	        	 testLayout.addView(box);
	        	 
	        	 TextView dob = new TextView(getApplicationContext());
	        	 dob.setText("Date of Birth: " + contacts.get(0).getDateOfBirth());
	        	 testLayout.addView(dob);
	         }
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
	
	
}
