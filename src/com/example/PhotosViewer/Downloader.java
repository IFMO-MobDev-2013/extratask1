package com.example.PhotosViewer;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Downloader {

    public boolean successfulDownload = false;
    String defaultEncoding = "ISO-8859-1";

    public static String APIADDRESS = "http://api-fotki.yandex.ru/api/recent/";

    private boolean downloadPage(String urlString, MySAXParser saxParser) throws XmlPullParserException, IOException, ParserConfigurationException, SAXException {

        boolean result = false;
        InputStream stream = null;

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        XMLReader xmlReader = parser.getXMLReader();
        xmlReader.setContentHandler(saxParser);

        URL url;
        HttpURLConnection conn;
        try {
            url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(7000);
            conn.setConnectTimeout(10000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            stream = conn.getInputStream();
            if (stream == null) {
                result = false;
                System.out.println("error stream");
            } else {
                InputSource inputSource = new InputSource(stream);
                System.out.println("encode " + defaultEncoding);
                inputSource.setEncoding(defaultEncoding);
                xmlReader.parse(inputSource);
                System.out.println("count " + saxParser.array.size());
                result = true;
            }
        } catch (Exception e) {
            result = false;
            System.out.println("count_bad_" + saxParser.array.size());
            e.printStackTrace();
        } finally {
            if (stream != null) {
                stream.close();
            }

        }
        return result;
    }

    public Downloader() {
        successfulDownload = false;
    }

    public Downloader(String url, MySAXParser saxParser) throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {

        try {
            successfulDownload = downloadPage(url, saxParser);
            if (successfulDownload)
                successfulDownload = saxParser.successfulDownload;
        } catch (Exception e) {
            successfulDownload = false;
        }
    }
}
