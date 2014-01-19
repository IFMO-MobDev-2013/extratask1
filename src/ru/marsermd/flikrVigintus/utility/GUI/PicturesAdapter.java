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
import ru.marsermd.flikrVigintus.utility.http.image.flickr.FlickrImageApiTask;
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
public class PicturesAdapter extends BaseAdapter {
    protected List<TwoPicturesModel> list;

    protected String FLICKR_API_KEY;
    protected FlickrTopApiTask topTask;
    protected List<ImageApiResult.Image> images;
    protected List<FlickrTopApiTask> imageTasks;
    protected int page = 0;
    protected int width;
    protected final int IMAGES_PER_PAGE = 20;
    boolean load = false;

    public void init() {
        images.clear();

        for (FlickrTopApiTask task : imageTasks) {
            task.cancel(true);
        }
        imageTasks.clear();

        for (TwoPicturesModel model : list) {
            model.cancel();
        }
        list.clear();

        page = 0;
        count = 0;
        load = false;
        loadMore();
    }

    public void loadMore() {
        if (load)
            return;
        load = true;
        page++;
        Log.d(PicturesAdapter.class.getCanonicalName(), "flick api key: " + FLICKR_API_KEY);
        topTask = new FlickrTopApiTask(FLICKR_API_KEY, page, IMAGES_PER_PAGE) {
            @Override
            protected void onPostExecute(ImageApiResult imageApiResult) {
                if (imageApiResult == null) {
                    Log.d(PicturesAdapter.class.getCanonicalName(), "ImageApiResult is null searchRequest:" + " page:" + page);
                } else {
                    ImageApiResult.Image[] resultImages = imageApiResult.getImages();
                    for (int i = 0; i < resultImages.length; i++) {
                        images.add(resultImages[i]);
                    }
                    for (int i = 0; i < resultImages.length / 2; i++) {
                        addItem();
                    }
                }
                imageTasks.remove(this);
                notifyDataSetChanged();
                load = false;
            }
        };
        imageTasks.add(topTask);
        topTask.executeOnHttpTaskExecutor();
    }

    public void addItem() {
        TwoPicturesModel item = new TwoPicturesModel(images.get(count * 2), images.get(count * 2 + 1));
        item.download();
        list.add(item);
        count++;
    }

    protected Activity context;

    protected int count;

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public PicturesAdapter(Activity context) {
        super();
        this.context = context;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        list = new LinkedList<TwoPicturesModel>();
        images = new ArrayList<ImageApiResult.Image>();
        imageTasks = new LinkedList<FlickrTopApiTask>();
        count = 0;
        FLICKR_API_KEY = context.getString(R.string.flickr_api_key);
    }

    private class ViewHolder {
        ImageView leftView, rightView;
        TwoPicturesModel model;

        public ViewHolder(ImageView leftView, ImageView rightView) {
            this.leftView = leftView;
            this.rightView = rightView;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.rowlayout, null);
            ImageView leftView = (ImageView) convertView.findViewById(R.id.icon_left);
            leftView.getLayoutParams().height = (int)(width * 0.35);
            ImageView rightView = (ImageView) convertView.findViewById(R.id.icon_right);
            rightView.getLayoutParams().height = (int)(width * 0.35);
            ViewHolder viewHolder = new ViewHolder(leftView, rightView);
            convertView.setTag(viewHolder);
            holder = viewHolder;
        } else {
            holder = (ViewHolder) convertView.getTag();
            if (holder.model != null) {
                holder.model.cancel();
            }
            holder.model = list.get(position);
            holder.model.setViews(holder.leftView, holder.rightView);
        }


        return convertView;
    }
}
