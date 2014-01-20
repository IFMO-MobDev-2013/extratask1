package com.example.BestPhoto;


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

public class ImageURLs extends AsyncTask<Void, Void, String[]> {
    private final int IMAGECOUNT = 20;
    private String url;


    public ImageURLs(String url, Context context) {
        this.url = url;

    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    protected void onPostExecute(String[] as) {
        super.onPostExecute(as);
    }

    @Override
    protected String[] doInBackground(Void... params) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            HttpResponse response = new DefaultHttpClient().execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();
            String xml = EntityUtils.toString(entity, "UTF-8");
            InputSource input = new InputSource(new StringReader(xml));
            String[] urls = new String[IMAGECOUNT];
            parser.parse(input, new ImageHandler(urls));
            return urls;
        } catch(Exception e) {
        }
        return null;
    }


    class ImageHandler extends DefaultHandler {
        private final String HREF = "href";
        private final String MYLINK = "rel";
        private final String MYATTR = "edit-media";
        StringBuilder sb;
        String[] urls;

        ImageHandler(String[] urls) {
            super();
            this.urls = urls;
            sb = new StringBuilder();
        }



        int size = -1;
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException {
            super.startElement(uri, localName, qName, attr);
            if (attr.getValue(MYLINK) != null && size < IMAGECOUNT - 1) {
                if (attr.getValue(MYLINK).equals(MYATTR)) {
                    size++;
                    urls[size] = attr.getValue(HREF);
                }
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
        }
    }
}
