package com.example.AppBestDailyPhotos;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

public class ViewPhotoActivity extends Activity {
	private int width, height;

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		int newSize = Math.min(newHeight, newWidth);
		Matrix matrix = new Matrix();
		matrix.postScale(
				((float) newSize) / width,
				((float) newSize) / height
		);
		return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.view_photos);
		ImageView photoImageView = (ImageView) findViewById(R.id.imageView);
		int id = Integer.parseInt(getIntent().getExtras().getString("id"));
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
		height = metrics.heightPixels;
		SQLiteDatabase sqLiteDatabase;
		PhotosSQLiteOpenHelper openHelper = new PhotosSQLiteOpenHelper(this);
		sqLiteDatabase = openHelper.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.query(PhotosSQLiteOpenHelper.TABLE_NAME,null,null,null,null,null,null);
		cursor.moveToPosition(id);
		byte[] blob = cursor.getBlob(cursor.getColumnIndex(PhotosSQLiteOpenHelper.IMAGE));
		Bitmap image = Convertor.getBitmapOnBytes(blob);
		photoImageView.setImageBitmap(getResizedBitmap(image,
						height,
						width
				));


	}
}
