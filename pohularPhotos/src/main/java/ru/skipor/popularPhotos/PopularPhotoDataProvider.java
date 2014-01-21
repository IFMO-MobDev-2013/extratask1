package ru.skipor.popularPhotos;

import android.util.Log;

import org.apache.http.HttpException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.skipor.Utils.HTTPUtils;

/**
 * Created by Vladimir Skipor on 1/18/14.
 * Email: vladimirskipor@gmail.com
 */
public class PopularPhotoDataProvider {
    private final static String TAG = "PopularPhotoDataProvider";
    private static final String[] SIZE_TAGS = {"orig", "XXXL", "XXL", "XL", "L", "M", "S", "XS", "XXS", "XXXS"};
    private static final String ORIGINAL_SIZE_TAG = "orig";
    private static final String URL_TAG = "href";

    public static final int OUTPUT_LIMIT = 20;
    private static final String QUERY_URL = "http://api-fotki.yandex.ru/api/recent/?format=json&limit=" + String.valueOf(OUTPUT_LIMIT);

    private final int smallImageSize, largeImageSize;

    public PopularPhotoDataProvider(int smallImageSize, int largeImageSize) {
        this.smallImageSize = smallImageSize;
        this.largeImageSize = largeImageSize;
    }


    public PhotoUrlsList getPopularPhotos() throws HttpException {

        try {
            PhotoUrlsList urlsList = new PhotoUrlsList();

            final String content = HTTPUtils.getContent(QUERY_URL);
            Log.d(TAG, content);
            JSONObject response = new JSONObject(content);
            JSONArray photosArray = response.getJSONArray("entries");
            for (int i = 0; i < photosArray.length(); i++) {
                JSONObject photo = photosArray.getJSONObject(i);
                JSONObject photoUrlsDesc = photo.getJSONObject("img");

                int firstExistingTag = 0; // use max size, and try to take smaller images references
                while (!photoUrlsDesc.has(SIZE_TAGS[firstExistingTag])) {
                    firstExistingTag++;
                }



                String smallImageUrl = photoUrlsDesc.getJSONObject(SIZE_TAGS[firstExistingTag]).getString(URL_TAG);
                String largeImageUrl = smallImageUrl;

                for (int k = firstExistingTag; k < SIZE_TAGS.length; k++) {
                    String currentSizeTag = SIZE_TAGS[k];
                    if (photoUrlsDesc.has(currentSizeTag)) {
                        JSONObject currentSizeUrlDesc = photoUrlsDesc.getJSONObject(currentSizeTag);
                        int currentWidth = currentSizeUrlDesc.getInt("width");
                        int currentHeight = currentSizeUrlDesc.getInt("height");
                        int currentMinSize = Math.min(currentHeight, currentWidth);
                        if(currentMinSize >= largeImageSize) {
                            largeImageUrl = currentSizeUrlDesc.getString(URL_TAG);
                        }
                        if(currentMinSize >= smallImageSize) {
                            smallImageUrl = currentSizeUrlDesc.getString(URL_TAG);
                        } else {
                            smallImageUrl = currentSizeUrlDesc.getString(URL_TAG); // take smaller size
                            break;
                        }
                    }
                }
//                Log.d(TAG, smallImageUrl);
//                Log.d(TAG, largeImageUrl);


                urlsList.smallImageUrlList.add(smallImageUrl);
                urlsList.largeImageUrlList.add(largeImageUrl);

            }

            return urlsList;
        } catch (JSONException e) {
            Log.e(TAG, "Error", e);

        }

        return null;


    }
}
