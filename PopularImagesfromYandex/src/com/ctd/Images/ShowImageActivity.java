package com.ctd.Images;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class ShowImageActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_activity);
        Intent intent = getIntent();
        String path = intent.getStringExtra(ImagesDataBaseHelper.PATH);
        ImageView imageView = (ImageView) findViewById(R.id.ivBig);
        try {
            imageView.setImageURI(Uri.fromFile(new File(getApplicationContext().getCacheDir(), path)));
        } catch (Exception e) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
        }

    }
}