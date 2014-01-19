/*******************************************************************************
 * Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package md.zoidberg.android.yandexparser;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import md.zoidberg.android.yandexparser.Constants.Extra;
import md.zoidberg.android.yandexparser.db.Image;
import md.zoidberg.android.yandexparser.db.ImagesDbHelper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GridActivity extends Activity {
    ImageAdapter adapter;
    ImagesDbHelper helper;

    DisplayImageOptions options;

    protected AbsListView listView;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        applyScrollListener();
    }

    private void applyScrollListener() {
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader, false, false));
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem refreshItem = menu.findItem(R.id.refresh);
        refreshItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                fetchXml();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("YandexImages", "onCreate");
        setContentView(R.layout.activity_image_grid);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.NONE)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        adapter = new ImageAdapter();
        helper = new ImagesDbHelper(this);
        syncGridAdapter();

        listView = (GridView) findViewById(R.id.gridview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }
        });

        adapter.notifyDataSetChanged();
        fetchXml();
    }

    @Override
    public void onStart() {
        super.onStart();
        setGridColumns(getResources().getConfiguration());
    }

    private void fetchXml() {
        AsyncTask<Void, Void, Boolean> refresh = new AsyncTask<Void, Void, Boolean>() {
            private static final String API_URL = "http://api-fotki.yandex.ru/api/recent/";

            @Override
            protected Boolean doInBackground(Void... params) {
                ArrayList<Image> images = new ArrayList<Image>();
                HttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(API_URL);
                HttpResponse httpResponse = null;
                try {
                    httpResponse = client.execute(httpGet);
                    String entity = EntityUtils.toString(httpResponse.getEntity());
                    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                    SAXParser saxParser = saxParserFactory.newSAXParser();
                    saxParser.parse(new ByteArrayInputStream(entity.getBytes()), new FotkiApiParser(images));


                    helper.flushImages();

                    for (int i = 0; i < images.size(); i++) {
                        helper.addImage(images.get(i));
                    }

                    syncGridAdapter();

                    GridActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
                return true;
            }
        };

        refresh.execute();
    }

    private void syncGridAdapter() {
        List<String> urls = new ArrayList<String>();
        for(Image im: helper.getLastImages()) {
            urls.add(im.getUrl());
        }
        adapter.setUrls(urls);
    }

    private void startImagePagerActivity(int position) {
        Intent intent = new Intent(this, SingleImageActivity.class);
        intent.putExtra(Extra.IMAGE_POSITION, position);
        startActivity(intent);
    }

    public class ImageAdapter extends BaseAdapter {
        public void setUrls(List<String> urls) {
            this.urls = urls;
        }

        private List<String> urls;

        @Override
        public int getCount() {
            return helper.getCount();
        }

        @Override
        public Object getItem(int position) {
            return helper.getById(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_grid_image, parent, false);
                holder = new ViewHolder();
                assert view != null;
                holder.imageView = (ImageView) view.findViewById(R.id.image);
                holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            imageLoader.displayImage(urls.get(position), holder.imageView, options, new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progressBar.setProgress(0);
                            holder.progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String imageUri, View view, int current,
                                                     int total) {
                            holder.progressBar.setProgress(Math.round(100.0f * current / total));
                        }
                    }
            );

            return view;
        }

        class ViewHolder {
            ImageView imageView;
            ProgressBar progressBar;
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setGridColumns(newConfig);
    }

    private void setGridColumns(Configuration config) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            ((GridView)listView).setNumColumns(2);
            ((GridView)listView).setColumnWidth(width * 35 / 100);
            ((GridView)listView).setHorizontalSpacing(width / 10);
            ((GridView)listView).setPadding(width / 10, 0, width / 10, 0);
        } else if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ((GridView)listView).setNumColumns(4);
            ((GridView)listView).setColumnWidth(width * 20 / 100);
            ((GridView)listView).setHorizontalSpacing(width * 4 / 100);
            ((GridView)listView).setPadding(width * 4 / 100, 0, width * 4 / 100, 0);
        }
    }


}