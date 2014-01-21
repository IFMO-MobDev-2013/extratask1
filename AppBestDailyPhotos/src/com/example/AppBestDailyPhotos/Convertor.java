package com.example.AppBestDailyPhotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;

public class Convertor {
	public static byte[] getBytesOnBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
		return stream.toByteArray();
	}

	public static Bitmap getBitmapOnBytes(byte[] image) {
		return BitmapFactory.decodeByteArray(image, 0, image.length);
	}
}
