package com.example.BestPhoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private Bitmap[] bitmap;


    public ImageAdapter(Context c, Bitmap[] bm) {
        mContext = c;
        this.bitmap = bm;
    }

    @Override
    public int getCount() {
        return bitmap.length;
    }

    @Override
    public Object getItem(int position) {
        return bitmap[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bitmap[position]);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView;
    }
}