package com.ids.fixot.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.classes.MessageEvent;
import com.ids.fixot.classes.SqliteDb_TimeSales;
import com.ids.fixot.fragments.NormalLoginFragment;
import com.ids.fixot.model.TimeSale;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;

public class GetTradeService extends Service {
    public GetTradeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        new GetTrades().executeOnExecutor(MyApplication.threadPoolExecutor);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }


    private class GetTrades extends AsyncTask<Void, String, String> {

        ArrayList<TimeSale> retrievedTimeSales;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            retrievedTimeSales = new ArrayList<>();
            MyApplication.timeSalesTimesTamp = "0";
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.GetTrades.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();

          //  MyApplication.timeSalesTimesTamp="246986000";


            parameters.put("stockId", "");
            parameters.put("instrumentId", "");
            parameters.put("MarketID", MyApplication.marketID);
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));
            parameters.put("FromTS", MyApplication.timeSalesTimesTamp);



            Log.wtf("GetTrades Login url", "is: " + url);
            Log.wtf("GetTrades Login parameters", "is: " + parameters);
            try {

                result = ConnectionRequests.GET(url, getApplicationContext(), parameters);
                retrievedTimeSales = GlobalFunctions.GetTimeSales(result,true);

                try{Log.wtf("test_service_login","size:"+retrievedTimeSales.size());}catch (Exception e){
                    Log.wtf("test_service_login",e.toString());
                }
                MyApplication.timeSales = new ArrayList<>();
                MyApplication.timeSales.addAll(retrievedTimeSales);
                SqliteDb_TimeSales timeSales_DB = new SqliteDb_TimeSales(getApplicationContext());
                timeSales_DB.open();
                timeSales_DB.deleteTimeSales();
                timeSales_DB.insertTimeSalesList(retrievedTimeSales);
                timeSales_DB.close();



                Log.wtf("test_service_NormalLoginFragment", "insertTimeSalesList size = " + retrievedTimeSales.size());
                Log.wtf("test_service_NormalLoginFragment", "insertTimeSalesList size = " + retrievedTimeSales.size());

            } catch (Exception e) {



                Log.wtf("test_service_NormalLoginFragment",e.toString());
                new GetTrades().executeOnExecutor(MyApplication.threadPoolExecutor);
                e.printStackTrace();


                try{
                    Log.wtf("test_call_loginagain","true");
               /*     GetTrades getTrades = new GetTrades();
                    getTrades.executeOnExecutor(MyApplication.threadPoolExecutor);*/
                }catch (Exception e1){}

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MyApplication.IsTimeSaleLoginRetreived=true;
            EventBus.getDefault().post(new MessageEvent("1"));
            Log.wtf("loginretreived","true");

            stopSelf();
            Log.wtf("timeSales login size", "is: " + MyApplication.timeSales.size());
            Log.wtf("AAAAsync Login Timestamp", "is: " + MyApplication.timeSalesTimesTamp);

        }
    }

}
