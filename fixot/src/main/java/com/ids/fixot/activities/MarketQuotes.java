package com.ids.fixot.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.MarketQuotesRecyclerAdapter;
import com.ids.fixot.model.OffMarketQuotes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by MK on 15/01/2019.
 */

public class MarketQuotes extends AppCompatActivity implements MarketStatusListener {

    TextView tvStockHeader, tvPriceHeader, tvQuantityHeader, tvVolumeHeader, tvValueHeader;
    Toolbar myToolbar;
    RelativeLayout rootLayout;
    RecyclerView rvTrades;
    TextView tvToolbarTitle, tvToolbarStatus;
    TextView tvToDate;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat sdf;
    ImageView ivBack;
    LinearLayoutManager llm;
    MarketQuotesRecyclerAdapter adapter;
    ArrayList<OffMarketQuotes> allMarketQuotes = new ArrayList<>();
    private BroadcastReceiver receiver;
    private boolean started = false;

    public MarketQuotes() {
        LocalUtils.updateConfig(this);
    }

    @Override
    public void refreshMarketTime(String status, String time, Integer color) {

        final TextView marketstatustxt = findViewById(R.id.market_state_value_textview);

        final LinearLayout llmarketstatus = findViewById(R.id.ll_market_state);
        final TextView markettime = findViewById(R.id.market_time_value_textview);
        if(BuildConfig.label.matches("tijari") && (MyApplication.marketStatus.getStatusDescriptionAr().equals("مفتوح") ||
                        MyApplication.marketStatus.getStatusDescriptionEn().equals("Open"))) {
            marketstatustxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green_color));
            markettime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green_color));
        }else {
            marketstatustxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            markettime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
        marketstatustxt.setText(status);
        markettime.setText(time);
        llmarketstatus.setBackground(ContextCompat.getDrawable(this, color));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_market_quotes);
        Actions.initializeBugsTracking(this);

        sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        findViews();
//        Actions.initializeToolBar(getString(R.string.trades), MarketQuotes.this);

        started = true;

        adapter = new MarketQuotesRecyclerAdapter(this, allMarketQuotes);
        llm = new LinearLayoutManager(MarketQuotes.this);
        rvTrades.setLayoutManager(llm);
        rvTrades.setAdapter(adapter);

        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);

        if (!Actions.isNetworkAvailable(this)) {
            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }

        Actions.setTypeface(new TextView[]{tvStockHeader, tvPriceHeader, tvQuantityHeader, tvVolumeHeader, tvValueHeader}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
    }

    public void back(View v) {
        this.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);
        Actions.checkLanguage(this, started);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        // Actions.InitializeMarketServiceV2(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }


    private void findViews() {

        myToolbar = findViewById(R.id.my_toolbar);
        rootLayout = findViewById(R.id.rootLayout);
        rvTrades = findViewById(R.id.rvTrades);
        tvToolbarTitle = findViewById(R.id.toolbar_title);
        tvToolbarStatus = findViewById(R.id.toolbar_status);
        ivBack = findViewById(R.id.ivBack);

        tvToDate = findViewById(R.id.tvToDate);

        tvVolumeHeader = findViewById(R.id.tvVolumeHeader);
        tvStockHeader = findViewById(R.id.tvStockHeader);
        tvPriceHeader = findViewById(R.id.tvPriceHeader);
        tvQuantityHeader = findViewById(R.id.tvQuantityHeader);
        tvValueHeader = findViewById(R.id.tvValueHeader);

        tvToDate.setOnClickListener(v -> new DatePickerDialog(MarketQuotes.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        myCalendar = Calendar.getInstance();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(tvToDate);
            new GetTimeSales().execute();
        };

        updateLabel(tvToDate);
        new GetTimeSales().execute();
    }


    private void updateLabel(TextView editText) {
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    public void loadFooter(View v) {
        Actions.loadFooter(this, v);
    }

    private class GetTimeSales extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            allMarketQuotes.clear();

            String url = MyApplication.link + MyApplication.GetOffMarketQuotes.getValue();
//            String url = "http://10.2.2.103/OTWebService/Services/DataService.svc"+ MyApplication.GetOffMarketQuotes.getValue();

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("Date", sdf.format(myCalendar.getTime()));
            parameters.put("MarketSegmentID", "1");
            parameters.put("key", getResources().getString(R.string.beforekey));
            parameters.put("MarketID", MyApplication.marketID);

            Log.wtf("GetTimeSales", "url = " + url);
            Log.wtf("GetTimeSales", "parameters = " + parameters);

            try {
                result = ConnectionRequests.GET(url, MarketQuotes.this, parameters);
                Log.wtf("result", "rs = " + result);
                allMarketQuotes.addAll(GlobalFunctions.GetOffMarketQuotes(result));
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetQuickLinks.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.wtf("allMarketQuotes", allMarketQuotes.size() + "");

            try {
                MyApplication.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
        }
    }


}
