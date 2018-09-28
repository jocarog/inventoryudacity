package com.example.android.inventoryudacity.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PcDbHelper extends SQLiteOpenHelper {


    public static final String LOG_TAG = PcDbHelper.class.getSimpleName ();

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "product.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link PcDbHelper}.
     *
     * @param context of the app
     */
    public PcDbHelper(Context context) {
        super ( context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_PRODUCT_TABLE = "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " ("
                + ProductContract.ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY + " INTEGER, "
                + ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + ProductContract.ProductEntry.COLUMN_PHONE_NUMBER + " INTEGER NOT NULL );";

        // Execute the SQL statement
        db.execSQL ( SQL_CREATE_PRODUCT_TABLE );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}