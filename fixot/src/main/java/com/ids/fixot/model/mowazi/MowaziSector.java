package com.ids.fixot.model.mowazi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziSector implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MowaziSector createFromParcel(Parcel in) {
            return new MowaziSector(in);
        }

        public MowaziSector[] newArray(int size) {
            return new MowaziSector[size];
        }
    };
    private int id;
    private String name;

    public MowaziSector() {
    }

    // "De-parcel object
    public MowaziSector(Parcel in) {

        id = in.readInt();
        name = in.readString();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    public int describeContents() {
        return 0;
    }

}
