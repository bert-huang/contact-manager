package cepw.contactmanager;

import cepw.contact.Contact;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

public class InfoActivity extends Activity {

	private Contact contact = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
		    if(extras == null) {
		    	contact = null;
		    	finish();
		    } else {
		    	contact = extras.getParcelable("SELECTED_CONTACT");
		    }
		} else {
			contact= (Contact) savedInstanceState.getSerializable("SELECTED_CONTACT");
		}
		
		
		
		setupActionBar();
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
		default:
			return true;
			
		}
	}

}
