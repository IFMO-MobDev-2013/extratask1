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
    private List<Bitmap> bm;
    private int width;

    public ImageAdapter(Context c, List<Bitmap> bm, int width) {
        mContext = c;
        this.bm = bm;
        this.width = width;
    }

    @Override
    public int getCount() {
        return bm.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return bm.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bm.get(position));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams((int) (0.35 * width), (int) (0.35 * width)));
        //imageView.setPadding(5,5,5,5);
        return imageView;
    }
}