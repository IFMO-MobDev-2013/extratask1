package com.android.example.dronov;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import com.android.example.dronov.database.YandexPhotos;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 19.01.14
 * Time: 21:58
 * To change this template use File | Settings | File Templates.
 */
public class ImageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.image_view);

        Intent intent = getIntent();
        int index = intent.getIntExtra("bitmap", 0);
        YandexPhotos database = new YandexPhotos(this);
        database.open();
        Bitmap bitmap = database.getPicture(index);
        database.close();
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);
    }
}
