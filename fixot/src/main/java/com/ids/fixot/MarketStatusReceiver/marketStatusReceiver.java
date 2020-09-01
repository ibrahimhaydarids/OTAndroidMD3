package com.ids.fixot.MarketStatusReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import java.util.Date;

public class marketStatusReceiver extends BroadcastReceiver {
    private MarketStatusListener listener;

    public marketStatusReceiver(MarketStatusListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String time = "";
        Integer Color = R.drawable.other_market_status;

        try {

            String marketStatus = intent.getExtras().getString(AppService.EXTRA_MARKET_STATUS);
            String marketTime = intent.getExtras().getString(AppService.EXTRA_MARKET_TIME);

            if (marketTime != null) {
                if (marketTime.equals(""))
                    marketTime = MyApplication.marketStatus.getMarketTime();
                //Log.wtf("InitializeMarketServiceV2 1", "setMarketTime: " + marketTime);
            }


            final Date date = AppService.marketDateFormat.parse(marketTime);

            date.setTime(date.getTime() + 1000);

            time = AppService.marketSetDateFormat.format(date);
            //Log.wtf("setMarketTime : AppService.marketSetDateFormat.format(date)", "= " + time);


        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("setMarketTime error", "e: " + e.getMessage());
        }

        //testing
        //MyApplication.marketStatus.setStatusID(-2);


        if (MyApplication.marketStatus.getStatusID() == 1) {
            if(BuildConfig.label.matches("tijari")) {
                Color = R.drawable.white_open_market;
            }else {
                Color = R.drawable.open_market_status;
            }

        } else if (MyApplication.marketStatus.getStatusID() == -1) {
            Color = R.drawable.closed_market_status;
        } else {

            Color = R.drawable.other_market_status;
        }

        listener.refreshMarketTime(MyApplication.marketStatus.getStatusDescription(), time, Color);

    }
}