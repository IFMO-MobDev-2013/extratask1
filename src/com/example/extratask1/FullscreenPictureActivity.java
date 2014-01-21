package com.example.extratask1;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: PWR
 * Date: 19.01.14
 * Time: 20:44
 * To change this template use File | Settings | File Templates.
 */
public class FullscreenPictureActivity extends Activity
{

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fsp);

        DataBases database = new DataBases(getApplicationContext());
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        int id = getIntent().getIntExtra("id", 0);
        MyPictures picture = database.getPicture(id);

        imageView.setImageBitmap(picture.getBitmap());
    }

}
