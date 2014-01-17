package com.example.pphotoes;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MyActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        AsyncTask<Void, Void, List<Bitmap>> translator = new Downloader();
        translator.execute();
    }

    public void setRes(List<Bitmap> bm) {
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, bm, 100, 100, 10));
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            }
        });
    }

    private class Downloader extends AsyncTask<Void, Void, List<Bitmap>> {

        public Bitmap getBitmapFromURL(String src) {
            int i = src.length() - 1;
            while (i >= 0) {
                if (src.charAt(i) == '_') {
                    src = src.substring(0, i + 1) + "M";
                    break;
                }
                i--;
            }
            try {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected List<Bitmap> doInBackground(Void... v) {
            String query = "GET /api/top/published/?limit=2 HTTP/1.1\n" +
                    "Host: api-fotki.yandex.ru\n" +
                    "Accept: application/atom+xml; type=entry\n", result = "";
            try {
                Socket s = new Socket("api-fotki.yandex.ru", 80);
                PrintStream out = new PrintStream(s.getOutputStream());
                out.println(query);
                Scanner scanner = new Scanner(s.getInputStream());
                while (scanner.hasNext()) {
                    result += scanner.nextLine();
                }
            } catch (IOException e) {
            }
            List<Bitmap> images = new ArrayList<Bitmap>();
            for (int i = 0; i < result.length() - 15; i++) {
                if (result.substring(i, i + 14).equals("<content src=\"")) {
                    String url = "";
                    for (int j = i + 14; ; j++) {
                        char c = result.charAt(j);
                        if (c == '"') {
                            i = j;
                            break;
                        }
                        url += c;
                    }
                    images.add(getBitmapFromURL(url));
                }
            }
            return images;
        }

        @Override
        protected void onPostExecute(List<Bitmap> result) {
            super.onPostExecute(result);
            setRes(result);
        }
    }
}