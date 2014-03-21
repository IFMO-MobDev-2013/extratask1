package com.example.extratask1;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SecondScreen extends Activity {
    private String link;
    boolean result = true;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        Bitmap bitmap = null;
        int width = 0;
        int height = 0;
        String title = new String();
        ImageDataBase imageDataBase = new ImageDataBase(getApplicationContext());
        SQLiteDatabase liteDatabase = imageDataBase.getReadableDatabase();
        if(liteDatabase == null){
            Toast toast = Toast.makeText(getApplicationContext(), "Sorry, your database is empty and there is no internet connection.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{

            Cursor cursor = liteDatabase.query(imageDataBase.getTableName(), null, null, null, null, null, null);

            if(cursor.moveToPosition(getIntent().getExtras().getInt("index"))){
                bitmap = (Bitmap.createBitmap(Converter.fromByteArrayToIntArray(
                        cursor.getBlob(cursor.getColumnIndex(imageDataBase.getSmallPicRow()))),
                        cursor.getInt(cursor.getColumnIndex(imageDataBase.getSmallWidth())),
                        cursor.getInt(cursor.getColumnIndex(imageDataBase.getSmallHeight())), Bitmap.Config.ARGB_8888
                ));
                width = cursor.getInt(cursor.getColumnIndex(imageDataBase.getSmallWidth()));
                height = cursor.getInt(cursor.getColumnIndex(imageDataBase.getSmallHeight()));
                title = cursor.getString(cursor.getColumnIndex(imageDataBase.getTitleRow()));
            }
            cursor.close();
            liteDatabase.close();
            imageDataBase.close();
        }
        if(result){
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), R.string.loadError, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        }
    }
}
