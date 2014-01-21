package ru.marsermd.flikrVigintus;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import ru.marsermd.flikrVigintus.utility.http.download.SimpleCachingDownloadTask;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: marsermd
 * Date: 03.10.13
 * Time: 5:20
 * To change this template use File | Settings | File Templates.
 */
public class ImageActivity extends Activity {
    private static SimpleCachingDownloadTask downloadTask;
    private ImageView fullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("wow!", "started!!!!!!!!!!!!!!!!!!!1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_actitvity);

        Bundle extra = getIntent().getExtras();

        if (downloadTask != null)
            downloadTask.cancel(true);

        fullImage = (ImageView) findViewById(R.id.fullscreen_image);


        downloadTask = new SimpleCachingDownloadTask(extra.getString("ImageUrl")) {
            @Override
            protected void onPostExecute(File file) {
                fullImage.setImageURI(Uri.fromFile(file));
                Window window = getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            }
        };

        Log.e("wow!", "startedexecuting!!!!!!!!!!!");

        downloadTask.executeOnHttpTaskExecutor();
    }
}
