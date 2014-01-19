package com.example.extratask1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created with IntelliJ IDEA.
 * User: PWR
 * Date: 19.01.14
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class MyPictures {

    private byte[] picture;
    private Bitmap bitmap;
    private int id;

    public MyPictures(byte[] tmp_picture)
    {
        picture = tmp_picture;
    }

    public Bitmap getBitmap()
    {
        if(bitmap == null)
            bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.length);
        return bitmap;
    }

    public void newID(int tmp_id)
    {
        id = tmp_id;
    }

    public int getID()
    {
        return id;
    }
}
