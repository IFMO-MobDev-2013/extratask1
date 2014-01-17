package com.example.extratask;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;


public class PictureActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);

        DataBase db = new DataBase(getApplicationContext());

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        int id = getIntent().getIntExtra("id", 0);

        Image i = db.getImage(id);

        imageView.setImageBitmap(i.getBitmap());
    }
}