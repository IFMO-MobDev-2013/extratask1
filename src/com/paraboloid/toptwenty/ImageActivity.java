package com.paraboloid.toptwenty;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageActivity extends Activity {
	public static final String PIC = "PIC";

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic);
        imageView = (ImageView) findViewById(R.id.thepicture);
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra(PIC);
        imageView.setImageBitmap(bitmap);
    }
}
