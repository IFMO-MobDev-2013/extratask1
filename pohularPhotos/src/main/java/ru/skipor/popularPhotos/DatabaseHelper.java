package ru.skipor.popularPhotos;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper {

    private static DatabaseHelper instance = null;

    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public static final String KEY_ROWID = "_id";
    public static final String KEY_SMALL_IMAGE_NAME = "small_image";
    public static final String KEY_LARGE_IMAGE_URL = "large_image";

    private static final String DATABASE_NAME = "data";

    private static final String TAG = "DbHelper";
    private final LocalDatabaseHelper myLocalDatabaseHelper;
    private SQLiteDatabase myDatabase;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table " + DATABASE_NAME + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_SMALL_IMAGE_NAME + " text not null," + KEY_LARGE_IMAGE_URL + " text not null" +
                    ");";

    private static final int DATABASE_VERSION = 1;

    private int databaseUsers;


    private static class LocalDatabaseHelper extends SQLiteOpenHelper {


        public LocalDatabaseHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
            onCreate(db);
        }
    }


    private DatabaseHelper(Context context) {
        myLocalDatabaseHelper = new LocalDatabaseHelper(context);
        databaseUsers = 0;

    }


    synchronized public void open() throws SQLException {

        if (databaseUsers == 0) {
            myDatabase = myLocalDatabaseHelper.getWritableDatabase();
        }
        databaseUsers++;


    }


    synchronized public void close() {
        databaseUsers--;
        if (databaseUsers == 0) {
            myLocalDatabaseHelper.close();
            myDatabase = null;
        }
    }

    public void recreateTable() {
        myDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        myDatabase.execSQL(DATABASE_CREATE);
    }


    public long create(String smallImageName, String largeImageUrl) {

        ContentValues args = new ContentValues();
        args.put(KEY_LARGE_IMAGE_URL, largeImageUrl);
        args.put(KEY_SMALL_IMAGE_NAME, smallImageName);

        return myDatabase.insert(DATABASE_NAME, null, args);
    }


    /**
     * Delete the feed with the given rowId
     *
     * @param rowId id of feed to delete
     * @return true if deleted, false otherwise
     */
    public void delete(long rowId) {

        myDatabase.delete(DATABASE_NAME, KEY_ROWID + "=" + rowId, null);
    }

    /**
     * Return a Cursor over the list of all feeds in the database
     *
     * @return Cursor over all feeds
     */
    public Cursor fetchAll() {


        return myDatabase.query(DATABASE_NAME, new String[]{KEY_ROWID,
                KEY_SMALL_IMAGE_NAME, KEY_LARGE_IMAGE_URL}, null, null, null, null, null);
    }


    /**
     * Return a Cursor positioned at the feed that matches the given rowId
     *
     * @param rowId id of feed to retrieve
     * @return Cursor positioned to matching feed, if found
     * @throws android.database.SQLException if feed could not be found/retrieved
     */
    public Cursor fetch(long rowId) throws SQLException {

        Cursor mCursor =

                myDatabase.query(true, DATABASE_NAME, new String[]{KEY_ROWID,
                        KEY_SMALL_IMAGE_NAME, KEY_LARGE_IMAGE_URL}, KEY_ROWID + "=" + rowId, null,
                        null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }

        return mCursor;

    }


    public boolean update(long rowId, String smallImageName, String largeImageName) {

        ContentValues args = new ContentValues();
        args.put(KEY_LARGE_IMAGE_URL, largeImageName);
        args.put(KEY_SMALL_IMAGE_NAME, smallImageName);

        return myDatabase.update(DATABASE_NAME, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
