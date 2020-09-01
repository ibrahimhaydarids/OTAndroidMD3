package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.ids.fixot.adapters.InstrumentsRecyclerAdapter;
import com.ids.fixot.adapters.MarketsSpinnerAdapter;
import com.ids.fixot.adapters.StockQuotationRecyclerAdapter;
import com.ids.fixot.enums.enums.TradingSession;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.StockQuotation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class StockActivity extends AppCompatActivity implements InstrumentsRecyclerAdapter.RecyclerViewOnItemClickListener, MarketStatusListener {

    FrameLayout rlStockSearch;
    RelativeLayout rootLayout;
    RecyclerView rvStocks;
    ArrayList<StockQuotation> allStocks = new ArrayList<>();
    ArrayList<StockQuotation> tmpStocks = new ArrayList<>();
    String sectorId = "12";
    String instrumentId = "";
    Instrument selectedInstrument = new Instrument();
    GetInstruments getInstruments;
    boolean firstTabClick = true;
    EditText etSearch;
    Button btClear;
    LinearLayoutManager llm;
    RecyclerView rvInstruments;
    InstrumentsRecyclerAdapter instrumentsRecyclerAdapter;
    Spinner spMarkets;
    MarketsSpinnerAdapter marketsSpinnerAdapter;
    TradingSession selectMarket = TradingSession.All;
    ArrayList<TradingSession> AllMarkets = new ArrayList<>();
    ArrayList<Instrument> marketInstruments = new ArrayList<>();
    ArrayList<Instrument> allInstruments = new ArrayList<>();
    Boolean isSelectInstrument = false;
    TextView tvStock, tvSessionName, tvPrice, tvChange;
    TextView sectorTitle;
    boolean isIslamic = false;
    private BroadcastReceiver receiver;
    private StockQuotationRecyclerAdapter adapter;
    private boolean running = true, firstTime = true;
    private boolean started = false;

    public StockActivity() {
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_stocks);
        Actions.initializeBugsTracking(this);

        if (getIntent().hasExtra("isIslamicStocks")) {
            isIslamic = true;
        }

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

        if (getIntent().hasExtra("sectorId")) {

            MyApplication.stockTimesTamp = "0";

            sectorId = getIntent().getExtras().getString("sectorId");
            Actions.initializeToolBar(getIntent().getExtras().getString("sectorName"), StockActivity.this);
            // sectorTitle.setText(getIntent().getExtras().getString("sectorName"));
            allStocks.clear();
            allStocks.addAll(Actions.filterStocksBySectorAndInstrumentID(MyApplication.stockQuotations, instrumentId, sectorId));
            Log.wtf("hasExtra sectorId", ": " + sectorId + " , allStocks count = " + allStocks.size());

        } else {

            //  sectorTitle.setVisibility(View.GONE);

            if (!Actions.isNetworkAvailable(this)) {

                Actions.CreateDialog(this, getString(R.string.no_net), false, false);
            }

            Actions.initializeToolBar(getString(R.string.stock), StockActivity.this);
            allStocks.addAll(MyApplication.stockQuotations);
            Log.wtf("no sectorId", ": allStocks count = " + allStocks.size());

            if (getIntent().hasExtra("isIslamicStocks")) {
                isIslamic = true;
                tmpStocks = Actions.filterStocksByIsIslamic(allStocks);
                allStocks.clear();
                allStocks.addAll(tmpStocks);
            }
        }

        tmpStocks = allStocks;
        allStocks.clear();
        /*for(int i=0; i<marketInstruments.size(); i++) {
            allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, marketInstruments.get(i).getInstrumentCode() ));
        }*/
        allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, marketInstruments));

        started = true;
        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);
        //   sectorTitle.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        adapter = new StockQuotationRecyclerAdapter(this, allStocks);//, this);
        rvStocks.setAdapter(adapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (running) {

                            tmpStocks = new ArrayList<>();
                            Log.wtf("LocalBroadcastManager", "before allStocks size = " + allStocks.size());
                            allStocks.clear();
                            if (getIntent().hasExtra("sectorId")) {

                                tmpStocks.addAll(Actions.filterStocksBySectorAndInstrumentID(MyApplication.stockQuotations, instrumentId, sectorId));
                            } else {

                                MyApplication.stockQuotations = intent.getExtras().getParcelableArrayList(AppService.EXTRA_STOCK_QUOTATIONS_LIST);
                                tmpStocks.addAll(Actions.filterStocksByInstrumentID(MyApplication.stockQuotations, MyApplication.instrumentId));
                            }

                            if (isSelectInstrument) {

                                allStocks.addAll(tmpStocks);
                            } else {

                                if (selectMarket.getValue() == TradingSession.All.getValue()) {
                                    allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, allInstruments));
                                } else {
                                    allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, marketInstruments));
                                }
                            }

                            if (isIslamic) {
                                tmpStocks = Actions.filterStocksByIsIslamic(allStocks);
                                allStocks.clear();
                                allStocks.addAll(tmpStocks);
                            }

                            //tmpStocks = new ArrayList<>();
                            //tmpStocks.addAll(allStocks);
                            //allStocks = new ArrayList<>();
                            //allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, marketInstruments));

                            Log.wtf("allStocks", "count = " + allStocks.size());


                            try {
                                Log.wtf("LocalBroadcastManager", "after allStocks size = " + allStocks.size());
                                if (etSearch.length() > 0) {
                                    adapter.getFilter().filter(etSearch.getText().toString());
                                }
                                Log.wtf("allStocks in adapter", "count = " + adapter.getFilteredItems().size());
                                adapter.notifyDataSetChanged();
                            }catch (Exception e){}
                            //rvStocks.setAdapter(adapter);
                        }
                    }
                }, new IntentFilter(AppService.ACTION_STOCKS_SERVICE)
        );

        //<editor-fold desc="instruments section">
        if (MyApplication.instruments.size() < 2) { //being empty or only having the fake entry
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
        }
        //</editor-fold>

        Actions.setTypeface(new TextView[]{tvStock, tvSessionName, tvPrice, tvChange}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        tvSessionName.setGravity(MyApplication.lang == MyApplication.ARABIC ? Gravity.LEFT : Gravity.RIGHT);
    }


    private void retrieveFiltered(boolean hasSectorId) {
        tmpStocks = new ArrayList<>();
        allStocks = new ArrayList<>();

        if (hasSectorId) {

            tmpStocks = (Actions.filterStocksBySectorAndInstrumentID(MyApplication.stockQuotations, MyApplication.instrumentId, sectorId));
        } else {

            tmpStocks = (Actions.filterStocksByInstrumentID(MyApplication.stockQuotations, MyApplication.instrumentId));
        }

        if (isSelectInstrument) {

            allStocks.addAll(tmpStocks);
        } else {

            if (selectMarket.getValue() == TradingSession.All.getValue()) {
                /*for(int i=0; i<allInstruments.size(); i++) {
                    allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, allInstruments.get(i).getInstrumentCode() ));
                }*/
                allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, allInstruments));
            } else {
                /*for(int i=0; i<marketInstruments.size(); i++) {
                    allStocks.addAll(Actions.filterStocksByInstrumentID(tmpStocks, marketInstruments.get(i).getInstrumentCode() ));
                }*/
                allStocks.addAll(Actions.filterStocksByInstruments(tmpStocks, marketInstruments));
            }
        }

        if (isIslamic) {

            tmpStocks = Actions.filterStocksByIsIslamic(allStocks);
            allStocks.clear();
            allStocks.addAll(tmpStocks);
        }

        if (etSearch.length() > 0) {
            allStocks = FilterStocks(allStocks);
            //adapter.getFilter().filter(etSearch.getText().toString());
        }

        adapter = new StockQuotationRecyclerAdapter(StockActivity.this, allStocks);
        rvStocks.setAdapter(adapter);

        Log.wtf("on instr click", "allStocks count = " + allStocks.size());

        //rvStocks.setAdapter(adapter);
        //adapter.notifyDataSetChanged();
    }


    @Override
    protected void onResume() {
        super.onResume();
        running = true;

        Actions.checkSession(this);
        Actions.checkLanguage(this, started);

        //Actions.InitializeSessionService(this);
        //Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //  Actions.InitializeMarketServiceV2(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Actions.unregisterMarketReceiver(this);
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

        MyApplication.instrumentId = "";
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void back(View v) {
        this.finish();
    }

    public void loadFooter(View v) {
        Actions.loadFooter(this, v);
    }


    public void findViews() {

        rlStockSearch = findViewById(R.id.rlStockSearch);
        rootLayout = findViewById(R.id.rootLayout);
        rvStocks = findViewById(R.id.rvStocks);


        if (MyApplication.isOTC) {
            LinearLayout vs = findViewById(R.id.spMarketLayout);

            ViewGroup.LayoutParams params = vs.getLayoutParams();

            params.width = 0;

            vs.setVisibility(View.INVISIBLE);
        }

        ImageView iv_arrow = findViewById(R.id.iv_arrow);
        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spMarkets.performClick();
            }
        });


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

                instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(StockActivity.this, marketInstruments, StockActivity.this);
                rvInstruments.setAdapter(instrumentsRecyclerAdapter);
                Log.wtf("select Market : " + selectMarket.toString(), "instrument count = " + marketInstruments.size());

                retrieveFiltered(getIntent().hasExtra("sectorId"));

                Log.wtf("on instr click", "allStocks count = " + allStocks.size());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        rvInstruments.setLayoutManager(new LinearLayoutManager(StockActivity.this, LinearLayoutManager.HORIZONTAL, false));
        instrumentsRecyclerAdapter = new InstrumentsRecyclerAdapter(this, marketInstruments, this);
        rvInstruments.setAdapter(instrumentsRecyclerAdapter);

        llm = new LinearLayoutManager(StockActivity.this);
        rvStocks.setLayoutManager(llm);
        btClear = rlStockSearch.findViewById(R.id.btClear);
        etSearch = rlStockSearch.findViewById(R.id.etSearch);
        etSearch.setSelected(false);
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                if (arg0.length() == 0) {
                    btClear.setVisibility(View.GONE);

                    try {
                        Actions.closeKeyboard(StockActivity.this);
                    } catch (Exception e) {
                        Log.wtf("catch ", "" + e.getMessage());
                    }

                    allStocks.clear();
                    retrieveFiltered(getIntent().hasExtra("sectorId"));
                    adapter = new StockQuotationRecyclerAdapter(StockActivity.this, allStocks);//, StockActivity.this);
                    adapter.notifyDataSetChanged();
                    rvStocks.setAdapter(adapter);
                } else {

                    adapter.getFilter().filter(arg0);
                    btClear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        btClear.setOnClickListener(v -> etSearch.setText(""));


        tvStock = findViewById(R.id.tvStock);
        tvSessionName = findViewById(R.id.tvSessionName);
        tvPrice = findViewById(R.id.tvPrice);
        tvChange = findViewById(R.id.tvChange);

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
        retrieveFiltered(getIntent().hasExtra("sectorId"));
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

    private class GetInstruments extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.showDialog(StockActivity.this);
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

                result = ConnectionRequests.GET(url, StockActivity.this, parameters);

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
        }
    }

}
