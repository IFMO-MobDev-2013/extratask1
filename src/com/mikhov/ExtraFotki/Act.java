package com.mikhov.ExtraFotki;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.Toast;
import com.mikhov.ExtraFotki.dbHelper.DbAdapter;

public class Act extends Activity implements View.OnClickListener {
    DbAdapter dbHelper;
    String[] miniUrls;
    Display display;
    Bitmap[] d, bm;
    ImageView[] iv;
    boolean allowed, landscape;
    Toast toastBase, toastInternet;

    public static enum TransitionType {
        SlideLeft
    }
    public static TransitionType transitionType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setContentView(R.layout.main);
            landscape = false;
        } else {
            setContentView(R.layout.main_landscape);
            landscape = true;
        }
        display = this.getWindowManager().getDefaultDisplay();
        dbHelper = new DbAdapter(this);
        dbHelper.open();
        d = new Bitmap[20];
        iv = new ImageView[20];
        initImages();
        initSpacers();
        toastBase = Toast.makeText(this, "Base is empty now. You may update it...", Toast.LENGTH_SHORT);
        toastInternet = Toast.makeText(this, "No Internet Connection...", Toast.LENGTH_SHORT);

        if (dbHelper.isNotEmpty()) {
            allowed = true;
            bm = dbHelper.getPhotos();

            for (int i = 0; i < 20; i++) {
                iv[i].setImageBitmap(scaleMini(bm[i]));
            }

            for (int i = 0; i < 20; i++) {
                iv[i].setOnClickListener(this);
            }
        } else {
            initImages();
            initSpacers();
            allowed = false;
            toastBase.show();
        }
    }

    public void update() {
        if (internetAccess()) {
            updateMyBaseWithInternet();
            dbHelper.drop();
            dbHelper.addPhotos(d);
            bm = dbHelper.getPhotos();
            allowed = true;

            for (int i = 0; i < 20; i++) {
                iv[i].setImageBitmap(scaleMini(bm[i]));
            }

            for (int i = 0; i < 20; i++) {
                iv[i].setOnClickListener(this);
            }
        } else {
            toastInternet.show();
            if (dbHelper.isNotEmpty()) {
                allowed = true;
                bm = dbHelper.getPhotos();

                for (int i = 0; i < 20; i++) {
                    iv[i].setImageBitmap(scaleMini(bm[i]));
                }

                for (int i = 0; i < 20; i++) {
                    iv[i].setOnClickListener(this);
                }
            } else {
                initImages();
                initSpacers();
                allowed = false;
                toastBase.show();
            }
        }
    }

    public boolean internetAccess() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
        for (NetworkInfo networkInfo1 : networkInfo) {
            if (networkInfo1.getTypeName().equalsIgnoreCase("mobile")) {
                if (networkInfo1.isConnected()) {
                    return true;
                }
            } else if (networkInfo1.getTypeName().equalsIgnoreCase("wifi")) {
                if (networkInfo1.isConnected())  {
                    return true;
                }
            }
        }
        return false;
    }

    public void updateMyBaseWithInternet() {
        try {
            miniUrls = new GetMiniUrls("http://api-fotki.yandex.ru/api/recent/", this).execute().get();
        } catch (Exception e) {

        }

        try {
            for (int i = 0; i < 20; i++) {
                d[i] = new GetPhoto(miniUrls[i]).execute().get();
            }
        } catch (Exception e) {

        }
    }

    public Bitmap scaleMini(Bitmap b) {
        double nw, nh, scale, sc;
        int displayWidth = display.getWidth();
        int bitmapWidth = b.getWidth();
        int bitmapHeight= b.getHeight();
        sc = (landscape) ? 0.2 : 0.35;
        if (bitmapHeight > bitmapWidth) {
            nw = sc * displayWidth;
            scale = nw / bitmapWidth;
            nh = scale * bitmapHeight;
            b = Bitmap.createScaledBitmap(b, (int) nw, (int) nh, false);
            b = heightCrop(b);
        } else if (bitmapHeight == bitmapWidth) {
            nw = sc * displayWidth;
            scale = nw / bitmapWidth;
            nh = scale * bitmapHeight;
            b = Bitmap.createScaledBitmap(b, (int) nw, (int) nh, false);
        } else {
            nh = sc * displayWidth;
            scale = nh / bitmapHeight;
            nw = scale * bitmapWidth;
            b = Bitmap.createScaledBitmap(b, (int) nw, (int) nh, false);
            b = widthCrop(b);
        }
        return b;
    }

    public Bitmap heightCrop(Bitmap b) {
        int bitmapWidth = b.getWidth();
        int bitmapHeight = b.getHeight();
        int[] pixels = new int[bitmapWidth * bitmapWidth];
        b.getPixels(pixels, 0, bitmapWidth, 0, (int) ((bitmapHeight - bitmapWidth) / 2), bitmapWidth, bitmapWidth);
        b = Bitmap.createBitmap(bitmapWidth, bitmapWidth, Bitmap.Config.ARGB_8888);
        b.setPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, bitmapWidth);
        return b;
    }

    public Bitmap widthCrop(Bitmap b) {
        int bitmapWidth = b.getWidth();
        int bitmapHeight = b.getHeight();
        int[] pixels = new int[bitmapHeight * bitmapHeight];
        b.getPixels(pixels, 0, bitmapHeight, (int) ((bitmapWidth - bitmapHeight) / 2), 0, bitmapHeight, bitmapHeight);
        b = Bitmap.createBitmap(bitmapHeight, bitmapHeight, Bitmap.Config.ARGB_8888);
        b.setPixels(pixels, 0, bitmapHeight, 0, 0, bitmapHeight, bitmapHeight);
        return b;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fotka1:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 1);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka2:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 2);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka3:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 3);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka4:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 4);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka5:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 5);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka6:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 6);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka7:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 7);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka8:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 8);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka9:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 9);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka10:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 10);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka11:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 11);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka12:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 12);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka13:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 13);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka14:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 14);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka15:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 15);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka16:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 16);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka17:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 17);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka18:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 18);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka19:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 19);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
            case R.id.fotka20:
                if (allowed) {
                    this.finish();
                    Intent intent = new Intent(this, Act2.class);
                    intent.putExtra("id", 20);
                    startActivity(intent);
                    transitionType = TransitionType.SlideLeft;
                    overridePendingTransition(R.layout.slide_left_in, R.layout.slide_left_out);
                } else {
                    toastBase.show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.update:
                update();
                return true;
        }
        return super.onMenuItemSelected(featureId, menuItem);
    }


    public void initImages() {
        iv[0] = (ImageView) findViewById(R.id.fotka1);
        iv[1] = (ImageView) findViewById(R.id.fotka2);
        iv[2] = (ImageView) findViewById(R.id.fotka3);
        iv[3] = (ImageView) findViewById(R.id.fotka4);
        iv[4] = (ImageView) findViewById(R.id.fotka5);
        iv[5] = (ImageView) findViewById(R.id.fotka6);
        iv[6] = (ImageView) findViewById(R.id.fotka7);
        iv[7] = (ImageView) findViewById(R.id.fotka8);
        iv[8] = (ImageView) findViewById(R.id.fotka9);
        iv[9] = (ImageView) findViewById(R.id.fotka10);
        iv[10] = (ImageView) findViewById(R.id.fotka11);
        iv[11] = (ImageView) findViewById(R.id.fotka12);
        iv[12] = (ImageView) findViewById(R.id.fotka13);
        iv[13] = (ImageView) findViewById(R.id.fotka14);
        iv[14] = (ImageView) findViewById(R.id.fotka15);
        iv[15] = (ImageView) findViewById(R.id.fotka16);
        iv[16] = (ImageView) findViewById(R.id.fotka17);
        iv[17] = (ImageView) findViewById(R.id.fotka18);
        iv[18] = (ImageView) findViewById(R.id.fotka19);
        iv[19] = (ImageView) findViewById(R.id.fotka20);
        for (int i = 0; i < 20; i++) {
            iv[i].setImageBitmap(scaleMini(BitmapFactory.decodeResource(this.getResources(), R.drawable.no_image)));
            iv[i].setOnClickListener(this);
        }
    }

    public void initSpacers() {
        ImageView[] spacers = new ImageView[15];
        Bitmap spacerBitmap = scaleSpacer(BitmapFactory.decodeResource(this.getResources(), R.drawable.spacer));
        spacers[0] = (ImageView) findViewById(R.id.spacer1);
        spacers[1] = (ImageView) findViewById(R.id.spacer2);
        spacers[2] = (ImageView) findViewById(R.id.spacer3);
        spacers[3] = (ImageView) findViewById(R.id.spacer4);
        spacers[4] = (ImageView) findViewById(R.id.spacer5);
        spacers[5] = (ImageView) findViewById(R.id.spacer6);
        spacers[6] = (ImageView) findViewById(R.id.spacer7);
        spacers[7] = (ImageView) findViewById(R.id.spacer8);
        spacers[8] = (ImageView) findViewById(R.id.spacer9);
        spacers[9] = (ImageView) findViewById(R.id.spacer10);
        for (int i = 0; i < 10; i++) {
            spacers[i].setImageBitmap(spacerBitmap);
        }
        if (landscape) {
            spacers[10] = (ImageView) findViewById(R.id.spacer1e);
            spacers[11] = (ImageView) findViewById(R.id.spacer2e);
            spacers[12] = (ImageView) findViewById(R.id.spacer3e);
            spacers[13] = (ImageView) findViewById(R.id.spacer4e);
            spacers[14] = (ImageView) findViewById(R.id.spacer5e);
            for (int i = 10; i < 15; i++) {
                spacers[i].setImageBitmap(spacerBitmap);
            }
        }
    }

    public Bitmap scaleSpacer(Bitmap b) {
        double nw, nh, scale, sc;
        sc = (landscape) ? 0.04 : 0.1;
        int displayWidth = display.getWidth();
        int bitmapWidth = b.getWidth();
        int bitmapHeight= b.getHeight();
        nw = sc * displayWidth;
        scale = nw / bitmapWidth;
        nh = scale * bitmapHeight;
        b = Bitmap.createScaledBitmap(b, (int) nw, (int) nh, false);
        return b;
    }
}
