package com.gmail.mazinva.PopularPhotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class SQLiteAccessor {

    private MyDBHelper helper;
    private SQLiteDatabase database;
    private Context context;
    private boolean isOpened;

    private static final String DATABASE_NAME = "MyPopPhotosDatabase";

    private static final String DATABASE_TABLE_IMAGES = "images";

    public static final String KEY_ID_IMAGES = "_id";
    public static final String KEY_URL_IMAGES = "url";
    public static final String KEY_BITMAP_IMAGES = "bitmap";

    private static final int DATABASE_VERSION = 1;

    private static final String[] DATABASE_CREATE =
            {"CREATE TABLE " + DATABASE_TABLE_IMAGES +
                    " (" + KEY_ID_IMAGES + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_URL_IMAGES + " TEXT NOT NULL, " + KEY_BITMAP_IMAGES + " BLOB NOT NULL);"
            };

    public SQLiteAccessor(Context context) {
        this.context = context;
        isOpened = false;
    }

    public void open() {
        helper = new MyDBHelper(context);
        database = helper.getWritableDatabase();
        isOpened = true;
    }

    public void close() {
        isOpened = false;
        helper.close();
    }

    public Cursor fetchAll() throws MySQLiteException {
        if(!isOpened){
            throw new MySQLiteException(context.getString(R.string.dbIsNotOpened));
        }
        return database.query(DATABASE_TABLE_IMAGES, null, null, null, null, null, null, null);
    }

    public Cursor fetchImageById(int id) throws MySQLiteException {
        if(!isOpened){
            throw new MySQLiteException(context.getString(R.string.dbIsNotOpened));
        }
        return database.query(DATABASE_TABLE_IMAGES, null, KEY_ID_IMAGES + "=" + id, null, null, null, null);
    }

    public void insertImage(String url, Bitmap bitmap) throws MySQLiteException {
        if(!isOpened){
            throw new MySQLiteException(context.getString(R.string.dbIsNotOpened));
        }
        byte[] data = getBitmapAsByteArray(bitmap);

        ContentValues values = new ContentValues();
        values.put(KEY_URL_IMAGES, url);
        values.put(KEY_BITMAP_IMAGES, data);
        database.insert(DATABASE_TABLE_IMAGES, null, values);
    }

    /*public void updateImage(int id, String url, Bitmap bitmap) throws MySQLiteException {
        if(!isOpened){
            throw new MySQLiteException(context.getString(R.string.dbIsNotOpened));
        }
        byte[] data = getBitmapAsByteArray(bitmap);

        ContentValues values = new ContentValues();
        values.put(KEY_URL_IMAGES, url);
        values.put(KEY_BITMAP_IMAGES, data);
        database.update(DATABASE_TABLE_IMAGES, values, KEY_ID_IMAGES + "=" + id, null);
    }*/

    public void deleteAll() throws MySQLiteException {
        if(!isOpened){
            throw new MySQLiteException(context.getString(R.string.dbIsNotOpened));
        }
        database.delete(DATABASE_TABLE_IMAGES, null, null);
        database.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + DATABASE_TABLE_IMAGES + "'");
    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private static class MyDBHelper extends SQLiteOpenHelper {

        MyDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            for(int i = 0; i < DATABASE_CREATE.length; i++){
                db.execSQL(DATABASE_CREATE[i]);
                Log.d("TABLE CREATED", DATABASE_CREATE[i]);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_IMAGES);
            onCreate(db);
        }
    }
}

