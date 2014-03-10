package com.example.android;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class Image extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //To change body of overridden methods use File | Settings | File Templates.
        setContentView(R.layout.image);
        Intent intent = getIntent();
        int index = intent.getIntExtra("bitmap", 0);

        Database database = new Database(this);
        database.open();
        Bitmap bitmap = database.getPicture(index);
        database.close();
        ImageView imageView = (ImageView) findViewById(R.id.picture);
        imageView.setImageBitmap(bitmap);
    }
}
