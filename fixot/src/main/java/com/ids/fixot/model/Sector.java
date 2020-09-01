package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amal on 3/23/2017.
 */

public class Sector implements Parcelable {

    public static final Creator<Sector> CREATOR = new Creator<Sector>() {
        @Override
        public Sector createFromParcel(Parcel in) {
            return new Sector(in);
        }

        @Override
        public Sector[] newArray(int size) {
            return new Sector[size];
        }
    };
    private String SectorID;
    private double Amount, ChangeWeight, ChangeWeightPercent, High, HighWeight, LastWeight, Low,
            LowWeight, Open, OpenWeight, ReferencePriceWeight, Trades, Volume;
    private String NameAr, NameEn, Change, ChangePercent, Value;
    private String SecuritiesDown, SecuritiesNbTraded, SecuritiesUp;
    private double ReferencePrice, High52, Low52;

    public Sector() {
    }

    protected Sector(Parcel in) {

        SectorID = in.readString();
        Amount = in.readDouble();
        ChangeWeight = in.readDouble();
        ChangeWeightPercent = in.readDouble();
        High = in.readDouble();
        HighWeight = in.readDouble();
        LastWeight = in.readDouble();
        Low = in.readDouble();
        LowWeight = in.readDouble();
        Open = in.readDouble();
        OpenWeight = in.readDouble();
        ReferencePriceWeight = in.readDouble();
        Trades = in.readDouble();
        Volume = in.readDouble();

        NameAr = in.readString();
        NameEn = in.readString();
        Change = in.readString();
        ChangePercent = in.readString();
        Value = in.readString();

        ReferencePrice = in.readDouble();
        High52 = in.readDouble();
        Low52 = in.readDouble();

    }

    public String getSecuritiesDown() {
        return SecuritiesDown;
    }

    public void setSecuritiesDown(String SecuritiesDown) {
        SecuritiesDown = SecuritiesDown;
    }

    public String getSecuritiesUp() {
        return SecuritiesDown;
    }

    public void setSecuritiesUp(String SecuritiesUp) {
        SecuritiesUp = SecuritiesUp;
    }

    public String getSecuritiesNbTraded() {
        return SecuritiesNbTraded;
    }

    public void setSecuritiesNbTraded(String SecuritiesNbTraded) {
        SecuritiesNbTraded = SecuritiesNbTraded;
    }

    public String getSectorID() {
        return SectorID;
    }

    public void setSectorID(String sectorID) {
        SectorID = sectorID;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getChange() {
        return Change;
    }

    public void setChange(String change) {
        Change = change;
    }

    public String getChangePercent() {
        return ChangePercent;
    }

    public void setChangePercent(String changePercent) {
        ChangePercent = changePercent;
    }

    public double getChangeWeight() {
        return ChangeWeight;
    }

    public void setChangeWeight(double changeWeight) {
        ChangeWeight = changeWeight;
    }

    public double getChangeWeightPercent() {
        return ChangeWeightPercent;
    }

    public void setChangeWeightPercent(double changeWeightPercent) {
        ChangeWeightPercent = changeWeightPercent;
    }

    public double getHigh() {
        return High;
    }

    public void setHigh(double high) {
        High = high;
    }

    public double getHighWeight() {
        return HighWeight;
    }

    public void setHighWeight(double highWeight) {
        HighWeight = highWeight;
    }

    public double getLastWeight() {
        return LastWeight;
    }

    public void setLastWeight(double lastWeight) {
        LastWeight = lastWeight;
    }

    public double getLow() {
        return Low;
    }

    public void setLow(double low) {
        Low = low;
    }

    public double getLowWeight() {
        return LowWeight;
    }

    public void setLowWeight(double lowWeight) {
        LowWeight = lowWeight;
    }

    public double getOpen() {
        return Open;
    }

    public void setOpen(double open) {
        Open = open;
    }

    public double getOpenWeight() {
        return OpenWeight;
    }

    public void setOpenWeight(double openWeight) {
        OpenWeight = openWeight;
    }

    public double getReferencePriceWeight() {
        return ReferencePriceWeight;
    }

    public void setReferencePriceWeight(double referencePriceWeight) {
        ReferencePriceWeight = referencePriceWeight;
    }

    public double getTrades() {
        return Trades;
    }

    public void setTrades(double trades) {
        Trades = trades;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public double getReferencePrice() {
        return ReferencePrice;
    }

    public void setReferencePrice(double referencePrice) {
        ReferencePrice = referencePrice;
    }

    public double getHigh52() {
        return High52;
    }

    public void setHigh52(double high52) {
        High52 = high52;
    }

    public double getLow52() {
        return Low52;
    }

    /*private int SectorID,Amount,Change,ChangePercent,ChangeWeight,ChangeWeightPercent,High,HighWeight,LastWeight,Low,
    LowWeight,Open,OpenWeight,ReferencePriceWeight,Trades,Volume;
    private String NameAr,NameEn;
    private double Value,ReferencePrice,High52,Low52;*/

    public void setLow52(double low52) {
        Low52 = low52;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(SectorID);
        dest.writeDouble(Amount);
        dest.writeDouble(ChangeWeight);
        dest.writeDouble(ChangeWeightPercent);
        dest.writeDouble(High);
        dest.writeDouble(HighWeight);
        dest.writeDouble(LastWeight);
        dest.writeDouble(Low);
        dest.writeDouble(LowWeight);
        dest.writeDouble(Open);
        dest.writeDouble(OpenWeight);
        dest.writeDouble(ReferencePriceWeight);
        dest.writeDouble(Trades);
        dest.writeDouble(Volume);

        dest.writeString(NameAr);
        dest.writeString(NameEn);
        dest.writeString(Change);
        dest.writeString(ChangePercent);
        dest.writeString(Value);

        dest.writeDouble(ReferencePrice);
        dest.writeDouble(High52);
        dest.writeDouble(Low52);
    }
}
