package cepw.contactmanager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import cepw.contact.*;

public class MainActivity extends Activity {

	static final int CREATE_CONTACT_REQUEST = 1; // The request code

	private List<Contact> contacts;
	private ListView list;
	private ArrayAdapter<Contact> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		forceCreateOverflow();
		getActionBar().setDisplayShowTitleEnabled(false);

		contacts = new ArrayList<Contact>();
		list = (ListView) findViewById(R.id.listview_contact_list);
		adapter = new ContactListAdapter(MainActivity.this, contacts);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new ListItemClickedListener());
		list.setOnItemLongClickListener(new ListItemClickedListener());

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
		Intent i = new Intent(getApplicationContext(), EditActivity.class);
		startActivityForResult(i, CREATE_CONTACT_REQUEST);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == CREATE_CONTACT_REQUEST) {
			if (resultCode == RESULT_OK) {

				contacts.add((Contact) data.getExtras().getParcelable(
						"NEW_CONTACT"));
				adapter.notifyDataSetChanged();
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

			iv.setImageBitmap(Bitmap.createScaledBitmap(curContact.getImage(),
					dpToPx(50), dpToPx(50), false));
			nameTag.setText(fullName);

			return (View) vg;

		}

		private int dpToPx(int dp) {
			DisplayMetrics displayMetrics = getContext().getResources()
					.getDisplayMetrics();
			int px = Math.round(dp
					* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
			return px;
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
			Toast.makeText(getApplicationContext(), "Contact Removed!", Toast.LENGTH_SHORT).show();
			return true;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent i = new Intent(getApplicationContext(), InfoActivity.class);
			i.putExtra("SELECTED_CONTACT", contacts.get(position));
			startActivity(i);
			
		}

	}
}
