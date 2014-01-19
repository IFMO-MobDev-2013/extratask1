package com.example.extratask1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 17.01.14
 * Time: 2:51
 * To change this template use File | Settings | File Templates.
 */
public class MyArrayAdapter extends ArrayAdapter<ArrayList<PictureItem>> {
    protected final Context context;
    private final ArrayList<ArrayList<PictureItem>> values;

    public MyArrayAdapter(Context context, ArrayList<ArrayList<PictureItem>> values) {
        super(context, MyActivity.orientation ? R.layout.landscape : R.layout.profile, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(MyActivity.orientation ? R.layout.landscape : R.layout.profile, parent, false);
        Bitmap bitmap;
        if (MyActivity.orientation) {
            if (MyActivity.numberPic / 4 > position){
                ImageView image1 = (ImageView) rowView.findViewById(R.id.imageLand1);
                bitmap = Bitmap.createScaledBitmap(values.get(position).get(0).getImage(),Math.min(values.get(position).get(0).getImage().getHeight(), values.get(position).get(0).getImage().getWidth()),Math.min(values.get(position).get(0).getImage().getHeight(), values.get(position).get(0).getImage().getWidth()),false);
                image1.setImageBitmap(bitmap);
                ImageView image2 = (ImageView) rowView.findViewById(R.id.imageLand2);
                bitmap = Bitmap.createScaledBitmap(values.get(position).get(1).getImage(),Math.min(values.get(position).get(1).getImage().getHeight(), values.get(position).get(1).getImage().getWidth()),Math.min(values.get(position).get(1).getImage().getHeight(), values.get(position).get(1).getImage().getWidth()),false);
                image2.setImageBitmap(bitmap);
                ImageView image3 = (ImageView) rowView.findViewById(R.id.imageLand3);
                bitmap = Bitmap.createScaledBitmap(values.get(position).get(2).getImage(),Math.min(values.get(position).get(2).getImage().getHeight(), values.get(position).get(2).getImage().getWidth()),Math.min(values.get(position).get(2).getImage().getHeight(), values.get(position).get(2).getImage().getWidth()),false);
                image3.setImageBitmap(bitmap);
                ImageView image4 = (ImageView) rowView.findViewById(R.id.imageLand4);
                bitmap = Bitmap.createScaledBitmap(values.get(position).get(3).getImage(),Math.min(values.get(position).get(3).getImage().getHeight(), values.get(position).get(3).getImage().getWidth()),Math.min(values.get(position).get(3).getImage().getHeight(), values.get(position).get(3).getImage().getWidth()),false);
                image4.setImageBitmap(bitmap);
                image1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("id",position * 4);
                        intent.setClass(context,ItemDisplayer.class);
                        context.startActivity(intent);
                    }
                });
                image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("id",position * 4 + 1);
                        intent.setClass(context,ItemDisplayer.class);
                        context.startActivity(intent);
                    }
                });
                image3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("id",position * 4 + 2);
                        intent.setClass(context,ItemDisplayer.class);
                        context.startActivity(intent);
                    }
                });
                image4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("id",position * 4 + 3);
                        intent.setClass(context,ItemDisplayer.class);
                        context.startActivity(intent);
                    }
                });
            }
        } else {
            if (MyActivity.numberPic / 2 > position){
                ImageView image1 = (ImageView) rowView.findViewById(R.id.imageProf1);
                bitmap = Bitmap.createScaledBitmap(values.get(position).get(0).getImage(),Math.min(values.get(position).get(0).getImage().getHeight(), values.get(position).get(0).getImage().getWidth()),Math.min(values.get(position).get(0).getImage().getHeight(), values.get(position).get(0).getImage().getWidth()),false);
                image1.setImageBitmap(bitmap);
                ImageView image2 = (ImageView) rowView.findViewById(R.id.imageProf2);
                bitmap = Bitmap.createScaledBitmap(values.get(position).get(1).getImage(),Math.min(values.get(position).get(1).getImage().getHeight(), values.get(position).get(1).getImage().getWidth()),Math.min(values.get(position).get(1).getImage().getHeight(), values.get(position).get(1).getImage().getWidth()),false);
                image2.setImageBitmap(bitmap);
                image1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("id",position * 2);
                        intent.setClass(context,ItemDisplayer.class);
                        context.startActivity(intent);
                    }
                });
                image2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.putExtra("id",position * 2 + 1);
                        intent.setClass(context,ItemDisplayer.class);
                        context.startActivity(intent);
                    }
                });
            }
        }
        return rowView;
    }
}
