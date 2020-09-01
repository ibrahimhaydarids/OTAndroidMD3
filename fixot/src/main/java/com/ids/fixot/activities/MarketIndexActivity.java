package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.ids.fixot.adapters.SectorSpinnerAdapter;
import com.ids.fixot.adapters.ValuesGridRecyclerAdapter;
import com.ids.fixot.classes.MyMarkerView;
import com.ids.fixot.classes.ScrollViewExt;
import com.ids.fixot.model.ChartData;
import com.ids.fixot.model.NewsItem;
import com.ids.fixot.model.Sector;
import com.ids.fixot.model.Stock;
import com.ids.fixot.model.TimeSale;
import com.ids.fixot.model.ValueItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.ids.fixot.MyApplication.lang;

public class MarketIndexActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener, MarketStatusListener {

    ImageView ivExpand, ivImage, ivSector;
    RecyclerView rvValueList;
    FrameLayout fl_Chart;
    ArrayList<Object> tickerSectorsObject = new ArrayList<>();
    GetPriceTickerData getPriceTickerData;
    boolean running = true;
    boolean first = true;
    GridLayoutManager llmValues;
    RelativeLayout rootLayout;
    Spinner sectorSpinner;
    SectorSpinnerAdapter sectorSpinnerAdapter;
    GetTradeTickerData getTradeTickerData;
    SwipeRefreshLayout swipeContainer;
    TextView tvSectorsTicker, tvTradesTicker, tvNewsHeader, tvNewsMore, tvLogout;
    float tickerSpeed = 90f;
    LinearLayout ll_SectorChanges;
    TextView title, value, secondValue, tvNewTicker;
    Sector sector;
    Button btnSector;
    ArrayList<NewsItem> allNews = new ArrayList<>();
    RecyclerView rvNews;
    LinearLayoutManager llm;
    GetNews mGetNews;
    ScrollViewExt hsScroll;
    Handler handler;
    Handler timehandler;
    int i = 0;
    int j = 0;
    private ArrayList<TimeSale> allTrades = new ArrayList<>();
    private LineChart mChart;
    private ChartData chartdata;
    private ArrayList<Sector> sectorsTicker = new ArrayList<>();
    private ArrayList<ValueItem> allValueItems = new ArrayList<>();
    private ValuesGridRecyclerAdapter valueListAdapter;
    private GetSectorChartData getchart;
    private GetSectorIndex getsectorindex;
    private boolean started = false;
    private String selectedSectorID = "0";
    private NewsRecyclerAdapter adapter;
    private BroadcastReceiver receiver;

