package com.ids.fixot.model;

import java.util.ArrayList;

/**
 * Created by Amal on 3/9/2017.
 */

public class User {

    private int id;
    private String CurrencyNameAr, CurrencyNameEn;
    private boolean hasWeeklyCheck, AllowPlacement;

    private String InternetBankNameAr, InternetBankNameEn, username;
    private int investorId;
    private boolean isRecycledAccount;
    private boolean isResetPassword;
    private boolean isTradingPasswordMandatory;
    private String NameEn, NameAr;
    private int permissionId;
    private int portfolioNumber;
    private String preferableBankNameAr, getPreferableBankNameEn;
    private String SessionID;
    private int userTypeID;
    private String key;
    private String MessageAr, MessageEn;
    private int PortfolioId;
    private int Status;

    private ArrayList<SubAccount> subAccounts = new ArrayList<>();

    public User() {
        this.id = -1;
    }

    public boolean isTradingPasswordMandatory() {
        return isTradingPasswordMandatory;
    }

    public void setTradingPasswordMandatory(boolean tradingPasswordMandatory) {
        isTradingPasswordMandatory = tradingPasswordMandatory;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPortfolioId() {
        return PortfolioId;
    }

    public void setPortfolioId(int portfolioId) {
        PortfolioId = portfolioId;
    }

    public String getMessageEn() {
        return MessageEn;
    }

    public void setMessageEn(String successEn) {
        this.MessageEn = successEn;
    }

    public String getMessageAr() {
        return MessageAr;
    }

    public void setMessageAr(String messageAr) {
        MessageAr = messageAr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurrencyNameAr() {
        return CurrencyNameAr;
    }

    public void setCurrencyNameAr(String currencyNameAr) {
        CurrencyNameAr = currencyNameAr;
    }

    public String getCurrencyNameEn() {
        return CurrencyNameEn;
    }

    public void setCurrencyNameEn(String currencyNameEn) {
        CurrencyNameEn = currencyNameEn;
    }

    public boolean isHasWeeklyCheck() {
        return hasWeeklyCheck;
    }

    public void setHasWeeklyCheck(boolean hasWeeklyCheck) {
        this.hasWeeklyCheck = hasWeeklyCheck;
    }

    public boolean isAllowPlacement() {
        return AllowPlacement;
    }

    public void setAllowPlacement(boolean allowPlacement) {
        AllowPlacement = allowPlacement;
    }

    public String getInternetBankNameAr() {
        return InternetBankNameAr;
    }

    public void setInternetBankNameAr(String internetBankNameAr) {
        InternetBankNameAr = internetBankNameAr;
    }

    public String getInternetBankNameEn() {
        return InternetBankNameEn;
    }

    public void setInternetBankNameEn(String internetBankNameEn) {
        InternetBankNameEn = internetBankNameEn;
    }

    public int getInvestorId() {
        return investorId;
    }

    public void setInvestorId(int investorId) {
        this.investorId = investorId;
    }

    public boolean isRecycledAccount() {
        return isRecycledAccount;
    }

    public void setRecycledAccount(boolean recycledAccount) {
        isRecycledAccount = recycledAccount;
    }

    public boolean isResetPassword() {
        return isResetPassword;
    }

    public void setResetPassword(boolean resetPassword) {
        isResetPassword = resetPassword;
    }

    public String getNameEn() {
        return NameEn;
    }

    public void setNameEn(String nameEn) {
        NameEn = nameEn;
    }

    public String getNameAr() {
        return NameAr;
    }

    public void setNameAr(String nameAr) {
        NameAr = nameAr;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public int getPortfolioNumber() {
        return portfolioNumber;
    }

    public void setPortfolioNumber(int portfolioNumber) {
        this.portfolioNumber = portfolioNumber;
    }

    public String getPreferableBankNameAr() {
        return preferableBankNameAr;
    }

    public void setPreferableBankNameAr(String preferableBankNameAr) {
        this.preferableBankNameAr = preferableBankNameAr;
    }

    public String getGetPreferableBankNameEn() {
        return getPreferableBankNameEn;
    }

    public void setGetPreferableBankNameEn(String getPreferableBankNameEn) {
        this.getPreferableBankNameEn = getPreferableBankNameEn;
    }

    public String getSessionID() {
        return SessionID;
    }

    public void setSessionID(String sessionID) {
        SessionID = sessionID;
    }

    public int getUserTypeID() {
        return userTypeID;
    }

    public void setUserTypeID(int userTypeID) {
        this.userTypeID = userTypeID;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ArrayList<SubAccount> getSubAccounts() {
        return subAccounts;
    }

    public void setSubAccounts(ArrayList<SubAccount> subAccounts) {
        this.subAccounts = subAccounts;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
