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
	
	public long createContact(Name name, Photo photo, List<Phone> phones, List<Email> emails,
			List<Address> addresses, DateOfBirth dob) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_FIRST_NAME, name.getFirstName());
		values.put(KEY_MIDDLE_NAME, name.getMiddleName());
		values.put(KEY_LAST_NAME, name.getLastName());
		values.put(KEY_NAME_SUFFIX, name.getSuffix());
		values.put(KEY_DOB, dob.getValue());
		values.put(KEY_IMAGE, photo.getByteArray());

		// Phone
		String[] phoneData = createPhoneData(phones);
		values.put(KEY_PHONE_TYPE, phoneData[0]);
		values.put(KEY_PHONE_NUMBER, phoneData[1]);
		values.put(KEY_PHONE_PRIMARY, phoneData[2]);
		
		// Email
		String[] emailData = createEmailData(emails);
		values.put(KEY_EMAIL_TYPE, emailData[0]);
		values.put(KEY_EMAIL_VALUE, emailData[1]);
		
		// Address
		String[] addressData = createAddressData(addresses);
		values.put(KEY_ADDRESS_TYPE, addressData[0]);
		values.put(KEY_ADDRESS_VALUE, addressData[1]);
		
		// insert row
		long contactId = db.insert(TABLE_CONTACTS, null, values);


		return contactId;
	}

	public String[] createPhoneData(List<Phone> phoneList) {
		//Phone
		StringBuffer phoneType = new StringBuffer();
		StringBuffer phoneNumber = new StringBuffer();
		StringBuffer phonePrimary = new StringBuffer();
		for (Phone p : phoneList) {
			phoneType	.append(p.getType() + ";");
			phoneNumber	.append(p.getNumber() + ";");
			phonePrimary.append((p.isDefault()? "1" : "0") + ";");
		}
		if(phoneType.length() != 0) {phoneType.deleteCharAt(phoneType.length()-1);}
		if(phoneNumber.length() != 0) {phoneNumber.deleteCharAt(phoneNumber.length()-1);}
		if(phonePrimary.length() != 0) {phonePrimary.deleteCharAt(phonePrimary.length()-1);}
		
		return new String[] {phoneType.toString(), phoneNumber.toString(), phonePrimary.toString()};
	}
	
	public String[] createEmailData(List<Email> emailList) {
		StringBuffer emailType = new StringBuffer();
		StringBuffer emailValue = new StringBuffer();
		for (Email e : emailList) {
			emailType.append(e.getType() + ";");
			emailValue.append(e.getEmail() + ";");
		}
		if(emailType.length() != 0) {emailType.deleteCharAt(emailType.length()-1);}
		if(emailValue.length() != 0) {emailValue.deleteCharAt(emailValue.length()-1);}
		
		return new String[] {emailType.toString(), emailValue.toString()};
	}
	
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
	public void deleteContact(long contactId) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
	            new String[] { String.valueOf(contactId) });
	}
	
	// ===================================================

	// closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
    
    //==================================================
    public int getContactCount() {
    	SQLiteDatabase db = this.getReadableDatabase();
    	String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;
    	Cursor cursor = db.rawQuery(selectQuery, null);
    	return cursor.getCount();
    }
	
}
