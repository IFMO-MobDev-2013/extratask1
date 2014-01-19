package com.example.extratask1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 17.01.14
 * Time: 8:17
 * To change this template use File | Settings | File Templates.
 */
public class MyAsyncTask extends AsyncTask<String, Integer, ArrayList<PictureItem>> {
    private static ArrayList<PictureItem> picItems = new ArrayList<PictureItem>();

    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = null;
    HttpResponse httpResponse = null;
    String xml = null;

    @Override
    protected ArrayList<PictureItem> doInBackground(String... urls) {
        try {
            String url = urls[0];
            parser = factory.newSAXParser();
            httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
            InputSource is = new InputSource(new StringReader(xml));
            MySAXApp handler = new MySAXApp();
            parser.parse(is, handler);
        } catch (Exception e) {
            return null;
        }
        return picItems;
    }

    protected void onPostExecute(ArrayList<PictureItem> result) {
        System.out.println("End Async");
        MyActivity.pictureItems.clear();
        MyActivity.pictureItems.addAll(result);
        MyActivity.myArrayAdapter = new MyArrayAdapter(MyActivity.context, MyActivity.translateOrientation(result));
        MyActivity.listView.setAdapter(MyActivity.myArrayAdapter);
        MyActivity.myArrayAdapter.notifyDataSetChanged();
        MyActivity.startRefresh = false;
        Intent intent = new Intent("com.example.extratask1.SAVESTART");
        MyActivity.context.sendBroadcast(intent);
    }

    public class MySAXApp extends DefaultHandler {
        private String currentElement = null;
        private String link;
        private String name;
        private String id;
        private boolean entryOpen;
        private int kol = 0;

        @Override
        public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName, org.xml.sax.Attributes attributes) throws SAXException {
            currentElement = qName;
            if ("entry".equals(qName)){
                entryOpen = true;
                link="";
                name="";
                id = "";
            }
            if (entryOpen && "f:img".equals(qName) && "L".equals(attributes.getValue("size"))){
                link = attributes.getValue("href");
            }
        }

        public void endElement(String uri, String local_name, String raw_name){
            if ("entry".equals(local_name)){
                entryOpen = false;
                if (kol<MyActivity.numberPic){
                    kol++;
                    System.out.println("Save " + kol);
                    picItems.add(new PictureItem(name, id, download(link, id)));
                }
            }
        }

        public void characters(char[] ch, int start, int length){
            String value = new String(ch,start,length);
            if (!Character.isISOControl(value.charAt(0))) {
                if (entryOpen){
                    if ("title".equals(currentElement)) {
                        name += value;
                    } else if ("id".equals(currentElement)) {
                        id += value;
                    }
                }
            }
        }

        private Bitmap download(String link, String id){
            try {
                URL url = new URL(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
                //return BitmapFactory.decodeStream((InputStream) new URL(link).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
