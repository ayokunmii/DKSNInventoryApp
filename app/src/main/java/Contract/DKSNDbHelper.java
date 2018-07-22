package Contract;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ayoawotunde on 20/07/2018.
 */

public class DKSNDbHelper extends SQLiteOpenHelper {
    //Create constants for database name and version. Version usually starts at 1
    private static final String DATABASE_NAME = "dksn.db";
    private static final int DATABASE_VERSION = 2;

    public DKSNDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_DKSN_TABLE_SQL = "CREATE TABLE " + DKSNContract.DKSNEntry.TABLE_NAME +
                "(" + DKSNContract.DKSNEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DKSNContract.DKSNEntry.COLUMN_DKSN_PRODUCT_NAME + " TEXT NOT NULL, " +
                DKSNContract.DKSNEntry.COLUMN_DKSN_PRICE + " TEXT NOT NULL, " +
                DKSNContract.DKSNEntry.COLUMN_DKSN_QUANTITY + " INTEGER NOT NULL, " +
                DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                DKSNContract.DKSNEntry.COLUMN_DKSN_SUPPLIER_NO + " TEXT " + ")";

        //execute SQL statement in java
        sqLiteDatabase.execSQL(CREATE_DKSN_TABLE_SQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
