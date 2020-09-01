package com.ids.fixot.model.mowazi;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziCompany implements Parcelable {// company is a sector

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MowaziCompany createFromParcel(Parcel in) {
            return new MowaziCompany(in);
        }

        public MowaziCompany[] newArray(int size) {
            return new MowaziCompany[size];
        }
    };
    private int companyId;
    private String nameAr;
    private String nameEn;
    private String symbolAr;
    private String symbolEn;
    private int sectorId;
    private int countryId;
    private String isStoped;
    private String forBid;
    private String stoppedDate;
    private String creationDate;
    private String descEn;
    private String descAr;
    private String lastUpdate;
    private String untransformed;
    private String isIssue;
    private String sectorName, sectorNameAr;
    private MowaziSector sector;

    public MowaziCompany() {

    }

    // "De-parcel object
    public MowaziCompany(Parcel in) {

        companyId = in.readInt();
        nameAr = in.readString();
        nameEn = in.readString();
        symbolAr = in.readString();
        symbolEn = in.readString();
        sectorId = in.readInt();
        companyId = in.readInt();
        isStoped = in.readString();
        forBid = in.readString();
        stoppedDate = in.readString();
        creationDate = in.readString();
        descEn = in.readString();
        descAr = in.readString();
        lastUpdate = in.readString();
        untransformed = in.readString();
        sectorName = in.readString();
        sectorNameAr = in.readString();
    }

    public String getSectorName() {
        return sectorName;
    }

    public void setSectorName(String sectorName) {
        this.sectorName = sectorName;
    }

    public String getIsIssue() {
        return isIssue;
    }

    public void setIsIssue(String isIssue) {
        this.isIssue = isIssue;
    }

    public MowaziSector getSector() {
        return sector;
    }

    public void setSector(MowaziSector sector) {
        this.sector = sector;
    }

    public String getUntransformed() {
        return untransformed;
    }

    public void setUntransformed(String untransformed) {
        this.untransformed = untransformed;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getDescAr() {
        return descAr;
    }

    public void setDescAr(String descAr) {
        this.descAr = descAr;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getDescEn() {
        return descEn;
    }

    public void setDescEn(String descEn) {
        this.descEn = descEn;
    }

    public String getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(String stoppedDate) {
        this.stoppedDate = stoppedDate;
    }

    public String getForBid() {
        return forBid;
    }

    public void setForBid(String forBid) {
        this.forBid = forBid;
    }

    public String getIsStoped() {
        return isStoped;
    }

    public void setIsStoped(String isStoped) {
        this.isStoped = isStoped;
    }

    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    public int getSectorId() {
        return sectorId;
    }

    public void setSectorId(int sectorId) {
        this.sectorId = sectorId;
    }

    public String getSymbolEn() {
        return symbolEn;
    }

    public void setSymbolEn(String symbolEn) {
        this.symbolEn = symbolEn;
    }

    public String getSymbolAr() {
        return symbolAr;
    }

    public void setSymbolAr(String symbolAr) {
        this.symbolAr = symbolAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getSectorNameAr() {
        return sectorNameAr;
    }

    public void setSectorNameAr(String sectorNameAr) {
        this.sectorNameAr = sectorNameAr;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        return getCompanyId() == ((MowaziCompany) o).getCompanyId();
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(companyId);
        dest.writeString(nameAr);
        dest.writeString(nameEn);
        dest.writeString(symbolAr);
        dest.writeString(symbolEn);
        dest.writeInt(sectorId);
        dest.writeInt(companyId);
        dest.writeString(isStoped);
        dest.writeString(forBid);
        dest.writeString(stoppedDate);
        dest.writeString(creationDate);
        dest.writeString(descEn);
        dest.writeString(descAr);
        dest.writeString(lastUpdate);
        dest.writeString(untransformed);
        dest.writeString(sectorName);
        dest.writeString(sectorNameAr);
    }

    @Override
    public String toString() {

        try {
            Log.wtf("sector", getSector().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Company{" + "companyId=" + companyId + "sectorId=" + sectorId
                + '}';

    }

}
