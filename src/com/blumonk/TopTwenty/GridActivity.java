package com.blumonk.TopTwenty;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.InputSource;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class GridActivity extends Activity {

    private GridView grid;
    private Button refresh;
    private int width;
    private int padding;
    private DbAdapter dbAdapter;
    private Context context;
    private boolean portrait;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        context = this;
        grid = (GridView) findViewById(R.id.gridView);
        refresh = (Button) findViewById(R.id.refresh);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;

        portrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        if (portrait) {
            padding = width / 10;
            grid.setPadding(padding, padding, padding, padding);
            grid.setNumColumns(2);
            grid.setVerticalSpacing(padding);
            grid.setHorizontalSpacing(padding);
        } else {
            padding = width / 25;
            grid.setPadding(padding, padding, padding, padding);
            grid.setNumColumns(4);
            grid.setVerticalSpacing(padding);
            grid.setHorizontalSpacing(padding);
        }

        dbAdapter = new DbAdapter(this);
        dbAdapter.open();

        final ArrayList<Bitmap> bitmaps = dbAdapter.getAllPics();
        grid.setAdapter(new GridViewAdapter(this, bitmaps, width, portrait));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra("pic", bitmaps.get(position));
                startActivity(intent);
            }
        });
    }

    public void refreshPictures(View view) {
        AsyncTask<Void, Void, ArrayList<Bitmap>> asyncTask = new GetPicsTask();
        asyncTask.execute();
    }

    private void processNewPics(ArrayList<Bitmap> bitmaps) {
        if (bitmaps == null) {
            bitmaps = dbAdapter.getAllPics();
        }
        final ArrayList<Bitmap> pics = bitmaps;
        grid.setAdapter(new GridViewAdapter(this, pics, width, portrait));
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent intent = new Intent(context, ImageActivity.class);
                intent.putExtra("pic", pics.get(position));
                startActivity(intent);
            }
        });
        if (bitmaps != null && bitmaps.size() == 20) {
            dbAdapter.deletePics();
            for (int i = 0; i < 20; ++i) {
                dbAdapter.addPic(bitmaps.get(i));
            }
        }
    }

    private class GetPicsTask extends AsyncTask<Void, Void, ArrayList<Bitmap>> {

        public static final String XML = "http://api-fotki.yandex.ru/api/recent/";
        private ArrayList<String> links;
        private ProgressDialog dialog;

        @Override
        protected ArrayList<Bitmap> doInBackground(Void... params) {
            try {
                links = new ArrayList<String>();
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SAXParser saxParser = saxParserFactory.newSAXParser();
                HttpResponse httpResponse = new DefaultHttpClient().execute(new HttpGet(XML));
                HttpEntity httpEntity = httpResponse.getEntity();
                String xml = EntityUtils.toString(httpEntity, "UTF-8");
                InputSource is = new InputSource(new StringReader(xml));
                saxParser.parse(is, new SaxHandler(links));
                ArrayList<Bitmap> pics = new ArrayList<Bitmap>();
                for (int i = 0; i < 20; ++i) {
                    pics.add(downloadPic(links.get(i)));
                }
                return pics;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage("Downloading...");
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {
            super.onPostExecute(bitmaps);
            dialog.dismiss();
            if (bitmaps == null || bitmaps.size() == 0) {
                Toast.makeText(context, "No internet connection...", Toast.LENGTH_LONG).show();
            }
            processNewPics(bitmaps);
        }

        private Bitmap downloadPic(String source) {
            try {
                URL url = new URL(source);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(input);
                return bitmap;
            } catch (Exception e) {
                return null;
            }
        }
    }
}
