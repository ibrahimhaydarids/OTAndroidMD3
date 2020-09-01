package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ids.fixot.classes.Picture;

import java.util.ArrayList;

/**
 * Created by dev on 10/4/2016.
 */

public class NewsItem implements Parcelable {

    public static final Creator CREATOR
            = new Creator() {
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
    private int id, pageSize;
    private String title, summary, description, isHomePage, publishDate, Head, Details, CreationDate, Link, Time;
    private ArrayList<Picture> pictures = new ArrayList<Picture>();
    private String securityId;

    public NewsItem() {

    }

    // "De-parcel object
    public NewsItem(Parcel in) {

        id = in.readInt();
        pageSize = in.readInt();
        title = in.readString();
        summary = in.readString();
        description = in.readString();
        isHomePage = in.readString();
        publishDate = in.readString();

        Head = in.readString();
        Details = in.readString();
        CreationDate = in.readString();
        Link = in.readString();
        Time = in.readString();

        in.readList(pictures, getClass().getClassLoader());
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        securityId = securityId;
    }

    public String getHead() {
        return Head;
    }

    public void setHead(String head) {
        Head = head;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsHomePage() {
        return isHomePage;
    }

    public void setIsHomePage(String isHomePage) {
        this.isHomePage = isHomePage;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public ArrayList<Picture> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<Picture> pictures) {
        this.pictures = pictures;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(pageSize);
        dest.writeString(title);
        dest.writeString(summary);
        dest.writeString(description);
        dest.writeString(isHomePage);
        dest.writeString(publishDate);
        dest.writeString(Head);
        dest.writeString(Details);
        dest.writeString(CreationDate);
        dest.writeString(Link);
        dest.writeString(Time);
        dest.writeList(pictures);

    }
}
