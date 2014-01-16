package com.ctd.Images;

import android.content.Context;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class DownloadImages {
    String url;
    Context context;
    ArrayList<String> arrayList;

    DownloadImages(Context context, String url) throws MalformedURLException {
        this.context = context;
        this.url = url;
        arrayList = new ArrayList<String>();
    }

    public class RSSHandler extends DefaultHandler {

        StringBuilder builder;

        @Override
        public void startDocument() throws SAXException {

        }

        @Override
        public void endDocument() throws SAXException {

        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equals("entry")) {
            } else {
                builder = new StringBuilder();
                if (qName.equals("f:img")) {
                    if (attributes.getValue("size").equals("L"))
                        arrayList.add(attributes.getValue("href"));
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {

        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            builder.append(ch, start, length);
        }
    }


    ArrayList<String> execute() throws IOException, SAXException, ParserConfigurationException, URISyntaxException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = null;
        HttpResponse httpResponse = null;
        String xml = null;
        try {
            parser = factory.newSAXParser();
            httpResponse = new DefaultHttpClient().execute(new HttpGet(url));
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
            InputSource is = new InputSource(new StringReader(xml));
            RSSHandler handler = new RSSHandler();
            parser.parse(is, handler);
        } catch (IOException e) {
            throw e;
        }


        return arrayList;
    }

}
