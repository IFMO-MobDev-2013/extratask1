package com.example.photo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.Display;
import android.view.DragEvent;
import android.view.View;
import android.view.View.OnDragListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class big_photo extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.big);
		int x = getIntent().getExtras().getInt("key");
		ImageView imageView = (ImageView)findViewById(R.id.imageView1);
			
		imageView.setImageBitmap(MainActivity.image_Bitmap[x]);
		
		
		
	}
		
}
