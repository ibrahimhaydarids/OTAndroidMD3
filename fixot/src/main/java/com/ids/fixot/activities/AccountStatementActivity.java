package com.ids.fixot.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by user on 10/5/2017.
 */

public class AccountStatementActivity extends AppCompatActivity implements MarketStatusListener {


    LinearLayout llDate;
    RelativeLayout rootLayout;
    TextView tvToDate;
    Button btAccountStatement;
    WebView wvData;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    Spinner spSubAccounts;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    int reportType = 1;
    RadioGroup rgAccounts;
    RadioButton rbReport, rbGroupedReport;
    private BroadcastReceiver receiver;
    private boolean started = false;

    public AccountStatementActivity() {
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
        setContentView(R.layout.activity_account_statement);
        Actions.initializeBugsTracking(this);

        findViews();

        started = true;

        Actions.showHideFooter(this);
        Actions.initializeToolBar(getResources().getString(R.string.account_statement_title), AccountStatementActivity.this);
        Actions.overrideFonts(this, rootLayout, false);
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

    private void findViews() {

        spSubAccounts = findViewById(R.id.spSubAccounts);
        rgAccounts = findViewById(R.id.rgAccounts);
        rbReport = findViewById(R.id.rbReport);
        rbGroupedReport = findViewById(R.id.rbGroupedReport);

        llDate = findViewById(R.id.llDate);
        rootLayout = findViewById(R.id.rootLayout);
        wvData = findViewById(R.id.wvData);
        tvToDate = findViewById(R.id.tvToDate);
        btAccountStatement = findViewById(R.id.btAccountStatement);

        //<editor-fold desc="Webview Settings">
        wvData.getSettings().setDomStorageEnabled(true);
        wvData.clearCache(true);
        wvData.getSettings().setBuiltInZoomControls(true);
        wvData.getSettings().setSupportZoom(true);
        wvData.getSettings().setUseWideViewPort(true);
        wvData.getSettings().setJavaScriptEnabled(true);
        wvData.getSettings().setLoadWithOverviewMode(true);
        wvData.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvData.getSettings().setDomStorageEnabled(true);

        wvData.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                //handler.proceed(); // Ignore SSL certificate errors
                Log.wtf("onReceivedSslError", "onReceivedSslError");


                final AlertDialog.Builder builder = new AlertDialog.Builder(AccountStatementActivity.this, R.style.AlertDialogCustom);
                builder.setMessage("SSL Approval");
                builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                final AlertDialog dialog = builder.create();
                dialog.show();

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                MyApplication.showDialog(AccountStatementActivity.this);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.wtf("onPageFinished", "onPageFinished");
                Log.wtf("PAGE URL", url);
                MyApplication.dismiss();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.wtf("onReceivedError", "onReceivedError");
                MyApplication.dismiss();
            }
        });
        //</editor-fold>

        if (getIntent().getExtras().getBoolean("isAccountStatement")) {

            llDate.setVisibility(View.VISIBLE);
            btAccountStatement.setVisibility(View.VISIBLE);

            //<editor-fold desc="Load Account Statement Page">
            tvToDate.setOnClickListener(v -> new DatePickerDialog(AccountStatementActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show());

            date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(tvToDate);
            };
            myCalendar = Calendar.getInstance();

            //this.updateLabel(tvToDate);

            btAccountStatement.setOnClickListener(v -> {

                HashMap<String, String> hash = new HashMap<>();
                hash.put("userid", MyApplication.selectedSubAccount.getUserId() + "");
                hash.put("PortfolioID", MyApplication.selectedSubAccount.getPortfolioId() + "");
                hash.put("lang", MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
                hash.put("todate", tvToDate.getText().toString());
                hash.put("key", MyApplication.currentUser.getKey());

                //if (MyApplication.isMultiAccountStatements)
                hash.put("reporttype", reportType + "");

                try {
                    wvData.loadUrl(MyApplication.baseLink + "AccountStatementReport.aspx", hash);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.wtf("exception", e.getMessage());
                }
            });
            //</editor-fold>
        }


        rgAccounts.setVisibility(MyApplication.isMultiAccountStatements ? View.VISIBLE : View.GONE);
        rbReport.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                reportType = 1;
            }
        });
        rbGroupedReport.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                reportType = 2;
            }
        });


        subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, MyApplication.currentUser.getSubAccounts());
        spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
        spSubAccounts.setSelection(returnAccountIndex());
        spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private void updateLabel(TextView editText) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        editText.setText(sdf.format(myCalendar.getTime()));
    }

    public void back(View v) {

        finish();
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onResume() {
        super.onResume();

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //   Actions.InitializeMarketServiceV2(this);
        Actions.checkLanguage(this, started);
        Actions.checkSession(this);
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


}
