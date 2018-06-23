package com.application.example.storeinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

//Content Provider for Store App

public class StoreProvider extends ContentProvider {

   // Log tag
    public static final String LOG_TAG = StoreProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the it table
     */
    private static final int STORES = 10;
    /**
     * URI matcher code for the content URI for a single it device in the it table
     */
    private static final int STORE_ID = 11;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer run on first-time
    static {
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_STORE, STORES);
        // The content URI of the form  will map to the
        // integer code, provide access to ONE single row of the it table.
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, StoreContract.PATH_STORE + "/#", STORE_ID);
    }

    // Database helper object
    private DbHelper storeDbHelper;

    @Override
    public boolean onCreate() {
        storeDbHelper = new DbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = storeDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STORES:
                // Directly query table.
                cursor = database.query(StoreContract.StoreEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case STORE_ID:
                // extract id from uri
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // query on table after check is performed
                cursor = database.query(StoreContract.StoreEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORES:
                return insertStore(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertStore(Uri uri, ContentValues values) {

        // Null checking user input for table fields

        String name = values.getAsString(StoreContract.StoreEntry.COLUMN_NAME);
        if ((name == null) || (name.isEmpty())) {
            throw new IllegalArgumentException("The it device requires a name");
        }
        Integer price = values.getAsInteger(StoreContract.StoreEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("The price field requires valid number");
        }
        Integer quantity = values.getAsInteger(StoreContract.StoreEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("The quantity field requires valid number");
        }
        String supName = values.getAsString(StoreContract.StoreEntry.COLUMN_SUP_NAME);
        if (supName == null) {
            throw new IllegalArgumentException("The supplier of the it device requires a name");
        }
        String phone = values.getAsString(StoreContract.StoreEntry.COLUMN_SUP_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("The supplier phone requires a phone number");
        }
        // Get writeable database
        SQLiteDatabase database = storeDbHelper.getWritableDatabase();

        long id = database.insert(StoreContract.StoreEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify listeners that data has changed for content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with ID  appended at end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORES:
                return updateStore(uri, contentValues, selection, selectionArgs);
            case STORE_ID:
                // For the STORE_ID code, extract out the ID from the URI,
                //selection arguments will be a String array with ID.
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateStore(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    // Method to update field db
    private int updateStore(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // Null check for user provided fields
        if (values.containsKey(StoreContract.StoreEntry.COLUMN_NAME)) {
            String name = values.getAsString(StoreContract.StoreEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("The it device requires a name");
            }
        }
        if (values.containsKey(StoreContract.StoreEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(StoreContract.StoreEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("The price field requires valid number");
            }
        }

        Integer quantity = values.getAsInteger(StoreContract.StoreEntry.COLUMN_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("The quantity field requires valid number");
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_SUP_NAME)) {
            String sname = values.getAsString(StoreContract.StoreEntry.COLUMN_SUP_NAME);
            if (sname == null) {
                throw new IllegalArgumentException("The supplier of the it device requires a name");
            }
        }

        if (values.containsKey(StoreContract.StoreEntry.COLUMN_SUP_PHONE)) {
            String phone = values.getAsString(StoreContract.StoreEntry.COLUMN_SUP_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("The supplier phone requires a phone number");
            }
        }
        // Check for values to update
        if (values.size() == 0) {
            return 0;
        }
        // Get writable database to update the data
        SQLiteDatabase database = storeDbHelper.getWritableDatabase();

        // Update db and get changed rows
        int rowsUpdated = database.update(StoreContract.StoreEntry.TABLE_NAME, values, selection, selectionArgs);

        //On update notify uri listeners about data change
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = storeDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORES:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(StoreContract.StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case STORE_ID:
                // Delete a single row given by the ID in the URI
                selection = StoreContract.StoreEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(StoreContract.StoreEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }
    //Returns the MIME type of data for the content URI.
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STORES:
                return StoreContract.StoreEntry.CONTENT_LIST_TYPE;
            case STORE_ID:
                return StoreContract.StoreEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}