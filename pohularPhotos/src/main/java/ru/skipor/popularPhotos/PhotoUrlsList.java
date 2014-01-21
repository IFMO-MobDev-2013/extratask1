package ru.skipor.popularPhotos;

import java.util.ArrayList;

/**
 * Created by Vladimir Skipor on 1/18/14.
 * Email: vladimirskipor@gmail.com
 */
public class PhotoUrlsList {
    private final static String TAG = "PhotoUrlsList";

    public ArrayList<String> smallImageUrlList, largeImageUrlList;

    public PhotoUrlsList(ArrayList<String> smallImageUrlList, ArrayList<String> largeImageUrlList) {
        this.smallImageUrlList = smallImageUrlList;
        this.largeImageUrlList = largeImageUrlList;
    }

    public PhotoUrlsList() {
        smallImageUrlList = new ArrayList<String>();
        largeImageUrlList = new ArrayList<String>();
    }
}
