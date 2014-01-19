package local.firespace.RecentPhotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

	private ArrayList<Bitmap> croppedImages = new ArrayList<Bitmap>();
	private Context context;
	private double imageSize;

	private void setCroppedImages(ArrayList<Bitmap> images) {
		for (Bitmap image : images) {
			boolean isImageLandscape = image.getWidth() > image.getHeight();

			float scale_factor = isImageLandscape ?
					(float)imageSize / image.getHeight() :
					(float)imageSize / image.getWidth();
			Matrix matrix = new Matrix();
			matrix.postScale(scale_factor, scale_factor);
			int start = isImageLandscape ?
					(image.getWidth() - image.getHeight()) / 2 :
					(image.getHeight() - image.getWidth()) / 2;
			Bitmap croppedBitmap = isImageLandscape ?
					Bitmap.createBitmap(image, start, 0, image.getHeight(), image.getHeight(), matrix, true) :
					Bitmap.createBitmap(image, 0, start, image.getWidth(), image.getWidth(), matrix, true);

			croppedImages.add(croppedBitmap);
		}
	}

	public ImageAdapter(ArrayList<Bitmap> images, Context context, int width, boolean isPortraitOrientation) {
		this.imageSize = isPortraitOrientation ? 35 * width / 100 : 20 * width / 100;
		setCroppedImages(images);
		this.context = context;
	}

	@Override
	public int getCount() {
		return croppedImages.size();
	}

	@Override
	public Object getItem(int position) {
		return croppedImages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		Log.d("adapter", position + " ; " + croppedImages.size());

		/*boolean isImageLandscape = images.get(position).getWidth() > images.get(position).getHeight();

		float scale_factor = isImageLandscape ?
				(float)imageSize / images.get(position).getHeight() :
				(float)imageSize / images.get(position).getWidth();
		Matrix matrix = new Matrix();
		matrix.postScale(scale_factor, scale_factor);
		int start = isImageLandscape ?
				(images.get(position).getWidth() - images.get(position).getHeight()) / 2 :
				(images.get(position).getHeight() - images.get(position).getWidth()) / 2;
		croppedBitmap = isImageLandscape ?
				Bitmap.createBitmap(images.get(position), start, 0, images.get(position).getHeight(), images.get(position).getHeight(), matrix, true) :
				Bitmap.createBitmap(images.get(position), 0, start, images.get(position).getWidth(), images.get(position).getWidth(), matrix, true);
*/
		imageView.setImageBitmap(croppedImages.get(position));
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return imageView;
	}

	public void updateImages(ArrayList<Bitmap> newImages) {
		setCroppedImages(newImages);
	}
}
