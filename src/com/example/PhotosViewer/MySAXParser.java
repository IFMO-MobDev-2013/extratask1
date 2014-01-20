package com.example.PhotosViewer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class MySAXParser extends DefaultHandler {

    ArrayList<String> array;
    boolean successfulDownload;
    int count;

    MySAXParser() {
        array = new ArrayList<String>();
    }

    @Override
    public void startDocument() throws SAXException {
        array = new ArrayList<String>();
        System.out.println("Start document rss");
        count = 0;
        successfulDownload = false;
    }

    @Override
    public void startElement(String uri, String local_name, String raw_name, Attributes amap) throws SAXException {

        //System.out.println("Start element:" + local_name + "  count = " + count);
        if (count < 20 && ("f:img".equals(local_name) || "img".equals(local_name))) {
            if (MyActivity.IMAGE_SIZE.equals(amap.getValue(amap.getIndex("size")))) {
                array.add(amap.getValue(amap.getIndex("href")));
                System.out.println("img        =" + amap.getValue(amap.getIndex("href")));
                count++;
            }
        }
    }

    @Override
    public void endDocument() throws SAXException {

        successfulDownload = (count == 20);
    }
}
