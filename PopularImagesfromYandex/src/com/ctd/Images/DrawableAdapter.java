package com.ctd.Images;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.io.File;
import java.util.ArrayList;

public class DrawableAdapter extends ArrayAdapter<Pair> {
    ArrayList<Pair> arrayList;
    Context context;

    public DrawableAdapter(Context context, ArrayList<Pair> objects) {
        super(context, R.layout.item, objects);
        this.context = context;
        arrayList = objects;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item, parent, false);
        SuperImageView imageView1 = (SuperImageView) view.findViewById(R.id.view);
        SuperImageView imageView2 = (SuperImageView) view.findViewById(R.id.view2);
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ImagesDataBaseHelper.PATH, arrayList.get(position).first);
                context.startActivity(intent);
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ImagesDataBaseHelper.PATH, arrayList.get(position).second);
                context.startActivity(intent);
            }
        });

        imageView1.setImageURI(Uri.fromFile(new File(context.getCacheDir(), arrayList.get(position).first)));
        imageView2.setImageURI(Uri.fromFile(new File(context.getCacheDir(), arrayList.get(position).second)));
        return view;
    }


}
