package com.example.PopularPhoto;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class RSSHandler extends DefaultHandler {
    ArrayList<String> smallLinks;
    ArrayList<String> bigLinks;
    ArrayList<String> titles;

    final String IMAGE = "f:img";
    final String ENTRY = "entry";
    final String TITLE = "title";
    boolean entry = false;
    boolean title = false;
    String buffer = "";

    final int displayHEIGHT;
    final int displayWIDTH;
    int smallHeight;
    int smallWidth;
    int bigWidth;
    int bigHeight;
    String bigLink;
    String smallLink;

    RSSHandler(ArrayList<String> smallLinks, ArrayList<String> bigLinks, ArrayList<String> titles, int h, int w) {
        super();
        this.titles = titles;
        this.bigLinks = bigLinks;
        this.smallLinks = smallLinks;
        this.titles.clear();
        displayHEIGHT = h;
        displayWIDTH = w;
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {

        buffer = "";
        if (TITLE.equals(localName) && entry) {
            title = true;
        }

        if (ENTRY.equals(localName)) {
            entry = true;
        }

        if (IMAGE.equals(qName) && entry) {
            int height = Integer.parseInt(attrs.getValue("height"));
            int width = Integer.parseInt(attrs.getValue("width"));

            if (width > bigWidth && width < displayWIDTH) {
                bigWidth = width;
                bigHeight = height;
                bigLink = attrs.getValue("href");
            }

            if (width > bigWidth && height > bigWidth && width < displayWIDTH && height < displayHEIGHT) {
                bigWidth = width;
                bigHeight = height;
                bigLink = attrs.getValue("href");
            }

            if (width > smallWidth && width <= displayWIDTH * 0.35) {
                smallWidth = width;
                smallHeight = height;
                smallLink = attrs.getValue("href");
            }

            if (width > smallWidth && height > smallWidth && width < displayWIDTH * 0.35 && height < displayHEIGHT * 0.2) {
                smallWidth = width;
                smallHeight = height;
                smallLink = attrs.getValue("href");
            }
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (TITLE.equals(localName) && title) {
            title = false;
            titles.add(buffer);
        }

        if (ENTRY.equals(localName)) {
            entry = false;
            upDate();
            bigLinks.add(bigLink);
            smallLinks.add(smallLink);
        }
        buffer = "";
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (title) {
            buffer += new String(ch, start, length);
        }

    }

    public void upDate() {
        smallHeight = 0;
        smallWidth = 0;
        bigWidth = 0;
        bigHeight = 0;
    }
}
