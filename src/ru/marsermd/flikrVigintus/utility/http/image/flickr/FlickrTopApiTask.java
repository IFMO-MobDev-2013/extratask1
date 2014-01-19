package ru.marsermd.flikrVigintus.utility.http.image.flickr;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.marsermd.flikrVigintus.utility.http.image.ImageApiTask;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 22:41
 * To change this template use File | Settings | File Templates.
 */
public class FlickrTopApiTask extends ImageApiTask {
    protected String apiKey;

    public FlickrTopApiTask(String apiKey, int pageNum) {
        this.apiKey = apiKey;
        this.pageNumber = pageNum;
    }
    public FlickrTopApiTask(String apiKey, int pageNum, int perPage) {
        this.apiKey = apiKey;
        this.pageNumber = pageNum;
        this.perPage = perPage;
    }

    public FlickrTopApiTask(String apiKey) {
        this(apiKey, 1);
    }

    @Override
    protected FlickrImageApiResult getResultByJson(JSONObject jObject) {
        FlickrImageApiResult result = new FlickrImageApiResult();
        try {
            Log.d("Flickr.totalCount", jObject.getJSONObject("photos").getString("total"));
            JSONObject jsonObject = jObject.getJSONObject("photos");
            result.setPageCount(jsonObject.getInt("pages"));

            result.setPageNumber(jsonObject.getInt("page"));
            String totalCnt = jsonObject.getString("total");
            FlickrImageApiResult.FlickrImage[] images;
            if (totalCnt.equals("null")) {
                images = new FlickrImageApiResult.FlickrImage[]{};
            } else {
                try {
                    result.setTotalImageCount(Integer.valueOf(totalCnt));
                } catch (NumberFormatException ex) {
                    throw new JSONException("Total count is unexpectedly NAN: " + totalCnt + " " + (totalCnt == null ? "" : totalCnt.getClass()));
                }
                JSONArray imageJSONArray = jsonObject.getJSONArray("photo");
                images = new FlickrImageApiResult.FlickrImage[imageJSONArray.length()];
                for (int i = 0; i < images.length; ++i) {
                    images[i] = new FlickrImageApiResult.FlickrImage();
                    images[i].parseFromJSON(imageJSONArray.getJSONObject(i));
                }
            }
            result.setImages(images);
        } catch (JSONException e) {
            handleJSONException(e, jObject.toString());
            return null;
        }
        return result;
    }

    @Override
    protected String getUrl() {
        String url = null;
        Map<String, String> getParams = new Hashtable<String, String>();
        getParams.put("method", "flickr.interestingness.getList");
        getParams.put("api_key", apiKey);
        if (pageNumber != null) getParams.put("page", pageNumber.toString());
        if (perPage != null) getParams.put("per_page", perPage.toString());
        getParams.put("format", "json");
        getParams.put("nojsoncallback", "1");
        try {
            url = composeUrl("http://api.flickr.com/services/rest/", getParams);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
