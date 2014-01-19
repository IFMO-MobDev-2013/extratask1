package ru.marsermd.flikrVigintus.utility.http.image;

/**
 * Created with IntelliJ IDEA.
 * User: georgeee
 * Date: 29.09.13
 * Time: 22:03
 * To change this template use File | Settings | File Templates.
 */
public class ImageApiResult {
    protected Integer pageNumber = null;
    protected Integer pageCount = null;
    protected Integer totalImageCount = null;
    protected String title;

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    protected Image[] images = null;

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public void setTotalImageCount(Integer totalImageCount) {
        this.totalImageCount = totalImageCount;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public Integer getTotalImageCount() {
        return totalImageCount;
    }

    public static abstract class Image {
        public abstract String getTitle();

        public abstract String getSmallImageUrl();

        public abstract String getDefaultImageUrl();

        @Override
        public String toString() {
            return "[ImageApiResult.Image title=" + getTitle() + " smallUrl=" + getSmallImageUrl() + " defaultUrl=" + getDefaultImageUrl() + " ]";
        }
    }
}
