package com.ids.fixot.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
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
import com.ids.fixot.adapters.OrderDurationTypeAdapter;
import com.ids.fixot.adapters.PredefineQuantityAdapter;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;
import com.ids.fixot.enums.enums;
import com.ids.fixot.model.BrokerageFee;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.OrderDurationType;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.Trade;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class TradesActivity extends AppCompatActivity implements OrderDurationTypeAdapter.RecyclerViewOnItemClickListener, PredefineQuantityAdapter.RecyclerViewOnItemClickListener, MarketStatusListener {

    int selectedPos = -3;
    private boolean firstTime=true;
    Toolbar myToolbar;
    ImageView ivPortfolio, ivArrow, iv_PredfQtty, BtnCalc;
    TextView tvToolbarTitle, tvToolbarStatus, tvUserName, tvPortfolioNumber, tvInstrumentValue, tvSessionValue, close;
    TextView tvQuantityValue, tvPurchasePowerValue, tvStockTitle, tvCloseValue, tvLastValue, tvBidValue, tvAskValue;
    TextView tvHighValue, tvLowValue;
    Button btTimeSales, btOrderBook;
    Button btSell, btBuy;
    Button btMarketPrice, btLimit;
    LinearLayout llTradeSection;
    OrderDurationType orderDurationType = new OrderDurationType();
    OrderDurationTypeAdapter adapter;
    PredefineQuantityAdapter predfQttyAdapter;
    EditText etLimitPrice, etQuantity, etDurationType;
    Button btLimitPlus, btLimitMinus, btQuantityPlus, btQuantityMinus;
    Spinner spLimitedPrice;
    SwipeRefreshLayout swipeContainer;
    TextView tvCostValue, tvCommissionValue, tvOverallValue, tvFill;
    Button btReview, btCancel;
    double price = 0;
    //    double tickDirection = 0 ;
    double ticketPrice = 0.1, ticketQtt = 0.1;
    double HiLimit = 1000000000;
    int quantity = 0, tradeType = MyApplication.ORDER_BUY, orderType = MyApplication.LIMIT;
    StockQuotation stockQuotation;
    RelativeLayout rlUserHeader, rlBuySell;
    RelativeLayout rootLayout;
    Trade trade = new Trade();
    OnlineOrder onlineOrder = new OnlineOrder();
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    LinearLayout llOrderType;
    TextView tvOtcPrice;
    //MKobaissy Popup
    AlertDialog.Builder builder;
    AlertDialog dialog;
    RecyclerView rvDurationType;
    int remainingQty = 0;
    int[] predefinedQuantityData = new int[]{};
    GetOrderDurationTypes getOrderDurationTypes;
    GetTradeInfo getTradeInfo;
    CheckBox cbPrivate;
    String dateFormatter = "dd/MM/yyyy 00:00:00";
    String setDateFormatter = "dd/MM/yyyy";
    Spinner spSubAccounts;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    boolean setMaxMin = false;
    private BroadcastReceiver receiver;
    private boolean started = false, running = true, firstOpen = true;
    private boolean isFromOrderDetails = false;

    public TradesActivity() {
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
        Log.wtf("onCreate", " onCreate");

        Actions.setLocal(MyApplication.lang, this);
        Actions.initializeBugsTracking(this);

        setContentView(R.layout.activity_trades);

        if (getIntent().hasExtra("isFromOrderDetails")) {

            isFromOrderDetails = true;
            stockQuotation = getIntent().getExtras().getParcelable("stockQuotation");
            onlineOrder = getIntent().getExtras().getParcelable("onlineOrder");

            Log.wtf("onlineOrder.getDurationID())", " t " + onlineOrder.getDurationID());
            Log.wtf("onlineOrder.getGoodUntilDate())", " t " + onlineOrder.getGoodUntilDate());

            trade.setDurationTypeId(onlineOrder.getDurationID());
            trade.setQuantity(onlineOrder.getQuantity());
            trade.setExecutedQuantity(onlineOrder.getQuantityExecuted());

            trade.setGoodUntilDate(onlineOrder.getGoodUntilDate());
            trade.setTradeTypeID(onlineOrder.getTradeTypeID());
            trade.setOrderType(onlineOrder.getOrderTypeID());
            trade.setPrice(onlineOrder.getPrice());
            orderType = onlineOrder.getOrderTypeID();
            selectedPos = Actions.returnDurationIndex(orderType);

        } else {

            isFromOrderDetails = false;
            if (getIntent().hasExtra("stockQuotation")) { //from stocks activity

                stockQuotation = getIntent().getExtras().getParcelable("stockQuotation");
            } else if (getIntent().hasExtra("stockID")) { //from portfolio
                stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, getIntent().getExtras().getInt("stockID"));
                stockQuotation.setStockID(getIntent().getExtras().getInt("stockID"));
            } else {
                stockQuotation = new StockQuotation();
            }
        }

        findViews();

        try {
            setMaxMin = stockQuotation.getInstrumentId().equals(MyApplication.Auction_Instrument_id) || MyApplication.marketID.equals(Integer.toString(enums.MarketType.KWOTC.getValue())) || cbPrivate.isChecked();
        }catch (Exception e){}
        started = true;

        setInitialData();

        if (MyApplication.allOrderDurationType.size() == 0) {
            Log.wtf("MyApplication.allOrderDurationType.size() ", "0");
            getOrderDurationTypes = new GetOrderDurationTypes();
            getOrderDurationTypes.executeOnExecutor(MyApplication.threadPoolExecutor);

        } else {
            if (getIntent().hasExtra("isFromOrderDetails")) {
                Log.wtf("MyApplication.allOrderDurationType.size() > 0", "isFromOrderDetails");

                int index = Actions.returnDurationIndex(onlineOrder.getDurationID());
                selectedPos = index;
                setOrderDuration(index);

                trade.setDurationType(MyApplication.lang == MyApplication.ARABIC ? MyApplication.allOrderDurationType.get(index).getDescriptionAr() : MyApplication.allOrderDurationType.get(index).getDescriptionEn());
            } else {
                Log.wtf("MyApplication.allOrderDurationType.size() > 0", " Not have extra isFromOrderDetails ");
                if (!Actions.isMarketOpen()) {
                    try {
                        selectedPos = Actions.returnDurationIndex(1);
                        setOrderDuration(Actions.returnDurationIndex(1));
                    }catch (Exception e){}
                } else {
                        try {
                    selectedPos = Actions.returnDurationIndex(0);

                       setOrderDuration(Actions.returnDurationIndex(0));
                   }catch (Exception e){}
                }
            }
        }

        Actions.initializeToolBar(getString(R.string.trades_page_title), TradesActivity.this);
        Actions.overrideFonts(this, rootLayout, false);
        setTypefaces();

    }


    public void back(View v) {
        this.finish();
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.checkLanguage(this, started);

        Actions.InitializeSessionServiceV2(this);

        running = true;
         firstTime=true;
        Log.wtf("onResume", "getTradeInfo");
    }


    private void findViews() {


        //<editor-fold desc="find views">

        spSubAccounts = findViewById(R.id.spSubAccounts);
        subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, MyApplication.currentUser.getSubAccounts());
        spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
        spSubAccounts.setSelection(returnAccountIndex());
        spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);

                getTradeInfo = new GetTradeInfo();
                getTradeInfo.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        llOrderType = findViewById(R.id.llOrderType);
        tvOtcPrice = findViewById(R.id.tvOtcPrice);

        rlBuySell = findViewById(R.id.rlBuySell);
        rootLayout = findViewById(R.id.rootLayout);
        cbPrivate = findViewById(R.id.cbPrivate);
        llTradeSection = findViewById(R.id.llTradeSection);
        rlUserHeader = findViewById(R.id.rlUserHeader);
        myToolbar = findViewById(R.id.my_toolbar);
        spLimitedPrice = findViewById(R.id.spLimitedPrice);
        swipeContainer = findViewById(R.id.swipeContainer);

        ivPortfolio = rlUserHeader.findViewById(R.id.ivPortfolio);
        ivArrow = findViewById(R.id.ivArrow);

        cbPrivate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                trade.setStatusTypeId(16);
                trade.setOperationTypeID(4);
            } else {

                trade.setStatusTypeId(1);
                trade.setOperationTypeID(0);
            }

            setMaxMin = stockQuotation.getInstrumentId().equals(MyApplication.Auction_Instrument_id) || MyApplication.marketID.equals(Integer.toString(enums.MarketType.KWOTC.getValue())) || cbPrivate.isChecked();
        });

        close = findViewById(R.id.close);
        tvUserName = rlUserHeader.findViewById(R.id.tvUserName);
        tvPortfolioNumber = findViewById(R.id.tvPortfolioNumber);
        tvInstrumentValue = findViewById(R.id.tvInstrumentValue);
        tvSessionValue = findViewById(R.id.tvSessionValue);
        tvToolbarStatus = findViewById(R.id.toolbar_status);
        tvToolbarTitle = findViewById(R.id.toolbar_title);
        tvQuantityValue = findViewById(R.id.tvQuantityValue);
        tvPurchasePowerValue = findViewById(R.id.tvPurchasePowerValue);
        tvStockTitle = findViewById(R.id.tvStockTitle);
        tvCloseValue = findViewById(R.id.tvCloseValue);
        tvLastValue = findViewById(R.id.tvLastValue);
        tvBidValue = findViewById(R.id.tvBidValue);
        tvAskValue = findViewById(R.id.tvAskValue);
        tvHighValue = findViewById(R.id.tvHighValue);
        tvLowValue = findViewById(R.id.tvLowValue);
        tvCostValue = findViewById(R.id.tvCostValue);
        tvCommissionValue = findViewById(R.id.tvCommissionValue);
        tvOverallValue = findViewById(R.id.tvOverallValue);
        iv_PredfQtty = findViewById(R.id.iv_PredfQtty);
        BtnCalc = findViewById(R.id.BtnCalc);
        tvFill = findViewById(R.id.tvFill);

        btTimeSales = findViewById(R.id.btTimeSales);
        btOrderBook = findViewById(R.id.btOrderBook);
        btReview = findViewById(R.id.btReview);
        btCancel = findViewById(R.id.btCancel);

        btSell = findViewById(R.id.btSell);
        btBuy = findViewById(R.id.btBuy);
        btMarketPrice = findViewById(R.id.btMarketPrice);
        btLimit = findViewById(R.id.btLimit);

        etLimitPrice = findViewById(R.id.etLimitPrice);
        etQuantity = findViewById(R.id.etQuantity);
        etDurationType = findViewById(R.id.etDurationType);

        btLimitPlus = findViewById(R.id.btLimitPlus);
        btLimitMinus = findViewById(R.id.btLimitMinus);
        btQuantityPlus = findViewById(R.id.btQuantityPlus);
        btQuantityMinus = findViewById(R.id.btQuantityMinus);
        //</editor-fold>


        if (tradeType == MyApplication.ORDER_SELL) {

            setSellChecked(true);
        } else {

            setSellChecked(false);
        }

        if (MyApplication.isOTC) {

            tvOtcPrice.setVisibility(View.VISIBLE);
            llOrderType.setVisibility(View.GONE);

            trade.setOrderType(MyApplication.LIMIT);
            etLimitPrice.setText("0");

        } else {

            llOrderType.setVisibility(View.VISIBLE);
            tvOtcPrice.setVisibility(View.GONE);

            if (orderType == MyApplication.LIMIT) {

                setLimitChecked(true);
                trade.setOrderType(MyApplication.LIMIT);
            } else {

                setLimitChecked(false);
                trade.setOrderType(MyApplication.MARKET_PRICE);
            }
        }

        ivArrow.setOnClickListener(v -> showPopupDurationType());
        etDurationType.setOnClickListener(v -> showPopupDurationType());  //Mkobaissy
        iv_PredfQtty.setOnClickListener(v -> showPredefinedQuantity());
        BtnCalc.setOnClickListener(v -> showCalculator());
        tvFill.setOnClickListener(v -> fillData());


        if (onlineOrder.getDurationID() == 6) {
            String goodUntil = onlineOrder.getGoodUntilDate();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
                Date date = sdf.parse(goodUntil);
                goodUntil = sdf.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            etDurationType.setText(goodUntil);
        }

        /*etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {

                *//*if (etQuantity.getText().length() > 0) {

                    try {
                        quantity = Integer.parseInt(getNumberFromString(etQuantity.getText().toString()));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        quantity = 0;
                    }
                } else {
                    quantity = 0;
                }
                Log.wtf("quantity",": " + quantity);
                updateOverAllViews(price, quantity);

                if(etQuantity.getText().length() != Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator).length()){
                    etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                    int pos = etQuantity.getText().length();
                    etQuantity.setSelection(pos);
                }*//*
            }
        });*/

        swipeContainer.setOnRefreshListener(() -> {
            getTradeInfo = new GetTradeInfo();
            getTradeInfo.executeOnExecutor(MyApplication.threadPoolExecutor);
        });

        etQuantity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    if (etQuantity.getText().length() > 0) {

                        try {

                            long quantityValue = Long.parseLong(getNumberFromString(etQuantity.getText().toString()));
                            Log.wtf("long Value", ": " + quantityValue);

                            if (quantityValue > 1000000000) {
                                quantityValue = 1000000000;
                            }
                            quantity = (int) quantityValue;
                            Log.wtf("quantity Value", ": " + quantity);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            quantity = 0;
                        }
                    } else {
                        quantity = 0;
                    }
                    Log.wtf("quantity", ": " + quantity);
                    updateOverAllViews(price, quantity);

                    if (etQuantity.getText().length() != Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator).length()) {
                        etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                        int pos = etQuantity.getText().length();
                        etQuantity.setSelection(pos);
                    }

                }
                return false;
            }
        });

        tvPurchasePowerValue.setBackgroundColor(getResources().getColor(R.color.even_green_color));
        btReview.setBackgroundColor(getResources().getColor(R.color.green_color));
        date = (view, year, monthOfYear, dayOfMonth) -> {
            if (year >= Calendar.getInstance().get(Calendar.YEAR)) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(etDurationType);
            }
        };

        if (getIntent().hasExtra("action")) {

            tradeType = getIntent().getExtras().getInt("action");

            if (tradeType == MyApplication.ORDER_BUY)
                btBuy.performClick();
            else
                btSell.performClick();
        }

        if (!Actions.isMarketOpen()) {
            btMarketPrice.setEnabled(false);
            btLimit.performClick();
        }

        if (isFromOrderDetails) {
            rlBuySell.setEnabled(false);
            btSell.setEnabled(false);
            btBuy.setEnabled(false);
            etLimitPrice.setText(String.valueOf(onlineOrder.getPrice()));

            if (trade.getOrderType() == MyApplication.MARKET_PRICE) {

                btMarketPrice.performClick();
            } else {

                btLimit.performClick();
            }

//        }
//        if (isFromOrderDetails) {

            TextView quantity_title = findViewById(R.id.quantity_title);
            quantity_title.setText(getResources().getString(R.string.fs_rem_qtty));

            if (onlineOrder.getStatusID() == 16) {
                cbPrivate.setChecked(true);
            } else {
                cbPrivate.setChecked(false);
            }
            cbPrivate.setEnabled(false);
        }

        etLimitPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {

                    if (!MyApplication.isOTC) {

                        if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {

                            if ((Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) > HiLimit) || (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < 0)) {

                                if (tradeType == MyApplication.ORDER_SELL) {
                                    etLimitPrice.setText("" + stockQuotation.getHiLimit());
                                } else {
                                    etLimitPrice.setText("" + stockQuotation.getLowlimit());
                                }
                            }
                        } else {

                            if ((Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) > stockQuotation.getHiLimit())
                                    || (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < stockQuotation.getLowlimit())) {

                                if (tradeType == MyApplication.ORDER_SELL) {
                                    etLimitPrice.setText("" + stockQuotation.getHiLimit());
                                } else {
                                    etLimitPrice.setText("" + stockQuotation.getLowlimit());

                                }
                            }
                        }

                        if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) > 100.9) {
                            etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), Actions.OneDecimalThousandsSeparator));
                        } else {
                            etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), Actions.OneDecimalThousandsSeparator));
                        }
                        price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
                        setTick();

                    } else {

                        if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) > 100.9) {
                            etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), Actions.OneDecimalThousandsSeparator));
                        } else {
                            etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), Actions.OneDecimalThousandsSeparator));
                        }
                        price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
                        setTick();

                    }
                }
            }
        });
    }


    public void setTick() {
        ticketQtt = 1;
        for (int i = 0; i < MyApplication.units.size(); i++) {
            if (MyApplication.units.get(i).getFromPrice() <= price && price <= MyApplication.units.get(i).getToPrice()) {
                ticketPrice = MyApplication.units.get(i).getPriceUnit();
                Log.wtf("setTick - Price Change", "price = " + price + " / ticketPrice = " + ticketPrice + " / ticketQtt = " + ticketQtt);
            }
        }

        if (price > 100.9) {
            etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), Actions.NoDecimalSeparator));
        }
    }


    public void showCalculator() {
        try {
            startActivity(new Intent(TradesActivity.this, SiteMapDataActivity.class)
                    .putExtra("calcualtor", true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void fillData() {
        Log.wtf("tvFill", "fillData ");

        if (tradeType == MyApplication.ORDER_SELL) {
            Log.wtf("tradeType", "ORDER_SELL ");
            quantity = trade.getAvailableShareCount();
            etQuantity.setText(Actions.formatNumber(trade.getAvailableShareCount(), Actions.NoDecimalSeparator));
        } else {
            Log.wtf("tradeType", "ORDER_BUY ");

            if (trade.getPurchasePower() != 0 && price != 0) {

                for (int i = 0; i < MyApplication.allBrokerageFees.size(); i++) {
                    if (MyApplication.allBrokerageFees.get(i).getInstrumentId().equals(trade.getStockQuotation().getInstrumentId())) {
                        BrokerageFee data = MyApplication.allBrokerageFees.get(i);
                        double fr = trade.getPurchasePower() - data.getClearing();
                        double sr = price * (1 + data.getTotalBrokerageFee());
                        double rawqty = ((fr / sr) * 1000);
                        quantity = (int) rawqty;

                        etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalSeparator));
                    }
                }


            }
        }
        updateOverAllViews(price, quantity);
    }


    private int returnAccountIndex() {

        int index = -1;

        for (int i = 0; i < MyApplication.currentUser.getSubAccounts().size(); i++) {
            if (MyApplication.currentUser.getSubAccounts().get(i).getPortfolioId() == MyApplication.selectedSubAccount.getPortfolioId()) {
                index = i;
            }
        }

        return index;
    }


    public void setOrderDuration(int position) {
        String txt = "";
        orderDurationType = MyApplication.allOrderDurationType.get(position);

        if (MyApplication.allOrderDurationType.get(position).getID() == 6) {
            txt = "" + onlineOrder.getGoodUntilDate();
        } else {
            if (MyApplication.lang == MyApplication.ARABIC) {
                etDurationType.setTypeface(MyApplication.droidbold);
                txt = "" + MyApplication.allOrderDurationType.get(position).getDescriptionAr();
            } else {
                etDurationType.setTypeface(MyApplication.giloryBold);
                txt = "" + MyApplication.allOrderDurationType.get(position).getDescriptionEn();
            }
        }

        etDurationType.setText(txt);
    }


    private void showPopupDurationType() {

        builder = new AlertDialog.Builder(this);
        LinearLayoutManager layoutManager;
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout llPrice;

        final View editDialog = inflater.inflate(R.layout.popup_order_duration_type, null);

        rvDurationType = editDialog.findViewById(R.id.rvDurationType);

        layoutManager = new LinearLayoutManager(this);
        rvDurationType.setLayoutManager(layoutManager);


        adapter = new OrderDurationTypeAdapter(this, MyApplication.allOrderDurationType, this, selectedPos);
        rvDurationType.setAdapter(adapter);

        builder.setView(editDialog);
        dialog = builder.create();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            if (year >= Calendar.getInstance().get(Calendar.YEAR)) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Log.wtf("date", "value change");

                orderDurationType = MyApplication.allOrderDurationType.get(Actions.returnDurationIndex(6));
                updateLabel(etDurationType);
                selectedPos = Actions.returnDurationIndex(6);
                dialog.dismiss();
            }
        };

        dialog.show();
    }


    private void showPredefinedQuantity() {

        builder = new AlertDialog.Builder(this);
        LinearLayoutManager layoutManager;
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout llPrice;

        final View editDialog = inflater.inflate(R.layout.popup_order_duration_type, null);

        rvDurationType = editDialog.findViewById(R.id.rvDurationType);

        layoutManager = new LinearLayoutManager(this);
        rvDurationType.setLayoutManager(layoutManager);

        predefinedQuantityData = new int[]{5000, 10000, 15000, 20000, 30000, 50000, 100000, 200000, 300000};

        predfQttyAdapter = new PredefineQuantityAdapter(this, predefinedQuantityData, this, 0);
        rvDurationType.setAdapter(predfQttyAdapter);

        builder.setView(editDialog);
        dialog = builder.create();

        date = (view, year, monthOfYear, dayOfMonth) -> {
            if (year >= Calendar.getInstance().get(Calendar.YEAR)) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Log.wtf("date", "value change");

                orderDurationType = MyApplication.allOrderDurationType.get(Actions.returnDurationIndex(6));
                updateLabel(etDurationType);
                selectedPos = Actions.returnDurationIndex(6);
                dialog.dismiss();
            }
        };

        dialog.show();
    }


    @Override
    public void onItemClicked(View v, int position) {


        if (MyApplication.allOrderDurationType.get(position).getID() != 6) {
            if (!Actions.isMarketOpen()) {
                if (MyApplication.allOrderDurationType.get(position).getID() == 1 || ((MyApplication.marketStatus.getStatusID() != MyApplication.MARKET_CLOSED || MyApplication.marketStatus.getStatusID() != MyApplication.Enquiry) && position == 0)) {
                    orderDurationType = MyApplication.allOrderDurationType.get(position);
                    selectedPos = position;
                   try {
                       setOrderDuration(position);
                   }catch (Exception e){}
                    dialog.dismiss();
                }
            } else {
                orderDurationType = MyApplication.allOrderDurationType.get(position);
                selectedPos = position;
               try {
                   setOrderDuration(position);
               }catch (Exception e){}
                dialog.dismiss();
            }
        } else {
            showDateDialog();
        }
    }


    @Override
    public void onItemClickedd(View v, int position) {

        quantity = predefinedQuantityData[position];
        Log.wtf("onItemClickedd", "quantity = " + quantity);
        etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
        dialog.dismiss();
    }


    public void setLimit(View v) {

        if (etLimitPrice.getText().length() > 0) {

            try {
                price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                price = 0.0;
            }
        } else {

            if (orderType == MyApplication.LIMIT) {

                if (tradeType == MyApplication.ORDER_SELL)
                    price = stockQuotation.getHiLimit();
                else
                    price = stockQuotation.getLowlimit();
            } else {

                if (tradeType == MyApplication.ORDER_SELL)
                    price = stockQuotation.getLowlimit();
                else
                    price = stockQuotation.getHiLimit();
            }
        }

        switch (v.getId()) {

            case R.id.btLimitMinus:
                String quantityText = "";

                if (orderType == MyApplication.LIMIT) {


                    if (setMaxMin) {

                        if (price > 1) {
                            double pr = price - ticketPrice;
                            if (pr >= 1) {
                                price = pr;
                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        } else {

                            etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                        }


                        /*if(trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)){
                            if (price > 0) {
                                double pr = price - ticketPrice;
                                if(pr >= 0) {
                                    price = pr;
                                    etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                                }
                            } else {

                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        }
                        else{
                            if (price > stockQuotation.getLowlimit()) {
                                double pr = price - ticketPrice;
                                if(pr >= stockQuotation.getLowlimit()) {
                                    price = pr;
                                    etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                                }
                            } else {

                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        }*/
                    } else {


                        if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {
                            if (price > 0) {
                                double pr = price - ticketPrice;
                                if (pr >= 0) {
                                    price = pr;
                                    etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                                }
                            } else {

                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        } else {
                            if (price > stockQuotation.getLowlimit()) {
                                double pr = price - ticketPrice;
                                if (pr >= stockQuotation.getLowlimit()) {
                                    price = pr;
                                    etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                                }
                            } else {

                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        }
                    }
                }


                quantityText = etQuantity.getText().toString();
                if (isFromOrderDetails && quantityText.length() > 0) {
                    quantity = Integer.parseInt(getNumberFromString(quantityText));
                }
                updateOverAllViews(price, quantity);
                setTick();

                break;

            case R.id.btLimitPlus:

                if (orderType == MyApplication.LIMIT) {

                    if (setMaxMin) {

                        int maxHigh = 1000000;

                        if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < 0) {
                            etLimitPrice.setText(Actions.formatNumber(0, "##.##"));
                            price = 0;
                        } else if (price < maxHigh) {
                            double pr = price + ticketPrice;
                            if (pr <= maxHigh) {
                                price = pr;
                                etLimitPrice.setText(Actions.formatNumber(pr, "##.##"));
                            }
                        } else {
                            etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                        }


                        /*if(trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)){

                            if(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < 0){
                                etLimitPrice.setText(Actions.formatNumber(0, "##.##")) ;
                                price = 0;
                            }  else if (price < HiLimit) {
                                double pr = price + ticketPrice;
                                if(pr <= HiLimit ) {
                                    price = pr ;
                                    etLimitPrice.setText(Actions.formatNumber(pr, "##.##")) ;
                                }
                            } else {
                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        }
                        else{
                            if(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < stockQuotation.getLowlimit()){
                                etLimitPrice.setText(Actions.formatNumber(stockQuotation.getLowlimit(), "##.##")) ;
                                price = stockQuotation.getLowlimit();
                            }
                            else if (price < stockQuotation.getHiLimit()) {
                                double pr = price + ticketPrice;
                                if(pr <= stockQuotation.getHiLimit() ) {
                                    price = pr ;
                                    etLimitPrice.setText(Actions.formatNumber(pr, "##.##")) ;
                                }
                            } else {
                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        }*/

                    } else {

                        if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {

                            if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < 0) {
                                etLimitPrice.setText(Actions.formatNumber(0, "##.##"));
                                price = 0;
                            } else if (price < HiLimit) {
                                double pr = price + ticketPrice;
                                if (pr <= HiLimit) {
                                    price = pr;
                                    etLimitPrice.setText(Actions.formatNumber(pr, "##.##"));
                                }
                            } else {
                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        } else {
                            if (Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())) < stockQuotation.getLowlimit()) {
                                etLimitPrice.setText(Actions.formatNumber(stockQuotation.getLowlimit(), "##.##"));
                                price = stockQuotation.getLowlimit();
                            } else if (price < stockQuotation.getHiLimit()) {
                                double pr = price + ticketPrice;
                                if (pr <= stockQuotation.getHiLimit()) {
                                    price = pr;
                                    etLimitPrice.setText(Actions.formatNumber(pr, "##.##"));
                                }
                            } else {
                                etLimitPrice.setText(Actions.formatNumber(price, "##.##"));
                            }
                        }
                    }


                }

                quantityText = etQuantity.getText().toString();
                if (isFromOrderDetails && quantityText.length() > 0) {
                    quantity = Integer.parseInt(getNumberFromString(quantityText));
                }
                updateOverAllViews(price, quantity);
                setTick();
                break;
        }
    }


    private void setInitialData() {

        try {
            tvToolbarStatus.setText(MyApplication.marketStatus.getStatusName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvPortfolioNumber.setText(String.valueOf(MyApplication.currentUser.getPortfolioNumber()));

        etLimitPrice.setText(setEtPriceValue());

        trade.setPortfolioId(MyApplication.currentUser.getPortfolioId());
        trade.setPortfolioNumber(MyApplication.currentUser.getPortfolioNumber());
        //trade.setOrderType(LIMIT);

        if (isFromOrderDetails) {

            int remainingQuantity = onlineOrder.getQuantity() - onlineOrder.getQuantityExecuted();
            etQuantity.setText(String.valueOf(remainingQuantity));
            price = onlineOrder.getPrice();
            updateOverAllViews(price, remainingQuantity);
        }
    }


    private void showDateDialog() {

        Log.wtf("open", "date");
        myCalendar = Calendar.getInstance();
        myCalendar.roll(Calendar.DATE, 1);
        Calendar calendar = new GregorianCalendar();

        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatter);
        int dyear = myCalendar.get(Calendar.YEAR), dmonth = myCalendar.get(Calendar.MONTH), dday = myCalendar.get(Calendar.DAY_OF_MONTH);

        if (selectedPos == 5) {
            try {
                calendar.setTime(dateFormat.parse(etDurationType.getText().toString()));
                dyear = calendar.get(Calendar.YEAR);
                dmonth = calendar.get(Calendar.MONTH);
                dday = calendar.get(Calendar.DAY_OF_MONTH);

            } catch (Exception e) {
                Log.wtf("Exception", "Exception" + e.getMessage());
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(TradesActivity.this, date, dyear, dmonth, dday);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogs, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    // Do Stuff
                    Log.wtf("calendr ", "btn cancel click");
                    dialogs.dismiss();
                    //    dialog.dismiss();
                }
            }
        });

        datePickerDialog.show();
    }


    private void updateLabel(EditText editText) {
        SimpleDateFormat sdf = new SimpleDateFormat(setDateFormatter, Locale.ENGLISH);
        editText.setText(sdf.format(myCalendar.getTime()));
    }


    public void close(View v) {
        finish();
    }

    //<editor-fold desc="Design">
    private void setSellChecked(boolean sellChecked) {

        if (sellChecked) {

            btSell.setTextColor(ContextCompat.getColor(this, R.color.white));
            btSell.setBackground(ContextCompat.getDrawable(this, R.drawable.border_sell_selected));

            btBuy.setTextColor(ContextCompat.getColor(this, R.color.red_color));
            btBuy.setBackground(ContextCompat.getDrawable(this, R.drawable.border_buy_not_selected));

            llTradeSection.setBackgroundColor(ContextCompat.getColor(this, R.color.trade_sell_color));
        } else {

            btBuy.setTextColor(ContextCompat.getColor(this, R.color.white));
            btBuy.setBackground(ContextCompat.getDrawable(this, R.drawable.border_buy_selected));

            btSell.setTextColor(ContextCompat.getColor(this, R.color.green_color));
            btSell.setBackground(ContextCompat.getDrawable(this, R.drawable.border_sell_not_selected));

            llTradeSection.setBackgroundColor(ContextCompat.getColor(this, R.color.trade_buy_color));
        }
    }


    private void setLimitChecked(boolean limitChecked) {

        if (limitChecked) {

            btLimit.setTextColor(ContextCompat.getColor(this, R.color.white));
            btLimit.setBackground(ContextCompat.getDrawable(this, R.drawable.border_limit_selected));

//            btMarketPrice.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            btMarketPrice.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
            btMarketPrice.setBackground(ContextCompat.getDrawable(this, R.drawable.border_market_not_selected));
        } else {

            btMarketPrice.setTextColor(ContextCompat.getColor(this, R.color.white));
            btMarketPrice.setBackground(ContextCompat.getDrawable(this, R.drawable.border_market_selected));

//            btLimit.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            btLimit.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
            btLimit.setBackground(ContextCompat.getDrawable(this, R.drawable.border_limit_not_selected));
        }
    }


    private void showLimitPrice(boolean show) {
        if (show) {
            etLimitPrice.setBackgroundColor(getResources().getColor(R.color.colorLight));
//            etLimitPrice.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorMediumTransparent));

//            etLimitPrice.setTextColor(getResources().getColor(R.color.colorDark));
            etLimitPrice.setTextColor(getResources().getColor(R.color.black));
//            etLimitPrice.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ?  R.color.colorDark  : R.color.colorDarkInv));

            etLimitPrice.setEnabled(true);
            btLimitMinus.setEnabled(true);
            btLimitPlus.setEnabled(true);
        } else {
            etLimitPrice.setBackgroundColor(getResources().getColor(R.color.lightgrey));
            etLimitPrice.setTextColor(getResources().getColor(R.color.black));
            etLimitPrice.setEnabled(false);
            btLimitMinus.setEnabled(false);
            btLimitPlus.setEnabled(false);
        }

        if (!Actions.isMarketOpen()) {
            btLimit.setAlpha((float) 0.5);// setBackground(ContextCompat.getDrawable(this, R.drawable.border_limit_selected));
        }
    }
    //</editor-fold>


    public void setTradeType(View v) {

        LinearLayout.LayoutParams paramsElev = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        LinearLayout.LayoutParams paramsNull = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        paramsElev.setMargins(10, 10, 10, 10);
        paramsNull.setMargins(0, 0, 0, 0);

        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);

        switch (v.getId()) {

            case R.id.btSell:
               sellClick();
                break;
//
            case R.id.btBuy:
               buyClick();
                break;
        }

        setPriceWithMarketPrice(true);
    }


    private void buyClick(){
        tradeType = MyApplication.ORDER_BUY;
       if(!firstTime)
        etQuantity.setText("0");

       firstTime=false;
        setSellChecked(false);
        etLimitPrice.setText(setEtPriceValue());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    tvPurchasePowerValue.setLayoutParams(params);
//                    tvQuantityValue.setLayoutParams(paramsNull);

            tvPurchasePowerValue.setElevation(getResources().getDimension(R.dimen.padding));
            tvQuantityValue.setElevation(0);

            tvPurchasePowerValue.setBackgroundColor(getResources().getColor(R.color.even_green_color));
            btReview.setBackgroundColor(getResources().getColor(R.color.green_color));
//                    tvQuantityValue.setBackgroundColor(getResources().getColor(R.color.colorLight));
            tvQuantityValue.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorMedium : R.color.colorMediumInv));
        }

        trade.setTradeTypeID(tradeType);
        updateOverAllViews(price, quantity);
    }

    private void sellClick(){
        tradeType = MyApplication.ORDER_SELL;
        if(!firstTime)
          etQuantity.setText("0");

        firstTime=true;
        setSellChecked(true);
        etLimitPrice.setText(setEtPriceValue());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            tvQuantityValue.setElevation(getResources().getDimension(R.dimen.padding));
            tvPurchasePowerValue.setElevation(0);

            tvQuantityValue.setBackgroundColor(getResources().getColor(R.color.even_red_color));
            btReview.setBackgroundColor(getResources().getColor(R.color.red_color));
//                    tvPurchasePowerValue.setBackgroundColor(getResources().getColor(R.color.colorLight));
            tvPurchasePowerValue.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorMedium : R.color.colorMediumInv));
        }
        trade.setTradeTypeID(tradeType);
        updateOverAllViews(price, quantity);
    }

    public void setOrderType(View v) {

        switch (v.getId()) {

            case R.id.btLimit:
                orderType = MyApplication.LIMIT;
                setLimitChecked(true);
                showLimitPrice(true);

                etLimitPrice.setText(setEtPriceValue());

                trade.setOrderType(orderType);
                updateOverAllViews(price, quantity);
                break;

            case R.id.btMarketPrice:
                orderType = MyApplication.MARKET_PRICE;
                setLimitChecked(false);
                showLimitPrice(false);

                etLimitPrice.setText(setEtPriceValue());

                trade.setOrderType(orderType);
                updateOverAllViews(price, quantity);
                break;
        }
    }


    private String setEtPriceValue() {
        Log.wtf("setEtPriceValue", "setEtPriceValue");

        if (!isFromOrderDetails) {

            price = setPriceWithMarketPrice(true);

        } else {

            price = onlineOrder.getPrice();
        }

        trade.setOrderType(orderType);
        updateOverAllViews(price, quantity);
        setTick();

        String priceValue = "";
        if (price > 100.9) {

            priceValue = Actions.formatNumber(price, Actions.NoDecimalThousandsSeparator);
            return priceValue;

        } else {

            if (price == 0.0)
                return "0.0";
            else {

                priceValue = Actions.formatNumber(price, Actions.OneDecimalThousandsSeparator);
                return priceValue;
            }
        }
    }


    private void updateOverAllViews(double price, int quantity) {
        setTradeData(quantity, price, tradeType);
        if (quantity == 0) {
            tvCostValue.setText("0");
            tvCommissionValue.setText("0");
            tvOverallValue.setText("0");
        } else {
            tvCostValue.setText(Actions.formatNumber(trade.getOverallTotal(), Actions.ThreeDecimalThousandsSeparator));

            tvCommissionValue.setText(Actions.formatNumber(trade.getCommission(), Actions.ThreeDecimalThousandsSeparator));

            tvOverallValue.setText(Actions.formatNumber(trade.getCost(), Actions.ThreeDecimalThousandsSeparator));
        }
    }


    public void setQuantity(View v) {

        if (etQuantity.getText().length() > 0) {
            try {
                quantity = Integer.parseInt(getNumberFromString(etQuantity.getText().toString()));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                quantity = 0;
            }

        } else {

            quantity = 0;
        }

        switch (v.getId()) {

            case R.id.btQuantityMinus:

                if (quantity > 1) {

                    quantity -= ticketQtt;
                    etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                } else {
                    quantity = 0;
                    etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                }
                updateOverAllViews(price, quantity);
                break;

            case R.id.btQuantityPlus:
                Log.wtf("btQuantityPlus", "quantity = " + quantity);
                quantity += ticketQtt;
                etQuantity.setText(Actions.formatNumber(quantity, Actions.NoDecimalThousandsSeparator));
                updateOverAllViews(price, quantity);
                break;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        MyApplication.sessionOut = Calendar.getInstance();
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


    public void goTo(View v) {

        switch (v.getId()) {

            case R.id.btTimeSales:
                startActivity(new Intent(TradesActivity.this, TimeSalesActivity.class)
                        .putExtra("stockId", stockQuotation.getStockID())
                        .putExtra("securityId", stockQuotation.getSecurityId())
                        .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn())
                );
                break;

            case R.id.btOrderBook:
                startActivity(new Intent(TradesActivity.this, StockOrderBookActivity.class)
                        .putExtra("stockId", stockQuotation.getStockID())
                        .putExtra("securityId", stockQuotation.getSecurityId())
                        .putExtra("last",stockQuotation.getLast())
                        .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn())
                );
                break;

            case R.id.btReview:
                String quantityTxt = etQuantity.getText().toString();
                String limitTxt = etLimitPrice.getText().toString();

                Animation shake = AnimationUtils.loadAnimation(TradesActivity.this, R.anim.shake);

                if (cbPrivate.isChecked()) {

                    try {
                        quantity = Integer.parseInt(getNumberFromString(quantityTxt));
                    } catch (Exception e) {
                        quantity = 0;
                        e.printStackTrace();
                    }

                    price = Double.parseDouble(getNumberFromString(limitTxt));
                    setTradeData(quantity, price, tradeType);

                    startActivity(new Intent(TradesActivity.this, TradeConfirmationActivity.class)
                            .putExtra("isUpdate", isFromOrderDetails)
                            .putExtra("trade", trade));
                } else if ((limitTxt.length() == 0 || getNumberFromString(limitTxt).equals("0"))) {

                    etLimitPrice.startAnimation(shake);

                } else if ((quantityTxt.length() == 0 || getNumberFromString(quantityTxt).equals("0"))) {

                    etQuantity.startAnimation(shake);

                } else {

                    try {
                        quantity = Integer.parseInt(getNumberFromString(etQuantity.getText().toString()));
                    } catch (Exception e) {
                        //quantity = 0;
                        e.printStackTrace();
                    }

                    price = Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString()));
                    setTradeData(quantity, price, tradeType);

                    if (tradeType == MyApplication.ORDER_BUY) { //أBuy

                        //<editor-fold desc="buy validation">
                        /*if (trade.getPurchasePower() < trade.getOverallTotal()) {

                            //dialog
                            Actions.CreateDialog(TradesActivity.this, getResources().getString(R.string.error_purchase_power), false, false);
                        } else {

                            Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
                            Log.wtf("trade", " getDurationType = " + trade.getDurationType());
                            Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());

                            startActivity(new Intent(TradesActivity.this, TradeConfirmationActivity.class)
                                    .putExtra("isUpdate", isFromOrderDetails)
                                    .putExtra("trade", trade));
                        }*/
                        //</editor-fold>

                        Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
                        Log.wtf("trade", " getDurationType = " + trade.getDurationType());
                        Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());

                        startActivity(new Intent(TradesActivity.this, TradeConfirmationActivity.class)
                                .putExtra("isUpdate", isFromOrderDetails)
                                .putExtra("trade", trade));
                    } else { //Sell

                        //<editor-fold desc="sell validaiton">
                        /*if (trade.getAvailableShareCount() < quantity) {

                            Actions.CreateDialog(TradesActivity.this, getResources().getString(R.string.error_share_count), false, false);

                        } else {

                            Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
                            Log.wtf("trade", " getDurationType = " + trade.getDurationType());
                            Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());

                            startActivity(new Intent(TradesActivity.this, TradeConfirmationActivity.class)
                                    .putExtra("isUpdate", isFromOrderDetails)
                                    .putExtra("trade", trade));
                        }*/
                        //</editor-fold>

                        Log.wtf("trade", " getDurationTypeId = " + trade.getDurationTypeId());
                        Log.wtf("trade", " getDurationType = " + trade.getDurationType());
                        Log.wtf("trade", " getGoodUntilDate = " + trade.getGoodUntilDate());

                        startActivity(new Intent(TradesActivity.this, TradeConfirmationActivity.class)
                                .putExtra("isUpdate", isFromOrderDetails)
                                .putExtra("trade", trade));
                    }
                }
                break;

            case R.id.btCancel:
                finish();
                break;
        }
    }


    private String setOrderDate() {

        String date = "";

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - hh:mm:ss", Locale.ENGLISH);
        date = df.format(c.getTime());

        return date;
    }


    private void setTradeData(int quantity, double price, int tradeType) {

        BrokerageFee brokerageFee;

        try {
            brokerageFee = Actions.getBrokerageFeeByInstrumentID(MyApplication.allBrokerageFees, stockQuotation.getInstrumentId());
        } catch (Exception e) {
            e.printStackTrace();
            brokerageFee = new BrokerageFee();
            brokerageFee.setTotalBrokerageFee(0.00125);
            brokerageFee.setClearing(0.5);
        }

        double total = (price * quantity) / 1000;

        double commission;

        double brokerage = total * brokerageFee.getTotalBrokerageFee();

        if (brokerage < brokerageFee.getMinimumBrokerageFee())
            brokerage = brokerageFee.getMinimumBrokerageFee();

        if (total >= 50) {

            commission = brokerage + brokerageFee.getClearing();
        } else {

            commission = brokerage;
        }

        String orderDuration = "";
        String goodUntilDate = "";
        orderDuration = MyApplication.lang == MyApplication.ARABIC ? orderDurationType.getDescriptionAr() : orderDurationType.getDescriptionEn();

        if (orderDurationType.getID() == 6) {
            orderDuration = orderDuration + " " + etDurationType.getText().toString();
            goodUntilDate = etDurationType.getText().toString();
        }

        trade.setDurationTypeId(orderDurationType.getID());
        trade.setDurationType(orderDuration);
        trade.setOverallTotal(Actions.roundNumber(total, Actions.ThreeDecimal));
        trade.setCommission(Actions.roundNumber(commission, Actions.ThreeDecimal));
        //trade.setStockQuotation(stockQuotation);

        trade.setGoodUntilDate(goodUntilDate + " 00:00:00");
        trade.setTradeTypeID(tradeType);
        trade.setPrice(price);
        trade.setQuantity(quantity);
        trade.setCost(tradeType == MyApplication.ORDER_SELL ? (total - commission) : (total + commission));

        if (cbPrivate.isChecked()) {

            trade.setStatusTypeId(16);
            trade.setOperationTypeID(4);
        } else {

            trade.setStatusTypeId(1);
            trade.setOperationTypeID(0);
        }

        trade.setDate(setOrderDate());
        if (isFromOrderDetails) {
            trade.setReference(onlineOrder.getReference());
            trade.setExecutedQuantity(onlineOrder.getQuantityExecuted());
        } else {

            trade.setReference(0);
        }
    }

    private void setPortfolioData(Trade trade) {

        //<editor-fold desc="Trade object">
        tvCloseValue.setText(String.valueOf(trade.getStockQuotation().getPreviousClosing()));
        tvLastValue.setText(String.valueOf(trade.getStockQuotation().getLast()));
        tvBidValue.setText(String.valueOf(trade.getStockQuotation().getBid()));
        tvAskValue.setText(String.valueOf(trade.getStockQuotation().getAsk()));

        tvHighValue.setText(String.valueOf(trade.getStockQuotation().getHiLimit()));
        /*try {

            tvHighValue.setText(Actions.formatNumber(trade.getStockQuotation().getHiLimit(), Actions.TwoDecimal)); // kenet One Decimal
        } catch (Exception e) {
            e.printStackTrace();
            tvHighValue.setText(String.valueOf(trade.getStockQuotation().getHiLimit()));
        }
        try {
            tvLowValue.setText(Actions.formatNumber(trade.getStockQuotation().getLowlimit(), Actions.TwoDecimal)); // kenet One Decimal
        } catch (Exception e) {
            e.printStackTrace();
            tvLowValue.setText(String.valueOf(trade.getStockQuotation().getLowlimit()));
        }*/

        tvLowValue.setText(String.valueOf(trade.getStockQuotation().getLowlimit()));

        String stockName = "--";
        if (MyApplication.lang == MyApplication.ARABIC) {

            tvUserName.setText(MyApplication.currentUser.getNameAr());
            //   tvStockTitle.setText(trade.getStockQuotation().getNameAr());
            stockName = trade.getStockQuotation().getSecurityId() + "-" + trade.getStockQuotation().getSymbolAr(); //getStockID()
            tvSessionValue.setText(trade.getStockQuotation().getSessionNameAr());
//            tvInstrumentValue.setText(trade.getStockQuotation().getInstrumentNameAr());
        } else {

            tvUserName.setText(MyApplication.currentUser.getNameEn());
            //   tvStockTitle.setText(trade.getStockQuotation().getNameEn());
            stockName = trade.getStockQuotation().getSecurityId() + "-" + trade.getStockQuotation().getSymbolEn(); //getStockID()
            tvSessionValue.setText(trade.getStockQuotation().getSessionNameEn());
//            tvInstrumentValue.setText(trade.getStockQuotation().getInstrumentNameEn());
        }

        tvInstrumentValue.setText(Actions.formatNumber(trade.getStockQuotation().getNormalMarketSize(), Actions.NoDecimalThousandsSeparator));
        tvStockTitle.setText(stockName);
        //</editor-fold>

        tvPurchasePowerValue.setText(Actions.formatNumber(trade.getPurchasePower(), Actions.ThreeDecimalThousandsSeparator));

        tvQuantityValue.setText(Actions.formatNumber(trade.getAvailableShareCount(), Actions.NoDecimalSeparator));
    }

    private void setTypefaces() {

        Actions.setTypeface(new TextView[]{tvUserName, tvPortfolioNumber, tvStockTitle, tvInstrumentValue, tvSessionValue},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        Actions.setTypeface(new TextView[]{tvCloseValue, tvLastValue, tvBidValue, tvAskValue, tvHighValue,
                tvLowValue, tvPurchasePowerValue, tvQuantityValue, tvCostValue, tvCommissionValue, tvOverallValue}, MyApplication.giloryBold);

        etLimitPrice.setTypeface(MyApplication.giloryBold);
        etQuantity.setTypeface(MyApplication.giloryBold);
        etDurationType.setTypeface(MyApplication.giloryBold);
        close.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public String getNumberFromString(String qtty) {
        String nQtty = "";
        //Log.wtf("qtty" , "length = " + qtty.length());
        for (int i = 0; i < qtty.length(); i++) {
            //Log.wtf("nQtty" , nQtty + " - i = " + i);
            /*if(qtty.charAt(i) == '.'){
                break;
            }*/
            if (qtty.charAt(i) == '.' || qtty.charAt(i) == '0' || qtty.charAt(i) == '1' || qtty.charAt(i) == '2' || qtty.charAt(i) == '3' || qtty.charAt(i) == '4' || qtty.charAt(i) == '5'
                    || qtty.charAt(i) == '6' || qtty.charAt(i) == '7' || qtty.charAt(i) == '8' || qtty.charAt(i) == '9') {
                nQtty += qtty.charAt(i);
            }
        }
        if (nQtty.equals("")) {
            nQtty = "0";
        }
        return nQtty;
    }

    public Double setPriceWithMarketPrice(Boolean onLoad) {
        Double pricee = 0.0;

        Boolean condition = (MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.PreClose.getValue()
                || MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.TradeAtLast.getValue()
                || MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.Acceptance.getValue()
                || MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.PreOpen.getValue()
                || MyApplication.marketStatus.getStatusID() == enums.MarketStatuss.CLOSE_ACCPT2.getValue());

        Log.wtf("condition", ": " + condition);
        Log.wtf("MyApplication.marketID", ": " + MyApplication.marketID);
        Log.wtf("enums.MarketType.KWOTC.getValue()", ": " + enums.MarketType.KWOTC.getValue());
        Log.wtf("orderType", ": " + orderType);
        Log.wtf("MyApplication.MARKET_PRICE", ": " + MyApplication.MARKET_PRICE);


        if (MyApplication.marketID.equals(Integer.toString(enums.MarketType.KWOTC.getValue()))) {

            pricee = price;
        } else {
            if (condition && onLoad) {
                pricee = (stockQuotation.getLast() > 0 ? stockQuotation.getLast() : stockQuotation.getReferencePrice());
            } else {
                if (onLoad) {

                    if (MyApplication.defaultPriceType == 0) {
                        pricee = 0.0;
                    } else if (MyApplication.defaultPriceType == 1) {

                        if (tradeType == MyApplication.ORDER_SELL) {
                            pricee = (stockQuotation.getAsk() > 0 ? stockQuotation.getAsk() : stockQuotation.getHiLimit());
                        } else {
                            pricee = (stockQuotation.getBid() > 0 ? stockQuotation.getBid() : stockQuotation.getLowlimit());
                        }
                    } else if (MyApplication.defaultPriceType == 2) {

                        pricee = (tradeType == MyApplication.ORDER_SELL) ? stockQuotation.getHiLimit() : stockQuotation.getLowlimit();
                    }


                } else {
                    pricee = price;
                }
            }
        }
        price = pricee;
        return pricee;

    }

    public Double getDefaultPrice() {
        Double pricee = 0.0;

        Log.wtf("MyApplication.defaultPriceType", ": " + MyApplication.defaultPriceType);

        if (MyApplication.defaultPriceType == 0) {
            pricee = 0.0;
        } else if (MyApplication.defaultPriceType == 1) {

            if (tradeType == MyApplication.ORDER_SELL) {
                pricee = (stockQuotation.getAsk() > 0 ? stockQuotation.getAsk() : stockQuotation.getHiLimit());
            } else {
                pricee = (stockQuotation.getBid() > 0 ? stockQuotation.getBid() : stockQuotation.getLowlimit());
            }
        } else if (MyApplication.defaultPriceType == 2) {

            pricee = (tradeType == MyApplication.ORDER_SELL) ? stockQuotation.getHiLimit() : stockQuotation.getLowlimit();
        }
        return pricee;
    }

    private class GetOrderDurationTypes extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradesActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetOrderDurationTypes.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("key", MyApplication.currentUser.getKey());
            parameters.put("MarketId", MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, TradesActivity.this, parameters);

                MyApplication.allOrderDurationType.addAll(GlobalFunctions.GetOrderDurationList(result));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetOrderDurationTypes.getKey(), Toast.LENGTH_LONG).show();
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
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (getIntent().hasExtra("isFromOrderDetails")) {
                Log.wtf("onPostExecute - isFromOrderDetails", "isFromOrderDetails ");
                int index = Actions.returnDurationIndex(onlineOrder.getDurationID());
                Log.wtf("onPostExecute - isFromOrderDetails", "index : " + index);

                selectedPos = index;
                try {
                    setOrderDuration(index);
                }catch (Exception e){}

                //   previous = MyApplication.allOrderDurationType.get(index);
//                trade.setDurationType(MyApplication.lang == MyApplication.ARABIC ? adapter.getItem(index).getDescriptionAr() : adapter.getItem(index).getDescriptionEn());
                trade.setDurationType(MyApplication.lang == MyApplication.ARABIC ? MyApplication.allOrderDurationType.get(index).getDescriptionAr() : MyApplication.allOrderDurationType.get(index).getDescriptionEn());
            } else {

                Log.wtf("onPostExecute - isFromOrderDetails", " Not have extra isFromOrderDetails ");
                Log.wtf("Actions - isMarketOpen : ", " Actions.isMarketOpen() : " + Actions.isMarketOpen());
                if (!Actions.isMarketOpen()) {
                    try{
                    selectedPos = Actions.returnDurationIndex(1);
                  setOrderDuration(Actions.returnDurationIndex(1));}catch (Exception e){}
                } else {
                        try {  selectedPos = Actions.returnDurationIndex(0);

                    setOrderDuration(Actions.returnDurationIndex(0));
                }catch (Exception e){}

                }
            }
        }
    }

    private class GetTradeInfo extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetTradeInfo.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("portfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));
            parameters.put("stockId", stockQuotation.getStockID() + "");
            parameters.put("MarketId", MyApplication.marketID);

//            while (running) {

//                if (isCancelled())
//                    break;

            for (Map.Entry<String, String> map : parameters.entrySet()) {
                Log.wtf("TradesActivity GetTradeInfo", "parameters : " + map.getKey() + "= " + map.getValue());
            }
            try {

                result = ConnectionRequests.GET(url, TradesActivity.this, parameters);

//                    publishProgress(result);
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetTradeInfo.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }


            return result;
        }


        @Override
        protected void onPostExecute(String aVoid) {

            super.onPostExecute(aVoid);

            Log.wtf("GetTradeInfo", "GetTradeInfo");

            try {
                trade = GlobalFunctions.GetTradeInfo(aVoid);

                try {
                    /*if (isFromOrderDetails) {

                        if (tradeType == MyApplication.ORDER_SELL){

                            trade.setAvailableShareCount(trade.getAvailableShareCount() + (onlineOrder.getQuantity() - onlineOrder.getQuantityExecuted()));
                        }else{

                            trade.setAvailableShareCount(trade.getAvailableShareCount());
                        }
                    }*/

                    trade.setAvailableShareCount(trade.getAvailableShareCount());
                } catch (Exception e) {
                    try {
                        Toast.makeText(TradesActivity.this, "error in setAvailableShareCount", Toast.LENGTH_SHORT).show();
                    } catch (Exception es) {
                        Log.wtf("setAvailableShareCount  ", "error : " + es.getMessage());
                    }
                }

                //<editor-fold desc="setting data">
                if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {
                    stockQuotation.setHiLimit(0);
                    stockQuotation.setLowlimit(0);
                } else {
                    stockQuotation.setHiLimit(trade.getStockQuotation().getHiLimit());
                    stockQuotation.setLowlimit(trade.getStockQuotation().getLowlimit());
                }

                if (trade.getStockQuotation().getSessionId().equals(MyApplication.CB_Auction_id)) {
                    tvSessionValue.setBackgroundColor(getResources().getColor(R.color.orange));
                }

                stockQuotation.setPreviousClosing(trade.getStockQuotation().getPreviousClosing());
                stockQuotation.setLast(trade.getStockQuotation().getLast());
                stockQuotation.setBid(trade.getStockQuotation().getBid());
                stockQuotation.setAsk(trade.getStockQuotation().getAsk());
                stockQuotation.setInstrumentId(trade.getStockQuotation().getInstrumentId());
                stockQuotation.setInstrumentNameAr(trade.getStockQuotation().getInstrumentNameAr());
                stockQuotation.setInstrumentNameEn(trade.getStockQuotation().getInstrumentNameEn());
                stockQuotation.setLow(trade.getStockQuotation().getLow());
                stockQuotation.setNumberOfOrders(trade.getStockQuotation().getNumberOfOrders());
                stockQuotation.setSessionId(trade.getStockQuotation().getSessionId());
                stockQuotation.setSessionNameAr(trade.getStockQuotation().getSessionNameAr());
                stockQuotation.setSessionNameEn(trade.getStockQuotation().getSessionNameEn());
                stockQuotation.setStockID(trade.getStockQuotation().getStockID());
                stockQuotation.setStockTradingStatus(trade.getStockQuotation().getStockTradingStatus());
                stockQuotation.setVolumeBid(trade.getStockQuotation().getVolumeBid());
                stockQuotation.setVolume(trade.getStockQuotation().getVolume());
                stockQuotation.setVolumeAsk(trade.getStockQuotation().getVolumeAsk());
                stockQuotation.setTickDirection(trade.getStockQuotation().getTickDirection());
                stockQuotation.setSymbolAr(trade.getStockQuotation().getSymbolAr());
                stockQuotation.setSymbolEn(trade.getStockQuotation().getSymbolEn());
                stockQuotation.setNormalMarketSize(trade.getStockQuotation().getNormalMarketSize());

                stockQuotation.setSecurityId(trade.getStockQuotation().getSecurityId());
//                tickDirection = stockQuotation.getTickDirection();
                trade.setStockQuotation(stockQuotation);
                trade.setOrderType(orderType);
                //</editor-fold>
                setPortfolioData(trade);

                if (firstOpen && !isFromOrderDetails) {
                    double price = 0.0;

//                    etLimitPrice.setText("" + price);

                    price = setPriceWithMarketPrice(true);
                    //getDefaultPrice();

                    /*if (MyApplication.defaultPriceType == 0) {
                        price = 0;
                    }
                    else if (MyApplication.defaultPriceType == 1) {

                        if (tradeType == MyApplication.ORDER_SELL) {
                            price = (stockQuotation.getBid() > 0 ? stockQuotation.getBid() : stockQuotation.getHiLimit());
                        } else {
                            price = (stockQuotation.getAsk() > 0 ? stockQuotation.getAsk() : stockQuotation.getLowlimit());
                        }
                    }
                    else if (MyApplication.defaultPriceType == 2) {

                        price = (tradeType == MyApplication.ORDER_SELL) ? stockQuotation.getHiLimit() : stockQuotation.getLowlimit();
                    }*/

                    /*

                      if(appDelegate.parameter.defaultPriceOnTrade == "0") {
                        price = 0
                    }
                    else if(appDelegate.parameter.defaultPriceOnTrade == "1") {
                        price = isSell ? (self.data.stockQuotation.bid > 0 ? self.data.stockQuotation.bid : self.data.stockQuotation.hiLimit) : (self.data.stockQuotation.ask > 0 ? self.data.stockQuotation.ask : self.data.stockQuotation.lowLimit)
                    }
                    else if(appDelegate.parameter.defaultPriceOnTrade == "2") {
                        price = isSell ? self.data.stockQuotation.hiLimit : self.data.stockQuotation.lowLimit
                    }
                     */

                    etLimitPrice.setText("" + price);

                    if (price == 0.0) {
                        etLimitPrice.setText("0.0");
                    } else {

                        if (price > 100.9) {
                            etLimitPrice.setText(Actions.formatNumber(price, Actions.NoDecimalThousandsSeparator));
                        } else {
                            etLimitPrice.setText(Actions.formatNumber(price, Actions.OneDecimalThousandsSeparator));
                        }
                    }
                }
                firstOpen = false;
                if(tradeType == MyApplication.ORDER_BUY){
                    // btSell.performClick();
                    buyClick();
                }else {
                    sellClick();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            swipeContainer.setRefreshing(false);
        }
    }


}
