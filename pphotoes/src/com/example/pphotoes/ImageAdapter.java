package com.example.pphotoes;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Bitmap> bitmaps;
    private int width;
    private int height;
    private int paddings;

    public ImageAdapter(Context c, List<Bitmap> bitmaps, int width, int height, int paddings) {
        mContext = c;
        this.bitmaps = bitmaps;
        this.width = width;
        this.height = height;
        this.paddings = paddings;
    }

    @Override
    public int getCount() {
        if (bitmaps == null) {
            return 0;
        }
        return bitmaps.size();
    }

    @Override
    public Object getItem(int i) {
        if (bitmaps == null) {
            return null;
        }
        return bitmaps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(width, height));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(paddings, paddings, paddings, paddings);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageBitmap(bitmaps.get(position));
        return imageView;
    }
}