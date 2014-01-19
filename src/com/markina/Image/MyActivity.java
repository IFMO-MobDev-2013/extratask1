package com.markina.Image;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyActivity extends Activity implements View.OnClickListener {

    Button button;
    LinearLayout images;
    TextView tv;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        setContentView(R.layout.main);
        button = (Button) findViewById(R.id.UpBtn);
        button.setOnClickListener(this);
        images = (LinearLayout) findViewById(R.id.ImgList);
    }


    String urlText;

    class Download extends AsyncTask<String, Void, List<Bitmap>> {

        @Override
        protected List<Bitmap> doInBackground(String... params) {
            try {
                String url = "http://api-fotki.yandex.ru/api/recent/";
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response = client.execute(get);
                String html = readStream(response.getEntity().getContent());

                Pattern pattern = Pattern.compile("href=\"([^\"]*?_L)\" size=\"L\"");
                Matcher matcher = pattern.matcher(html);
                List<String> res = new ArrayList<String>();
                while(matcher.find() && res.size() < 20) {
                    res.add(matcher.group(1));
                }

                final List<Bitmap> b = new ArrayList<Bitmap>();
                for(String string : res) {
                    b.add(getBitmapFromURL(string));
                }
                return b;
            } catch (Exception e) {
                e.printStackTrace();
                return new ArrayList<Bitmap>();
            }
        }

        @Override
        protected void onPostExecute(final List<Bitmap> strings) {
            if (strings.size() == 0) {
                MyActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        dialog.dismiss();
                        Toast.makeText(MyActivity.this, "No internet! :)", Toast.LENGTH_SHORT);
                    }
                });
            } else

            MyActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    images.removeAllViews();
                    for(Bitmap bitmap : strings) {
                        ImageView image = new ImageView(MyActivity.this);
                        image.setImageBitmap(bitmap);
                        images.addView(image);
                    }
                    button.setEnabled(true);
                }
            });
        }

        public String readStream(InputStream in) {
            StringBuilder builder = new StringBuilder();
            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(in);
                char[] buffer = new char[10240];
                int read;
                while ((read = reader.read(buffer)) > 0) {
                    builder.append(buffer, 0, read);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                Log.e("src", src);
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                Log.e("Bitmap","returned");
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception",e.getMessage());
                return null;
            }
        }
    }

    Intent intent = new Intent();
    String enans;

    ProgressDialog dialog;

    @Override
    public void onClick(View v) {
        dialog.setCancelable(false);
        dialog.show();
        new Download().execute();
        button.setEnabled(false);
    }
}