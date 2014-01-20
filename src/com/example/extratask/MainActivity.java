package com.example.extratask;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AppResultsReceiver.Receiver{

    private AppResultsReceiver mReceiver;
    private Adapter adapter;
    private DataBase db;
    private List<Image> images;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mReceiver = new AppResultsReceiver(new Handler());
        mReceiver.setReceiver(this);
        db = new DataBase(getApplicationContext());

        int width = getDisplayWidth();

        GridView gridView = (GridView) findViewById(R.id.gridview);


        double k = 0.1;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)  {
        //gridView.setHorizontalSpacing((int)(width * k));
        gridView.setVerticalSpacing((int)(width * k));
        gridView.setColumnWidth((int)(width*0.35));
        gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        } else {
            k = 0.04;
            gridView.setVerticalSpacing((int)(width * k));
            gridView.setColumnWidth((int)(width*0.2));
            gridView.setNumColumns(4);
            gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        }

        images = new ArrayList<>();
        db.getAllImages(images);

        if (images.size() == 0) {
            startDownloading();
        }

        adapter = new Adapter(getApplicationContext(), R.layout.grid_item,images);
        gridView.setAdapter(adapter);

        GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                                    long id) {

                Intent i = new Intent(getApplicationContext(),
                       PictureActivity.class);
                i.putExtra("id", images.get(position).getId());
                startActivity(i);
            }
        };

        gridView.setOnItemClickListener(gridviewOnItemClickListener);

    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        db.getAllImages(images);
        adapter.notifyDataSetChanged();
        Toast toast = Toast.makeText(getApplicationContext(),
                "Download complete.", Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.closeDB();
    }

    public void refresh(View v) {
        startDownloading();
    }

    private int getDisplayWidth() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    private void startDownloading() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Starting download...", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("receiver", mReceiver);
        startService(intent);
    }
}
