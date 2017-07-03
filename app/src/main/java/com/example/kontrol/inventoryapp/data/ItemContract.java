package com.example.kontrol.inventoryapp.data;

import android.content.ContentResolver;
import android.content.UriMatcher;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kontrol on 4/19/2017.
 */

public final class ItemContract {

    private ItemContract(){}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.kontrol.inventoryapp";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.kontrol.inventoryapp/items/ is a valid path for
     */
    public static final String PATH_ITEMS = "items";

    public static abstract class ItemEntry implements BaseColumns{

        /** The content URI to access the items data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String TABLE_NAME = "items";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_SUPPLIER = "supplier";
        public static final String COLUMN_ITEM_PHOTO = "photo";


    }
}
