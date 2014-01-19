package com.example.PopularPhoto;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.Time;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

public class MyIntentService extends IntentService {

    private static final String API_link = "http://api-fotki.yandex.ru/api/recent/";
    public static final String key = "com.example.PopularPhoto";
    boolean error;

    int displayHEIGHT;
    int displayWIDTH;
    ArrayList<String> smallLinks;
    ArrayList<String> bigLinks;
    ArrayList<String> titles;
    ArrayList<Bitmap> smallImages;
    ArrayList<Bitmap> bigImages;
    ArrayList<String> bigDelete = new ArrayList<String>();
    ArrayList<String> smallDelete = new ArrayList<String>();
    String folderToSave;
    Cursor cursor;

    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        error = false;
        try {
            displayHEIGHT = intent.getExtras().getInt("height");
            displayWIDTH = intent.getExtras().getInt("width");
            bigLinks = new ArrayList<String>();
            smallLinks = new ArrayList<String>();
            bigImages = new ArrayList<Bitmap>();
            smallImages = new ArrayList<Bitmap>();
            titles = new ArrayList<String>();
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(API_link);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            String inform = EntityUtils.toString(httpResponse.getEntity());
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new ByteArrayInputStream(inform.getBytes()), new RSSHandler(smallLinks, bigLinks,
                    titles, displayHEIGHT, displayWIDTH));
            if (smallLinks.size() != 0 && bigLinks.size() != 0 && titles.size() != 0) {
                for (int i = 0; i < 20; i++) {
                    smallImages.add(BitmapFactory.decodeStream(new URL(smallLinks.get(i)).openStream()));
                    if (smallLinks.get(i) == bigLinks.get(i)) {
                        bigImages.add(smallImages.get(i));
                    } else {
                        bigImages.add(BitmapFactory.decodeStream(new URL(bigLinks.get(i)).openStream()));
                    }
                }
                if (smallImages.size() == 0 || bigImages.size() == 0) {
                    error = true;
                }
            } else {
                error = true;
            }
        } catch (SAXException e) {
            error = true;
        } catch (IOException e) {
            error = true;
        } catch (ParserConfigurationException e) {
            error = true;
        }
        if (!error) {
            folderToSave = getApplicationContext().getCacheDir().toString();
            PictureDataBaseHelper dataBaseHelper = new PictureDataBaseHelper(getApplicationContext());
            SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
            bigDelete.clear();
            smallDelete.clear();

            cursor = database.query(PictureDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                bigDelete.add(cursor.getString(cursor.getColumnIndex(PictureDataBaseHelper.BIG_IMAGE_NAME)));
                smallDelete.add(cursor.getString(cursor.getColumnIndex(PictureDataBaseHelper.SMALL_IMAGE_NAME)));
            }
            cursor.close();
            database.execSQL(PictureDataBaseHelper.DELETE);
            dataBaseHelper.onCreate(database);
            for (int i = 0; i < 20; i++) {
                Time time = new Time();
                time.setToNow();
                String bigName = Integer.toString(time.hour) + Integer.toString(time.minute)
                        + Integer.toString(time.second) + "big" + Integer.toString(i) + ".jpg";
                String smallName = Integer.toString(time.hour) + Integer.toString(time.minute)
                        + Integer.toString(time.second) + "small" + Integer.toString(i) + ".jpg";
                try {
                    savePicture(bigName, i);
                    savePicture(smallName, i);
                } catch (IOException e) {
                    error = true;
                }
                if (!error) {

                    ContentValues values = new ContentValues();
                    values.put(PictureDataBaseHelper.TITLE, titles.get(i));
                    values.put(PictureDataBaseHelper.BIG_IMAGE_NAME, bigName);
                    values.put(PictureDataBaseHelper.SMALL_IMAGE_NAME, smallName);
                    database.insert(PictureDataBaseHelper.TABLE_NAME, null, values);
                }
            }
            database.close();
            dataBaseHelper.close();
        }

        Intent result = new Intent();
        result.putExtra("error", error);
        result.putExtra("bigDelete", bigDelete);
        result.putExtra("smallDelete", smallDelete);
        result.setAction(key);
        sendBroadcast(result);
    }

    public void savePicture(String name, int i) throws IOException {
        OutputStream fOut = null;
        Time time = new Time();
        time.setToNow();
        File file = new File(folderToSave, name);
        fOut = new FileOutputStream(file);

        Bitmap bitmap = bigImages.get(i);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        fOut.flush();
        fOut.close();
    }
}
