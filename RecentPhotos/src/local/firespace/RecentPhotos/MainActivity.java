package local.firespace.RecentPhotos;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends Activity {

	GridView gridView;
	ArrayList<Bitmap> photos = new ArrayList<Bitmap>();
	DisplayMetrics metrics = new DisplayMetrics();
	ImagesDatabase database;
	ImageAdapter adapter;

	private void setScreenRes() {
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		Log.d("screen", "" + metrics.widthPixels);
		Log.d("screen", "" + metrics.heightPixels);
	}

	private void gridViewManage() {
		int padding = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ?
					metrics.widthPixels / 10 : metrics.widthPixels / 25;
		gridView.setHorizontalSpacing(padding);
		gridView.setVerticalSpacing(padding);
		gridView.setPadding(padding, 0, padding, 0);
		gridView.setAdapter(adapter);
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		gridView = (GridView) findViewById(R.id.gridview);
		setScreenRes();

		database = new ImagesDatabase(this);
		database.open();
		photos = database.getImages();
		adapter = new ImageAdapter(photos, this, metrics.widthPixels, getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
		gridViewManage();

		try {
			//if (photos != null) return;
			photos = new TaskManager().execute().get();
			if (photos != null) {
				database.reset();
				database.addImages(photos);
				adapter.updateImages(photos);
				gridView.invalidate();
			}
		} catch (Exception e) {
			Log.e("TaskManager", "task manager exception");
			e.printStackTrace();
		}
		

	}
}