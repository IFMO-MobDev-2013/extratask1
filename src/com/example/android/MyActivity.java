package com.example.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity {

    private boolean isPortrait;
    private int width, height, newWidth, newHeight;
    private GridView gridView;
    private MyAdapter adapter;
    private Database database;
    private List<Bitmap> list, smallList;
    private Button button;
    private Object images;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        smallList = new ArrayList<Bitmap>();
        isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        DisplayMetrics dimension = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dimension);
        width = dimension.widthPixels;
        height = dimension.heightPixels;
        gridView = (GridView) findViewById(R.id.grid_view);
        if (isPortrait) {
            gridView.setNumColumns(2);
            gridView.setColumnWidth((int) (width * 0.35));
            newWidth = (int)(width * 0.35);
            newHeight = (int)(width * 0.35);
        } else {
            gridView.setNumColumns(4);
            gridView.setColumnWidth((int) (width * 0.2));
            newWidth = (int)(width * 0.2);
            newHeight = (int)(width * 0.2);
        }
        gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);

        database = new Database(this);
        database.open();
        list = database.getAllPicturesData();

        if (list.isEmpty()) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Нет загруженных фотографий!",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

        smallList.clear();
        for (int i = 0; i < list.size(); i++)
            smallList.add(getSmallBitmap(list.get(i)));

        adapter = new MyAdapter(this, android.R.layout.simple_list_item_1, smallList);
        gridView.setAdapter(adapter);
        button = (Button) findViewById(R.id.refreshButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getImages();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), Image.class);
                intent.putExtra("bitmap", i);
                startActivity(intent);
            }
        });
    }

    private Bitmap getSmallBitmap(Bitmap bitmap) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }

    public void getImages() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Начинаем загрузку фотографий...",
                Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        database.deleteAllChannels();
        new Task(){
            @Override
            protected void onPostExecute(List<Photo> bitmaps) {
                if (bitmaps.size() == 0) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Проблемы с интернет соединением.",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                list.clear();
                for (int i = 0; i < bitmaps.size(); i++) {
                    list.add(bitmaps.get(i).bitmap);
                    database.addChannel(bitmaps.get(i).width, bitmaps.get(i).height, bitmaps.get(i).bitmap);
                }
                smallList.clear();
                for (int i = 0; i < list.size(); i++)
                    smallList.add(getSmallBitmap(list.get(i)));

                adapter.notifyDataSetChanged();
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Картинки загружены и сохранены.",
                        Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }.execute();
    }

    public class MyAdapter extends ArrayAdapter<Bitmap> {

        private Context context;
        public MyAdapter(Context context, int textViewResourceId, List<Bitmap> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = new ImageView(getContext());
            view.setImageBitmap(getItem(position));
            return view;
        }
    }

}
