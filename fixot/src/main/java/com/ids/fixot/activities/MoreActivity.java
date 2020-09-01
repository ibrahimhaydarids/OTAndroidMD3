package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.Calendar;

import static com.ids.fixot.MyApplication.lang;

public class MoreActivity extends AppCompatActivity implements MarketStatusListener {

    ImageView ivBack;
    Toolbar myToolbar;
    TextView tvLogout;
    RelativeLayout rootLayout, rlMowazi, rlSectors, rlTops, rlNews, rlLinks, rlChangePassword, rlSettings;
    private BroadcastReceiver receiver;
    private boolean started = false;

    public MoreActivity() {
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
        setContentView(R.layout.activity_more);
        Actions.initializeBugsTracking(this);

        Actions.showHideFooter(this);
        findViews();

        started = true;

        Actions.initializeToolBar(getString(R.string.menu_more), MoreActivity.this);

        Actions.overrideFonts(this, rootLayout, false);

        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);
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

    public void back(View v) {
        Actions.forceExitApp(this);
       // finish();
    }

    public void goTo(View v) {
        Actions.goTo(this, v);
    }


    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);
        myToolbar = findViewById(R.id.my_toolbar);
        ivBack = myToolbar.findViewById(R.id.ivBack);
        ivBack.setVisibility(View.GONE);

        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(MoreActivity.this));

        rlMowazi = findViewById(R.id.rlMowazi);
        rlMowazi.setVisibility(MyApplication.showMowazi ? View.VISIBLE : View.GONE);

        rlSectors = findViewById(R.id.rlSectors);
        rlTops = findViewById(R.id.rlTops);
        rlNews = findViewById(R.id.rlNews);
        rlLinks = findViewById(R.id.rlLinks);
        rlChangePassword = findViewById(R.id.rlChangePassword);
        rlSettings = findViewById(R.id.rlSettings);

        if (MyApplication.isOTC) {
            rlSectors.setVisibility(View.GONE);

            rlTops.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            rlNews.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightTheme));
            rlLinks.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            rlChangePassword.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightTheme));
            rlSettings.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
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

//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//    }

    @Override
    public void onBackPressed() {
        Actions.forceExitApp(this);
    }
}
