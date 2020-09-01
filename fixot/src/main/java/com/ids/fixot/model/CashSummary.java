package com.ids.fixot.model;

import java.util.ArrayList;

/**
 * Created by user on 3/21/2017.
 */

public class CashSummary {

    private int id;
    private String bankAccountBalance, pendingPurchaseOrders;
    private boolean allowBanks;
    private String limit, purchasePower, credit, debit, availableForWithdrawal, amountUnderIssue;
    private ArrayList<ValueItem> allvalueItems = new ArrayList<ValueItem>();

    public CashSummary() {

    }

    public ArrayList<ValueItem> getAllvalueItems() {
        return allvalueItems;
    }

    public void setAllvalueItems(ArrayList<ValueItem> allvalueItems) {
        this.allvalueItems = allvalueItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBankAccountBalance() {
        return bankAccountBalance;
    }

    public void setBankAccountBalance(String bankAccountBalance) {
        this.bankAccountBalance = bankAccountBalance;
    }

    public String getCredit() {
        return credit;
    }

    public void setCredit(String credit) {
        this.credit = credit;
    }

    public String getDebit() {
        return debit;
    }

    public void setDebit(String debit) {
        this.debit = debit;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getPendingPurchaseOrders() {
        return pendingPurchaseOrders;
    }

    public void setPendingPurchaseOrders(String pendingPurchaseOrders) {
        this.pendingPurchaseOrders = pendingPurchaseOrders;
    }

    public String getPurchasePower() {
        return purchasePower;
    }

    public void setPurchasePower(String purchasePower) {
        this.purchasePower = purchasePower;
    }

    public boolean isAllowBanks() {
        return allowBanks;
    }

    public void setAllowBanks(boolean allowBanks) {
        this.allowBanks = allowBanks;
    }

    public String getAvailableForWithdrawal() {
        return availableForWithdrawal;
    }

    public void setAvailableForWithdrawal(String availableForWithdrawal) {
        this.availableForWithdrawal = availableForWithdrawal;
    }

    public String getAmountUnderIssue() {
        return amountUnderIssue;
    }

    public void setAmountUnderIssue(String amountUnderIssue) {
        this.amountUnderIssue = amountUnderIssue;
    }

    @Override
    public String toString() {
        return "CashSummary{" +
                "id=" + id +
                ", bankAccountBalance=" + bankAccountBalance +
                ", credit=" + credit +
                ", debit=" + debit +
                ", limit=" + limit +
                ", pendingPurchaseOrders=" + pendingPurchaseOrders +
                ", purchasePower=" + purchasePower +
                ", allowBanks=" + allowBanks +
                '}';
    }
}
