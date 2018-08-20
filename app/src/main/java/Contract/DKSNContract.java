package Contract;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ayoawotunde on 20/07/2018.
 */

public class DKSNContract {
    public static final String CONTENT_AUTHORITY = "com.example.android.inventoryapp";
    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "dksn";

    public static class DKSNEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);

        //The MIME type of the {@link #CONTENT_URI} for a list .
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_PRODUCTS;
        //The MIME type of the {@link #CONTENT_URI} for a single product.

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY +
                        "/" + PATH_PRODUCTS;


        public final static String TABLE_NAME = "dksn";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DKSN_PRODUCT_NAME = "product";
        public final static String COLUMN_DKSN_PRICE = "price";
        public final static String COLUMN_DKSN_QUANTITY = "quantity";
        public final static String COLUMN_DKSN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_DKSN_SUPPLIER_NO = "phone";
    }
}

