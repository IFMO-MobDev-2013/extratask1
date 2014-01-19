package md.zoidberg.android.yandexparser.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gfv on 19.01.14.
 */
public class ImagesDbHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private SQLiteDatabase db;

    public ImagesDbHelper(Context context) {
        super(context, "images", null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL("create table images (id integer primary key autoincrement, url string unique, creation integer");
    }

    public List<Image> getLastImages() {
        if (db == null) Log.wtf("IDBH", "db == null");
        Cursor cur = db.query("images", new String[] {"url", "creation"}, null, null, null, null, "creation desc", "20");
        List<Image> images = new ArrayList<Image>();

        if (cur == null) return images;

        for (int i = 0; i < cur.getCount(); i++) {
            images.add(new Image(cur.getString(0), new Date(Long.parseLong(cur.getString(1)))));
        }

        return images;
    }

    public int getCount() {
        Cursor cur = db.query("images", new String[]{"url", "creation"}, null, null, null, null, "creation desc", "20");
        if (cur == null) return 0;
        return cur.getCount();
    }

    public void addImage(Image image) {
        ContentValues values = new ContentValues();
        values.put("url", image.getUrl());
        values.put("last_seen", image.getLastUpdate().getTime());
        db.replace("image", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists images;");
        onCreate(db);
    }


}
