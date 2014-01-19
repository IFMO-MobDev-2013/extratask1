package com.example.extratask1;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    //ArrayList<ImageView> views = new ArrayList<ImageView>();
    //boolean result = false;
    int SCREEN_W;
    int SCREEN_H;
    ArrayList<Bitmap> smallPictures = new ArrayList<Bitmap>();
    ArrayList<Bitmap> bigPictures = new ArrayList<Bitmap>();
    //ArrayList<String> links = new ArrayList<String>();
    //ArrayList<String> titles = new ArrayList<String>();
    ImageBroadcastReceiver iBR = new ImageBroadcastReceiver();
    IntentFilter filter = new IntentFilter();

    public static final int PIC_COUNT = 20;




    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ScreenSize myScreen = getScreenSize();
        SCREEN_W = myScreen.widht;
        SCREEN_H = myScreen.heigth;
        iBR = new ImageBroadcastReceiver();
        filter = new IntentFilter(ImageLoaderIntentService.key);
        registerReceiver(iBR, filter);
        printImages(false);
    }

    private boolean getScreenOrientation(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            return false;
        else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else
            return false;
    }

    public void updateClick(View currentView){
        Button b = (Button)findViewById(R.id.button);
        b.setEnabled(false);
        if(!checkConnection()){
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.internetConnectionError, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }else{

            ((TextView) findViewById(R.id.textView)).setText(R.string.reload);
            Intent intentService = new Intent(MyActivity.this, ImageLoaderIntentService.class);
            intentService.putExtra("Height", SCREEN_H);
            intentService.putExtra("Width", SCREEN_W);
            startService(intentService);
        }
    }

    private ScreenSize getScreenSize(){
        Point magicPoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(magicPoint);
        return new ScreenSize(magicPoint.x, magicPoint.y);
    }

    public boolean checkConnection(){
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null){
            return false;
        }
        NetworkInfo[] networkInfos = manager.getAllNetworkInfo();
        for(NetworkInfo currentInfo : networkInfos){
            if(currentInfo.getTypeName().equalsIgnoreCase("WIFI") || currentInfo.getTypeName().equalsIgnoreCase("MOBILE"))
                if(currentInfo.isConnected())
                    return true;
        }
        return false;
    }

    public void setGridSettings(GridView gV, boolean land){
        if(land){
            gV.setNumColumns(4);
            gV.setColumnWidth((int) (SCREEN_W * 0.2));
        }else{
            gV.setNumColumns(2);
            gV.setColumnWidth((int) (SCREEN_W * 0.35));
        }
        gV.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        gV.setVerticalSpacing(20);
    }

    public class MyAdapter extends ArrayAdapter<Bitmap>{
        private final Context context;
        private final ArrayList<Bitmap> data;
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<Bitmap> values){
            super(context, R.layout.support, values);
            this.context = context;
            data = values;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent){
            View view = convertView;
            if(view == null){
                view = inflater.inflate(R.layout.support, parent, false);
            }
            ImageView im = (ImageView) view.findViewById(R.id.imageView);
            im.setScaleType(ImageView.ScaleType.CENTER_CROP);
            im.setImageBitmap(data.get(pos));
            if(getScreenOrientation())
                im.getLayoutParams().height = (int) (SCREEN_W * 0.2);
            else
                im.getLayoutParams().height = (int) (SCREEN_W * 0.35);

            return view;
        }
    }

    public class ImageBroadcastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent){
            boolean thisResult = intent.getExtras().getBoolean("result");
            if(thisResult){
                printImages(true);
                ((TextView) findViewById(R.id.textView)).setText(R.string.title);

            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        R.string.fatalError, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    private void printImages(boolean reload){
        ImageDataBase imageDataBase = new ImageDataBase(getApplicationContext());
        SQLiteDatabase liteDatabase = imageDataBase.getReadableDatabase();
        if(liteDatabase == null){
            if(checkConnection()){
                updateClick((View) findViewById(R.id.button));
            } else{
                Toast toast = Toast.makeText(getApplicationContext(), R.string.databaseError, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }else{
            smallPictures.clear();
            bigPictures.clear();
            Cursor cursor = liteDatabase.query(imageDataBase.getTableName(), null, null, null, null, null, null);

            while(cursor.moveToNext()){
                smallPictures.add(Bitmap.createBitmap(Converter.fromByteArrayToIntArray(
                        cursor.getBlob(cursor.getColumnIndex(imageDataBase.getSmallPicRow()))),
                        cursor.getInt(cursor.getColumnIndex(imageDataBase.getSmallWidth())),
                        cursor.getInt(cursor.getColumnIndex(imageDataBase.getSmallHeight())), Bitmap.Config.ARGB_8888
                ));

            }
            cursor.close();
            liteDatabase.close();
            imageDataBase.close();

            MyAdapter adapter = new MyAdapter(getApplicationContext(), smallPictures);
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setAdapter(adapter);
            setGridSettings(gridView, getScreenOrientation());
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(MyActivity.this, SecondScreen.class);

                    intent.putExtra("index", i);
                    startActivity(intent);

                }
            });
            if(reload){
                Toast toast = Toast.makeText(getApplicationContext(), R.string.reloadSuccess, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                Button b = (Button)findViewById(R.id.button);
                b.setEnabled(true);
            }
        }
    }

}
