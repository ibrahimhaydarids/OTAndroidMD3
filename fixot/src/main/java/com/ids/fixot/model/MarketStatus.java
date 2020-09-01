package com.ids.fixot.model;

import com.ids.fixot.MyApplication;

/**
 * Created by Amal on 3/13/2017.
 */

public class MarketStatus {

    private int statusID;
    private String serverTime, StatusDescriptionAr, StatusDescriptionEn, StatusName, MessageAr, MessageEn, marketTime = "";
    private boolean sessionChanged;

    public MarketStatus() {
    }

    public String getMarketTime() {
        return marketTime;
    }

    public void setMarketTime(String marketTime) {
        this.marketTime = marketTime;
    }

    public boolean isSessionChanged() {
        return sessionChanged;
    }

    public void setSessionChanged(boolean sessionChanged) {
        this.sessionChanged = sessionChanged;
    }

    public String getMessageAr() {
        return MessageAr;
    }

    public void setMessageAr(String messageAr) {
        MessageAr = messageAr;
    }

    public String getMessageEn() {
        return MessageEn;
    }

    public void setMessageEn(String messageEn) {
        MessageEn = messageEn;
    }

    public int getStatusID() {
        return statusID;
    }

    public void setStatusID(int statusID) {
        this.statusID = statusID;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    public String getStatusDescriptionAr() {
        return StatusDescriptionAr;
    }

    public void setStatusDescriptionAr(String statusDescriptionAr) {
        StatusDescriptionAr = statusDescriptionAr;
    }

    public String getStatusDescription() {

        return MyApplication.lang == MyApplication.ARABIC ? MyApplication.marketStatus.getStatusDescriptionAr() : MyApplication.marketStatus.getStatusDescriptionEn();
    }

    public String getStatusDescriptionEn() {
        return StatusDescriptionEn;
    }

    public void setStatusDescriptionEn(String statusDescriptionEn) {
        StatusDescriptionEn = statusDescriptionEn;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }
}
