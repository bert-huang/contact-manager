package cepw.contactmanager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import cepw.contact.*;
import cepw.contact.Email.InvalidEmailException;
import cepw.contact.Phone.InvalidPhoneException;
import cepw.contactmanager.ImageChooserDialog.LoadImageType;
import cepw.contactmanager.database.DatabaseHandler;

/**
 * This is an android activity that contains fields that allows user to create a
 * new or edit a pre existing contact.
 * 
 * @author I-Yang Huang, IHUA164, 5503504
 */
public class EditActivity extends Activity implements
		ImageChooserDialog.OnCompleteListener {

	private static final String LOG = "EditActivity";
	
	// Request code
	private static final int GALLERY_REQUEST = 1;
	private static final int CAMERA_REQUEST = 2;
	private static final int IMG_CROP_REQUEST = 3;
	
	private static final int IMAGE_SIZE = Photo.IMAGE_SIZE;

	// Constants for determining the type of field to inflate when buttons are
	// clicked on different views
	private enum FieldType { PHONE, EMAIL, ADDRESS }

	private DatabaseHandler db;
	
	// Individual components
	private ImageView imageBtn;
	private ImageButton expandName, collapseName, clearDob;
	private EditText fullName, firstName, middleName, lastName, nameSuffix;
	private TextView dobField;
	private LinearLayout dynamicPhoneLayout, dynamicEmailLayout, dynamicAddressLayout;
	private Bitmap displayPhoto;

	// Fields for Bundles
	private Contact contact = null;
	private int position;
	private boolean isNewContact;
	
	//Camera
	private Uri uri;

	/**
	 * @see android.app.Activity#onCreate(Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayShowTitleEnabled(false);

		// Initializing
		imageBtn = (ImageView) findViewById(R.id.button_change_display_image);
		displayPhoto = BitmapFactory.decodeResource(getResources(), R.drawable.ic_face);

		expandName = (ImageButton) findViewById(R.id.button_name_expand);
		collapseName = (ImageButton) findViewById(R.id.button_name_collapse);
		fullName = (EditText) findViewById(R.id.textfield_name_full);
		firstName = (EditText) findViewById(R.id.textfield_name_given);
		middleName = (EditText) findViewById(R.id.textfield_name_middle);
		lastName = (EditText) findViewById(R.id.textfield_name_last);
		nameSuffix = (EditText) findViewById(R.id.textfield_name_suffix);

		dynamicPhoneLayout = (LinearLayout) findViewById(R.id.layout_dynamic_phonefield);
		dynamicEmailLayout = (LinearLayout) findViewById(R.id.layout_dynamic_emailfield);
		dynamicAddressLayout = (LinearLayout) findViewById(R.id.layout_dynamic_addressfield);

		dobField = (TextView) findViewById(R.id.textview_dob);
		clearDob = (ImageButton) findViewById(R.id.button_clear_dob);

		// Set component visibilities
		collapseName.setVisibility(View.GONE);
		firstName.setVisibility(View.GONE);
		middleName.setVisibility(View.GONE);
		lastName.setVisibility(View.GONE);
		nameSuffix.setVisibility(View.GONE);
		clearDob.setVisibility(View.INVISIBLE);

		// add actions for components
		setOnClickActions();

		// Getting Data
		Bundle extras = getIntent().getExtras();

		// IMPORTANT!
		// If extras is null, means it's coming from the ADD NEW menu button.
		// If extras is NOT null, means it's coming from the EDIT menu button.
		if (extras == null) {
			isNewContact = true;
		} else {
			isNewContact = false;
			position = extras.getInt("POSITION");
			contact = extras.getParcelable("SELECTED_CONTACT");
		}
		// Populate data (If possible)
		populateData(contact);

		// Request focus on the name section
		fullName.setFocusable(true);
		fullName.requestFocus();
		fullName.setSelection(0);
	}

	/**
	 * @see android.app.Activity#onCreateOptionsMenu(Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit, menu);
		return true;
	}

	/**
	 * @see android.app.Activity#onOptionsItemSelected(MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		// Home button acts the same as the back button, which simply shows the
		// discard dialog
		case android.R.id.home:
			onBackPressed();
			return true;

			// Discard button displays discard dialog
		case R.id.action_create_discard:
			showDiscardDialog();
			return true;

			// Done button creates || updates a contact, and pass the data back
			// to source activity
		case R.id.action_create_done:

			Contact contact;
			try {
				// Generate/update contact
				contact = generateContact(this.contact);

				Intent intent = new Intent();
				// If is new contact, pass the created contact back to source
				// activity
				if (isNewContact) {
					if (contact == null) {
						setResult(RESULT_CANCELED, intent);
						finish();
					}else {
						intent.putExtra("NEW_CONTACT", contact);
						Log.d(LOG, ""+contact.getID());
						setResult(RESULT_OK, intent);
						Toast.makeText(EditActivity.this, "Created",
								Toast.LENGTH_SHORT).show();
						finish();
					}

					// If is existing contact, pass the updated contact back to
					// source activity
				} else {
					if (contact == null) {
						setResult(RESULT_CANCELED, intent);
						Toast.makeText(EditActivity.this, "Not Saved",
								Toast.LENGTH_SHORT).show();
						finish();
					}else {
						intent.putExtra("POSITION", position);
						intent.putExtra("EDITED_CONTACT", contact);
						setResult(RESULT_OK, intent);
						Toast.makeText(EditActivity.this, "Saved",
								Toast.LENGTH_SHORT).show();
						finish();
					}
				}

				// If InvalidEmailException is caught, display a toast
			} catch (InvalidEmailException e) {
				Toast.makeText(
						this,
						"Invalid E-mail detected!\nPlease fix it and try again.",
						Toast.LENGTH_SHORT).show();

				// If InvalidPhoneException is caught, display a toast
			} catch (InvalidPhoneException e) {
				Toast.makeText(
						this,
						"Invalid phone number detected!\nPlease fix it and try again.",
						Toast.LENGTH_SHORT).show();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @see android.app.Activity#onActivityResult(int, int, Intent)
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		// If result's request code is either GALLERY_REQUEST or CAMERA_REQUEST
		if (requestCode == GALLERY_REQUEST || requestCode == CAMERA_REQUEST) {
			if (resultCode == RESULT_OK) {
				Uri selectedImage;
				// Get the Uri
				if(data != null){
					selectedImage = data.getData();
				}else{
					selectedImage = uri;
				}
				

				// Perform crop
				// If phone does not support image cropping, then simply squeeze image
				if (!performCrop(selectedImage)) {
					String[] filePathColumn = { MediaStore.Images.Media.DATA };

					Cursor cursor = getContentResolver().query(selectedImage,
							filePathColumn, null, null, null);
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					String picturePath = cursor.getString(columnIndex);
					cursor.close();

					Bitmap b = BitmapFactory.decodeFile(picturePath);
					if (b != null) {
						displayPhoto = b;
						imageBtn.setImageBitmap(Bitmap.createScaledBitmap(b,
								IMAGE_SIZE, IMAGE_SIZE, false));
					} else {
						Toast.makeText(this, "Failed to load image!",
								Toast.LENGTH_LONG).show();
					}
				}
			}
		}

		// If result request code is IMG_CROP_REQUEST
		if (requestCode == IMG_CROP_REQUEST) {
			if (resultCode == RESULT_OK && data != null) {
				// get the returned data
				Bundle extras = data.getExtras();
				// get the cropped bitmap
				Bitmap b = extras.getParcelable("data");

				// if successfully retrieved bitmap, store to field and assign
				// to img
				if (b != null) {
					displayPhoto = b;
					imageBtn.setImageBitmap(b);
				}
			}
		}
	}

	/**
	 * When the back button is pressed, display Discard Dialog
	 */
	@Override
	public void onBackPressed() {
		// Check if all fields are empty
		showDiscardDialog();
	}

	/**
	 * Populate and initialize fields
	 * 
	 * @param contact
	 *            A contact object pass in through another intent. If null, then
	 *            initialize fields for creating new contact If != null, then
	 *            initialize fields with the data within the contact
	 */
	private void populateData(Contact contact) {
		if (contact != null) {
			// Set up image
			displayPhoto = contact.getPhoto().getImage();

			// Set up name
			String full = Name.parseName(null,
					contact.getName().getFirstName(), contact.getName()
							.getMiddleName(), contact.getName().getLastName(),
					contact.getName().getSuffix())[0];
			fullName.setText(full);

			// Set up phone
			for (int i = 0; i < contact.getPhones().size(); i++) {
				Phone p = contact.getPhones().get(i);
				// Get type (for spinner selection index
				int type = 0;
				if (p.getType().equals("Mobile")) {
					type = 0;
				} else if (p.getType().equals("Home")) {
					type = 1;
				} else if (p.getType().equals("Work")) {
					type = 2;
				} else if (p.getType().equals("Home Fax")) {
					type = 3;
				} else if (p.getType().equals("Work Fax")) {
					type = 4;
				} else if (p.getType().equals("Other")) {
					type = 5;
				}

				createNewField(FieldType.PHONE);
				((Spinner) ((ViewGroup) dynamicPhoneLayout.getChildAt(i))
						.getChildAt(0)).setSelection(type);
				((EditText) ((ViewGroup) dynamicPhoneLayout.getChildAt(i))
						.getChildAt(1)).setText(p.getNumber());
			}

			// Set up email
			for (int i = 0; i < contact.getEmails().size(); i++) {
				Email e = contact.getEmails().get(i);
				// Get type (for spinner selection index
				int type = 0;
				if (e.getType().equals("Home")) {
					type = 0;
				} else if (e.getType().equals("Work")) {
					type = 1;
				} else if (e.getType().equals("Other")) {
					type = 2;
				}

				createNewField(FieldType.EMAIL);
				((Spinner) ((ViewGroup) dynamicEmailLayout.getChildAt(i))
						.getChildAt(0)).setSelection(type);
				((EditText) ((ViewGroup) dynamicEmailLayout.getChildAt(i))
						.getChildAt(1)).setText(e.getEmail());
			}

			// Set up address
			for (int i = 0; i < contact.getAddresses().size(); i++) {
				Address a = contact.getAddresses().get(i);
				// Get type (for spinner selection index
				int type = 0;
				if (a.getType().equals("Home")) {
					type = 0;
				} else if (a.getType().equals("Work")) {
					type = 1;
				} else if (a.getType().equals("Other")) {
					type = 2;
				}

				createNewField(FieldType.ADDRESS);
				((Spinner) ((ViewGroup) dynamicAddressLayout.getChildAt(i))
						.getChildAt(0)).setSelection(type);
				((EditText) ((ViewGroup) dynamicAddressLayout.getChildAt(i))
						.getChildAt(1)).setText(a.getAddress());
			}

			// Set up date of birth
			dobField.setText(contact.getDateOfBirth().getValue());
			if (dobField.getText().toString().equals("")) {
				clearDob.setVisibility(View.INVISIBLE);
			} else {
				clearDob.setVisibility(View.VISIBLE);
			}
		} else {
			createNewField(FieldType.PHONE);
		}

		// Assign Image
		imageBtn.setImageBitmap(displayPhoto);
	}

	/**
	 * Generate or update an existing contact based on the data entered in the
	 * fields
	 * 
	 * @param contact
	 *            A contact object pass in through another intent. If null, then
	 *            at the end of the method, create a new Contact object If !=
	 *            null, then at the end of the method, update the new Contact
	 *            object
	 * 
	 * @return either a new Contact object, or an updated Contact, depend on
	 *         whether the parameter is null or not null.
	 * @throws InvalidEmailException
	 *             when invalid email format detected
	 * @throws InvalidPhoneException
	 *             when invalid phone format detected
	 */
	private Contact generateContact(Contact contact)
			throws InvalidEmailException, InvalidPhoneException {
		Name name = null;
		Photo photo = null;
		int childCount = 0;
		List<Phone> phones = new ArrayList<Phone>();
		List<Email> emails = new ArrayList<Email>();
		List<Address> addresses = new ArrayList<Address>();
		DateOfBirth dob = null;

		// Parsing contact name
		if (fullName.getVisibility() == View.VISIBLE) {
			String[] splits = Name.parseName(fullName.getText().toString(),
					null, null, null, null);
			name = new Name(splits[0], splits[1], splits[2], splits[3]);

		} else {
			name = new Name(firstName.getText().toString(), middleName
					.getText().toString(), lastName.getText().toString(),
					nameSuffix.getText().toString());
		}

		// Get Photo
		photo = new Photo(displayPhoto);

		// Populating phones
		childCount = dynamicPhoneLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			ViewGroup vg = (ViewGroup) dynamicPhoneLayout.getChildAt(i);
			String type = ((Spinner) vg.getChildAt(0)).getSelectedItem()
					.toString();
			String number = ((EditText) vg.getChildAt(1)).getText().toString();

			Phone phoneObject = (i == 0) ? new Phone(type, number, true)
					: new Phone(type, number, false);

			if (phoneObject.getNumber().isEmpty())
				continue;
			phones.add(phoneObject);

		}
		Collections.sort(phones, new Phone.PhoneComparator());

		// Populating emails
		childCount = dynamicEmailLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			ViewGroup vg = (ViewGroup) dynamicEmailLayout.getChildAt(i);
			String type = ((Spinner) vg.getChildAt(0)).getSelectedItem()
					.toString();
			String email = ((EditText) vg.getChildAt(1)).getText().toString();

			Email emailObject = null;
			emailObject = new Email(type, email);

			if (emailObject.getEmail().isEmpty())
				continue;
			emails.add(emailObject);

		}

		// Populating addresses
		childCount = dynamicAddressLayout.getChildCount();
		for (int i = 0; i < childCount; i++) {
			ViewGroup vg = (ViewGroup) dynamicAddressLayout.getChildAt(i);
			String type = ((Spinner) vg.getChildAt(0)).getSelectedItem()
					.toString();
			String address = ((EditText) vg.getChildAt(1)).getText().toString();

			Address addressObject = new Address(type, address);
			if (addressObject.getAddress().isEmpty())
				continue;
			addresses.add(addressObject);

		}

		// Get Date of Birth
		dob = new DateOfBirth(dobField.getText().toString());

		// Check if all fields are empty
		boolean isEmpty = 
				(name.getFirstName().length() + 
				name.getMiddleName().length()+
				name.getLastName().length() +
				name.getSuffix().length() +
				phones.size() +
				emails.size() + 
				addresses.size() + 
				dob.getValue().length()) == 0 
				? true : false;
		
		// If all fields are empty, return null
		db = new DatabaseHandler(getApplicationContext());
		if (isEmpty) {
			contact = null;
			
		// If contact existed (therefore editing)
		}else if (contact != null) {
			contact.setPhoto(photo);
			contact.setName(name);
			contact.setPhones(phones);
			contact.setEmail(emails);
			contact.setAddresses(addresses);
			contact.setDateOfBirth(dob);
			Log.d(LOG, "Contact Object Updated");
			db.updateContact(contact);
			Log.d(LOG, "Updated Contact in DB");
			
		// If contact is null (therefore creating new)
		} else {
			long contactId = db.createContact(name, photo, phones, emails, addresses, dob);
			Log.d(LOG, "Created Contact in DB");
			contact = db.getContact(contactId, null);
			Log.d(LOG, "Contact Object Created");
		}
		
		db.closeDB();
		return contact;

	}

	/**
	 * A method that contains all the method for anonymous OnClickListener
	 * implementation
	 */
	private void setOnClickActions() {

		// Image chooser button
		imageBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DialogFragment imageLoader = new ImageChooserDialog();
				imageLoader.show(getFragmentManager(), "Image Selection Option");
			}
		});

		// Expand name, hide full name edittext, display first, middle, last and
		// suffix edittext
		expandName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				expandName.setVisibility(View.GONE);
				collapseName.setVisibility(View.VISIBLE);

				String[] splits = Name.parseName(fullName.getText().toString(),
						null, null, null, null);
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
		});

		// Collapse name, hide first, middle, last and suffix edittext, display
		// full name edittext
		collapseName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				expandName.setVisibility(View.VISIBLE);
				collapseName.setVisibility(View.GONE);

				String[] combine = Name.parseName(null, firstName.getText()
						.toString(), middleName.getText().toString(), lastName
						.getText().toString(), nameSuffix.getText().toString());
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

		// Date of click field, when clicked display DateChooserDialog
		dobField.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DateChooserDialog();
				newFragment.show(getFragmentManager(), "datePicker");
			}
		});

		// Clears dob edittext.
		clearDob.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clearDob.setVisibility(View.INVISIBLE);
				dobField.setText("");
			}
		});
	}

	/**
	 * This method is for the add button for phone, email and address. Because
	 * the + button view does not have an ID, they have to be created by
	 * inflating XML layout, thus using the onClick xml code.
	 * 
	 * @param v
	 *            The view that is clicked
	 */
	public void addNewField(View v) {

		// Check which parent this view belongs to.
		// Could be LinearLayout for Phone, Email or Address
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

		// Created new field based on the field type
		createNewField(ft);
	}

	/**
	 * Created a new field View by inflating XML layout and adding it to its
	 * parent layout based on the field type parameter.
	 * 
	 * @param fieldType
	 */
	private void createNewField(FieldType fieldType) {

		// Get the inflate layout ID, parent layout and String array for the
		// spinner
		int infl = 0;
		int charSeq = 0;
		LinearLayout ll = null;
		switch (fieldType) {
		case PHONE:
			infl = R.layout.phone_field_item;
			ll = dynamicPhoneLayout;
			charSeq = R.array.phone_type;
			break;
		case EMAIL:
			infl = R.layout.email_field_item;
			ll = dynamicEmailLayout;
			charSeq = R.array.email_type;
			break;
		case ADDRESS:
			infl = R.layout.address_field_item;
			ll = dynamicAddressLayout;
			charSeq = R.array.address_type;
			break;
		default:
			return;
		}

		// Inflate XML layout
		ViewGroup fieldInfo = (ViewGroup) getLayoutInflater().inflate(infl, ll,
				false);

		// Setup spinner
		Spinner spinner = (Spinner) fieldInfo.getChildAt(0);
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, charSeq, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);

		// Add the inflated view to the parent layout
		ll.addView(fieldInfo);
	}

	/**
	 * This method is for the remove button for phone, email and address.
	 * Because the remove button view does not have an ID, they have to be
	 * created by inflating XML layout, thus using the onClick xml syntax.
	 * 
	 * @param v
	 *            The view that is clicked
	 */
	public void removeCurrentField(View v) {
		ViewGroup view2rm = (ViewGroup) v.getParent();
		ViewGroup parent = (ViewGroup) view2rm.getParent();

		parent.removeView(view2rm);

	}

	/**
	 * A discard dialog when home, back and discard buttons is clicked.
	 */
	public void showDiscardDialog() {
		if(isAllFieldsEmpty()){
			setResultCanceled();
		}else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("All changes will be lost")
					.setTitle("Discard changes?").setNegativeButton("Cancel", null)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							setResultCanceled();
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
		
	}
	
	/**
	 * Perform final operations when user discards changes
	 */
	private void setResultCanceled(){
		Intent intent = new Intent();
		intent.putExtra("POSITION", position);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	/**
	 * Performs a image crop by launching external image crop application
	 * 
	 * @param picUri
	 *            Uri of the image you want to crop
	 * @return whether the crop is success. Returns false if cropping is not
	 *         supported.
	 */
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
			cropIntent.putExtra("outputX", IMAGE_SIZE);
			cropIntent.putExtra("outputY", IMAGE_SIZE);
			// retrieve data on return
			cropIntent.putExtra("return-data", true);
			// start the activity - we handle returning in onActivityResult
			startActivityForResult(cropIntent, IMG_CROP_REQUEST);
			return true;
		}
		// respond to users whose devices do not support the crop action
		catch (ActivityNotFoundException anfe) {
			// display an error message
			String errorMessage = "Your device doesn't support the crop action";
			Toast toast = Toast
					.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
			return false;
		}
	}

	/**
	 * This onComplete method is for ImageChooserDialog. It will react according
	 * to what the user selected in the list dialog.
	 */
	@Override
	public void onComplete(LoadImageType loadingType) {
		switch (loadingType) {
		case SELECTED_GALLERY:
			Intent galleryIntent = new Intent(
					Intent.ACTION_PICK,
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(galleryIntent, GALLERY_REQUEST);
			break;
		case SELECTED_CAMERA:
			uri = getOutputMediaFileUri();
			Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(captureIntent, CAMERA_REQUEST);
			break;
		}

	}

	/**
	 * Check whether all the fields are empty
	 * NOTE: IMAGES DOES NOT COUNT
	 * @return true if all the fields are empty, false otherwise
	 */
	private boolean isAllFieldsEmpty() {
		
		// Check if All phone entries are empty
		boolean emptyPhones = true;
		EditText editText = null;
		for(int i = 0; i < dynamicPhoneLayout.getChildCount(); i++){
			editText = (EditText)((ViewGroup)dynamicPhoneLayout.getChildAt(i)).getChildAt(1);
			if(editText.getText().length() != 0){
				emptyPhones = false;
			}
		}
		
		// Check if All email entries are empty
		boolean emptyEmails = true;
		for(int i = 0; i < dynamicEmailLayout.getChildCount(); i++){
			editText = (EditText)((ViewGroup)dynamicPhoneLayout.getChildAt(i)).getChildAt(1);
			if(editText.getText().length() != 0){
				emptyPhones = false;
			}
		}
		
		// Check if All address entries are empty
		boolean emptyAddress = true;
		for(int i = 0; i < dynamicAddressLayout.getChildCount(); i++){
			editText = (EditText)((ViewGroup)dynamicAddressLayout.getChildAt(i)).getChildAt(1);
			if(editText.getText().length() != 0){
				emptyAddress = false;
			}
		}
		
		// Check if name fields are empty
		boolean emptyName = 
				(	fullName.getText().length() +
					firstName.getText().length() + 
					middleName.getText().length()+
					lastName.getText().length() +
					nameSuffix.getText().length() == 0)
					? true : false;
		
		// Check if date of birth fields are empty
		boolean emptyDob = (dobField.getText().length() == 0) ? true : false;
		
		return emptyName && emptyPhones && emptyEmails && emptyAddress & emptyDob;
	}
	
	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(){
	      return Uri.fromFile(getOutputMediaFile());
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(){
	    // To be safe, you should check that the SDCard is mounted
	    // using Environment.getExternalStorageState() before doing this.

	    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
	              Environment.DIRECTORY_PICTURES), "cepw.temp");
	    // This location works best if you want the created images to be shared
	    // between applications and persist after your app has been uninstalled.

	    // Create the storage directory if it does not exist
	    if (! mediaStorageDir.exists()){
	        if (! mediaStorageDir.mkdirs()){
	            Log.d(LOG, "failed to create directory");
	            return null;
	        }
	    }

	    // Create a media file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    File mediaFile = null;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

	    return mediaFile;
	}
}
