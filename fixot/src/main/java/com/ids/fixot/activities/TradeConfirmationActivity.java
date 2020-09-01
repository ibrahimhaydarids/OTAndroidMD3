package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.Trade;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DEV on 2/16/2018.
 */

public class TradeConfirmationActivity extends AppCompatActivity implements MarketStatusListener {

    RelativeLayout rootLayout;
    LinearLayout llConfirm;
    Button btConfirm;
    EditText etConfirm;
    TextView tvStockValue, tvQuantityValue, tvPriceValue, tvTradeTypeValue, tvDateValue, tvDurationTypeValue;
    TextView tvCostValue, tvCommissionValue, tvOverallValue;
    int quantity = 0;
    Trade trade;
    StockQuotation stockQuotation;
    String dateFormatter = "dd/MM/yyyy 00:00:00";
    private BroadcastReceiver receiver;
    private boolean started = false;

    public TradeConfirmationActivity() {
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
        setContentView(R.layout.activity_confirm_trade);
        Actions.initializeBugsTracking(this);

        started = true;

        trade = getIntent().getExtras().getParcelable("trade");

        stockQuotation = trade.getStockQuotation();

        findViews();

        setListeners();

        Actions.initializeToolBar(getString(R.string.trades_confirmation_page_title), TradeConfirmationActivity.this);

        Actions.overrideFonts(this, rootLayout, false);
        Actions.showHideFooter(this);

        setTypefaces();

        Log.wtf("AddNewOrder", "GoodUntilDate : '" + trade.getGoodUntilDate() + "'");
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    private void setTypefaces() {

        Actions.setTypeface(new TextView[]{tvStockValue, tvTradeTypeValue, tvDateValue, tvDurationTypeValue},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);


        Actions.setTypeface(new TextView[]{tvQuantityValue, tvPriceValue, tvCostValue, tvCommissionValue, tvOverallValue}, MyApplication.giloryBold);
    }

    public void back(View v) {
        TradeConfirmationActivity.this.finish();
    }

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        llConfirm = findViewById(R.id.llConfirm);
        btConfirm = findViewById(R.id.btConfirm);
        etConfirm = findViewById(R.id.etConfirm);


        etConfirm.setVisibility(MyApplication.currentUser.isTradingPasswordMandatory() ? View.VISIBLE : View.GONE);

        tvStockValue = findViewById(R.id.tvStockValue);
        tvQuantityValue = findViewById(R.id.tvQuantityValue);
        tvPriceValue = findViewById(R.id.tvPriceValue);
        tvTradeTypeValue = findViewById(R.id.tvTradeTypeValue);
        tvDateValue = findViewById(R.id.tvDateValue);
        tvDurationTypeValue = findViewById(R.id.tvDurationTypeValue);
        tvCostValue = findViewById(R.id.tvCostValue);
        tvCommissionValue = findViewById(R.id.tvCommissionValue);
        tvOverallValue = findViewById(R.id.tvOverallValue);

        String stockValue = MyApplication.lang == MyApplication.ARABIC ?
                (stockQuotation.getSecurityId() + "-" + stockQuotation.getSymbolAr()) : (stockQuotation.getSecurityId() + "-" + stockQuotation.getSymbolEn());
        tvStockValue.setText(stockValue);
//getStockID()
        tvPriceValue.setText(Actions.formatNumber(trade.getPrice(), Actions.TwoDecimal));
        tvTradeTypeValue.setText(trade.getTradeTypeID() == MyApplication.ORDER_BUY ? getResources().getString(R.string.buy) : getResources().getString(R.string.sell));
        tvDateValue.setText(trade.getDate());
        tvDurationTypeValue.setText(trade.getDurationType());
        tvCostValue.setText(Actions.formatNumber(trade.getOverallTotal(), Actions.ThreeDecimalThousandsSeparator));
        tvCommissionValue.setText(Actions.formatNumber(trade.getCommission(), Actions.ThreeDecimalThousandsSeparator));
        tvOverallValue.setText(Actions.formatNumber(trade.getCost(), Actions.ThreeDecimalThousandsSeparator));


        if (getIntent().getExtras().getBoolean("isUpdate")) {

            quantity = trade.getQuantity() + trade.getExecutedQuantity();
        } else {

            quantity = trade.getQuantity();
        }

        tvQuantityValue.setText(String.valueOf(quantity));
    }

