package com.blumonk.TopTwenty;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by blumonk on 1/19/14.
 */
public class DbAdapter {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private final Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "top20pics";

    public static final String TABLE_PICS = "pictures";
    public static final String KEY_ID = "_id";
    public static final String KEY_PIC = "pic";

    private static final String INIT_PICS = "CREATE TABLE " + TABLE_PICS + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_PIC + " BLOB)";

    private static class DbHelper extends SQLiteOpenHelper {

        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(INIT_PICS);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICS);
            onCreate(db);
        }

    }

    public DbAdapter(Context context) {
        this.context = context;
    }

    public DbAdapter open() {
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        db.close();
        dbHelper.close();
    }

    public ArrayList<Bitmap> getAllPics() {
        ArrayList<Bitmap> pics = new ArrayList<Bitmap>();
        Cursor cursor = db.query(TABLE_PICS, new String[] {KEY_ID, KEY_PIC},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                byte[] rawData = cursor.getBlob((cursor.getColumnIndex(KEY_PIC)));
                pics.add(BitmapFactory.decodeByteArray(rawData, 0, rawData.length));
            } while (cursor.moveToNext());
        }
        return pics;
    }

    public Bitmap getPicById(int id) {
        Bitmap bitmap = null;
        Cursor cursor = db.query(TABLE_PICS, new String[]{KEY_ID, KEY_PIC}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        byte[] rawData = cursor.getBlob((cursor.getColumnIndex(KEY_PIC)));
        bitmap = BitmapFactory.decodeByteArray(rawData, 0, rawData.length);
        return bitmap;
    }

    public void addPic(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] rawData = stream.toByteArray();
        ContentValues values = new ContentValues();
        values.put(KEY_PIC, rawData);
        db.insert(TABLE_PICS, null, values);
    }

    public void deletePics() {
        db.delete(TABLE_PICS, null, null);
    }

}
