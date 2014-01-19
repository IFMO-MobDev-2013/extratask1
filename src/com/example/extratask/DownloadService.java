package com.example.extratask;


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("intentService");
    }

    public void onCreate() {
        super.onCreate();
    }

    private static final String url = "http://api.flickr.com/services/rest/?" +
            "method=flickr.interestingness.getList&" +
            "api_key=a5e90f753ad62a0f97eba79d831134e5&" +
            "per_page=20&" +
            "format=json&nojsoncallback=1";

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra("receiver");

        DataBase db = new DataBase(getApplicationContext());

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

        ResponseParser parser = new ResponseParser();
        ArrayList<String> urls = parser.getURLList(json);

        db.deleteAllImages();

        for (int i = 0; i < urls.size(); i++) {
            db.insertImage(getImage(urls.get(i)));
        }

        Bundle bundle = new Bundle();
        receiver.send(0, bundle);
    }

    private byte[] getImage(String url) {
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();

            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);

            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }

            return baf.toByteArray();
        } catch (Exception e) {
            Log.e("Downloading image", "error");
        }
        return null;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
