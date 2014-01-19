package com.example.javlon.extratask1.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: javlon
 * Date: 19.01.14
 * Time: 21:53
 * To change this template use File | Settings | File Templates.
 */
public class Pictures {
    private static final String DB_NAME = "pictures";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "photos";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_IMAGE = "big";

    public static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_IMAGE + " blob" +
                    ");";

    private final Context context;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public Pictures(Context context) {
        this.context = context;
    }

    public void open() {
        mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }


    public Cursor getAll() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public void add(Bitmap bitmap) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_IMAGE, BitmapToByte(bitmap));
        mDB.insert(DB_TABLE, null, cv);
    }

    private byte[] BitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] array = stream.toByteArray();
        return array;
    }

    public ArrayList<Bitmap> getAllPictures() {
        ArrayList<Bitmap> list = new ArrayList<Bitmap>();
        Cursor cursor = getAll();
        while (cursor.moveToNext()) {
            byte[] pixels = cursor.getBlob(cursor.getColumnIndex(COLUMN_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(pixels, 0, pixels.length);
            list.add(bitmap);
        }
        return list;
    }

    public void deleteAll() {
        mDB.delete(DB_TABLE, null, null);
    }
}
