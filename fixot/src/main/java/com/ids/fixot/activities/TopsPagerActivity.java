package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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
import com.ids.fixot.fragments.TopGainersFragment;
import com.ids.fixot.fragments.TopLosersFragment;
import com.ids.fixot.fragments.TopTradedFragment;
import com.ids.fixot.fragments.TopTradesFragment;
import com.ids.fixot.fragments.TopValuesFragment;

import java.util.Calendar;


public class TopsPagerActivity extends AppCompatActivity implements MarketStatusListener {

    FragmentManager fragmentManager;
    RelativeLayout rootLayout;
    String tag_top_gainers = "top_gainers";
    String tag_top_losers = "top_losers";
    String tag_top_trades = "top_trades";
    String tag_top_traded = "top_traded";
    String tag_top_values = "top_values";
    private BroadcastReceiver receiver;
    private TabLayout tlTabs;
    private boolean started = false;

    public TopsPagerActivity() {
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
        setContentView(R.layout.activity_tops_pager);
        Actions.showHideFooter(this);
        Actions.initializeBugsTracking(this);

        Actions.initializeToolBar(getResources().getString(R.string.tops), this);

        started = true;
        findViews();

        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.container, new TopGainersFragment(), tag_top_gainers)
                .commit();

        Actions.overrideFonts(this, rootLayout, false);

        changeTabsFont(tlTabs, MyApplication.lang == MyApplication.ENGLISH ? MyApplication.giloryBold : MyApplication.droidbold);
    }

    private void findViews() {

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        tlTabs = (TabLayout) findViewById(R.id.tlTabs);
        fragmentManager = getSupportFragmentManager();

        initializeTabHost();

    }

    private void initializeTabHost() {

        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_gainer)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_looser)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_traded)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_trades)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.top_value)));

        tlTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                Log.wtf("pos", "is " + tab.getPosition());
                int position = tab.getPosition();

                switch (position) {

                    case 0:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopGainersFragment(), tag_top_gainers)
                                .commit();
                        break;

                    case 1:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopLosersFragment(), tag_top_losers)
                                .commit();
                        break;

                    case 2:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopTradedFragment(), tag_top_traded)
                                .commit();
                        break;

                    case 3:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopTradesFragment(), tag_top_trades)
                                .commit();
                        break;

                    case 4:
                        fragmentManager.beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                .replace(R.id.container, new TopValuesFragment(), tag_top_values)
                                .commit();
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        tlTabs.getTabAt(0).select();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.InitializeSessionService(this);
        //    Actions.InitializeMarketService(this);
        Actions.checkLanguage(this, started);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void back(View v) {

        finish();
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
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
}
