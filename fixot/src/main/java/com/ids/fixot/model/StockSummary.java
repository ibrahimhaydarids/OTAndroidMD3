package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by user on 3/21/2017.
 */

public class StockSummary implements Parcelable {

    public static final Creator CREATOR
            = new Creator() {
        public StockSummary createFromParcel(Parcel in) {
            return new StockSummary(in);
        }

        public StockSummary[] newArray(int size) {
            return new StockSummary[size];
        }
    };
    private int id;
    private String availableShares, shareCount;
    private String averageCost, bid, last, totalMarket, unrealized, pendingOrders;
    private String unrealizedPercent, stockId, totalCost, symbolEn, symbolAr;
    private String securityId;

    public StockSummary() {

    }

    // "De-parcel object
    public StockSummary(Parcel in) {

        id = in.readInt();
        availableShares = in.readString();
        shareCount = in.readString();

        averageCost = in.readString();
        bid = in.readString();
        last = in.readString();
        totalMarket = in.readString();
        unrealized = in.readString();
        pendingOrders = in.readString();

        unrealizedPercent = in.readString();
        stockId = in.readString();
        totalCost = in.readString();
        symbolEn = in.readString();
        symbolAr = in.readString();
    }

    public String getSymbolEn() {
        return symbolEn;
    }

    public void setSymbolEn(String symbolEn) {
        this.symbolEn = symbolEn;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public String getSymbolAr() {
        return symbolAr;
    }

    public void setSymbolAr(String symbolAr) {
        this.symbolAr = symbolAr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public String getAvailableShares() {
        return availableShares;
    }

    public void setAvailableShares(String availableShares) {
        this.availableShares = availableShares;
    }

    public String getShareCount() {
        return shareCount;
    }

    public void setShareCount(String shareCount) {
        this.shareCount = shareCount;
    }

    public String getPendingOrders() {
        return pendingOrders;
    }

    public void setPendingOrders(String pendingOrders) {
        this.pendingOrders = pendingOrders;
    }

    public String getAverageCost() {
        return averageCost;
    }

    public void setAverageCost(String averageCost) {
        this.averageCost = averageCost;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(String totalCost) {
        this.totalCost = totalCost;
    }

    public String getTotalMarket() {
        return totalMarket;
    }

    public void setTotalMarket(String totalMarket) {
        this.totalMarket = totalMarket;
    }

    public String getUnrealized() {
        return unrealized;
    }

    public void setUnrealized(String unrealized) {
        this.unrealized = unrealized;
    }

    public String getUnrealizedPercent() {
        return unrealizedPercent;
    }

    public void setUnrealizedPercent(String unrealizedPercent) {
        this.unrealizedPercent = unrealizedPercent;
    }


    /*
    private int id, availableShares, shareCount;
    private String averageCost, bid, last, totalMarket, unrealized, pendingOrders;
    private String unrealizedPercent,stockId,totalCost,symbolEn,symbolAr;*/

    @Override
    public String toString() {
        return "StockSummary{" +
                "id=" + id +
                ", stockId=" + stockId +
                ", availableShares=" + availableShares +
                ", shareCount=" + shareCount +
                ", averageCost=" + averageCost +
                ", bid=" + bid +
                ", last=" + last +
                ", totalCost=" + totalCost +
                ", totalMarket=" + totalMarket +
                ", unrealized=" + unrealized +
                ", pendingOrders=" + pendingOrders +
                ", unrealizedPercent='" + unrealizedPercent + '\'' +
                '\n' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(availableShares);
        dest.writeString(shareCount);

        dest.writeString(averageCost);
        dest.writeString(bid);
        dest.writeString(last);
        dest.writeString(totalMarket);
        dest.writeString(unrealized);
        dest.writeString(pendingOrders);

        dest.writeString(unrealizedPercent);
        dest.writeString(stockId);
        dest.writeString(totalCost);
        dest.writeString(symbolEn);
        dest.writeString(symbolAr);

    }
}

