package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import com.ids.fixot.adapters.NewsRecyclerAdapter;
import com.ids.fixot.adapters.ValuesListArrayAdapter;
import com.ids.fixot.classes.MyMarkerView;
import com.ids.fixot.model.ChartData;
import com.ids.fixot.model.NewsItem;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.ValueItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class StockDetailActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener, MarketStatusListener {

    ImageView ivExpand;
    NestedScrollView nsScroll;
    Button btTimeSales, btOrderBook, btBuy, btSell;
    RelativeLayout rootLayout;
    TextView tvStockName;
    ImageView ivArrow;
    LinearLayout linearStockName;
    StockQuotation stock = new StockQuotation();
    RecyclerView rvValueList;
    ImageView ivFavorite;
    ValuesListArrayAdapter valueListAdapter;
    GridLayoutManager llmValues;
    GetStockInformation getStockInformation;
    GetChartData getChartData;
    boolean running = true, isFavorite = false, connected = false;
    TextView title, value, secondValue, tvNewsHeader;
    SwipeRefreshLayout swipeContainer;
    RecyclerView rvNews;
    LinearLayoutManager llm;
    GetNews mGetNews;
    ImageView ivImage;
    String instrumentId = "";
    String instrumentNameEn = "";
    String instrumentNameAr = "";
    private BroadcastReceiver receiver;
    private ArrayList<ValueItem> allValueItems = new ArrayList<>();
    private boolean started = false;
    private LineChart mChart;
    private ChartData chartdata;
    private ArrayList<NewsItem> allNews = new ArrayList<>();
    private NewsRecyclerAdapter adapter;

    public StockDetailActivity() {
        LocalUtils.updateConfig(this);
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
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
        setContentView(R.layout.activity_stock_detail);
        Actions.initializeBugsTracking(this);

        Actions.initializeToolBar(getString(R.string.stock), StockDetailActivity.this);
        Actions.showHideFooter(this);

        if (getIntent().hasExtra("stock")) {
            stock = getIntent().getExtras().getParcelable("stock");

            instrumentId = stock.getInstrumentId();
            instrumentNameEn = stock.getInstrumentNameEn();
            instrumentNameAr = stock.getInstrumentNameAr();
        } else {

            stock = Actions.getStockQuotationById(MyApplication.stockQuotations, getIntent().getExtras().getInt("stockID"));
            stock.setStockID(getIntent().getExtras().getInt("stockID"));

            instrumentId = stock.getInstrumentId();
            instrumentNameEn = stock.getInstrumentNameEn();
            instrumentNameAr = stock.getInstrumentNameAr();
        }

        try{MyApplication.lastId=stock.getLast();}catch (Exception e){}

        findViews();
        started = true;

        setStockName(stock);

        ivFavorite.setOnClickListener(v -> new AddRemoveFavoriteStock().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));

        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
            connected = false;
        } else {

            connected = true;
        }

        Actions.overrideFonts(this, rootLayout, false);
    }

    public void goToTrade(View v) {

        switch (v.getId()) {

            case R.id.btSell:

                Bundle sellBundle = new Bundle();
                sellBundle.putParcelable("stockQuotation", stock);
                sellBundle.putInt("action", MyApplication.ORDER_SELL);
                Intent sellIntent = new Intent(StockDetailActivity.this, TradesActivity.class);
                sellIntent.putExtras(sellBundle);
                StockDetailActivity.this.startActivity(sellIntent);
                break;

            case R.id.btBuy:

                Bundle buyBundle = new Bundle();
                buyBundle.putParcelable("stockQuotation", stock);
                buyBundle.putInt("action", MyApplication.ORDER_BUY);
                Intent buyIntent = new Intent(StockDetailActivity.this, TradesActivity.class);
                buyIntent.putExtras(buyBundle);
                StockDetailActivity.this.startActivity(buyIntent);
                break;
        }

    }

    public void back(View v) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            getChartData.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getChartData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("StockChart ex", e.getMessage());
        }


        try {
            getStockInformation.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getStockInformation);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Stock Info ex", e.getMessage());
        }

        try {
            mGetNews.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(mGetNews);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Stock Info ex", e.getMessage());
        }

        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void findViews() {

        nsScroll = findViewById(R.id.nsScroll);
        btTimeSales = findViewById(R.id.btTimeSales);
        btOrderBook = findViewById(R.id.btOrderBook);
        swipeContainer = findViewById(R.id.swipeContainer);

        btSell = findViewById(R.id.btSell);
        btBuy = findViewById(R.id.btBuy);

        mChart = findViewById(R.id.chart1);
        mChart.getLayoutParams().height = MyApplication.screenWidth / 2;
        mChart.setGridBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        mChart.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));

        title = findViewById(R.id.title);
        value = findViewById(R.id.value);
        tvNewsHeader = findViewById(R.id.tvNewsHeader);
        secondValue = findViewById(R.id.secondValue);
        rootLayout = findViewById(R.id.rootLayout);
        ivFavorite = findViewById(R.id.ivFavorite);
        tvStockName = findViewById(R.id.stockName);
        ivArrow = findViewById(R.id.ivInfo);
