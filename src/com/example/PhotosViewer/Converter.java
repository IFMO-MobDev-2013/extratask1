package com.example.PhotosViewer;

import android.content.ContentValues;
import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Converter {

    public static void bitmapToContentValues(ContentValues contentValues, Bitmap bitmap) {
        int tempArray[] = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(tempArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        contentValues.put(MyDataBaseHelper.IMAGE, intArrayToByteArray(tempArray));
    }

    public static void smallBitmapToContentValues(ContentValues contentValues, Bitmap bitmap) {
        try {
            int tempArray[] = new int[bitmap.getWidth() * bitmap.getHeight()];
            bitmap.getPixels(tempArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
            contentValues.put(MyDataBaseHelper.MINI, intArrayToByteArray(tempArray));
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("ArrayIndexOutOfBoundsException");
            e.printStackTrace();
        }
    }

    public static int[] byteArrayToIntArray(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        byteBuffer.put(bytes);
        int[] array = new int[intBuffer.remaining()];
        intBuffer.get(array);
        return array;
    }

    public static byte[] intArrayToByteArray(int[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(bytes.length * 4);
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(bytes);

        return byteBuffer.array();
    }
}
