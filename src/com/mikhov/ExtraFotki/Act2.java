package com.mikhov.ExtraFotki;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Activity;
import android.view.Display;
import android.widget.ImageView;
import com.mikhov.ExtraFotki.dbHelper.DbAdapter;

public class Act2 extends Activity {
    int id;
    Display display;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act2);
        display = this.getWindowManager().getDefaultDisplay();
        Bundle extras = getIntent().getExtras();
        id = Integer.valueOf(extras.getInt("id"));
        DbAdapter dbHelper = new DbAdapter(this);
        dbHelper.open();
        Bitmap bm = dbHelper.getPhoto(id - 1);

        ImageView large = (ImageView) findViewById(R.id.large);
        bm = scaleLarge(bm);
        large.setImageBitmap(bm);
    }

    public Bitmap scaleLarge(Bitmap b) {
        double nw, nh, scale;
        int displayWidth = display.getWidth();
        int bitmapWidth = b.getWidth();
        int bitmapHeight= b.getHeight();
        if (bitmapHeight > bitmapWidth) {
            nw = displayWidth;
            scale = nw / bitmapWidth;
            nh = scale * bitmapHeight;
            b = Bitmap.createScaledBitmap(b, (int) nw, (int) nh, false);
        } else if (bitmapHeight == bitmapWidth) {
            nw = displayWidth;
            scale = nw / bitmapWidth;
            nh = scale * bitmapHeight;
            b = Bitmap.createScaledBitmap(b, (int) nw, (int) nh, false);
        } else {
            nh = displayWidth;
            scale = nh / bitmapHeight;
            nw = scale * bitmapWidth;
            b = Bitmap.createScaledBitmap(b, (int) nw, (int) nh, false);
        }
        return b;
    }

    @Override
    public void onBackPressed() {
        this.finish();

        Intent intent = new Intent(this, Act.class);
        startActivity(intent);

        overridePendingTransition(R.layout.slide_right_in, R.layout.slide_right_out);
    }
}
