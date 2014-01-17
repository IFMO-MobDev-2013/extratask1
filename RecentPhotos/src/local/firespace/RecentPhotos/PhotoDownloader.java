package local.firespace.RecentPhotos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PhotoDownloader {
	private static final String URL_API_PHOTKI = "http://api-fotki.yandex.ru/api/recent/";
	private static final int COUNT_PHOTOS = 10;

	private ArrayList<String> URLs = new ArrayList<String>();
	private ArrayList<Bitmap> photos = new ArrayList<Bitmap>();

	public ArrayList<Bitmap> getPhotos() {

		/*StringBuilder response = new StringBuilder();
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(URL_API_PHOTKI).openConnection();
			Log.d("Response code", ((Integer)connection.getResponseCode()).toString());

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				Log.d("input", inputLine);
			}
			in.close();
		} catch (Exception e) {
			Log.e("DownloadDoc", "fail downloading document");
			return null;
		}*/



		try {
			HttpClient client = new DefaultHttpClient();
			HttpResponse response = client.execute(new HttpGet(URL_API_PHOTKI));
			try {
				InputStreamReader reader = new InputStreamReader(response.getEntity().getContent());
				InputSource inputSource = new InputSource(reader);
				SAXParserFactory.newInstance().newSAXParser().parse(inputSource, new MySAXHandler());
			} catch (Exception e) {
				client.getConnectionManager().shutdown();
				e.printStackTrace();
				Log.e("Parsing", "Parse fail");
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Download", "fail opening URL");
		}

		/*try {
			SAXParserFactory.newInstance().newSAXParser().parse(response.toString(), new MySAXHandler());
			Log.d("Parsing", "Parse OK");
		} catch (Exception e) {
			Log.e("Parsing", "Parse fail");
			e.printStackTrace();
			return null;
		}*/

		for (int i = 0; i < COUNT_PHOTOS; i++) {
			try {
				photos.add(loadBitmap( new URL(URLs.get(i))));
			} catch (MalformedURLException e) {
				Log.e("DownloadPic", "Bad URL : " + URLs.get(i));
			}
		}

		return photos;
	}

	private Bitmap loadBitmap(URL url) {
		Bitmap bitmap = null;

		try {
			bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
		} catch (IOException e) {
			Log.e("DownloadPic", "Could not load Bitmap from: " + url);
		}

		return bitmap;
	}

	private class MySAXHandler extends DefaultHandler {
		int currCountPhotos = 0;
		private static final String TAG_SIZE = "size";
		private static final String TAG_CURR_SIZE = "L";
		private static final String TAG_HREF = "href";

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (attributes.getValue(TAG_SIZE) != null && attributes.getValue(TAG_SIZE).equals(TAG_CURR_SIZE) && currCountPhotos < COUNT_PHOTOS) {
				currCountPhotos++;
				URLs.add(attributes.getValue(TAG_HREF));
			}
		}
	}

}
