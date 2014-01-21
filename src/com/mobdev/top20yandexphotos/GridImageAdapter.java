package com.mobdev.top20yandexphotos;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

class GridImageAdapter extends ArrayAdapter<Bitmap> {
	private Context context;
	private int width;
	private boolean isPortrait;

	public GridImageAdapter(Context context, int textViewResourceId, ArrayList<Bitmap> items, int width, boolean isPortrait) {
		super(context, textViewResourceId, items);
		this.context = context;
		this.width = width;
		this.isPortrait = isPortrait;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ImageView imageView = new ImageView(context);
		imageView.setImageBitmap(getItem(position));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		if (isPortrait)
			imageView.setLayoutParams(new GridView.LayoutParams((int) (0.35 * width), (int) (0.35 * width)));
		else
			imageView.setLayoutParams(new GridView.LayoutParams((int) (0.2 * width), (int) (0.2 * width)));
		return imageView;
	}
}