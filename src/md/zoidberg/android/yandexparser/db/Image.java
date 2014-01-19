package md.zoidberg.android.yandexparser.db;

import android.content.ContentValues;

import java.util.Date;

/**
 * Created by gfv on 19.01.14.
 */
public class Image {
    private String url;
    private Date lastUpdate;

    public Image(String url, Date lastUpdate) {
        this.url = url;
        this.lastUpdate = lastUpdate;
    }

    public String getUrl() {

        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
