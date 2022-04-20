package com.batcodes.pharmaapplication.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.batcodes.pharmaapplication.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductDatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ProductManager";
    private static final String TABLE_CATEGORY = "product";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_QTY = "qty";
    private static final String KEY_CATEGORY_ID = "category_id";
    private static final String KEY_DETAILS = "details";
    private static final String KEY_PRICE = "price";
    private static final String KEY_IMAGE = "image";

    public ProductDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_QTY + " TEXT,"+ KEY_CATEGORY_ID + " INTEGER,"
                + KEY_DETAILS + " TEXT,"+ KEY_PRICE + " TEXT,"+ KEY_IMAGE + " TEXT"+ ")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Drop older table if existed
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        // Create tables again
        onCreate(sqLiteDatabase);
    }

    public synchronized void addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, product.getName());
        values.put(KEY_QTY, product.getQty());
        //values.put(KEY_CATEGORY_ID, product.getCategoryId());
        values.put(KEY_DETAILS, product.getDetails());
        values.put(KEY_PRICE, product.getPrice());
        values.put(KEY_IMAGE, product.getImage());

        // Inserting Row
        db.insert(TABLE_CATEGORY, null, values);
        db.close(); // Closing database connection
    }

    public void addProductS(ArrayList<Product> productArrayList) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i = 0; i < productArrayList.size(); i++) {
            Product product = productArrayList.get(i);
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, product.getName());
            values.put(KEY_QTY, product.getQty());
            //values.put(KEY_CATEGORY_ID, product.getCategoryId());
            values.put(KEY_DETAILS, product.getDetails());
            values.put(KEY_PRICE, product.getPrice());
            values.put(KEY_IMAGE, product.getImage());

            db.insert(TABLE_CATEGORY, null, values);
        }
        db.close();
    }

    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<Product>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CATEGORY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                //product.setId(Integer.parseInt(cursor.getString(0)));
                product.setName(cursor.getString(1));
                product.setQty(cursor.getString(2));
                //product.setCategoryId(Integer.parseInt(cursor.getString(3)));
                product.setDetails(cursor.getString(4));
                product.setPrice(cursor.getString(5));
                product.setImage(cursor.getString(6));
                productList.add(product);
            } while (cursor.moveToNext());
        }

        // return contact list
        return productList;
    }

    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORY, new String[] { KEY_ID,
                        KEY_NAME, KEY_QTY, KEY_CATEGORY_ID, KEY_DETAILS, KEY_PRICE, KEY_IMAGE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Product product = null;
        /*Product product = new Product(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getInt(3),cursor.getString(4),cursor.getString(5),cursor.getString(6));*/
        return product;
    }

    public List<Product> getProductsForCategory(int categoryId) {
        List<Product> productList = new ArrayList<Product>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CATEGORY, new String[] { KEY_ID,
                        KEY_NAME, KEY_QTY, KEY_CATEGORY_ID, KEY_DETAILS, KEY_PRICE, KEY_IMAGE }, KEY_CATEGORY_ID + "=?",
                new String[] { String.valueOf(categoryId) }, null, null, null, null);

        if (cursor!=null && cursor.moveToFirst()) {
            do {
                Product product = new Product();
                //product.setId(Integer.parseInt(cursor.getString(0)));
                product.setName(cursor.getString(1));
                product.setQty(cursor.getString(2));
                //product.setCategoryId(Integer.parseInt(cursor.getString(3)));
                product.setDetails(cursor.getString(4));
                product.setPrice(cursor.getString(5));
                product.setImage(cursor.getString(6));
                // Adding contact to list
                productList.add(product);
            } while (cursor.moveToNext());
        }
        return productList;
    }
}
