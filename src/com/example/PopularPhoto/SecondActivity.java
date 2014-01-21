package com.example.PopularPhoto;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class SecondActivity extends Activity {
    String title;
    String name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);
        TextView textView = (TextView) findViewById(R.id.title);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        title = getIntent().getExtras().getString("title");
        textView.setText(title);
        name = getIntent().getExtras().getString("name");
        imageView.setImageURI(Uri.fromFile(new File(getApplicationContext().getCacheDir(), name)));
    }
}