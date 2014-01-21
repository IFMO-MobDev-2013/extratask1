package com.android.example.dronov;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: dronov
 * Date: 19.01.14
 * Time: 7:08
 * To change this template use File | Settings | File Templates.
 */
public class Picture {
    private int width;
    private int height;
    private Bitmap bigImage;
    Picture() {}

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }


    public Bitmap getBigImage() {
        return bigImage;
    }

    public void setBigImage(Bitmap bigImage) {
        this.bigImage = bigImage;
    }

    public Picture(int width, int height, Bitmap bigImage) {

        this.width = width;
        this.height = height;
        this.bigImage = bigImage;
    }
}
