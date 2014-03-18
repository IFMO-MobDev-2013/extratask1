package com.paraboloid.toptwenty;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class SaxHandler extends DefaultHandler {

    private StringBuilder stringBuilder;
    private ArrayList<String> links;

    public SaxHandler(ArrayList<String> links) {
        super();
        this.links = links;
        stringBuilder = new StringBuilder();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        stringBuilder = new StringBuilder();
        if (qName.equals("f:img") && links.size() < 20) {
            if (attributes.getValue("size").equals("M")) {
                links.add(attributes.getValue("href"));
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        stringBuilder.append(ch, start, length);
    }

}
