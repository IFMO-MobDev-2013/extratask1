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

/**
 * Created by Alexei on 17.01.14.
 */
public class DrawableAdapterLandscape extends ArrayAdapter<Quadro> {
    ArrayList<Quadro> arrayList;
    Context context;

    public DrawableAdapterLandscape(Context context, ArrayList<Quadro> objects) {
        super(context, R.layout.item, objects);
        this.context = context;
        arrayList = objects;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item1, parent, false);
        SuperImageView imageView1 = (SuperImageView) view.findViewById(R.id.view);
        SuperImageView imageView2 = (SuperImageView) view.findViewById(R.id.view2);
        SuperImageView imageView3 = (SuperImageView) view.findViewById(R.id.view3);
        SuperImageView imageView4 = (SuperImageView) view.findViewById(R.id.view4);
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

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ImagesDataBaseHelper.PATH, arrayList.get(position).third);
                context.startActivity(intent);
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(ImagesDataBaseHelper.PATH, arrayList.get(position).forth);
                context.startActivity(intent);
            }
        });

        imageView1.setImageURI(Uri.fromFile(new File(context.getCacheDir(), arrayList.get(position).first)));
        imageView2.setImageURI(Uri.fromFile(new File(context.getCacheDir(), arrayList.get(position).second)));
        imageView3.setImageURI(Uri.fromFile(new File(context.getCacheDir(), arrayList.get(position).third)));
        imageView4.setImageURI(Uri.fromFile(new File(context.getCacheDir(), arrayList.get(position).forth)));
        return view;
    }

}
