package com.example.javlon.extratask1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: javlon
 * Date: 19.01.14
 * Time: 23:09
 * To change this template use File | Settings | File Templates.
 */
public class MyHandler extends DefaultHandler {
    private int countPictures;
    private ArrayList<Bitmap> result;
    @Override
    public void startDocument() throws SAXException {
        countPictures = 0;
        result = new ArrayList<Bitmap>();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (countPictures >= 20)
            return;
        if (qName.equals("f:img") && attributes.getValue(attributes.getIndex("size")).equals("M")) {
            try {
                result.add(BitmapFactory.decodeStream(new URL(attributes.getValue(attributes.getIndex("href"))).openStream()));
                countPictures++;
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    public ArrayList<Bitmap> getResult() {
        return result;
    }
}
