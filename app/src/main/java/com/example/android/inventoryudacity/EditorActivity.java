package com.example.android.inventoryudacity;

import android.annotation.SuppressLint;
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
import android.widget.Button;
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

    /**
     * Boolean flag that keeps track of whether the product has been edited (true) or not (false)
     */
    private boolean mProductHasChanged = false;
    //setup spinner
    private Spinner mCanSellSpinner;
    //sell button
    private int mCanSell = ProductContract.ProductEntry.CAN_SELL_UNKNOWN;
    //buttons to increase and decrease quantity
    private Button moreButton;
    private Button lessButton;
    //call button
    private Button phoneButton;

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mPetHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener () {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };


    @SuppressLint("ClickableViewAccessibility")
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
            // This is a new product, so change the app bar to say "Add a Product"
            setTitle ( getString ( R.string.editor_activity_title_new_product ) );
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product that hasn't been created yet.)
            invalidateOptionsMenu ();
        } else {
            // Otherwise this is an existing product, so change app bar to say "Edit Product"
            setTitle ( getString ( R.string.edit_product ) );

            // Initialize a loader to read the product data from the database
            // and display the current values in the editor
            getLoaderManager ().initLoader ( EXISTING_PRODUCT_LOADER, null, this );
        }

        // Find all relevant views that we will need to read user input from
        mProductEditText = (EditText) findViewById ( R.id.edit_product_name );
        mQuantityEditText = (EditText) findViewById ( R.id.edit_product_quantity );
        mCanSellSpinner = (Spinner) findViewById ( R.id.spinner_available );
        mPriceEditText = (EditText) findViewById ( R.id.edit_product_price );
        mSupplierEditText = (EditText) findViewById ( R.id.edit_product_supplier );
        mPhoneEditText = (EditText) findViewById ( R.id.edit_supplier_phone_number );

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mProductEditText.setOnTouchListener ( mTouchListener );
        mQuantityEditText.setOnTouchListener ( mTouchListener );
        mCanSellSpinner.setOnTouchListener ( mTouchListener );
        mPriceEditText.setOnTouchListener ( mTouchListener );
        mSupplierEditText.setOnTouchListener ( mTouchListener );
        mPhoneEditText.setOnTouchListener ( mTouchListener );
        //find buttons
        moreButton = (Button) findViewById ( R.id.increment_button );
        lessButton = (Button) findViewById ( R.id.decrement_button );
        phoneButton = (Button) findViewById ( R.id.call_supplier );
        setupSpinner ();
        // set up buttons
        moreButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Integer changeQuantity;
                // pull the current value
                try {
                    changeQuantity = Integer.parseInt ( mQuantityEditText.getText ().toString () );
                    changeQuantity += 1;
                    //check for to low of value
                    if (changeQuantity > 100) {
                        changeQuantity = 100;
                    }
                } catch (Exception e) {
                    // if the value in the field is invalid set it to 1
                    changeQuantity = 1;
                }
                // update field
                mQuantityEditText.setText ( Integer.toString ( changeQuantity ) );
            }
        } );
        lessButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Integer changeQuantity;
                // pull the current value
                try {
                    changeQuantity = Integer.parseInt ( mQuantityEditText.getText ().toString () );
                    changeQuantity -= 1;
                    //check for to low of value
                    if (changeQuantity < 0) {
                        changeQuantity = 0;
                    }
                } catch (Exception e) {
                    // if the value in the field is invalid set it to 0
                    changeQuantity = 0;
                }
                // update field
                mQuantityEditText.setText ( Integer.toString ( changeQuantity ) );
            }
        } );
        phoneButton.setOnClickListener ( new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent ( Intent.ACTION_DIAL );
                intent.setData ( Uri.parse ( "tel:" + mPhoneEditText.getText ().toString () ) );
                startActivity ( intent );
            }
        } );
    }

    /**
     * Setup the dropdown spinner that allows the user to select the availability of the book.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource ( this,
                R.array.array_available_options, android.R.layout.simple_spinner_item );
        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource ( android.R.layout.simple_dropdown_item_1line );
        // Apply the adapter to the spinner
        mCanSellSpinner.setAdapter ( genderSpinnerAdapter );
        // Set the integer mSelected to the constant values
        mCanSellSpinner.setOnItemSelectedListener ( new AdapterView.OnItemSelectedListener () {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition ( position );
                if (!TextUtils.isEmpty ( selection )) {
                    if (selection.equals ( getString ( R.string.can_sell_yes ) )) {
                        mCanSell = ProductContract.ProductEntry.CAN_SELL_YES;
                    } else if (selection.equals ( getString ( R.string.can_sell_no ) )) {
                        mCanSell = ProductContract.ProductEntry.CAN_SELL_NO;
                    } else {
                        mCanSell = ProductContract.ProductEntry.CAN_SELL_UNKNOWN;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCanSell = ProductContract.ProductEntry.CAN_SELL_UNKNOWN;
            }
        } );
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
        // turn the string to int for data base.
        int quantityInt;
        try {
            quantityInt = Integer.parseInt ( quantityString );
        } catch (Exception e) {
            quantityInt = 0;
        }


        // Check if this is supposed to be a new product
        if (mCurrentProductUri == null &&
                TextUtils.isEmpty ( nameString ) && TextUtils.isEmpty ( quantityString ) &&
                TextUtils.isEmpty ( supplierString ) && TextUtils.isEmpty ( priceString )
                && TextUtils.isEmpty ( phoneString )) {

            // and check if all the fields in the editor are blank
            if (!typedInfo ( nameString, quantityString, priceString, supplierString, phoneString )) {
                // Since no fields were modified, we can return early without creating a new book.
                // No need to create ContentValues and no need to do any ContentProvider operations.
                return;
            }
        }
        // Create a ContentValues object where column names are the keys,
        // and product attributes from the editor are the values.
        ContentValues values = new ContentValues ();
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, nameString );
        values.put ( ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY, quantityString );
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, priceString );
        values.put ( ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, supplierString );
        values.put ( ProductContract.ProductEntry.COLUMN_PHONE_NUMBER, phoneString );
        values.put ( ProductContract.ProductEntry.COLUMNS_PRODUCT_CAN_SELL, mCanSell );

        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentProductUri == null) {

            // This is a NEW product, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver ().insert ( ProductContract.ProductEntry.CONTENT_URI, values );
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText ( this, getString ( R.string.error_saving ),
                        Toast.LENGTH_SHORT ).show ();
            } else {
                Toast.makeText ( this, getString ( R.string.product_saved ),
                        Toast.LENGTH_SHORT ).show ();
            }
        } else {

            // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentProductUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentProductUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver ().update ( mCurrentProductUri, values, null, null );
            if (!typedInfo ( nameString, quantityString, priceString, supplierString, phoneString )) {
                // Since no fields were modified, we can return early without creating a new book.
                // No need to create ContentValues and no need to do any ContentProvider operations.
                return;
            }
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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater ().inflate ( R.menu.menu_editor, menu );
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu ( menu );
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem ( R.id.action_delete );
            menuItem.setVisible ( false );
        }
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
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog ();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask ( this );
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask ( EditorActivity.this );
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask ( EditorActivity.this );
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog ( discardButtonClickListener );
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
            super.onBackPressed ();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish ();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog ( discardButtonClickListener );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all product attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMNS_PRODUCT_CAN_SELL,
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
            int canSellColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMNS_PRODUCT_CAN_SELL );
            int priceColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE );
            int supplierColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME );
            int phoneColumnIndex = cursor.getColumnIndex ( ProductContract.ProductEntry.COLUMN_PHONE_NUMBER );

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString ( nameColumnIndex );
            String price = cursor.getString ( priceColumnIndex );
            int quantity = cursor.getInt ( quantityColumnIndex );
            int canSell = cursor.getInt ( canSellColumnIndex );
            int phone = cursor.getInt ( phoneColumnIndex );
            String supplier = cursor.getString ( supplierColumnIndex );

            // Update the views on the screen with the values from the database
            mProductEditText.setText ( name );
            mPriceEditText.setText ( price );
            mSupplierEditText.setText ( supplier );
            mQuantityEditText.setText ( Integer.toString ( quantity ) );
            mPhoneEditText.setText ( Integer.toString ( phone ) );
            // Can sell is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is yes, 2 is no).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (canSell) {
                case ProductContract.ProductEntry.CAN_SELL_YES:
                    mCanSellSpinner.setSelection ( 1 );
                    break;
                case ProductContract.ProductEntry.CAN_SELL_NO:
                    mCanSellSpinner.setSelection ( 2 );
                    break;
                default:
                    mCanSellSpinner.setSelection ( 0 );
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mProductEditText.setText ( "" );
        mQuantityEditText.setText ( "" );
        mSupplierEditText.setText ( "" );
        mPriceEditText.setText ( "" );
        mPhoneEditText.setText ( "" );
        mCanSellSpinner.setSelection ( 0 ); // Select "Unknown" option
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
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( R.string.unsaved_changes_dialog_msg );
        builder.setPositiveButton ( R.string.discard, discardButtonClickListener );
        builder.setNegativeButton ( R.string.keep_editing, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss ();
                }
            }
        } );
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder ( this );
        builder.setMessage ( R.string.delete_dialog_msg );
        builder.setPositiveButton ( R.string.delete_button, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the book.
                deleteProduct ();
            }
        } );
        builder.setNegativeButton ( R.string.cancel, new DialogInterface.OnClickListener () {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss ();
                }
            }
        } );
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();
    }

    private void deleteProduct() {
        // Only perform the delete if this is an existing book.
        if (mCurrentProductUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver ().delete ( mCurrentProductUri, null, null );
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText ( this, getString ( R.string.editor_delete_product_failed ),
                        Toast.LENGTH_SHORT ).show ();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText ( this, getString ( R.string.editor_delete_book_successful ),
                        Toast.LENGTH_SHORT ).show ();
            }
            // Close the activity
            finish ();
        }
    }

    private boolean typedInfo(String nameString, String quantity, String
            price, String supplierString, String phoneString) {
        if (TextUtils.isEmpty ( nameString )) {
            Toast.makeText ( this, getResources ().getString ( R.string.empty_name ), Toast.LENGTH_SHORT ).show ();
            return false;
        }
        if (TextUtils.isEmpty ( quantity )) {
            Toast.makeText ( this, getResources ().getString ( R.string.empty_quantity ), Toast.LENGTH_SHORT ).show ();
            return false;
        }
        if (TextUtils.isEmpty ( supplierString )) {
            Toast.makeText ( this, getResources ().getString ( R.string.empty_supplier ), Toast.LENGTH_SHORT ).show ();
            return false;
        }
        if (TextUtils.isEmpty ( price )) {
            Toast.makeText ( this, getResources ().getString ( R.string.empty_price ), Toast.LENGTH_SHORT ).show ();
            return false;
        }
        if (TextUtils.isEmpty ( phoneString )) {
            Toast.makeText ( this, getResources ().getString ( R.string.empty_phone ), Toast.LENGTH_SHORT ).show ();
            return false;
        }

        return true;
    }

}