package com.example.android.inventoryudacity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
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
     * Identifier for the pet data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * Content URI for the existing pet (null if it's a new pet)
     */
    private Uri mCurrentProductUri;
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
        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new pet or editing an existing one.
        Intent intent = getIntent ();
        mCurrentProductUri = intent.getData ();

        // If the intent DOES NOT contain a pet content URI, then we know that we are
        // creating a new pet.
        if (mCurrentProductUri == null) {
            // This is a new pet, so change the app bar to say "Add a Product"
            setTitle ( getString ( R.string.editor_activity_title_new_product ) );
        } else {
            // Otherwise this is an existing pet, so change app bar to say "Edit Product"
            setTitle ( getString ( R.string.edit_product ) );

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager ().initLoader ( EXISTING_PRODUCT_LOADER,null, null);
        }

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

    private void saveProduct() {
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

        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentProductUri == null) {
            // This is a NEW product, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver ().insert ( ProductContract.ProductEntry.CONTENT_URI, values );
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText ( this, getString ( R.string.editor_insert_pet_failed ),
                        Toast.LENGTH_SHORT ).show ();
            } else {
                // Otherwise this is an EXISTING product, so update the pet with content URI: mCurrentPetUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentPetUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver ().update ( mCurrentProductUri, values, null,null );
                // Show a toast message depending on whether or not the update was successful.
                if (rowsAffected == 0) {
                    // If no rows were affected, then there was an error with the update.
                    Toast.makeText ( this, getString ( R.string.save_error ),
                            Toast.LENGTH_SHORT ).show ();
                } else {
                    // Otherwise, the update was successful and we can display a toast.
                    Toast.makeText ( this, getString ( R.string.prod_saved_id ),
                            Toast.LENGTH_SHORT ).show ();
                }
            }
            if (mCurrentProductUri == null &&
                    TextUtils.isEmpty(nameString) && TextUtils.isEmpty(supplierString) &&
                    TextUtils.isEmpty(phoneString) && TextUtils.isEmpty ( priceString )
                    && TextUtils.isEmpty ( quantityString ))  {return;}
            int price = 0;
            if (!TextUtils.isEmpty(priceString)) {
                price = Integer.parseInt(priceString);
            }
            values.put(ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price);
            int quantity = 0;
            if (!TextUtils.isEmpty(quantityString)) {
                quantity = Integer.parseInt(quantityString);
            }
            values.put(ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY, quantity);
        }
    }

        @Override
            public boolean onCreateOptionsMenu (Menu menu){
                // Inflate the menu options from the res/menu/menu_editor.xml file.
                // This adds menu items to the app bar.
                getMenuInflater ().inflate ( R.menu.menu_editor, menu );
                return true;
            }

            @Override
            public boolean onOptionsItemSelected (MenuItem item){
                // User clicked on a menu option in the app bar overflow menu
                switch (item.getItemId ()) {
                    // Respond to a click on the "Save" menu option
                    case R.id.action_save:
                        // Save pet to database
                        saveProduct ();
                        // Exit activity
                        finish ();
                        return true;
                    // Respond to a click on the "Delete" menu option
                    case R.id.action_delete:
                        //do nothing for now
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