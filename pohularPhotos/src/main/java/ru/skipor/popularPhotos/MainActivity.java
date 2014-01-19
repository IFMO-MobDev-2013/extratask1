package ru.skipor.popularPhotos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ru.skipor.Utils.HTTPUtils;
import ru.skipor.Utils.InternalStorageUtils;
import ru.skipor.pohularPhotos.R;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final int PORTRAIT_COLUMN_NUM = 2;
    private static final int LANDSCAPE_COLUMN_NUM = 4;
    private static final int PORTRAIT_SPACING_PERCENT = 10;
    private static final int LANDSCAPE_SPACING_PERCENT = 4;
    private int columnWidth;
    private int minScreenDimension;
    private int maxSmallImageSize;


    private ImageAdapter adapter;
    private ArrayList<String> largePictureUrls;
    private AsyncTask currentTask;

    private DatabaseHelper myDatabaseHelper = DatabaseHelper.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDatabaseHelper.open();

        GridView gridView = (GridView) findViewById(R.id.grid_view);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int windowWidth = size.x;
        minScreenDimension = Math.min(size.x, size.y);

        maxSmallImageSize = Math.max(getLandscapeColumnWidth(Math.max(size.x, size.y)), getPortraitColumnWidth(Math.min(size.x, size.y)));
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

            gridView.setNumColumns(PORTRAIT_COLUMN_NUM);
            int spacing = windowWidth * PORTRAIT_SPACING_PERCENT / 100;
            gridView.setPadding(spacing, 0, spacing, 0);

            gridView.setHorizontalSpacing(spacing);
            gridView.setVerticalSpacing(spacing);
            columnWidth = getPortraitColumnWidth(windowWidth);
