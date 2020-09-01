package com.ids.fixot.model;

import com.ids.fixot.R;

/**
 * Created by Amal on 3/23/2017.
 */

public class TimeSale {

    private int ChangeIndicator, StockID, orderType;
    private String id, StockSymbolAr, StockSymbolEn, TradeTime, Change, Quantity, Price, orderTypeId, instrumentId;
    private String securityId;


    public TimeSale() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public String getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(String orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public String getChange() {
        return Change;
    }

    public void setChange(String change) {
        Change = change;
    }

    public int getChangeIndicator() {
        return ChangeIndicator;
    }

    public void setChangeIndicator(int changeIndicator) {
        ChangeIndicator = changeIndicator;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public int getStockID() {
        return StockID;
    }

    public void setStockID(int stockID) {
        StockID = stockID;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getStockSymbolAr() {
        return StockSymbolAr;
    }

    public void setStockSymbolAr(String stockSymbolAr) {
        StockSymbolAr = stockSymbolAr;
    }

    public String getStockSymbolEn() {
        return StockSymbolEn;
    }

    public void setStockSymbolEn(String stockSymbolEn) {
        StockSymbolEn = stockSymbolEn;
    }

    public String getTradeTime() {
        return TradeTime;
    }

    public void setTradeTime(String tradeTime) {
        TradeTime = tradeTime;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public int getIntegerQuantity() {

        return Integer.parseInt(this.getQuantity().replace(",", ""));
    }

    public int getArrow() {

        /*if (this.getChange().equals("0.0") || this.getChange().equals("0"))
            return R.drawable.neutral_updown_arrow;
        else if (this.getChange().contains("-") || this.getChange().contains("("))
            return R.drawable.down_red_arrow;
        else
            return R.drawable.up_green_arrow;*/

        return this.getChangeIndicator() == 1 ? R.drawable.up_green_arrow : R.drawable.down_red_arrow;
    }
}
