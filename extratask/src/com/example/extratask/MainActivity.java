package com.example.extratask;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

public class MainActivity extends Activity {

   // public static boolean database_empty = true;
    public static boolean is_land;
    public static Bitmap[] bitmaps;
    public static Bitmap[] full_size_bitmaps;
    public static int width;
    public static GridView gridView;
    public static Context context;
    public static int height;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        is_land = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
        gridView = (GridView) findViewById(R.id.gridView);
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        width = size.x;
        height = size.y;
        if (is_land) {
            gridView.setNumColumns(4);
            gridView.setVerticalSpacing((int)(width * 0.04));
            gridView.setHorizontalSpacing((int)(width * 0.04));
            gridView.setPadding((int)(width * 0.04), (int)(width * 0.04), (int)(width * 0.04), (int)(width * 0.04));
        } else {
            gridView.setNumColumns(2);
            gridView.setVerticalSpacing((int)(width * 0.1));
            gridView.setHorizontalSpacing((int)(width * 0.1));
            gridView.setPadding((int)(width * 0.1), (int)(width * 0.1), (int)(width * 0.1), (int)(width * 0.1));
        }
        new Downloader().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            new Downloader().execute();
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        is_land = !is_land;
        if (is_land) {
            gridView.setNumColumns(4);
            gridView.setVerticalSpacing((int)(width * 0.04));
            gridView.setHorizontalSpacing((int)(width * 0.04));
            gridView.setPadding((int)(width * 0.04), (int)(width * 0.04), (int)(width * 0.04), (int)(width * 0.04));
        } else {
            gridView.setNumColumns(2);
            gridView.setVerticalSpacing((int)(width * 0.1));
            gridView.setHorizontalSpacing((int)(width * 0.1));
            gridView.setPadding((int)(width * 0.1), (int)(width * 0.1), (int)(width * 0.1), (int)(width * 0.1));
        }
    }

    public static void setGridViewAdapter(Bitmap[] bitmaps) {
        gridView.setAdapter(new ImageAdapter(context, bitmaps, width, is_land));
    }


}
