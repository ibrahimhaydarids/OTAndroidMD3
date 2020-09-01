package com.ids.fixot.model;

/**
 * Created by user on 3/31/2017.
 */

public class Stock {

    private int topType;
    private String id, sectorID, symbolAr, symbolEn, amount, change, changePercent, last, trades, TradingSession, volume, instrumentId, MarketID;
    private String securityId;

    public Stock() {

    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getTradingSession() {
        return TradingSession;
    }

    public void setTradingSession(String tradingSession) {
        TradingSession = tradingSession;
    }

    public String getMarketID() {
        return MarketID;
    }

    public void setMarketID(String marketID) {
        MarketID = marketID;
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

    public String getSectorID() {
        return sectorID;
    }

    public void setSectorID(String sectorID) {
        this.sectorID = sectorID;
    }

    public String getSymbolAr() {
        return symbolAr;
    }

    public void setSymbolAr(String symbolAr) {
        this.symbolAr = symbolAr;
    }

    public String getSymbolEn() {
        return symbolEn;
    }

    public void setSymbolEn(String symbolEn) {
        this.symbolEn = symbolEn;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getTrades() {
        return trades;
    }

    public void setTrades(String trades) {
        this.trades = trades;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public int getTopType() {
        return topType;
    }

    public void setTopType(int topType) {
        this.topType = topType;
    }
}
