package com.example.extratask1;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MyActivity extends Activity implements Reciever.inReciever {

    private Reciever receiver;
    private DataBases database;
    private List<MyPictures> pictures;
    private PictureAdapter pictureAdapter;
    private GridView gridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        receiver = new Reciever(new Handler());
        receiver.setReciever(this);
        database = new DataBases(getApplicationContext());

        viewConfig((GridView) findViewById(R.id.grid_view));

        pictures = new ArrayList<>();
        database.newPictures(pictures);

        if(pictures.size() == 0)
            downloadPictures();

        pictureAdapter = new PictureAdapter(getApplicationContext(), R.layout.pictures, pictures);
        gridView.setAdapter(pictureAdapter);

        GridView.OnItemClickListener grid_layoutOnItemClickListener = new GridView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), FullscreenPictureActivity.class);
                intent.putExtra("id", pictures.get(position).getID());
                startActivity(intent);

            }
        };

        gridView.setOnItemClickListener(grid_layoutOnItemClickListener);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                item.setEnabled(false);
                downloadPictures();
                return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    private void downloadPictures() {
        Toast toast = Toast.makeText(getApplicationContext(),"Downloading...", Toast.LENGTH_SHORT);
        toast.show();
        Intent intent = new Intent(this, DownloadPictures.class);
        intent.putExtra("receiver", receiver);
        startService(intent);
    }

    private void viewConfig(GridView tmpGridView) {

        gridView = tmpGridView;
        Point display_size = new Point();
        getWindowManager().getDefaultDisplay().getSize(display_size);
        int width = display_size.x;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            tmpGridView.setVerticalSpacing((int) (width * 0.1));
            tmpGridView.setColumnWidth((int) (width * 0.35));
            tmpGridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        }
        else
        {
            tmpGridView.setVerticalSpacing((int) (width * 0.04));
            tmpGridView.setColumnWidth((int) (width * 0.2));
            tmpGridView.setNumColumns(4);
            tmpGridView.setStretchMode(GridView.STRETCH_SPACING_UNIFORM);
        }

    }

    @Override
    public void onReceiveResult(int result, Bundle data) {
        database.newPictures(pictures);
        pictureAdapter.notifyDataSetChanged();
        findViewById(R.id.action_refresh).setEnabled(true);
        Toast toast = Toast.makeText(getApplicationContext(), "Ready!", Toast.LENGTH_SHORT);
        toast.show();
    }
}
