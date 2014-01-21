package com.blumonk.TopTwenty;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by blumonk on 1/19/14.
 */
public class ImageActivity extends Activity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic);
        imageView = (ImageView) findViewById(R.id.thepicture);
        Intent intent = getIntent();
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("pic");
        imageView.setImageBitmap(bitmap);
    }
}
