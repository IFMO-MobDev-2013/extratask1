package com.example.javlon.extratask1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.javlon.extratask1.database.Pictures;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final double PORTRAIT_SCALE = 0.35;
    private static final double LANDSKAPE_SCALE = 0.2;
    private boolean orientaion;
    private int displayWidth, displayHeight;
    private GridView gridView;
    private int scale;
    private ImageButton button;
    private Pictures database;
    private ArrayList<Bitmap> result, resultSmall = new ArrayList<Bitmap>();
    private MyAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        init();
        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        if (orientaion) {
            gridView.setNumColumns(2);
            gridView.setColumnWidth((int) (displayWidth * PORTRAIT_SCALE));
            scale = (int) (displayWidth * PORTRAIT_SCALE);
        } else {
            gridView.setNumColumns(4);
            gridView.setColumnWidth((int) (displayWidth * LANDSKAPE_SCALE));
            scale = (int) (displayWidth * LANDSKAPE_SCALE);
        }


        database = new Pictures(this);
        database.open();

        result = database.getAllPictures();
        resultSmall.clear();
        for (int i = 0; i < result.size(); i++)
            resultSmall.add(Bitmap.createScaledBitmap(result.get(i), scale, scale, false));


        adapter = new MyAdapter(this, android.R.layout.simple_list_item_1, resultSmall);
        gridView.setAdapter(adapter);


        button = (ImageButton) findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PicturesTask() {
                    @Override
                    protected void onPreExecute() {
                        button.setEnabled(false);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
                        button.setEnabled(true);
                        if (bitmaps.isEmpty()) {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Some internet problems...",
                                    Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            database.deleteAll();
                            result.clear();
                            resultSmall.clear();
                            for (int i = 0; i < bitmaps.size(); i++) {
                                result.add(bitmaps.get(i));
                                resultSmall.add(Bitmap.createScaledBitmap(bitmaps.get(i), scale, scale, false));
                                database.add(bitmaps.get(i));
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }.execute();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(view.getContext(), ImageShower.class);
                intent.putExtra("result", result.get(i));
                startActivity(intent);
            }
        });
    }

    private void init() {
        orientaion = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        displayWidth = displayMetrics.widthPixels;
        displayHeight = displayMetrics.heightPixels;
    }


    private class MyAdapter extends ArrayAdapter<Bitmap> {

        private Context context;
        public MyAdapter(Context context, int textViewResourceId, List<Bitmap> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView current = new ImageView(getApplicationContext());
            current.setImageBitmap(getItem(position));
            return current;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
        database.close();
    }
}
