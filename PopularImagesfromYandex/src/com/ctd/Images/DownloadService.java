package com.ctd.Images;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.format.Time;
import android.util.Log;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class DownloadService extends IntentService {
    public static final String URL = "http://api-fotki.yandex.ru/api/recent/";

    public DownloadService(String name) {
        super(name);
    }

    public DownloadService() {
        super("");
    }

    ArrayList<String> arrayList;

    @Override
    protected void onHandleIntent(Intent intent) {
        arrayList = new ArrayList<String>();
        boolean done = true;
        try {
            DownloadImages downloadImages = new DownloadImages(getApplicationContext(), URL);
            arrayList = downloadImages.execute();
        } catch (Exception e) {
            done = false;
        }
        if (arrayList.size() != 0) {
            try {
                uploadPictures();
            } catch (IOException e) {
                done = false;
            }
        }
        Intent intentResp = new Intent();
        intentResp.setAction("ACTION");
        intentResp.putExtra("RESULT", done);
        sendBroadcast(intentResp);
    }

    ImagesDataBaseHelper imagesDataBaseHelper;
    SQLiteDatabase sqLiteDatabase;

    String downloadDrawable(String URL) throws IOException {
        Time time = new Time();
        time.setToNow();
        Drawable drawable = Drawable.createFromStream((InputStream) new URL(URL).getContent(), "src");
        Random random = new Random();
        String fileName = Integer.toString(time.year) + Integer.toString(time.month) + Integer.toString(time.monthDay) + Integer.toString(time.hour) + Integer.toString(time.minute) + Integer.toString(time.second) + Math.abs(random.nextInt()) + ".jpg";
        File file = new File(getApplicationContext().getCacheDir(), fileName);
        OutputStream fOut = new FileOutputStream(file);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
        fOut.flush();
        fOut.close();
        return fileName;
    }

    void uploadPictures() throws IOException {

        imagesDataBaseHelper = new ImagesDataBaseHelper(getApplicationContext());
        sqLiteDatabase = imagesDataBaseHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query(ImagesDataBaseHelper.DATABASE_NAME, null, null, null, null, null, null);
        ArrayList<String> delete = new ArrayList<String>();
        int pathColumn = cursor.getColumnIndex(ImagesDataBaseHelper.PATH);
        while (cursor.moveToNext()) {
            delete.add(cursor.getString(pathColumn));
        }
        cursor.close();
        for (int i = 0; i < delete.size(); i++) {
            File deleteFile = new File(getCacheDir(), delete.get(i));
            if (deleteFile.exists()) {
                boolean t = deleteFile.delete();
                if (t) {
                    Log.d("DELETE", String.valueOf(true));
                } else {
                    Log.d("DELETE", String.valueOf(false));
                }
            }
        }
        sqLiteDatabase.execSQL(imagesDataBaseHelper.dropDataBase());
        sqLiteDatabase.execSQL(imagesDataBaseHelper.createDatabase());
        ContentValues contentValues;
        for (int i = 0; i < 20; i++) {
            contentValues = new ContentValues();
            contentValues.put(ImagesDataBaseHelper.URL, arrayList.get(i));
            contentValues.put(ImagesDataBaseHelper.PATH, downloadDrawable(arrayList.get(i)));
            sqLiteDatabase.insert(ImagesDataBaseHelper.DATABASE_NAME, null, contentValues);
        }
        sqLiteDatabase.close();
        imagesDataBaseHelper.close();


    }
}
