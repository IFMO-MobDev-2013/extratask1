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

/**
 * Created with IntelliJ IDEA.
 * User: Дмитрий
 * Date: 18.01.14
 * Time: 3:33
 * To change this template use File | Settings | File Templates.
 */
public class SecondScreen extends Activity {
    private String link;
    boolean result = true;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        /*if(getIntent().getExtras().getInt("width") == 0 || getIntent().getExtras().getInt("height") == 0 || getIntent().getExtras().getByteArray("bitmap") == null)
            result = false;
        ((TextView) findViewById(R.id.textView)).setText(getIntent().getExtras().getString("title"));
        Bitmap bitmap = Bitmap.createBitmap(Converter.fromByteArrayToIntArray(getIntent().getExtras().getByteArray("bitmap")),
                getIntent().getExtras().getInt("width"),
                getIntent().getExtras().getInt("height"),
                Bitmap.Config.ARGB_8888);
        /*GetPicture picture = new GetPicture();
        picture.execute();*/
        Bitmap bitmap = null;
        int width = 0;
        int height = 0;
        String title = new String();
        ImageDataBase imageDataBase = new ImageDataBase(getApplicationContext());
        SQLiteDatabase liteDatabase = imageDataBase.getReadableDatabase();
        if(liteDatabase == null){
            Toast toast = Toast.makeText(getApplicationContext(), "Мы не смогли найти вашу базу;(", Toast.LENGTH_LONG);
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
            ((TextView) findViewById(R.id.textView)).setText(title);
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), R.string.loadError, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            finish();
        }
    }
}
