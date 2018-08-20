package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import Contract.DKSNContract;
import Contract.DKSNCursorAdapter;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER = 0;
    DKSNCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setting up the floating action button to open up EditorActivity

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(i);
            }
        });


        ListView list = (ListView) findViewById(R.id.list_view_prod);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        list.setEmptyView(emptyView);


        mAdapter = new DKSNCursorAdapter(this, null);
        list.setAdapter(mAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditorActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(DKSNContract.DKSNEntry.CONTENT_URI, id);
                i.setData(currentProductUri);
                startActivity(i);

            }
        });


        getLoaderManager().initLoader(LOADER, null, this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options and add menu items to the app bar.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.insert_dummy_data:
                insertProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {

        ContentValues values = new ContentValues();
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME, "Fenty Beauty Pro Filt'r Soft Matte Longwear Foundation");
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE, 26.00);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY, 40);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME, "Harvey Nichols");
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO, "+442072018088");

        Uri newUri = getContentResolver().insert(DKSNContract.DKSNEntry.CONTENT_URI, values);
    }

    private void deleteAllProducts() {
        if (DKSNContract.DKSNEntry.CONTENT_URI != null) {
            int deleteAll = getContentResolver().delete(DKSNContract.DKSNEntry.CONTENT_URI, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (deleteAll == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.total_del_failed), Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.total_del_successful), Toast.LENGTH_SHORT).show();
            }


        }
    }


    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_all_products);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete products.
                deleteAllProducts();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog and continue editing the product.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // dealing with only columns we care about and want to display boohoo
        String[] projection = {
                DKSNContract.DKSNEntry._ID,
                DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME,
                DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE,
                DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY,
                DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME,
                DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO

        };

        //Get database from content resolver query as defined in DKSNProvider java class using the parameters set under the query method
        return new CursorLoader(this,
                DKSNContract.DKSNEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {


        mAdapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }


}