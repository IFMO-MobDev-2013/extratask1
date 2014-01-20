package com.example.PhotosViewer;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.net.URL;

public class UpdatingService extends IntentService {

    public static final String RESULT = "result";

    public UpdatingService(String name) {
        super(name);
    }

    public UpdatingService() {
        super("default_name_");
    }

    Bitmap bitmap[] = new Bitmap[MyActivity.IMAGE_NUMBER];

    public static final String UPDATING_ACTION = "updating";
    public static final String START_UPDATING_ACTION = "start_updating";

    public boolean downloadPhotos() {

        boolean result = false;
        try {
            MySAXParser mySAXParser = new MySAXParser();
            Downloader downloader = new Downloader(Downloader.APIADDRESS, mySAXParser);

            if (downloader.successfulDownload) {
                for (int i = 0; i < MyActivity.IMAGE_NUMBER; i++) {
                    System.out.println("request   to   api         =" + mySAXParser.array.get(i));
                    bitmap[i] = BitmapFactory.decodeStream(new URL(mySAXParser.array.get(i)).openStream());
                }
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        boolean result = false;
        if (downloadPhotos()) {
            MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(getApplicationContext());
            SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getWritableDatabase();
            sqLiteDatabase.execSQL(MyDataBaseHelper.DROP_DATABASE);
            myDataBaseHelper.onCreate(sqLiteDatabase);

            for (int i = 0; i < MyActivity.IMAGE_NUMBER; i++) {
                System.out.println("Try to put imagelayout    i = " + i);
                ContentValues contentValues = new ContentValues();
                Converter.bitmapToContentValues(contentValues, bitmap[i]);
                Converter.smallBitmapToContentValues(contentValues, bitmap[i]);
                contentValues.put(MyDataBaseHelper.WIDTH, bitmap[i].getWidth());
                contentValues.put(MyDataBaseHelper.HEIGHT, bitmap[i].getHeight());
                sqLiteDatabase.insert(MyDataBaseHelper.DATABASE_NAME, null, contentValues);
            }
            sqLiteDatabase.close();
            myDataBaseHelper.close();
            result = true;
        }
        Intent intentResponse = new Intent();
        intentResponse.putExtra(RESULT, result);
        intentResponse.setAction(UPDATING_ACTION);
        sendBroadcast(intentResponse);
    }

}
