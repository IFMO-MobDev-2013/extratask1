package com.gmail.mazinva.PopularPhotos;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class SingleImageActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int id = bundle.getInt(MainActivity.IMAGE_ID);
        SQLiteAccessor accessor = new SQLiteAccessor(this);
        accessor.open();
        try {
            id++;
            Cursor cursor = accessor.fetchImageById(id);
            cursor.moveToFirst();

            byte[] data = cursor.getBlob(cursor.getColumnIndexOrThrow(SQLiteAccessor.KEY_BITMAP_IMAGES));
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            ImageView imageView = (ImageView) findViewById(R.id.image);
            imageView.setImageBitmap(bitmap);
            cursor.close();
        } catch (MySQLiteException e) {
            e.printStackTrace();
        }
        accessor.close();
    }

}
