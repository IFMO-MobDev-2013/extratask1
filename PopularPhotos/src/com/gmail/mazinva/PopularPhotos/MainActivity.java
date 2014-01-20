package com.gmail.mazinva.PopularPhotos;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;


public class MainActivity extends Activity {

    public static final int NUMBER = 20;
    public static final String URL = "http://api-fotki.yandex.ru/api/recent/";
    public static final String MY_ACTION = "com.gmail.mazinva.ACTION";

    // Extras:
    public static final String UPDATE_ALL = "updateAll";
    public static final String DOWNLOAD_ALL = "downloadAll";
    public static final String IMAGE_ID = "imageId";
    
    SQLiteAccessor accessor;
    private boolean isPortraitOrientation = true;
    GridView gridView;
    private int width;
    Context context;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            boolean downloadAll = bundle.getBoolean(MainActivity.DOWNLOAD_ALL);
            boolean updateAll = bundle.getBoolean(MainActivity.UPDATE_ALL);
            if (downloadAll) {
               showImages();
            }
            if (updateAll) {
                showImages();
                Toast.makeText(getApplicationContext(), R.string.sucUpdateMsg, Toast.LENGTH_LONG).show();
            }
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context = this;

        gridView = (GridView) findViewById(R.id.imagesGrid);
        getOrientation();
        getDisplayWidth();
        setLayoutsAccordingOrientation();

        accessor = new SQLiteAccessor(this);
        accessor.open();

        downloadIfDBEmpty();

        (findViewById(R.id.updateButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()){
                    Toast.makeText(getApplicationContext(), R.string.startUpdateMsg, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, MyService.class);
                    intent.putExtra(MainActivity.UPDATE_ALL, true);
                    startService(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_LONG).show();
                }
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(context, SingleImageActivity.class);
                intent.putExtra(MainActivity.IMAGE_ID, (int) l);
                startActivity(intent);
            }
        });


    }

    private void downloadIfDBEmpty() {
        try {
            Cursor cursor = accessor.fetchAll();
            if (cursor.getCount() <= 0) {
                // Need to download pictures for the first time
                if (isNetworkAvailable()) {
                    Toast.makeText(getApplicationContext(), R.string.startDownloadMsg, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, MyService.class);
                    intent.putExtra(MainActivity.DOWNLOAD_ALL, true);
                    startService(intent);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_LONG).show();
                }
            } else {
                showImages();
            }
            cursor.close();
        } catch (MySQLiteException e) {
            e.printStackTrace();
        }

    }

    public void showImages() {
        try {
            Cursor cursor = accessor.fetchAll();
            Bitmap[] imagesScaled = new Bitmap[MainActivity.NUMBER];

            int counter = 0;
            while(cursor.moveToNext()) {
                byte[] data = cursor.getBlob(cursor.getColumnIndexOrThrow(SQLiteAccessor.KEY_BITMAP_IMAGES));
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if (isPortraitOrientation) {
                    imagesScaled[counter] = Bitmap.createScaledBitmap(bitmap,
                                                                      (int) (width * 0.35),
                                                                      (int) (width * 0.35),
                                                                      false);
                } else {
                    imagesScaled[counter] = Bitmap.createScaledBitmap(bitmap,
                                                                      (int) (width * 0.2),
                                                                      (int) (width * 0.2),
                                                                      false);
                }
                counter++;
            }

            GridAdapter adapter = new GridAdapter(context, android.R.layout.simple_list_item_1, imagesScaled);
            gridView.setAdapter(adapter);
            cursor.close();
        } catch (MySQLiteException e) {
            e.printStackTrace();
        }
    }

    private void getOrientation() {
        isPortraitOrientation = (getResources().getConfiguration().orientation
                                 == Configuration.ORIENTATION_PORTRAIT);
    }

    private void getDisplayWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
    }

    private void setLayoutsAccordingOrientation() {
        if (isPortraitOrientation) {
            gridView.setNumColumns(2);
            gridView.setColumnWidth((int) (width * 0.35));
        } else {
            gridView.setNumColumns(4);
            gridView.setColumnWidth((int) (width * 0.2));
        }
        gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
    }

    private class GridAdapter extends ArrayAdapter<Bitmap> {
        private Context context;

        public GridAdapter(Context context, int resource, Bitmap[] images) {
            super(context, resource, images);
            this.context = context;
        }

        public View getView(int position, View view, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setImageBitmap(getItem(position));
            return imageView;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, new IntentFilter(MainActivity.MY_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessor.close();
    }
}
