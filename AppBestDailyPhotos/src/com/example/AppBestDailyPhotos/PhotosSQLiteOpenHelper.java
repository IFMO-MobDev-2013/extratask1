package com.example.AppBestDailyPhotos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.AdapterView;


public class PhotosSQLiteOpenHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "database.db";
	public static final String TABLE_NAME = "table_image";
	public static final String IMAGE = "image";
	public static final int DATABASE_VERSION = 1;

	public PhotosSQLiteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ IMAGE + " BLOB);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE " + TABLE_NAME);
		onCreate(db);
	}
}
