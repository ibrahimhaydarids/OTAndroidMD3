package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.ids.fixot.adapters.FavoritesRecyclerAdapter;
import com.ids.fixot.adapters.InstrumentsRecyclerAdapter;
import com.ids.fixot.adapters.MarketsSpinnerAdapter;
import com.ids.fixot.enums.enums.TradingSession;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.StockQuotation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.ids.fixot.MyApplication.GetFavoriteStocks;
import static com.ids.fixot.MyApplication.lang;

/**
 * Created by user on 9/28/2017.
 */

public class FavoritesActivity extends AppCompatActivity implements FavoritesRecyclerAdapter.RecyclerViewOnItemClickListener, InstrumentsRecyclerAdapter.RecyclerViewOnItemClickListener, MarketStatusListener {

    //    InstrumentsAdapter instrumentsAdapter;
    public static String instrumentId = "";
    public static BroadcastReceiver favMarketReceiver;
    ImageView ivBack;
    Toolbar myToolbar;
    RelativeLayout rootLayout;
    FrameLayout rlStockSearch;
    ArrayList<StockQuotation> favoriteStocks = new ArrayList<>();
    ArrayList<StockQuotation> allStocks = new ArrayList<>();
    ArrayList<StockQuotation> tmpStocks = new ArrayList<>();
    FavoritesRecyclerAdapter adapter;
    SwipeRefreshLayout swipe_container;
    EditText etSearch;
    Button btClear;
    RecyclerView rvStocks;
    GetFavoriteStocks getFavoriteStocks;
    LinearLayoutManager llm;
    Instrument selectedInstrument = new Instrument();
    GetInstruments getInstruments;
    //    TabLayout tlInstrumentItemsTabs;
    LinearLayout llTab;

    RecyclerView rvInstruments;
    InstrumentsRecyclerAdapter instrumentsRecyclerAdapter;
    Spinner spMarkets;
    MarketsSpinnerAdapter marketsSpinnerAdapter;
    TradingSession selectMarket = TradingSession.All;
    ArrayList<TradingSession> AllMarkets = new ArrayList<>();
    ArrayList<Instrument> marketInstruments = new ArrayList<>();
    ArrayList<Instrument> allInstruments = new ArrayList<>();
    Boolean isSelectInstrument = false;

    TextView tvStock, tvSessionName, tvPrice, tvChange, tvLogout;
    ArrayList<Integer> favoritesIds = new ArrayList<>();
    private BroadcastReceiver receiver;
    private boolean started = false;
    private boolean firstLaunch = true;
    private boolean running=false;

    public FavoritesActivity() {
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
        setContentView(R.layout.activity_favorites);
        Actions.initializeBugsTracking(this);

        if (MyApplication.lang == MyApplication.ARABIC) {
            AllMarkets.add(TradingSession.All_ar);
            AllMarkets.add(TradingSession.REG_ar);
            AllMarkets.add(TradingSession.FUNDS_ar);
        } else {
            AllMarkets.add(TradingSession.All);
            AllMarkets.add(TradingSession.REG);
            AllMarkets.add(TradingSession.FUNDS);
        }
        MyApplication.instrumentId = "";

        findViews();
        Actions.initializeToolBar(getString(R.string.favorite), FavoritesActivity.this);
        Actions.showHideFooter(this);

        started = true;

        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }

