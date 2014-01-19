package com.example.extratask1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 19.01.14
 * Time: 6:20
 * To change this template use File | Settings | File Templates.
 */
public class ItemDisplayer extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.itemdisplayer);
        Intent intent = getIntent();
        int id = intent.getIntExtra("id",0);
        ImageView imageView = (ImageView) findViewById(R.id.imageitem);
        imageView.setImageBitmap(MyActivity.pictureItems.get(id).getImage());
    }
}
