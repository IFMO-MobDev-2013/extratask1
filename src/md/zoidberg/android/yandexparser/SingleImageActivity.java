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
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import md.zoidberg.android.yandexparser.Constants.Extra;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import md.zoidberg.android.yandexparser.db.ImagesDbHelper;

public class SingleImageActivity extends Activity {
	private static final String STATE_POSITION = "STATE_POSITION";
    ImageLoader imageLoader = ImageLoader.getInstance();

    ImageView view;
    ProgressBar spinner;
	DisplayImageOptions options;

    ImagesDbHelper helper;
    String imageUrl;
	ViewPager pager;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_single_image);
		Bundle bundle = getIntent().getExtras();
		assert bundle != null;

        helper = new ImagesDbHelper(this);

		int position = bundle.getInt(Extra.IMAGE_POSITION, 0);
        Log.v("SingleImageActivity", "rendering id " + Integer.toString(position));

		if (savedInstanceState != null) {
			position = savedInstanceState.getInt(STATE_POSITION);
		}

		options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.resetViewBeforeLoading(true)
			.cacheOnDisc(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.considerExifParams(true)
			.displayer(new FadeInBitmapDisplayer(300))
			.build();

        view = (ImageView)findViewById(R.id.image);
        spinner = (ProgressBar)findViewById(R.id.loading);

        imageUrl = helper.getLastImages().get(position).getUrl();
        Log.d("SingleImageActivity", "url: " + imageUrl);
    }

    public void onStart() {
        super.onStart();
        renderImage(imageUrl);
    }

    private void renderImage(String url) {
        imageLoader.displayImage(url, view, options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                spinner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                String message = null;
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case DECODING_ERROR:
                        message = "Image can't be decoded";
                        break;
                    case NETWORK_DENIED:
                        message = "Downloads are denied";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                }
                Toast.makeText(SingleImageActivity.this, message, Toast.LENGTH_SHORT).show();

                spinner.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                spinner.setVisibility(View.GONE);
            }
        });
    }


    @Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_POSITION, pager.getCurrentItem());
	}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        renderImage(imageUrl);
    }
}