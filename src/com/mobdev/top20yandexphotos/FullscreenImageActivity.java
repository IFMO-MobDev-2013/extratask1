package com.mobdev.top20yandexphotos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class FullscreenImageActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fullscreen_image);
		((ImageView) findViewById(R.id.img))
				.setImageBitmap(MainActivity.bitmaps.get(getIntent()
						.getIntExtra(MainActivity.image_string, 0)));
	}
}
