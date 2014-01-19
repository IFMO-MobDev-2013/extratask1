package com.android.example.dronov;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;
import com.android.example.dronov.database.YandexPhotos;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity implements ActionBar.TabListener {

    public static final int COLUMNS_PORTRAIT = 2;
    public static final int COLUMNS_LANDSCAPE = 4;
    public static int DEFAULT_WIDTH;
    public static int DEFAULT_HEIGHT;
    private int width, height;
    private GridView layout;
    private GridViewAdapter adapter;
    private boolean isPortraitOrientaion;
    private List<Bitmap> bitmapList, bitmapSmallImage = new ArrayList<Bitmap>();
    private YandexPhotos database;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        isPortraitOrientaion = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        ActionBar bar = getActionBar();

        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        width = dimension.widthPixels;
        height = dimension.heightPixels;


        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab tab = bar.newTab();
        tab.setText("Яндекс.Фотки");
        tab.setTabListener(this);
        bar.addTab(tab);

        layout = (GridView) findViewById(R.id.grid_view);
        if (isPortraitOrientaion) {
            layout.setNumColumns(COLUMNS_PORTRAIT);
            layout.setColumnWidth((int) (width * 0.35));
            DEFAULT_WIDTH = (int)(width * 0.35);
            DEFAULT_HEIGHT = (int)(width * 0.35);
        } else {
            layout.setNumColumns(COLUMNS_LANDSCAPE);
            layout.setColumnWidth((int)(width * 0.2));
            DEFAULT_WIDTH = (int)(width * 0.2);
            DEFAULT_HEIGHT = (int)(width * 0.2);
        }
        layout.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);


        database = new YandexPhotos(this);
        database.open();
        bitmapList = database.getAllPicturesData();
        getSmallBitmap();
        if (bitmapList.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Вы еще ни разу не загружали фотографий!",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        adapter = new GridViewAdapter(this, android.R.layout.simple_list_item_1, bitmapSmallImage);
        layout.setAdapter(adapter);
        ImageButton button = (ImageButton) findViewById(R.id.imageButton);
        button.setImageResource(R.drawable.refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadImages();
             }
        });

        layout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ImageActivity.class);
                intent.putExtra("bitmap", bitmapList.get(i));
                startActivity(intent);
            }
        });

    }

    private void loadImages() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Загружаем фотографии...");
        dialog.setCancelable(false);
        dialog.show();

        database.deleteAllChannels();
        new PictureDownload() {
            @Override
            protected void onPostExecute(List<Picture> pictures) {
                dialog.dismiss();
                if (pictures.size() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Проблемы с интернет соединением.",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                bitmapList.clear();
                for (int i = 0; i < pictures.size(); i++) {
                    bitmapList.add(pictures.get(i).getBigImage());
                    database.addChannel(pictures.get(i));
                }
                getSmallBitmap();
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    private Bitmap getSmallImage(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, DEFAULT_WIDTH, DEFAULT_HEIGHT, false);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.close();
    }

    public void getSmallBitmap() {
        bitmapSmallImage.clear();
        for (int i = 0; i < bitmapList.size(); i++)
            bitmapSmallImage.add(getSmallImage(bitmapList.get(i)));
    }
}
