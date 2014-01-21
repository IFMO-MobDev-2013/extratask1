package com.example.popularphotos;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

public class PicActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image);	
		((ImageView)findViewById(R.id.pic)).setImageBitmap(MainActivity.bm[getIntent().getIntExtra("PICTURE",0)]);
	}
}
