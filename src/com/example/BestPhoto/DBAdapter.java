package com.example.BestPhoto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class DBAdapter {

    public static final String TABLE_ID = "_id";
    private static final String DATABASE_NAME = "images";
    private static final int DATABASE_VERSION = 3;
    public static final String TABLE_NAME = "image_table";
    public static final String BITMAP = "bitmap";
    public static final String POSITION = "position";
    private final int IMAGECOUNT = 20;



    private static final String SQL_CREATE_ENTRIES = "create table "
            + TABLE_NAME + " ("
            + TABLE_ID + " integer primary key autoincrement, "
            + POSITION + " integer not null, "
            + BITMAP + " BLOB not null ); ";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
            + TABLE_NAME;

    private final Context mcontext;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;


    public DBAdapter(Context context) {
        this.mcontext = context;
        DBHelper = new DatabaseHelper(mcontext);
        db = DBHelper.getWritableDatabase();
    }

    public void saveBitmaps(Bitmap[] bmp) {
        for (int i = 0; i < bmp.length; i++) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp[i].compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] bytes = stream.toByteArray();
            ContentValues cv = new ContentValues();
            cv.put(BITMAP, bytes); cv.put(POSITION, i);
            db.insert(TABLE_NAME, null, cv);
        }
    }

    public Bitmap[] getImages() {
        Cursor cursor = getAllData();
        Bitmap[] bmp = new Bitmap[IMAGECOUNT];
        int size = -1;
        byte[] bb;
        while (cursor.moveToNext()) {
            size++;
            bb = cursor.getBlob(cursor.getColumnIndex(BITMAP));
            bmp[size] = BitmapFactory.decodeByteArray(bb, 0 , bb.length);
        }
        return bmp;
    }

    public Bitmap getImageById(int id) {
        Cursor cursor = db.query(TABLE_NAME, null, POSITION + " = ?", new String[]{String.valueOf(id)}, null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() <= 0) return null;
        byte[] bb = cursor.getBlob(cursor.getColumnIndex(BITMAP));
        return BitmapFactory.decodeByteArray(bb, 0, bb.length);
    }

    public Cursor getAllData() {
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void deleteAll() {
        db.execSQL(SQL_DELETE_ENTRIES);
        db.execSQL(SQL_CREATE_ENTRIES);
    }




    private class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
    }
}