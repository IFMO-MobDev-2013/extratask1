package com.example.extratask;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: mv
 * Date: 19.01.14
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class FullSizeImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullsize);

        int pos = getIntent().getIntExtra(MainActivity.KEY_POSITION, 0);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageBitmap(MainActivity.full_size_bitmaps[pos]);
    }
}
