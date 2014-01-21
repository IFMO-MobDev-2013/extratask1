package com.example.popularphotos;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

public class MainActivity extends Activity {
	public static Bitmap[] bm = new Bitmap[20];
	private MyAdapter adpr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	
		
		Button btn =(Button)findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new GetPhotos().execute();
			}
		});
	}

	private class GetPhotos extends AsyncTask<Void, Void, Bitmap[]> {
		@Override
		protected Bitmap[] doInBackground(Void... v) {
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
				Bitmap[] images = new Bitmap[20];
				int ik=0;
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
						images[ik] = getBitmapFromURL(urls);
						++ik;
						if (ik == 20) {
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
		protected void onPostExecute(Bitmap[] result) {
			bm = result;
			GridView g = (GridView) findViewById(R.id.GridMain);
			g.setNumColumns(2);
			g.setVerticalSpacing(15);
			g.setPadding(8, 15, 8, 3);
			g.setGravity(Gravity.CENTER_HORIZONTAL);
			WindowManager wm = getWindowManager();
			adpr = new MyAdapter(getApplicationContext(),
					android.R.layout.simple_list_item_1, bm, wm);
			g.setAdapter(adpr);
			g.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v, int position, long id){
					Intent i = new Intent();
					i.setClass(MainActivity.this, PicActivity.class);		
					i.putExtra("PICTURE", position);
					startActivity(i);
				}
			});
		}
		
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
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				Bitmap myBitmap = BitmapFactory.decodeStream(input);
				return myBitmap;
			} catch (IOException e) {
				return null;
			}
		}
	}
}
