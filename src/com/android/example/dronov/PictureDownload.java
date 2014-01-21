package com.android.example.dronov;

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
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 19.01.14
 * Time: 8:15
 * To change this template use File | Settings | File Templates.
 */
public class PictureDownload extends AsyncTask<Void, Void, List<Picture>> {
    public static final String YANDEX_FOTOS_URL = "http://api-fotki.yandex.ru/api/recent/";

    @Override
    protected List<Picture> doInBackground(Void... voids) {
        List<Picture> list = new ArrayList<Picture>();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(YANDEX_FOTOS_URL));
            HttpEntity httpEntity = httpResponse.getEntity();

            String xml = EntityUtils.toString(httpEntity);
            InputSource is = new InputSource(new StringReader(xml));
            YandexHandler handler = new YandexHandler();
            parser.parse(is, handler);

            list = handler.getResult();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ClientProtocolException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return list;
    }
}
