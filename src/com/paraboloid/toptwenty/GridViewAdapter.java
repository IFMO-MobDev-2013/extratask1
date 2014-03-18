package com.paraboloid.toptwenty;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Bitmap> pics;
    private int screenWidth;
    private boolean portrait;

    public GridViewAdapter(Context context, ArrayList<Bitmap> pics, int screenWidth, boolean portrait) {
        this.context = context;
        this.pics = pics;
        this.screenWidth = screenWidth;
        this.portrait = portrait;
    }

    public int getCount() {
        return pics.size();
    }

    public Object getItem(int position) {
        return pics.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        int picWidth = (int) (screenWidth * 0.35);
        if (!portrait) {
            picWidth = (int)(screenWidth * 0.2);
        }
        if (convertView == null) {
            imageView = new ImageView(context);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setLayoutParams(new GridView.LayoutParams(picWidth, picWidth));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageBitmap(pics.get(position));
        return imageView;
    }
}
