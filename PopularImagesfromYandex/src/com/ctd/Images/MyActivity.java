package com.ctd.Images;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    void fillArray() {
        ImagesDataBaseHelper imagesDataBaseHelper = new ImagesDataBaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = imagesDataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(ImagesDataBaseHelper.DATABASE_NAME, null, null, null, null, null, null);
        int pathColumn = cursor.getColumnIndex(ImagesDataBaseHelper.PATH);
        arrayList.clear();
        while (cursor.moveToNext()) {
            arrayList.add(cursor.getString(pathColumn));
        }
        cursor.close();
        sqLiteDatabase.close();
        imagesDataBaseHelper.close();
        for (int i = 0; i < arrayList.size(); i += 2) {
            pairs.add(new Pair(arrayList.get(i), arrayList.get(i + 1)));
        }
        cursor.close();
        sqLiteDatabase.close();
        imagesDataBaseHelper.close();
        drawableAdapter.notifyDataSetChanged();
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
                        pairs.clear();
                        drawableAdapter.notifyDataSetChanged();
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
        drawableAdapter = new DrawableAdapter(getApplicationContext(), pairs);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(drawableAdapter);
        fillArray();
        drawableAdapter.notifyDataSetChanged();
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
