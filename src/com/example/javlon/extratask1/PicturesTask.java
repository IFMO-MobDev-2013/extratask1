package com.example.javlon.extratask1;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: javlon
 * Date: 19.01.14
 * Time: 23:03
 * To change this template use File | Settings | File Templates.
 */
public class PicturesTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {
    public static final String URL = "http://api-fotki.yandex.ru/api/recent/";

    @Override
    protected ArrayList<Bitmap> doInBackground(Void... voids) {
        ArrayList<Bitmap> result = new ArrayList<Bitmap>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(URL));
            HttpEntity httpEntity = httpResponse.getEntity();

            String xml = EntityUtils.toString(httpEntity);
            InputSource is = new InputSource(new StringReader(xml));
            MyHandler handler = new MyHandler();
            parser.parse(is, handler);

            result = handler.getResult();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClientProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return result;
    }
}
