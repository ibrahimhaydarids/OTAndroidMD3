package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amal on 3/21/2017.
 */

public class StockQuotation implements Parcelable {


    public static final Creator<StockQuotation> CREATOR = new Creator<StockQuotation>() {
        @Override
        public StockQuotation createFromParcel(Parcel in) {
            return new StockQuotation(in);
        }

        @Override
        public StockQuotation[] newArray(int size) {
            return new StockQuotation[size];
        }
    };
    private int Amount;
    private int stockID;
    private boolean isIslamic;
    private double last, ask, bid, HiLimit, lowlimit, tickDirection, previousClosing, referencePrice;
    private int highlimit, normalMarketSize;
    private boolean changed;
    private boolean isFavorite;
    private String orderType, durationType;
    private String nameAr, nameEn, symbolAr, symbolEn, change, changePercent, value;
    private String instrumentId, marketCapital, numberOfOrders, sessionNameEn, sessionNameAr, sessionId = "", nms, instrumentNameAr, instrumentNameEn, sectorID, tradeSettlementDate;
    private int high, low, open, stockTradingStatus, trade, volume, volumeAsk, volumeBid, marketId;
    private String securityId;

    public StockQuotation() {
    }

    protected StockQuotation(Parcel in) {
        Amount = in.readInt();
        stockID = in.readInt();
        isIslamic = in.readByte() != 0;
        last = in.readDouble();
        ask = in.readDouble();
        bid = in.readDouble();
        HiLimit = in.readDouble();
        lowlimit = in.readDouble();
        tickDirection = in.readDouble();
        previousClosing = in.readDouble();
        highlimit = in.readInt();
        changed = in.readByte() != 0;
        isFavorite = in.readByte() != 0;
        nameAr = in.readString();
        nameEn = in.readString();
        symbolAr = in.readString();
        symbolEn = in.readString();
        change = in.readString();
        changePercent = in.readString();
        value = in.readString();

        instrumentId = in.readString();
        marketCapital = in.readString();
        numberOfOrders = in.readString();
        sessionNameEn = in.readString();
        sessionNameAr = in.readString();
        sessionId = in.readString();
        nms = in.readString();
        instrumentNameAr = in.readString();
        instrumentNameEn = in.readString();
        sectorID = in.readString();
        tradeSettlementDate = in.readString();

        high = in.readInt();
        low = in.readInt();
        open = in.readInt();
        stockTradingStatus = in.readInt();
        trade = in.readInt();
        volume = in.readInt();
        volumeAsk = in.readInt();
        volumeBid = in.readInt();
        orderType = in.readString();
        durationType = in.readString();
        securityId = in.readString();
    }

    public double getReferencePrice() {
        return referencePrice;
    }

    public void setReferencePrice(double referencePrice) {
        this.referencePrice = referencePrice;
    }

    public int getMarketId() {
        return marketId;
    }

