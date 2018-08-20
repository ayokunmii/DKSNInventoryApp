package Contract;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ayoawotunde on 07/08/2018.
 */

public class DKSNProvider extends ContentProvider {
    /**
     * URI matcher code for the content URI for the entire table
     */
    private static final int PRODUCTS = 100;
    /**
     * URI matcher code for the content URI for a single product from the table
     */

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PRODUCT_ID = 101;

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        sUriMatcher.addURI(DKSNContract.CONTENT_AUTHORITY, DKSNContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(DKSNContract.CONTENT_AUTHORITY, DKSNContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    private DKSNDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new DKSNDbHelper(getContext());
        return true;
    }

    //Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // For the PRODUCT code, query the products table directly with the given projection,
                // selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the products table.
                cursor = database.query(
                        DKSNContract.DKSNEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case PRODUCT_ID:

                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = DKSNContract.DKSNEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query on the products table with the relevant id to return a
                // Cursor containing that row of the table.
                cursor = database.query(
                        DKSNContract.DKSNEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        // Set notification URI on the Cursor, so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    //Insert new data into the provider with the given ContentValues.
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                //notification for change in data in content uri
                getContext().getContentResolver().notifyChange(uri, null);
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }


    //Insert a productt into the database with the given content values. Return the new content URI for that specific row in the database.

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the product name is not null
        String name = values.getAsString(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME);
        Log.i("Check", "is name null? " + name);
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), "Product requires a name", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Product requires a name");
        }

        // Check that the price is valid
        Integer price = values.getAsInteger(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE);
        if (price == null) {
            Toast.makeText(getContext(), "Product requires an accurate quantity", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Product requires an accurate quantity");
        }

        // Check that the supplier name is not null
        String suppName = values.getAsString(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME);
        Log.i("Check", "is name null? " + suppName);
        if (TextUtils.isEmpty(suppName)) {
            Toast.makeText(getContext(), "Product supplier requires a name", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("Product supplier requires a name");
        }


        // Get writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(DKSNContract.DKSNEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e("DKSN Provider", "Failed to insert row for " + uri);
            return null;
        }
        //notification for change in data in content uri
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Track the number of rows that were deleted
        int rowsDeleted;

        // Get writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PRODUCTS:
                //notification for change in data in content uri
                getContext().getContentResolver().notifyChange(uri, null);
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(DKSNContract.DKSNEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = DKSNContract.DKSNEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Delete a single row given by the ID in the URI
                rowsDeleted = database.delete(DKSNContract.DKSNEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                //notification for change in data in content uri
                getContext().getContentResolver().notifyChange(uri, null);
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                //notification for change in data in content uri
                getContext().getContentResolver().notifyChange(uri, null);
                // For the PRODUCT_ID code, extract out the ID from the URI, so we know which row to update. Selection will be "_id=?"
                // and selection arguments will be a String array containing the actual ID.
                selection = DKSNContract.DKSNEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more products).
     * Return the number of rows that were successfully updated.
     */
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME)) {

            // Check that the product name is not null
            String name = values.getAsString(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME);
            Log.i("Check", "is name null? " + name);
            if (TextUtils.isEmpty(name)) {
                Toast.makeText(getContext(), "Product requires a name", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE)) {
            // Check that the price is valid
            Integer price = values.getAsInteger(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE);
            if (price == null) {
                Toast.makeText(getContext(), "Product requires an accurate quantity", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Product requires an accurate quantity");
            }
        }

        if (values.containsKey(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME)) {

            // Check that the supplier name is not null
            String suppName = values.getAsString(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME);
            Log.i("Check", "is name null? " + suppName);
            if (TextUtils.isEmpty(suppName)) {
                Toast.makeText(getContext(), "Product supplier requires a name", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Product supplier requires a name");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(DKSNContract.DKSNEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement i.e rowsUpdated variable
        return rowsUpdated;
    }


    //Returns the MIME type of data for the content URI.
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return DKSNContract.DKSNEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return DKSNContract.DKSNEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


}