//        if (MyApplication.lang == MyApplication.ARABIC)
//            ivArrow.setRotation(0);
//        else
//            ivArrow.setRotation(180);

        if(MyApplication.isOTC)
            ivArrow.setVisibility(View.VISIBLE);
        else
            ivArrow.setVisibility(View.GONE);


        linearStockName = findViewById(R.id.linearStockName);
        rvValueList = findViewById(R.id.valueList);
        rvNews = findViewById(R.id.rvNews);
        ivExpand = findViewById(R.id.ivExpand);
        ivImage = findViewById(R.id.ivImage);

        valueListAdapter = new ValuesListArrayAdapter(StockDetailActivity.this, allValueItems);
        rvValueList.setAdapter(valueListAdapter);
        llmValues = new GridLayoutManager(StockDetailActivity.this, MyApplication.VALUES_SPAN_COUNT);
        rvValueList.setLayoutManager(llmValues);
        rvValueList.setNestedScrollingEnabled(false);

        adapter = new NewsRecyclerAdapter(StockDetailActivity.this, allNews);
        rvNews.setAdapter(adapter);
        llm = new LinearLayoutManager(StockDetailActivity.this);
        rvNews.setLayoutManager(llm);
        rvNews.setNestedScrollingEnabled(false);

        ivExpand.setOnClickListener(view -> {

            startActivity(new Intent(StockDetailActivity.this, FullChartActivity.class)
//                    .putExtra("isSector", false)
                            .putExtra("stockId", stock.getStockID())
            );
        });

        if(MyApplication.isOTC) {

            linearStockName.setOnClickListener(v -> {
                        String lang = "ar";
                        if (MyApplication.lang == MyApplication.ENGLISH)
                            lang = "en";
                        String url = "https://www.almowazi.com/MobileCompnayProfile.aspx?OTCID=" + stock.getSecurityId() + "&language=" + lang;
                        startActivity(new Intent(StockDetailActivity.this, PdfDisplayActivity.class).putExtra("url", url));
                    }
            );

        }



        btTimeSales.setOnClickListener(v -> startActivity(new Intent(StockDetailActivity.this, TimeSalesActivity.class)
                .putExtra("stockId", stock.getStockID())
                .putExtra("securityId", stock.getSecurityId())
                .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stock.getNameAr() : stock.getNameEn())));

        btOrderBook.setOnClickListener(v -> startActivity(new Intent(StockDetailActivity.this, StockOrderBookActivity.class)
                .putExtra("stockId", stock.getStockID())
                .putExtra("last",stock.getLast())
                .putExtra("securityId", stock.getSecurityId())
                .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stock.getNameAr() : stock.getNameEn())
                .putExtra("isFavorite", isFavorite ? "1" : "0")));

        swipeContainer.setOnRefreshListener(() -> {

            if (connected) {

                allValueItems.clear();
                valueListAdapter.notifyDataSetChanged();

                getStockInformation = new GetStockInformation();
                getStockInformation.executeOnExecutor(MyApplication.threadPoolExecutor);

                getChartData = new GetChartData();
                getChartData.executeOnExecutor(MyApplication.threadPoolExecutor);

                mGetNews = new GetNews();
                mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);

            } else {
                Actions.CreateDialog(this, getString(R.string.no_net), false, false);
            }

            swipeContainer.setRefreshing(false);
        });
    }

    private void setStockName(StockQuotation stock) {

        String stockName = "";
        if (MyApplication.lang == MyApplication.ARABIC) {

            stockName = stock.getSecurityId() + " - " + stock.getNameAr(); //getStockid
            tvStockName.setTypeface(MyApplication.droidbold);
        } else {

            stockName = stock.getSecurityId() + " - " + stock.getNameEn(); //getStockid
            tvStockName.setTypeface(MyApplication.giloryBold);
        }
        tvStockName.setText(stockName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        if (connected) {

            getStockInformation = new GetStockInformation();
            getStockInformation.executeOnExecutor(MyApplication.threadPoolExecutor);

            getChartData = new GetChartData();
            getChartData.executeOnExecutor(MyApplication.threadPoolExecutor);

            mGetNews = new GetNews();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);

        }
        Actions.checkLanguage(this, started);

        //Actions.InitializeSessionService(this);
        //Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //Actions.InitializeMarketServiceV2(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    //<editor-fold desc="chart functions">
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {
    }
    //</editor-fold>

    public ArrayList<ValueItem> GetStockDetails(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {
            try {
                JSONObject json_data = object.getJSONObject("StockDetails");

                stock = new StockQuotation();
                stock.setChange(json_data.getString("Change").equals("0") ? "0.0" : json_data.getString("Change"));
                stock.setChangePercent(json_data.getString("ChangePercent"));
                stock.setStockID(Integer.parseInt(json_data.getString("StockID")));
                stock.setNameAr(json_data.getString("NameAr"));
                stock.setNameEn(json_data.getString("NameEn"));
                stock.setValue(json_data.getString("Value"));
                stock.setSymbolAr(json_data.getString("SymbolAr"));
                stock.setSymbolEn(json_data.getString("SymbolEn"));
                stock.setSessionId(json_data.getString("SessionId"));
                stock.setSecurityId(json_data.getString("SecurityID"));
                stock.setLast(json_data.getDouble("Last"));
                isFavorite = json_data.getBoolean("IsFavorite");

                stock.setInstrumentId(instrumentId);
                stock.setInstrumentNameEn(instrumentNameEn);
                stock.setInstrumentNameAr(instrumentNameAr);

                JSONArray json_fields = json_data.getJSONArray("Field");
                for (int i = 0; i < json_fields.length(); i++) {

                    JSONObject field = json_fields.getJSONObject(i);
                    ValueItem stockValueItem = new ValueItem(field.getString("Key"), field.getString("Value"));
                    allValueItems.add(stockValueItem);
                }
            } catch (Exception e) {
                Log.d("ee", e + "");
            }
        }
        return allValueItems;
    }

    private class AddRemoveFavoriteStock extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(StockDetailActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.AddFavoriteStocks.getValue();

            if (isFavorite) {
                url = MyApplication.link + MyApplication.RemoveFavoriteStocks.getValue();
            }

            String stringer = "{\"StockIDs\":[\"" + stock.getStockID() + "\"],\"UserID\":" + MyApplication.currentUser.getId()
                    + ",\"key\":\"" + getString(R.string.beforekey) + "\"}";

            Log.wtf("Favorite", "AddRemoveFavoriteStock stringer = " + stringer);

            result = ConnectionRequests.POSTWCF2(url, stringer);

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                MyApplication.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject object = null;
            try {
                object = new JSONObject(result);
                String success = object.getString("MessageEn");
                if (success.equals("Success")) {

                    if (!isFavorite) {
                        Actions.CreateDialog(StockDetailActivity.this, getString(R.string.save_success), false, false);
                        ivFavorite.setImageResource(R.drawable.added_to_favorites);
                        isFavorite = true;
                    } else {
                        Actions.CreateDialog(StockDetailActivity.this, getString(R.string.delete_success), false, false);
                        ivFavorite.setImageResource(R.drawable.add_to_favorites);
                        isFavorite = false;
                    }
                } else {

                    Actions.CreateDialog(StockDetailActivity.this, getString(R.string.error), false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();

                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (isFavorite) {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.RemoveFavoriteStocks.getKey(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.AddFavoriteStocks.getKey(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        }
    }

    private class GetStockInformation extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MyApplication.showDialog(StockDetailActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.LoadStockDetails.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();

            try {
                parameters.clear();
                parameters.put("stockId", "'" + stock.getStockID() + "'");
                parameters.put("UserID", MyApplication.currentUser.getId() + "");
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("Lang", Actions.getLanguage());
                parameters.put("MarketId", MyApplication.marketID);

                result = ConnectionRequests.GET(url, StockDetailActivity.this, parameters);

                for (Map.Entry<String, String> map : parameters.entrySet()) {
                    Log.wtf("StockDetails LoadStockDetails", "parameters : " + map.getKey() + "= " + map.getValue());
                }
                Log.wtf("StockDetails", "LoadStockDetails result = " + result);

                try {
                    GetStockDetails(result);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    if (MyApplication.isDebug) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.LoadStockDetails.getKey(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.LoadStockDetails.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            try {
                MyApplication.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.wtf("LoadStockDetails ", "stockValueItems size = " + allValueItems.size() + "");

            title.setText("" + stock.getLast());
            value.setText(stock.getChange());
            Log.wtf("getChange getChange", "ss " + stock.getChange());

            try {
                double change = Double.parseDouble(stock.getChangePercent().split("%")[0]);
                if (change == 0) {
                    ivImage.setVisibility(View.GONE);
                } else {
                    ivImage.setVisibility(View.VISIBLE);
                    ivImage.setImageResource(change > 0 ? R.drawable.up_green_arrow : R.drawable.down_red_arrow);
                }
            } catch (Exception e) {
                Log.wtf("stockDetails", "getChange Parse Double Error : " + e.getMessage());
            }

            double change = 0;
            String changeP = "";
            try {

                String changeValue = null;
                try {
                    changeValue = stock.getChange();//.substring(1, stock.getChange().length() - 1);
                    Log.wtf("getChange getChange", "ss " + changeValue);
                } catch (Exception e) {
                    e.printStackTrace();
                    changeValue = "0";
                }

                //  change = Double.parseDouble(changeValue);
                if (stock.getChangePercent() == null || stock.getChangePercent().equals("null")) {
                    stock.setChangePercent("0");
                }

                String changePercent = stock.getChangePercent();
                changeP = stock.getChangePercent().replace("%", "").trim();

                Log.wtf("changeP", "ss " + changeP);
                secondValue.setText(changePercent);
            } catch (Exception e) {
                e.printStackTrace();

                changeP = "0";
                change = 0;
                String changePercent = stock.getChangePercent();
                secondValue.setText(changePercent);
                Toast.makeText(StockDetailActivity.this, "changeP error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.wtf("changeP Error", " cause : " + e.getMessage());
            }


            //<editor-fold desc="comparison on changePercent">
            try {
                if ((Double.parseDouble(changeP) + 0) == 0) {

                    title.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.other_market));
                    value.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.other_market));
                    secondValue.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.other_market));
                } else if ((Double.parseDouble(changeP) + 0) < 0) {

                    title.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.red_color));
                    value.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.red_color));
                    secondValue.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.red_color));
                } else {

                    title.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.green_color));
                    value.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.green_color));
                    secondValue.setTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.green_color));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(StockDetailActivity.this, "Color error : " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.wtf("Color Error", " cause : " + e.getMessage());
            }
            //</editor-fold>

            if (MyApplication.lang == MyApplication.ARABIC) {
                title.setTypeface(MyApplication.giloryBold);
                value.setTypeface(MyApplication.giloryBold);
                secondValue.setTypeface(MyApplication.giloryBold);
            } else {
                title.setTypeface(MyApplication.giloryBold);
                value.setTypeface(MyApplication.giloryBold);
                secondValue.setTypeface(MyApplication.giloryBold);
            }


            if (!isFavorite)
                ivFavorite.setImageResource(R.drawable.add_to_favorites);
            else {
                ivFavorite.setImageResource(R.drawable.added_to_favorites);
                isFavorite = true;
            }

            //stock.setType(Stock.STOCK);
            //setStockDetails(stock.getId() + " " + stock.getName());

            Actions.initializeToolBar(MyApplication.lang == MyApplication.ARABIC ? stock.getSymbolAr() : stock.getSymbolEn(), StockDetailActivity.this);
            setStockName(stock);

            valueListAdapter.notifyDataSetChanged();

            nsScroll.scrollTo(0, 0);
        }
    }

    public class MyValueFormatter implements YAxisValueFormatter {

        public MyValueFormatter() {

        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return String.valueOf(value).replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "4").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "8").replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0");
        }
    }

    public class GetChartData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mChart.setNoDataText("Loading...");
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {

                if (chartdata.getChartvalues().size() != 0) {

                    mChart.setVisibility(View.VISIBLE);
                    ivExpand.setVisibility(View.VISIBLE);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> xVals = new ArrayList<String>();
                            ArrayList<Entry> yVals = new ArrayList<>();

                            for (int i = 0; i <= chartdata.getChartvalues().size() - 1; i++) {

                                xVals.add(i, chartdata.getChartvalues().get(i).getDate().substring(
                                        chartdata.getChartvalues().get(i).getDate().indexOf(" ")
                                ));
                                float f = (float) chartdata.getChartvalues().get(i).getValue();


                                yVals.add(new Entry(f, i));
                            }

                            LineDataSet set1 = new LineDataSet(yVals, "");
                            set1.setAxisDependency(YAxis.AxisDependency.LEFT);

                            YAxis yAxis = mChart.getAxisLeft(); // get the left or right axis
                            yAxis.setStartAtZero(false);
                            yAxis.setSpaceBottom(20f);

                            yAxis.setValueFormatter(new MyValueFormatter());
                            yAxis.setTextColor(ContextCompat.getColor(StockDetailActivity.this, MyApplication.mshared.getBoolean(StockDetailActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));

//                            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//                                set1.setColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.colorDark));
                            set1.setColor(ContextCompat.getColor(StockDetailActivity.this, MyApplication.mshared.getBoolean(StockDetailActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
                            set1.setValueTextColor(ContextCompat.getColor(StockDetailActivity.this, R.color.colorDark));
                            //  set1.setLineWidth(1.5f);
                            set1.setDrawCircles(false);
                            set1.setDrawValues(false);

                            set1.setFillAlpha(65);
                            set1.setFillColor(ContextCompat.getColor(StockDetailActivity.this, R.color.colorDark));
                            set1.setHighLightColor(Color.rgb(244, 117, 117));
                            set1.setDrawCircleHole(false);

                            final LineData data = new LineData(xVals, set1);
                            data.setValueTextColor(Color.WHITE);
                            data.setValueTextSize(9f);

                            mChart.getAxisRight().setEnabled(false);
                            mChart.setPinchZoom(false);
                            mChart.getAxisLeft().setStartAtZero(false);
                            mChart.setDescriptionColor(Color.TRANSPARENT);
                            mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                            mChart.setOnChartGestureListener(StockDetailActivity.this);
                            mChart.setOnChartValueSelectedListener(StockDetailActivity.this);
                            mChart.setDescriptionTypeface(MyApplication.giloryItaly);

                            MyMarkerView mv = new MyMarkerView(StockDetailActivity.this, R.layout.custom_marker_view, chartdata);

                            mv.setKeepScreenOn(true);

                            mChart.setMarkerView(mv);

                            mChart.setData(data);

                            mChart.getLegend().setEnabled(false);
                            mChart.getAxisLeft().setStartAtZero(false);

                            XAxis xAxis = mChart.getXAxis();
                            xAxis.setTextColor(ContextCompat.getColor(StockDetailActivity.this, MyApplication.mshared.getBoolean(StockDetailActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));


                            mChart.invalidate();

                            //  mChart.setVisibleXRangeMaximum(10);
                            mChart.getLayoutParams().height = MyApplication.screenWidth / 2;
                        }
                    });

                } else {
                    mChart.setData(null);
                    mChart.setNoDataText(getString(R.string.noData));
                    mChart.invalidate();
                    mChart.setVisibility(View.GONE);
                    ivExpand.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetStockChartData.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("stockId", "" + stock.getStockID());
            parameters.put("key", getString(R.string.beforekey));
            parameters.put("MarketId", MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, StockDetailActivity.this, parameters);
                chartdata = GlobalFunctions.GetStockChartData(result);

                publishProgress(params);
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetStockChartData.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }
    }

    private class GetNews extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            allNews.clear();
            adapter.notifyDataSetChanged();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetNews.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("newsId", "0");
            parameters.put("stockId", "" + stock.getStockID());
            parameters.put("language", ""); //Actions.getLanguage()
            parameters.put("count", "" + MyApplication.count);
            parameters.put("key", getResources().getString(R.string.beforekey));

            try {
                result = ConnectionRequests.GET(url, StockDetailActivity.this, parameters);
                Log.wtf("News result ", result);
                allNews.addAll(GlobalFunctions.GetNews(result));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetNews.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (allNews.size() > 0) {

                tvNewsHeader.setVisibility(View.VISIBLE);
                rvNews.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();
            } else {

                tvNewsHeader.setVisibility(View.GONE);
                rvNews.setVisibility(View.GONE);
            }
            Log.wtf("News Count for STock", stock.getSymbolEn() + " = " + allNews.size());

        }
    }
}
