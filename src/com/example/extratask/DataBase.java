package com.example.extratask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DataBase extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "flickr";

    private static final String TABLE_NAME = "images";

    private static final String KEY_ID = "id";
    private static final String KEY_IMAGE = "image";
    private static final String KEY_CREATED_AT = "created_at";

    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_IMAGE + " BLOB," + KEY_CREATED_AT
            + " DATETIME" + ")";

    public DataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    public long insertImage(byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IMAGE, image);
        values.put(KEY_CREATED_AT, getDateTime());

        long todo_id = db.insert(TABLE_NAME, null, values);

        return todo_id;
    }

    public void getAllImages(List<Image> images) {
        images.clear();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_CREATED_AT;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                byte[] b = c.getBlob((c.getColumnIndex(KEY_IMAGE)));
                Image i = new Image(b);
                i.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                images.add(i);
            } while (c.moveToNext());
        }

        c.close();
    }

    public Image getImage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                + KEY_ID + " = " + id;

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Image i = new Image((c.getBlob(c.getColumnIndex(KEY_IMAGE))));
        i.setId(c.getInt(c.getColumnIndex(KEY_ID)));

        c.close();

        return i;
    }

    public void deleteAllImages() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, null, null);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
