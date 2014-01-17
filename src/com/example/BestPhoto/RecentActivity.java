package com.example.BestPhoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class RecentActivity extends Activity {
    private final String WIFI = "WIFI";
    private final String MOBILE = "MOBILE";

    private final String QUALITY = "M";
    private final String URL = "url";
    private final int IMAGECOUNT = 20;
    GridView grid;
    String[] urls;
    Bitmap[] images;
    DBAdapter db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.main_land);
        } else {
            setContentView(R.layout.main);
        }
        setPadding();
        db = new DBAdapter(this);
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.bar));
        if (checkInternetConnection()) getImages();
        else {
            urls = db.getUrls();
            images = db.getImages();
            setAdapter(images);
        }
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
    private void getImages() {
        urls = null;
        try {
             urls = new ImageURLs(getResources().getString(R.string.url), this).execute().get();
        } catch (Exception e) {  }

        for (int i = 0; i < IMAGECOUNT; i++) {
            String url = urls[i];
            int pos = url.length() - 1;
            while (url.charAt(pos) != '_') pos--;
            url = url.substring(0, pos + 1) + QUALITY;
            urls[i] = url;
        }
        new ImageDownloader(urls).execute();


    }

    private void setAdapter(Bitmap[] bitmap) {
        if (checkInternetConnection()) saveBitmap();
        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        int size;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            size = 1;
        } else {
            size = 2;
        }
        Bitmap[] newBitmap = copyFromBitmap(bitmap);

        grid.setAdapter(new ImageAdapter(this, newBitmap, size, true, dimension.widthPixels));
    }

    private Bitmap[] copyFromBitmap(Bitmap[] bm) {
        int ssize = 0;
        while (bm[ssize] != null) {
            ssize++;
            if (ssize == bm.length) break;
        }
        Bitmap[] result = new Bitmap[ssize];
        for (int i = 0; i < ssize; i++) {
            result[i] = bm[i];
        }
        return result;
    }

    private void setItemClick() {
        grid.setOnItemClickListener( new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
                Intent intent = new Intent(RecentActivity.this, OneImageView.class);
                intent.putExtra(URL, urls[position]);
                startActivity(intent);
            }
        });
    }
    private void setPadding() {



        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            grid = (GridView) findViewById(R.id.grid);

            int left = dimension.widthPixels / 10;
            grid.setHorizontalSpacing(left);
            grid.setVerticalSpacing(left);
            grid.setPadding(left, 0, left, 0);
        } else {
            grid = (GridView) findViewById(R.id.grid);

            int left = dimension.widthPixels / 25;
            grid.setHorizontalSpacing(left);
            grid.setVerticalSpacing(left);
            grid.setPadding(left, 0, left, 0);
        }
        setItemClick();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.main_land);
        } else {
            setContentView(R.layout.main);
        }
        setPadding();
        setAdapter(images);
        setItemClick();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void saveBitmap() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                db.deleteAll();
                db.saveBitmaps(images, urls);
            }
        });
        thread.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!checkInternetConnection()) {
            Toast.makeText(this, getResources().getString(R.string.nointernet), Toast.LENGTH_SHORT).show();
            return  false;
        }
        if (item.getItemId() == R.id.refresh) {
            getImages();
        }
        return true;
    }

    class ImageDownloader extends AsyncTask<String, Void, Bitmap[]> {
        ProgressDialog dialog;
        String[] imageUrls;

        public ImageDownloader(String[] imageUrls) {
            this.imageUrls = imageUrls;
            images = new Bitmap[imageUrls.length];
        }

        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(RecentActivity.this);
            dialog.setMessage(getResources().getString(R.string.downloading));
            dialog.show();
        }

        @Override
        protected Bitmap[] doInBackground(String... params) {
            try {
                for (int i = 0; i < imageUrls.length; i++) {
                    java.net.URL url = new URL(imageUrls[i]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    images[i] = BitmapFactory.decodeStream(input);
                }
                return images;
            } catch(Exception e) {
            }
            return null;
        }
        protected void onPostExecute(Bitmap[] images) {
            if (dialog != null) {
                dialog.dismiss();
            }
            setAdapter(images);
        }
        }

}