        Actions.overrideFonts(this, rootLayout, false);


        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);

        //<editor-fold desc="instruments section">

        if (MyApplication.instruments.size() < 2) {

            Actions.initializeInstruments(this);
            getInstruments = new GetInstruments();
            getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);
        } else {

            allInstruments.clear();

            if (!MyApplication.isOTC) {
                for (int i = 1; i < AllMarkets.size(); i++) {
                    allInstruments.addAll(Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, AllMarkets.get(i).getValue()));
                }
            } else {
                allInstruments.addAll(MyApplication.instruments);
            }

            getFavoriteStocks = new GetFavoriteStocks();
            getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
        }
        //</editor-fold>

        Actions.setTypeface(new TextView[]{tvStock, tvSessionName, tvPrice, tvChange}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        tvSessionName.setGravity(MyApplication.lang == MyApplication.ARABIC ? Gravity.LEFT : Gravity.RIGHT);

        /*try { //testing

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MyApplication.isDebug = true;

                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }


    private void findViews() {


        if (MyApplication.isOTC) {
            LinearLayout vs = findViewById(R.id.spMarketLayout);

            ViewGroup.LayoutParams params = vs.getLayoutParams();

            params.width = 0;

            vs.setVisibility(View.INVISIBLE);
        }


        llTab = findViewById(R.id.llTab);
        rlStockSearch = findViewById(R.id.rlStockSearch);
        rootLayout = findViewById(R.id.rootLayout);
        rvStocks = findViewById(R.id.rvStocks);
        swipe_container = findViewById(R.id.swipe_container);
        btClear = rlStockSearch.findViewById(R.id.btClear);
        etSearch = rlStockSearch.findViewById(R.id.etSearch);

        tvStock = findViewById(R.id.tvStock);
        tvSessionName = findViewById(R.id.tvSessionName);
        tvPrice = findViewById(R.id.tvPrice);
        tvChange = findViewById(R.id.tvChange);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(FavoritesActivity.this));
        tvLogout.setVisibility((BuildConfig.GoToMenu) ? View.GONE : View.VISIBLE);


        rvInstruments = findViewById(R.id.RV_instrument);
        spMarkets = findViewById(R.id.spMarket);
        marketsSpinnerAdapter = new MarketsSpinnerAdapter(this, AllMarkets, true);
        spMarkets.setAdapter(marketsSpinnerAdapter);
        spMarkets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMarket = AllMarkets.get(position);
                MyApplication.instrumentId = "";
                isSelectInstrument = false;

                if (selectMarket.getValue() == TradingSession.All.getValue() || MyApplication.isOTC) {
                    marketInstruments = allInstruments;
                } else {
                    marketInstruments = Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, selectMarket.getValue());
                }

                for (Instrument inst : marketInstruments) {
                    inst.setIsSelected(false);
                }

                instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(FavoritesActivity.this, marketInstruments, FavoritesActivity.this);
                rvInstruments.setAdapter(instrumentsRecyclerAdapter);
                Log.wtf("select Market : " + selectMarket.toString(), "instrument count = " + marketInstruments.size());

                Log.wtf("on instr click", "allStocks count = " + allStocks.size());
                retrieveFiltered(false);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rvInstruments.setLayoutManager(new LinearLayoutManager(FavoritesActivity.this, LinearLayoutManager.HORIZONTAL, false));
        instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(this, marketInstruments, this);
        rvInstruments.setAdapter(instrumentsRecyclerAdapter);

        etSearch.setSelected(false);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (arg0.length() == 0) {

                    btClear.setVisibility(View.GONE);

                    try {
                        Actions.closeKeyboard(FavoritesActivity.this);
                    } catch (Exception e) {
                        Log.wtf("catch ", "" + e.getMessage());
                    }

                    adapter = new FavoritesRecyclerAdapter(FavoritesActivity.this, favoriteStocks, FavoritesActivity.this);
                    adapter.notifyDataSetChanged();
                    rvStocks.setAdapter(adapter);
                } else {

                    btClear.setVisibility(View.VISIBLE);
                    adapter.getFilter().filter(arg0);

                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        btClear.setOnClickListener(v -> {
            etSearch.setText("");
            try {
                Actions.closeKeyboard(FavoritesActivity.this);
            } catch (Exception e) {
                Log.wtf("catch ", "" + e.getMessage());
            }
        });

        myToolbar = findViewById(R.id.my_toolbar);
        ivBack = myToolbar.findViewById(R.id.ivBack);
        ivBack.setVisibility((BuildConfig.GoToMenu) ? View.VISIBLE : View.GONE);

        llm = new LinearLayoutManager(this);
        adapter = new FavoritesRecyclerAdapter(this, favoriteStocks, this);
        rvStocks.setAdapter(adapter);
        rvStocks.setLayoutManager(llm);

        swipe_container.setOnRefreshListener(() -> {

            allStocks.clear();
            favoriteStocks.clear();
            adapter.notifyDataSetChanged();
            swipe_container.setRefreshing(false);
            getFavoriteStocks = new GetFavoriteStocks();
            getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
        });
        swipe_container.setVisibility(View.GONE);

    }

    @Override
    public void onItemFavClicked(View v, int position) {

        Bundle b = new Bundle();
        b.putParcelable("stock", adapter.getFilteredItems().get(position));
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(FavoritesActivity.this, StockDetailActivity.class);
        startActivity(i);
    }

    private void changeTabsFont(TabLayout tabLayout, Typeface typeface) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {

                    ((TextView) tabViewChild).setTypeface(typeface);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        running = true;
        Actions.checkSession(this);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);

        Actions.InitializeSessionServiceV2(this);
        //Actions.InitializeMarketServiceV2(this);

        //InitializeMarketServiceLocal();

        Actions.checkLanguage(this, started);
    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            unregisterReceiver(favMarketReceiver);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("unregisterReceiver ex", e.getMessage());
        }

        try {
            getInstruments.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getInstruments);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Instruments ex", e.getMessage());
        }

        try {
            getFavoriteStocks.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getFavoriteStocks);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Favorites ex", e.getMessage());
        }

        try {
            System.gc();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    public void back(View v) {
        finish();
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.GoToMenu) {
            super.onBackPressed();
        }
    }

    @Override
    public void onItemClicked(View v, int position) {

        for (int i = 0; i < marketInstruments.size(); i++) {
            if (i == position) {
                marketInstruments.get(i).setIsSelected(!marketInstruments.get(i).getIsSelected());
                selectedInstrument = marketInstruments.get(i).getIsSelected() ? marketInstruments.get(i) : new Instrument();
                instrumentId = marketInstruments.get(i).getIsSelected() ? selectedInstrument.getInstrumentCode() : "";
                isSelectInstrument = marketInstruments.get(i).getIsSelected();
                MyApplication.instrumentId = instrumentId;
            } else {
                marketInstruments.get(i).setIsSelected(false);
            }
        }

        /*if(allInstruments.get(position).getIsSelected()){ allInstruments.get(position).setIsSelected(false); }
        else{ allInstruments.get(position).setIsSelected(true); }*/

        instrumentsRecyclerAdapter.notifyDataSetChanged();

        allStocks.clear();
        retrieveFiltered(false);
    }

    private void retrieveFiltered(boolean hasSectorId) {
        tmpStocks = new ArrayList<>();
        allStocks = new ArrayList<>();

        tmpStocks = (Actions.getStocksByIds(MyApplication.stockQuotations, favoritesIds));
        tmpStocks = (Actions.filterStocksByInstrumentID(tmpStocks, MyApplication.instrumentId));

        if (isSelectInstrument) {

            allStocks.addAll(tmpStocks);
        } else {

            if (selectMarket.getValue() == TradingSession.All.getValue()) {
                for (int i = 0; i < allInstruments.size(); i++) {
                    allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, allInstruments.get(i).getInstrumentCode()));
                }
            } else {
                for (int i = 0; i < marketInstruments.size(); i++) {
                    allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, marketInstruments.get(i).getInstrumentCode()));
                }
            }
        }


        if (etSearch.length() > 0) {
            allStocks = FilterStocks(allStocks);
            //adapter.getFilter().filter(etSearch.getText().toString());
        }

        favoriteStocks.clear();
        favoriteStocks.addAll(allStocks);
        Collections.sort(favoriteStocks, new Comparator<StockQuotation>() {
            @Override
            public int compare(StockQuotation lhs, StockQuotation rhs) {
                return Integer.compare(Integer.parseInt(lhs.getSecurityId()), Integer.parseInt(rhs.getSecurityId()));

            }
        });

        adapter = new FavoritesRecyclerAdapter(FavoritesActivity.this, favoriteStocks, this);
        rvStocks.setAdapter(adapter);

        Log.wtf("on instr click", "allStocks count = " + allStocks.size());


        Collections.sort(allStocks, new Comparator<StockQuotation>() {
            public int compare(StockQuotation o1, StockQuotation o2) {
                return Integer.compare(Integer.parseInt(o1.getSecurityId()), Integer.parseInt(o2.getSecurityId()));

            }
        });

        //rvStocks.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }

    public ArrayList<StockQuotation> FilterStocks(ArrayList<StockQuotation> stockList) {

        String filterString = etSearch.getText().toString().toLowerCase();

        ArrayList<StockQuotation> results = new ArrayList<StockQuotation>();

        final ArrayList<StockQuotation> list = stockList;

        int count = list.size();
        final ArrayList<StockQuotation> nlist = new ArrayList<>(count);

        String filterableString;

        for (int i = 0; i < count; i++) {

            if (MyApplication.instrumentId.length() > 0) {

                if (list.get(i).getInstrumentId().equals(MyApplication.instrumentId)) {

                    filterableString = list.get(i).getSecurityId() + list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                            + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }
            } else {

                filterableString = list.get(i).getSecurityId() + list.get(i).getStockID() + list.get(i).getNameAr() + list.get(i).getNameEn()
                        + list.get(i).getSymbolAr() + list.get(i).getSymbolEn();

                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }
        }

        results = nlist;

        return results;
    }

    void InitializeMarketServiceLocal() {
        try {
            View includedLayout = this.findViewById(R.id.my_toolbar);

            final TextView marketstatustxt = includedLayout.findViewById(R.id.market_state_value_textview);
            final LinearLayout llmarketstatus = includedLayout.findViewById(R.id.ll_market_state);


            favMarketReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String marketTime = intent.getExtras().getString(AppService.EXTRA_MARKET_TIME);
                    Actions.setMarketStatus(llmarketstatus, marketstatustxt, FavoritesActivity.this);
                    Log.wtf("InitializeMarketServiceV2", "setMarketStatus: " + MyApplication.marketStatus.getStatusDescriptionAr());

                    if (marketTime != null) {
                        if (marketTime.equals(""))
                            marketTime = MyApplication.marketStatus.getMarketTime();
                        Actions.setMarketTime(marketTime, FavoritesActivity.this);
                        Log.wtf("InitializeMarketServiceV2 3", "setMarketTime: " + marketTime);
                    }
                }
            };
            LocalBroadcastManager.getInstance(this).registerReceiver(favMarketReceiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

//            LocalBroadcastManager.getInstance(this).registerReceiver(
//                    new BroadcastReceiver() {
//                        @Override
//                        public void onReceive(Context context, Intent intent) {
//
//                            String marketTime = intent.getExtras().getString(AppService.EXTRA_MARKET_TIME);
//                            Actions.setMarketStatus(llmarketstatus,marketstatustxt, FavoritesActivity.this);
//                            Log.wtf("InitializeMarketService","setMarketStatus: " + MyApplication.marketStatus.getStatusDescriptionAr());
//
//                            if (marketTime != null) {
//                                if (marketTime.equals(""))
//                                    marketTime = MyApplication.marketStatus.getMarketTime();
//                                Actions.setMarketTime(marketTime, FavoritesActivity.this);
//                                Log.wtf("InitializeMarketServiceV2","setMarketTime: " + marketTime);
//                            }
//                        }
//                    }, new IntentFilter(AppService.ACTION_MARKET_SERVICE)
//            );
            Log.wtf("InitializeMarketServiceV2", "call from : " + this.getLocalClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GetInstruments extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.showDialog(FavoritesActivity.this);
            Log.wtf("play ", "GetInstruments");
        }

        @Override
        protected String doInBackground(Void... a) {


            String result = "";
            String url = MyApplication.link + MyApplication.GetInstruments.getValue(); // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", instrumentId.length() == 0 ? "0" : instrumentId);
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("MarketId", MyApplication.marketID);
                result = ConnectionRequests.GET(url, FavoritesActivity.this, parameters);

                MyApplication.instruments.addAll(GlobalFunctions.GetInstrumentsList(result));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetInstruments.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            MyApplication.dismiss();
            Log.wtf("dismiss ", "GetInstruments");

            allInstruments.clear();
            if (!MyApplication.isOTC) {
                for (int i = 1; i < AllMarkets.size(); i++) {
                    allInstruments.addAll(Actions.filterInstrumentsByMarketSegmentID(MyApplication.instruments, AllMarkets.get(i).getValue()));
                }
            } else {
                allInstruments.addAll(MyApplication.instruments);
            }
            marketInstruments = allInstruments;
            instrumentsRecyclerAdapter.notifyDataSetChanged();

            getFavoriteStocks = new GetFavoriteStocks();
            getFavoriteStocks.executeOnExecutor(MyApplication.threadPoolExecutor);
        }
    }

    private class GetFavoriteStocks extends AsyncTask<Void, Void, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MyApplication.showDialog(FavoritesActivity.this);
            Log.wtf("play ", "GetFavoriteStocks");
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + GetFavoriteStocks.getValue(); // this method uses key after login

            try {

                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("UserID", MyApplication.currentUser.getId() + "");
                parameters.put("key", getString(R.string.beforekey));
                parameters.put("TStamp", "0");
                parameters.put("InstrumentId", ""/*instrumentId*/);

                Log.wtf("GetFavoriteStocks", "parameters : " + parameters);

                result = ConnectionRequests.GET(url, FavoritesActivity.this, parameters);
                try {
                    favoritesIds.addAll(GlobalFunctions.GetFavoriteStocks(result));
                } catch (Exception e) {

                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetInstruments.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                MyApplication.dismiss();
                Log.wtf("dismiss ", "GetFavoriteStocks");
            } catch (Exception e) {
                e.printStackTrace();
            }
            allStocks.clear();
            favoriteStocks.clear();
            allStocks.addAll(Actions.getStocksByIds(MyApplication.stockQuotations, favoritesIds));
            favoriteStocks.addAll(allStocks);
            Collections.sort(favoriteStocks, new Comparator<StockQuotation>() {
                @Override
                public int compare(StockQuotation lhs, StockQuotation rhs) {
                    return Integer.compare(Integer.parseInt(lhs.getSecurityId()), Integer.parseInt(rhs.getSecurityId()));
                }
            });

            adapter.notifyDataSetChanged();
            Log.wtf("allStocks count", ": " + allStocks.size());
            Log.wtf("favoriteStocks count", ": " + favoriteStocks.size());

            LocalBroadcastManager.getInstance(FavoritesActivity.this).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            try {



                /*                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {*/
                                if (running) {
                                    favoriteStocks.clear();
                                    MyApplication.stockQuotations = intent.getExtras().getParcelableArrayList(AppService.EXTRA_STOCK_QUOTATIONS_LIST);
                                    allStocks = Actions.getStocksByIds(MyApplication.stockQuotations, favoritesIds);
                                    favoriteStocks.addAll(Actions.filterStocksByInstrumentID(allStocks, instrumentId));
                                    if (favoriteStocks.size() > 0) {
                                        favoriteStocks.get(0).setChanged(!favoriteStocks.get(0).isChanged());
                                        favoriteStocks.get(0).setChanged(!favoriteStocks.get(0).isChanged());
                                    }

                                    Collections.sort(favoriteStocks, new Comparator<StockQuotation>() {
                                        @Override
                                        public int compare(StockQuotation lhs, StockQuotation rhs) {
                                            return Integer.compare(Integer.parseInt(lhs.getSecurityId()), Integer.parseInt(rhs.getSecurityId()));

                                        }
                                    });
                                    Log.wtf("favorite_all_count", "count:" + changedCountAll());
                                    Log.wtf("favorite_fav_count", "count:" + changedCountFavorite());
                                    adapter.notifyDataSetChanged();

                                }
                     /*               }
                                }, 100);*/


                        /*        adapter = new FavoritesRecyclerAdapter(FavoritesActivity.this, favoriteStocks, FavoritesActivity.this);
                                rvStocks.setAdapter(adapter);
                                rvStocks.setLayoutManager(llm);*/

                            } catch (Exception e) {
                            }
                        }
                    }, new IntentFilter(AppService.ACTION_STOCKS_SERVICE)
            );

            MyApplication.dismiss();
            retrieveFiltered(false);
        }
    }



    private int changedCountAll(){
        int count=0;
        for (int i=0;i<allStocks.size();i++){
            if(allStocks.get(i).isChanged()) {
                //adapter.notifyItemChanged(i);
                count++;
            }
        }
        return count;
    }


    private int changedCountFavorite(){
        int count=0;
        for (int i=0;i<favoriteStocks.size();i++){
            if(favoriteStocks.get(i).isChanged()) {
                count++;
            }
        }
        return count;
    }
}
