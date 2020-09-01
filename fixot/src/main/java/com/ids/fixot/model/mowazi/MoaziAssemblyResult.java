package com.ids.fixot.model.mowazi;

/**
 * Created by dev on 6/15/2016.
 */

public class MoaziAssemblyResult {

    private String year,changeFrom,changeTo,winLossPastYear,winLossCurrentYear;
    private String distributionCashPastYear,distributionDonationPastYear,distributionPremiumPastYear,distributionCashCurrentYear,distributionDonationCurrentYear,distributionPremiumCurrentYear;
    private double shareProfitPastYear,shareProfitCurrentYear;
    private String notes,capital;
    private MoaziCommunity community;
    private int id,communityId;

    public MoaziAssemblyResult(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getCommunityId() {
        return communityId;
    }

    public void setCommunityId(int communityId) {
        this.communityId = communityId;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getChangeFrom() {
        return changeFrom;
    }

    public void setChangeFrom(String changeFrom) {
        this.changeFrom = changeFrom;
    }

    public String getChangeTo() {
        return changeTo;
    }

    public void setChangeTo(String changeTo) {
        this.changeTo = changeTo;
    }

    public String getWinLossPastYear() {
        return winLossPastYear;
    }

    public void setWinLossPastYear(String winLossPastYear) {
        this.winLossPastYear = winLossPastYear;
    }

    public String getWinLossCurrentYear() {
        return winLossCurrentYear;
    }

    public void setWinLossCurrentYear(String winLossCurrentYear) {
        this.winLossCurrentYear = winLossCurrentYear;
    }

    public String getDistributionCashPastYear() {
        return distributionCashPastYear;
    }

    public void setDistributionCashPastYear(String distributionCashPastYear) {
        this.distributionCashPastYear = distributionCashPastYear;
    }

    public String getDistributionDonationPastYear() {
        return distributionDonationPastYear;
    }

    public void setDistributionDonationPastYear(String distributionDonationPastYear) {
        this.distributionDonationPastYear = distributionDonationPastYear;
    }

    public String getDistributionPremiumPastYear() {
        return distributionPremiumPastYear;
    }

    public void setDistributionPremiumPastYear(String distributionPremiumPastYear) {
        this.distributionPremiumPastYear = distributionPremiumPastYear;
    }

    public String getDistributionCashCurrentYear() {
        return distributionCashCurrentYear;
    }

    public void setDistributionCashCurrentYear(String distributionCashCurrentYear) {
        this.distributionCashCurrentYear = distributionCashCurrentYear;
    }

    public String getDistributionDonationCurrentYear() {
        return distributionDonationCurrentYear;
    }

    public void setDistributionDonationCurrentYear(String distributionDonationCurrentYear) {
        this.distributionDonationCurrentYear = distributionDonationCurrentYear;
    }

    public String getDistributionPremiumCurrentYear() {
        return distributionPremiumCurrentYear;
    }

    public void setDistributionPremiumCurrentYear(String distributionPremiumCurrentYear) {
        this.distributionPremiumCurrentYear = distributionPremiumCurrentYear;
    }

    public double getShareProfitPastYear() {
        return shareProfitPastYear;
    }

    public void setShareProfitPastYear(double shareProfitPastYear) {
        this.shareProfitPastYear = shareProfitPastYear;
    }

    public double getShareProfitCurrentYear() {
        return shareProfitCurrentYear;
    }

    public void setShareProfitCurrentYear(double shareProfitCurrentYear) {
        this.shareProfitCurrentYear = shareProfitCurrentYear;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public MoaziCommunity getCommunity() {
        return community;
    }

    public void setCommunity(MoaziCommunity community) {
        this.community = community;
    }
}
