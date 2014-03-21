package com.example.extratask1;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class ImageLoaderIntentService extends IntentService {
    boolean result = false;
    ArrayList<Bitmap> smallPictures = new ArrayList<Bitmap>();
    final String API = "http://api-fotki.yandex.ru/api/recent/";
    ArrayList<String> links = new ArrayList<String>();
    ArrayList<String> smallPicturesLinks = new ArrayList<String>();
    ArrayList<String> titles = new ArrayList<String>();
    int SCREEN_W;
    int SCREEN_H;
    public static final String key = "com.example.extratask1";

    public ImageLoaderIntentService(){
        super("ImageLoader");
    }

    @Override
    public void onHandleIntent(Intent intent){


        SCREEN_H = intent.getExtras().getInt("Height");
        SCREEN_W = intent.getExtras().getInt("Width");
        try{
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            saxParser.parse(new ByteArrayInputStream(EntityUtils.toString(new DefaultHttpClient().execute(new HttpGet(API)).getEntity()).getBytes()),
                    new RSSHandler());
            for(int i = 0; i < MyActivity.PIC_COUNT; i++){
                smallPictures.add(BitmapFactory.decodeStream(new URL(smallPicturesLinks.get(i)).openStream()));
            }
            if (smallPictures.size() != 0)
                result = true;

        } catch (IOException e){
        } catch (SAXException e){
        } catch (ParserConfigurationException e){
        }


        if(result){
            ImageDataBase imageDataBase = new ImageDataBase(getApplicationContext());
            SQLiteDatabase liteDatabase = imageDataBase.getWritableDatabase();
            liteDatabase.execSQL(ImageDataBase.SQL_DELETE_QUERY);
            imageDataBase.onCreate(liteDatabase);
            for(int i = 0; i < MyActivity.PIC_COUNT; i++){
                ContentValues values = new ContentValues();
                Converter.putBitmapInConvertValue(values, smallPictures.get(i), imageDataBase.getSmallPicRow());
                values.put(imageDataBase.getSmallHeight(), smallPictures.get(i).getHeight());
                values.put(imageDataBase.getSmallWidth(), smallPictures.get(i).getWidth());
                values.put(imageDataBase.getBigPicLink(), smallPicturesLinks.get(i));
                values.put(imageDataBase.getTitleRow(), titles.get(i));
                liteDatabase.insert(imageDataBase.getTableName(), null, values);
            }
            liteDatabase.close();
            imageDataBase.close();

        }
        Intent answer = new Intent();
        answer.putExtra("result", result);
        answer.setAction(key);
        sendBroadcast(answer);

    }

    private class RSSHandler extends DefaultHandler {
        int currentHeight = -1;
        int currentWidth = -1;
        int currentBestW = -1;
        int currentBestH = -1;
        String currentLink;
        String currentSmallLink;
        final String FILE = "f:img";
        final String ENTRY = "entry";
        final String TITLE = "title";
        boolean entry = false;
        boolean title = false;
        final double DELTA = 0.1;
        final double MAX_SIZE = 220;
        String buffer = "";

        public void startElement(String uri, String localName, String qName,
                                 Attributes attrs)throws SAXException{

            buffer = "";
            if(ENTRY.equals(localName)){
                entry = true;
            }

            if(TITLE.equals(localName) && entry){
                title = true;
            }

            if(FILE.equals(qName) && entry){
                int h = Integer.parseInt(attrs.getValue("height"));
                int w = Integer.parseInt(attrs.getValue("width"));
                if(w > currentWidth && w < SCREEN_W){
                    currentWidth = w;
                    currentHeight = h;
                    currentLink = attrs.getValue("href");
                }
                if(w > currentWidth && h > currentHeight && w < SCREEN_W && h < SCREEN_H){
                    currentWidth = w;
                    currentHeight = h;
                    currentLink = attrs.getValue("href");
                }
                int cHW = (int) Math.min(Math.max(Math.min(SCREEN_W, SCREEN_H) * (0.35 + DELTA), Math.max(SCREEN_W, SCREEN_H) * (0.2 + DELTA)), MAX_SIZE);
                if(currentBestW * currentBestH == 1){
                    currentBestW = w;
                    currentBestH = h;
                    currentSmallLink = attrs.getValue("href");
                }
                if(w >= cHW && h >= cHW && (w < currentBestW || currentBestW < cHW)){
                    currentBestW = w;
                    currentBestH = h;
                    currentSmallLink = attrs.getValue("href");
                }
            }
        }

        public void endElement(String uri, String localName, String qName)throws SAXException{
            if(ENTRY.equals(localName)){
                entry = false;
                currentHeight = -1;
                currentWidth = -1;
                currentBestH = -1;
                currentBestW = -1;
                links.add(currentLink);
                smallPicturesLinks.add(currentSmallLink);
            }

            if(TITLE.equals(localName) && title){
                titles.add(buffer);
                title = false;
            }
            buffer = "";
        }

        @Override
        public void characters(char[] ch, int start, int length){
            if(title)
                buffer += new String(ch, start, length);

        }

    }
}