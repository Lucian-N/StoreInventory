package com.application.example.storeinventory.data;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
    //Db name + version
    public static final String DATABASE_NAME = "products.db";
    public static final int DATABASE_VERSION = 1;

    // Constructor
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    // Table create statement
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create constant String that contains SQL statement to create db
        String SQL_CREATE_TABLE = "CREATE TABLE " + StoreContract.StoreEntry.TABLE_NAME + " (" +
                StoreContract.StoreEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                StoreContract.StoreEntry.COLUMN_NAME + " TEXT NOT NULL," +
                StoreContract.StoreEntry.COLUMN_PRICE + " REAL NOT NULL," +
                StoreContract.StoreEntry.COLUMN_QUANTITY +" INTEGER NOT NULL DEFAULT 0," +
                StoreContract.StoreEntry.COLUMN_SUP_NAME +" INTEGER NOT NULL DEFAULT 0," +
                StoreContract.StoreEntry.COLUMN_SUP_PHONE + " TEXT NOT NULL );";
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVers, int newVers) {
        // version 1 of db
    }
}
