package com.mobdev.top20yandexphotos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PhotosDB extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "photosDB.db";
	public static final String DATABASE_TABLE = "Photos";
	private static final int DATABASE_VERSION = 1;
	
	public static final String KEY_ID = "_id";
	
	public static final String PHOTO = "PHOTO";
	
	private static final String DATABASE_CREATE = "create table "
			+ DATABASE_TABLE + " (" + KEY_ID
			+ " integer primary key autoincrement, " + PHOTO + " blob);";
	
	private static final String DATABASE_CLEAR = "DROP TABLE IF IT EXISTS "
			+ DATABASE_NAME;
	
	public PhotosDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(DATABASE_CLEAR);
		onCreate(db);
	}

}
