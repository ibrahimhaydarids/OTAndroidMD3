package com.ids.fixot.model;

/**
 * Created by Amal on 3/23/2017.
 */

public class StockOrderBook {

    private int id,TradeType;
    private double askValue, bidValue;
    private String ask, bid, Count, Quantity, stockid, AskQuantity, BidQuantity, Price,OrderTime;
    private String securityId;


    public StockOrderBook() {
    }

    public int getTradeType() {
        return TradeType;
    }

    public void setTradeType(int TradeType) {
        this.TradeType = TradeType;
    }

    public double getAskValue() {
        return askValue;
    }

    public void setAskValue(double askValue) {
        this.askValue = askValue;
    }

    public double getBidValue() {
        return bidValue;
    }

    public void setBidValue(double bidValue) {
        this.bidValue = bidValue;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAsk() {
        return ask;
    }

    public void setAsk(String ask) {
        this.ask = ask;
    }

    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getCount() {
        return Count;
    }

    public void setCount(String count) {
        Count = count;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getStockid() {
        return stockid;
    }

    public void setStockid(String stockid) {
        this.stockid = stockid;
    }

    public String getAskQuantity() {
        return AskQuantity;
    }

    public void setAskQuantity(String askQuantity) {
        AskQuantity = askQuantity;
    }

    public String getBidQuantity() {
        return BidQuantity;
    }

    public void setBidQuantity(String bidQuantity) {
        BidQuantity = bidQuantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }
}
