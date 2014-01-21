package com.example.extratask1;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 19.01.14
 * Time: 5:52
 * To change this template use File | Settings | File Templates.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        CharSequence text = "Loading successful";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        DbPic dbPic = new DbPic(context);
        SQLiteDatabase db = dbPic.getWritableDatabase();
        db.execSQL(dbPic.dropDataBase());
        dbPic.onCreate(db);
        for (int i=0;i<MyActivity.numberPic;i++){
            ContentValues cv = new ContentValues();
            cv.put(DbPic.ID, MyActivity.pictureItems.get(i).getId());
            cv.put(DbPic.NAME,MyActivity.pictureItems.get(i).getName());
            db.insert(DbPic.TABLE_NAME,null,cv);
            FileOutputStream out = null;
            try {
                Bitmap bmp = MyActivity.pictureItems.get(i).getImage();
                File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + MyActivity.dirName + File.separator + MyActivity.pictureItems.get(i).getId() + ".jpeg");
                out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.JPEG, 85, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
