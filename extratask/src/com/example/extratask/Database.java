package com.example.extratask;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.BaseAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: mv
 * Date: 18.01.14
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */
public class Database {

    public static final String KEY_ID = "_id";
    public static final String KEY_PICTURE = "picture";
    public static final String TABLE_NAME = "popular_pictures";
    public static final int ID_COLUMN = 0;
    public static final int PICTURE_COLUMN = 1;

    public static final String CREATE_TABLE = "CREATE_TABLE " + TABLE_NAME + " (" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_PICTURE + " BLOB NOT NULL);";

    private Cursor cursor;
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    private Context context;


    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(sqLiteDatabase);
        }
    }
}
