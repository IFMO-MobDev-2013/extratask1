package com.mikhov.ExtraFotki;

import android.content.Context;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.StringReader;

public class GetMiniUrls extends AsyncTask<Void, Void, String[]> {
    String url;

    public GetMiniUrls(String url, Context context) {
        this.url = url;
    }

    @Override
    protected String[] doInBackground(Void... params) {
        String[] urls = new String[20];
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser parser = saxParserFactory.newSAXParser();
            HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
            HttpEntity httpEntity = httpResponse.getEntity();
            String xml = EntityUtils.toString(httpEntity, "UTF-8");
            InputSource inputSource = new InputSource(new StringReader(xml));
            parser.parse(inputSource, new MyHandler(urls));
            for (int i = 0; i < 20; i++) {
                String url = urls[i];
                int j = url.length() - 1;
                while (url.charAt(j) != '_') {
                    j--;
                }
                url = url.substring(0, j + 1) + "M";
                urls[i] = url;
            }
            return urls;
        } catch(Exception e) {
            return urls;
        }
    }

    class MyHandler extends DefaultHandler {
        StringBuilder sb;
        String[] urls;
        int i = 0;

        MyHandler(String[] urls) {
            super();
            this.urls = urls;
            sb = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
            super.startElement(uri, localName, qName, attr);
            if (attr.getValue("rel") != null && i < 20) {
                if (attr.getValue("rel").equals("edit-media")) {
                    urls[i] = attr.getValue("href");
                    i++;
                }
            }
        }
    }
}
