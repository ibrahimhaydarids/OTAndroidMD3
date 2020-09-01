package com.ids.fixot.model.mowazi;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOrderBook {

    private MowaziCompany company = new MowaziCompany();
    private int askquantity;
    private int companyId;
    private int bidQuantity;
    private double askPrice;
    private double bidPrice;
    private int askCount;
    private int bidCount;
    private int id;

    public MowaziOrderBook() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBidcount(int bidCount) {
        this.bidCount = bidCount;
    }

    public int getBidCount() {
        return bidCount;
    }

    public int getAskCount() {
        return askCount;
    }

    public void setAskCount(int askCount) {
        this.askCount = askCount;
    }

    public double getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public double getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(double askPrice) {
        this.askPrice = askPrice;
    }

    public int getBidQuantity() {
        return bidQuantity;
    }

    public void setBidQuantity(int bidQuantity) {
        this.bidQuantity = bidQuantity;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public int getAskquantity() {
        return askquantity;
    }

    public void setAskquantity(int askquantity) {
        this.askquantity = askquantity;
    }

    public MowaziCompany getCompany() {
        return company;
    }

    public void setCompany(MowaziCompany company) {
        this.company = company;
    }

}
