package com.example.extratask;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: mv
 * Date: 19.01.14
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
public class Downloader extends AsyncTask<Void, Void, Bitmap[]> {


    @Override
    protected Bitmap[] doInBackground(Void... voids) {
        Bitmap[] res = new Bitmap[20];
        try {
            String query = "http://api-fotki.yandex.ru/api/recent/";
            URL url = new URL(query);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db =dbf.newDocumentBuilder();
            BufferedInputStream is = new BufferedInputStream(url.openStream());
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("entry");
            for (int i = 0; i < 20; i++) {
                Node node = nodeList.item(i);
                NodeList childs = node.getChildNodes();
                Element e = (Element) childs.item(37);
                String link = e.getAttribute("href");
                URL imageUrl = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                res[i] = BitmapFactory.decodeStream(input);
            }

        } catch (Exception e) {
            return null;
        }
        return res;
    }

    @Override
    protected void onPostExecute(Bitmap[] bitmaps) {
        MainActivity.setGridViewAdapter(bitmaps);
        MainActivity.bitmaps =  bitmaps;
    }
}
