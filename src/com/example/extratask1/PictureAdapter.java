package com.example.extratask1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: PWR
 * Date: 19.01.14
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
public class PictureAdapter extends ArrayAdapter<MyPictures>{

    private final List<MyPictures> items;
    private final Context context;

    public PictureAdapter(Context appContext, int picturesID, List<MyPictures> pictures)
    {
        super(appContext, picturesID, pictures);
        items = pictures;
        context = appContext;
    }

    public View getView(int position, View view, ViewGroup parent)
    {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grItem = layoutInflater.inflate(R.layout.pictures, parent, false);
        ImageView imageView = (ImageView) grItem.findViewById(R.id.picture);

        imageView.setImageBitmap(items.get(position).getBitmap());

        return grItem;

    }
}
