package ru.ifmo.mobdev.extra;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class SecondScreen extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.only_image);

        ImageView bigImage = (ImageView)findViewById(R.id.bigImageView);

        bigImage.setImageBitmap(DownloadData.currentImage);

    }
}