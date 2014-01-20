package com.example.PhotosViewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDataBaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;

    public static final String _ID = "_id";
    public static final String DATABASE_NAME = "photodb";
    public static final String IMAGE = "image";
    public static final String MINI = "mini";
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";

    public static final String CREATE_DATABASE = "CREATE TABLE " + DATABASE_NAME
            + " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + HEIGHT + " INTEGER, " + WIDTH + " INTEGER, " + MINI + " BLOB, " + IMAGE + " BLOB);";

    public static final String DROP_DATABASE = "DROP TABLE IF EXISTS " + DATABASE_NAME;

    public MyDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.execSQL(DROP_DATABASE);
            onCreate(db);
        }
    }

}