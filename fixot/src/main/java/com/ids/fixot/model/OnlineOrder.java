package com.ids.fixot.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 3/23/2017.
 */

public class OnlineOrder implements Parcelable {

    public static final Creator CREATOR
            = new Creator() {
        public OnlineOrder createFromParcel(Parcel in) {
            return new OnlineOrder(in);
        }

        public OnlineOrder[] newArray(int size) {
            return new OnlineOrder[size];
        }
    };
    private int ID, adminID, application, brokerEmployeeID, brokerID, durationID, forwardContractID;
    private int reference, userID, userTypeID, triggerPriceTypeID, triggerPriceDirectionID, tradingSessionID, tradeTypeID;
    private int tStamp, statusID, quantityExecuted, quantity, orderTypeID, operationTypeID,OrderStatusTypeID;
    private double averagePrice, oldPrice, price, triggerPrice;
    private String statusDescription, rejectionCause, orderDateTime, goodUntilDate, executedDateTime, tradeID, portfolioNumber;
    private String stockID, investorID, key, targetSubID, orderNumber, stockName, stockSymbol, InstrumentID;
    private boolean canDelete, canUpdate, isInvalidOrder, hasUsersRestricted, hasErrors;
    private List<ValueItem> allvalueItems = new ArrayList<ValueItem>();
    private String securityId;

    public OnlineOrder() {

    }

    // "De-parcel object
    public OnlineOrder(Parcel in) {

        ID = in.readInt();
        adminID = in.readInt();
        application = in.readInt();
        brokerEmployeeID = in.readInt();
        brokerID = in.readInt();
        durationID = in.readInt();
        forwardContractID = in.readInt();

        reference = in.readInt();
        userID = in.readInt();
        userTypeID = in.readInt();
        triggerPriceTypeID = in.readInt();
        triggerPriceDirectionID = in.readInt();
        tradingSessionID = in.readInt();
        tradeTypeID = in.readInt();

        tStamp = in.readInt();
        statusID = in.readInt();
        quantityExecuted = in.readInt();
        quantity = in.readInt();
        orderTypeID = in.readInt();
        OrderStatusTypeID = in.readInt();
        operationTypeID = in.readInt();

        averagePrice = in.readDouble();
        oldPrice = in.readDouble();
        price = in.readDouble();
        triggerPrice = in.readDouble();

        statusDescription = in.readString();
        rejectionCause = in.readString();
        orderDateTime = in.readString();
        goodUntilDate = in.readString();
        executedDateTime = in.readString();
        tradeID = in.readString();
        portfolioNumber = in.readString();

        stockID = in.readString();
        investorID = in.readString();
        key = in.readString();
        targetSubID = in.readString();
        orderNumber = in.readString();
        stockName = in.readString();
        stockSymbol = in.readString();
        InstrumentID = in.readString();

        canDelete = in.readByte() != 0;
        canUpdate = in.readByte() != 0;
        isInvalidOrder = in.readByte() != 0;
        hasUsersRestricted = in.readByte() != 0;
        hasErrors = in.readByte() != 0;
        in.readList(allvalueItems, getClass().getClassLoader());
    }

