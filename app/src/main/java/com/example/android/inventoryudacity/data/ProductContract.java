package com.example.android.inventoryudacity.data;

import android.provider.BaseColumns;

public final class ProductContract {// To prevent someone from accidentally instantiating the contract class,

    // give it an empty constructor.
    private ProductContract() {
    }

    /**
     * Inner class that defines constant values for the pets database table.
     * Each entry in the table represents a single pet.
     */
    public static final class ProductEntry implements BaseColumns {

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
         * Type: TEXT
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


    }

}

