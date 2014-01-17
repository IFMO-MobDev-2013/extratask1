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
    private double imageSize;
    private boolean needScale;


    public ImageAdapter(Context c, Bitmap[] bm, int orientation, boolean needScale, int width) {
        mContext = c;
        this.bitmap = bm;
        this.needScale = needScale;
        if (orientation == 1) {
            this.imageSize = 35 * width / 100;
        } else this.imageSize = 20 * width / 100;
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
        Bitmap croppedBitmap;

        if (needScale) {
            boolean landscape = bitmap[position].getWidth() > bitmap[position].getHeight();

            float scale_factor;
            if (landscape) scale_factor = (float)imageSize / bitmap[position].getHeight();
            else scale_factor = (float)imageSize / bitmap[position].getWidth();
            Matrix matrix = new Matrix();
            matrix.postScale(scale_factor, scale_factor);

            if (landscape){
                int start = (bitmap[position].getWidth() - bitmap[position].getHeight()) / 2;
                croppedBitmap = Bitmap.createBitmap(bitmap[position], start, 0, bitmap[position].getHeight(), bitmap[position].getHeight(), matrix, true);
            } else {
                int start = (bitmap[position].getHeight() - bitmap[position].getWidth()) / 2;
                croppedBitmap = Bitmap.createBitmap(bitmap[position], 0, start, bitmap[position].getWidth(), bitmap[position].getWidth(), matrix, true);
            }
        } else {
            croppedBitmap = bitmap[position];
        }

        imageView.setImageBitmap(croppedBitmap);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return imageView;
    }
}