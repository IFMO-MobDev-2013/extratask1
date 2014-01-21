package ru.marsermd.flikrVigintus.utility.GUI;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import ru.marsermd.flikrVigintus.R;
import ru.marsermd.flikrVigintus.utility.http.image.ImageApiResult;
import ru.marsermd.flikrVigintus.utility.http.image.flickr.FlickrTopApiTask;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: marsermd
 * Date: 02.10.13
 * Time: 2:13
 * To change this template use File | Settings | File Templates.
 */
public class PicturesAdapterLandscape extends PicturesAdapter {

    @Override
    public int getCount() {
        return count / 2;
    }

    @Override
    public Object getItem(int position) {
        return position / 2;
    }

    @Override
    public long getItemId(int position) {
        return position / 2;
    }

    public PicturesAdapterLandscape(Activity context) {
        super(context);
    }

    private class ViewHolder {
        ImageView leftLeftView, leftRightView, rightLeftView, rightRightView;
        TwoPicturesModel model_left, model_right;

        public ViewHolder(ImageView leftLeftView, ImageView leftRightView, ImageView rightLeftView, ImageView rightRightView) {
            this.leftLeftView = leftLeftView;
            this.leftRightView = leftRightView;
            this.rightLeftView = rightLeftView;
            this.rightRightView = rightRightView;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.rowlayout, null);
            ImageView leftLeftView = (ImageView) convertView.findViewById(R.id.icon_left_left);
            leftLeftView.getLayoutParams().height = (int)(width * 0.2);
            ImageView leftRightView = (ImageView) convertView.findViewById(R.id.icon_left_right);
            leftRightView.getLayoutParams().height = (int)(width * 0.2);
            ImageView rightLeftView = (ImageView) convertView.findViewById(R.id.icon_right_left);
            rightLeftView.getLayoutParams().height = (int)(width * 0.2);
            ImageView rightRightView = (ImageView) convertView.findViewById(R.id.icon_right_right);
            rightRightView.getLayoutParams().height = (int)(width * 0.2);
            ViewHolder viewHolder = new ViewHolder(leftLeftView, leftRightView, rightLeftView, rightRightView);
            convertView.setTag(viewHolder);
            holder = viewHolder;
        } else {
            holder = (ViewHolder) convertView.getTag();
            if (holder.model_left != null) {
                holder.model_left.cancel();
            }
            if (holder.model_right != null) {
                holder.model_right.cancel();
            }
        }
        holder.model_left = list.get(position * 2);
        holder.model_left.setViews(holder.leftLeftView, holder.leftRightView);

        holder.model_right = list.get(position * 2 + 1);
        holder.model_right.setViews(holder.rightLeftView, holder.rightRightView);



        return convertView;
    }
}
