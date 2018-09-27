package com.example.android.inventoryudacity.data;

import android.provider.BaseColumns;

public final class ProductContract {// To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ProductContract() {}

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {

        /** Name of database table for pets */
        public final static String TABLE_NAME = "product";

        /**
         * Unique ID number for the pet (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the product.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PRODUCT_NAME ="name";

        /**
         * Quantity.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PROUCT_QUANTITY = "quantity";

        /**
         * Price
         *
         * Type: Integer
         */
        public final static String COLUMN_PRODUCT_PRICE ="price";

        /**
         * Phone Number
         *
         * Type Integer
         */
        public final static String COLUMN_PHONE_NUMBER = "phone number";

        /**
         * Supplier Name
         *
         * Type: Text
         */
        public final static String COLUMN_SUPPLIER_NAME ="supplier name";
    }

}

