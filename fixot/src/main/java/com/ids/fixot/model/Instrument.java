package com.ids.fixot.model;

import com.ids.fixot.MyApplication;

/**
 * Created by DEV on 3/5/2018.
 */

public class Instrument {

    private String id, instrumentNameAr, instrumentNameEn, instrumentState, instrumentCode, instrumentSymbol, securityClassAr, securityClassEn;
    private int instrumentPic, marketSegmentID, marketID;
    private Boolean isSelected = false;


    public Instrument() {

    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean bool) {
        this.isSelected = bool;
    }


    public int getMarketID() {
        return marketID;
    }

    public void setMarketID(int marketID) {
        this.marketID = marketID;
    }

    public int getInstrumentPic() {
        return instrumentPic;
    }

    public void setInstrumentPic(int instrumentPic) {
        this.instrumentPic = instrumentPic;
    }

    public int getMarketSegmentID() {
        return marketSegmentID;
    }

    public void setMarketSegmentID(int marketSegmentID) {
        this.marketSegmentID = marketSegmentID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getInstrumentState() {
        return instrumentState;
    }

    public void setInstrumentState(String instrumentState) {
        this.instrumentState = instrumentState;
    }

    public String getInstrumentSymbol() {
        return instrumentSymbol;
    }

    public void setInstrumentSymbol(String instrumentSymbol) {
        this.instrumentSymbol = instrumentSymbol;
    }

    public String getSecurityClassAr() {
        return securityClassAr;
    }

    public void setSecurityClassAr(String securityClassAr) {
        this.securityClassAr = securityClassAr;
    }

    public String getSecurityClassEn() {
        return securityClassEn;
    }

    public void setSecurityClassEn(String securityClassEn) {
        this.securityClassEn = securityClassEn;
    }

    public String getInstrumentCode() {
        return instrumentCode;
    }

    public void setInstrumentCode(String instrumentCode) {
        this.instrumentCode = instrumentCode;
    }

    public String getInstrumentName() {
        return (MyApplication.lang == MyApplication.ARABIC ? instrumentNameAr : instrumentNameEn);
    }

}
