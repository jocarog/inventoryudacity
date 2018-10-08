package com.example.android.inventoryudacity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.example.android.inventoryudacity.data.PcDbHelper;
import com.example.android.inventoryudacity.data.ProductContract;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int Product_Loader = 0;
    ProductCursorAdapter mCursorAdapter;


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

        // Find the ListView which will be populated with the pet data
        ListView productListView = (ListView) findViewById ( R.id.list );
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById ( R.id.empty_view );
        productListView.setEmptyView ( emptyView );

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        mCursorAdapter = new ProductCursorAdapter ( this, null );
        productListView.setAdapter ( mCursorAdapter );
        //Setup item clickListener
        productListView.setOnItemClickListener( new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri currentProductUri = ContentUris.withAppendedId ( ProductContract.ProductEntry.CONTENT_URI, id );
                Intent intent = new Intent (CatalogActivity.this, EditorActivity.class);
                intent.setData(currentProductUri);
                startActivity ( intent );

            }
        } );


        // kick off the Loader
        getLoaderManager ().initLoader ( Product_Loader, null, this );
    }


    /**
     * Helper method to insert hardcoded product data into the database. For debugging purposes only.
     */

    private void insertProduct() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's pet attributes are the values.
        ContentValues values = new ContentValues ();
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_NAME, "Earrings" );
        values.put ( ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY, " 5 " );
        values.put ( ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE, " 10 " );
        values.put ( ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME, " Millanus " );
        values.put ( ProductContract.ProductEntry.COLUMN_PHONE_NUMBER, " 3055459778 " );

        // The third argument is the ContentValues object containing the dummy info.
        Uri newUri = getContentResolver ().insert ( ProductContract.ProductEntry.CONTENT_URI, values );
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                //do nothing for now
                return true;
        }
        return super.onOptionsItemSelected ( item );
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
// Define a projection that specifies the column from the table
        String[] projection = {
                ProductContract.ProductEntry._ID,
                ProductContract.ProductEntry.COLUMN_PRODUCT_NAME,
                ProductContract.ProductEntry.COLUMN_PROUCT_QUANTITY,
                ProductContract.ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductContract.ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductContract.ProductEntry.COLUMN_PHONE_NUMBER,};
        //This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader ( this,
                ProductContract.ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor ( data );
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor ( null );
    }
}