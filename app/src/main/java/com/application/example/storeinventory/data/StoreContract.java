package com.application.example.storeinventory.data;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StoreContract {
    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.application.example.storeinventory.data.StoreContract";
    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STORE = "storeinventory";
    // empty constructor
    private StoreContract() {
    }
    // constant values for Store products are stored
    public static final class StoreEntry implements BaseColumns {
        /**
         * The content URI to access the store inventory data in the provider
         */
        public static  Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STORE);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STORE;

        // Name of db table
        public static final String TABLE_NAME = "store";
        // Unique product id
        public static final String COLUMN_ID = "_id";
        // Product name
        public static final String COLUMN_NAME = "name";
        // Product price
        public static final String COLUMN_PRICE = "price";
        // Number of items
        public static final String COLUMN_QUANTITY = "quantity";
        // Product supplier name and phone
        public static final String COLUMN_SUP_NAME= "supplier_name";
        public static final String COLUMN_SUP_PHONE= "supplier_phone";
    }
}
