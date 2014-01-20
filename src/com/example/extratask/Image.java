package com.example.extratask;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class Image {

    private byte[] image;

    private int id;

    private Bitmap bitmap;

    public Image(byte[] i) {
        image = i;
    }

    public int getId() {
        return id;
    }

    public void setId(int i) {
        id = i;
    }


    public Bitmap getBitmap() {
        if (bitmap == null)
        bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);

        return bitmap;
    }
}
