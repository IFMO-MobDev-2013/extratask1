package local.firespace.RecentPhotos;

import android.graphics.Bitmap;
import android.os.AsyncTask;

public class TaskManager extends AsyncTask<Void, Void, Bitmap[]> {

	@Override
	protected Bitmap[] doInBackground(Void... params) {
		return new PhotoDownloader().getPhotos();
	}
}