package com.paraboloid.toptwenty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GetPicsTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {

    public static final String XML = "http://api-fotki.yandex.ru/api/recent/";
    private ArrayList<String> links;

    @Override
    protected ArrayList<Bitmap> doInBackground(Void... params) {
        try {
            links = new ArrayList<String>();
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(XML));
            HttpEntity httpEntity = httpResponse.getEntity();
            String xml = EntityUtils.toString(httpEntity, "UTF-8");
            InputSource is = new InputSource(new StringReader(xml));
            saxParser.parse(is, new SaxHandler(links));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
        super.onPostExecute(bitmaps);
    }

    private Bitmap downloadPic(String source) {
        try {
            URL url = new URL(source);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;
        } catch (Exception e) {
            return null;
        }

    }

}
