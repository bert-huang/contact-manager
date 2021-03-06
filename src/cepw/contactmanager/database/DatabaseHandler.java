package cepw.contactmanager.database;

import java.util.ArrayList;
import java.util.List;

import cepw.contact.Contact;
import cepw.contact.DateOfBirth;
import cepw.contact.Name;
import cepw.contact.Phone;
import cepw.contact.Email;
import cepw.contact.Address;
import cepw.contact.Email.InvalidEmailException;
import cepw.contact.Phone.InvalidPhoneException;
import cepw.contact.Photo;
import cepw.contactmanager.R;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.BitmapFactory;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "contactsManager";

	// Contacts table name
	private static final String TABLE_CONTACTS = "contacts";

	// Contacts Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_FIRST_NAME = "first_name";
	private static final String KEY_MIDDLE_NAME = "middle_name";
	private static final String KEY_LAST_NAME = "last_name";
	private static final String KEY_NAME_SUFFIX = "name_suffix";
	private static final String KEY_PHONE_TYPE = "phone_types";
	private static final String KEY_PHONE_NUMBER = "phone_numbers";
	private static final String KEY_PHONE_PRIMARY = "phone_primary";
	private static final String KEY_EMAIL_TYPE = "email_types";
	private static final String KEY_EMAIL_VALUE = "email_value";
	private static final String KEY_ADDRESS_TYPE = "address_types";
	private static final String KEY_ADDRESS_VALUE = "address";
	private static final String KEY_DOB = "date_of_birth";
	private static final String KEY_IMAGE = "image";

	// Table Create Statements
	// Contacts table create statement
	private static final String CREATE_TABLE_CONTACTS = 
			"CREATE TABLE " + TABLE_CONTACTS + "(" + 
			KEY_ID 				+ " INTEGER PRIMARY KEY," + 
			KEY_FIRST_NAME 		+ " TEXT," + 
			KEY_MIDDLE_NAME 	+ " TEXT," + 
			KEY_LAST_NAME 		+ " TEXT," + 
			KEY_NAME_SUFFIX 	+ " TEXT," + 
			KEY_PHONE_TYPE		+ " TEXT," +
			KEY_PHONE_NUMBER	+ " TEXT," +
			KEY_PHONE_PRIMARY	+ " TEXT," +
			KEY_EMAIL_TYPE		+ " TEXT," +
			KEY_EMAIL_VALUE		+ " TEXT," +
			KEY_ADDRESS_TYPE	+ " TEXT," +
			KEY_ADDRESS_VALUE	+ " TEXT," +
			KEY_DOB 			+ " TEXT," + 
			KEY_IMAGE 			+ " BLOB" + ")";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_CONTACTS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CONTACTS);

		// create new tables
		onCreate(db);

	}

	// ========== Helper method ================

	// -=-=-=-=-=-= CREATE =-=-=-=-=-=-=- //
	
	/**
	 * Store data into database from provided information
	 * 
	 * @param name Name object
	 * @param photo Photo object
	 * @param phoneList A list of Phones
	 * @param emailList A list of Email
	 * @param addressList A list of Address
	 * @param dob DateOfBirth object
	 * @return The ID of the contact in the database
	 */
	public long createContact(Name name, Photo photo, List<Phone> phoneList, List<Email> emailList,
			List<Address> addressList, DateOfBirth dob) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		// Put name data into ContentValues
		values.put(KEY_FIRST_NAME, name.getFirstName());
		values.put(KEY_MIDDLE_NAME, name.getMiddleName());
		values.put(KEY_LAST_NAME, name.getLastName());
		values.put(KEY_NAME_SUFFIX, name.getSuffix());
		values.put(KEY_DOB, dob.getValue());
		values.put(KEY_IMAGE, photo.getByteArray());

		// Put Phone data into ContentValues
		String[] phoneData = createPhoneData(phoneList);
		values.put(KEY_PHONE_TYPE, phoneData[0]);
		values.put(KEY_PHONE_NUMBER, phoneData[1]);
		values.put(KEY_PHONE_PRIMARY, phoneData[2]);
		
		// Put Email data into ContentValues
		String[] emailData = createEmailData(emailList);
		values.put(KEY_EMAIL_TYPE, emailData[0]);
		values.put(KEY_EMAIL_VALUE, emailData[1]);
		
		// Put Address data into ContentValues
		String[] addressData = createAddressData(addressList);
		values.put(KEY_ADDRESS_TYPE, addressData[0]);
		values.put(KEY_ADDRESS_VALUE, addressData[1]);
		
		// Store the ContentValues datas into the database
		long contactId = db.insert(TABLE_CONTACTS, null, values);


		return contactId;
	}

	/**
	 * This method will convert a list of phone into 3 strings that is stored in an array.
	 * The type, number and isPrimary will be concatenated for each phone, separated by a ";"
	 * @param phoneList A list of Phone
	 * @return A String array with a size of 3.
	 * 	<br/>Index 0 will be the type of the phone number (Mobile, Home, Work..)
	 * 	<br/>Index 1 will be the number of the phone number
	 * 	<br/>Index 2 will be a string with 0 or 1, 1 will be the primary number
	 */
	public String[] createPhoneData(List<Phone> phoneList) {
		
		// String buffer
		StringBuffer phoneType = new StringBuffer();
		StringBuffer phoneNumber = new StringBuffer();
		StringBuffer phonePrimary = new StringBuffer();
		
		// Iterate through the phone list
		for (Phone p : phoneList) {
			phoneType	.append(p.getType() + ";");
			phoneNumber	.append(p.getNumber() + ";");
			phonePrimary.append((p.isPrimary()? "1" : "0") + ";");
		}
		
		// Delete the last occurrence of ";"
		if(phoneType.length() != 0) {phoneType.deleteCharAt(phoneType.length()-1);}
		if(phoneNumber.length() != 0) {phoneNumber.deleteCharAt(phoneNumber.length()-1);}
		if(phonePrimary.length() != 0) {phonePrimary.deleteCharAt(phonePrimary.length()-1);}
		
		// Return the array
		return new String[] {phoneType.toString(), phoneNumber.toString(), phonePrimary.toString()};
	}
	
	/**
	 * This method will convert a list of email into 2 strings that is stored in an array.
	 * The type and value will be concatenated for each email, separated by a ";"
	 * @param emailList A list of email
	 * @return A String array with a size of 2.
	 * 	<br/>Index 0 will be the type of the email (Home, Work, Other)
	 * 	<br/>Index 1 will be the email address
	 */
	public String[] createEmailData(List<Email> emailList) {
		
		// String buffers
		StringBuffer emailType = new StringBuffer();
		StringBuffer emailValue = new StringBuffer();
		
		// Iterate through all email in the list
		for (Email e : emailList) {
			emailType.append(e.getType() + ";");
			emailValue.append(e.getEmail() + ";");
		}
		// Delete the last occurrence of ";"
		if(emailType.length() != 0) {emailType.deleteCharAt(emailType.length()-1);}
		if(emailValue.length() != 0) {emailValue.deleteCharAt(emailValue.length()-1);}
		
		// Return the array
		return new String[] {emailType.toString(), emailValue.toString()};
	}
	
	/**
	 * This method will convert a list of address into 2 strings that is stored in an array.
	 * The type and value will be concatenated for each address, separated by a ";"
	 * @param addressList A list of address
	 * @return A String array with a size of 2.
	 * 	<br/>Index 0 will be the type of the address (Home, Work, Other)
	 * 	<br/>Index 1 will be the physical address
	 */
	public String[] createAddressData(List<Address> addressList) {
		StringBuffer addressType = new StringBuffer();
		StringBuffer addressValue = new StringBuffer();
		for (Address a : addressList) {
			addressType.append(a.getType() + ";");
			addressValue.append(a.getAddress() + ";");
		}
		if(addressType.length() != 0) {addressType.deleteCharAt(addressType.length()-1);}
		if(addressValue.length() != 0) {addressValue.deleteCharAt(addressValue.length()-1);}
		
		return new String[] {addressType.toString(), addressValue.toString()};
	}
	
	// -=-=-=-=-=-= Retrieve =-=-=-=-=-=-=- //

	/**
	 * Get a single contact data from the database and create a Contact object
	 * @param contactId ID of the contact
	 * @param db Param for an pre-opened database, if put null, then fetch the ReadableDatabase
	 * @return
	 */
	public Contact getContact(long contactId, SQLiteDatabase db) {
		if(db == null) {
			db = this.getReadableDatabase();
		}

		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS + " WHERE "
				+ KEY_ID + " = " + contactId;

		Log.d(LOG, selectQuery);

		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor != null)
			cursor.moveToFirst();

		Name name = new Name(
				cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)), 
				cursor.getString(cursor.getColumnIndex(KEY_MIDDLE_NAME)), 
				cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)), 
				cursor.getString(cursor.getColumnIndex(KEY_NAME_SUFFIX)));

		Photo photo;
		if (cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)) != null) {
			photo = new Photo(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)));
		} else {
			photo = new Photo(BitmapFactory.decodeResource(
					(new Activity()).getResources(), R.drawable.ic_face));
		}
		
		// Get Phones
		List<Phone> phoneList = new ArrayList<Phone>();
		String[] phoneTypes = cursor.getString(cursor.getColumnIndex(KEY_PHONE_TYPE)).split(";");
		String[] phoneNumber = cursor.getString(cursor.getColumnIndex(KEY_PHONE_NUMBER)).split(";");
		String[] phonePrimary = cursor.getString(cursor.getColumnIndex(KEY_PHONE_PRIMARY)).split(";");
		if (!phoneTypes[0].isEmpty()){
			for (int i = 0; i < phoneTypes.length; i++) {
				try {
					phoneList.add(new Phone(phoneTypes[i], phoneNumber[i], (phonePrimary[i].equals("1")) ? true : false));
				} catch (InvalidPhoneException phEx) {
					continue;
				}
			}
		}
		
		// Get email
		List<Email> emailList = new ArrayList<Email>();
		String[] emailType = cursor.getString(cursor.getColumnIndex(KEY_EMAIL_TYPE)).split(";");
		String[] emailValue = cursor.getString(cursor.getColumnIndex(KEY_EMAIL_VALUE)).split(";");
		if (!emailType[0].isEmpty()){
			for (int i = 0; i < emailType.length; i++) {
				try {
					emailList.add(new Email(emailType[i], emailValue[i]));
				} catch (InvalidEmailException emEx) {
					continue;
				}
			}
		}
		
		// Get addresses
		List<Address> addressList = new ArrayList<Address>();
		String[] addressType = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS_TYPE)).split(";");
		String[] addressValue = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS_VALUE)).split(";");
		if (!addressType[0].isEmpty()){
			for (int i = 0; i < addressType.length; i++) {
				addressList.add(new Address(addressType[i], addressValue[i]));
			}
		}

		DateOfBirth dob = new DateOfBirth(cursor.getString(cursor
				.getColumnIndex(KEY_DOB)));

		Contact c = new Contact(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
				name, photo, phoneList, emailList, addressList, dob);

		return c;
	}

	/**
	 * Get all contacts from the database and store all created Contact in a list
	 * @return The list that contains all Contact objects
	 */
	public List<Contact> getAllContacts() {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;
		List<Contact> cl = new ArrayList<Contact>();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Contact c = getContact(cursor.getInt(cursor.getColumnIndex(KEY_ID)), db);
				cl.add(c);
			} while (cursor.moveToNext());
		}
		return cl;
	}

	// -=-=-=-=-=-= Update =-=-=-=-=-=-=- //
	/**
	 * Update an existing contact
	 * @param c A contact object containing updated data
	 * @return The ID of the updated contact
	 */
	public int updateContact(Contact c) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FIRST_NAME, c.getName().getFirstName());
		values.put(KEY_MIDDLE_NAME, c.getName().getMiddleName());
		values.put(KEY_LAST_NAME, c.getName().getLastName());
		values.put(KEY_NAME_SUFFIX, c.getName().getSuffix());
		values.put(KEY_DOB, c.getDateOfBirth().getValue());
		values.put(KEY_IMAGE, c.getPhoto().getByteArray());

		// Phone
		String[] phoneData = createPhoneData(c.getPhones());
		values.put(KEY_PHONE_TYPE, phoneData[0]);
		values.put(KEY_PHONE_NUMBER, phoneData[1]);
		values.put(KEY_PHONE_PRIMARY, phoneData[2]);
		
		// Email
		String[] emailData = createEmailData(c.getEmails());
		values.put(KEY_EMAIL_TYPE, emailData[0]);
		values.put(KEY_EMAIL_VALUE, emailData[1]);
		
		// Address
		String[] addressData = createAddressData(c.getAddresses());
		values.put(KEY_ADDRESS_TYPE, addressData[0]);
		values.put(KEY_ADDRESS_VALUE, addressData[1]);
		
		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(c.getID()) });
	}
	
	// -=-=-=-=-=-= Delete =-=-=-=-=-=-=- //
	/**
	 * Delete a contact from the database
	 * @param contactId The ID of the unwanted contact
	 */
	public void deleteContact(long contactId) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
	            new String[] { String.valueOf(contactId) });
	}
	
	// ===================================================

	/**
	 * A Method that closes the database if it is not closed already.
	 */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    
    /**
     * Get the number of contacts currently stored in the database
     * @return the number of contacts in the database
     */
    public int getContactCount() {
    	SQLiteDatabase db = this.getReadableDatabase();
    	String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	return cursor.getCount();
    }
	
}
