package ru.ifmo.mobdev.extra;

import android.graphics.Bitmap;

public class DownloadData {

    static int screenHeight;
    static int screenWidth;

    public static final int IMAGES_NUMBER = 20;

    public static Bitmap currentImage;

    public static void setMetrics(int height, int width) {
        screenHeight = height;
        screenWidth = width;
    }

}
