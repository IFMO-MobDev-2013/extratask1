package ru.ifmo.mobdev.extra;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;

public class FirstScreen extends Activity {

    ArrayList<String> links = new ArrayList<String>();
    ArrayList<Bitmap> imageBitmaps = new ArrayList<Bitmap>();

    GridView gridView;
    ImageAdapter gridAdapter;
    DisplayMetrics metrics = new DisplayMetrics();

    int screenHeight = metrics.heightPixels;
    int screenWidth = metrics.widthPixels;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        final String requestedURL = getString(R.string.ya_fotki_url);

        DownloadData.setMetrics(screenHeight, screenWidth);

        Button button = (Button)findViewById(R.id.button);

        gridView = (GridView)findViewById(R.id.gridView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageBitmaps.clear();

                new ParserTask().execute(requestedURL);

                new ImageDownloaderTask().execute();

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FirstScreen.this, SecondScreen.class);
                DownloadData.currentImage = imageBitmaps.get(position);
                startActivity(intent);

            }
        });

    }

    class ParserTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            try {
                links = TParser.parse(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    class ImageDownloaderTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            for (String url : links) {
                try {
                    Bitmap bm = TDownloader.download(url);
                    imageBitmaps.add(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            gridAdapter = new ImageAdapter(FirstScreen.this, imageBitmaps);

            gridView.setAdapter(gridAdapter);

            gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);

        }
    }

}
