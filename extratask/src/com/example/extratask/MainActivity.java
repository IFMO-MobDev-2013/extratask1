package com.example.extratask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {

    public static boolean is_first = true;
    public static boolean is_land;
    public static Bitmap[] bitmaps = new Bitmap[20];
    public static String[] full_size_bitmaps = new String[20];
    public static int width;
    public static GridView gridView;
    public static Context context;
    public static int height;
    public static final String KEY_POSITION = "position";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        is_land = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
        setContentView(R.layout.main);
     //   full_size_bitmaps = new String[20];
      //  bitmaps = new Bitmap[20];
        gridView = (GridView) findViewById(R.id.gridView);
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        width = size.x;
        height = size.y;
        if (is_land) {
            gridView.setNumColumns(4);
            gridView.setVerticalSpacing((int)(width * 0.04));
            gridView.setHorizontalSpacing((int)(width * 0.04));
            gridView.setPadding((int)(width * 0.04), (int)(width * 0.04), (int)(width * 0.04), (int)(width * 0.04));
        } else {
            gridView.setNumColumns(2);
            gridView.setVerticalSpacing((int)(width * 0.1));
            gridView.setHorizontalSpacing((int)(width * 0.1));
            gridView.setPadding((int)(width * 0.1), (int)(width * 0.1), (int)(width * 0.1), (int)(width * 0.1));
        }
        if (checkInternetConnection()) {
            if (is_first) {
                new Downloader().execute();
                is_first = false;
            }
        }  else {
            Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, FullSizeImageActivity.class);
                intent.putExtra(KEY_POSITION, i);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item1) {
            new Downloader().execute();
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        is_land = !is_land;
        if (is_land) {
            gridView.setNumColumns(4);
            gridView.setVerticalSpacing((int)(width * 0.04));
            gridView.setHorizontalSpacing((int)(width * 0.04));
            gridView.setPadding((int)(width * 0.04), (int)(width * 0.04), (int)(width * 0.04), (int)(width * 0.04));
        } else {
            gridView.setNumColumns(2);
            gridView.setVerticalSpacing((int)(width * 0.1));
            gridView.setHorizontalSpacing((int)(width * 0.1));
            gridView.setPadding((int)(width * 0.1), (int)(width * 0.1), (int)(width * 0.1), (int)(width * 0.1));
        }
        setGridViewAdapter(bitmaps);
    }

    public static void setGridViewAdapter(Bitmap[] bitmaps) {
        gridView.setAdapter(new ImageAdapter(context, bitmaps, width, is_land));
    }

    public boolean checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo : networkInfos ) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        return false;
    }

    public class Downloader extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage(getResources().getString(R.string.download));
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
          //  Bitmap[] res = new Bitmap[20];
            try {
                String query = "http://api-fotki.yandex.ru/api/recent/";
                URL url = new URL(query);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db =dbf.newDocumentBuilder();
                BufferedInputStream is = new BufferedInputStream(url.openStream());
                Document doc = db.parse(is);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("entry");
                for (int i = 0; i < 20; i++) {
                    Node node = nodeList.item(i);
                    NodeList childs = node.getChildNodes();
                    Element e = (Element) childs.item(37);
                    String link = e.getAttribute("href");
                    URL imageUrl = new URL(link);
                    HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                   // res[i] = BitmapFactory.decodeStream(input);
                    MainActivity.bitmaps[i] = BitmapFactory.decodeStream(input);

                    Element el = (Element) childs.item(33);
                    int w = Integer.parseInt(el.getAttribute("width"));
                    int h = Integer.parseInt(el.getAttribute("height"));
                    String temp = el.getAttribute("href");
                    for (int j = 35; j <= childs.getLength() - 4; j += 2) {
                        el = (Element) childs.item(j);
                        int w1 = Integer.parseInt(el.getAttribute("width"));
                        int h1 = Integer.parseInt(el.getAttribute("height"));
                        if (w < MainActivity.width && h < MainActivity.height && w1 > w && h1 > h) {
                            temp = el.getAttribute("href");
                            w = w1;
                            h = h1;
                        }
                    }
                    MainActivity.full_size_bitmaps[i] = temp;

                   /*
                    imageUrl = new URL(temp);
                    connection = (HttpURLConnection) imageUrl.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    input = connection.getInputStream();
                    MainActivity.full_size_bitmaps[i] = BitmapFactory.decodeStream(input);
                      */

                }

            } catch (Exception e) {
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void bitmaps) {
            if (dialog != null) {
                dialog.dismiss();
            }
            MainActivity.setGridViewAdapter(MainActivity.bitmaps);
         //   MainActivity.bitmaps =  bitmaps;
        }
    }
}
