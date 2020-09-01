package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 4/3/2017.
 */

public class WebItem implements Parcelable {

    public static final Creator CREATOR
            = new Creator() {
        public WebItem createFromParcel(Parcel in) {
            return new WebItem(in);
        }

        public WebItem[] newArray(int size) {
            return new WebItem[size];
        }
    };
    private int ID;
    private String titleEn, titleAr, contentAr, contentEn, url;

    public WebItem() {
        setID(0);
        setTitleEn("");
        setTitleAr("");
        setContentEn("");
        setContentAr("");
    }

    // "De-parcel object
    public WebItem(Parcel in) {

        ID = in.readInt();
        titleEn = in.readString();
        titleAr = in.readString();
        contentAr = in.readString();
        contentEn = in.readString();
        url = in.readString();
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getTitleAr() {
        return titleAr;
    }

    public void setTitleAr(String titleAr) {
        this.titleAr = titleAr;
    }

    public String getContentAr() {
        return contentAr;
    }

    public void setContentAr(String contentAr) {
        this.contentAr = contentAr;
    }

    public String getContentEn() {
        return contentEn;
    }

    public void setContentEn(String contentEn) {
        this.contentEn = contentEn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(ID);
        dest.writeString(titleEn);
        dest.writeString(titleAr);
        dest.writeString(contentAr);
        dest.writeString(contentEn);
        dest.writeString(url);
    }
}
