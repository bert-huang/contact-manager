package cepw.contactmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "contactsManager";
 
    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_PHONE = "phones";
    private static final String TABLE_EMAIL = "emails";
    private static final String TABLE_ADDRESS = "addresses";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_MIDDLE_NAME = "middle_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_NAME_SUFFIX = "name_suffix";
    private static final String KEY_PH_ID = "phone_id";
    private static final String KEY_EM_ID = "email_id";
    private static final String KEY_AD_ID = "address_id";
    private static final String KEY_DOB = "date_of_birth";
    private static final String KEY_IMAGE = "image_(byte_array)";
    
    // Phone Table Columns names
    private static final String KEY_PHONE_ID = "id";
    private static final String KEY_PHONE_TYPE = "type";
    private static final String KEY_NUMBER = "number";
    
    // Email Table Columns names
    private static final String KEY_EMAIL_ID = "id";
    private static final String KEY_EMAIL_TYPE = "type";
    private static final String KEY_EMAIL = "email_address";
    
    // Address Table Columns names
    private static final String KEY_ADDRESS_ID = "id";
    private static final String KEY_ADDRESS_TYPE = "type";
    private static final String KEY_ADDRESS = "physical_address";
    
    
 // Table Create Statements
    // Contacts table create statement
    private static final String CREATE_TABLE_CONTACTS = "CREATE TABLE "
            + TABLE_CONTACTS + "(" + 
    		KEY_ID + " INTEGER PRIMARY KEY," + 
    		KEY_FIRST_NAME + " TEXT," + 
            KEY_MIDDLE_NAME + " TEXT," + 
    		KEY_LAST_NAME + " TEXT," + 
            KEY_NAME_SUFFIX + " TEXT," +
            KEY_PH_ID + " INTEGER," + 
            KEY_EM_ID + " INTEGER," + 
            KEY_AD_ID + " INTEGER," + 
            KEY_DOB + " TEXT," + ")";
    
    // Phones table create statement
    private static final String CREATE_TABLE_PHONE = "CREATE TABLE "
            + TABLE_CONTACTS + "(" + 
            KEY_PHONE_ID + " INTEGER PRIMARY KEY," + 
            KEY_PHONE_TYPE + " TEXT," + 
            KEY_NUMBER + " TEXT," + ")";
    
    // Email table create statement
    private static final String CREATE_TABLE_EMAIL = "CREATE TABLE "
            + TABLE_CONTACTS + "(" + 
            KEY_EMAIL_ID + " INTEGER PRIMARY KEY," + 
            KEY_EMAIL_TYPE + " TEXT," + 
            KEY_EMAIL + " TEXT," + ")";
    
    // Address table create statement
    private static final String CREATE_TABLE_ADDRESS = "CREATE TABLE "
            + TABLE_CONTACTS + "(" + 
            KEY_ADDRESS_ID + " INTEGER PRIMARY KEY," + 
            KEY_ADDRESS_TYPE + " TEXT," + 
            KEY_ADDRESS + " TEXT," + ")";

    
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

}
