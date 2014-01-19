package md.zoidberg.android.yandexparser;

import android.util.Log;
import md.zoidberg.android.yandexparser.db.Image;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Date;

public class FotkiApiParser extends DefaultHandler {
    ArrayList<Image> images;

    final String IMAGE = "f:img";
    final String PREFERRED_SIZE = "XL";

    FotkiApiParser(ArrayList<Image> images) {
        super();
        this.images = images;
    }

    public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
        if (qName.equals(IMAGE) && attrs.getValue(attrs.getIndex("size")).equals(PREFERRED_SIZE)) {
            images.add(new Image(attrs.getValue(attrs.getIndex("href")), new Date()));
        }
    }
}