    public void setMarketId(int marketId) {
        this.marketId = marketId;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public int getNormalMarketSize() {
        return normalMarketSize;
    }

    public void setNormalMarketSize(int normalMarketSize) {
        this.normalMarketSize = normalMarketSize;
    }

    public String getTradeSettlementDate() {
        return tradeSettlementDate;
    }

    public void setTradeSettlementDate(String tradeSettlementDate) {
        this.tradeSettlementDate = tradeSettlementDate;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getDurationType() {
        return durationType;
    }

    public void setDurationType(String durationType) {
        this.durationType = durationType;
    }

    public boolean isChanged() {
        return changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
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

    public boolean islamic() {
        return isIslamic;
    }

    public void setIslamic(boolean islamic) {
        isIslamic = islamic;
    }

    public double getHiLimit() {
        return HiLimit;
    }

    public void setHiLimit(double hiLimit) {
        HiLimit = hiLimit;
    }

    public double getTickDirection() {
        return tickDirection;
    }

    public void setTickDirection(double tickDirection) {
        this.tickDirection = tickDirection;
    }

    public int getAmount() {
        return Amount;
    }

    public void setAmount(int amount) {
        Amount = amount;
    }

    public int getStockID() {
        return stockID;
    }

    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
    }

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public int getHighlimit() {
        return highlimit;
    }

    public void setHighlimit(int highlimit) {
        this.highlimit = highlimit;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

//    public void setHiLimit(int hiLimit) {
//        HiLimit = hiLimit;
//    }

//    public void setLowlimit(int lowlimit) {
//        this.lowlimit = lowlimit;
//    }

    public double getLowlimit() {
        return lowlimit;
    }

    public void setLowlimit(double lowlimit) {
        this.lowlimit = lowlimit;
    }

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public double getPreviousClosing() {
        return previousClosing;
    }

    public void setPreviousClosing(double previousClosing) {
        this.previousClosing = previousClosing;
    }

    public int getStockTradingStatus() {
        return stockTradingStatus;
    }

    public void setStockTradingStatus(int stockTradingStatus) {
        this.stockTradingStatus = stockTradingStatus;
    }

    public int getTrade() {
        return trade;
    }

    public void setTrade(int trade) {
        this.trade = trade;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getVolumeAsk() {
        return volumeAsk;
    }

    public void setVolumeAsk(int volumeAsk) {
        this.volumeAsk = volumeAsk;
    }

    public int getVolumeBid() {
        return volumeBid;
    }

    public void setVolumeBid(int volumeBid) {
        this.volumeBid = volumeBid;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public String getMarketCapital() {
        return marketCapital;
    }

    public void setMarketCapital(String marketCapital) {
        this.marketCapital = marketCapital;
    }

    public String getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(String numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public String getSessionNameEn() {
        return sessionNameEn;
    }

    public void setSessionNameEn(String sessionNameEn) {
        this.sessionNameEn = sessionNameEn;
    }

    public String getSessionNameAr() {
        return sessionNameAr;
    }

    public void setSessionNameAr(String sessionNameAr) {
        this.sessionNameAr = sessionNameAr;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getNms() {
        return nms;
    }

    public void setNms(String nms) {
        this.nms = nms;
    }

    public String getInstrumentNameAr() {
        return instrumentNameAr;
    }

    public void setInstrumentNameAr(String instrumentNameAr) {
        this.instrumentNameAr = instrumentNameAr;
    }

    public String getInstrumentNameEn() {
        return instrumentNameEn;
    }

    public void setInstrumentNameEn(String instrumentNameEn) {
        this.instrumentNameEn = instrumentNameEn;
    }

    public String getSectorID() {
        return sectorID;
    }

    public void setSectorID(String sectorID) {
        this.sectorID = sectorID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(Amount);
        dest.writeInt(stockID);
        dest.writeByte((byte) (isIslamic ? 1 : 0));

        dest.writeDouble(last);
        dest.writeDouble(ask);
        dest.writeDouble(bid);
        dest.writeDouble(HiLimit);
        dest.writeDouble(lowlimit);
        dest.writeDouble(tickDirection);
        dest.writeDouble(previousClosing);
        dest.writeInt(highlimit);
        dest.writeByte((byte) (changed ? 1 : 0));
        dest.writeByte((byte) (isFavorite ? 1 : 0));
        dest.writeString(nameAr);
        dest.writeString(nameEn);
        dest.writeString(symbolAr);
        dest.writeString(symbolEn);
        dest.writeString(change);
        dest.writeString(changePercent);
        dest.writeString(value);
        dest.writeString(instrumentId);
        dest.writeString(marketCapital);
        dest.writeString(numberOfOrders);
        dest.writeString(sessionNameEn);
        dest.writeString(sessionNameAr);
        dest.writeString(sessionId);
        dest.writeString(nms);
        dest.writeString(instrumentNameAr);
        dest.writeString(instrumentNameEn);
        dest.writeString(sectorID);
        dest.writeString(tradeSettlementDate);

        dest.writeInt(high);
        dest.writeInt(low);
        dest.writeInt(open);
        dest.writeInt(stockTradingStatus);
        dest.writeInt(trade);
        dest.writeInt(volume);
        dest.writeInt(volumeAsk);
        dest.writeInt(volumeBid);
        dest.writeString(orderType);
        dest.writeString(durationType);
        dest.writeString(securityId);
    }
}
