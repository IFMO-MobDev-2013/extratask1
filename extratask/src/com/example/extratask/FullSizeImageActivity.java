package com.example.extratask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import org.apache.http.HttpConnection;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: mv
 * Date: 19.01.14
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class FullSizeImageActivity extends Activity {

    public ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullsize);
        imageView = (ImageView) findViewById(R.id.imageView) ;
        int pos = getIntent().getIntExtra(MainActivity.KEY_POSITION, 0);
        String link = MainActivity.full_size_bitmaps[pos];
        new FullSizeImageDownloader().execute(link);

    }

    public class FullSizeImageDownloader extends AsyncTask<String, Void, Bitmap> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(FullSizeImageActivity.this);
            dialog.setMessage(getResources().getString(R.string.download));
            dialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap;
            try {
                URL imageUrl = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                Log.d("eroer", "ERROR");
                return null;
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (dialog != null) {
                dialog.dismiss();
            }
            if (bitmap != null) {

                 imageView.setImageBitmap(bitmap);
            }
        }
    }
}
