package local.firespace.RecentPhotos;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.util.ArrayList;

public class TaskManager extends AsyncTask<Void, Void, ArrayList<Bitmap>> {

	@Override
	protected ArrayList<Bitmap> doInBackground(Void... params) {
		return new PhotoDownloader().getPhotos();
	}
}