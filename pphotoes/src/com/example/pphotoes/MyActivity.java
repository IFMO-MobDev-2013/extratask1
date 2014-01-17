package com.example.pphotoes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

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

    private Context context = this;
    private List<Bitmap> btm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.textView)).setText("Downloading images...");
        AsyncTask<Void, Void, List<Bitmap>> translator = new Downloader();
        translator.execute();
    }

    public void onClick(View view) {
        AsyncTask<Void, Void, List<Bitmap>> translator = new Downloader();
        translator.execute();
        findViewById(R.id.button).setClickable(false);
        ((TextView) findViewById(R.id.textView)).setText("Updating...");
    }

    public void setRes(List<Bitmap> bm) {
        btm = bm;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new ImageAdapter(this, bm, width));
        gridview.setNumColumns(2);
        gridview.setVerticalSpacing(width / 20);
        gridview.setHorizontalSpacing(width / 18);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(context, ViewImageActivity.class);
                intent.putExtra("BI", btm.get(position));
                startActivity(intent);
            }
        });
        ((TextView) findViewById(R.id.textView)).setText("Images:");
        findViewById(R.id.button).setClickable(true);
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
            String query = "GET /api/top/published/?limit=20 HTTP/1.1\n" +
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