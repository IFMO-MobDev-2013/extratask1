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
    private final String URL = "url";
    private final String WIFI = "WIFI";
    private final String MOBILE = "MOBILE";

    ImageView image, i2;
    String url;
    DBAdapter db;
    Bitmap imageBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.main2_land);
        } else {
            setContentView(R.layout.main2);
        }
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar));
        url = getIntent().getStringExtra(URL);
        db = new DBAdapter(this);
        setImage();
    }

    private void setImage() {
        image = (ImageView) findViewById(R.id.imageView);
        if (checkInternetConnection()) {
            new ImageDownloader(url).execute();
        } else image.setImageBitmap(db.getImageByUrl(url));
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

    private boolean checkInternetConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase(WIFI))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase(MOBILE))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    class ImageDownloader extends AsyncTask<String, Void, Void> {
        ProgressDialog dialog;
        String imageUrl;

        public ImageDownloader(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(OneImageView.this);
            dialog.setMessage(getResources().getString(R.string.downloading));
            dialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                    java.net.URL url = new java.net.URL(imageUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    imageBitmap = BitmapFactory.decodeStream(input);
            } catch(Exception e) {
            }
            return null;
        }
        protected void onPostExecute(Void v) {
            if (dialog != null) {
                dialog.dismiss();
            }
            image.setImageBitmap(imageBitmap);
        }
    }
}
