package com.ids.fixot.model;

public class webserviceItem {

    item url = new item();
    item accountStatmentUrl = new item();
    item kccStatmentUrl = new item();
    item GetStockQuotation = new item();
    item GetRealTimeData = new item();
    item AddDevice2 = new item();
    item GetParameters = new item();
    item GetInstruments = new item();
    item GetBrokerageFees = new item();
    item GetSiteMapData = new item();
    item UpdateBadgeNotification = new item();
    item GetOrderDurationTypes = new item();
    item Login = new item();
    item GetTradeTickerData = new item();
    item GetPriceTickerData = new item();
    item GetSectorChartData = new item();
    item LoadSectorDetails = new item();
    item GetPortfolio = new item();
    item GetUserOrders = new item();
    item LoadStockDetails = new item();
    item GetStockChartData = new item();
    item GetTrades = new item();
    item GetStockOrderBook = new item();
    item GetSectorIndex = new item();
    item GetStockTops = new item();
    item GetNews = new item();
    item GetMobileSiteMap = new item();
    item AddFavoriteStocks = new item();
    item RemoveFavoriteStocks = new item();
    item GetFavoriteStocks = new item();
    item GetQuickLinks = new item();
    item GetTradeInfo = new item();
    item AddNewOrder = new item();
    item CancelOrder = new item();
    item UpdateOrder = new item();
    item ActivateOrder = new item();
    item GetUnits = new item();


    public void setGetStockQuotation(item items) {
        this.GetStockQuotation.setKey(items.getKey());
        this.GetStockQuotation.setValue(items.getValue());
    }

}
