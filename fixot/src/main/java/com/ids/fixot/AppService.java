package com.ids.fixot;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.ids.fixot.model.StockQuotation;

import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AppService extends Service {


    public static final String ACTION_STOCKS_SERVICE = AppService.class.getName() + "AppService";
    public static final String ACTION_MARKET_SERVICE = AppService.class.getName() + "MarketService";
    public static final String ACTION_SESSION_SERVICE = AppService.class.getName() + "SessiontService";
    public static String EXTRA_MARKET_STATUS = "marketstatus";
    public static String EXTRA_MARKET_TIME = "markettime";
    public static String EXTRA_SESSION = "session";
    public static String EXTRA_STOCK_QUOTATIONS_LIST = "stocksList";
    public static SimpleDateFormat marketDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
    public static SimpleDateFormat marketSetDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    Date date;
    boolean firstTime = true, running = true;
    private Handler handler;
    private ArrayList<StockQuotation> changedStocks = new ArrayList<>();

    @Override
    public void onDestroy() {

        Log.wtf("AppService on", "destroy");

        try {

            stopForeground(true);
            stopSelf();
            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new GetStockQuotation().executeOnExecutor(MyApplication.threadPoolExecutor);

        int delay = 1000; //milliseconds

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                try {
//                    Log.wtf("MyApplication.currentUser","getId : " + MyApplication.currentUser.getId());
                    if (MyApplication.currentUser.getId() != -1) {
                        new GetRealTimeData().executeOnExecutor(MyApplication.threadPoolExecutor);
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    MyApplication.threadPoolExecutor = null;
                    MyApplication.threadPoolExecutor = new ThreadPoolExecutor(MyApplication.corePoolSize, MyApplication.maximumPoolSize,
                            MyApplication.keepAliveTime, TimeUnit.SECONDS, MyApplication.workQueue);
                }


                handler.postDelayed(this, 2000);
            }
        }, delay);

        return START_STICKY;
    }

    private void sendBroadcastMarket(String marketstatus, String markettime) {

        Intent intent = new Intent(ACTION_MARKET_SERVICE);
        intent.putExtra(EXTRA_MARKET_STATUS, marketstatus);
        intent.putExtra(EXTRA_MARKET_TIME, markettime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.wtf("sendBroadcastMarketStatus", "ACTION_MARKET_SERVICE");
    }


    private void sendBroadcastSession(boolean session) {
        Intent intent = new Intent(ACTION_SESSION_SERVICE);
        intent.putExtra(EXTRA_SESSION, session);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendBroadcastUpdatedList(ArrayList<StockQuotation> stockQuotationsList) {

        Intent intent = new Intent(ACTION_STOCKS_SERVICE);
        intent.putParcelableArrayListExtra(EXTRA_STOCK_QUOTATIONS_LIST, stockQuotationsList);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void replaceOldStocks(ArrayList<StockQuotation> oldstocks, ArrayList<StockQuotation> newstocks) {

        for (int i = 0; i < oldstocks.size(); i++) {
            boolean contains = false;
            for (int k = 0; k < newstocks.size(); k++) {
                if (oldstocks.get(i).getStockID() == newstocks.get(k).getStockID()) {
                    contains = true;
                    //Log.wtf("replaceOldStocks",newstocks.get(k).getSymbolEn() + " contains");
                    newstocks.get(k).setChanged(true);
                    oldstocks.set(i, newstocks.get(k));
                }
            }
            if (!contains)
                oldstocks.get(i).setChanged(false);
        }

    }

    private class GetRealTimeData extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetRealTimeData.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", MyApplication.currentUser.getId() + "");
            parameters.put("currentUserSessionID", MyApplication.currentUser.getSessionID() + "");
            parameters.put("key", getResources().getString(R.string.beforekey));
            parameters.put("MarketId", MyApplication.marketID);


            try {
                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                Log.wtf("GetRealTimeData", "result : " + result);

            } catch (IOException e) {
                e.printStackTrace();
                if (MyApplication.showBackgroundRequestToastError) {
                    try {
                        if (MyApplication.isDebug) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetRealTimeData.getKey(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception es) {
                        Log.wtf("Appservice ", "Error - " + MyApplication.GetRealTimeData.getKey() + " : " + es.getMessage());
                    }
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                MyApplication.marketStatus = GlobalFunctions.GetMarketStatus(s);


                try {

                    String marketTime = MyApplication.marketStatus.getServerTime();

                    sendBroadcastSession(MyApplication.marketStatus.isSessionChanged());

                    try {

                        date = marketDateFormat.parse(marketTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (MyApplication.marketStatus.getMarketTime().equals("")) {
                        sendBroadcastMarket(MyApplication.marketStatus.getStatusDescription(), marketDateFormat.format(date));
                    } else {
                        sendBroadcastMarket(MyApplication.marketStatus.getStatusDescription(), "");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetStockQuotation extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            sendBroadcastUpdatedList(MyApplication.stockQuotations);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetStockQuotation.getValue();

            HashMap<String, String> parameters = new HashMap<String, String>();

            while (running) {

                if (isCancelled())
                    break;

                try {
                    parameters.clear();
                    parameters.put("InstrumentId", "");
                    parameters.put("stockIds", "");
                    parameters.put("TStamp", MyApplication.stockTimesTamp);
                    parameters.put("key", getResources().getString(R.string.beforekey));
                    parameters.put("MarketId", MyApplication.marketID);

                    result = ConnectionRequests.GET(url, getApplicationContext(), parameters);

                    if (firstTime || MyApplication.stockQuotations.size() == 0) {
                        MyApplication.stockQuotations.addAll(GlobalFunctions.GetStockQuotation(result));
                        firstTime = false;
                    } else if (!MyApplication.stockTimesTamp.equals("0")) {
                        try {
                            changedStocks = GlobalFunctions.GetStockQuotation(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (changedStocks.size() != 0 && !MyApplication.stockTimesTamp.equals("0")) {

                        replaceOldStocks(MyApplication.stockQuotations, changedStocks);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (MyApplication.showBackgroundRequestToastError) {
                        try {
                            if (MyApplication.isDebug) {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetStockQuotation.getKey(), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception es) {
                            Log.wtf("Appservice ", "Error - " + MyApplication.GetStockQuotation.getKey() + " : " + es.getMessage());
                        }
                    }
                }
                publishProgress();
                try {
                    Thread.sleep(2000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
