package cepw.contactmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cepw.contact.*;
import cepw.contact.Email.InvalidEmailException;

public class EditActivity extends Activity {

	static final int RESULT_LOAD_IMAGE = 1;
	final int PIC_CROP = 2;

	private final int BTN_BORDER = 11;

	private enum FieldType {
		PHONE, EMAIL, ADDRESS
	};

	private Button newFieldCategoryBtn;
	private ImageButton imageBtn, expandName, collapseName, clearDob;
	private EditText fullName, firstName, middleName, lastName, nameSuffix;
	private TextView dobField;
	private LinearLayout emailLinLayout, addressLinLayout, dobLinLayout;
	private LinearLayout dynamicPhoneLinLayout, dynamicEmailLinLayout,
			dynamicAddressLinLayout;

	private Bitmap displayPhoto;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		setupActionBar();
		setupNameFields();
		setupDobField();

		imageBtn = (ImageButton) findViewById(R.id.button_change_display_image);
		displayPhoto = BitmapFactory.decodeResource(getResources(),
				R.drawable.ic_face);
		dynamicPhoneLinLayout = (LinearLayout) findViewById(R.id.layout_dynamic_phonefield);
		emailLinLayout = (LinearLayout) findViewById(R.id.layout_emailfields);
		dynamicEmailLinLayout = (LinearLayout) findViewById(R.id.layout_dynamic_emailfield);
		addressLinLayout = (LinearLayout) findViewById(R.id.layout_addressfields);
		dynamicAddressLinLayout = (LinearLayout) findViewById(R.id.layout_dynamic_addressfield);
		dobLinLayout = (LinearLayout) findViewById(R.id.layout_dobfields);
		newFieldCategoryBtn = (Button) findViewById(R.id.button_new_field_category);

