package com.example.AppBestDailyPhotos;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends Activity {
	GridView listPhotoGridView;
	private DownloadPhotoAsyncTask downloadPhotoAsyncTask = new DownloadPhotoAsyncTask();
	ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		listPhotoGridView = (GridView) findViewById(R.id.gridView);
		listPhotoGridView.setAdapter(null);
		if (!isOnline()) {
			ArrayList<Bitmap> arrayList = new ArrayList<Bitmap>();
			int px = (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
			SQLiteDatabase sqLiteDatabase;
			PhotosSQLiteOpenHelper openHelper = new PhotosSQLiteOpenHelper(this);
			sqLiteDatabase = openHelper.getWritableDatabase();
			Cursor cursor = sqLiteDatabase.query(PhotosSQLiteOpenHelper.TABLE_NAME,null,null,null,null,null,null);
			if (cursor.getCount() != 0) {
				while (cursor.moveToNext()) {
					byte[] blob = cursor.getBlob(cursor.getColumnIndex(PhotosSQLiteOpenHelper.IMAGE));
					Bitmap image = Convertor.getBitmapOnBytes(blob);
					arrayList.add(image);
				}
				listPhotoGridView.setAdapter(new ImageAdapter(MainActivity.this, arrayList, px, px, 0));
			} else {
				Toast.makeText(getApplicationContext(),
						"Nope network connection",
						Toast.LENGTH_LONG).show();
			}
		} else {
			downloadPhotoAsyncTask.execute();
		}

		listPhotoGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View view, int index, long arg3) {
				startActivity(
						new Intent(MainActivity.this, ViewPhotoActivity.class)
								.putExtra("id", String.valueOf(index)));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item2:
				if (!isOnline()) {
					Toast.makeText(getApplicationContext(),
							"Nope network connection",
							Toast.LENGTH_LONG).show();
				} else {
					listPhotoGridView.setAdapter(null);
					DownloadPhotoAsyncTask downloadPhotoAsyncTask1 = new DownloadPhotoAsyncTask();
					downloadPhotoAsyncTask1.execute();
				}
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public boolean isOnline() {
		ConnectivityManager cm =
				(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo() != null &&
				cm.getActiveNetworkInfo().isConnectedOrConnecting();
	}


	public class DownloadPhotoAsyncTask extends AsyncTask<Void,Void,Void> {
		ArrayList<String> links = new ArrayList<String>();

		public Bitmap getBitmapFromURL(String link) {
			try {
				HttpURLConnection connection =
						(HttpURLConnection)
						new URL(link)
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				return BitmapFactory.decodeStream(connection.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected Void doInBackground(Void... params) {
			final String LINK = "http://api-fotki.yandex.ru/api/recent/";
			final String COUNT_PICTURE = "20";
			final String SIZE_PICTURE_MIN = "M";

			ContentValues newValues = new ContentValues();
			Document doc;
			NamedNodeMap attributes;
			Node node;
			NodeList entryNodeList,childEntryNodeList;
			PhotosSQLiteOpenHelper photosSQLiteOpenHelper = new PhotosSQLiteOpenHelper(MainActivity.this);
			SQLiteDatabase db;
			try {
				//connect
				doc = DocumentBuilderFactory
						.newInstance()
						.newDocumentBuilder()
						.parse(
								new URL(LINK + "?limit=" + COUNT_PICTURE)
								.openConnection()
								.getInputStream()
						);

				//parse link's picture
				entryNodeList = doc.getElementsByTagName("entry");
				for (int i = 0; i < entryNodeList.getLength(); i++) {
					childEntryNodeList = entryNodeList.item(i).getChildNodes();
					for (int j = 0; j < childEntryNodeList.getLength(); j++) {
						node = childEntryNodeList.item(j);
						if (node.getNodeName().equals("f:img")) {
							attributes = node.getAttributes();
							if (attributes.getNamedItem("size").getNodeValue().equals(SIZE_PICTURE_MIN)) {
								links.add(attributes.getNamedItem("href").getNodeValue());
							}
						}
					}
				}

				//download picture and insert in SQL
				db = photosSQLiteOpenHelper.getWritableDatabase();
				db.execSQL("DELETE FROM " +
						photosSQLiteOpenHelper.TABLE_NAME +
						";");
				for (String link : links) {
					bitmaps.add(getBitmapFromURL(link));
					newValues.put(
							photosSQLiteOpenHelper.IMAGE,
							Convertor.getBytesOnBitmap(
									getBitmapFromURL(link)
							)
						);
					db.insert(photosSQLiteOpenHelper.TABLE_NAME, null, newValues);
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			int px = (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()));
			listPhotoGridView.setAdapter(new ImageAdapter(MainActivity.this, bitmaps, px, px, 0));
		}
	}
}
