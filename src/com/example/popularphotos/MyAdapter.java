package com.example.popularphotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MyAdapter extends ArrayAdapter<Bitmap> {

	private Bitmap[] pictures;
	Context mContext;
	WindowManager wm;

	
	public MyAdapter(Context context, int textViewResourceId , Bitmap[] a, WindowManager wm) {
		super(context, textViewResourceId, a);
		this.mContext = context;
		this.pictures = a;
		this.wm = wm;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView img = new ImageView(mContext);
		img.setImageBitmap(pictures[position]);
		img.setScaleType(ImageView.ScaleType.CENTER_CROP);
		Point pnt = new Point();
		wm.getDefaultDisplay().getSize(pnt);
		img.setLayoutParams(new GridView.LayoutParams((int)(0.35*(pnt.x)),(int)(0.35*(pnt.x))));
		return img;
	}

}