package com.gmail.mazinva.PopularPhotos;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

public class MySAXParser {

    public MySAXParser() {}

    public String[] parse(InputStream inputStream) throws ParserConfigurationException,
                                                               SAXException,
                                                               IOException {

        Reader reader = new BufferedReader(new InputStreamReader(inputStream));
        InputSource inputSource = new InputSource(reader);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();
        MyHandler handler = new MyHandler();
        saxParser.parse(inputSource, handler);
        return handler.getImageURLs();
    }

    private class MyHandler extends DefaultHandler {
        private String[] imageURLs = new String[MainActivity.NUMBER];
        private int counter;

        public MyHandler() {
            counter = 0;
        }

        @Override
        public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
            if (name.equals("f:img") && counter < MainActivity.NUMBER) {
                if (attributes.getValue("size").equals("L")) {
                    imageURLs[counter] = attributes.getValue("href");
                    counter++;
                }
            }
        }

        @Override
        public void endDocument() throws SAXException {
            if (imageURLs.length == 0) {
                throw new SAXException();
            }
        }

        private String[] getImageURLs() {
            return imageURLs;
        }
    }

}