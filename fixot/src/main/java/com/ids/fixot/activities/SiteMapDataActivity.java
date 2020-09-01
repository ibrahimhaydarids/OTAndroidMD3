package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.WebItem;

import java.util.Calendar;


/**
 * Created by user on 4/3/2017.
 */

public class SiteMapDataActivity extends AppCompatActivity implements MarketStatusListener {

    RelativeLayout rootLayout;
    ProgressBar progressBar;
    WebView wvDetails;
    String webIUrl = "";
    WebItem webItem;
    private BroadcastReceiver receiver;
    private boolean started = false;

    public SiteMapDataActivity() {
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
        setContentView(R.layout.activity_site_map_data);
        Actions.initializeBugsTracking(this);

        started = true;
        findViews();

        loadWebView();

        if (getIntent().hasExtra("fromLogin")) {

            Log.wtf("from", "login");

            Actions.initializeToolBar(getString(R.string.links), SiteMapDataActivity.this);

            webIUrl = getIntent().getExtras().getString("url");
            wvDetails.loadUrl(webIUrl);

            LinearLayout footer = findViewById(R.id.footer);
            footer.setVisibility(View.GONE);

        } else if (getIntent().hasExtra("calcualtor")) {
            wvDetails.loadUrl("file:///android_asset/files/index.html");
            wvDetails.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightTheme));
        } else {//Quick Links page

            webItem = getIntent().getExtras().getParcelable("linkObject");
            Actions.initializeToolBar(MyApplication.lang == MyApplication.ARABIC ? webItem.getTitleAr() : webItem.getTitleEn(), SiteMapDataActivity.this);
            webIUrl = webItem.getUrl();
            wvDetails.loadUrl(webItem.getUrl());
            Actions.showHideFooter(this);
        }


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

        rootLayout = findViewById(R.id.rootLayout);
        wvDetails = findViewById(R.id.wvDetails);
        progressBar = findViewById(R.id.progressBar);
    }


    public void share(View v) {
        String shareBody = webIUrl;
        Intent sharingIntent = new Intent(Intent.ACTION_VIEW);
        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }


    private void loadWebView() {

        wvDetails.getSettings().setJavaScriptEnabled(true);
        wvDetails.getSettings().setUseWideViewPort(true);
        wvDetails.getSettings().setLoadWithOverviewMode(true);
        wvDetails.getSettings().setAllowFileAccess(true);
        wvDetails.getSettings().setAllowContentAccess(true);
        wvDetails.setLayerType(WebView.LAYER_TYPE_NONE, null);

        wvDetails.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.wtf("url", url);

                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        wvDetails.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {

            }

            @Override
            public void onReceivedTitle(WebView view, String title) {

            }
        });
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onResume() {
        super.onResume();

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


        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void back(View v) {
        finish();
    }
}
