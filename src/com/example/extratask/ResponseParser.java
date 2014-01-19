package com.example.extratask;


import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResponseParser {
    public ResponseParser() {}

    public ArrayList<String> getURLList(JSONObject jsonObject) {
        ArrayList<String> urls = new ArrayList<>();

        try {
            JSONArray array = jsonObject.getJSONObject("photos").getJSONArray("photo");
            for (int i = 0; i < array.length(); i++) {
                JSONObject photo = array.getJSONObject(i);
                String url = "http://farm" + photo.getInt("farm")
                        + ".staticflickr.com/"
                        + photo.getString("server") + "/"
                        + photo.getString("id") + "_" + photo.getString("secret") + ".jpg";
                urls.add(url);
            }
        } catch (JSONException e) {
            Log.e("PARSER", "error");
        }

        return urls;
    }
}
