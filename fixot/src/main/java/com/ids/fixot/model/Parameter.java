package com.ids.fixot.model;

/**
 * Created by user on 3/23/2017.
 */

public class Parameter {

    private int id, MaximumPasswordLength, MinimumPasswordLength, DefaultDMABrokerEmployeeID, broker_ID;
    private String messageEn, messageAr, serverVersionNumber, status, mowaziBrokerId, mowaziServiceLink, defaultPriceOnTrade;
    private String ContactUsUrl,forgotPasswordUrl, forgotUsernameUrl, unlockUserUrl, clientRegistrationUrl, facebookLink, rssLink, twitterLink, youTubeLink, AlmowaziRegistrationLink, AlmowaziPolicyLink;
    private boolean forceUpdate, renewPasswordHashing, tradeOnlyIslamicStocks, mowaziCompanyDetail, enableMowazi, ComplexPasswordEnabled, CanUserManageTraderOrder, EnableOTC;

    public Parameter() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDefaultDMABrokerEmployeeID() {
        return DefaultDMABrokerEmployeeID;
    }

    public void setDefaultDMABrokerEmployeeID(int DefaultDMABrokerEmployeeID) {
        this.DefaultDMABrokerEmployeeID = DefaultDMABrokerEmployeeID;
    }

    public int getMaximumPasswordLength() {
        return MaximumPasswordLength;
    }

    public void setMaximumPasswordLength(int MaximumPasswordLength) {
        this.MaximumPasswordLength = MaximumPasswordLength;
    }

    public int getMinimumPasswordLength() {
        return MinimumPasswordLength;
    }

    public void setMinimumPasswordLength(int MinimumPasswordLength) {
        this.MinimumPasswordLength = MinimumPasswordLength;
    }

    public String getContactUsUrl() {
        return ContactUsUrl;
    }

    public void setContactUsUrl(String contactUsUrl) {
        ContactUsUrl = contactUsUrl;
    }

    public boolean isComplexPasswordEnabled() {
        return ComplexPasswordEnabled;
    }

    public void setComplexPasswordEnabled(boolean ComplexPasswordEnabled) {
        this.ComplexPasswordEnabled = ComplexPasswordEnabled;
    }

    public boolean isCanUserManageTraderOrder() {
        return CanUserManageTraderOrder;
    }

    public void setCanUserManageTraderOrder(boolean CanUserManageTraderOrder) {
        this.CanUserManageTraderOrder = CanUserManageTraderOrder;
    }

    public String getDefaultPriceOnTrade() {
        return defaultPriceOnTrade;
    }

    public void setDefaultPriceOnTrade(String defaultPriceOnTrade) {
        this.defaultPriceOnTrade = defaultPriceOnTrade;
    }

    public String getServerVersionNumber() {
        return serverVersionNumber;
    }

    public void setServerVersionNumber(String serverVersionNumber) {
        this.serverVersionNumber = serverVersionNumber;
    }

    public String getMowaziBrokerId() {
        return mowaziBrokerId;
    }

    public void setMowaziBrokerId(String mowaziBrokerId) {
        this.mowaziBrokerId = mowaziBrokerId;
    }

    public String getMowaziServiceLink() {
        return mowaziServiceLink;
    }

    public void setMowaziServiceLink(String mowaziServiceLink) {
        this.mowaziServiceLink = mowaziServiceLink;
    }

    public boolean isEnableMowazi() {
        return enableMowazi;
        //return true;
    }

    public void setEnableMowazi(boolean enableMowazi) {
        this.enableMowazi = enableMowazi;
    }

    public boolean isMowaziCompanyDetail() {
        return mowaziCompanyDetail;
    }

    public void setMowaziCompanyDetail(boolean mowaziCompanyDetail) {
        this.mowaziCompanyDetail = mowaziCompanyDetail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public boolean isForceUpdate() {
        return forceUpdate;
    }

    public void setForceUpdate(boolean forceUpdate) {
        this.forceUpdate = forceUpdate;
    }

    public boolean isRenewPasswordHashing() {
        return renewPasswordHashing;
    }

    public void setRenewPasswordHashing(boolean renewPasswordHashing) {
        this.renewPasswordHashing = renewPasswordHashing;
    }

    public boolean isTradeOnlyIslamicStocks() {
        return tradeOnlyIslamicStocks;
    }

    public void setTradeOnlyIslamicStocks(boolean tradeOnlyIslamicStocks) {
        this.tradeOnlyIslamicStocks = tradeOnlyIslamicStocks;
    }

    public String getForgotPasswordUrl() {
        return forgotPasswordUrl;
    }

    public void setForgotPasswordUrl(String forgotPasswordUrl) {
        this.forgotPasswordUrl = forgotPasswordUrl;
    }


    public String getAlmowaziRegistrationLink() {
        return AlmowaziRegistrationLink;
    }

    public void setAlmowaziRegistrationLink(String almowaziRegistrationLink) {
        AlmowaziRegistrationLink = almowaziRegistrationLink;
    }

    public String getAlmowaziPolicyLink() {
        return AlmowaziPolicyLink;
    }

    public void setAlmowaziPolicyLink(String almowaziPolicyLink) {
        AlmowaziPolicyLink = almowaziPolicyLink;
    }

    public boolean isEnableOTC() {
        return EnableOTC;
    }

    public void setEnableOTC(boolean enableOTC) {
        EnableOTC = enableOTC;
    }

    public int getBroker_ID() {
        return broker_ID;
    }

    public void setBroker_ID(int broker_ID) {
        this.broker_ID = broker_ID;
    }

    public String getForgotUsernameUrl() {
        return forgotUsernameUrl;
    }

    public void setForgotUsernameUrl(String forgotUsernameUrl) {
        this.forgotUsernameUrl = forgotUsernameUrl;
    }

    public String getUnlockUserUrl() {
        return unlockUserUrl;
    }

    public void setUnlockUserUrl(String unlockUserUrl) {
        this.unlockUserUrl = unlockUserUrl;
    }

    public String getClientRegistrationUrl() {
        return clientRegistrationUrl;
    }

    public void setClientRegistrationUrl(String clientRegistrationUrl) {
        this.clientRegistrationUrl = clientRegistrationUrl;
    }

    public String getFacebookLink() {
        return facebookLink;
    }

    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }


    public String getRssLink() {
        return rssLink;
    }

    public void setRssLink(String rssLink) {
        this.rssLink = rssLink;
    }

    public String getTwitterLink() {
        return twitterLink;
    }

    public void setTwitterLink(String twitterLink) {
        this.twitterLink = twitterLink;
    }

    public String getYouTubeLink() {
        return youTubeLink;
    }

    public void setYouTubeLink(String youTubeLink) {
        this.youTubeLink = youTubeLink;
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "id=" + id +
                ", serverVersionNumber=" + serverVersionNumber +
                ", status=" + status +
                ", messageEn='" + messageEn + '\'' +
                ", messageAr='" + messageAr + '\'' +
                ", forceUpdate=" + forceUpdate +
                ", renewPasswordHashing=" + renewPasswordHashing +
                ", tradeOnlyIslamicStocks=" + tradeOnlyIslamicStocks +
                '}';
    }
}