		emailLinLayout.setVisibility(View.GONE);
		addressLinLayout.setVisibility(View.GONE);
		dobLinLayout.setVisibility(View.GONE);
		newFieldCategoryBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new FieldCategoryDialog();
				newFragment.show(getFragmentManager(), "newfield");
			}
		});

		imageBtn.setImageBitmap(displayPhoto);
		imageBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
			}
		});

		createNewField(FieldType.PHONE, dynamicPhoneLinLayout);
		createNewField(FieldType.EMAIL, dynamicEmailLinLayout);
		createNewField(FieldType.ADDRESS, dynamicAddressLinLayout);

		fullName.setFocusable(true);
		fullName.requestFocus();

		// DUMMY!
		createNewField(FieldType.PHONE, dynamicPhoneLinLayout);
		createNewField(FieldType.EMAIL, dynamicEmailLinLayout);
		createNewField(FieldType.ADDRESS, dynamicAddressLinLayout);

		fullName.setText("Bert DERP Huang, #YOLOSWAG");

		((Spinner) ((ViewGroup) dynamicPhoneLinLayout.getChildAt(0))
				.getChildAt(0)).setSelection(0);
		((EditText) ((ViewGroup) dynamicPhoneLinLayout.getChildAt(0))
				.getChildAt(1)).setText("0211557760");
		((Spinner) ((ViewGroup) dynamicPhoneLinLayout.getChildAt(1))
				.getChildAt(0)).setSelection(1);
		((EditText) ((ViewGroup) dynamicPhoneLinLayout.getChildAt(1))
				.getChildAt(1)).setText("095376635");

		((Spinner) ((ViewGroup) dynamicEmailLinLayout.getChildAt(0))
				.getChildAt(0)).setSelection(0);
		((EditText) ((ViewGroup) dynamicEmailLinLayout.getChildAt(0))
				.getChildAt(1)).setText("dendeer82@gmail.com");
		((Spinner) ((ViewGroup) dynamicEmailLinLayout.getChildAt(1))
				.getChildAt(0)).setSelection(1);
		((EditText) ((ViewGroup) dynamicEmailLinLayout.getChildAt(1))
				.getChildAt(1)).setText("ihua164@aucklanduni.ac.nz");

		((EditText) ((ViewGroup) dynamicAddressLinLayout.getChildAt(0))
				.getChildAt(1)).setText("51 Evelyn Road, Cockle Bay, Auckland");
		((EditText) ((ViewGroup) dynamicAddressLinLayout.getChildAt(1))
				.getChildAt(1)).setText("彰化縣溪湖鎮西還路205號");

		dobField.setText("27-01-1993");

	}

	private void setupActionBar() {
		
		// Show the Up button in the action bar.
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

		expandName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				expandName.setVisibility(View.GONE);
				collapseName.setVisibility(View.VISIBLE);

				if (!fullName.getText().toString().equals("")) {
					String[] splits = Name.ParseName(fullName.getText()
							.toString(), firstName.getText().toString(),
							middleName.getText().toString(), lastName.getText()
									.toString(), nameSuffix.getText()
									.toString());
					fullName.setText("");
					firstName.setText(splits[0]);
					middleName.setText(splits[1]);
					lastName.setText(splits[2]);
					nameSuffix.setText(splits[3]);
				}

				fullName.setVisibility(View.GONE);
				nameSuffix.setVisibility(View.VISIBLE);
				firstName.setVisibility(View.VISIBLE);
				middleName.setVisibility(View.VISIBLE);
				lastName.setVisibility(View.VISIBLE);

				firstName.setFocusable(true);
				firstName.requestFocus();

			}
		});
		collapseName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				expandName.setVisibility(View.VISIBLE);
				collapseName.setVisibility(View.GONE);

				String[] combine = Name.ParseName(
						fullName.getText().toString(), firstName.getText()
								.toString(), middleName.getText().toString(),
						lastName.getText().toString(), nameSuffix.getText()
								.toString());
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
		});

	}

	private void setupDobField() {
		dobField = (TextView) findViewById(R.id.textview_dob);
		clearDob = (ImageButton) findViewById(R.id.button_clear_dob);

		dobField.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DateChooserDialog();
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});
		clearDob.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dobField.setText("");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			showDiscardDialog();
			return true;

		case R.id.action_create_discard:
			showDiscardDialog();
			return true;

		case R.id.action_create_done:

			Intent intent = new Intent();
			Contact contact = null;
			Name name = null;
			Photo photo = null;
			int childCount = 0;
			List<Phone> phones = new ArrayList<Phone>();
			List<Email> emails = new ArrayList<Email>();
			List<Address> addresses = new ArrayList<Address>();
			DateOfBirth dob = null;

			// Parsing contact name
			if (fullName.getVisibility() == View.VISIBLE) {
				if (!fullName.getText().toString().equals("")) {
					String[] splits = Name.ParseName(fullName.getText()
							.toString(), firstName.getText().toString(),
							middleName.getText().toString(), lastName.getText()
									.toString(), nameSuffix.getText()
									.toString());
					name = new Name(splits[0], splits[1], splits[2], splits[3]);
				} else {
					name = new Name("", "", "", "");
				}
			} else {
				name = new Name(firstName.getText().toString(), middleName
						.getText().toString(), lastName.getText().toString(),
						nameSuffix.getText().toString());
			}

			// Get Photo
			photo = new Photo(displayPhoto);

			// Populating phones
			childCount = dynamicPhoneLinLayout.getChildCount();
			for (int i = 0; i < childCount; i++) {
				ViewGroup vg = (ViewGroup) dynamicPhoneLinLayout.getChildAt(i);
				String type = ((Spinner) vg.getChildAt(0)).getSelectedItem()
						.toString();
				String number = ((EditText) vg.getChildAt(1)).getText()
						.toString();

				Phone phoneObject = (i == 0) ? new Phone(type, number, true)
						: new Phone(type, number, false);

				if (phoneObject.getNumber().isEmpty())
					continue;
				phones.add(phoneObject);

			}
			Collections.sort(phones);

			// Populating emails
			childCount = dynamicEmailLinLayout.getChildCount();
			for (int i = 0; i < childCount; i++) {
				ViewGroup vg = (ViewGroup) dynamicEmailLinLayout.getChildAt(i);
				String type = ((Spinner) vg.getChildAt(0)).getSelectedItem()
						.toString();
				String email = ((EditText) vg.getChildAt(1)).getText()
						.toString();

				Email emailObject = null;
				try {
					emailObject = new Email(type, email);

					if (emailObject.getEmail().isEmpty())
						continue;
					emails.add(emailObject);

				} catch (InvalidEmailException e) {
					Toast.makeText(
							this,
							"Invalid E-mail detected!\n"
									+ "Please fix it and try again.",
							Toast.LENGTH_SHORT).show();
					return false;
				}
			}

			// Populating addresses
			childCount = dynamicAddressLinLayout.getChildCount();
			for (int i = 0; i < childCount; i++) {
				ViewGroup vg = (ViewGroup) dynamicAddressLinLayout
						.getChildAt(i);
				String type = ((Spinner) vg.getChildAt(0)).getSelectedItem()
						.toString();
				String address = ((EditText) vg.getChildAt(1)).getText()
						.toString();

				Address addressObject = new Address(type, address);
				if (addressObject.getAddress().isEmpty())
					continue;
				addresses.add(addressObject);

			}

			// Get Date of Birth
			dob = new DateOfBirth(dobField.getText().toString());

			contact = new Contact(name, photo, phones, emails, addresses, dob);

			intent.putExtra("NEW_CONTACT", contact);
			setResult(RESULT_OK, intent);
			Toast.makeText(this, " created!", Toast.LENGTH_SHORT).show();
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == RESULT_LOAD_IMAGE) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					Uri selectedImage = data.getData();

					// If phone does not support image cropping, then simply
					// squeeze image
					if (!performCrop(selectedImage)) {
						String[] filePathColumn = { MediaStore.Images.Media.DATA };

						Cursor cursor = getContentResolver()
								.query(selectedImage, filePathColumn, null,
										null, null);
						cursor.moveToFirst();

						int columnIndex = cursor
								.getColumnIndex(filePathColumn[0]);
						String picturePath = cursor.getString(columnIndex);
						cursor.close();

						Bitmap b = BitmapFactory.decodeFile(picturePath);
						if (b != null) {
							displayPhoto = b;
							imageBtn.setImageBitmap(Bitmap.createScaledBitmap(
									b, imageBtn.getWidth() - BTN_BORDER,
									imageBtn.getHeight() - BTN_BORDER, false));
						} else {
							Toast.makeText(this, "Failed to load image!",
									Toast.LENGTH_LONG).show();
						}
					}
				}
			}
		}

		if (requestCode == PIC_CROP) {
			if (resultCode == RESULT_OK) {
				if (data != null) {
					// get the returned data
					Bundle extras = data.getExtras();
					// get the cropped bitmap
					Bitmap b = extras.getParcelable("data");

					if (b != null) {
						displayPhoto = b;
						imageBtn.setImageBitmap(Bitmap.createScaledBitmap(b,
								imageBtn.getWidth() - BTN_BORDER,
								imageBtn.getHeight() - BTN_BORDER, false));
					}
				}
			}
		}
	}

	@Override
	public void onBackPressed() {
		showDiscardDialog();
	}

	// =======================================//
	//
	// Method for general buttons
	// (or buttons without ID)
	//
	// =======================================//
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

		createNewField(ft, v);
	}

	private void createNewField(FieldType ft, View v) {

		int infl = 0;
		int charSeq = 0;
		LinearLayout ll = null;
		switch (ft) {
		case PHONE:
			infl = R.layout.phone_field_item;
			ll = dynamicPhoneLinLayout;
			charSeq = R.array.phone_type;
			break;
		case EMAIL:
			infl = R.layout.email_field_item;
			ll = dynamicEmailLinLayout;
			charSeq = R.array.email_type;
			break;
		case ADDRESS:
			infl = R.layout.address_field_item;
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
				this, charSeq, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		ll.addView(fieldInfo);

		if (ll.getChildCount() >= 5) {
			v.setVisibility(View.INVISIBLE);
		}
	}

	public void removeCurrentField(View v) {
		int count = 0;
		ViewGroup view2rm = (ViewGroup) v.getParent();
		ViewGroup parent = (ViewGroup) view2rm.getParent();
		ViewGroup mainLayout = (ViewGroup) parent.getParent();

		parent.removeView(view2rm);
		count = parent.getChildCount();
		if (count < 5) {
			((ViewGroup) mainLayout.getChildAt(0)).getChildAt(1).setVisibility(
					View.VISIBLE);
		}

	}

	public void showDiscardDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("All data entries will be lost")
				.setTitle("Discard changes?")
				.setNegativeButton("Cancel", null)
				.setPositiveButton("Confirm",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();
							}
						});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private boolean performCrop(Uri picUri) {
		try {

			Intent cropIntent = new Intent("com.android.camera.action.CROP");
			// indicate image type and Uri
			cropIntent.setDataAndType(picUri, "image/*");
			// set crop properties
			cropIntent.putExtra("crop", "true");
			// indicate aspect of desired crop
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			// indicate output X and Y
			cropIntent.putExtra("outputX", 128);
			cropIntent.putExtra("outputY", 128);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, PIC_CROP);
			return true;
		}
		// respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Whoops - your device doesn't support the crop action!";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
	}
}
