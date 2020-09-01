package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.ids.fixot.model.WebItem;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by user on 10/2/2017.
 */

public class WebPageActivity extends AppCompatActivity implements MarketStatusListener {

    RelativeLayout rootLayout;
    WebView wvDetails;
    int websiteContentId = -1;
    private BroadcastReceiver receiver;
    private WebItem webItem;
    private boolean started = false;
    private ImageView share;

    public WebPageActivity() {
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
        setContentView(R.layout.activity_webpage);
        Actions.initializeBugsTracking(this);

        Actions.initializeToolBar(getString(R.string.links), WebPageActivity.this);

        findViews();

        started = true;


        if (getIntent().hasExtra("webItem")) {

            webItem = getIntent().getExtras().getParcelable("webItem");
            LinearLayout footer = (LinearLayout) findViewById(R.id.footer);
            footer.setVisibility(View.GONE);

            try {
                if (MyApplication.lang == MyApplication.ARABIC) {

                    Actions.initializeToolBar(webItem.getTitleAr(), WebPageActivity.this);

                    wvDetails.loadDataWithBaseURL("file:///android_asset/", "<html><head>\n" +
                            "<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;\"/>" +
                            "<style type=\"text/css\">\n" +
                            "@font-face {\n" +
                            "    font-family: MyFont;\n" +
                            "    src: url(\"file:///android_asset/DroidKufiRegular.ttf\")\n" +
                            "}\n" +
                            "body {\n" +
                            "    font-family: MyFont;\n" +
                            "    text-align: justify;\n" +
                            "}\n" +
                            "</style><body dir=\"rtl\" >" + webItem.getContentAr() + "</body></html>", "text/html", "UTF-8", "");
                } else {

                    Actions.initializeToolBar(webItem.getTitleEn(), WebPageActivity.this);

                    wvDetails.loadDataWithBaseURL("file:///android_asset/", "<html><head>\n" +
                            "<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;\"/>" +
                            "<style type=\"text/css\">\n" +
                            "@font-face {\n" +
                            "    font-family: MyFont;\n" +
                            "    src: url(\"file:///android_asset/GilroyLight.ttf\")\n" +
                            "}\n" +
                            "body {\n" +
                            "    font-family: MyFont;\n" +
                            "    text-align: justify;\n" +
                            "}\n" +
                            "</style><body>" + webItem.getContentEn() + "</body></html>", "text/html", "UTF-8", "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {


            Actions.showHideFooter(this);
            websiteContentId = getIntent().getExtras().getInt("websiteContentId");
            new GetWebItem().execute();
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


    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);
        Actions.checkLanguage(this, started);

        //Actions.InitializeSessionService(this);
        Actions.InitializeSessionServiceV2(this);
        //Actions.InitializeMarketService(this);
        //   Actions.InitializeMarketServiceV2(this);
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
        share=findViewById(R.id.share);
        share.setVisibility(View.GONE);
        rootLayout = findViewById(R.id.rootLayout);
        wvDetails = findViewById(R.id.wvDetails);
    }

    public void back(View v) {
        finish();
    }

    //<editor-fold desc="Get Web Item">
    private class GetWebItem extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.showDialog(WebPageActivity.this);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetSiteMapData.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("key", getResources().getString(R.string.beforekey));
            parameters.put("Language", MyApplication.lang + "");
            parameters.put("websiteContentId", "" + websiteContentId);
            parameters.put("MarketId", MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, WebPageActivity.this, parameters);
                Log.wtf("result", result);
                webItem = GlobalFunctions.GetWebItems(result).get(0);

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetSiteMapData.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MyApplication.dismiss();

            try {
                if (MyApplication.lang == MyApplication.ARABIC) {

                    wvDetails.loadDataWithBaseURL("file:///android_asset/", "<html><head>\n" +
                            "<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;\"/>" +
                            "<style type=\"text/css\">\n" +
                            "@font-face {\n" +
                            "    font-family: MyFont;\n" +
                            "    src: url(\"file:///android_asset/DroidKufiRegular.ttf\")\n" +
                            "}\n" +
                            "body {\n" +
                            "    font-family: MyFont;\n" +
                            "    text-align: justify;\n" +
                            "}\n" +
                            "</style><body  dir=\"rtl\" >" + webItem.getContentAr() + "</body></html>", "text/html", "UTF-8", "");
                } else {
                    wvDetails.loadDataWithBaseURL("file:///android_asset/", "<html><head>\n" +
                            "<meta name=\"viewport\" content=\"width=device-width; initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=0;\"/>" +
                            "<style type=\"text/css\">\n" +
                            "@font-face {\n" +
                            "    font-family: MyFont;\n" +
                            "    src: url(\"file:///android_asset/GilroyLight.ttf\")\n" +
                            "}\n" +
                            "body {\n" +
                            "    font-family: MyFont;\n" +
                            "    text-align: justify;\n" +
                            "}\n" +
                            "</style><body>" + webItem.getContentEn() + "</body></html>", "text/html", "UTF-8", "");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    //</editor-fold>

}