    public int getTradeTypeColor() {

        if (this.tradeTypeID == MyApplication.ORDER_BUY) {// shira2

            return R.color.green_color;

        } else if (this.tradeTypeID == MyApplication.ORDER_SELL)// bay3
            return R.color.red_color;

        return R.color.colorValues;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public List<ValueItem> getAllvalueItems() {
        return allvalueItems;
    }

    public void setAllvalueItems(ArrayList<ValueItem> allvalueItems) {
        this.allvalueItems = allvalueItems;
    }

    public void setAllvalueItems(List<ValueItem> allvalueItems) {
        this.allvalueItems = allvalueItems;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getInstrumentID() {
        return InstrumentID;
    }

    public void setInstrumentID(String instrumentID) {
        InstrumentID = instrumentID;
    }

    public int getId() {
        return ID;
    }

    public String getInvestorID() {
        return investorID;
    }

    public void setInvestorID(String investorID) {
        this.investorID = investorID;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public int getApplication() {
        return application;
    }

    public void setApplication(int application) {
        this.application = application;
    }

    public int getBrokerEmployeeID() {
        return brokerEmployeeID;
    }

    public void setBrokerEmployeeID(int brokerEmployeeID) {
        this.brokerEmployeeID = brokerEmployeeID;
    }

    public int getBrokerID() {
        return brokerID;
    }

    public void setBrokerID(int brokerID) {
        this.brokerID = brokerID;
    }

    public int getDurationID() {
        return durationID;
    }

    public void setDurationID(int durationID) {
        this.durationID = durationID;
    }

    public int getForwardContractID() {
        return forwardContractID;
    }

    public void setForwardContractID(int forwardContractID) {
        this.forwardContractID = forwardContractID;
    }

    public String getTargetSubID() {
        return targetSubID;
    }

    public void setTargetSubID(String targetSubID) {
        this.targetSubID = targetSubID;
    }

    public int getReference() {
        return reference;
    }

    public void setReference(int reference) {
        this.reference = reference;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserTypeID() {
        return userTypeID;
    }

    public void setUserTypeID(int userTypeID) {
        this.userTypeID = userTypeID;
    }

    public int getTriggerPriceTypeID() {
        return triggerPriceTypeID;
    }

    public void setTriggerPriceTypeID(int triggerPriceTypeID) {
        this.triggerPriceTypeID = triggerPriceTypeID;
    }

    public int getTriggerPriceDirectionID() {
        return triggerPriceDirectionID;
    }

    public void setTriggerPriceDirectionID(int triggerPriceDirectionID) {
        this.triggerPriceDirectionID = triggerPriceDirectionID;
    }

    public int getTradingSessionID() {
        return tradingSessionID;
    }

    public void setTradingSessionID(int tradingSessionID) {
        this.tradingSessionID = tradingSessionID;
    }

    public int getTradeTypeID() {
        return tradeTypeID;
    }

    public void setTradeTypeID(int tradeTypeID) {
        this.tradeTypeID = tradeTypeID;
    }

    public String getTradeID() {
        return tradeID;
    }

    public void setTradeID(String tradeID) {
        this.tradeID = tradeID;
    }

    public int gettStamp() {
        return tStamp;
    }

    public void settStamp(int tStamp) {
        this.tStamp = tStamp;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public int getQuantityExecuted() {
        return quantityExecuted;
    }

    public void setQuantityExecuted(int quantityExecuted) {
        this.quantityExecuted = quantityExecuted;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(String portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public int getOrderTypeID() {
        return orderTypeID;
    }

    public void setOrderTypeID(int orderTypeID) {
        this.orderTypeID = orderTypeID;
    }

    public int getOrderStatusTypeID() {
        return OrderStatusTypeID;
    }

    public void setOrderStatusTypeID(int orderStatusTypeID) {
        OrderStatusTypeID = orderStatusTypeID;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOperationTypeID() {
        return operationTypeID;
    }

    public void setOperationTypeID(int operationTypeID) {
        this.operationTypeID = operationTypeID;
    }

    public double getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(double averagePrice) {
        this.averagePrice = averagePrice;
    }

    public double getOldPrice() {
        return oldPrice;
    }

    public void setOldPrice(double oldPrice) {
        this.oldPrice = oldPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTriggerPrice() {
        return triggerPrice;
    }

    public void setTriggerPrice(double triggerPrice) {
        this.triggerPrice = triggerPrice;
    }

    public String getRejectionCause() {
        return rejectionCause;
    }

    public void setRejectionCause(String rejectionCause) {
        this.rejectionCause = rejectionCause;
    }

    public String getOrderDateTime() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime = orderDateTime;
    }

    public String getGoodUntilDate() {
        return goodUntilDate;
    }

    public void setGoodUntilDate(String goodUntilDate) {
        this.goodUntilDate = goodUntilDate;
    }

    public String getExecutedDateTime() {
        return executedDateTime;
    }

    public void setExecutedDateTime(String executedDateTime) {
        this.executedDateTime = executedDateTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isCanDelete() {
        return canDelete;
    }

    public void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }

    public boolean isCanUpdate() {
        return canUpdate;
    }

    public void setCanUpdate(boolean canUpdate) {
        this.canUpdate = canUpdate;
    }

    public boolean isInvalidOrder() {
        return isInvalidOrder;
    }

    public void setInvalidOrder(boolean invalidOrder) {
        isInvalidOrder = invalidOrder;
    }

    public boolean isHasUsersRestricted() {
        return hasUsersRestricted;
    }

    public void setHasUsersRestricted(boolean hasUsersRestricted) {
        this.hasUsersRestricted = hasUsersRestricted;
    }

    public boolean isHasErrors() {
        return hasErrors;
    }

    public void setHasErrors(boolean hasErrors) {
        this.hasErrors = hasErrors;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(ID);
        dest.writeInt(adminID);
        dest.writeInt(application);
        dest.writeInt(brokerEmployeeID);
        dest.writeInt(brokerID);
        dest.writeInt(durationID);
        dest.writeInt(forwardContractID);

        dest.writeInt(reference);
        dest.writeInt(userID);
        dest.writeInt(userTypeID);
        dest.writeInt(triggerPriceTypeID);
        dest.writeInt(triggerPriceDirectionID);
        dest.writeInt(tradingSessionID);
        dest.writeInt(tradeTypeID);

        dest.writeInt(tStamp);
        dest.writeInt(statusID);
        dest.writeInt(quantityExecuted);
        dest.writeInt(quantity);
        dest.writeInt(orderTypeID);
        dest.writeInt(OrderStatusTypeID);
        dest.writeInt(operationTypeID);

        dest.writeDouble(averagePrice);
        dest.writeDouble(oldPrice);
        dest.writeDouble(price);
        dest.writeDouble(triggerPrice);

        dest.writeString(statusDescription);
        dest.writeString(rejectionCause);
        dest.writeString(orderDateTime);
        dest.writeString(goodUntilDate);
        dest.writeString(executedDateTime);
        dest.writeString(tradeID);
        dest.writeString(portfolioNumber);

        dest.writeString(stockID);
        dest.writeString(investorID);
        dest.writeString(key);
        dest.writeString(targetSubID);
        dest.writeString(orderNumber);
        dest.writeString(stockName);
        dest.writeString(stockSymbol);
        dest.writeString(InstrumentID);

        dest.writeByte((byte) (canDelete ? 1 : 0));
        dest.writeByte((byte) (canUpdate ? 1 : 0));
        dest.writeByte((byte) (isInvalidOrder ? 1 : 0));
        dest.writeByte((byte) (hasUsersRestricted ? 1 : 0));
        dest.writeByte((byte) (hasErrors ? 1 : 0));
        dest.writeList(allvalueItems);

    }
}




