package com.example.kontrol.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.kontrol.inventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by Kontrol on 4/19/2017.
 */

public class ItemsDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "inventory.db";

    public ItemsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_ENTRIES_DELETE);
        onCreate(db);
    }

    private static final String SQL_CREATE_ENTRIES =    "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
            ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
            ItemEntry.COLUMN_ITEM_PRICE + " INTEGER NOT NULL, " +
            ItemEntry.COLUMN_ITEM_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
            ItemEntry.COLUMN_ITEM_SUPPLIER + " TEXT NOT NULL )";

    private static final String SQL_ENTRIES_DELETE = "DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME;
}
