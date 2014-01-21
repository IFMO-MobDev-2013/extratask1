package com.mikhov.ExtraFotki.dbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class DbAdapter {

    public static final String KEY_ROWID = "_id";
    public static final String KEY_PHOTO = "photo";
    private static final String DATABASE_TABLE = "photos";
    private Context context;
    private SQLiteDatabase database;
    private DbHelper dbHelper;
    Bitmap[] bm;

    public DbAdapter(Context context) {
        this.context = context;
        bm = new Bitmap[20];
    }

    public DbAdapter open() throws SQLException {
        dbHelper = new DbHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public boolean isNotEmpty() {
        Cursor cursor = getAllPhotos();
        return cursor.moveToNext();
    }

    public void close() {
        dbHelper.close();
    }

    public void drop() {
        database.execSQL("DROP TABLE IF EXISTS photos");
        database.execSQL("create table photos (_id integer primary key autoincrement, photo blob not null);");
    }

    public void addPhotos(Bitmap[] photos) {
        for (int i = 0; i < 20; i++) {
            ByteArrayOutputStream st = new ByteArrayOutputStream();
            ContentValues initialValues = new ContentValues();
            photos[i].compress(Bitmap.CompressFormat.PNG, 100, st);
            byte[] bm_b = st.toByteArray();
            initialValues.put(KEY_PHOTO, bm_b);
            database.insert(DATABASE_TABLE, null, initialValues);
        }
    }

    public Cursor getAllPhotos() {
        return database.query(DATABASE_TABLE, new String[] { KEY_ROWID, KEY_PHOTO }, null, null, null, null, null);
    }

    public Bitmap[] getPhotos() {
        Cursor cursor = getAllPhotos();
        byte[] bm_b;
        int i = -1;
        while (cursor.moveToNext()) {
            i++;
            bm_b = cursor.getBlob(cursor.getColumnIndex(KEY_PHOTO));
            bm[i] = BitmapFactory.decodeByteArray(bm_b, 0, bm_b.length);
        }
        return bm;
    }

    public Bitmap getPhoto(int id) {
        Cursor cursor = getAllPhotos();
        Bitmap ph = null;
        byte[] bm_b;
        int i = -1;
        while (cursor.moveToNext()) {
            i++;
            if (i == id) {
                bm_b = cursor.getBlob(cursor.getColumnIndex(KEY_PHOTO));
                ph = BitmapFactory.decodeByteArray(bm_b, 0, bm_b.length);
                break;
            }
        }
        return ph;
    }
}
