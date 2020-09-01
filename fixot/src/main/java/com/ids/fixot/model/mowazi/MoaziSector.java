package com.ids.fixot.model.mowazi;

import android.os.Parcel;
import android.os.Parcelable;

import com.ids.fixot.model.Sector;


/**
 * Created by dev on 6/20/2016.
 */

public class MoaziSector implements Parcelable {

    private int id;
    private String name;

    public MoaziSector(){
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }


    // "De-parcel object
    public MoaziSector(Parcel in) {

        id = in.readInt();
        name=in.readString();
    }

    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {
        public MoaziSector createFromParcel(Parcel in) {
            return new MoaziSector(in);
        }

        public MoaziSector[] newArray(int size) {
            return new MoaziSector[size];
        }
    };
}