    public MarketIndexActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);

        setContentView(R.layout.activity_home);

        Actions.initializeBugsTracking(this);

        findViews();

        initializeValueList();
        initializeSectorsTicker();

        Log.wtf("marketID", "marketID = " + MyApplication.marketID);

        if (Actions.isNetworkAvailable(this)) {

            mGetNews = new GetNews();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);
        }


        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Actions.stopAppService(MarketIndexActivity.this);
            }
        }, 5000);*/
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
        //testing
       // marketstatustxt.setText("قبول  يبب يالطلبات");
        markettime.setText(time);
        llmarketstatus.setBackground(ContextCompat.getDrawable(this, color));

    }

    private void findViews() {

        hsScroll = findViewById(R.id.hsScroll);
        tvNewTicker = findViewById(R.id.tvNewTicker);

        swipeContainer = findViewById(R.id.swipeContainer);
        tvNewsHeader = findViewById(R.id.tvNewsHeader);
        tvNewsMore = findViewById(R.id.tvNewsMore);
        ivExpand = findViewById(R.id.ivExpand);
        ivImage = findViewById(R.id.ivImage);
        ivSector = findViewById(R.id.iv_sector);
        title = findViewById(R.id.title);
        tvSectorsTicker = findViewById(R.id.tvTicker);
        tvTradesTicker = findViewById(R.id.tvTradesTicker);
        value = findViewById(R.id.value);
        secondValue = findViewById(R.id.secondValue);
        rootLayout = findViewById(R.id.rootLayout);
        btnSector = findViewById(R.id.sectors_button);

        btnSector.setVisibility(MyApplication.isOTC ? View.GONE : View.VISIBLE);

        Actions.overrideFonts(this, rootLayout, false);
        Actions.initializeToolBar(getString(R.string.market_index), MarketIndexActivity.this);
        Actions.showHideFooter(this);
        started = true;

        fl_Chart = findViewById(R.id.fl_Chart);
        fl_Chart.setVisibility(MyApplication.isOTC ? View.GONE : View.VISIBLE);

        mChart = findViewById(R.id.chart1);
        mChart.getLayoutParams().height = MyApplication.screenWidth / 2;
        mChart.setGridBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        mChart.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));

        rvValueList = findViewById(R.id.valueList);
        rvValueList.setVisibility(MyApplication.isOTC ? View.GONE : View.VISIBLE);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(MarketIndexActivity.this));
        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);
        tvLogout.setVisibility((BuildConfig.GoToMenu) ? View.INVISIBLE : View.VISIBLE);

        ImageView ivBack;
        ivBack = myToolbar.findViewById(R.id.ivBack);
        ivBack.setVisibility((BuildConfig.GoToMenu) ? View.VISIBLE : View.GONE);


        ivExpand.setOnClickListener(view -> startActivity(new Intent(MarketIndexActivity.this, FullChartActivity.class)
                .putExtra("isSector", true)
                .putExtra("sectorId", "0000")
        ));

        ivSector.setOnClickListener(view -> {
            sectorSpinner.performClick();
        });

        ll_SectorChanges = findViewById(R.id.ll_SectorChanges);
        ll_SectorChanges.setVisibility(MyApplication.isOTC ? View.GONE : View.VISIBLE);

        llmValues = new GridLayoutManager(MarketIndexActivity.this, MyApplication.GRID_VALUES_SPAN_COUNT);
        rvValueList.setLayoutManager(llmValues);
        rvValueList.setNestedScrollingEnabled(false);

        rvNews = findViewById(R.id.rvNews);
        llm = new LinearLayoutManager(MarketIndexActivity.this);
        rvNews.setLayoutManager(llm);
        adapter = new NewsRecyclerAdapter(this, allNews);
        rvNews.setAdapter(adapter);
        rvNews.setNestedScrollingEnabled(false);

        tvNewsMore.setOnClickListener(view -> startActivity(new Intent(MarketIndexActivity.this, NewsActivity.class)));

        swipeContainer.setOnRefreshListener(() -> {

            mGetNews = new GetNews();
            mGetNews.executeOnExecutor(MyApplication.threadPoolExecutor);
            swipeContainer.setRefreshing(false);
        });


        Button stock_button = findViewById(R.id.stock_button);
        Button news_button = findViewById(R.id.news_button);
        Button time_sales_button = findViewById(R.id.time_sales_button);
        Button sectors_button = findViewById(R.id.sectors_button);
        Button tops_button = findViewById(R.id.tops_button);
        if (MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true)) {

            stock_button.setBackground(ContextCompat.getDrawable(this, R.drawable.borderlayout));
            news_button.setBackground(ContextCompat.getDrawable(this, R.drawable.borderlayout));
            time_sales_button.setBackground(ContextCompat.getDrawable(this, R.drawable.borderlayout));
            sectors_button.setBackground(ContextCompat.getDrawable(this, R.drawable.borderlayout));
            tops_button.setBackground(ContextCompat.getDrawable(this, R.drawable.borderlayout));
            tvNewsHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.borderlayout));
            tvNewsMore.setBackground(ContextCompat.getDrawable(this, R.drawable.borderlayout));

            stock_button.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            news_button.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            time_sales_button.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            sectors_button.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            tops_button.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            tvNewsHeader.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            tvNewsMore.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
        } else {

            stock_button.setBackground(ContextCompat.getDrawable(this, R.drawable.border_layout_dark));
            news_button.setBackground(ContextCompat.getDrawable(this, R.drawable.border_layout_dark));
            time_sales_button.setBackground(ContextCompat.getDrawable(this, R.drawable.border_layout_dark));
            sectors_button.setBackground(ContextCompat.getDrawable(this, R.drawable.border_layout_dark));
            tops_button.setBackground(ContextCompat.getDrawable(this, R.drawable.border_layout_dark));
            tvNewsHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.border_layout_dark));
            tvNewsMore.setBackground(ContextCompat.getDrawable(this, R.drawable.border_layout_dark));

            stock_button.setTextColor(ContextCompat.getColor(this, R.color.white));
            news_button.setTextColor(ContextCompat.getColor(this, R.color.white));
            time_sales_button.setTextColor(ContextCompat.getColor(this, R.color.white));
            sectors_button.setTextColor(ContextCompat.getColor(this, R.color.white));
            tops_button.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvNewsHeader.setTextColor(ContextCompat.getColor(this, R.color.white));
            tvNewsMore.setTextColor(ContextCompat.getColor(this, R.color.white));

        }
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.GoToMenu) {
            super.onBackPressed();
        } else {
            Actions.exitApp(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();

        if (getchart != null)
            getchart.cancel(true);
        if (getsectorindex != null)
            getsectorindex.cancel(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        handler = new Handler();
        timehandler = new Handler();

        Actions.setActivityTheme(this);
        Actions.checkSession(this);
        Actions.checkLanguage(this, started);

        Actions.InitializeSessionServiceV2(MarketIndexActivity.this);
        // Actions.InitializeMarketServiceV2(MarketIndexActivity.this);

        if (Actions.isNetworkAvailable(this) && !MyApplication.isOTC) {

            getchart = new GetSectorChartData();
            getchart.executeOnExecutor(MyApplication.threadPoolExecutor);

            getsectorindex = new GetSectorIndex();
            getsectorindex.executeOnExecutor(MyApplication.threadPoolExecutor);

            getPriceTickerData = new GetPriceTickerData();
            getPriceTickerData.executeOnExecutor(MyApplication.threadPoolExecutor, "0");

            getTradeTickerData = new GetTradeTickerData();
            getTradeTickerData.executeOnExecutor(MyApplication.threadPoolExecutor);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (getchart != null)
            getchart.cancel(true);

        if (getsectorindex != null)
            getsectorindex.cancel(true);

        try {
            getPriceTickerData.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getPriceTickerData);
            handler.removeCallbacks(null);
            handler = null;
            getPriceTickerData = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            getTradeTickerData.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getTradeTickerData);
            timehandler.removeCallbacks(null);
            timehandler = null;
            getTradeTickerData = null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            System.gc();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMarqueeSpeed(TextView tv, float speed) {
        if (tv != null) {
            try {
                Field f = null;
                if (tv instanceof AppCompatTextView) {
                    f = tv.getClass().getSuperclass().getDeclaredField("mMarquee");
                } else {
                    f = tv.getClass().getDeclaredField("mMarquee");
                }
                if (f != null) {
                    f.setAccessible(true);
                    Object marquee = f.get(tv);
                    if (marquee != null) {
                        String scrollSpeedFieldName = "mScrollUnit";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            scrollSpeedFieldName = "mPixelsPerSecond";
                        }
                        Field mf = marquee.getClass().getDeclaredField(scrollSpeedFieldName);
                        mf.setAccessible(true);
                        mf.setFloat(marquee, speed);
                    }
                } else {
                    Log.wtf("Marquee", "mMarquee object is null.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void initializeSectorsTicker() {

        sectorSpinner = findViewById(R.id.sectorSpinner);
        sectorsTicker.clear();
        Sector fake = new Sector();
        fake.setSectorID("-1");
        fake.setNameAr(getString(R.string.all_sectors));
        fake.setNameEn(getString(R.string.all_sectors));
        sectorsTicker.add(0, fake);
        sectorSpinnerAdapter = new SectorSpinnerAdapter(MarketIndexActivity.this, sectorsTicker);
        sectorSpinner.setAdapter(sectorSpinnerAdapter);

//        sectorSpinner.setBackgroundColor(getResources().getColor(R.color.colorDark));
//        sectorSpinner.setPopupBackgroundResource((MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorDarkTheme) );
//        sectorSpinner.setPopupBackgroundDrawable( this.getResources().getDrawable(R.drawable.spinnershape));


        sectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {

                if (position != 0) {
                    selectedSectorID = sectorsTicker.get(position).getSectorID() + "";
                    tvNewTicker.setText(". . . . . .");
                } else {
                    selectedSectorID = "0";
                }

                if (first)
                    first = false;
                else {
                    getPriceTickerData = new GetPriceTickerData();
                    getPriceTickerData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, selectedSectorID);
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

    }

    public void logout(View view) {
        Actions.logout(MarketIndexActivity.this);
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    public void loadPage(View v) {
        int id = v.getId();
        if (id == R.id.stock_button) {

            Actions.startActivity(MarketIndexActivity.this, StockActivity.class, false);

        } else if (id == R.id.news_button) {
            Actions.startActivity(MarketIndexActivity.this, NewsActivity.class, false);

        } else if (id == R.id.sectors_button) {

            Actions.startActivity(MarketIndexActivity.this, SectorsActivity.class, false);

        } else if (id == R.id.time_sales_button) {
            Actions.startActivity(MarketIndexActivity.this, TimeSalesActivity.class, false);

        } else if (id == R.id.tops_button) {
            //Actions.startActivity(MarketIndexActivity.this, TopsPagerActivity.class, false);
            Actions.startActivity(MarketIndexActivity.this, TopsActivity.class, false);
        }

    }

    public void initializeValueList() {
        valueListAdapter = new ValuesGridRecyclerAdapter(MarketIndexActivity.this, allValueItems);
        rvValueList.setAdapter(valueListAdapter);
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

    private ArrayList<Sector> GetPriceTickerData(String JsonString) throws JSONException {

        ArrayList<Sector> sectors = new ArrayList<>();

        JSONObject object = new JSONObject(JsonString);

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");

        if (success.equals("Success")) {
            try {
                JSONArray jarray = object.getJSONArray("Items");
                for (int i = 0; i < jarray.length(); i++) {

                    Sector sectorIndex = new Sector();
                    JSONObject json_data_sector = jarray.getJSONObject(i);
                    JSONObject json_data = json_data_sector.getJSONObject("Sector");
                    sectorIndex.setNameAr(json_data.getString("NameAr"));
                    sectorIndex.setNameEn(json_data.getString("NameEn"));
                    sectorIndex.setSectorID(json_data.getString("ID"));

                    if (!sectorIndex.getNameAr().equals("null") && !sectorIndex.getNameEn().equals("null") && !sectorIndex.getSectorID().equals("null")) {

                        tickerSectorsObject.add(sectorIndex);
                    }

                    JSONArray jarraystocks = json_data_sector.getJSONArray("StockList");
                    for (int j = 0; j < jarraystocks.length(); j++) {
                        Stock stock = new Stock();
                        JSONObject json_data_stock = jarraystocks.getJSONObject(j);

                        stock.setSymbolEn(json_data_stock.getString("SymbolEn"));
                        stock.setSymbolAr(json_data_stock.getString("SymbolAr"));
                        stock.setChange(json_data_stock.getString("Change"));
                        stock.setTrades(json_data_stock.getString("Trades"));
                        stock.setLast(json_data_stock.getString("Last"));
                        stock.setSectorID(sectorIndex.getSectorID() + "");
                        tickerSectorsObject.add(stock);
                    }

                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        }
        return sectors;
    }

    private ArrayList<ValueItem> GetSectorDetails(String JsonString) throws JSONException {

        JSONObject object = new JSONObject(JsonString);
        allValueItems.clear();

        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {
            try {
                JSONObject json_data = object.getJSONObject("SectorDetails");

                sector.setChange(json_data.getString("Change"));
                sector.setChangePercent(json_data.getString("ChangePercent"));
                sector.setSectorID(json_data.getString("SectorID"));
                sector.setNameAr(MyApplication.lang == MyApplication.ARABIC ? json_data.getString("NameAr") : json_data.getString("NameEn"));
                sector.setNameEn(MyApplication.lang == MyApplication.ARABIC ? json_data.getString("NameAr") : json_data.getString("NameEn"));
                sector.setValue(json_data.getString("Value"));

                String securitiesDown = "";
                String securitiesNbTraded = "";
                String securitiesUp = "";

                try {
                    securitiesDown = json_data.getString("SecuritiesDown");
                } catch (Exception e) {
                }

                try {
                    securitiesNbTraded = json_data.getString("SecuritiesNbTraded");
                    sector.setSecuritiesNbTraded(json_data.getString("SecuritiesNbTraded"));
                } catch (Exception e) {
                }

                try {
                    securitiesUp = json_data.getString("SecuritiesUp");
                } catch (Exception e) {
                }

                sector.setSecuritiesDown(securitiesDown);
                sector.setSecuritiesNbTraded(securitiesNbTraded);
                sector.setSecuritiesUp(securitiesUp);

                JSONArray json_fields = json_data.getJSONArray("Field");
                for (int i = 0; i < json_fields.length(); i++) {

                    JSONObject field = json_fields.getJSONObject(i);
                    ValueItem stockValueItem = new ValueItem(field.getString("Key"), field.getString("Value"));
                    allValueItems.add(stockValueItem);
                }
                ValueItem SecuritiesDownValueItem = new ValueItem(MyApplication.lang == MyApplication.ENGLISH ? "Down" : "المنخفض", securitiesDown);
                allValueItems.add(SecuritiesDownValueItem);
                ValueItem SecuritiesNbTradedValueItem = new ValueItem(MyApplication.lang == MyApplication.ENGLISH ? "Traded" : "المتداول"
                        , securitiesNbTraded);
                allValueItems.add(SecuritiesNbTradedValueItem);
                ValueItem SecuritiesUpValueItem = new ValueItem(MyApplication.lang == MyApplication.ENGLISH ? "Up" : "المرتفع"
                        , securitiesUp);
                allValueItems.add(SecuritiesUpValueItem);

            } catch (Exception e) {
                Log.d("GetSectorDetails error", "Exception : " + e.getMessage());
            }
        }
        return allValueItems;
    }

    //<editor-fold desc="Prices Ticker Two Objct"
    private void animateTicker(ArrayList<Object> sectors) {

        ArrayList<Stock> stocks = new ArrayList<>();
        for (int j = 0; j < sectors.size(); j++) {
            if (sectors.get(j) instanceof Stock) {
                stocks.add((Stock) sectors.get(j));
            }
        }

        if (stocks.size() == 1) {

            setAnimatedValues(stocks.get(0), new Stock());
        } else {

            i = 0;
            int delay = 2500;

            try {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Stock firstObject;
                        Stock secondObject;

                        try {
                            firstObject = stocks.get(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            firstObject = new Stock();
                            firstObject.setSectorID("-1");
                        }

                        try {
                            secondObject = stocks.get(i + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            secondObject = new Stock();
                            secondObject.setSectorID("-1");
                        }

                        setAnimatedValues(firstObject, secondObject);

                        try {
                            i += 2;
                            if (i > stocks.size() - 1) {
                                i = 0;
                                handler.removeCallbacks(this);
                                getPriceTickerData = new GetPriceTickerData();
                                getPriceTickerData.executeOnExecutor(MyApplication.threadPoolExecutor, "0");
                            } else {
                                try {
                                    handler.postDelayed(this, delay);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setAnimatedValues(Stock firstObject, Stock secondObject) {

        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(500);
        //anim.setRepeatCount(1);
        //anim.setRepeatMode(Animation.REVERSE);
        tvNewTicker.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                setTickerTextView(firstObject, secondObject);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        /*enterAnim = AnimationUtils.loadAnimation(this, R.anim.enter_from_left);
        enterAnim.setDuration(500);
        enterAnim.setRepeatCount(1);
        enterAnim.setRepeatMode(Animation.REVERSE);

        exitAnim = AnimationUtils.loadAnimation(this, R.anim.exit_to_right);
        exitAnim.setDuration(500);
        exitAnim.setRepeatCount(1);
        exitAnim.setRepeatMode(Animation.REVERSE);

        tvNewTicker.startAnimation(enterAnim);

        enterAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                setTickerTextView(firstObject, secondObject, thirdObject);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Handler handler = new Handler();
                handler.postDelayed(() -> tvNewTicker.startAnimation(exitAnim), 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

                //setTickerTextView(firstObject, secondObject, thirdObject);
            }
        });

        exitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                tvNewTicker.setText("");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

                //setTickerTextView(firstObject, secondObject, thirdObject);
            }
        });*/

    }

    private void setTickerTextView(Stock firstObject, Stock secondObject) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            tvNewTicker.setText(Html.fromHtml(setTradeInfo(firstObject, secondObject), Html.FROM_HTML_MODE_LEGACY));
        } else {

            tvNewTicker.setText(Html.fromHtml(setTradeInfo(firstObject, secondObject)));
        }
    }

    private String setTradeInfo(Stock firstObject, Stock secondObject) {

        String orders = "";
        ArrayList<Stock> StockData = new ArrayList<>();
        StockData.add(firstObject);
        StockData.add(secondObject);

        try {

            for (int i = 0; i < StockData.size(); i++) {

                if (!StockData.get(i).getSectorID().equals("-1")) {

                    if (MyApplication.lang == MyApplication.ENGLISH)
                        orders += "\t\t" + StockData.get(i).getSymbolEn();
                    else
                        orders += "\t\t" + StockData.get(i).getSymbolAr();

                    orders += "  ";
                    orders += " <b>" + StockData.get(i).getLast() + "</b> |";

                    if (StockData.get(i).getChange().contains("-")) {

                        orders = orders + "<b><font color='#ef4e50'>" + " " + StockData.get(i).getChange() + " " + "</b></font>";

                    } else if (StockData.get(i).getChange().equals("0") || StockData.get(i).getChange().equals("0.0")) {

                        orders = orders + "<b><font color='#F29C44'>" + " " + StockData.get(i).getChange() + " " + "</b></font>";
                    } else {

                        orders = orders + "<b><font color='#4bba73'>" + " " + StockData.get(i).getChange() + " " + "</b></font>";
                    }

                    orders += "    ";
                }
            }


            /*if (!secondObject.getSectorID().equals("-1")) {

                if (MyApplication.lang == MyApplication.ENGLISH)
                    orders += secondObject.getSymbolEn();
                else
                    orders += secondObject.getSymbolAr();

                orders += "  ";
                orders += " <b>" + secondObject.getLast() + "</b> |";

                if (secondObject.getChange().contains("-")) {

                    orders = orders + "<b><font color='#ef4e50'>" + " " + secondObject.getChange() + " " + "</b></font>";

                } else if (secondObject.getChange().equals("0") || secondObject.getChange().equals("0.0")) {

                    orders = orders + "<b><font color='#F29C44'>" + " " + secondObject.getChange() + " " + "</b></font>";
                } else {

                    orders = orders + "<b><font color='#4bba73'>" + " " + secondObject.getChange() + " " + "</b></font>";
                }

                orders += "    ";
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }
    //</editor-fold>

    //<editor-fold desc="TimeSales Ticker Two Objc"
    private void animateTimesalesTicker(ArrayList<TimeSale> timeSales) {

        if (timeSales.size() == 1) {

            setTimesaleAnimatedValues(timeSales.get(0), new TimeSale());
        } else {

            j = 0;
            int delay = 2500;

            timehandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    TimeSale firstObject;
                    TimeSale secondObject;

                    try {
                        firstObject = timeSales.get(j);
                        firstObject.setId("1");
                    } catch (Exception e) {
                        e.printStackTrace();
                        firstObject = new TimeSale();
                        firstObject.setId("-1");
                    }

                    try {
                        secondObject = timeSales.get(j + 1);
                        secondObject.setId("2");
                    } catch (Exception e) {
                        e.printStackTrace();
                        secondObject = new TimeSale();
                        secondObject.setId("-1");
                    }

                    setTimesaleAnimatedValues(firstObject, secondObject);

                    try {
                        j += 2;
                        Log.wtf("animateTimesalesTicker", "j = " + j);
                        Log.wtf("animateTimesalesTicker", "timeSales.size() = " + timeSales.size());

                        if (j >= timeSales.size()) {
                            j = 0;
                            timehandler.removeCallbacks(this);
                            getTradeTickerData = new GetTradeTickerData();
                            getTradeTickerData.executeOnExecutor(MyApplication.threadPoolExecutor);
                        } else {
                            try {
                                timehandler.postDelayed(this, delay);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, delay);
        }
    }

    //<editor-fold desc="Prices Ticker"
    /*
    private void animateTicker(ArrayList<Object> sectors) {

        ArrayList<Stock> stocks = new ArrayList<>();
        for (int j = 0; j < sectors.size(); j++) {
            if (sectors.get(j) instanceof Stock) {
                stocks.add((Stock) sectors.get(j));
            }
        }

        if (stocks.size() == 1) {

            setAnimatedValues(stocks.get(0), new Stock(), new Stock());

        } else {

            i = 0;
            int delay = 1000;

            try {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Stock firstObject;
                        Stock secondObject;
                        Stock thirdObject;

                        try {
                            firstObject = stocks.get(i);
                        } catch (Exception e) {
                            e.printStackTrace();
                            firstObject = new Stock();
                            firstObject.setSectorID("-1");
                        }

                        try {
                            secondObject = stocks.get(i + 1);
                        } catch (Exception e) {
                            e.printStackTrace();
                            secondObject = new Stock();
                            secondObject.setSectorID("-1");
                        }

                        try {
                            thirdObject = stocks.get(i + 2);
                        } catch (Exception e) {
                            e.printStackTrace();
                            thirdObject = new Stock();
                            thirdObject.setSectorID("-1");
                        }

                        setAnimatedValues(firstObject, secondObject, thirdObject);
                        i += 3;
                        if (i >= stocks.size() - 1) {

                            i = 0;
                            try {
                                handler.removeCallbacks(this);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //getPriceTickerData = new GetPriceTickerData();
                            //getPriceTickerData.executeOnExecutor(MyApplication.threadPoolExecutor, "0");
                        }

                        try {
                            handler.postDelayed(this, 3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, delay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Animation enterAnim, exitAnim;

    private void setAnimatedValues(Stock firstObject, Stock secondObject, Stock thirdObject) {

        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(500);
        //anim.setRepeatCount(1);
        //anim.setRepeatMode(Animation.REVERSE);
        tvNewTicker.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                setTickerTextView(firstObject, secondObject, thirdObject);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        /*enterAnim = AnimationUtils.loadAnimation(this, R.anim.enter_from_left);
        enterAnim.setDuration(500);
        enterAnim.setRepeatCount(1);
        enterAnim.setRepeatMode(Animation.REVERSE);

        exitAnim = AnimationUtils.loadAnimation(this, R.anim.exit_to_right);
        exitAnim.setDuration(500);
        exitAnim.setRepeatCount(1);
        exitAnim.setRepeatMode(Animation.REVERSE);

        tvNewTicker.startAnimation(enterAnim);

        enterAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                setTickerTextView(firstObject, secondObject, thirdObject);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                Handler handler = new Handler();
                handler.postDelayed(() -> tvNewTicker.startAnimation(exitAnim), 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

                //setTickerTextView(firstObject, secondObject, thirdObject);
            }
        });

        exitAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                tvNewTicker.setText("");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

                //setTickerTextView(firstObject, secondObject, thirdObject);
            }
        });*//*

    }

    private void setTickerTextView(Stock firstObject, Stock secondObject, Stock thirdObject) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            tvNewTicker.setText(Html.fromHtml(setTradeInfo(firstObject, secondObject, thirdObject), Html.FROM_HTML_MODE_LEGACY));
        } else {

            tvNewTicker.setText(Html.fromHtml(setTradeInfo(firstObject, secondObject, thirdObject)));
        }
    }

    private String setTradeInfo(Stock firstObject, Stock secondObject, Stock thirdObject) {

        String orders = "";

        try {
            if (!firstObject.getSectorID().equals("-1")) {

                if (MyApplication.lang == MyApplication.ENGLISH)
                    orders += firstObject.getSymbolEn();
                else
                    orders += firstObject.getSymbolAr();

                orders += "  ";
                orders += " <b>" + firstObject.getLast() + "</b> |";

                if (firstObject.getChange().contains("-")) {

                    orders = orders + "<b><font color='#ef4e50'>" + " " + firstObject.getChange() + " " + "</b></font>";

                } else if (firstObject.getChange().equals("0") || firstObject.getChange().equals("0.0")) {

                    orders = orders + "<b><font color='#F29C44'>" + " " + firstObject.getChange() + " " + "</b></font>";
                } else {

                    orders = orders + "<b><font color='#4bba73'>" + " " + firstObject.getChange() + " " + "</b></font>";
                }

                orders += "    ";
            }


            if (!secondObject.getSectorID().equals("-1")) {

                if (MyApplication.lang == MyApplication.ENGLISH)
                    orders += secondObject.getSymbolEn();
                else
                    orders += secondObject.getSymbolAr();

                orders += "  ";
                orders += " <b>" + secondObject.getLast() + "</b> |";

                if (secondObject.getChange().contains("-")) {

                    orders = orders + "<b><font color='#ef4e50'>" + " " + secondObject.getChange() + " " + "</b></font>";

                } else if (secondObject.getChange().equals("0") || secondObject.getChange().equals("0.0")) {

                    orders = orders + "<b><font color='#F29C44'>" + " " + secondObject.getChange() + " " + "</b></font>";
                } else {

                    orders = orders + "<b><font color='#4bba73'>" + " " + secondObject.getChange() + " " + "</b></font>";
                }

                orders += "    ";
            }


            if (!thirdObject.getSectorID().equals("-1")) {

                if (MyApplication.lang == MyApplication.ENGLISH)
                    orders += thirdObject.getSymbolEn();
                else
                    orders += thirdObject.getSymbolAr();

                orders += "  ";
                orders += " <b>" + thirdObject.getLast() + "</b> |";

                if (thirdObject.getChange().contains("-")) {

                    orders = orders + "<b><font color='#ef4e50'>" + " " + thirdObject.getChange() + " " + "</b></font>";

                } else if (thirdObject.getChange().equals("0") || thirdObject.getChange().equals("0.0")) {

                    orders = orders + "<b><font color='#F29C44'>" + " " + thirdObject.getChange() + " " + "</b></font>";
                } else {

                    orders = orders + "<b><font color='#4bba73'>" + " " + thirdObject.getChange() + " " + "</b></font>";
                }

                orders += "    ";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return orders;
    }
    */
    //</editor-fold>

    private void setTimesaleAnimatedValues(TimeSale firstObject, TimeSale secondObject) {

        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(500);
        //anim.setRepeatCount(1);
        //anim.setRepeatMode(Animation.REVERSE);
        tvTradesTicker.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                setTimesaleTickerTextView(firstObject, secondObject);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void setTimesaleTickerTextView(TimeSale firstObject, TimeSale secondObject) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            tvTradesTicker.setText(Html.fromHtml(setTimesaleTradeInfo(firstObject, secondObject), Html.FROM_HTML_MODE_LEGACY));
            //Log.wtf("setTimesaleTickerTextView tvNewTicker >= N","setText : " + setTimesaleTradeInfo(firstObject, secondObject));
        } else {

            tvTradesTicker.setText(Html.fromHtml(setTimesaleTradeInfo(firstObject, secondObject)));
            //Log.wtf("setTimesaleTickerTextView tvNewTicker <= N","setText : " + setTimesaleTradeInfo(firstObject, secondObject));
        }
    }

    private String setTimesaleTradeInfo(TimeSale firstObject, TimeSale secondObject) {

        String trades = "";
        ArrayList<TimeSale> Data = new ArrayList<>();
        Data.add(firstObject);
        Data.add(secondObject);

        try {

            for (int i = 0; i < Data.size(); i++) {

                if (!Data.get(i).getId().equals("-1") && !Data.get(i).getId().equals("0")) {

                    if (MyApplication.lang == MyApplication.ENGLISH)
                        trades += "  \t\t" + Data.get(i).getStockSymbolEn();
                    else
                        trades += "  \t\t" + Data.get(i).getStockSymbolAr();

                    trades += "  ";

                    //trades = trades + "(" + timeSale.getChange() + ")";

                    trades = trades + " <b>" + Data.get(i).getQuantity() + "</b> |";

                    trades += " <b>" + Data.get(i).getPrice() + "</b> ";

                /*if (timeSale.getChange().contains("-"))

                    trades = trades + "<font color='#ef4e50'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                else {

                    trades = trades + "<font color='#4bba73'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                }*/

                    trades += "    ";
                }
            }


            /*if (!thirdObject.getId().equals("-1") && !thirdObject.getId().equals("0")) {

                if (MyApplication.lang == MyApplication.ENGLISH)
                    trades += "  \t" + thirdObject.getStockSymbolEn();
                else
                    trades += "  \t" + thirdObject.getStockSymbolAr();

                trades += "  ";

                //trades = trades + "(" + timeSale.getChange() + ")";

                trades = trades + " <b>" + thirdObject.getQuantity() + "</b> |";

                trades += " <b>" + thirdObject.getPrice() + "</b> ";

                *//*if (timeSale.getChange().contains("-"))

                    trades = trades + "<font color='#ef4e50'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                else {

                    trades = trades + "<font color='#4bba73'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                }*//*

                trades += "    ";
            }*/

        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("setTimesaleTradeInfo", "error : " + e.getMessage());
        }

        return trades;
    }

    public void back(View v) {
        finish();
    }
    //</editor-fold>

    private class MyValueFormatter implements YAxisValueFormatter {


        public MyValueFormatter() {

        }


        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return String.valueOf(value).replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "4")
                    .replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6")
                    .replaceAll("٧", "8").replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0");
        }
    }
    //<editor-fold desc="TimeSales Ticker"
     /*private void animateTimesalesTicker(ArrayList<TimeSale> timeSales) {


        if (timeSales.size() == 1) {

            setTimesaleAnimatedValues(timeSales.get(0), new TimeSale(), new TimeSale());

        } else {

            j = 0;
            int delay = 3000;

            timehandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    TimeSale firstObject;
                    TimeSale secondObject;
                    TimeSale thirdObject;

                    try {
                        firstObject = timeSales.get(j);
                        firstObject.setId("1");
                    } catch (Exception e) {
                        e.printStackTrace();
                        firstObject = new TimeSale();
                        firstObject.setId("-1");
                    }

                    try {
                        secondObject = timeSales.get(j + 1);
                        secondObject.setId("2");
                    } catch (Exception e) {
                        e.printStackTrace();
                        secondObject = new TimeSale();
                        secondObject.setId("-1");
                    }

                    try {
                        thirdObject = timeSales.get(j + 2);
                        thirdObject.setId("3");
                    } catch (Exception e) {
                        e.printStackTrace();
                        thirdObject = new TimeSale();
                        thirdObject.setId("-1");
                    }

                    setTimesaleAnimatedValues(firstObject, secondObject, thirdObject);

                    try {
                        j += 3;
                        Log.wtf("animateTimesalesTicker","j = " + j);
                        Log.wtf("animateTimesalesTicker","timeSales.size() = " + timeSales.size());

                        if (j >= timeSales.size()) {
                            j = 0;
                            timehandler.removeCallbacks(this);
                            //allTrades.clear();
                            getTradeTickerData = new GetTradeTickerData();
                            getTradeTickerData.executeOnExecutor(MyApplication.threadPoolExecutor);
                        }
                        else{
                            try {
                                timehandler.postDelayed(this, delay);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //timehandler.postDelayed(this, 2500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    *//*try {
                        timehandler.postDelayed(this, 2500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*//*
                }
            }, delay);
        }
    }

    private void setTimesaleAnimatedValues(TimeSale firstObject, TimeSale secondObject, TimeSale thirdObject) {

        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(500);
        //anim.setRepeatCount(1);
        //anim.setRepeatMode(Animation.REVERSE);
        tvNewTicker.startAnimation(anim);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                setTimesaleTickerTextView(firstObject, secondObject, thirdObject);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private void setTimesaleTickerTextView(TimeSale firstObject, TimeSale secondObject, TimeSale thirdObject) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            tvTradesTicker.setText(Html.fromHtml(setTimesaleTradeInfo(firstObject, secondObject, thirdObject), Html.FROM_HTML_MODE_LEGACY));
            Log.wtf("setTimesaleTickerTextView tvNewTicker >= N","setText : " + setTimesaleTradeInfo(firstObject, secondObject, thirdObject));
        } else {

            tvTradesTicker.setText(Html.fromHtml(setTimesaleTradeInfo(firstObject, secondObject, thirdObject)));
            Log.wtf("setTimesaleTickerTextView tvNewTicker <= N","setText : " + setTimesaleTradeInfo(firstObject, secondObject, thirdObject));
        }
    }

    private String setTimesaleTradeInfo(TimeSale firstObject, TimeSale secondObject, TimeSale thirdObject) {

        String trades = "";
        ArrayList<TimeSale> Data = new ArrayList<>();
        Data.add(firstObject);
        Data.add(secondObject);
        Data.add(thirdObject);

        try {

            for(int i = 0;i<Data.size();i++){

                if (!Data.get(i).getId().equals("-1") && !Data.get(i).getId().equals("0")) {

                    if (MyApplication.lang == MyApplication.ENGLISH)
                        trades += "  \t" + Data.get(i).getStockSymbolEn();
                    else
                        trades += "  \t" + Data.get(i).getStockSymbolAr();

                    trades += "  ";

                    //trades = trades + "(" + timeSale.getChange() + ")";

                    trades = trades + " <b>" + Data.get(i).getQuantity() + "</b> |";

                    trades += " <b>" + Data.get(i).getPrice() + "</b> ";

                *//*if (timeSale.getChange().contains("-"))

                    trades = trades + "<font color='#ef4e50'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                else {

                    trades = trades + "<font color='#4bba73'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                }*//*

                    trades += "    ";
                }
            }


            *//*if (!thirdObject.getId().equals("-1") && !thirdObject.getId().equals("0")) {

                if (MyApplication.lang == MyApplication.ENGLISH)
                    trades += "  \t" + thirdObject.getStockSymbolEn();
                else
                    trades += "  \t" + thirdObject.getStockSymbolAr();

                trades += "  ";

                //trades = trades + "(" + timeSale.getChange() + ")";

                trades = trades + " <b>" + thirdObject.getQuantity() + "</b> |";

                trades += " <b>" + thirdObject.getPrice() + "</b> ";

                *//**//*if (timeSale.getChange().contains("-"))

                    trades = trades + "<font color='#ef4e50'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                else {

                    trades = trades + "<font color='#4bba73'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                }*//**//*

                trades += "    ";
            }*//*

        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("setTimesaleTradeInfo","error : " + e.getMessage());
        }

        return trades;
    }*/
    //</editor-fold>

    private class GetSectorChartData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetSectorChartData.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("sectorId", "0000");
            parameters.put("key", getResources().getString(R.string.beforekey));
            Log.wtf("MarketIndex", "Call Request GetSectorChartData");


            while (running) {
                try {
                    result = ConnectionRequests.GET(url, MarketIndexActivity.this, parameters);
                    //Log.wtf("MarketIndex MyApplication.GetSectorChartData.getValue()","result" + result);
                } catch (Exception e) {
                    e.printStackTrace();

                    if (!result.equals("")) {
                        if (MyApplication.isDebug) {
                            runOnUiThread(new Runnable() {
                                public void run() {
//                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetSectorChartData.getKey() + " request chart's", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }

                try {
                    chartdata = GlobalFunctions.GetChartData(true, result);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!result.equals("")) {
                        if (MyApplication.isDebug) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetSectorChartData.getKey() + " parsing chart's", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }

                publishProgress(params);
                SystemClock.sleep(15000);
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            try {

                if (chartdata.getChartvalues().size() == 1 && chartdata.getChartvalues().get(0).getValue() == 0) {

                    mChart.setNoDataText(getString(R.string.noData));
                } else {

                    //<editor-fold desc="normal case">
                    if (chartdata.getChartvalues().size() != 0) {
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


                                YAxis yAxis = mChart.getAxisLeft(); // get the left or right axis
                                yAxis.setStartAtZero(false);
                                yAxis.setSpaceBottom(20f);
                                yAxis.setValueFormatter(new MyValueFormatter());
                                yAxis.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, MyApplication.mshared.getBoolean(MarketIndexActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));

                                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//                                set1.setColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.colorDark));
                                set1.setColor(ContextCompat.getColor(MarketIndexActivity.this, MyApplication.mshared.getBoolean(MarketIndexActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));

                                set1.setValueTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.colorDark));

                                set1.setLineWidth(3f);
                                set1.setDrawCircles(false);
                                set1.setDrawValues(false);

                                set1.setFillAlpha(65);
                                //set1.setFillColor(ColorTemplate.getHoloBlue());
                                set1.setFillColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.colorDark));
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
                                mChart.setOnChartGestureListener(MarketIndexActivity.this);
                                mChart.setOnChartValueSelectedListener(MarketIndexActivity.this);
                                //    mChart.setDescriptionTypeface(MyApplication.giloryItaly);

                                MyMarkerView mv = new MyMarkerView(MarketIndexActivity.this, R.layout.custom_marker_view, chartdata);

                                mv.setKeepScreenOn(true);

                                mChart.setMarkerView(mv);
                                // Set the marker to the chart

                                //    mChart.getY().setTypeface(MyApplication.giloryItaly);


                                mChart.setData(data);
                                mChart.getAxisLeft().setTypeface(MyApplication.giloryItaly);

                                //hsen remove dot
                                mChart.getLegend().setEnabled(false);
                                mChart.getAxisLeft().setStartAtZero(false);
                                mChart.invalidate();


                                mChart.getXAxis().setTypeface(MyApplication.giloryItaly);
                                mChart.getAxisRight().setTypeface(MyApplication.giloryItaly);

                                XAxis xAxis = mChart.getXAxis();
                                xAxis.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, MyApplication.mshared.getBoolean(MarketIndexActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));

//                                mChart.getAxisRight().setTextColor(ContextCompat.getColor(MarketIndexActivity.this, MyApplication.mshared.getBoolean(MarketIndexActivity.this.getResources().getString(R.string.normal_theme), true) ?  R.color.colorDark  : R.color.colorValuesInv));

                                //  mChart.setVisibleXRangeMaximum(10);
                                mChart.getLayoutParams().height = MyApplication.screenWidth / 2;
                            }
                        });

                    } else {

                        mChart.setNoDataText(getString(R.string.noData));
                    }
                    //</editor-fold>
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    //<editor-fold desc="async tasks and their description">
    private class GetTradeTickerData extends AsyncTask<Void, Void, String> {

        String trades;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            trades = "";
            allTrades.clear();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetTradeTickerData.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("key", getString(R.string.beforekey));
            Log.wtf("MarketIndex", "Call Request GetTradeTickerData");
            Log.wtf("GetTradeTickerData", "url : " + url);
            Log.wtf("GetTradeTickerData", "parameters : " + parameters);
            parameters.put("MarketId", MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, MarketIndexActivity.this, parameters);
                //Log.wtf("result"+MyApplication.GetTradeTickerData.getValue()," = " + result);
                allTrades.addAll(GlobalFunctions.GetTimeSales(result,false));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetTradeTickerData.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.wtf("======== Trade", "Ticker");
            Log.wtf("======== allTrades size", allTrades.size() + "");

            /*trades += "\t\t";
            for (int i = 0; i < allTrades.size(); i++) {

                TimeSale timeSale = allTrades.get(i);

                if (MyApplication.lang == MyApplication.ENGLISH)
                    trades += "  \t" + timeSale.getStockSymbolEn();
                else
                    trades += "  \t" + timeSale.getStockSymbolAr();

                trades += "  ";

                //trades = trades + "(" + timeSale.getChange() + ")";

                trades = trades + " <b>" + timeSale.getQuantity() + "</b> |";

                trades += " <b>" + timeSale.getPrice() + "</b> ";

                *//*if (timeSale.getChange().contains("-"))

                    trades = trades + "<font color='#ef4e50'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                else {

                    trades = trades + "<font color='#4bba73'>" + "(" + timeSale.getQuantity() + ")" + "</font>";
                }*//*

                trades += "    ";
                //Log.wtf("trades",": " + trades);
            }*/

            /*tvTradesTicker.setText((Html.fromHtml(trades)));
            tvTradesTicker.setSelected(true);
            tvTradesTicker.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            tvTradesTicker.setSingleLine(true);
            setMarqueeSpeed(tvTradesTicker, tickerSpeed);*/

            try {
                if (allTrades.size() > 0) {
                    animateTimesalesTicker(allTrades);
                }
            } catch (Exception e) {
                Log.wtf("animateTimesalesTicker", "error : " + e.getMessage());
            }

        }
    }

    private class GetPriceTickerData extends AsyncTask<String, Void, Void> {

        String ticker;
        Handler hHandler;
        int scroll_pos = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tickerSectorsObject.clear();
            ticker = "";
        }

        @Override
        protected Void doInBackground(String... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetPriceTickerData.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("sectorID", selectedSectorID);
            parameters.put("key", getString(R.string.beforekey));
            parameters.put("MarketId", MyApplication.marketID);

            try {

                result = ConnectionRequests.GET(url, MarketIndexActivity.this, parameters);
                //Log.wtf("result"+MyApplication.GetPriceTickerData.getValue()," = " + result);
                sectorsTicker.addAll(GetPriceTickerData(result));
                Log.wtf("GetPriceTickerData", "sectorsTicker size = " + sectorsTicker.size());

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetPriceTickerData.getKey(), Toast.LENGTH_LONG).show();
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
                Log.wtf("tickerSector size", tickerSectorsObject.size() + "");
                Log.wtf("sectorsTicker size", sectorsTicker.size() + "");
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (selectedSectorID.equals("0")) {
                sectorsTicker.clear();
                Sector fake = new Sector();
                fake.setSectorID("-1");
                fake.setNameAr(getString(R.string.all_sectors));
                fake.setNameEn(getString(R.string.all_sectors));

                ticker += "\t\t";

                ticker += getString(R.string.all_sectors);

                ticker += "\t\t";

                sectorsTicker.add(0, fake);
                Log.wtf("fake add sectorsTicker size", sectorsTicker.size() + "");

                if (sectorsTicker.size() <= 1) {

                    for (int i = 0; i < tickerSectorsObject.size(); i++) {

                        if (tickerSectorsObject.get(i) instanceof Sector) {
                            sectorsTicker.add((Sector) tickerSectorsObject.get(i));
                            Log.wtf("", "");
                        } else {

                            Stock s = (Stock) tickerSectorsObject.get(i);

                            if (MyApplication.lang == MyApplication.ENGLISH)
                                ticker += " \t" + s.getSymbolEn();
                            else
                                ticker += " \t" + s.getSymbolAr();

                            ticker += "  <b>" + s.getLast() + "</b> |";

                            if (s.getChange().contains("-")) {

                                if (MyApplication.lang == MyApplication.ARABIC) {

                                    ticker = ticker + "<b><font color='#ef4e50'>" + "  " + s.getChange().replace("-", "") + "- " + "</font></b>";
                                } else {

                                    ticker = ticker + "<b><font color='#ef4e50'>" + "  " + s.getChange() + " " + "</font></b>";
                                }

                            } else {

                                ticker = ticker + "<b><font color='#4bba73'>" + "  " + s.getChange() + " " + "</font></b>";
                            }

                            ticker += "    ";
                        }

                    }
                }
            } else {

                ticker += "  ";
                for (int i = 0; i < tickerSectorsObject.size(); i++) {
                    if (tickerSectorsObject.get(i) instanceof Sector)
                        Log.wtf("", ""); // sectorsTicker.add((Sector) tickerSectorsObject.get(i));
                    else {

                        Stock s = (Stock) tickerSectorsObject.get(i);

                        if (MyApplication.lang == MyApplication.ENGLISH)
                            ticker += s.getSymbolEn();
                        else
                            ticker += s.getSymbolAr();

                        ticker += "  ";
                        ticker += " <b>" + s.getLast() + "</b> |";

                        if (s.getChange().contains("-")) {

                            ticker = ticker + "<b><font color='#ef4e50'>" + " " + s.getChange() + " " + "</b></font>";

                        } else if (s.getChange().equals("0") || s.getChange().equals("0.0")) {

                            ticker = ticker + "<b><font color='#F29C44'>" + " " + s.getChange() + " " + "</b></font>";
                        } else {

                            ticker = ticker + "<b><font color='#4bba73'>" + " " + s.getChange() + " " + "</b></font>";
                        }

                        ticker += "    ";
                    }
                }
            }

//            tvSectorsTicker.setText((Html.fromHtml(tickerSectorsObject.size() < 1 ? "" : ticker)));
            //tvSectorsTicker.setSelected(true);
            //tvSectorsTicker.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            //tvSectorsTicker.setSingleLine(true);
            //setMarqueeSpeed(tvSectorsTicker, tickerSpeed);

//            sectorSpinnerAdapter.notifyDataSetChanged();

            //<editor-fold desc="old code">
            /*try {
                scroll_pos = (int)tvSectorsTicker.getLayout().getLineWidth(0);
                Log.wtf("*******  scroll_pos", "is " + scroll_pos );

            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("*******  Exception", "is " + e.getMessage() );
            }

            hsScroll.setScrollViewListener(new ScrollViewListener() {
                @Override
                public void onScrollChanged(ScrollViewExt scrollView, int x, int y, int oldx, int oldy) {
                    View view = (View) scrollView.getChildAt(scrollView.getChildCount() - 1);
                    int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollX()));

                    // if diff is zero, then the bottom has been reached
                    if (diff == 0) {
                        Log.wtf("*******  DONE","SCROLLING");
                    }
                }
            });

            hHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                    //hsScroll.scrollTo(scroll_pos, 0);
                    hsScroll.scrollTo(scroll_pos, 0);
                    scroll_pos--;
                    if(scroll_pos >= 0) {

                        hHandler.sendEmptyMessage(0);
                    }
                }
            };
            Message msg = new Message();
            msg.what = 1;
            hHandler.handleMessage(msg);*/
            //</editor-fold>

            Log.wtf("tickerSectorsObject ", " size " + tickerSectorsObject.size());
            handler.removeCallbacksAndMessages(null);
            tvSectorsTicker.setText(". . . . .");

            if (tickerSectorsObject.size() > 2) {

                animateTicker(tickerSectorsObject);
            } else if (tickerSectorsObject.size() > 0) {

                Stock firstObject;
                Stock secondObject;
                ArrayList<Stock> stocks = new ArrayList<>();
                for (int j = 0; j < tickerSectorsObject.size(); j++) {
                    if (tickerSectorsObject.get(j) instanceof Stock) {
                        stocks.add((Stock) tickerSectorsObject.get(j));
                    }
                }

                try {
                    firstObject = stocks.get(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    firstObject = new Stock();
                    firstObject.setSectorID("-1");
                }

                try {
                    secondObject = stocks.get(i + 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    secondObject = new Stock();
                    secondObject.setSectorID("-1");
                }

                setAnimatedValues(firstObject, secondObject);
            }
        }
    }

    private class GetSectorIndex extends AsyncTask<Void, String, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            allValueItems.clear();
            sector = new Sector();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.LoadSectorDetails.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("sectorId", "0000");
            parameters.put("Lang", MyApplication.lang == MyApplication.ENGLISH ? "English" : "Arabic");
            parameters.put("key", getResources().getString(R.string.beforekey));
            parameters.put("MarketId", MyApplication.marketID);

            try {

                while (running) {

                    if (isCancelled())
                        break;

                    result = ConnectionRequests.GET(url, MarketIndexActivity.this, parameters);

                    Log.wtf("stcoksss", "are " + result);

                    publishProgress(result);
                    try {

                        Thread.sleep(15000);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.LoadSectorDetails.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {

                allValueItems.clear();
                allValueItems = GetSectorDetails(values[0]);

                try {
                    title.setText(sector.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    value.setText(sector.getChange());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (sector != null && sector.getChangePercent() != null) {
                    try {
                        double changes = Double.parseDouble(sector.getChangePercent().split("%")[0]);
                        if (changes == 0) {
                            ivImage.setVisibility(View.GONE);
                        } else {
                            ivImage.setVisibility(View.VISIBLE);
                            ivImage.setImageResource(changes > 0 ? R.drawable.up_green_arrow : R.drawable.down_red_arrow);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                double change = 0;

                String changeP = "";
                String changeValue = "";

                try {

                    try {
                        Log.wtf("onCreate", "sector.getChange = " + sector.getChange());
                        changeValue = sector.getChange().substring(1, sector.getChange().length() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        changeValue = "0";
                    }

                    change = Double.parseDouble(changeValue);
                    if (sector.getChangePercent() == null || sector.getChangePercent().equals("null")) {
                        sector.setChangePercent("0");
                    }
                    String changePercent = sector.getChangePercent();
                    changeP = sector.getChangePercent().replace("%", "").trim();
                    secondValue.setText(changePercent);
                } catch (Exception e) {
                    e.printStackTrace();

                    change = 0;
                    String changePercent = sector.getChangePercent();
                    changeP = "0";
                    Log.wtf("EXception: changeP", "is " + changeP);
                    try {
                        secondValue.setText(changePercent);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }

                try {
                    secondValue.setTextDirection(View.TEXT_DIRECTION_LTR);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //<editor-fold desc="comparison on changePercent">
                try {
                    if (Double.parseDouble(changeP) == 0) {

                        title.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.other_market));
                        value.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.other_market));
                        secondValue.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.other_market));
                    } else if (Double.parseDouble(changeP) < 0) {

                        title.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.red_color));
                        value.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.red_color));
                        secondValue.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.red_color));
                    } else {

                        title.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.green_color));
                        value.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.green_color));
                        secondValue.setTextColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.green_color));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //</editor-fold>


                try {
                    if (MyApplication.lang == MyApplication.ARABIC) {
                        title.setTypeface(MyApplication.droidbold);
                        value.setTypeface(MyApplication.droidbold);
                        secondValue.setTypeface(MyApplication.droidbold);
                    } else {
                        title.setTypeface(MyApplication.giloryBold);
                        value.setTypeface(MyApplication.giloryBold);
                        secondValue.setTypeface(MyApplication.giloryBold);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                valueListAdapter.notifyDataSetChanged();
                llmValues = new GridLayoutManager(MarketIndexActivity.this, MyApplication.GRID_VALUES_SPAN_COUNT);//LinearLayoutManager.VERTICAL, false);
                rvValueList.setLayoutManager(llmValues);
                //valueListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
    //</editor-fold>

    private class GetNews extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetNews.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("newsId", "0");
            parameters.put("stockId", "");
            parameters.put("language", ""); //Actions.getLanguage()
            parameters.put("count", "10");
            parameters.put("key", getResources().getString(R.string.beforekey));
            Log.wtf("MarketIndex", "Call Request GetNews");

            try {
                result = ConnectionRequests.GET(url, MarketIndexActivity.this, parameters);
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

                try {
                    tvNewsMore.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    tvNewsHeader.setVisibility(View.VISIBLE);
                    tvNewsMore.setVisibility(View.VISIBLE);
                    rvNews.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                try {
                    tvNewsHeader.setVisibility(View.GONE);
                    tvNewsMore.setVisibility(View.GONE);
                    rvNews.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
