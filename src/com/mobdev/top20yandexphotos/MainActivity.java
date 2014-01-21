package com.mobdev.top20yandexphotos;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {
	public static ArrayList<Bitmap> bitmaps;
	private static boolean IS_STARTED_DOWNLOAD = false;
	private static boolean IS_FINISHED_DOWNLOAD = false;
	private GridImageAdapter adapter;
	private Button upd_btn;
	private GridView grv;
	int width;
	
	private class Downloader extends AsyncTask<Void, Void, ArrayList<Bitmap>> {

		public Bitmap getBitmapFromURL(String src) {
			int i = src.length() - 1;
			while (i >= 0) {
				if (src.charAt(i) == '_') {
					src = src.substring(0, i + 1) + "M";
					break;
				}
				i--;
			}
			try {
				URL url = new URL(src);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected ArrayList<Bitmap> doInBackground(Void... voids) {
			try {
				String query = "http://api-fotki.yandex.ru/api/recent/", result = "";
				URL url = new URL(query);
				URLConnection connection = url.openConnection();
				connection.setConnectTimeout(10000);
				connection.setReadTimeout(10000);
				Scanner scanner = new Scanner(connection.getInputStream());
				while (scanner.hasNext()) {
					result += scanner.nextLine();
				}
				ArrayList<Bitmap> images = new ArrayList<Bitmap>();
				for (int i = 0; i < result.length() - 15; i++) {
					if (result.substring(i, i + 14).equals("<content src=\"")) {
						String urls = "";
						for (int j = i + 14;; j++) {
							char c = result.charAt(j);
							if (c == '"') {
								i = j;
								break;
							}
							urls += c;
						}
						images.add(getBitmapFromURL(urls));
						if (images.size() == 20) {
							break;
						}
					}
				}
				scanner.close();
				return images;
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(ArrayList<Bitmap> result) {
			IS_FINISHED_DOWNLOAD = true;
			updateGridView(result);			
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		grv = (GridView)findViewById(R.id.grv);
		upd_btn = (Button)findViewById(R.id.upd_btn);
		
		Point size = new Point();		
		Display display = getWindowManager().getDefaultDisplay();
		display.getSize(size);
		width = size.x;
		
		updateGridView(bitmaps);
		
		upd_btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new Downloader().execute();
				Toast toast = Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_LONG);
				toast.show();
				IS_STARTED_DOWNLOAD = true;
				upd_btn.setClickable(false);
			}
		});
	}
	public void updateGridView(ArrayList<Bitmap> bm) {
		if (bm == null){
			if (IS_STARTED_DOWNLOAD){
				Toast toast = Toast.makeText(getApplicationContext(), "There is no Internet Connection", Toast.LENGTH_SHORT);
				toast.show();
				upd_btn.setClickable(true);
				IS_STARTED_DOWNLOAD = false;
				IS_FINISHED_DOWNLOAD = false;
			}
		}
		else{
			if (IS_STARTED_DOWNLOAD && IS_FINISHED_DOWNLOAD){
				Toast toast = Toast.makeText(getApplicationContext(), "Images have been downdoaded", Toast.LENGTH_SHORT);
				toast.show();
				IS_STARTED_DOWNLOAD = false;
				IS_FINISHED_DOWNLOAD = false;
			}				
			bitmaps = new ArrayList<Bitmap>();
			bitmaps = bm;
			adapter = new GridImageAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, bitmaps, width, getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
			grv.setAdapter(adapter);
			upd_btn.setClickable(true);
			upd_btn.setText(R.string.update_button_new);
			grv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
					Intent intent = new Intent(MainActivity.this, FullscreenImageActivity.class);
					intent.putExtra("IMAGE", position);
					startActivity(intent);
				}
			});
		}
	}

}
