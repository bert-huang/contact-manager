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

	// Common Table Columns names
	private static final String KEY_ID = "id";

	// Contacts Table Columns names
	private static final String KEY_FIRST_NAME = "first_name";
	private static final String KEY_MIDDLE_NAME = "middle_name";
	private static final String KEY_LAST_NAME = "last_name";
	private static final String KEY_NAME_SUFFIX = "name_suffix";
	private static final String KEY_PHONE = "phones";
	private static final String KEY_EMAIL = "emails";
	private static final String KEY_ADDRESS = "address";
	private static final String KEY_DOB = "date_of_birth";
	private static final String KEY_IMAGE = "image";

	// Email Table Columns names

	// Address Table Columns names

	// Table Create Statements
	// Contacts table create statement
	private static final String CREATE_TABLE_CONTACTS = 
			"CREATE TABLE " + TABLE_CONTACTS + "(" + 
			KEY_ID 				+ " INTEGER PRIMARY KEY," + 
			KEY_FIRST_NAME 		+ " TEXT," + 
			KEY_MIDDLE_NAME 	+ " TEXT," + 
			KEY_LAST_NAME 		+ " TEXT," + 
			KEY_NAME_SUFFIX 	+ " TEXT," + 
			KEY_PHONE			+ " TEXT," +
			KEY_EMAIL			+ " TEXT," +
			KEY_ADDRESS			+ " TEXT," +
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

		// add associated phones, emails and addresses
		StringBuffer phone = new StringBuffer();
		for (Phone p : phones) {
			phone.append(p.getType() + "," + p.getNumber() + "," + (p.isDefault()? "1" : "0") + ";");
		}
		if(phone.length() != 0) {phone.deleteCharAt(phone.length()-1);}
		values.put(KEY_PHONE, phone.toString());
		
		StringBuffer email = new StringBuffer();
		for (Email e : emails) {
			email.append(e.getType() + "," + e.getEmail() + ";");
		}
		if(email.length() != 0) {email.deleteCharAt(email.length()-1);}
		values.put(KEY_EMAIL, email.toString());
		
		StringBuffer address = new StringBuffer();
		for (Address a : addresses) {
			address.append(a.getType() + "," + a.getAddress() + ";");
		}
		if(address.length() != 0) {address.deleteCharAt(address.length()-1);}
		values.put(KEY_ADDRESS, address.toString());
		
		// insert row
		long contactId = db.insert(TABLE_CONTACTS, null, values);


		return contactId;
	}
	
	public long createContact(Contact c) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FIRST_NAME, c.getName().getFirstName());
		values.put(KEY_MIDDLE_NAME, c.getName().getMiddleName());
		values.put(KEY_LAST_NAME, c.getName().getLastName());
		values.put(KEY_NAME_SUFFIX, c.getName().getSuffix());
		values.put(KEY_DOB, c.getDateOfBirth().getValue());
		values.put(KEY_IMAGE, c.getPhoto().getByteArray());

		// add associated phones, emails and addresses
		StringBuffer phone = new StringBuffer();
		for (Phone p : c.getPhones()) {
			phone.append(p.getType() + "," + p.getNumber() + "," + (p.isDefault() ? "1" : "0") + ";");
		}
		if(phone.length() != 0) {phone.deleteCharAt(phone.length()-1);}
		values.put(KEY_PHONE, phone.toString());
		
		StringBuffer email = new StringBuffer();
		for (Email e : c.getEmails()) {
			email.append(e.getType() + "," + e.getEmail() + ";");
		}
		if(email.length() != 0) {email.deleteCharAt(email.length()-1);}
		values.put(KEY_EMAIL, email.toString());
		
		StringBuffer address = new StringBuffer();
		for (Address a : c.getAddresses()) {
			address.append(a.getType() + "," + a.getAddress() + ";");
		}
		if(address.length() != 0) {address.deleteCharAt(address.length()-1);}
		values.put(KEY_ADDRESS, address.toString());
		
		// insert row
		long contactId = db.insert(TABLE_CONTACTS, null, values);

		return contactId;
	}

	// -=-=-=-=-=-= Retrieve =-=-=-=-=-=-=- //

	public Contact getContact(long contactId) {
		SQLiteDatabase db = this.getReadableDatabase();

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
		
		List<Phone> phoneList = new ArrayList<Phone>();
		String[] phones = cursor.getString(cursor.getColumnIndex(KEY_PHONE)).split(";");
		
		if (!phones[0].isEmpty()){
			for (String p : phones) {
				String[] indiPh = p.split(",");
				
				try {
					phoneList.add(new Phone(indiPh[0], indiPh[1], (indiPh[2].equals("1")) ? true : false));
				} catch (InvalidPhoneException phEx) {
					continue;
				}
			}
		}
		
		List<Email> emailList = new ArrayList<Email>();
		String[] emails = cursor.getString(cursor.getColumnIndex(KEY_EMAIL)).split(";");
		if (!emails[0].isEmpty()){
			for (String e : emails) {
				String[] indiEm = e.split(",");
				try {
					emailList.add(new Email(indiEm[0], indiEm[1]));
				} catch (InvalidEmailException emEx) {
					continue;
				}
			}
		}
		
		List<Address> addressList = new ArrayList<Address>();
		String[] addresses = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)).split(";");
		if (!addresses[0].isEmpty()){
			for (String a : addresses) {
				String[] indiAd = a.split(",");
				addressList.add(new Address(indiAd[0], indiAd[1]));
	
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
				Name name = new Name(
						cursor.getString(cursor.getColumnIndex(KEY_FIRST_NAME)),
						cursor.getString(cursor.getColumnIndex(KEY_MIDDLE_NAME)),
						cursor.getString(cursor.getColumnIndex(KEY_LAST_NAME)),
						cursor.getString(cursor.getColumnIndex(KEY_NAME_SUFFIX)));

				Photo photo;
				if (cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)) != null) {
					photo = new Photo(cursor.getBlob(cursor
							.getColumnIndex(KEY_IMAGE)));
				} else {
					photo = new Photo(
							BitmapFactory.decodeResource(
									(new Activity()).getResources(),
									R.drawable.ic_face));
				}
				
				List<Phone> phoneList = new ArrayList<Phone>();
				String[] phones = cursor.getString(cursor.getColumnIndex(KEY_PHONE)).split(";");
				if (!phones[0].isEmpty()){
					for (String p : phones) {
						String[] indiPh = p.split(",");
						try {
							phoneList.add(new Phone(indiPh[0], indiPh[1], (indiPh[2].equals("1"))? true : false));
						} catch (InvalidPhoneException phEx) {
							continue;
						}
					}
				}
				
				List<Email> emailList = new ArrayList<Email>();
				String[] emails = cursor.getString(cursor.getColumnIndex(KEY_EMAIL)).split(";");
				if (!emails[0].isEmpty()){
					for (String e : emails) {
						String[] indiEm = e.split(",");
						try {
							emailList.add(new Email(indiEm[0], indiEm[1]));
						} catch (InvalidEmailException emEx) {
							continue;
						}
					}
				}
				
				List<Address> addressList = new ArrayList<Address>();
				String[] addresses = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)).split(";");
				if (!addresses[0].isEmpty()){
					for (String a : addresses) {
						String[] indiAd = a.split(",");
						addressList.add(new Address(indiAd[0], indiAd[1]));
			
					}
				}

				DateOfBirth dob = new DateOfBirth(cursor.getString(cursor
						.getColumnIndex(KEY_DOB)));

				Contact c = new Contact(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
						name, photo, phoneList, emailList, addressList, dob);
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

		StringBuffer phone = new StringBuffer();
		for (Phone p : c.getPhones()) {
			phone.append(p.getType() + "," + p.getNumber() + "," + (p.isDefault()? 1 : 0) + ";");
		}
		if(phone.length() != 0) {phone.deleteCharAt(phone.length()-1);}
		values.put(KEY_PHONE, phone.toString());
		
		StringBuffer email = new StringBuffer();
		for (Email e : c.getEmails()) {
			email.append(e.getType() + "," + e.getEmail() + ";");
		}
		if(email.length() != 0) {email.deleteCharAt(email.length()-1);}
		values.put(KEY_EMAIL, email.toString());
		
		StringBuffer address = new StringBuffer();
		for (Address a : c.getAddresses()) {
			address.append(a.getType() + "," + a.getAddress() + ";");
		}
		if(address.length() != 0) {address.deleteCharAt(address.length()-1);}
		values.put(KEY_ADDRESS, address.toString());
		
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
