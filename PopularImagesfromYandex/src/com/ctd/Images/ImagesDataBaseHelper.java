package com.ctd.Images;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ImagesDataBaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;

    public static final String _ID = "_id";
    public static final String DATABASE_NAME = "images";
    public static final String URL = "url";
    public static final String PATH = "path";


    public ImagesDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createDatabase());
    }

    String createDatabase() {
        String temp = "CREATE TABLE " + DATABASE_NAME
                + " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + PATH+ " TEXT," + URL + " TEXT);";
        return temp;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL(dropDataBase());
            onCreate(db);
        }
    }

    String dropDataBase() {
        return "DROP TABLE IF EXISTS " + DATABASE_NAME;
    }

}
