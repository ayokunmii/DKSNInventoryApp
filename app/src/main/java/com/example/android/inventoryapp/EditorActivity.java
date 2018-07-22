package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import Contract.DKSNContract;
import Contract.DKSNDbHelper;

public class EditorActivity extends AppCompatActivity {

    //declare variable for the edittext views in correspondng xml file
    int quantity = 0;
    EditText productName;
    EditText productPrice;
    EditText inputQuantity;
    EditText supplierName;
    EditText supplierNo;

    //declare variables that will serve as containers to help transfer edittext view's value(Ss) to SQLite Databse
    String pNameString;
    String priceString;
    int quantityString;
    String sNameString;
    String supplierNoString;

    // variable condition to check if all necessary values have been entereed
    boolean completedValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        //initialise edittext views
        productName = (EditText) findViewById(R.id.product_name);
        productPrice = (EditText) findViewById(R.id.product_price);
        inputQuantity = (EditText) findViewById(R.id.product_quantity);
        supplierName = (EditText) findViewById(R.id.supplier_name);
        supplierNo = (EditText) findViewById(R.id.supplier_phone_no);

    }

    private void insertProduct() {
        //get edittext values and store in container variables
        pNameString = productName.getText().toString().trim();
        priceString = productPrice.getText().toString().trim();
        quantityString = Integer.parseInt(inputQuantity.getText().toString());
        sNameString = supplierName.getText().toString().trim();
        supplierNoString = supplierNo.getText().toString().trim();

        // Create and/or open a database to read from it
        DKSNDbHelper mDBHelper = new DKSNDbHelper(this);
        SQLiteDatabase db = mDBHelper.getReadableDatabase();


        ContentValues values = new ContentValues();
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME, pNameString);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE, priceString);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY, quantityString);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME, sNameString);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO, supplierNoString);

        //set condition to ensure all necessary info is logged in before continuing
        if (TextUtils.isEmpty(pNameString) || TextUtils.isEmpty(priceString) || TextUtils.isEmpty(sNameString)) {
            Toast.makeText(this, "Please complete missing field(s)", Toast.LENGTH_SHORT).show();
            completedValues = false;
        } else {
            insertRow(db, values);
            completedValues = true;
        }
    }

    private void insertRow(SQLiteDatabase db, ContentValues values) {

        long newRowId = db.insert(DKSNContract.DKSNEntry.TABLE_NAME, null, values);
        Log.i("Editor Activity", "Values: " + values);

        if (newRowId == -1) {
            Toast.makeText(this, "Product cannot be added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Success \nProduct added, id - " + newRowId, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options and add menu items to the app bar.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "save button option
            case R.id.action_save:
                insertProduct();
                if (completedValues) {
                    finish();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    public void increment(View v) {
        if (inputQuantity != null) {
            String s = inputQuantity.getText().toString();
            ;
            Log.i("EditActivity", "Quantity is " + s);
            quantity = Integer.parseInt(s);
        }

        quantity++;
        displayStock(quantity);
    }

    public void decrement(View v) {
        String s = inputQuantity.getText().toString();
        ;
        Log.i("EditActivity", "Quantity is " + s);
        quantity = Integer.parseInt(s);
        quantity--;
        if (quantity <= 0) {
            Toast.makeText(this, "Add an item", Toast.LENGTH_SHORT).show();
            quantity = 0;
        }

        displayStock(quantity);
    }

    private void displayStock(int number) {
        inputQuantity.setText("" + number);
    }
}
