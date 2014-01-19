package com.example.extratask;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: mv
 * Date: 19.01.14
 * Time: 18:29
 * To change this template use File | Settings | File Templates.
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;
    public Bitmap[] bitmaps;
    private int width;
    private boolean is_land;

    public ImageAdapter(Context context, Bitmap[] bitmaps, int width, boolean is_land) {
        this.context = context;
        this.width = width;
        this.is_land = is_land;
        this.bitmaps = bitmaps;
    }

    public int getCount() {
        return bitmaps.length;
    }

    public Bitmap getItem(int position) {
        return bitmaps[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams((int)(width * (is_land ? 0.2 : 0.35)), (int)(width * (is_land ? 0.2 : 0.35))));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(bitmaps[position]);
        return imageView;
    }


}
