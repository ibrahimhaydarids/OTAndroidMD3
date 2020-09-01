package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.ids.fixot.activities.mowazi.MowaziHomeActivity;
import com.ids.fixot.adapters.MenuRecyclerAdapter;
import com.ids.fixot.classes.MenuItem;

import java.util.ArrayList;
import java.util.Calendar;

import static com.ids.fixot.MyApplication.lang;

/**
 * Created by user on 9/26/2017.
 */

public class MenuActivity extends AppCompatActivity implements MenuRecyclerAdapter.RecyclerViewOnItemClickListener
        , MarketStatusListener {

    final int MARKET_INDEX = 1, SECTORS = 2, STOCKS = 3, TIME_SALES = 4, TOPS = 5, NEWS = 6, FAVORITES = 7, LINKS = 8, PORTFOLIO = 9, ORDERS = 10, MOWAZI = 11, SETTINGS = 12;
    TextView tvMarketStatus, tvLogout;
    RelativeLayout mainBar, rootLayout;
    LinearLayout llMarketStatus;

    RecyclerView rvMenu;
    GridLayoutManager glm;
    int spanCount = 4;
    ArrayList<MenuItem> mainMenuItems = new ArrayList<>();
    MenuRecyclerAdapter adapter;
    private BroadcastReceiver receiver;
    private boolean started = false;

    public MenuActivity() {
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
        setContentView(R.layout.menu_page);
        Actions.initializeBugsTracking(this);

        Actions.showHideFooter(this);

        started = true;
        findViews();

        Actions.overrideFonts(this, rootLayout, false);
        //Actions.InitializeMarketService(this);

        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);

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

    private void findViews() {

        mainBar = findViewById(R.id.my_toolbar);
        rootLayout = findViewById(R.id.llLayout);
        rvMenu = findViewById(R.id.rvMenu);
        glm = new GridLayoutManager(this, spanCount);

        tvLogout = mainBar.findViewById(R.id.tvLogout);
        try {
            tvMarketStatus = mainBar.findViewById(R.id.market_state_value_textview);
            llMarketStatus = mainBar.findViewById(R.id.ll_market_state);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tvLogout.setOnClickListener(v -> Actions.logout(MenuActivity.this));

        if (!MyApplication.isOTC) {

            mainMenuItems.add(new MenuItem(MARKET_INDEX, R.drawable.market_index, getResources().getString(R.string.market_index)));
            mainMenuItems.add(new MenuItem(SECTORS, R.drawable.sectors, getResources().getString(R.string.sectors)));
        }
        mainMenuItems.add(new MenuItem(STOCKS, R.drawable.stock, getResources().getString(R.string.stock)));
        mainMenuItems.add(new MenuItem(TIME_SALES, R.drawable.time_sales_button, getResources().getString(R.string.time_sales)));

        mainMenuItems.add(new MenuItem(TOPS, R.drawable.top_value, getResources().getString(R.string.tops)));

        mainMenuItems.add(new MenuItem(NEWS, R.drawable.news, getResources().getString(R.string.news)));
        mainMenuItems.add(new MenuItem(FAVORITES, R.drawable.favorite, getResources().getString(R.string.favorite)));
        mainMenuItems.add(new MenuItem(LINKS, R.drawable.links, getResources().getString(R.string.links)));

        mainMenuItems.add(new MenuItem(PORTFOLIO, R.drawable.portfolio, getResources().getString(R.string.portfolio)));
        mainMenuItems.add(new MenuItem(ORDERS, R.drawable.orders, getResources().getString(R.string.orders)));

        if (MyApplication.showMowazi)
            mainMenuItems.add(new MenuItem(MOWAZI, R.drawable.mowazi_off, getResources().getString(R.string.almowazi)));

        mainMenuItems.add(new MenuItem(SETTINGS, R.drawable.settings, getResources().getString(R.string.settings)));

        adapter = new MenuRecyclerAdapter(this, mainMenuItems, this);
        rvMenu.setLayoutManager(glm);
        rvMenu.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();


        Actions.checkSession(this);

        try {
            Actions.setMarketStatus(llMarketStatus, tvMarketStatus, MenuActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Actions.checkLanguage(this, started);
        Actions.InitializeSessionServiceV2(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    public void onItemClicked(View v, int position) {

        MenuItem menuItem = mainMenuItems.get(position);

        switch (menuItem.getId()) {

            case MARKET_INDEX:

                try {
                    startActivity(new Intent(MenuActivity.this, MarketIndexActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case SECTORS:

                try {
                    startActivity(new Intent(MenuActivity.this, SectorsActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case STOCKS:

                try {
                    startActivity(new Intent(MenuActivity.this, StockActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case TIME_SALES:

                try {
                    startActivity(new Intent(MenuActivity.this, TimeSalesActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case TOPS:

                try {
                    //startActivity(new Intent(MenuActivity.this, TopsPagerActivity.class));
                    startActivity(new Intent(MenuActivity.this, TopsActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case NEWS:

                try {
                    startActivity(new Intent(MenuActivity.this, NewsActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case FAVORITES:


                try {
                    startActivity(new Intent(MenuActivity.this, FavoritesActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case LINKS:

                try {
                    startActivity(new Intent(MenuActivity.this, QuickLinksActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case PORTFOLIO:

                try {
                    startActivity(new Intent(MenuActivity.this, PortfolioActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case ORDERS:

                try {
                    startActivity(new Intent(MenuActivity.this, OrdersActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case MOWAZI:


                try {
                    startActivity(new Intent(MenuActivity.this, MowaziHomeActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case SETTINGS:


                try {
                    startActivity(new Intent(MenuActivity.this, SettingsActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }
}

