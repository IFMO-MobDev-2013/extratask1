package com.example.extratask;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;


public class PictureActivity extends Activity {

    private DataBase db;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture);

        db = new DataBase(getApplicationContext());

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        int id = getIntent().getIntExtra("id", 0);

        Image i = db.getImage(id);

        imageView.setImageBitmap(i.getBitmap());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }
}