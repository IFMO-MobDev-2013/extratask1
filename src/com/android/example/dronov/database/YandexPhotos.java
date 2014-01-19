package com.android.example.dronov.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.android.example.dronov.Picture;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 19.01.14
 * Time: 6:52
 * To change this template use File | Settings | File Templates.
 */
public class YandexPhotos {

    private static final String DB_NAME = "yandex";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "yandex_photos";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_WIDTH = "width";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_BIG_IMAGE = "big";

    public static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_WIDTH + " integer, " +
                    COLUMN_HEIGHT + " integer," +
                    COLUMN_BIG_IMAGE + " blob" +
                    ");";

    private final Context context;

    private DBHelper mDBHelper;
    private SQLiteDatabase mDB;

    public YandexPhotos(Context context) {
        this.context = context;
    }

    public void open() {
        mDBHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    public void close() {
        if (mDBHelper!=null) mDBHelper.close();
    }


    public Cursor getAllData() {
        return mDB.query(DB_TABLE, null, null, null, null, null, null);
    }

    public void addChannel(Picture picture) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_WIDTH, picture.getWidth());
        cv.put(COLUMN_HEIGHT, picture.getHeight());
        cv.put(COLUMN_BIG_IMAGE, decodeBitmap(picture.getBigImage()));
        mDB.insert(DB_TABLE, null, cv);
    }

    private byte[] decodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public List<Bitmap> getAllPicturesData() {
        List<Bitmap> list = new ArrayList<Bitmap>();
        Cursor cursor = getAllData();
        while (cursor.moveToNext()) {
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_BIG_IMAGE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            list.add(bitmap);
        }
        return list;
    }

    private int[] byteToInt(byte[] current) {
        IntBuffer intBuffer = ByteBuffer.wrap(current).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
        int [] array = new int[intBuffer.remaining()];
        intBuffer.get(array);
        return array;
    }

    public void deleteAllChannels() {
        mDB.delete(DB_TABLE, null, null);
    }
}