//            columnWidth = gridView

        } else {
            gridView.setNumColumns(LANDSCAPE_COLUMN_NUM);
            int spacing = windowWidth * LANDSCAPE_SPACING_PERCENT / 100;
            gridView.setPadding(spacing, 0, spacing, 0);
            gridView.setHorizontalSpacing(spacing);
            gridView.setVerticalSpacing(spacing);
            columnWidth = getLandscapeColumnWidth(windowWidth);

        }

        Log.d(TAG, "gridView column width is " + columnWidth);

        currentTask = null;
        largePictureUrls = null;

        adapter = new ImageAdapter(this);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, FullImageActivity.class);
                intent.putExtra(FullImageActivity.EXTRA_IMAGE_URL, largePictureUrls.get(position));
                startActivity(intent);
            }
        });

        loadSavedDataOrUpdate();
    }

    private int getLandscapeColumnWidth(int windowWidth) {
        return windowWidth * (100 - LANDSCAPE_SPACING_PERCENT * (LANDSCAPE_COLUMN_NUM + 1)) / 100 / LANDSCAPE_COLUMN_NUM;
    }

    private int getPortraitColumnWidth(int windowWidth) {
        return windowWidth * (100 - PORTRAIT_SPACING_PERCENT * (PORTRAIT_COLUMN_NUM + 1)) / 100 / PORTRAIT_COLUMN_NUM;
    }

    private class LoadSavedDataTask extends AsyncTask<Void, Bitmap, Void> {
        private  String TAG = "LoadSavedDataTask";
        @Override
        protected Void doInBackground(Void... params) {

            Cursor cursor = null;
            try{
                cursor = myDatabaseHelper.fetchAll();
                if(cursor == null || cursor.getCount() == 0) {
                    Log.d(TAG, " no correct saved data. Need to update");
                    cancel(true);
                    return null;
                }
                Log.d(TAG, "loading from saved data");
                largePictureUrls = new ArrayList<String>();
                int largeColumnNumber = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_LARGE_IMAGE_URL);
                int smallColumnNumber = cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SMALL_IMAGE_NAME);
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    largePictureUrls.add(cursor.getString(largeColumnNumber));
                    Log.d(TAG, "loading from storage " + cursor.getString(smallColumnNumber));
                   publishProgress(InternalStorageUtils.loadBitmap(MainActivity.this, cursor.getString(smallColumnNumber), columnWidth, columnWidth));
                    cursor.moveToNext();

                }



            } finally {
                if(cursor != null){
                    cursor.close();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);
            adapter.add(values[0]);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            Log.d(TAG, "onCancelled");
            super.onCancelled(aVoid);
            if(!HTTPUtils.checkConnection(MainActivity.this)) {
                Toast.makeText(MainActivity.this,"No internet connection", Toast.LENGTH_SHORT).show();
                return;

            }
            UpdateTask updateTask = new UpdateTask();
            currentTask = updateTask;
            updateTask.execute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "onPostExecute");
            super.onPostExecute(aVoid);
        }
    }

    private void loadSavedDataOrUpdate() {
        LoadSavedDataTask loadSavedDataTask = new LoadSavedDataTask();
        currentTask = loadSavedDataTask;
        loadSavedDataTask.execute();


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        myDatabaseHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                update();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void update() {

        if (currentTask == null || currentTask.getStatus() == AsyncTask.Status.FINISHED) {
            if(!HTTPUtils.checkConnection(this)) {
                Toast.makeText(this,"No internet connection", Toast.LENGTH_SHORT).show();

            }
            UpdateTask updateTask = new UpdateTask();
            currentTask = updateTask;
            updateTask.execute();
        }


    }

    private class UpdateTask extends AsyncTask<Void, Bitmap, String> {

        DatabaseHelper taskDatabaseHelper;
        boolean firstProgressUpdate;
        ArrayList<String> taskLargeImageUrls;

        private UpdateTask() {

            firstProgressUpdate = true;
            taskDatabaseHelper = DatabaseHelper.getInstance(MainActivity.this);
            taskLargeImageUrls = null;
        }



        @Override
        protected void onProgressUpdate(Bitmap... values) {
            super.onProgressUpdate(values);

            if(firstProgressUpdate) {
                firstProgressUpdate = false;

                largePictureUrls = taskLargeImageUrls;

                adapter.clear();


            }
            adapter.add(values[0]);
        }


        @Override
        protected String doInBackground(Void... params) {
            Log.d(TAG, "Update task starts");
            taskDatabaseHelper.open();

            try {
//                ArrayList<String> smallImagesFileNames = new ArrayList<String>();
                PhotoUrlsList urlsList = (new PopularPhotoDataProvider(maxSmallImageSize, minScreenDimension)).getPopularPhotos(); // getImage Urls
                taskDatabaseHelper.recreateTable(); //last state is invalid
                taskLargeImageUrls = urlsList.largeImageUrlList;
                for (String smallImageUrl : urlsList.smallImageUrlList) {

                    publishProgress(InternalStorageUtils.loadOrDownloadAndSaveBitmap(MainActivity.this, smallImageUrl)); // load small Images and publish to the UI
                }

                Set<String> necessaryFilesSet = new HashSet<String>();   // remember necessary files and write state to database
                for (int i = 0; i < urlsList.largeImageUrlList.size(); i++) {
                    String smallImageFileName = HTTPUtils.getFileName(urlsList.smallImageUrlList.get(i));
                    necessaryFilesSet.add(smallImageFileName);
                    String largeImageUrl = urlsList.largeImageUrlList.get(i);
                    necessaryFilesSet.add(HTTPUtils.getFileName(largeImageUrl));
                    taskDatabaseHelper.create(smallImageFileName, largeImageUrl);
                }

                String[] existingFileNames = fileList();   // delete unnecessary files
                for (String existingFile : existingFileNames) {
                    if (!necessaryFilesSet.contains(existingFile)) {
                        deleteFile(existingFile);
                    }
                }

                for (String largeImageUrl : urlsList.largeImageUrlList) {  // load large images witch not cached
                    InternalStorageUtils.downloadAndSaveBitmap(MainActivity.this, largeImageUrl);
                }


            } catch (HttpException e) {
                Log.e(TAG, "Error", e);
                e.printStackTrace();
                return "Connection problem";
            }


            taskDatabaseHelper.close();
            return "Update finished";
        }

        @Override
        protected void onPostExecute(String updateMessage) {
            super.onPostExecute(updateMessage);
            Log.d(TAG, updateMessage);
            Toast.makeText(MainActivity.this, updateMessage, Toast.LENGTH_SHORT).show();

        }
    }


    class ImageAdapter extends BaseAdapter {
        private Context mContext;

        private ArrayList<Bitmap> bitmaps;


        public ImageAdapter(Context c) {
            mContext = c;
            bitmaps = new ArrayList<Bitmap>();
        }

        public void add(Bitmap bitmap) {
            bitmaps.add(bitmap);
            notifyDataSetChanged();

        }

        public void clear() {
            bitmaps.clear();
            notifyDataSetChanged();
        }


        public int getCount() {
            return bitmaps.size();
        }

        public Bitmap getItem(int position) {
            return bitmaps.get(position);
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(columnWidth, columnWidth));
            } else {
                imageView = (ImageView) convertView;
            }

            imageView.setImageBitmap(getItem(position));

            return imageView;
        }



    }


}


