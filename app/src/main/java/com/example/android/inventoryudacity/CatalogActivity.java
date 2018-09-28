package com.example.android.inventoryudacity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.example.android.inventoryudacity.data.PcDbHelper;
import com.example.android.inventoryudacity.data.ProductContract;

public class CatalogActivity extends AppCompatActivity {
    private PcDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_catalog );

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById ( R.id.fab );
        fab.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent ( CatalogActivity.this, EditorActivity.class );
                startActivity ( intent );
            }
        } );

        mDbHelper = new PcDbHelper ( this );
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase ();
// Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_PHONE_NUMBER};

        // Perform a query on the pets table
        Cursor cursor = db.query (
                ProductContract.ProductEntry.TABLE_NAME,   // The table to query
                projection,            // The columns to return
                null,                  // The columns for the WHERE clause
                null,                  // The values for the WHERE clause
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null );                   // The sort order

        TextView displayView = (TextView) findViewById ( R.id.text_view_product );

        try {
            // Create a header in the Text View that looks like this:
            //
            // The product table contains <number of rows in Cursor> products.
            // _id - product name - quantity - price - supplier - phone number
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText ( "The products table contains " + cursor.getCount () + " products.\n\n" );
            displayView.append ( ProductContract.ProductEntry._ID + " - " +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_NAME + " - " +
                    ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY + " - " +
                    ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
                    ProductContract.ProductEntry.COLUMN_PHONE_NUMBER + " - " +
                    ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE + "- " );

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry._ID );
            int nameColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME );
            int quantityColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY );
            int priceColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE );
            int supplierColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME );
            int phoneColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PHONE_NUMBER );

            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext ()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt ( idColumnIndex );
                String currentName = cursor.getString ( nameColumnIndex );
                String currentQuantity = cursor.getString ( quantityColumnIndex );
                int currentPrice = cursor.getInt ( priceColumnIndex );
                int currentSupplier = cursor.getInt ( supplierColumnIndex );
                int currentPhone = cursor.getInt ( phoneColumnIndex );

                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append ( ("\n" + currentID + " - " +
                        currentName + " - " +
                        currentQuantity + " - " +
                        currentPrice + " - " +
                        currentSupplier + " - " +
                        currentPhone) );
            }
        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close ();
        }
    }

    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */

    private void insertProduct() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase ();
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues ();
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, "Earrings" );
        values.put ( ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY, "5" );
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, "$10" );
        values.put ( ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, "Millanus" );
        values.put ( ProductContract.ProductEntry.COLUMN_PHONE_NUMBER, "(305)545-9778" );
        // The third argument is the ContentValues object containing the info for Toto.
        long newRowId = db.insert ( ProductContract.ProductEntry.TABLE_NAME, null, values );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater ().inflate ( R.menu.menu_catalog, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId ()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct ();
                displayDatabaseInfo ();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:

                return true;
        }
        return super.onOptionsItemSelected ( item );
    }
}