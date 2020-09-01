package com.ids.fixot.model.mowazi;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziCompanyNotifications {

    private int companyId;
    private boolean ordersNotifications, tradesNotifications, newsNotifications;

    public MowaziCompanyNotifications() {

    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public boolean isOrdersNotifications() {
        return ordersNotifications;
    }

    public void setOrdersNotifications(boolean ordersNotifications) {
        this.ordersNotifications = ordersNotifications;
    }

    public boolean isTradesNotifications() {
        return tradesNotifications;
    }

    public void setTradesNotifications(boolean tradesNotifications) {
        this.tradesNotifications = tradesNotifications;
    }

    public boolean isNewsNotifications() {
        return newsNotifications;
    }

    public void setNewsNotifications(boolean newsNotifications) {
        this.newsNotifications = newsNotifications;
    }

    @Override
    public boolean equals(Object o) {
        return getCompanyId() == ((MowaziCompanyNotifications) o).getCompanyId();
    }
}

