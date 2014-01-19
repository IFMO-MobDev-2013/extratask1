package com.example.BestPhoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class OneImageView extends Activity {
    private final String POS = "pos";

    ImageView image;
    int pos;
    DBAdapter db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.main2_land);
        } else {
            setContentView(R.layout.main2);
        }
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar));
        pos = getIntent().getIntExtra(POS, 1);
        db = new DBAdapter(this);
        setImage();
    }

    private void setImage() {
        image = (ImageView) findViewById(R.id.imageView);
        image.setImageBitmap(db.getImageById(pos));
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.main2_land);
        } else {
            setContentView(R.layout.main2);
        }
        setImage();
    }

}