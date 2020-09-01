package com.ids.fixot.model;

import java.util.ArrayList;

/**
 * Created by user on 3/21/2017.
 */

public class Portfolio {

    private int id;
    private String messageEn, messageAr, lastStatementFilePath;
    private CashSummary cashSummary;
    //private ArrayList<ForwardContract> allForwardContracts;
    private ArrayList<StockSummary> allStockSummaries;

    public Portfolio() {

        cashSummary = new CashSummary();
        allStockSummaries = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public void setMessageEn(String messageEn) {
        this.messageEn = messageEn;
    }

    public String getMessageAr() {
        return messageAr;
    }

    public void setMessageAr(String messageAr) {
        this.messageAr = messageAr;
    }

    public CashSummary getCashSummary() {
        return cashSummary;
    }

    public void setCashSummary(CashSummary cashSummary) {
        this.cashSummary = cashSummary;
    }

    public ArrayList<StockSummary> getAllStockSummaries() {
        return allStockSummaries;
    }

    public void setAllStockSummaries(ArrayList<StockSummary> allStockSummaries) {
        this.allStockSummaries = allStockSummaries;
    }

    public String getLastStatementFilePath() {
        return lastStatementFilePath;
    }

    public void setLastStatementFilePath(String lastStatementFilePath) {
        this.lastStatementFilePath = lastStatementFilePath;
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "id=" + id +
                ", messageEn='" + messageEn + '\n' +
                ", messageAr='" + messageAr + '\n' +
                ", cashSummary=" + cashSummary + '\n' +
                ", allStockSummaries=" + allStockSummaries +
                '}';
    }
}
