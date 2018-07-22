package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import Contract.DKSNContract;
import Contract.DKSNDbHelper;

public class MainActivity extends AppCompatActivity {
    DKSNDbHelper mDBHelper;

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

        // To access our database, we instantiate our subclass of SQLiteOpenHelper and pass the current activity.
        mDBHelper = new DKSNDbHelper(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options and add menu items to the app bar.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();

    }

    private void displayDatabaseInfo() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();

        String[] projection = {
                DKSNContract.DKSNEntry._ID,
                DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME,
                DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE,
                DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY,
                DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME,
                DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO
        };
        Cursor c = db.query(
                DKSNContract.DKSNEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        TextView productDetails = (TextView) findViewById(R.id.product_list);
        try {
            // this is to get currency based on your location
            Locale locale = Locale.getDefault();
            Currency currency = Currency.getInstance(locale);
            // this is a format method that changes numbers to currencies
            NumberFormat currencyFormatter =
                    NumberFormat.getCurrencyInstance(locale);


            productDetails.setText("So far, we've found- \n" + c.getCount() + " items for you to play around with! ");
            productDetails.append("\n\n" +
                    DKSNContract.DKSNEntry._ID + ". " +
                    DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME + " - " +
                    DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE + " - " +
                    DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY + " - " +
                    DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME + " - " +
                    DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO + "\n"
            );

            //get column indices
            int idColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry._ID);
            int pNameColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME);
            int priceColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE);
            int quantityColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY);
            int sNameColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME);
            int sNoColumnIndex = c.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO);

            //iterate through table to return all rows to the cursor
            while (c.moveToNext()) {
                int currentID = c.getInt(idColumnIndex);
                String currentPName = c.getString(pNameColumnIndex);
                int currentPrice = c.getInt(priceColumnIndex);
                int currentQuantity = c.getInt(quantityColumnIndex);
                String currentSName = c.getString(sNameColumnIndex);
                String currentSNo = c.getString(sNoColumnIndex);

                // formatting using the currency NumberFormat declared above
                String localPrice = currencyFormatter.format(currentPrice);


                //now displayyy
                productDetails.append("\n" +
                        currentID + ". " +
                        currentPName + " - " +
                        localPrice + " - " +
                        currentQuantity + " - " +
                        currentSName + " - " +
                        currentSNo);
            }
        } finally {
            c.close();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_save:
                insertProduct();
                displayDatabaseInfo();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void insertProduct() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDBHelper.getReadableDatabase();


        ContentValues values = new ContentValues();
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME, "Fenty Beauty Pro Filt'r Soft Matte Longwear Foundation");
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE, 26.00);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY, 40);
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME, "Harvey Nichols");
        values.put(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO, "+442072018088");

        long newRowId = db.insert(DKSNContract.DKSNEntry.TABLE_NAME, null, values);
    }
}