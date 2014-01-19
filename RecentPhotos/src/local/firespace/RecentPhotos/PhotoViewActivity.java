package local.firespace.RecentPhotos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

public class PhotoViewActivity extends Activity {

	ImageView photo;
	ImagesDatabase database = new ImagesDatabase(this);
	GestureDetector gestureDetector;
	int position;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photo_view_layout);
		database.open();
		photo = (ImageView) findViewById(R.id.imageview);
		position = getIntent().getIntExtra(ImagesDatabase.KEY_IMAGE_ID, 0);
		photo.setImageBitmap(database.getImageByID(position));
		gestureDetector = new GestureDetector(this, new MyGestureListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	private void showAnotherPhoto(boolean isNext) {
		position += isNext ? 1 : -1;
		if (position < 0) position = PhotoDownloader.COUNT_PHOTOS;
		if (position >= PhotoDownloader.COUNT_PHOTOS) position = 0;
		photo.setImageBitmap(database.getImageByID(position));

	}

	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			Log.d("gesture", velocityX + " ; " + velocityY + " : " + position);
			if (Math.abs(velocityX) > 1000.0) { // need impressive scroll
				showAnotherPhoto(velocityX < 0);
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
}