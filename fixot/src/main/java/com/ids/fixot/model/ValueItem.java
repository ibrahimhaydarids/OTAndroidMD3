package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amal on 7/20/2017.
 */

public class ValueItem implements Parcelable {

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public ValueItem createFromParcel(Parcel in) {
            return new ValueItem(in);
        }

        public ValueItem[] newArray(int size) {
            return new ValueItem[size];
        }
    };
    private String title, value, value2;
    private int color;

    public ValueItem() {
    }

    public ValueItem(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public ValueItem(String title, String value, int color, String value2) {

        this.title = title;
        this.value = value;
        this.color = color;
        this.value2 = value2;
    }

    // "De-parcel object
    public ValueItem(Parcel in) {

        title = in.readString();
        value = in.readString();
        value2 = in.readString();
        color = in.readInt();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(title);
        dest.writeString(value);
        dest.writeString(value2);
        dest.writeInt(color);
    }
}

