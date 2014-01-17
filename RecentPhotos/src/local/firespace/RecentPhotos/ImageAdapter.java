package local.firespace.RecentPhotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

	private ArrayList<Bitmap> images;
	private Context context;
	private double imageSize;

	public ImageAdapter(ArrayList<Bitmap> images, Context context, int width) {
		this.images = images;
		this.context = context;
		this.imageSize = 35 * width / 100;
	}

	@Override
	public int getCount() {
		return images.size();
	}

	@Override
	public Object getItem(int position) {
		return images.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);

		boolean isLandscape = images.get(position).getWidth() > images.get(position).getHeight();

		float scale_factor = isLandscape ?
				(float)imageSize / images.get(position).getHeight() :
				(float)imageSize / images.get(position).getWidth();
		Matrix matrix = new Matrix();
		matrix.postScale(scale_factor, scale_factor);
		int start = isLandscape ?
				(images.get(position).getWidth() - images.get(position).getHeight()) / 2 :
				(images.get(position).getHeight() - images.get(position).getWidth()) / 2;
		Bitmap croppedBitmap = isLandscape ?
				Bitmap.createBitmap(images.get(position), start, 0, images.get(position).getHeight(), images.get(position).getHeight(), matrix, true) :
				Bitmap.createBitmap(images.get(position), 0, start, images.get(position).getWidth(), images.get(position).getWidth(), matrix, true);

		imageView.setImageBitmap(croppedBitmap);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return imageView;
	}
}
