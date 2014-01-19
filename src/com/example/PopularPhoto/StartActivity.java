package com.example.PopularPhoto;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.io.File;
import java.util.ArrayList;

public class StartActivity extends Activity {

    int HEIGHT;
    int WIDTH;
    MyBroadcastReceiver myBroadcastReceiver;
    IntentFilter intentFilter;
    TextView textView;
    PictureDataBaseHelper dataBaseHelper;
    SQLiteDatabase database;
    Button button;
    ArrayList<String> bigDelete;
    ArrayList<String> smallDelete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        button = (Button) findViewById(R.id.button);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        WIDTH = size.x;
        HEIGHT = size.y;
        textView = (TextView) findViewById(R.id.textView);

        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter(MyIntentService.key);
        registerReceiver(myBroadcastReceiver, intentFilter);

        dataBaseHelper = new PictureDataBaseHelper(getApplicationContext());
        database = dataBaseHelper.getReadableDatabase();
        Cursor cursor = database.query(PictureDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount() == 0) {
            if (hasInternetConnection()) {
                startingDownload();
                deleter();
            } else {
                showError(R.string.not_found);
            }
        } else {
            showImages();
        }
        database.close();
        dataBaseHelper.close();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(myBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    public void upDateClick(View view) {
        if (hasInternetConnection()) {
            startingDownload();
            deleter();
        } else {
            showError(R.string.internet);
        }
    }

    public void showImages() {
        ArrayList<String> smallName = new ArrayList<String>();
        ;
        final ArrayList<String> bigName = new ArrayList<String>();
        final ArrayList<String> title = new ArrayList<String>();
        Cursor cursor = database.query(PictureDataBaseHelper.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            smallName.add(cursor.getString(cursor.getColumnIndex(PictureDataBaseHelper.SMALL_IMAGE_NAME)));
            bigName.add(cursor.getString(cursor.getColumnIndex(PictureDataBaseHelper.BIG_IMAGE_NAME)));
            title.add(cursor.getString(cursor.getColumnIndex(PictureDataBaseHelper.TITLE)));
        }
        cursor.close();

        GridView gridView = (GridView) findViewById(R.id.grid);
        MyAdapter adapter = new MyAdapter(getApplicationContext(), smallName, getScreenOrientation());
        gridView.setAdapter(adapter);
        if (getScreenOrientation()) {
            gridView.setNumColumns(4);
            gridView.setColumnWidth((int) (WIDTH * 0.2));
        } else {
            gridView.setNumColumns(2);
            gridView.setColumnWidth((int) (WIDTH * 0.35));
        }
        gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        gridView.setVerticalSpacing(25);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(StartActivity.this, SecondActivity.class);
                intent.putExtra("name", bigName.get(i));
                intent.putExtra("title", title.get(i));
                startActivity(intent);

            }
        });
    }

    public void startingDownload() {
        textView.setText(R.string.wait);
        Intent intent = new Intent(StartActivity.this, MyIntentService.class);
        intent.putExtra("height", HEIGHT);
        intent.putExtra("width", WIDTH);
        button.setEnabled(false);
        startService(intent);
    }


    public void showError(int s) {
        Toast myToast = Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT);
        myToast.setGravity(Gravity.CENTER, 0, 0);
        myToast.show();
    }

    public boolean hasInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        if (netInfo == null) {
            return false;
        }
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected()) {
                    return true;
                }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected()) {
                    return true;
                }
        }
        return false;
    }

    private boolean getScreenOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return false;
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else
            return false;
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean result = intent.getExtras().getBoolean("error");
            if (!result) {
                dataBaseHelper = new PictureDataBaseHelper(getApplicationContext());
                database = dataBaseHelper.getReadableDatabase();
                showImages();
                database.close();
                dataBaseHelper.close();
                bigDelete = (ArrayList<String>) intent.getSerializableExtra("bigDelete");
                smallDelete = (ArrayList<String>) intent.getSerializableExtra("smallDelete");
                textView.setText(R.string.yandex_photo);
                showError(R.string.update);
                button.setEnabled(true);
            } else {
                textView.setText(R.string.yandex_photo);
                showError(R.string.error);
                button.setEnabled(true);
            }

        }
    }

    public void deleter() {
        if (bigDelete != null) {
            for (int i = 0; i < bigDelete.size(); i++) {
                File deleteFile = new File(getCacheDir(), bigDelete.get(i));
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
            }
        }
        if (smallDelete != null) {
            for (int i = 0; i < smallDelete.size(); i++) {
                File deleteFile = new File(getCacheDir(), smallDelete.get(i));
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
            }
        }
    }

    private class MyAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final ArrayList<String> name;
        private final boolean orientation;
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<String> name, boolean orientation) {
            super(context, R.layout.item, name);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.context = context;
            this.name = name;
            this.orientation = orientation;
        }

        @Override
        public View getView(int pos, View tmpView, ViewGroup parent) {
            View view = tmpView;
            if (view == null) {
                view = inflater.inflate(R.layout.item, parent, false);
            }
            ImageView imageView = (ImageView) view.findViewById(R.id.myImageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageURI(Uri.fromFile(new File(getApplicationContext().getCacheDir(), name.get(pos))));
            if (orientation)
                imageView.getLayoutParams().height = (int) (WIDTH * 0.2);
            else
                imageView.getLayoutParams().height = (int) (WIDTH * 0.35);
            return view;
        }
    }
}
