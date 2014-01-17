package com.example.PhotosViewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.*;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

public class MyActivity extends Activity {

    public static int IMAGE_NUMBER = 20;
    public static int DEFAULT_IMAGE_HEIGHT = 250;
    public static int DEFAULT_IMAGE_WIDTH = 500;
    public static String IMAGE_HEIGHT = "height";
    public static String IMAGE_WIDTH = "width";
    public static String IMAGE_SIZE = "M";

    Button button;

    class MyAdapter extends ArrayAdapter<Bitmap> {
        private Context context;

        public MyAdapter(Context context, int textViewResourceId, ArrayList<Bitmap> items) {
            super(context, textViewResourceId, items);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageBitmap(getItem(position));
            return imageView;
        }
    }

    public AdapterView.OnItemClickListener goToImage = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView parent, View v, int position, long id) {
            Intent intent = new Intent();
            int pixels[] = new int[bitmapsBig.get(position).getHeight() * bitmapsBig.get(position).getWidth()];
            bitmapsBig.get(position).getPixels(pixels, 0, bitmapsBig.get(position).getWidth(), 0, 0, bitmapsBig.get(position).getWidth(), bitmapsBig.get(position).getHeight());
            intent.putExtra(MyDataBaseHelper.IMAGE, pixels);
            intent.putExtra(IMAGE_WIDTH, bitmapsBig.get(position).getWidth());
            intent.putExtra(IMAGE_HEIGHT, bitmapsBig.get(position).getHeight());

            intent.setClass(getApplicationContext(), ImageActivity.class);

            startActivity(intent);
        }
    };


    public void updatePhotos(View view) {
        button.setEnabled(false);
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), UpdatingService.class);
        startService(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mMessageReceiver, new IntentFilter(UpdatingService.UPDATING_ACTION));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(UpdatingService.RESULT, false)) {
                Toast.makeText(context, R.string.SuccessfulUpdate, Toast.LENGTH_SHORT).show();
                DialogFragment newFragment = new MyAlertDialogFragment();
                newFragment.show(getFragmentManager(), "dialog");
            } else
                Toast.makeText(context, R.string.BadInternetAndMemory, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onPause() {
        unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    boolean havePictures;
    ArrayList<Bitmap> bitmaps;
    ArrayList<Bitmap> bitmapsBig;
    MyAdapter adapter;
    int displayWidth;
    int displayHeight;
    boolean portraitOrientation;

    void getSizes() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        displayWidth = point.x;
        displayHeight = point.y;
        portraitOrientation = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }


    public class MyAlertDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Dialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.NewImagesCome)
                    .setPositiveButton(R.string.YesChange,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    MyAdapter myAdapter = new MyAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, new ArrayList<Bitmap>());
                                    GridView gridView = (GridView) findViewById(R.id.gridview);
                                    gridView.setAdapter(myAdapter);
                                    myAdapter.notifyDataSetChanged();
                                    showPhotos();
                                    button.setEnabled(true);
                                }
                            }
                    )
                    .setNegativeButton(R.string.NoChange,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    button.setEnabled(true);
                                }
                            }
                    )
                    .setCancelable(true)
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }

    void showPhotos() {

        getSizes();
        havePictures = false;
        MyDataBaseHelper myDataBaseHelper = new MyDataBaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = myDataBaseHelper.getReadableDatabase();
        if (sqLiteDatabase == null) {
            System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAA    -  null database");
            return;
        }
        System.out.println("begin to showPhotos");
        Cursor cursor = sqLiteDatabase.query(MyDataBaseHelper.DATABASE_NAME, null, null, null, null, null, null);
        bitmaps = new ArrayList<Bitmap>();
        bitmapsBig = new ArrayList<Bitmap>();
        while (cursor.moveToNext()) {
            havePictures = true;
            Bitmap bitmap = Bitmap.createBitmap(Converter.byteArrayToIntArray(cursor.getBlob(cursor.getColumnIndex(MyDataBaseHelper.IMAGE))),
                    cursor.getInt(cursor.getColumnIndex(MyDataBaseHelper.WIDTH)),
                    cursor.getInt(cursor.getColumnIndex(MyDataBaseHelper.HEIGHT)), Bitmap.Config.ARGB_8888);
            bitmapsBig.add(bitmap);
            bitmaps.add(Bitmap.createScaledBitmap(bitmap, DEFAULT_IMAGE_HEIGHT, DEFAULT_IMAGE_HEIGHT, false));
        }
        cursor.close();
        sqLiteDatabase.close();
        myDataBaseHelper.close();
        adapter = new MyAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, bitmaps);
        GridView gridView = (GridView) findViewById(R.id.gridview);
        gridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        if (portraitOrientation) {
            gridView.setNumColumns(2);
            gridView.setColumnWidth((int) (displayWidth * 0.35));
        } else {
            gridView.setNumColumns(4);
            gridView.setColumnWidth((int) (displayWidth * 0.2));
        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(goToImage);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        showPhotos();
        button = (Button) findViewById(R.id.buttonUpdate);
    }
}
