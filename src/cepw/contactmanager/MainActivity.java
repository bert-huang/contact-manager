package cepw.contactmanager;

import java.lang.reflect.Field;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		forceCreateOverflow();
		getActionBar().setDisplayShowTitleEnabled(false);
		
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
				NewContactActivity.class);
		startActivity(i);
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
