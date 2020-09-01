package com.ids.fixot.model;

/**
 * Created by user on 3/21/2017.
 */

public class ForwardContract {

    private int id, stockId, contractNo, originalQty, remainingQty;
    private double breakPrice, costPrice, debit;
    private String contactDate, dueDate, stockSymbolAr, stockSymbolEn;

    public ForwardContract() {

    }

    public String getStockSymbolAr() {
        return stockSymbolAr;
    }

    public void setStockSymbolAr(String stockSymbolAr) {
        this.stockSymbolAr = stockSymbolAr;
    }

    public String getStockSymbolEn() {
        return stockSymbolEn;
    }

    public void setStockSymbolEn(String stockSymbolEn) {
        this.stockSymbolEn = stockSymbolEn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getContractNo() {
        return contractNo;
    }

    public void setContractNo(int contractNo) {
        this.contractNo = contractNo;
    }

    public int getOriginalQty() {
        return originalQty;
    }

    public void setOriginalQty(int originalQty) {
        this.originalQty = originalQty;
    }

    public int getRemainingQty() {
        return remainingQty;
    }

    public void setRemainingQty(int remainingQty) {
        this.remainingQty = remainingQty;
    }

    public double getBreakPrice() {
        return breakPrice;
    }

    public void setBreakPrice(double breakPrice) {
        this.breakPrice = breakPrice;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public String getContactDate() {
        return contactDate;
    }

    public void setContactDate(String contactDate) {
        this.contactDate = contactDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "ForwardContract{" +
                "id=" + id +
                ", stockId=" + stockId +
                ", contractNo=" + contractNo +
                ", originalQty=" + originalQty +
                ", remainingQty=" + remainingQty +
                ", breakPrice=" + breakPrice +
                ", costPrice=" + costPrice +
                ", debit=" + debit +
                ", contactDate='" + contactDate + '\'' +
                ", dueDate='" + dueDate + '\'' + '\n' +
                '}';
    }
}
