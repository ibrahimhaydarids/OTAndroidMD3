package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dev on 10/4/2016.
 */

public class Picture implements Parcelable {

    public static final Creator CREATOR
            = new Creator() {
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };
    private int id, pageSize, typeId;
    private String caption, fileName, filepath, youtubePath;

    public Picture() {

    }

    // "De-parcel object
    public Picture(Parcel in) {

        id = in.readInt();
        pageSize = in.readInt();
        caption = in.readString();
        fileName = in.readString();
        filepath = in.readString();
        youtubePath = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getYoutubePath() {
        return youtubePath;
    }

    public void setYoutubePath(String youtubePath) {
        this.youtubePath = youtubePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        /*private int id,pageSize,typeId;
        private String caption, fileName, filepath, youtubePath;*/

        dest.writeInt(id);
        dest.writeInt(pageSize);
        dest.writeString(caption);
        dest.writeString(fileName);
        dest.writeString(filepath);
        dest.writeString(youtubePath);
    }
}
