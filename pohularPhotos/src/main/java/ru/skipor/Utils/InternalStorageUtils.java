package ru.skipor.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.apache.http.HttpException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Vladimir Skipor on 1/8/14.
 * Email: vladimirskipor@gmail.com
 */
public class InternalStorageUtils {
    private final static String TAG = "InternalStorageUtils";

    public static synchronized void saveBitmap(Context context, String fileName, Bitmap bitmapImage) {

        FileOutputStream fileOutputStream;

        deleteFileIfExists(context, fileName);
        try {
            fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error", e);
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        }
    }

    public static synchronized void deleteFileIfExists(Context context, String fileName) {
        if (fileExists(context, fileName)) {
            context.deleteFile(fileName);
        }
    }

    public static String downloadAndSaveBitmap(Context context, String URL) throws HttpException {
        String fileName = HTTPUtils.getFileName(URL);
//        deleteFileIfExists(context, fileName);

        if (!fileExists(context, fileName)) {
            saveBitmap(context, fileName, HTTPUtils.getBitmap(URL));
        }
        return fileName;
    }

    public static Bitmap loadOrDownloadAndSaveBitmap(Context context, String URL) throws HttpException {
        Bitmap bitmap = loadBitmap(context, HTTPUtils.getFileName(URL));
        if (bitmap == null) {
            bitmap = HTTPUtils.getBitmap(URL);
            saveBitmap(context, HTTPUtils.getFileName(URL), bitmap);
        }
        return bitmap;
    }
//
//    public static void  deleteAllFiles(Context context) {
//
//        String[] files = context.fileList();
//        for (String fileName : files) {
//            Log.d(TAG, "file to delete: " + fileName);
////            deleteFileIfExists(Context fileName);
//
//        }
//
//
//    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public static Bitmap decodeSampledBitmapFromFile(Context context, String fileName, int reqWidth, int reqHeight) {

        FileInputStream is = null;
        try {
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            is = context.openFileInput(fileName);


            BitmapFactory.decodeStream(is, null, options);

            is.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            is = context.openFileInput(fileName);
            return BitmapFactory.decodeStream(is, null, options);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error", e);
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, "Error", e);
                }
            }
        }
        return null;
    }


    public static synchronized Bitmap loadBitmap(Context context, String fileName) {
        Log.d(TAG, "loading " + fileName);

        try {
            final FileInputStream fileInputStream = context.openFileInput(fileName);
            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
            return bitmap;
        } catch (FileNotFoundException e) {
//            Log.e(TAG, "Error", e);
            return null;
        }

    }

    public static synchronized Bitmap loadBitmap(Context context, String fileName, int reqWidth, int reqHeight) {

        Log.d(TAG, "loading " + fileName);

        return decodeSampledBitmapFromFile(context, fileName, reqWidth, reqHeight);

    }


    public static boolean fileExists(Context context, String fileName) {
        try {
            final FileInputStream fileInputStream = context.openFileInput(fileName);
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Log.e(TAG, "Error", e);
            }
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

}


