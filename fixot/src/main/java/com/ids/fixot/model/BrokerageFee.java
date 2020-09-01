package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amal on 11/29/2017.
 */

public class BrokerageFee implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public BrokerageFee createFromParcel(Parcel source) {
            return new BrokerageFee(source);
        }

        public BrokerageFee[] newArray(int size) {
            return new BrokerageFee[size];
        }
    };
    private double brokerageFeeId, brokerageLimit, brokerageLimitDiscount, clearing, companyBrokerageFree, marketBrokerageFee, marketId, marketSegmentId, minimumBrokerageFee, totalBrokerageFee;
    private String instrumentId;

    public BrokerageFee() {
    }

    public BrokerageFee(Parcel source) {

        readFromParcel(source);

    }

    public double getBrokerageFeeId() {
        return brokerageFeeId;
    }

    public void setBrokerageFeeId(double brokerageFeeId) {
        this.brokerageFeeId = brokerageFeeId;
    }

    public double getBrokerageLimit() {
        return brokerageLimit;
    }

    public void setBrokerageLimit(double brokerageLimit) {
        this.brokerageLimit = brokerageLimit;
    }

    public double getBrokerageLimitDiscount() {
        return brokerageLimitDiscount;
    }

    public void setBrokerageLimitDiscount(double brokerageLimitDiscount) {
        this.brokerageLimitDiscount = brokerageLimitDiscount;
    }

    public double getClearing() {
        return clearing;
    }

    public void setClearing(double clearing) {
        this.clearing = clearing;
    }

    public double getCompanyBrokerageFree() {
        return companyBrokerageFree;
    }

    public void setCompanyBrokerageFree(double companyBrokerageFree) {
        this.companyBrokerageFree = companyBrokerageFree;
    }

    public double getMarketBrokerageFee() {
        return marketBrokerageFee;
    }

    public void setMarketBrokerageFee(double marketBrokerageFee) {
        this.marketBrokerageFee = marketBrokerageFee;
    }

    public double getMarketId() {
        return marketId;
    }

    public void setMarketId(double marketId) {
        this.marketId = marketId;
    }

    public double getMarketSegmentId() {
        return marketSegmentId;
    }

    public void setMarketSegmentId(double marketSegmentId) {
        this.marketSegmentId = marketSegmentId;
    }

    public double getMinimumBrokerageFee() {
        return minimumBrokerageFee;
    }

    public void setMinimumBrokerageFee(double minimumBrokerageFee) {
        this.minimumBrokerageFee = minimumBrokerageFee;
    }

    public double getTotalBrokerageFee() {
        return totalBrokerageFee;
    }

    public void setTotalBrokerageFee(double totalBrokerageFee) {
        this.totalBrokerageFee = totalBrokerageFee;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeDouble(this.brokerageFeeId);
        dest.writeDouble(this.brokerageLimit);
        dest.writeDouble(this.brokerageLimitDiscount);
        dest.writeDouble(this.clearing);
        dest.writeDouble(this.companyBrokerageFree);
        dest.writeDouble(this.marketBrokerageFee);
        dest.writeDouble(this.marketId);
        dest.writeDouble(this.marketSegmentId);
        dest.writeDouble(this.minimumBrokerageFee);
        dest.writeDouble(this.totalBrokerageFee);
        dest.writeString(this.instrumentId);
    }

    private void readFromParcel(Parcel source) {

        this.brokerageFeeId = source.readDouble();
        this.brokerageLimit = source.readDouble();
        this.brokerageLimitDiscount = source.readDouble();
        this.clearing = source.readDouble();
        this.companyBrokerageFree = source.readDouble();
        this.marketBrokerageFee = source.readDouble();
        this.marketId = source.readDouble();
        this.marketSegmentId = source.readDouble();
        this.minimumBrokerageFee = source.readDouble();
        this.totalBrokerageFee = source.readDouble();
        this.instrumentId = source.readString();

    }

}
