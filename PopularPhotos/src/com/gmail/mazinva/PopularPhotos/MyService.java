package com.gmail.mazinva.PopularPhotos;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MyService extends IntentService {

    private String[] imageURLs = new String[MainActivity.NUMBER];
    private MySAXParser parser = new MySAXParser();
    private SQLiteAccessor accessor;

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        accessor = new SQLiteAccessor(this);
        accessor.open();

        Bundle bundle = intent.getExtras();
        boolean downloadAll = bundle.getBoolean(MainActivity.DOWNLOAD_ALL);
        boolean updateAll = bundle.getBoolean(MainActivity.UPDATE_ALL);

        try {
            if (downloadAll) {
                setImageURLs();
                insertAllImagesInDB();
                Intent broadcastIntent = new Intent(MainActivity.MY_ACTION);
                broadcastIntent.putExtra(MainActivity.DOWNLOAD_ALL, true);
                sendBroadcast(broadcastIntent);
            }
            if (updateAll) {
                setImageURLs();
                accessor.deleteAll();
                insertAllImagesInDB();
                Intent broadcastIntent = new Intent(MainActivity.MY_ACTION);
                broadcastIntent.putExtra(MainActivity.UPDATE_ALL, true);
                sendBroadcast(broadcastIntent);
            }

        } catch (MySQLiteException e) {
            e.printStackTrace();
        }

        accessor.close();
    }

    private void insertAllImagesInDB() {
        try {
            for (int i = 0; i < imageURLs.length; i++) {
                accessor.insertImage(imageURLs[i], getBitmapFromURL(imageURLs[i]));
            }
        } catch (MySQLiteException e) {
            e.printStackTrace();
        }
    }

    private void setImageURLs() {
        try {
            InputStream inputStream = (new URL(MainActivity.URL).openStream());
            imageURLs = parser.parse(inputStream);
            inputStream.close();

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Bitmap getBitmapFromURL(String url) {
        try {
            InputStream inputStream = (new URL(url).openStream());
            Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}