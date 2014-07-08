package ru.ifmo.mobdev.extra;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.net.URL;
import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bitmap> imgBitmaps;
    LayoutInflater layoutInflater;

    ImageAdapter(Context ctx, ArrayList<Bitmap> bitmapList) {
        context = ctx;
        imgBitmaps = new ArrayList<Bitmap>(bitmapList);
        //layoutInflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
     }

    @Override
    public int getCount() {
        return imgBitmaps.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return imgBitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView view;
        if (convertView == null) {
            view = new ImageView(context);
            view.setScaleType(ImageView.ScaleType.CENTER);
            view.setLayoutParams(new GridView.LayoutParams(200, 200));
            //view.setPadding(5, 5, 5, 5);
        } else {
            view = (ImageView) convertView;
        }

        view.setImageBitmap(imgBitmaps.get(position));
        //((ImageView)view.findViewById(R.id.itemImageView)).setImageBitmap(imgBitmaps.get(position));

        return view;

    }


}