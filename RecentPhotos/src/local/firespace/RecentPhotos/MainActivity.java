package local.firespace.RecentPhotos;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MainActivity extends Activity {

	GridView gridView;
	ArrayList<Bitmap> photos = new ArrayList<Bitmap>();
	private int screenWidth;
	private int screenHeight;

	private void setScreenRes() {
		Point point = new Point();
		getWindowManager().getDefaultDisplay().getSize(point);
		screenWidth = point.x;
		screenHeight = point.y;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		gridView = (GridView) findViewById(R.id.gridview);
		setScreenRes();

		new TaskManager().execute();
		gridView.setAdapter(new ImageAdapter(photos, this, screenWidth));
	}

	public class TaskManager extends AsyncTask <Void, Void, Void> {
		ArrayList<Bitmap> images;

		@Override
		protected Void doInBackground(Void... params) {
			images = new PhotoDownloader().getPhotos();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			photos = images;
		}
	}
}