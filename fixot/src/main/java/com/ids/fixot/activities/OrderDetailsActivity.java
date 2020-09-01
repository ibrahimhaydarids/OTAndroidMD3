package com.ids.fixot.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import com.ids.fixot.adapters.ValuesListArrayAdapter;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.OrderDurationType;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.Trade;
import com.ids.fixot.model.ValueItem;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class OrderDetailsActivity extends AppCompatActivity implements OrderDurationTypeAdapter.RecyclerViewOnItemClickListener, MarketStatusListener {

    OrderDurationType orderDurationType = new OrderDurationType();
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    RelativeLayout rlUserHeader, rlLayout;
    TextView tvUserName, tvPortfolioNumber, tvStockTitle;
    RecyclerView rvOrderDetails;
    GridLayoutManager llm;
    StockQuotation stockQuotation;
    OrderDurationTypeAdapter adapterDuration;
    ValuesListArrayAdapter adapter;
    Button btCancel, btEdit, btQuickEdit, btActivate;
    ImageView ivPortfolio;
    OnlineOrder onlineOrder;
    Trade trade = new Trade();
    GetTradeInfo getTradeInfo;
    FloatingActionMenu famOrderMenu, famOrderMenuRTL;
    FloatingActionButton fabFastEdit, fabEdit, fabCancel;
    FloatingActionButton fabFastEditRTL, fabEditRTL, fabCancelRTL;
    Double orderPrice = 0.0;
    int orderQuantity, orderType;
    String orderGoodUntilDate = "";
    String dateFormatter = "dd/MM/yyyy 00:00:00";
    //MKobaissy Popup
    AlertDialog.Builder builder;
    AlertDialog dialog, dialogEdit;
    RecyclerView rvDurationType;
    EditText etDurationType, etDialogConfirm;
    EditText etLimitPrice;
    Button btLimitPlus, btLimitMinus;
    int selectedPos = -3;
    //    double price = 0;
    double ticketPrice = 0.1, ticketQtt = 0.1;
    double HiLimit = 1000000000;
    private BroadcastReceiver receiver;
    private boolean started = false;
    private ArrayList<ValueItem> allValueItems = new ArrayList<>();

    public OrderDetailsActivity() {
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
        setContentView(R.layout.activity_order_details);

        Actions.initializeBugsTracking(this);
        Actions.initializeToolBar(getString(R.string.order_details), OrderDetailsActivity.this);
        Actions.showHideFooter(this);

        started = true;

        findViews();

        if (getIntent().hasExtra("order")) {

            onlineOrder = getIntent().getExtras().getParcelable("order");
            stockQuotation = Actions.getStockQuotationById(MyApplication.stockQuotations, Integer.parseInt(onlineOrder.getStockID()));
            stockQuotation.setStockID(Integer.parseInt(onlineOrder.getStockID()));
            Log.wtf("onlineOrder Good Until Date", "Date = " + onlineOrder.getGoodUntilDate());
            Log.wtf("onlineOrder Good Until Date", "Date = " + onlineOrder.getDurationID());
            allValueItems.addAll(onlineOrder.getAllvalueItems());
            adapter.notifyDataSetChanged();
        } else {

            onlineOrder = new OnlineOrder();
            stockQuotation = new StockQuotation();
        }

        //onlineOrder.setCanUpdate(true);
//        onlineOrder.setCanDelete(true);

        setOrderOptions();

        //String stockTitle = stockQuotation.getStockID() + "-" + (MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getSymbolEn());
        String stockTitle = onlineOrder.getSecurityId() + "-" + onlineOrder.getStockSymbol(); //getStockID
        tvStockTitle.setText(stockTitle);

        Actions.overrideFonts(this, rlLayout, false);

        if (MyApplication.lang == MyApplication.ARABIC) {

            tvUserName.setText(MyApplication.currentUser.getNameAr());
            tvUserName.setTypeface(MyApplication.droidbold);
            tvStockTitle.setTypeface(MyApplication.droidbold);
            tvPortfolioNumber.setTypeface(MyApplication.droidbold);
        } else {

            tvUserName.setText(MyApplication.currentUser.getNameEn());
            tvUserName.setTypeface(MyApplication.giloryBold);
            tvStockTitle.setTypeface(MyApplication.giloryBold);
            tvPortfolioNumber.setTypeface(MyApplication.giloryBold);
        }

        getTradeInfo = new GetTradeInfo();
        getTradeInfo.execute();

        setTick();

    }


    public void setTick() {
        ticketQtt = 1;
        for (int i = 0; i < MyApplication.units.size(); i++) {
            if (MyApplication.units.get(i).getFromPrice() <= orderPrice && orderPrice <= MyApplication.units.get(i).getToPrice()) {
                ticketPrice = MyApplication.units.get(i).getPriceUnit();
//                ticketQtt = MyApplication.units.get(i).getQuantityUnit();
                Log.wtf("setTick - orderPrice Change", "price = " + orderPrice + " / ticketPrice = " + ticketPrice + " / ticketQtt = " + ticketQtt);
            }
        }
        if (orderPrice > 100.9) {
            etLimitPrice.setText(Actions.formatNumber(Double.parseDouble(getNumberFromString(etLimitPrice.getText().toString())), Actions.NoDecimalSeparator));
        }
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

    private void setOrderOptionsMenu() {

//        famOrderMenu.setVisibility(MyApplication.lang == MyApplication.ENGLISH ? View.VISIBLE : View.GONE);
//        famOrderMenuRTL.setVisibility(MyApplication.lang == MyApplication.ARABIC ? View.VISIBLE : View.GONE);

        famOrderMenu.setVisibility(View.GONE);
        famOrderMenuRTL.setVisibility(View.GONE);
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

    private void setOrderOptions() {

        orderType = onlineOrder.getOrderTypeID();

//        fabCancel.setEnabled(onlineOrder.isCanDelete());
//        fabEdit.setEnabled(onlineOrder.isCanUpdate());
//        fabFastEdit.setEnabled(onlineOrder.isCanUpdate());
//
//        fabCancelRTL.setEnabled(onlineOrder.isCanDelete());
//        fabEditRTL.setEnabled(onlineOrder.isCanUpdate());
//        fabFastEditRTL.setEnabled(onlineOrder.isCanUpdate());

//        btCancel.setEnabled(onlineOrder.isCanDelete());
//        btEdit.setEnabled(onlineOrder.isCanUpdate());
//        btQuickEdit.setEnabled(onlineOrder.isCanUpdate());

        if (MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true)) {

            btCancel.setBackgroundColor(onlineOrder.isCanDelete() ? getResources().getColor(R.color.red_color) : getResources().getColor(R.color.gray));
            btEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDark) : getResources().getColor(R.color.gray));
            btQuickEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDark) : getResources().getColor(R.color.gray));
        } else {

            btCancel.setBackgroundColor(onlineOrder.isCanDelete() ? getResources().getColor(R.color.red_color) : getResources().getColor(R.color.grayInv));
            btEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDarkInv) : getResources().getColor(R.color.grayInv));
            btQuickEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDarkInv) : getResources().getColor(R.color.grayInv));
        }

        btActivate.setVisibility(onlineOrder.getStatusID() == MyApplication.STATUS_PRIVATE ? View.VISIBLE : View.GONE);

        setUpdate(false);
        setDelete(false);

        if (onlineOrder.isCanUpdate()) {
            if (MyApplication.parameter.isCanUserManageTraderOrder()) {
                setUpdate(true);
            } else {
                if (MyApplication.parameter.getDefaultDMABrokerEmployeeID() == onlineOrder.getBrokerEmployeeID()) {
                    setUpdate(true);
                } else {
                    setUpdate(false);
                }
            }
        }

        if (onlineOrder.isCanDelete()) {
            if (MyApplication.parameter.isCanUserManageTraderOrder()) {
                setDelete(true);
            } else {
                if (MyApplication.parameter.getDefaultDMABrokerEmployeeID() == onlineOrder.getBrokerEmployeeID()) {
                    setDelete(true);
                } else {
                    setDelete(false);
                }
            }
        }

    }

    public void setUpdate(Boolean stt) {
        fabEdit.setEnabled(stt);
        fabFastEdit.setEnabled(stt);

        fabEditRTL.setEnabled(stt);
        fabFastEditRTL.setEnabled(stt);

        btEdit.setEnabled(stt);
        btQuickEdit.setEnabled(stt);

        if (MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true)) {

            btEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDark) : getResources().getColor(R.color.gray));
            btQuickEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDark) : getResources().getColor(R.color.gray));
        } else {

            btEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDarkInv) : getResources().getColor(R.color.grayInv));
            btQuickEdit.setBackgroundColor(onlineOrder.isCanUpdate() ? getResources().getColor(R.color.colorDarkInv) : getResources().getColor(R.color.grayInv));
        }
    }

    public void setDelete(Boolean stt) {
        fabCancelRTL.setEnabled(stt);
        fabCancel.setEnabled(stt);
        btCancel.setEnabled(stt);

        if (MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true)) {

            btCancel.setBackgroundColor(stt ? getResources().getColor(R.color.red_color) : getResources().getColor(R.color.gray));
        } else {

            btCancel.setBackgroundColor(stt ? getResources().getColor(R.color.red_color) : getResources().getColor(R.color.grayInv));
        }
    }


    public void back(View v) {

        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);
        Actions.checkLanguage(this, started);

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
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }


    private void findViews() {

        famOrderMenu = findViewById(R.id.famOrderMenu);
        fabFastEdit = findViewById(R.id.fabFastEdit);
        fabEdit = findViewById(R.id.fabEdit);
        fabCancel = findViewById(R.id.fabCancel);

        famOrderMenuRTL = findViewById(R.id.famOrderMenuRTL);
        fabFastEditRTL = findViewById(R.id.fabFastEditRTL);
        fabEditRTL = findViewById(R.id.fabEditRTL);
        fabCancelRTL = findViewById(R.id.fabCancelRTL);

        rlUserHeader = findViewById(R.id.rlUserHeader);
        tvUserName = rlUserHeader.findViewById(R.id.tvUserName);
        tvPortfolioNumber = rlUserHeader.findViewById(R.id.tvPortfolioNumber);
        ivPortfolio = rlUserHeader.findViewById(R.id.ivPortfolio);
        rlLayout = findViewById(R.id.rlLayout);
        rvOrderDetails = findViewById(R.id.rvOrderDetails);
        tvStockTitle = findViewById(R.id.tvStockTitle);
        btEdit = findViewById(R.id.btEdit);
        btQuickEdit = findViewById(R.id.btQuickEdit);
        btActivate = findViewById(R.id.btActivate);
        btCancel = findViewById(R.id.btCancel);
        llm = new GridLayoutManager(this, MyApplication.VALUES_SPAN_COUNT);
        rvOrderDetails.setLayoutManager(llm);

        adapter = new ValuesListArrayAdapter(this, allValueItems);
        rvOrderDetails.setAdapter(adapter);


        setOrderOptionsMenu();


        //btEdit.setEnabled(true);
    }

    private void updateLabel(EditText editText) {
        String dateFormatters = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatters, Locale.ENGLISH);
        editText.setText(sdf.format(myCalendar.getTime()));

        sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
        orderGoodUntilDate = sdf.format(myCalendar.getTime());
    }

    public void goTo(View v) {

        switch (v.getId()) {

            case R.id.btTimeSales:
                startActivity(new Intent(OrderDetailsActivity.this, TimeSalesActivity.class)
                        .putExtra("stockId", Integer.parseInt(onlineOrder.getStockID()))
                        .putExtra("stockName", onlineOrder.getStockName())//MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn())
                );
                break;

            case R.id.btOrderBook:
                startActivity(new Intent(OrderDetailsActivity.this, StockOrderBookActivity.class)
                        .putExtra("stockId", Integer.parseInt(onlineOrder.getStockID()))
                        .putExtra("last",0)
                        .putExtra("stockName", onlineOrder.getStockName())//MyApplication.lang == MyApplication.ARABIC ? stockQuotation.getSymbolAr() : stockQuotation.getNameEn())
                );
                break;

            case R.id.fabFastEdit:
                fastEdit();
                break;

            case R.id.fabFastEditRTL:
                fastEdit();
                break;

            case R.id.btQuickEdit:
                fastEdit();
                break;

            case R.id.fabEdit:
                fbEdit();
                break;

            case R.id.fabEditRTL:
                fbEdit();
                break;

            case R.id.btEdit:
                fbEdit();
                break;

            case R.id.btActivate:
                showActivateDialog(this, onlineOrder);
                break;

            case R.id.btCancel:
                if (onlineOrder.isCanDelete()) {
                    showCancelDialog();
                }
                break;

            case R.id.fabCancel:
                if (onlineOrder.isCanDelete()) {
                    showCancelDialog();
                }
                break;

            case R.id.fabCancelRTL:
                if (onlineOrder.isCanDelete()) {
                    showCancelDialog();
                }
                break;
        }
    }


    private void fastEdit() {
        if (onlineOrder.isCanUpdate()) {
            if (MyApplication.allOrderDurationType.size() > 0) {
                showFastEditDialog(MyApplication.allOrderDurationType);
            } else {
                new GetOrderDurationTypes().execute();
            }
        }
    }


    private void fbEdit() {
        if (onlineOrder.isCanUpdate()) {
            try {
                Bundle b = new Bundle();
                b.putInt("action", onlineOrder.getTradeTypeID());
                b.putBoolean("isFromOrderDetails", true);
                b.putParcelable("stockQuotation", stockQuotation);
                b.putParcelable("onlineOrder", onlineOrder);
                Intent i = new Intent(OrderDetailsActivity.this, TradesActivity.class);
                i.putExtras(b);
                OrderDetailsActivity.this.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {


            try {
                Bundle b = new Bundle();
                b.putInt("action", onlineOrder.getTradeTypeID());
                b.putBoolean("isFromOrderDetails", true);
                b.putParcelable("stockQuotation", stockQuotation);
                b.putParcelable("onlineOrder", onlineOrder);
                Intent i = new Intent(OrderDetailsActivity.this, TradesActivity.class);
                i.putExtras(b);
                OrderDetailsActivity.this.startActivity(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showActivateDialog(Activity context, OnlineOrder order) {

        new AlertDialog.Builder(context)
                .setTitle(context.getResources().getString(R.string.activate_order))
                .setMessage(context.getResources().getString(R.string.activate_order_text))
                .setPositiveButton(android.R.string.yes,
                        (dialog, which) -> new ActivateOrder(context, order).execute())
                .setNegativeButton(android.R.string.no,
                        (dialog, which) -> {
                            // do nothing
                        })
                .show();
    }

    private void setLimitChecked(Button btLimit, Button btMarketPrice, boolean limitChecked) {

        Log.wtf("limit checked", "is " + limitChecked);

        if (limitChecked) {

//            btLimit.setTextColor(ContextCompat.getColor(this, R.color.white));
            btLimit.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
            btLimit.setBackground(ContextCompat.getDrawable(this, R.drawable.border_limit_selected));

//            btMarketPrice.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            btMarketPrice.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));

            btMarketPrice.setBackground(ContextCompat.getDrawable(this, R.drawable.border_market_not_selected));
        } else {

//            btMarketPrice.setTextColor(ContextCompat.getColor(this, R.color.white));
            btMarketPrice.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
            btMarketPrice.setBackground(ContextCompat.getDrawable(this, R.drawable.border_market_selected));

//            btLimit.setTextColor(ContextCompat.getColor(this, R.color.colorValues));
            btLimit.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));
            btLimit.setBackground(ContextCompat.getDrawable(this, R.drawable.border_limit_not_selected));
        }
    }

    private void showLimitPrice(LinearLayout llPrice, boolean show) {

        /*etLimitPrice.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        btLimitMinus.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        btLimitPlus.setVisibility(show ? View.VISIBLE : View.INVISIBLE);*/
        if (show) {
//            etLimitPrice.setBackgroundColor(getResources().getColor(R.color.white));
            etLimitPrice.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
            etLimitPrice.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));

//            etLimitPrice.setTextColor(getResources().getColor(R.color.colorDark));
            etLimitPrice.setEnabled(true);
            btLimitMinus.setEnabled(true);
            btLimitPlus.setEnabled(true);
        } else {
            etLimitPrice.setBackgroundColor(getResources().getColor(R.color.lightgrey));
            etLimitPrice.setTextColor(getResources().getColor(R.color.darkgray));

            etLimitPrice.setEnabled(false);
            btLimitMinus.setEnabled(false);
            btLimitPlus.setEnabled(false);
        }
        Log.wtf("show", "sho : " + show);

        //   llPrice.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    private void showFastEditDialog(ArrayList<OrderDurationType> allOrderDurations) {

        //ContextThemeWrapper ctw = new ContextThemeWrapper( this, R.style.AlertDialogCustom);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        //String goodUntil = "";

        LinearLayout llPrice;
        EditText etQuantity;
        Button btLimit, btMarketPrice;
        Button btQuantityPlus, btQuantityMinus;
        Button Dialog_btnCancel, Dialog_btnSend;
        ImageView ivArrow;

        final View editDialog = inflater.inflate(R.layout.popup_edit_dialog, null);

        llPrice = editDialog.findViewById(R.id.llPrice);

        //     spDurationType = editDialog.findViewById(R.id.spDurationType);
        ivArrow = editDialog.findViewById(R.id.ivArrow);

        etDialogConfirm = editDialog.findViewById(R.id.etConfirm);
        etLimitPrice = editDialog.findViewById(R.id.etLimitPrice);
        etQuantity = editDialog.findViewById(R.id.etQuantity);
        etDurationType = editDialog.findViewById(R.id.etDurationType);
        etDurationType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDurationType();
            }
        });

        btLimit = editDialog.findViewById(R.id.btOrderLimit);
        btMarketPrice = editDialog.findViewById(R.id.btOrderMarketPrice);

        btLimitPlus = editDialog.findViewById(R.id.btLimitPlus);
        btLimitMinus = editDialog.findViewById(R.id.btLimitMinus);

        btQuantityPlus = editDialog.findViewById(R.id.btQuantityPlus);
        btQuantityMinus = editDialog.findViewById(R.id.btQuantityMinus);

        Dialog_btnCancel = editDialog.findViewById(R.id.Dialog_btnCancel);
        Dialog_btnSend = editDialog.findViewById(R.id.Dialog_btnSend);


        etDurationType.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
        etDialogConfirm.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
        etLimitPrice.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
        etQuantity.setTextColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));



        Log.wtf("onlineOrder.getGoodUntilDate()", "onlineOrder.getGoodUntilDate() = " + onlineOrder.getGoodUntilDate());

        selectedPos = Actions.returnDurationIndex(onlineOrder.getDurationID());
        setOrderDuration(Actions.returnDurationIndex(onlineOrder.getDurationID()));

        if (orderType == MyApplication.LIMIT) {

            btLimit.performClick();
            setLimitChecked(btLimit, btMarketPrice, true);
            showLimitPrice(llPrice, true);
        } else {

            btMarketPrice.performClick();
            setLimitChecked(btLimit, btMarketPrice, false);
            showLimitPrice(llPrice, false);
        }

        btLimit.setOnClickListener(v -> {
            Log.wtf("order", "limit");
            orderType = MyApplication.LIMIT;
            setLimitChecked(btLimit, btMarketPrice, true);
            showLimitPrice(llPrice, true);
        });

        btMarketPrice.setOnClickListener(v -> {
            Log.wtf("order", "market");
            orderType = MyApplication.MARKET_PRICE;
            setLimitChecked(btLimit, btMarketPrice, false);
            showLimitPrice(llPrice, false);
        });

        ivArrow.setOnClickListener(v -> showPopupDurationType());

        if (!Actions.isMarketOpen()) {
            btMarketPrice.setEnabled(false);
            btLimit.performClick();
        }

        orderQuantity = onlineOrder.getQuantity() - onlineOrder.getQuantityExecuted();
        etQuantity.setText(String.valueOf(orderQuantity));

        orderPrice = onlineOrder.getPrice();
        etLimitPrice.setText(String.valueOf(orderPrice));

