package com.android.example.dronov;

import android.graphics.BitmapFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 19.01.14
 * Time: 8:18
 * To change this template use File | Settings | File Templates.
 */
public class YandexHandler extends DefaultHandler {
    private static final int PICTURES_COUNT = 20;
    private static final String PICTURES_SIZE = "M";
    private List<Picture> list;
    private int count;
    @Override
    public void startDocument() throws SAXException {
        list = new ArrayList<Picture>();
        count = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (count < PICTURES_COUNT)
            if (qName.equals("f:img") && attributes.getValue(attributes.getIndex("size")).equals(PICTURES_SIZE)) {
                try {
                    int width = Integer.parseInt(attributes.getValue(attributes.getIndex("width")));
                    int height = Integer.parseInt(attributes.getValue(attributes.getIndex("height")));
                    list.add(new Picture(
                            width,
                            height,
                            BitmapFactory.decodeStream(new URL(attributes.getValue(attributes.getIndex("href"))).openStream())
                           ));
                    count++;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
    }

    public List<Picture> getResult() {
        return list;
    }

}
