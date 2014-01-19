package local.firespace.RecentPhotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class ImagesDatabase  {

	public static final String KEY_ID = "id";
	public static final String KEY_IMAGE_ID = "im_id";
	public static final String KEY_IMAGE = "image";

	private static final String DATABASE_NAME = "imagesdb";
	private static final Integer DATABASE_VERSION = 1;
	private static final String CREATE_TABLE = "CREATE TABLE " + DATABASE_NAME + " (" + KEY_ID +
			" INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_IMAGE_ID + " INTEGER NOT NULL UNIQUE, " + KEY_IMAGE + " BLOB NOT NULL);";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + DATABASE_NAME;

	private DBHelper dbHelper;
	private SQLiteDatabase database;

	private class DBHelper extends SQLiteOpenHelper {

		DBHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(DROP_TABLE);
			onCreate(db);
		}
	}

	public void reset() {
		dbHelper.onUpgrade(database, 1, 1);
	}

	public ImagesDatabase(Context context) {
		dbHelper = new DBHelper(context);
	}

	public ImagesDatabase open() {
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}

	public void addImage(Bitmap image, Integer key) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, out);
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_IMAGE_ID, key);
		contentValues.put(KEY_IMAGE, out.toByteArray());
		database.insert(DATABASE_NAME, null, contentValues);
	}

	public Bitmap getImageByID(Integer key) {
		Cursor cursor = database.query(DATABASE_NAME, null, KEY_IMAGE_ID + " = ?", new String[] {key.toString()}, null, null, null);
		cursor.moveToFirst();
		if (cursor.getCount() < 1) {
			return null;
		} else {
			byte[] tData = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
			return BitmapFactory.decodeByteArray(tData, 0, tData.length);
		}
	}

	public Bitmap[] getImages() {
		Bitmap[] images = new Bitmap[PhotoDownloader.COUNT_PHOTOS];

		Cursor cursor = database.query(DATABASE_NAME, null, null, null, null, null, null);
		byte[] tData;
		int i = 0;
		while (cursor.moveToNext()) {
			tData = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
			images[i++] = BitmapFactory.decodeByteArray(tData, 0, tData.length);
		}
		if (i == 0) return null;
		return images;
	}

	public void addImages(Bitmap[] images) {
		for (int i = 0; i < images.length; i++) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			images[i].compress(Bitmap.CompressFormat.PNG, 100, out);
			ContentValues contentValues = new ContentValues();
			contentValues.put(KEY_IMAGE_ID, i);
			contentValues.put(KEY_IMAGE, out.toByteArray());
			database.insert(DATABASE_NAME, null, contentValues);
		}
	}
}
