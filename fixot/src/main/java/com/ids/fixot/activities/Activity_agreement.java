package com.ids.fixot.activities;



import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

/**
 * Created by DEV on 3/20/2018.
 */

public class Activity_agreement extends AppCompatActivity implements MarketStatusListener {

    RelativeLayout rootLayout;
    WebView wvDetails;
    String url;
    private BroadcastReceiver receiver;
    private boolean started = false;
    CheckBox cbTerms;
    Button btSubmit;

    public Activity_agreement() {
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
        setContentView(R.layout.activity_agreement);
        Actions.initializeBugsTracking(this);

        started = true;

        findViews();

        url = getIntent().getExtras().getString("url");

        loadWebView();
        wvDetails.loadUrl(url);


        Actions.initializeToolBar(getString(R.string.news), this);
        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);




    }


    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
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
                /*if (url.contains(".pdf")) {

                    try {
                        Log.wtf("contains pdf", "yes");

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url), "application/pdf");
                        view.getContext().startActivity(intent);

                        //startActivity(new Intent(AkIndexActivity.this, ReportsViewActivity.class).putExtra("url", url));

                    } catch (ActivityNotFoundException e) {
                        //user does not have a pdf viewer installed
                        e.printStackTrace();

                        //startActivity(new Intent(AkIndexActivity.this, ReportsViewActivity.class).putExtra("url", url));

                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setDataAndType(Uri.parse("https://docs.google.com/viewer?url=" + url), "text/html");
                        startActivity(intent2);
                    }
                } else {
                    Log.wtf("contains pdf", "no");
                    //view.loadUrl(url);

                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }*/
                return false;
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

    public void back(View v) {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        wvDetails = findViewById(R.id.wvDetails);
        cbTerms = findViewById(R.id.cbTerms);
        btSubmit =findViewById(R.id.btSubmit);


        btSubmit.setOnClickListener(v -> {
            if (!cbTerms.isChecked())
                Toast.makeText(Activity_agreement.this, getString(R.string.plztermAndcondition), Toast.LENGTH_SHORT).show();
            else {
                Intent returnIntent = new Intent();
               // returnIntent.putExtra("result","1");
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }

        });
    }


    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
        super.onBackPressed();
    }

    public void share(View v) {
        String shareBody = url;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
//        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share"));
    }
}
