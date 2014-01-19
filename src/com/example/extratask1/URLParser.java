package com.example.extratask1;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: PWR
 * Date: 19.01.14
 * Time: 20:08
 * To change this template use File | Settings | File Templates.
 */
public class URLParser {

    public URLParser() {}

    public ArrayList<String> getURLs (JSONObject jsonObject)
    {
        ArrayList<String> urls = new ArrayList<>();

        try
        {

            JSONArray array = jsonObject.getJSONArray("entries");

            for (int i = 0; i < array.length(); i++)
            {

                JSONObject photo = array.getJSONObject(i);
                JSONObject img = photo.getJSONObject("img");
                JSONObject img_xl = img.getJSONObject("XL");
                String url = img_xl.getString("href");

                urls.add(url);

            }

        } catch (JSONException e) {}

        return urls;

    }

}
