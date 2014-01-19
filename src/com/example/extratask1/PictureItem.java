package com.example.extratask1;

import android.graphics.Bitmap;

/**
 * Created with IntelliJ IDEA.
 * User: kris13
 * Date: 17.01.14
 * Time: 2:51
 * To change this template use File | Settings | File Templates.
 */
public class PictureItem {
    private Bitmap image;
    private String name;
    private String id;

    public PictureItem(String name, String id, Bitmap bitmap){
        this.id = id;
        this.name = name;
        this.image = bitmap;
    }

    public Bitmap getImage(){
        return image;
    }

    public String getName(){
        return name;
    }

    public String getId(){
        return id;
    }
}
