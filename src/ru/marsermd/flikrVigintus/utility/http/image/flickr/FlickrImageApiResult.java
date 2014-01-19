package ru.marsermd.flikrVigintus.utility.http.image.flickr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.marsermd.flikrVigintus.utility.http.HttpUtility;
import ru.marsermd.flikrVigintus.utility.http.JsonResponseHttpTask;
import ru.marsermd.flikrVigintus.utility.http.image.ImageApiResult;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 22:18
 * To change this template use File | Settings | File Templates.
 */
public class FlickrImageApiResult extends ImageApiResult {

    public static class FlickrImage extends Image {
        protected String id;
        protected String owner;
        protected String secret;
        protected String server;
        protected long farm;
        protected String title;
        protected boolean isPublic;
        protected boolean isFriend;
        protected boolean isFamily;
        Size[] sizes = null;
        boolean sizesLoaded = false;

        public void asyncLoadSizes(SizeLoadedHandler handler, String apiKey) {
            SizeLoader loader = new SizeLoader(apiKey);
            loader.setSizeLoadedHandler(handler);
            handler.setLoader(loader);
            if (!sizesLoaded)
                loader.executeOnHttpTaskExecutor();
        }

        public Size getSizeWithClosestWidth(int width) {
            if (sizes == null) {
                return null;
            }
            int minDiff = Integer.MAX_VALUE;
            Size result = null;
            for (Size size : sizes) {
                int diff = Math.abs(size.width - width);
                if (diff < minDiff) {
                    minDiff = diff;
                    result = size;
                }
            }
            return result;
        }

        public String getTitle() {
            return title;
        }

        public void parseFromJSON(JSONObject params) throws JSONException {
            id = params.getString("id");
            owner = params.getString("owner");
            secret = params.getString("secret");
            server = params.getString("server");
            farm = params.getLong("farm");
            title = params.getString("title");
            isPublic = params.getInt("ispublic") == 1;
            isFriend = params.getInt("isfriend") == 1;
            isFamily = params.getInt("isfamily") == 1;
        }

        protected String getImageUrl(String sizeMarker) {
            return "http://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + (sizeMarker == null ? "" : "_" + sizeMarker) + ".jpg";
        }

        @Override
        public String getSmallImageUrl() {
            return getImageUrl("q");
        }

        @Override
        public String getDefaultImageUrl() {
            return getImageUrl(null);
        }

        public Size[] getSizes() {
            return sizes;
        }

        public static class Size {
            int width, height;
            String source;

            public Size(JSONObject jsonObject) throws JSONException {
                width = Integer.valueOf(jsonObject.getString("width"));
                height = Integer.valueOf(jsonObject.getString("height"));
                source = jsonObject.getString("source");
            }

            public int getWidth() {
                return width;
            }

            public int getHeight() {
                return height;
            }

            public String getSource() {
                return source;
            }
        }

        public static class SizeLoadedHandler {

            SizeLoader loader;

            public void setLoader(SizeLoader loader) {
                this.loader = loader;
            }

            protected void onPostExecute(Size[] sizes) {

            }

            protected Size[] get() throws ExecutionException, InterruptedException {
                return loader.get();
            }
        }

        protected class SizeLoader extends JsonResponseHttpTask<Size[]> {
            protected String apiKey;
            private SizeLoadedHandler sizeLoadedHandler = null;

            public SizeLoader(String apiKey) {
                this.apiKey = apiKey;
            }

            public String getApiKey() {
                return apiKey;
            }

            public void setApiKey(String apiKey) {
                this.apiKey = apiKey;
            }

            public void setSizeLoadedHandler(SizeLoadedHandler sizeLoadedHandler) {
                this.sizeLoadedHandler = sizeLoadedHandler;
            }

            @Override
            protected String getUrl() {
                return "http://api.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=" + apiKey + "&photo_id=" + id + "&format=json&nojsoncallback=1";
            }

            @Override
            protected Size[] getResultByJson(JSONObject jsonObject) {
                sizes = getResultByJsonImpl(jsonObject);
                sizesLoaded = true;
                return sizes;
            }

            protected Size[] getResultByJsonImpl(JSONObject jsonObject) {
//                Log.d("FlickrImageApiResult", jsonObject.toString());
                try {
                    JSONObject _sizesEl = jsonObject.getJSONObject("sizes");
                    JSONArray sizes = _sizesEl.getJSONArray("size");
                    ArrayList<Size> sizeList = new ArrayList<Size>();
                    for (int i = 0; i < sizes.length(); ++i) {
                        try {
//                            Log.d("FlickrImageApiResult", sizes.getJSONObject(i).toString());
                            Size size = new Size(sizes.getJSONObject(i));
                            if (size != null) sizeList.add(size);
                        } catch (JSONException ex) {
                            handleJSONException(ex, sizes.getJSONObject(i).toString());
                        }
                    }
                    if (sizeList.isEmpty()) return null;
                    Size[] result = new Size[sizeList.size()];
                    return sizeList.toArray(result);
                } catch (JSONException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    handleJSONException(e, jsonObject.toString());
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Size[] sizes) {
                if (sizeLoadedHandler != null) sizeLoadedHandler.onPostExecute(sizes);
            }

            @Override
            protected Executor getExecutor() {
                return HttpUtility.getInstance().getImageApiExecutor();
            }
        }

    }
}
