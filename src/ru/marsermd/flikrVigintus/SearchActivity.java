package ru.marsermd.flikrVigintus;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import ru.marsermd.flikrVigintus.utility.GUI.PicturesAdapter;
import ru.marsermd.flikrVigintus.utility.GUI.PicturesAdapterLandscape;
import ru.marsermd.flikrVigintus.utility.cacher.FileCacher;
import ru.marsermd.flikrVigintus.utility.http.translate.TranslateResult;
import ru.marsermd.flikrVigintus.utility.http.translate.yandex.YandexTranslateTask;

public class SearchActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    private ListView searchedImages;
    private InputMethodManager imm;
    private PicturesAdapter adapter;
    private String YANDEX_API_KEY, FLICKR_API_KEY;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        YANDEX_API_KEY = getString(R.string.yandex_translate_api_key);
        FLICKR_API_KEY = getString(R.string.flickr_api_key);

        init();

    }

    private final static int CACHE_BYTE_LIMIT = 50 * 1024 * 1024;

    private void init() {
        FileCacher.getInstance().init(this, CACHE_BYTE_LIMIT);

        searchedImages = (ListView) findViewById(R.id.images_result);

        imm = (InputMethodManager) getSystemService(getBaseContext().INPUT_METHOD_SERVICE);

        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int orientation = display.getRotation();
        if (orientation == Surface.ROTATION_90 || orientation == Surface.ROTATION_270) {    // landscape
            adapter = new PicturesAdapterLandscape(this);
        } else {  //portrait
            adapter = new PicturesAdapter(this);
        }

        searchedImages.setAdapter(adapter);
        adapter.init();
    }
}
