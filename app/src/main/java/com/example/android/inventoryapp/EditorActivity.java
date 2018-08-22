package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Contract.DKSNContract;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PROD_LOADER = 0;
    //declare variable for the edittext and button views in correspondng xml file
    int quantity = 0;
    EditText productName;
    EditText productPrice;
    EditText inputQuantity;
    EditText supplierName;
    EditText supplierNo;
    Button decrement;
    Button increment;
    //declare variables that will serve as containers to help transfer edittext view's value(Ss) to SQLite Databse
    String pNameString;
    String priceString;
    int quantityString;
    String sNameString;
    String supplierNoString;
    // variable condition to check if all necessary values have been entered when inputing product details
    boolean completedValues;
    //Uri for current product,this will be null if product is new
    private Uri mCurrentProdUri;
    //boolean to monitor if any change has been made to product
    private boolean mProdHasChanged = false;
    // OnTouchListener that listens for any user touches on a View, implying that they are modifying the view, and we
    // change the variable boolean to true.
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mProdHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //get data from main activity, if product already exists
        Intent i = getIntent();
        mCurrentProdUri = i.getData();

        //code to determine label on activity depending on if product is new or existing
        if (mCurrentProdUri == null) {
            setTitle(R.string.editor_activity_title_new_product);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a product if it hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(R.string.editor_activity_title_edit_product);
            getLoaderManager().initLoader(PROD_LOADER, null, this);
        }

        //initialise edittext views and buttons
        productName = (EditText) findViewById(R.id.product_name);
        productPrice = (EditText) findViewById(R.id.product_price);
        inputQuantity = (EditText) findViewById(R.id.product_quantity);
        supplierName = (EditText) findViewById(R.id.supplier_name);
        supplierNo = (EditText) findViewById(R.id.supplier_phone_no);
        decrement = (Button) findViewById(R.id.decrement);
        increment = (Button) findViewById(R.id.increment);
        //set touch listeners to notify if an edit has been made to the product's variable(s)
        productName.setOnTouchListener(mTouchListener);
        productPrice.setOnTouchListener(mTouchListener);
        inputQuantity.setOnTouchListener(mTouchListener);
        supplierName.setOnTouchListener(mTouchListener);
        supplierNo.setOnTouchListener(mTouchListener);
        decrement.setOnTouchListener(mTouchListener);
        increment.setOnTouchListener(mTouchListener);

    }

    private void getData() {
        //getting user input from editor - for the variable values value
        pNameString = productName.getText().toString().trim();
        priceString = productPrice.getText().toString().trim();
        quantityString = Integer.parseInt(inputQuantity.getText().toString());
        sNameString = supplierName.getText().toString().trim();
        supplierNoString = supplierNo.getText().toString().trim();

        if (TextUtils.isEmpty(inputQuantity.getText().toString())) {
            quantityString = 0;

        } else {
            quantityString = Integer.parseInt(inputQuantity.getText().toString());
        }

        //set condition to ensure all necessary info is logged in before continuing
        if (TextUtils.isEmpty(pNameString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(sNameString)) {
            Toast.makeText(this, getString(R.string.missing_fields_alert), Toast.LENGTH_SHORT).show();
            completedValues = false;
        } else {
            completedValues = true;
            saveProduct();
        }

    }


    private void saveProduct() {

        ContentValues values = new ContentValues();
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME, pNameString);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE, priceString);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY, quantityString);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME, sNameString);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO, supplierNoString);


        // Determine if this is a new or existing product by checking if Uri variabe is null or not
        if (mCurrentProdUri == null) {
            // This is a NEW product, so insert a new product into the provider, returning the content URI for the new product.
            Uri newUri = getContentResolver().insert(DKSNContract.DKSNEntry.CONTENT_URI, values);
            // Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_prod_failed), Toast.LENGTH_SHORT).show();

            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_prod_successful),
                        Toast.LENGTH_SHORT).show();

            }


        } else {
            // Otherwise this is an EXISTING product, so update the product with content URI and pass in the new ContentValues.
            // Pass in null for the selection and selection args because var will already identify the correct row in the
            // database that we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentProdUri, values, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners for the postive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the product.
                deleteProduct();

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

    //Perform the deletion of the product in the database.

    private void deleteProduct() {

        // Only perform the delete if this is an existing product.
        if (mCurrentProdUri != null) {
            // Call the ContentResolver to delete the product at the given content URI. Pass in null for the selection and s
            // election args because the content URI already identifies the productt that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentProdUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_prod_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_prod_successful),
                        Toast.LENGTH_SHORT).show();
            }


        }
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options and add menu items to the app bar.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new product, hide the "Delete" menu item.
        if (mCurrentProdUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "save button option
            case R.id.action_save:
                getData();
                if (completedValues) {
                    finish();
                }
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the product hasn't changed, continue with navigating up to parent activity
                if (!mProdHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the product.
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
    public void onBackPressed() {
        // If the product hasn't changed, continue with handling back button press
        if (!mProdHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    public void increment(View v) {
        if (inputQuantity != null) {
            String s = inputQuantity.getText().toString();
            Log.i("EditActivity", "Quantity is " + s);
            quantity = Integer.parseInt(s);
        }
        if (quantity < 0) {
            quantity = 0;
        } else {
            quantity++;
        }
        displayStock(quantity);
    }

    public void decrement(View v) {
        String s = inputQuantity.getText().toString();
        Log.i("EditActivity", "Quantity is " + s);
        quantity = Integer.parseInt(s);
        quantity--;
        if (quantity <= 0) {
            Toast.makeText(this, getString(R.string.add_item), Toast.LENGTH_SHORT).show();
            quantity = 0;
        }

        displayStock(quantity);
    }

    private void displayStock(int number) {
        inputQuantity.setText("" + number);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                DKSNContract.DKSNEntry._ID,
                DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME,
                DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE,
                DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY,
                DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME,
                DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO
        };

        //Get database from content resolver query as defined in your Provider java class using the parameters set under the query method
        return new CursorLoader(this,
                mCurrentProdUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor c) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (c == null || c.getCount() < 1) {
            return;
        }

        if (c.moveToFirst()) {

            //get column indices
            int idColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry._ID);
            int pNameColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME);
            int priceColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE);
            int quantityColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY);
            int sNameColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME);
            int sNoColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO);

            // Extract out the value from the Cursor for the given column index
            int currentID = c.getInt(idColumnIndex);
            String currentPName = c.getString(pNameColumnIndex);
            int currentPrice = c.getInt(priceColumnIndex);
            int currentQuantity = c.getInt(quantityColumnIndex);
            String currentSName = c.getString(sNameColumnIndex);
            String currentSNo = c.getString(sNoColumnIndex);

            // Update the views on the screen with the values from the database
            productName.setText(currentPName);
            productPrice.setText(String.valueOf(currentPrice));
            inputQuantity.setText(String.valueOf(currentQuantity));
            supplierName.setText(currentSName);
            supplierNo.setText(currentSNo);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        productName.setText("");
        productPrice.setText("");
        inputQuantity.setText("");
        supplierName.setText("");
        supplierNo.setText("");
    }

}
