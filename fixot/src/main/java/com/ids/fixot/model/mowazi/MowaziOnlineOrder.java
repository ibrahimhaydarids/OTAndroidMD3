package com.ids.fixot.model.mowazi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOnlineOrder implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public MowaziOnlineOrder createFromParcel(Parcel in) {
            return new MowaziOnlineOrder(in);
        }

        public MowaziOnlineOrder[] newArray(int size) {
            return new MowaziOnlineOrder[size];
        }
    };
    String originalShares, executedShares, minimumSharesToBook;
    private int orderId, clientId, companyId, orderTpeId;
    private int orderStatusId, adminId;
    private String isDeleted;
    private String orderDate, expirationDate, activationDate, notes, dateStamp,
            goodUntilDate;
    private double price, sessionTs;
    private MowaziCompany company = new MowaziCompany();
    private MowaziClient client;

    public MowaziOnlineOrder() {

    }

    // "De-parcel object
    public MowaziOnlineOrder(Parcel in) {

        orderId = in.readInt();
        clientId = in.readInt();
        companyId = in.readInt();
        orderTpeId = in.readInt();
        orderStatusId = in.readInt();
        adminId = in.readInt();

        originalShares = in.readString();
        executedShares = in.readString();
        minimumSharesToBook = in.readString();
        isDeleted = in.readString();
        orderDate = in.readString();
        expirationDate = in.readString();
        activationDate = in.readString();
        notes = in.readString();
        dateStamp = in.readString();
        goodUntilDate = in.readString();

        price = in.readDouble();
        sessionTs = in.readDouble();
        company = in.readParcelable(getClass().getClassLoader());
        // descEn=in.readString();
        // descAr=in.readString();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getOriginalShares() {
        return originalShares;
    }

    public void setOriginalShares(String originalShares) {
        this.originalShares = originalShares;
    }

    public String getExecutedShares() {
        return executedShares;
    }

    public void setExecutedShares(String executedShares) {
        this.executedShares = executedShares;
    }

    public String getMinimumSharesToBook() {
        return minimumSharesToBook;
    }

    public void setMinimumSharesToBook(String minimumSharesToBook) {
        this.minimumSharesToBook = minimumSharesToBook;
    }

    public int getOrderTpeId() {
        return orderTpeId;
    }

    public void setOrderTpeId(int orderTpeId) {
        this.orderTpeId = orderTpeId;
    }

    public int getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(int orderStatusId) {
        this.orderStatusId = orderStatusId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public String getDeleted() {
        return isDeleted;
    }

    public void setDeleted(String deleted) {
        isDeleted = deleted;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(String activationDate) {
        this.activationDate = activationDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDateStamp() {
        return dateStamp;
    }

    public void setDateStamp(String dateStamp) {
        this.dateStamp = dateStamp;
    }

    public String getGoodUntilDate() {
        return goodUntilDate;
    }

    public void setGoodUntilDate(String goodUntilDate) {
        this.goodUntilDate = goodUntilDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getSessionTs() {
        return sessionTs;
    }

    public void setSessionTs(double sessionTs) {
        this.sessionTs = sessionTs;
    }

    public MowaziCompany getCompany() {
        return company;
    }

    public void setCompany(MowaziCompany company) {
        this.company = company;
    }

    public MowaziClient getClient() {
        return client;
    }

    public void setClient(MowaziClient client) {
        this.client = client;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(orderId);
        dest.writeInt(clientId);
        dest.writeInt(companyId);
        dest.writeInt(orderTpeId);
        dest.writeInt(orderStatusId);
        dest.writeInt(adminId);

        dest.writeString(originalShares);
        dest.writeString(executedShares);
        dest.writeString(minimumSharesToBook);
        dest.writeString(isDeleted);
        dest.writeString(orderDate);
        dest.writeString(expirationDate);
        dest.writeString(activationDate);
        dest.writeString(notes);
        dest.writeString(dateStamp);
        dest.writeString(goodUntilDate);

        dest.writeDouble(price);
        dest.writeDouble(sessionTs);
        dest.writeParcelable(company, flags);
        // dest.writeString(company); //company object
        // dest.writeString(client); //client object
    }
}
