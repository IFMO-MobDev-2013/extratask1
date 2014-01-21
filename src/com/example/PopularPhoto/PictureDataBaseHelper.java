package com.example.PopularPhoto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class PictureDataBaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public static final String DATA_BASE_NAME = "image.db";
    public static final String TABLE_NAME = "mainTable";
    public static final String BIG_IMAGE_NAME = "bigName";
    public static final String SMALL_IMAGE_NAME = "smallName";
    public static final String TITLE = "title";
    public static final int DATA_BASE_VERSION = 1;

    public static final String CREATE = "CREATE TABLE " + TABLE_NAME +
            " (" + PictureDataBaseHelper._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " +
            BIG_IMAGE_NAME + " TEXT, " + SMALL_IMAGE_NAME + " TEXT);";
    public static final String DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    PictureDataBaseHelper(Context context) {
        super(context, DATA_BASE_NAME, null, DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE);
        onCreate(db);
    }
}
