package com.ids.fixot.MarketStatusReceiver;

public interface MarketStatusListener {

    void refreshMarketTime(String status, String time, Integer color);

}