//        price = orderPrice;

        btLimitPlus.setOnClickListener(v -> {
            orderPrice = Double.parseDouble(etLimitPrice.getText().toString());
            String quantityText = "";
            if (orderType == MyApplication.LIMIT) {
                Log.wtf("orderType == MyApplication.LIMIT", "" + orderType);


                if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {
                    Log.wtf("trade", "is Auction_Instrument_id");

                    if (Double.parseDouble(etLimitPrice.getText().toString()) < 0) {
                        etLimitPrice.setText(Actions.formatNumber(0, "##.##"));
                        orderPrice = 0.0;
                        Log.wtf("etLimitPrice", "< 0");
                    } else if (orderPrice < HiLimit) {

                        Log.wtf("orderPrice", "< HiLimit");
                        double pr = orderPrice + ticketPrice;
                        Log.wtf("pr", " orderPrice + ticketPrice = " + pr);
                        if (pr <= HiLimit) {
                            orderPrice = pr;
                            etLimitPrice.setText(Actions.formatNumber(pr, "##.##"));
                        }
                    } else {
                        Log.wtf("price", "< else");
                        etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
                    }
                } else {
                    Log.wtf("trade", "is Not Auction_Instrument_id");

                    if (Double.parseDouble(etLimitPrice.getText().toString()) < stockQuotation.getLowlimit()) {
                        etLimitPrice.setText(Actions.formatNumber(stockQuotation.getLowlimit(), "##.##"));
                        orderPrice = stockQuotation.getLowlimit();
                    } else if (orderPrice < stockQuotation.getHiLimit()) {
                        double pr = orderPrice + ticketPrice;
                        if (pr <= stockQuotation.getHiLimit()) {
                            orderPrice = pr;
                            etLimitPrice.setText(Actions.formatNumber(pr, "##.##"));
                        }
                    } else {
                        etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
                    }
                }
            }

//            quantityText = etQuantity.getText().toString();
//            if (quantityText.length() > 0) {
//                orderQuantity = Integer.parseInt(quantityText);
//            }
//            etQuantity.setText(String.valueOf(orderQuantity));

//            updateOverAllViews(price, quantity);
            setTick();
//            orderPrice += 1;
//            etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
        });

        btLimitMinus.setOnClickListener(v -> {

            orderPrice = Double.parseDouble(etLimitPrice.getText().toString());
            if (orderType == MyApplication.LIMIT) {

                if (trade.getStockQuotation().getInstrumentId().equals(MyApplication.Auction_Instrument_id)) {
                    if (orderPrice > 0) {
                        double pr = orderPrice - ticketPrice;
                        if (pr >= 0) {
                            orderPrice = pr;
                            etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
                        }
                    } else {

                        etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
                    }
                } else {
                    if (orderPrice > stockQuotation.getLowlimit()) {
                        double pr = orderPrice - ticketPrice;
                        if (pr >= stockQuotation.getLowlimit()) {
                            orderPrice = pr;
                            etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
                        }
                    } else {

                        etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
                    }
                }

            }
//            quantityText = etQuantity.getText().toString();
//            if (isFromOrderDetails && quantityText.length() > 0) {
//                quantity = Integer.parseInt(quantityText);
//            }
//            updateOverAllViews(price, quantity);
            setTick();

//            if (orderPrice > 0) {
//                orderPrice -= 1;
//            }
//            etLimitPrice.setText(Actions.formatNumber(orderPrice, "##.##"));
        });

        btQuantityPlus.setOnClickListener(v -> {

            try {

                if (etQuantity.getText().toString().length() > 0) {

                    orderQuantity = Integer.parseInt(etQuantity.getText().toString());
                } else {
                    orderQuantity = 0;
                }

                orderQuantity += 1;
                etQuantity.setText(String.valueOf(orderQuantity));

            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        btQuantityMinus.setOnClickListener(v -> {

            try {
                if (etQuantity.getText().toString().length() > 0 && Integer.parseInt(etQuantity.getText().toString()) > 1) {

                    orderQuantity = Integer.parseInt(etQuantity.getText().toString());
                    orderQuantity -= 1;
                } else {
                    orderQuantity = 1;
                }
                etQuantity.setText(String.valueOf(orderQuantity));

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        builder.setView(editDialog);
//                .setPositiveButton(getResources().getString(R.string.send_button), (dialog, which) -> updates(etDialogConfirm.getText().toString()))
//                .setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> dialog.dismiss());

        dialogEdit = builder.create();
        dialogEdit.setOnShowListener(arg0 -> {
            dialogEdit.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, MyApplication.mshared.getBoolean(OrderDetailsActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
            dialogEdit.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(OrderDetailsActivity.this, MyApplication.mshared.getBoolean(OrderDetailsActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
            dialogEdit.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTransformationMethod(null);
            dialogEdit.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
        });

        dialogEdit.show();

        Dialog_btnCancel.setOnClickListener(v -> {
            dialogEdit.cancel();
        });

        Dialog_btnSend.setOnClickListener(v -> {
            updates(etDialogConfirm.getText().toString());
        });

    }

    public void updates(String pinCode) {
        if (pinCode.length() == 0) {
            Animation shake = AnimationUtils.loadAnimation(OrderDetailsActivity.this, R.anim.shake);
            etDialogConfirm.startAnimation(shake);
        } else {
            dialogEdit.cancel();
            new UpdateOrder(onlineOrder, pinCode, orderQuantity, orderType, orderPrice, orderGoodUntilDate).execute();
        }
    }

    public void setOrderDuration(int position) {
        String txt = "";
        orderDurationType = MyApplication.allOrderDurationType.get(position);

        if (MyApplication.allOrderDurationType.get(position).getID() == 6) {
            txt = "" + onlineOrder.getGoodUntilDate();
            orderGoodUntilDate = txt;
        } else {
            orderGoodUntilDate = "";
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
        adapterDuration = new OrderDurationTypeAdapter(this, MyApplication.allOrderDurationType, this, selectedPos);
        rvDurationType.setAdapter(adapterDuration);

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
                selectedPos = 5;
                dialog.dismiss();
            }
        };

        dialog.show();
    }

    @Override
    public void onItemClicked(View v, int position) {
//        Toast.makeText(this, "Position = " + position + " , id = " +
//                MyApplication.allOrderDurationType.get(position).getID() + " , name : " +
//                MyApplication.allOrderDurationType.get(position).getDescriptionEn(), Toast.LENGTH_SHORT).show();


        if (MyApplication.allOrderDurationType.get(position).getID() != 6) {
            if (!Actions.isMarketOpen()) {
                if (MyApplication.allOrderDurationType.get(position).getID() == 1) {
                    orderDurationType = MyApplication.allOrderDurationType.get(position);
                    selectedPos = position;
                    setOrderDuration(position);
                    dialog.dismiss();
                }
            } else {
                orderDurationType = MyApplication.allOrderDurationType.get(position);
                selectedPos = position;
                setOrderDuration(position);
                dialog.dismiss();
            }
        } else {
            showDateDialog();
        }
    }

    private void showDateDialog() {

        Log.wtf("open", "date");
        myCalendar = Calendar.getInstance();
        myCalendar.roll(Calendar.DATE, 1);
        DatePickerDialog datePickerDialog = new DatePickerDialog(OrderDetailsActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));

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

    private void showCancelDialog() {

        new AlertDialog.Builder(OrderDetailsActivity.this)
                .setTitle(getResources().getString(R.string.cancel_order))
                .setMessage(getResources().getString(R.string.cancel_order_text))
                .setPositiveButton(android.R.string.yes,
                        (dialog, which) -> new CancelOrder().execute())
                .setNegativeButton(android.R.string.no,
                        (dialog, which) -> {
                            // do nothing
                        }).setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void cancelDialog(final Activity c, String message, final boolean finish, boolean cancel) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctw);
        builder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
                    dialog.cancel();
                    if (finish) {

                        finishAffinity();
                        Intent intent = new Intent(OrderDetailsActivity.this, OrdersActivity.class);
                        //TradeConfirmationActivity.this.finish();
                        startActivity(intent);
                    }
                });
        if (cancel)
            builder.setNegativeButton(c.getString(R.string.confirm), (dialog, id) -> dialog.cancel());
        android.app.AlertDialog alert = builder.create();
        alert.show();

    }

    private class ActivateOrder extends AsyncTask<Void, Void, String> {

        OnlineOrder order;
        Activity context;

        public ActivateOrder(Activity context, OnlineOrder order) {

            this.context = context;
            this.order = order;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(context);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.ActivateOrder.getValue(); //"/ActivateOrder";

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("ApplicationType").value("7")
                        .key("Reference").value(order.getReference())
                        .key("key").value(MyApplication.currentUser.getKey())
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.ActivateOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            result = ConnectionRequests.POSTWCF(url, stringer);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                finish();

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.ActivateOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            // refreshInterface.refreshData();
        }
    }

    private class CancelOrder extends AsyncTask<Void, Void, String> {

        String random = Actions.getRandom();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(OrderDetailsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.CancelOrder.getValue();

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("ApplicationType").value(0)
                        .key("Reference").value(onlineOrder.getReference())
                        .key("key").value(MyApplication.currentUser.getKey())
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.CancelOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            //String stringer = "{\"ApplicationType\":\"1\",\" Reference\":" + onlineOrder.getID() + ",\"key\":\"" + MyApplication.currentUser.getKey() + "\"}";

            result = ConnectionRequests.POSTWCF(url, stringer);
            Log.wtf("Result", "is " + result);
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

                    cancelDialog(OrderDetailsActivity.this, getResources().getString(R.string.cancelOrderSuccess), true, false);

                } else {

                    cancelDialog(OrderDetailsActivity.this, getResources().getString(R.string.cancelOrderError), false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.CancelOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    }

    private class GetOrderDurationTypes extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(OrderDetailsActivity.this);
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
                result = ConnectionRequests.GET(url, OrderDetailsActivity.this, parameters);

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

            showFastEditDialog(MyApplication.allOrderDurationType);
        }
    }

    private class UpdateOrder extends AsyncTask<Void, Void, String> {

        OnlineOrder onlineOrder;
        int quantity, orderType;
        double price;
        String pin;
        String random = "";
        String tradingPin = "";
        String encrypted = "";
        String goodUntilDate;

        private UpdateOrder(OnlineOrder onlineOrder, String pin, int quantity, int orderType, double price, String goodUntilDate) {
            this.onlineOrder = onlineOrder;
            this.pin = pin;
            this.quantity = quantity;
            this.orderType = orderType;
            this.price = price;
            this.goodUntilDate = goodUntilDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(OrderDetailsActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            random = Actions.getRandom();

            encrypted = Actions.MD5(pin);

            encrypted = encrypted + random;
            tradingPin = Actions.MD5(encrypted);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.UpdateOrder.getValue(); //"/UpdateOrder";

            Log.wtf("UpdateOrder", "GoodUntilDate : '" + goodUntilDate + "'");

            String date = "";
            if (orderDurationType.getID() != 6) {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
                date = sdf.format(new Date());
            } else {
                date = goodUntilDate;
            }

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("UserID").value(MyApplication.currentUser.getId())
                        .key("InvestorID").value(MyApplication.currentUser.getInvestorId())
                        .key("PortfolioID").value(MyApplication.currentUser.getPortfolioId())
                        .key("TradingPIN").value(tradingPin)
                        .key("Random").value(Integer.parseInt(random))
                        .key("ApplicationType").value(7)
                        .key("Reference").value(onlineOrder.getReference())
                        .key("BrokerID").value(Integer.parseInt(MyApplication.brokerID))
                        .key("DurationID").value(orderDurationType.getID())

                        .key("GoodUntilDate").value(date)

                        .key("Price").value((trade.getOrderType() == 1 || trade.getOrderType()==8) ? 0.0 : price)
                        .key("OrderTypeID").value(orderType)
                        .key("Quantity").value(quantity)
                        .key("StockID").value(onlineOrder.getStockID())
                        .key("TradeTypeID").value(onlineOrder.getTradeTypeID())

                        .key("StatusID").value(onlineOrder.getStatusID())
                        .key("OperationTypeID").value(onlineOrder.getOperationTypeID())

                        .key("BrokerEmployeeID").value(0)
                        .key("ForwardContractID").value(0)
                        .key("key").value(MyApplication.currentUser.getKey())
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.UpdateOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            Log.wtf("OrderDetailsActivity : url ='" + url + "' ", " JSONStringer = '" + stringer + "'");
            result = ConnectionRequests.POSTWCF(url, stringer);
            Log.wtf("update result", "is " + result);
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

                    finishAffinity();
                    Intent intent = new Intent(OrderDetailsActivity.this, OrdersActivity.class);
                    //TradeConfirmationActivity.this.finish();
                    startActivity(intent);

                } else {

                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? success : object.getString("MessageAr");
                    Actions.CreateDialog(OrderDetailsActivity.this, error, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.UpdateOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
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
            parameters.put("userId", MyApplication.currentUser.getId() + "");
            parameters.put("portfolioId", MyApplication.currentUser.getPortfolioId() + "");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));
            parameters.put("stockId", stockQuotation.getStockID() + "");
            parameters.put("MarketId", MyApplication.marketID);

            for (Map.Entry<String, String> map : parameters.entrySet()) {
                Log.wtf("TradesActivity GetTradeInfo", "parameters : " + map.getKey() + "= " + map.getValue());
            }

            try {
                result = ConnectionRequests.GET(url, OrderDetailsActivity.this, parameters);

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
//                trade.getStockQuotation().setInstrumentId("1");

                try {
                    trade.setAvailableShareCount(trade.getAvailableShareCount() + (onlineOrder.getQuantity() - onlineOrder.getQuantityExecuted()));
                } catch (Exception e) {
                    try {
                        Toast.makeText(OrderDetailsActivity.this, "error in setAvailableShareCount", Toast.LENGTH_SHORT).show();
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
//                tickDirection = stockQuotation.getTickDirection();
                trade.setStockQuotation(stockQuotation);
                trade.setOrderType(orderType);
                //</editor-fold>
//                setData(trade);

            } catch (Exception e) {
                e.printStackTrace();
            }
//            swipeContainer.setRefreshing(false);
        }
    }


}
