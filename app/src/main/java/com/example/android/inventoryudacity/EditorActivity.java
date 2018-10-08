package com.example.android.inventoryudacity;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
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

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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

    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mProductHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


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

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager ().initLoader ( EXISTING_PRODUCT_LOADER, null, this );
        }

        // Find all relevant views that we will need to read user input from
        mProductEditText = (EditText) findViewById ( R.id.edit_product_name );
        mQuantityEditText = (EditText) findViewById ( R.id.edit_product_quantity );
        mPriceEditText = (EditText) findViewById ( R.id.edit_product_price );
        mSupplierEditText = (EditText) findViewById ( R.id.edit_product_supplier );
        mPhoneEditText = (EditText) findViewById ( R.id.edit_supplier_phone_number );

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mProductEditText.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mPhoneEditText.setOnTouchListener(mTouchListener);
    }
    /**
     * Get user input from editor and save new product into database.
     */

    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mProductEditText.getText ().toString ().trim ();
        String quantityString = mQuantityEditText.getText ().toString ().trim ();
        int quantity = Integer.parseInt ( quantityString );
        String priceString = mPriceEditText.getText ().toString ().trim ();
        String supplierString = mSupplierEditText.getText ().toString ().trim ();
        String phoneString = mPhoneEditText.getText ().toString ().trim ();
        double price = Double.parseDouble ( priceString );

        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty ( nameString ) && TextUtils.isEmpty ( quantityString ) &&
                TextUtils.isEmpty ( supplierString ) && TextUtils.isEmpty ( priceString )
                && TextUtils.isEmpty ( phoneString )) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues ();
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString );
        values.put ( ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY, quantity );
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, price );
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
               //Added a closing bracket below here
            } 
        }else {
                // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
                // and pass in the new ContentValues. Pass in null for the selection and selection args
                // because mCurrentPetUri will already identify the correct row in the database that
                // we want to modify.
                int rowsAffected = getContentResolver ().update ( mCurrentProductUri, values, null, null );
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
            }//And removed the closing bracket below here
            
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
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected ( item );
    }
    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader ( this,   // Parent activity context
                mCurrentProductUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null );                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount () < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst ()) {
            // Find the columns of product attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME );
            int quantityColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY );
            int priceColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE );
            int supplierColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME );
            int phoneColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PHONE_NUMBER );

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString ( nameColumnIndex );
            String price = cursor.getString ( priceColumnIndex );
            int quantity = cursor.getInt ( quantityColumnIndex );
            int phone = cursor.getInt ( phoneColumnIndex );
            String supplier =cursor.getString ( supplierColumnIndex );

            // Update the views on the screen with the values from the database
            mProductEditText.setText ( name );
            mPriceEditText.setText ( price );
            mSupplierEditText.setText (supplier);
            mQuantityEditText.setText (Integer.toString ( quantity ) );
            mPhoneEditText.setText ( Integer.toString ( phone ) );
            }
        }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductEditText.setText ( "" );
        mQuantityEditText.setText ( 0 );
        mSupplierEditText.setText ( "" );
        mPriceEditText.setText ( "" );
        mPhoneEditText.setText ( "" );
    }
    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
