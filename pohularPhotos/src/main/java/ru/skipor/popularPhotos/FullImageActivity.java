package ru.skipor.popularPhotos;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpException;

import ru.skipor.Utils.InternalStorageUtils;
import ru.skipor.pohularPhotos.R;

public class FullImageActivity extends Activity {

    private static String TAG = "FullImageActivity";
    public static final String EXTRA_IMAGE_URL = "image url";

    private ImageView imageView;
    private TextView informationTextView;
    private  String extraUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        extraUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
        setContentView(R.layout.activity_full_image);

        imageView = ((ImageView) findViewById(R.id.image_view));
        informationTextView = ((TextView) findViewById(R.id.information_text_view));

        setImage();



    }


    private void setImage() {
        LoadImageTask loadImageTask = new LoadImageTask();
        loadImageTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



    }

    private class LoadImageTask extends AsyncTask<Void, Bitmap, String> {
        @Override
        protected String doInBackground(Void... params) {
            try {
                publishProgress(InternalStorageUtils.loadOrDownloadAndSaveBitmap(FullImageActivity.this, extraUrl));
            } catch (HttpException e) {
                Log.e(TAG, "Error", e);
                e.printStackTrace();
                return "Connection Error";
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            imageView.setImageBitmap(values[0]);

        }

        @Override
        protected void onPostExecute(String updateMessage) {

            super.onPostExecute(updateMessage);

            informationTextView.setText(updateMessage);
        }
    }



}
