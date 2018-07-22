package Contract;

import android.provider.BaseColumns;

/**
 * Created by ayoawotunde on 20/07/2018.
 */

public class DKSNContract {
    public class DKSNEntry implements BaseColumns {
        public final static String TABLE_NAME = "dksn";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_DKSN_PRODUCT_NAME = "product";
        public final static String COLUMN_DKSN_PRICE = "price";
        public final static String COLUMN_DKSN_QUANTITY = "quantity";
        public final static String COLUMN_DKSN_SUPPLIER_NAME = "supplier";
        public final static String COLUMN_DKSN_SUPPLIER_NO = "phone";
    }
}
