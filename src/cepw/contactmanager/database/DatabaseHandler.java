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
	private static final String TABLE_PHONE = "phones";
	private static final String TABLE_EMAIL = "emails";
	private static final String TABLE_ADDRESS = "addresses";

	// Common Table Columns names
	private static final String KEY_ID = "id";
	private static final String KEY_CONTACT_ID = "contact";
	private static final String KEY_TYPE = "type";

	// Contacts Table Columns names
	private static final String KEY_FIRST_NAME = "first_name";
	private static final String KEY_MIDDLE_NAME = "middle_name";
	private static final String KEY_LAST_NAME = "last_name";
	private static final String KEY_NAME_SUFFIX = "name_suffix";
	private static final String KEY_DOB = "date_of_birth";
	private static final String KEY_IMAGE = "image_(byte_array)";

	// Phone Table Columns names
	private static final String KEY_NUMBER = "number";
	private static final String KEY_DEFAULT = "is_default";

	// Email Table Columns names
	private static final String KEY_EMAIL = "email_address";

	// Address Table Columns names
	private static final String KEY_ADDRESS = "physical_address";

	// Table Create Statements
	// Contacts table create statement
	private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE "
			+ TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_FIRST_NAME + " TEXT," + KEY_MIDDLE_NAME + " TEXT,"
			+ KEY_LAST_NAME + " TEXT," + KEY_NAME_SUFFIX + " TEXT," + KEY_DOB
			+ " TEXT," + KEY_IMAGE + " BLOB" + ")";

	// Phones table create statement
	private static final String CREATE_TABLE_PHONE = "CREATE TABLE "
			+ TABLE_PHONE + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_CONTACT_ID + " INTEGER," + KEY_TYPE + " TEXT," + KEY_NUMBER
			+ " TEXT," + KEY_DEFAULT + " INTEGER" + ")";

	// Email table create statement
	private static final String CREATE_TABLE_EMAIL = "CREATE TABLE "
			+ TABLE_EMAIL + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_CONTACT_ID + " INTEGER," + KEY_TYPE + " TEXT," + KEY_EMAIL
			+ " TEXT" + ")";

	// Address table create statement
	private static final String CREATE_TABLE_ADDRESS = "CREATE TABLE "
			+ TABLE_ADDRESS + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
			+ KEY_CONTACT_ID + " INTEGER," + KEY_TYPE + " TEXT," + KEY_ADDRESS
			+ " TEXT" + ")";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_CONTACTS);
		db.execSQL(CREATE_TABLE_PHONE);
		db.execSQL(CREATE_TABLE_EMAIL);
		db.execSQL(CREATE_TABLE_ADDRESS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_CONTACTS);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_PHONE);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_EMAIL);
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_ADDRESS);

		// create new tables
		onCreate(db);

	}

	// ========== Helper method ================

	// -=-=-=-=-=-= CREATE =-=-=-=-=-=-=- //

	public long createContact(Contact c) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_FIRST_NAME, c.getName().getFirstName());
		values.put(KEY_MIDDLE_NAME, c.getName().getMiddleName());
		values.put(KEY_LAST_NAME, c.getName().getLastName());
		values.put(KEY_NAME_SUFFIX, c.getName().getSuffix());
		values.put(KEY_DOB, c.getDateOfBirth().getValue());
		values.put(KEY_IMAGE, c.getPhoto().getByteArray());

		// insert row
		long contactId = db.insert(TABLE_CONTACTS, null, values);

		// add associated phones, emails and addresses
		for (Phone p : c.getPhones()) {
			createPhone(p, contactId);
		}

		for (Email e : c.getEmails()) {
			createEmail(e, contactId);
		}

		for (Address a : c.getAddresses()) {
			createAddress(a, contactId);
		}

		return contactId;
	}

	public long createPhone(Phone p, long contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CONTACT_ID, contactId);
		values.put(KEY_TYPE, p.getType());
		values.put(KEY_NUMBER, p.getNumber());
		values.put(KEY_DEFAULT, (p.isDefault() ? 1 : 0));

		return db.insert(TABLE_PHONE, null, values);
	}

	public long createEmail(Email e, long contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CONTACT_ID, contactId);
		values.put(KEY_TYPE, e.getType());
		values.put(KEY_EMAIL, e.getEmail());

		return db.insert(TABLE_EMAIL, null, values);
	}

	public long createAddress(Address a, long contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CONTACT_ID, contactId);
		values.put(KEY_TYPE, a.getType());
		values.put(KEY_ADDRESS, a.getAddress());

		return db.insert(TABLE_ADDRESS, null, values);
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

		Name name = new Name(cursor.getString(cursor
				.getColumnIndex(KEY_FIRST_NAME)), cursor.getString(cursor
				.getColumnIndex(KEY_MIDDLE_NAME)), cursor.getString(cursor
				.getColumnIndex(KEY_LAST_NAME)), cursor.getString(cursor
				.getColumnIndex(KEY_NAME_SUFFIX)));

		Photo photo;
		if (cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)) != null) {
			photo = new Photo(cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE)));
		} else {
			photo = new Photo(BitmapFactory.decodeResource(
					(new Activity()).getResources(), R.drawable.ic_face));
		}

		DateOfBirth dob = new DateOfBirth(cursor.getString(cursor
				.getColumnIndex(KEY_DOB)));

		Contact c = new Contact(cursor.getInt(cursor.getColumnIndex(KEY_ID)),
				name, photo, getAllPhones(contactId), getAllEmails(contactId),
				getAllAddresses(contactId), dob);

		return c;
	}

	public List<Contact> getAllContacts() {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;
		List<Contact> cl = new ArrayList<Contact>();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				int contactId = cursor.getInt(cursor.getColumnIndex("KEY_ID"));

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

				DateOfBirth dob = new DateOfBirth(cursor.getString(cursor
						.getColumnIndex(KEY_DOB)));

				Contact c = new Contact(cursor.getInt(cursor
						.getColumnIndex(KEY_ID)), name, photo,
						getAllPhones(contactId), getAllEmails(contactId),
						getAllAddresses(contactId), dob);
				cl.add(c);
			} while (cursor.moveToNext());
		}
		return cl;
	}

	public List<Phone> getAllPhones(long contactId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_PHONE + " WHERE "
				+ KEY_CONTACT_ID + " = " + contactId;
		List<Phone> pl = new ArrayList<Phone>();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				try {
					Phone p = new Phone(
							cursor.getInt(cursor.getColumnIndex(KEY_ID)),
							cursor.getInt(cursor.getColumnIndex(KEY_CONTACT_ID)),
							cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
							cursor.getString(cursor.getColumnIndex(KEY_NUMBER)),
							((cursor.getInt(cursor.getColumnIndex(KEY_DEFAULT)) == 1 ? true
									: false)));

					pl.add(p);
				} catch (InvalidPhoneException e) {
					Log.d(LOG,
							"InvalidPhoneException caught. This number will be ignored");
					continue;
				}
			} while (cursor.moveToNext());
		}
		return pl;
	}

	public List<Email> getAllEmails(long contactId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_EMAIL + " WHERE "
				+ KEY_CONTACT_ID + " = " + contactId;
		List<Email> el = new ArrayList<Email>();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				try {
					Email e = new Email(cursor.getInt(cursor
							.getColumnIndex(KEY_ID)), cursor.getInt(cursor
							.getColumnIndex(KEY_CONTACT_ID)),
							cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
							cursor.getString(cursor.getColumnIndex(KEY_EMAIL)));
					el.add(e);
				} catch (InvalidEmailException e) {
					Log.d(LOG,
							"InvalidEmailException caught. This email will be ignored");
					continue;
				}
			} while (cursor.moveToNext());
		}
		return el;
	}

	public List<Address> getAllAddresses(long contactId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = "SELECT  * FROM " + TABLE_ADDRESS + " WHERE "
				+ KEY_CONTACT_ID + " = " + contactId;
		List<Address> al = new ArrayList<Address>();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				Address a = new Address(cursor.getInt(cursor
						.getColumnIndex(KEY_ID)), cursor.getInt(cursor
						.getColumnIndex(KEY_CONTACT_ID)),
						cursor.getString(cursor.getColumnIndex(KEY_TYPE)),
						cursor.getString(cursor.getColumnIndex(KEY_ADDRESS)));
				al.add(a);
			} while (cursor.moveToNext());
		}
		return al;
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

		for (Phone p : c.getPhones()) {
			if (p.getID() != -2) {
				updatePhone(p, c.getID());
			}else {
				createPhone(p, c.getID());
			}
		}
		
		for (Email e : c.getEmails()) {
			if (e.getID() != -2) {
				updateEmail(e, c.getID());
			}else {
				createEmail(e, c.getID());
			}
		}
		
		for (Address a : c.getAddresses()) {
			if (a.getID() != -2) {
				updateAddress(a, c.getID());
			}else {
				createAddress(a, c.getID());
			}
		}
		
		// updating row
		return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
				new String[] { String.valueOf(c.getID()) });
	}
	
	public int updatePhone(Phone p, int contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CONTACT_ID, contactId);
		values.put(KEY_TYPE, p.getType());
		values.put(KEY_NUMBER, p.getNumber());
		values.put(KEY_DEFAULT, (p.isDefault() ? 1 : 0));
		
		return db.update(TABLE_PHONE, values, KEY_ID + " = ?",
				new String[] { String.valueOf(p.getID()) });
	}
	
	public int updateEmail(Email e, int contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CONTACT_ID, contactId);
		values.put(KEY_TYPE, e.getType());
		values.put(KEY_EMAIL, e.getEmail());
		
		return db.update(TABLE_EMAIL, values, KEY_ID + " = ?",
				new String[] { String.valueOf(e.getID()) });
	}
	
	public int updateAddress(Address a, int contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_CONTACT_ID, contactId);
		values.put(KEY_TYPE, a.getType());
		values.put(KEY_EMAIL, a.getAddress());
		
		return db.update(TABLE_EMAIL, values, KEY_ID + " = ?",
				new String[] { String.valueOf(a.getID()) });
	}
	
	// -=-=-=-=-=-= Delete =-=-=-=-=-=-=- //
	public void deleteContact(long contactId) {
	    SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
	            new String[] { String.valueOf(contactId) });
	}
	
	public void deletePhones(long contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_PHONE, KEY_CONTACT_ID + " = ?",
	            new String[] { String.valueOf(contactId) });
	}
	
	public void deleteEmails(long contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_EMAIL, KEY_CONTACT_ID + " = ?",
	            new String[] { String.valueOf(contactId) });
	}
	
	public void deleteAddresses(long contactId) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_ADDRESS, KEY_CONTACT_ID + " = ?",
	            new String[] { String.valueOf(contactId) });
	}
	
}
