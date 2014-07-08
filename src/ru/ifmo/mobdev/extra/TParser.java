package ru.ifmo.mobdev.extra;

import android.util.Xml;
import android.widget.ImageView;
import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class TParser {

    static String ENTRY_TAG = new String("entry");
    static String FIMG_TAG = new String("img");

    static int photosNumber = DownloadData.IMAGES_NUMBER;

    public static ArrayList<String> parse(String urlString) throws Exception{

        ArrayList<String> links = new ArrayList<String>();
        ArrayList<ImageView> images = new ArrayList<ImageView>();
        ArrayList<DownloadData> data = new ArrayList<DownloadData>();

        XmlPullParser parser = Xml.newPullParser();

        URL url = new URL(urlString);
        InputStream inputStream = url.openStream();
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        if (eventType == XmlPullParser.START_DOCUMENT) {
            eventType = parser.next();
            while (true) {
                if (eventType == XmlPullParser.START_TAG
                        && parser.getName().equals(ENTRY_TAG))
                    break;
                eventType = parser.next();
            }
        }
        for (int i = 0; i < photosNumber; i++) {
            while (true) {
                if (eventType == XmlPullParser.START_TAG
                        && parser.getName().equals(FIMG_TAG))
                    break;
                eventType = parser.next();
            }

            int bestHeight = 0, bestWidth = 0;

            String bestLink = null;

            while (eventType == XmlPullParser.START_TAG && parser.getName().equals(FIMG_TAG)) {
                String currentLink = null;
                int height = 0, width = 0;
                for (int j = 0; j < parser.getAttributeCount(); j++) {
                    if (parser.getAttributeName(j).equals("href"))
                        currentLink = new String(parser.getAttributeValue(j));
                    if (parser.getAttributeName(j).equals("height"))
                        height = Integer.parseInt(parser.getAttributeValue(j));
                    if (parser.getAttributeName(j).equals("width"))
                        width = Integer.parseInt(parser.getAttributeValue(j));
                }

                /*
                if (currentLink != null && height == 75 && width == 75) {
                    links.add(currentLink);
                }
                */

                if (currentLink != null
                        && (height >= bestHeight && height <= DownloadData.screenHeight)
                        && (width >= bestWidth && width <= DownloadData.screenWidth)) {
                    bestLink = new String(currentLink);
                    bestHeight = height;
                    bestWidth = width;
                }

                do {
                    eventType = parser.next();
                } while (eventType != XmlPullParser.START_TAG);
            }

            links.add(bestLink);

            while (true) {
                if (eventType == XmlPullParser.START_TAG
                        && parser.getName().equals(ENTRY_TAG))
                    break;
                eventType = parser.next();
            }
        }

        return links;

    }
}
