package com.example.android.inventoryudacity.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ProductContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {
    }

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryudacity";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse ( "content://" + CONTENT_AUTHORITY );

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.product/products/ is a valid path for
     * looking at pet data. content://com.example.android.pets/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_PRODUCT = "PRODUCT";

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {

        /**
         * The content URI to access the product data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath ( BASE_CONTENT_URI, PATH_PRODUCT );
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        /**
         * Name of database table for pets
         */
        public final static String TABLE_NAME = "product";
        /**
         * Unique ID number for the product (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME = "name";

        /**
         * Quantity.
         * <p>
         * Type: Integer
         */
        public final static String COLUMN_PROUCT_QUANTITY = "quantity";

        /**
         * Price
         * <p>
         * Type: Integer
         */
        public final static String COLUMN_PRODUCT_PRICE = "price";

        /**
         * Supplier Name
         * <p>
         * Type: Text
         */
        public final static String COLUMN_SUPPLIER_NAME = "supplier";
        /**
         * Phone Number
         * <p>
         * Type Integer
         */
        public final static String COLUMN_PHONE_NUMBER = "phone";
// Sell option
        public final static String COLUMNS_PRODUCT_CAN_SELL = "sell";
        //Possible values for if the product can be sold or not.
        public static final int CAN_SELL_UNKNOWN = 0;
        public static final int CAN_SELL_YES = 1;
        public static final int CAN_SELL_NO = 2;


    }


}

