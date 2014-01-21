package com.example.extratask1;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: PWR
 * Date: 19.01.14
 * Time: 19:55
 * To change this template use File | Settings | File Templates.
 */
public class DownloadPictures extends IntentService
{

    private static final String url = "http://api-fotki.yandex.ru/api/top/published/?limit=20&format=json";

    public DownloadPictures()
    {
        super("intentService");
    }

    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

        ResultReceiver receiver = intent.getParcelableExtra("receiver");
        DataBases database = new DataBases(getApplicationContext());
        JSONObject json = null;

        try {
            InputStream is = new URL(url).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
            is.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        URLParser parser = new URLParser();
        ArrayList<String> urls = parser.getURLs(json);

        database.delPictures();

        for (int i = 0; i < urls.size(); i++)
            database.newPicture(getPicture(urls.get(i)));

        Bundle bundle = new Bundle();
        receiver.send(0, bundle);
    }

    private String readAll(Reader reader) throws IOException
    {
        StringBuilder stringBuilder = new StringBuilder();
        int tmp;

        while((tmp = reader.read()) != -1)
            stringBuilder.append((char) tmp);

        return stringBuilder.toString();

    }

    private byte[] getPicture(String url)
    {
        try
        {

            URL pictureUrl = new URL(url);
            URLConnection ucon = pictureUrl.openConnection();
            InputStream iStream = ucon.getInputStream();
            BufferedInputStream buffIStream = new BufferedInputStream(iStream);
            ByteArrayBuffer byteArrBuffer = new ByteArrayBuffer(700);
            int current = 0;

            while ((current = buffIStream.read()) != -1) {
                byteArrBuffer.append((byte) current);
            }

            return byteArrBuffer.toByteArray();

        } catch (Exception e) {}

        return null;

    }

}
