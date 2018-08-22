package Contract;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.R;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * Created by ayoawotunde on 07/08/2018.
 */

public class DKSNCursorAdapter extends CursorAdapter {
    int quantityBought = 1;

    int quantityInt;
    Button increment;
    Button decrement;
    TextView inputQuan;
    ImageButton suppContact;

    public DKSNCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameOfProduct = (TextView) view.findViewById(R.id.name_text);
        TextView priceOfProduct = (TextView) view.findViewById(R.id.price_text);
        TextView quantityOfProduct = (TextView) view.findViewById(R.id.quantity_text);
        TextView productSupplier = (TextView) view.findViewById(R.id.supplier_text);
        final TextView supplierNo = (TextView) view.findViewById(R.id.supplier_number);

        suppContact = (ImageButton) view.findViewById(R.id.contact_supplier);
        TextView stockLevel = (TextView) view.findViewById(R.id.stock_level);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        increment = (Button) view.findViewById(R.id.increment_b);
        decrement = (Button) view.findViewById(R.id.decrement_b);
        inputQuan = (TextView) view.findViewById(R.id.quantity_bought);

        final Button confirmation = (Button) view.findViewById(R.id.ok_button);
        final LinearLayout saleQuantity = (LinearLayout) view.findViewById(R.id.sales_quantity);
        saleQuantity.setVisibility(View.INVISIBLE);
        confirmation.setVisibility(View.INVISIBLE);


        int nameColumnIndex = cursor.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY);
        int supplierColumnIndex = cursor.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME);
        int sNoColumnIndex = cursor.getColumnIndex(DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO);

        String name = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        final String quantity = cursor.getString(quantityColumnIndex);
        String supplierName = cursor.getString(supplierColumnIndex);
        String supplierNumber = cursor.getString(sNoColumnIndex);

        if (TextUtils.isEmpty(supplierNumber)) {
            supplierNumber = context.getString(R.string.no_contact);
        }

        // this is to get currency based on your location
        Locale locale = Locale.getDefault();
        Currency currency = Currency.getInstance(locale);
        // this is a format method that changes numbers to currencies
        NumberFormat currencyFormatter =
                NumberFormat.getCurrencyInstance(locale);
        // formatting using the currency NumberFormat declared above
        final double intPrice = Double.valueOf(price);
        String localPrice = currencyFormatter.format(intPrice);

        nameOfProduct.setText(name);
        priceOfProduct.setText(localPrice);
        quantityOfProduct.setText(quantity);
        productSupplier.setText(supplierName);
        supplierNo.setText(supplierNumber);


        quantityInt = Integer.valueOf(quantity);

        if (quantityInt <= 0) {
            stockLevel.setText(R.string.out_of_stock);
            stockLevel.setTextColor((context.getResources().getColor(R.color.out_of_stock)));
        } else if (quantityInt <= 15) {
            stockLevel.setText(R.string.low_in_stock);
            stockLevel.setTextColor((context.getResources().getColor(R.color.black)));
        } else if (quantityInt > 15) {
            stockLevel.setText(R.string.in_stock);
            stockLevel.setTextColor((context.getResources().getColor(R.color.black)));
        }


        //Setting up the button to order product via a phone call
        final String finalSupplierNumber = supplierNumber;
        suppContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String phoneNumber = supplierNo.getText().toString().trim();
                    Intent intentPhone = new Intent(Intent.ACTION_DIAL);
                    intentPhone.setData(Uri.parse("tel:" + phoneNumber));
                    if (intentPhone.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intentPhone);
                        }


            }
        });


        //Implementing the functionality for the "Buy me" button - how to successfully decrease the no of products
        //stored on database
        String currentQuantity = cursor.getString(quantityColumnIndex);
        final int quantityIntCurrent = Integer.valueOf(currentQuantity);

        //Getting the ID of the product the user just bought
        final int productId = cursor.getInt(cursor.getColumnIndex(DKSNContract.DKSNEntry._ID));

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityInt = Integer.valueOf(quantity);
                Log.i("EditActivity", "Quantity is " + quantity + " Input quantity is " + inputQuan);
                if (quantityInt <= 0) {
                    Toast.makeText(context, context.getString(R.string.out_of_stock_memo), Toast.LENGTH_SHORT).show();
                    quantityBought = 0;
                } else {
                    quantityBought = 1;
                    Toast.makeText(context, "Add " + quantityBought + " item(s) to basket? \nClick OK to confirm purchase", Toast.LENGTH_LONG).show();
                    saleQuantity.setVisibility(View.VISIBLE);
                    confirmation.setVisibility(View.VISIBLE);
                }


            }

        });

        confirmation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newQuantity = quantityIntCurrent - quantityBought;
                Uri cookieQuantityUri = ContentUris.withAppendedId(DKSNContract.DKSNEntry.CONTENT_URI, productId);
                ContentValues contentValues = new ContentValues();
                contentValues.put(DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY, newQuantity);
                context.getContentResolver().update(cookieQuantityUri, contentValues, null, null);
                Toast.makeText(context, "You have " + quantityBought + " item(s) in your basket.", Toast.LENGTH_SHORT).show();

            }
        });


        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantityInt = Integer.valueOf(quantity);
                Log.i("EditActivity", "Quantity is " + quantity + " Input quantity is " + inputQuan);
                if (quantityInt <= 0){
                    Toast.makeText(context, context.getString(R.string.out_of_stock_memo), Toast.LENGTH_SHORT).show();
                    quantityBought = 0;

                } else {
                    quantityBought++;

                    if (quantityBought > quantityInt) {
                        Toast.makeText(context, "Only " + quantityInt + " available for purchase", Toast.LENGTH_SHORT).show();
                        quantityBought = quantityInt;
                    } else if (quantityBought > 5) {
                        Toast.makeText(context, context.getString(R.string.max_purchase), Toast.LENGTH_SHORT).show();
                        quantityBought = 5;
                    } else {
                        Toast.makeText(context, "Add " + quantityBought + " item(s) to basket? Click OK to confirm purchase", Toast.LENGTH_LONG).show();

                    }
                    Log.i("EditActivity", "Quantity bought  is " + quantityBought + " Input quantity is " + inputQuan);
                }


            }
        });

        decrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                quantityBought--;

                if (quantityBought <= 1) {
                    Toast.makeText(context, context.getString(R.string.add_item), Toast.LENGTH_SHORT).show();
                    quantityBought = 1;
                } else {
                    Toast.makeText(context, "Add " + quantityBought + " item(s) to basket? Click OK to confirm purchase", Toast.LENGTH_LONG).show();

                }


            }
        });
    }


}




