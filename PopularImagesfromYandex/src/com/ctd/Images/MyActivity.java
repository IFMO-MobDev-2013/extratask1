package com.ctd.Images;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Button btnUpdate;

    ArrayList<String> arrayList;
    ArrayList<Pair> pairs;
    ArrayList<Quadro> quadros;

    void fillArray() {
        ImagesDataBaseHelper imagesDataBaseHelper = new ImagesDataBaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = imagesDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(ImagesDataBaseHelper.DATABASE_NAME, null, null, null, null, null, null);
        int pathColumn = cursor.getColumnIndex(ImagesDataBaseHelper.PATH);
        arrayList.clear();
        pairs.clear();
        quadros.clear();
        while (cursor.moveToNext()) {
            arrayList.add(cursor.getString(pathColumn));
        }
        cursor.close();
        sqLiteDatabase.close();
        imagesDataBaseHelper.close();
        for (int i = 0; i < arrayList.size(); i += 2) {
            pairs.add(new Pair(arrayList.get(i), arrayList.get(i + 1)));
        }
        for (int i = 3; i < arrayList.size(); i += 4) {
            quadros.add(new Quadro(arrayList.get(i - 3), arrayList.get(i - 2), arrayList.get(i - 1), arrayList.get(i)));
        }

        cursor.close();
        sqLiteDatabase.close();
        imagesDataBaseHelper.close();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            drawableAdapter.notifyDataSetChanged();
        } else {
            drawableAdapterLandscape.notifyDataSetChanged();
        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("RESULT", false)) {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.download_completed), Toast.LENGTH_LONG).show();
                fillArray();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getText(R.string.download_failed), Toast.LENGTH_LONG).show();
            }
            btnUpdate.setClickable(true);
        }
    };


    DrawableAdapter drawableAdapter;
    DrawableAdapterLandscape drawableAdapterLandscape;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
                builder.setTitle(R.string.warning);
                builder.setMessage(R.string.message);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnUpdate.setClickable(false);
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.download_started), Toast.LENGTH_LONG).show();
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            pairs.clear();
                            drawableAdapter.notifyDataSetChanged();
                        } else {
                            quadros.clear();
                            drawableAdapterLandscape.notifyDataSetChanged();
                        }
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), DownloadService.class);
                        startService(intent);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.show();
                alertDialog.show();


            }
        });

        arrayList = new ArrayList<String>();
        pairs = new ArrayList<Pair>();
        quadros = new ArrayList<Quadro>();
        drawableAdapter = new DrawableAdapter(getApplicationContext(), pairs);
        drawableAdapterLandscape = new DrawableAdapterLandscape(getApplicationContext(), quadros);
        ListView listView = (ListView) findViewById(R.id.listView);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            listView.setAdapter(drawableAdapter);
            fillArray();
            drawableAdapter.notifyDataSetChanged();
        } else {
            listView.setAdapter(drawableAdapterLandscape);
            fillArray();
            drawableAdapterLandscape.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        fillArray();

        registerReceiver(mMessageReceiver, new IntentFilter("ACTION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMessageReceiver);
    }
}
