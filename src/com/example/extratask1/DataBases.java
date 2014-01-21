package com.example.extratask1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: PWR
 * Date: 19.01.14
 * Time: 18:47
 * To change this template use File | Settings | File Templates.
 */
public class DataBases extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "yandex_photo";
    private static final String TABLE_NAME = "images";
    private static final String KEY_ID = "id";
    private static final String KEY_PICTURE = "picture";
    private static final String KEY_TIME = "time";

    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_PICTURE + " BLOB," + KEY_TIME
            + " DATETIME" + ")";

    public DataBases(Context applicationContext)
    {
        super(applicationContext, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
    {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(database);
    }

    public long newPicture(byte[] picture)
    {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues c_values = new ContentValues();
        long id;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        c_values.put(KEY_PICTURE, picture);
        c_values.put(KEY_TIME, sdf.format(date));
        id = database.insert(TABLE_NAME, null, c_values);

        database.close();
        return id;
    }

    public void newPictures(List<MyPictures> pictures)
    {
        String select = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + KEY_TIME;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(select, null);

        pictures.clear();

        if(cursor.moveToFirst())
        {
            do
            {
                byte[] bytes = cursor.getBlob(cursor.getColumnIndex(KEY_PICTURE));
                MyPictures picture = new MyPictures(bytes);
                picture.newID(cursor.getInt((cursor.getColumnIndex(KEY_ID))));
                pictures.add(picture);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();

    }

    public MyPictures getPicture(int id)
    {

        SQLiteDatabase database = this.getReadableDatabase();
        String select = "SELECT  * FROM " + TABLE_NAME + " WHERE "
                + KEY_ID + " = " + id;
        Cursor cursor = database.rawQuery(select, null);

        if (cursor != null)
            cursor.moveToFirst();
        MyPictures picture = new MyPictures((cursor.getBlob(cursor.getColumnIndex(KEY_PICTURE))));
        picture.newID(cursor.getInt((cursor.getColumnIndex(KEY_ID))));

        cursor.close();
        database.close();

        return picture;

    }

    public void delPictures()
    {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_NAME, null, null);

        database.close();
    }

}
