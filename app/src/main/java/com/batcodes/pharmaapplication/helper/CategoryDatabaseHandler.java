package com.batcodes.pharmaapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.batcodes.pharmaapplication.model.Address;
import com.batcodes.pharmaapplication.model.Category;
import com.batcodes.pharmaapplication.model.User;

import java.util.ArrayList;
import java.util.List;

public class CategoryDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CategoryManager";
    private static final String TABLE_CATEGORY = "category";
    private static final String TABLE_USER = "user";
    private static final String TABLE_ADDRESS = "address";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_ICON = "icon";

    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL_ADDRESS = "user_email_address";
    private static final String KEY_USER_MOBILE_NUMBER = "user_mobile_number";
    private static final String KEY_USER_PASSWORD = "user_password";

    private static final String KEY_ADDRESS_ID = "address_id";
    private static final String KEY_ADDRESS_NAME = "address_name";
    private static final String KEY_ADDRESS = "user_address";

    public CategoryDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_ICON + " TEXT" + ")";

        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_EMAIL_ADDRESS + " TEXT,"
                + KEY_USER_MOBILE_NUMBER + " TEXT,"
                + KEY_USER_PASSWORD + " TEXT"+ ")";

        String CREATE_ADDRESS_TABLE = "CREATE TABLE " + TABLE_ADDRESS + "("
                + KEY_ADDRESS_ID + " INTEGER PRIMARY KEY,"
                + KEY_ADDRESS_NAME + " TEXT,"
                + KEY_ADDRESS + " TEXT"+ ")";

        sqLiteDatabase.execSQL(CREATE_CATEGORY_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_ADDRESS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDRESS);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, category.getName());
        values.put(KEY_ICON, category.getIcon());
        db.insert(TABLE_CATEGORY, null, values);
        db.close(); // Closing database connection
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_EMAIL_ADDRESS, user.getEmailAddress());
        values.put(KEY_USER_MOBILE_NUMBER, user.getMobileNumber());
        values.put(KEY_USER_PASSWORD, user.getPassword());
        db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection
    }

    public boolean isValidUser(String emailAddress, String password)
    {
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(TABLE_USER, new String[] {
                            KEY_USER_EMAIL_ADDRESS, KEY_USER_PASSWORD }, KEY_USER_EMAIL_ADDRESS + "=?",
                    new String[] { String.valueOf(emailAddress) }, null, null, null, null);
            if(cursor!=null)
                cursor.moveToFirst();
            if (cursor != null && !cursor.isNull(0)){
                String emailAddressSQL =  cursor.getString(0);
                String passwordSQL = cursor.getString(1);
                if(emailAddressSQL.equalsIgnoreCase(emailAddress) && passwordSQL.equalsIgnoreCase(password))
                    return true;
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<Category>();

        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Category contact = new Category();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setIcon(cursor.getString(2));
                categoryList.add(contact);
            } while (cursor.moveToNext());
        }
        return categoryList;
    }

    Category getCategory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORY, new String[] { KEY_ID,
                        KEY_NAME, KEY_ICON }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Category contact = new Category(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2));
        return contact;
    }

    public void addAddress(Address address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS_NAME, address.getName());
        values.put(KEY_ADDRESS, address.getAddress());
        db.insert(TABLE_ADDRESS, null, values);
        db.close();
    }

    public List<Address> getAllAddresses() {
        List<Address> addressList = new ArrayList<Address>();
        try {
            String selectQuery = "SELECT  * FROM " + TABLE_ADDRESS;
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Address address = new Address();
                    address.setId(Integer.parseInt(cursor.getString(0)));
                    address.setName(cursor.getString(1));
                    address.setAddress(cursor.getString(2));
                    addressList.add(address);
                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return addressList;
    }
}
