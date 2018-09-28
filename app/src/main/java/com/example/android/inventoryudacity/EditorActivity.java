package com.example.android.inventoryudacity;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.inventoryudacity.data.PcDbHelper;
import com.example.android.inventoryudacity.data.ProductContract;

import static android.icu.text.RelativeDateTimeFormatter.Direction.THIS;
import static com.example.android.inventoryudacity.data.ProductContract.ProductEntry.TABLE_NAME;

public class EditorActivity extends AppCompatActivity {

    /**
     * EditText field to enter the product name
     */
    private EditText mProductEditText;

    /**
     * EditText field to enter the product quantity
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the product price
     */
    private EditText mPriceEditText;

    /**
     * EditText field to enter the supplier name
     */
    private EditText mSupplierEditText;

    /**
     * EditText field to enter the phone number
     */
    private EditText mPhoneEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_editor );

        // Find all relevant views that we will need to read user input from
        mProductEditText = (EditText) findViewById ( R.id.edit_product_name );
        mQuantityEditText = (EditText) findViewById ( R.id.edit_product_quantity );
        mPriceEditText = (EditText) findViewById ( R.id.edit_product_price );
        mSupplierEditText = (EditText) findViewById ( R.id.edit_product_supplier );
        mPhoneEditText = (EditText) findViewById ( R.id.edit_supplier_phone_number );
    }

    /**
     * Get user input from editor and save new product into database.
     */

    private void insertProduct() {
        // Create database helper
        PcDbHelper mDbHelper = new PcDbHelper ( this );
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase ();

        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mProductEditText.getText ().toString ().trim ();
        String quantityString = mQuantityEditText.getText ().toString ().trim ();
        String priceString = mPriceEditText.getText ().toString ().trim ();
        String supplierString = mSupplierEditText.getText ().toString ().trim ();
        String phoneString = mPhoneEditText.getText ().toString ().trim ();

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues ();
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString );
        values.put ( ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY, quantityString );
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, priceString );
        values.put ( ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierString );
        values.put ( ProductContract.ProductEntry.COLUMN_PHONE_NUMBER, phoneString );

        /// Insert a new row for product in the database, returning the ID of that new row.
        long newRowId = db.insert ( TABLE_NAME, null, values );

        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText ( this, "Error with saving product", Toast.LENGTH_SHORT ).show ();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText ( this, "Product saved with row id: " + newRowId, Toast.LENGTH_SHORT ).show ();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater ().inflate ( R.menu.menu_editor, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId ()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save pet to database
                insertProduct ();
                // Exit activity
                finish ();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask ( this );
                return true;
        }
        return super.onOptionsItemSelected ( item );
    }
}