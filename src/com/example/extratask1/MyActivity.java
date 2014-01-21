package com.example.extratask1;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

public class MyActivity extends Activity {
    public static boolean orientation;
    public static ArrayList<PictureItem> pictureItems = new ArrayList<PictureItem>();
    public static MyArrayAdapter myArrayAdapter;
    public static int numberPic = 20;
    public static String dirName = "recentImage";
    private String yandexApiRecent = "http://api-fotki.yandex.ru/api/recent/";
    public static boolean startRefresh = false;
    public static Context context ;
    public static ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            orientation = false;
        else
            orientation = true;
        setContentView(R.layout.main);

        context = getApplicationContext();

        File dir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), dirName);
        if (!dir.exists())
            dir.mkdirs();

        listView = (ListView) findViewById(R.id.listView);
        Button refresh = (Button) findViewById(R.id.button);

        DbPic dbPic = new DbPic(MyActivity.this);
        SQLiteDatabase db = dbPic.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DbPic.TABLE_NAME,null);
        cursor.moveToFirst();
        pictureItems.clear();
        for (int i=0;i<cursor.getCount();i++){
            String filename = cursor.getString(cursor.getColumnIndex(DbPic.ID));
            File pic = new File(dir + File.separator + filename + ".jpeg");
            if (pic.exists())
                pictureItems.add(new PictureItem(cursor.getString(cursor.getColumnIndex(DbPic.NAME)),filename, BitmapFactory.decodeFile(pic.getAbsolutePath())));
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        myArrayAdapter = new MyArrayAdapter(this, translateOrientation(pictureItems));
        listView.setAdapter(myArrayAdapter);
        myArrayAdapter.notifyDataSetChanged();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startRefresh){
                    startRefresh = true;
                    new MyAsyncTask().execute(yandexApiRecent);
                }
            }
        });
    }

    public static ArrayList<ArrayList<PictureItem>> translateOrientation(ArrayList<PictureItem> pictureItems) {
        ArrayList<ArrayList<PictureItem>> ans = new ArrayList<ArrayList<PictureItem>>();
        for (int i = 0; i < pictureItems.size() / (orientation?4:2); i++){
            ArrayList<PictureItem> temp = new ArrayList<PictureItem>();
            for (int j = 0; j < (orientation?4:2); j++){
                temp.add(pictureItems.get(i * (orientation ? 4 : 2) + j));
            }
            ans.add(temp);
        }
        return ans;
    }
}
