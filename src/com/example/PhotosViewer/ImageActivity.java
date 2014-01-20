package com.example.PhotosViewer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

public class ImageActivity extends Activity {

    @Override
    public void onCreate(Bundle savedBundleInstance) {
        super.onCreate(savedBundleInstance);
        setContentView(R.layout.imagelayout);
        int width = getIntent().getIntExtra(MyActivity.IMAGE_WIDTH, 0);
        int height = getIntent().getIntExtra(MyActivity.IMAGE_HEIGHT, 0);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        if (width * height == 0)
            System.out.println("Error    -   0  in sizes");
        else {
            Bitmap bitmap = Bitmap.createBitmap(getIntent().getIntArrayExtra(MyDataBaseHelper.IMAGE), width, height, Bitmap.Config.ARGB_8888);
            imageView.setImageBitmap(bitmap);
        }
    }
}
