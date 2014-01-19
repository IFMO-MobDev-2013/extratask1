package com.example.javlon.extratask1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: javlon
 * Date: 19.01.14
 * Time: 23:21
 * To change this template use File | Settings | File Templates.
 */
public class ImageShower extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.image_shower);

        ImageView imageView = (ImageView) findViewById(R.id.image);
        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("result");
        imageView.setImageBitmap(bitmap);
    }
}
