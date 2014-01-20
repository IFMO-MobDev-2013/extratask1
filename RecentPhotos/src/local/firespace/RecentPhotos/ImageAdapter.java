package local.firespace.RecentPhotos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Bitmap[] croppedImages = new Bitmap[PhotoDownloader.COUNT_PHOTOS];
	private Context context;
	private double imageSize;

	private void setCroppedImages(Bitmap[] images) {
		for (int i = 0; i < images.length; i++) {
			boolean isImageLandscape = images[i].getWidth() > images[i].getHeight();

			float scale_factor = isImageLandscape ?
					(float)imageSize / images[i].getHeight() :
					(float)imageSize / images[i].getWidth();
			Matrix matrix = new Matrix();
			matrix.postScale(scale_factor, scale_factor);
			int start = isImageLandscape ?
					(images[i].getWidth() - images[i].getHeight()) / 2 :
					(images[i].getHeight() - images[i].getWidth()) / 2;
			Bitmap croppedBitmap = isImageLandscape ?
					Bitmap.createBitmap(images[i], start, 0, images[i].getHeight(), images[i].getHeight(), matrix, true) :
					Bitmap.createBitmap(images[i], 0, start, images[i].getWidth(), images[i].getWidth(), matrix, true);

			croppedImages[i] = croppedBitmap;
		}
	}

	public ImageAdapter(Bitmap[] images, Context context, int width, boolean isPortraitOrientation) {
		this.imageSize = isPortraitOrientation ? 35 * width / 100 : 20 * width / 100;
		this.context = context;
		if (images == null) {
			return;
		}
		setCroppedImages(images);
	}

	@Override
	public int getCount() {
		return croppedImages.length;
	}

	@Override
	public Object getItem(int position) {
		return croppedImages[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		Log.d("adapter", position + " ; " + croppedImages.length);

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
		imageView.setImageBitmap(croppedImages[position]);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		return imageView;
	}

	public void updateImages(Bitmap[] newImages) {
		setCroppedImages(newImages);
	}
}
