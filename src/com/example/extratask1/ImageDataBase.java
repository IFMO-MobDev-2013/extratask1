package com.example.extratask1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: Дмитрий
 * Date: 18.01.14
 * Time: 17:34
 * To change this template use File | Settings | File Templates.
 */
public class ImageDataBase extends SQLiteOpenHelper implements BaseColumns{
    private static final String DATA_BASE_NAME = "image_database1.db";
    private static final String TABLE_NAME = "imageTable1";
    private static final String TITLE_ROW = "title";
    private static final String SMALL_WIDTH = "sWidth";
    private static final String SMALL_HEIGHT = "sHeight";
    private static final String SMALL_PIC_ROW = "smallPicture";

    private static final String BIG_PIC_LINK = "bigPicture";

    public String getDataBaseName(){
        return DATA_BASE_NAME;
    }
    public String getTableName(){
        return TABLE_NAME;
    }
    public String getTitleRow(){
        return TITLE_ROW;
    }
    public String getSmallWidth(){
        return SMALL_WIDTH;
    }
    public String getSmallHeight(){
        return SMALL_HEIGHT;
    }
    public String getSmallPicRow(){
        return SMALL_PIC_ROW;
    }

    public String getBigPicLink(){
        return BIG_PIC_LINK;
    }


    public static final String SQL_CREATE_QUERY = "CREATE TABLE " + TABLE_NAME +
            " (" + ImageDataBase._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE_ROW + " TEXT, " +
            SMALL_WIDTH + " INTEGER, " + SMALL_HEIGHT + " INTEGER, " + SMALL_PIC_ROW + " BLOB, " +
            BIG_PIC_LINK + " TEXT);";

    public static final String SQL_DELETE_QUERY = "DROP TABLE IF EXISTS " + TABLE_NAME;


    public ImageDataBase(Context context){
        super(context, DATA_BASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int old, int nw){
        db.execSQL(SQL_DELETE_QUERY);
        onCreate(db);
    }

}
