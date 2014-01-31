package com.mobdev.top20yandexphotos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class FullscreenImageActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreen_image);
		Intent intent = getIntent();
		Bitmap bitmap = MainActivity.bitmaps.get(intent.getIntExtra(MainActivity.image_string, 0));
		ImageView img = (ImageView)findViewById(R.id.img);
		img.setImageBitmap(bitmap);
	}
}
