package com.example.extratask;

import android.content.Context;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;


public class Adapter extends ArrayAdapter<Image> {

    private final List<Image> items;

    private final Context context;

    public Adapter(Context context, int resourseId, List<Image> images) {
        super(context, resourseId, images);
        items = images;
        this.context = context;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View gridItem;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        gridItem = inflater.inflate(R.layout.grid_item, parent, false);

        ImageView imageView = (ImageView) gridItem.findViewById(R.id.picture);

        imageView.setImageBitmap(items.get(position).getBitmap());

        return gridItem;
    }
}