    private void setListeners() {

        btConfirm.setOnClickListener(v -> {

            try {
                Actions.closeKeyboard(TradeConfirmationActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace();
            }

            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                if (etConfirm.getText().toString().length() > 0) {
                    btConfirm.setClickable(false);

                    if (getIntent().getExtras().getBoolean("isUpdate")) {

                        new UpdateOrder().execute();
                    } else {

                        new AddOrder().execute();
                    }
                } else {

                    Animation shake = AnimationUtils.loadAnimation(TradeConfirmationActivity.this, R.anim.shake);
                    etConfirm.startAnimation(shake);
                }

            } else {
                btConfirm.setClickable(false);

                if (getIntent().getExtras().getBoolean("isUpdate")) {

                    new UpdateOrder().execute();
                } else {

                    new AddOrder().execute();
                }
            }
        });


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
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
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

    private class AddOrder extends AsyncTask<Void, Void, String> {

        String random = "";
        String tradingPin = "";
        String encrypted = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradeConfirmationActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                random = Actions.getRandom();

                String cofirmPin = etConfirm.getText().toString();
                encrypted = Actions.MD5(cofirmPin);

                encrypted = encrypted + random;

                tradingPin = Actions.MD5(encrypted);
            } else {

                random = "";

                encrypted = "";

                tradingPin = "";
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.AddNewOrder.getValue();

            Log.wtf("AddNewOrder", "GoodUntilDate : '" + trade.getGoodUntilDate() + "'");

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("PlacementUserID").value(MyApplication.currentUser.getId())
                        .key("UserID").value(MyApplication.selectedSubAccount.getUserId())
                        .key("InvestorID").value(MyApplication.selectedSubAccount.getInvestorId())
                        .key("PortfolioID").value(MyApplication.selectedSubAccount.getPortfolioId())
                        .key("TradingPIN").value(tradingPin)
                        .key("Random").value(random)
                        .key("ApplicationType").value(7)
                        .key("Reference").value(0)
                        .key("BrokerID").value(Integer.parseInt(MyApplication.brokerID))
                        .key("DurationID").value(trade.getDurationTypeId())
                        .key("GoodUntilDate").value(trade.getGoodUntilDate()) //iza na2a date be3abe, iza ma na2a mnb3ato fade
                        .key("Price").value(trade.getOrderType() == 1 ? 0.0 : trade.getPrice())
                        .key("OrderTypeID").value(trade.getOrderType())
                        .key("Quantity").value(trade.getQuantity())
                        .key("StockID").value(String.valueOf(trade.getStockQuotation().getStockID()))
                        .key("TradeTypeID").value(trade.getTradeTypeID())

                        .key("StatusID").value(trade.getStatusTypeId()) //16 iza private checkbox checked, else 1
                        .key("OperationTypeID").value(trade.getOperationTypeID())//4 harcoded ademe ios if privateCb is checked, else 1

                        .key("BrokerEmployeeID").value(0)
                        .key("ForwardContractID").value(0)
                        .key("key").value(MyApplication.currentUser.getKey())
                        .endObject();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.AddNewOrder.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            Log.wtf("TradeConfiramtionActivity - AddNewOrder ", " DurationID : " + trade.getDurationTypeId());
            Log.wtf("TradeConfiramtionActivity", "AddNewOrder");
            Log.wtf("TradeConfiramtionActivity : url ='" + url + "'", " / JSONStringer = '" + stringer.toString() + "'");
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

                    finishAffinity();
                    Intent intent = new Intent(TradeConfirmationActivity.this, OrdersActivity.class);
                    //TradeConfirmationActivity.this.finish();
                    startActivity(intent);

                } else {

                    btConfirm.setClickable(true);
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? success : object.getString("MessageAr");
                    Actions.CreateDialog(TradeConfirmationActivity.this, error, false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Actions.CreateDialog(TradeConfirmationActivity.this, getResources().getString(R.string.error), false, false);
            }
        }
    }

    private class UpdateOrder extends AsyncTask<Void, Void, String> {

        String random = "";
        String tradingPin = "";
        String encrypted = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(TradeConfirmationActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            quantity = trade.getQuantity() + trade.getExecutedQuantity();


            if (MyApplication.currentUser.isTradingPasswordMandatory()) {

                random = Actions.getRandom();

                encrypted = Actions.MD5(etConfirm.getText().toString());

                encrypted = encrypted + random;

                tradingPin = Actions.MD5(encrypted);
            } else {

                random = "";

                encrypted = "";

                tradingPin = "";
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.UpdateOrder.getValue();


            String date = "";
            if (trade.getDurationTypeId() != 6) {
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormatter, Locale.ENGLISH);
                date = sdf.format(new Date());
            } else {
                date = trade.getGoodUntilDate();
            }

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("PlacementUserID").value(MyApplication.currentUser.getId())
                        .key("UserID").value(MyApplication.currentUser.getId())
                        .key("InvestorID").value(MyApplication.currentUser.getInvestorId())
                        .key("PortfolioID").value(MyApplication.currentUser.getPortfolioId())
                        .key("TradingPIN").value(tradingPin)
                        .key("Random").value(random)
                        .key("ApplicationType").value(7)
                        .key("Reference").value(trade.getReference())
                        .key("BrokerID").value(Integer.parseInt(MyApplication.brokerID))
                        .key("DurationID").value(trade.getDurationTypeId())

                        .key("GoodUntilDate").value(date) //iza na2a date be3abe, iza ma na2a mnb3ato fade

                        .key("Price").value((trade.getOrderType() == 1 || trade.getOrderType()==8) ? 0.0 : trade.getPrice())
                        .key("OrderTypeID").value(trade.getOrderType())
                        .key("Quantity").value(quantity)
                        .key("StockID").value(String.valueOf(trade.getStockQuotation().getStockID()))
                        .key("TradeTypeID").value(trade.getTradeTypeID())

                        .key("StatusID").value(trade.getStatusTypeId()) //1 iza private checkbox checked, else 16
                        .key("OperationTypeID").value(trade.getOperationTypeID())//0 harcoded ademe ios if privateCb is checked, else 4

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
            Log.wtf("TradeConfiramtionActivity - UpdateOrder ", " DurationID : " + trade.getDurationTypeId());
            Log.wtf("TradeConfiramtionActivity", "UpdateOrder");
            Log.wtf("TradeConfiramtionActivity : url ='" + url + "' ", " / JSONStringer = '" + stringer + "'");
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
                    Intent intent = new Intent(TradeConfirmationActivity.this, OrdersActivity.class);
                    //TradeConfirmationActivity.this.finish();
                    startActivity(intent);

                } else {

                    btConfirm.setClickable(true);
                    String error;
                    error = MyApplication.lang == MyApplication.ENGLISH ? success : object.getString("MessageAr");
                    Actions.CreateDialog(TradeConfirmationActivity.this, error, false, false);
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

}
