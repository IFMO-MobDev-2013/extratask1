package local.firespace.RecentPhotos;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MainActivity extends Activity {

	GridView gridView;
	Bitmap[] photos;
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
		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MainActivity.this, PhotoViewActivity.class);
				intent.putExtra(ImagesDatabase.KEY_IMAGE_ID, position);
				startActivity(intent);
			}
		});
		gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				downloadPhotos();
				return false;
			}
		});
	}

	private void downloadPhotos() {
		try {
			photos = new TaskManager().execute().get();
			if (photos != null) {
				Log.d("photos", "get photos from internet : " + photos.length);
				database.reset();
				database.addImages(photos);
				adapter.updateImages(photos);
			}
		} catch (Exception e) {
			Log.e("TaskManager", "task manager exception");
			e.printStackTrace();
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		gridView = (GridView) findViewById(R.id.gridview);
		setScreenRes();

		database = new ImagesDatabase(this);
		database.open();
		//database.reset();
		photos = database.getImages();
		adapter = new ImageAdapter(photos, this, metrics.widthPixels, getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
		if (photos == null) {
			Log.d("photos", "not photos on database");
			downloadPhotos();
		}
		gridViewManage();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		/*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setContentView(R.layout.main_activity_layout_land);
		} else {
			setContentView(R.layout.main_activity);
		}*/
		setContentView(R.layout.main_activity);
		gridView = (GridView) findViewById(R.id.gridview);
		setScreenRes();
		gridViewManage();
	}
}