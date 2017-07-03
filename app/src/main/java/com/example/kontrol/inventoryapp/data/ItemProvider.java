package com.example.kontrol.inventoryapp.data;

import android.content.ClipData;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.kontrol.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by Kontrol on 4/20/2017.
 */

/**
 * {@link ContentProvider} for Inventory app.
 */
public class ItemProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();

    private static final int ITEM = 100;
    private static final int ITEM_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static{

        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEM);
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }


    /** Database Helper Object */
    private ItemsDbHelper mDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        // Create and initialize a ItemsDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.

        mDbHelper = new ItemsDbHelper(getContext());

        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                // For the ITEM code, query the items table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the items table.
                // TODO: Perform database query on pets table
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert an item into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPet(Uri uri, ContentValues values) {

        // Check that the name is not null
        String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Check that the price is not a -ve value or zero
        int price = values.getAsInteger(ItemEntry.COLUMN_ITEM_PRICE);
        if(price < 0 || price == 0){
            throw new IllegalArgumentException("Item requires a valid price");
        }

        // Check that the quantity is not -ve value
        int quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
        if(quantity < 0){
            throw new IllegalArgumentException("Item's quantity cannot be a -ve number");
        }

        // Check that the supplier is not null.
        String supplier = values.getAsString(ItemEntry.COLUMN_ITEM_SUPPLIER);
        if(supplier == null){
            throw new IllegalArgumentException("Item requires a supplier");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(ItemEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case ITEM_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if(values.containsKey(ItemEntry.COLUMN_ITEM_NAME)){
            String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Pet requires a name");
            }
        }

        if(values.containsKey(ItemEntry.COLUMN_ITEM_PRICE)){
            int price = values.getAsInteger(ItemEntry.COLUMN_ITEM_PRICE);
            if(price < 0 || price == 0){
                throw new IllegalArgumentException("Item requires a valid price");
            }
        }

        if(values.containsKey(ItemEntry.COLUMN_ITEM_QUANTITY)){
            int quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
            if(quantity < 0){
                throw new IllegalArgumentException("Item's quantity cannot be a -ve number");
            }
        }

        if(values.containsKey(ItemEntry.COLUMN_ITEM_SUPPLIER)){
            String supplier = values.getAsString(ItemEntry.COLUMN_ITEM_SUPPLIER);
            if(supplier == null){
                throw new IllegalArgumentException("Item requires a supplier");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
        if(rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }


        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                if(rowsDeleted != 0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted !=0){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEM:
                return ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}