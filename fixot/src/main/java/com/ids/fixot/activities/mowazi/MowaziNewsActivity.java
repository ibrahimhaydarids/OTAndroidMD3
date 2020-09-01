package com.ids.fixot.activities.mowazi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.classes.ZoomOutPageTransformer;
import com.ids.fixot.fragments.mowazi.AssemblyNewsFragments;
import com.ids.fixot.fragments.mowazi.CompaniesNewsFragment;
import com.ids.fixot.fragments.mowazi.GeneralNewsFragment;

import java.util.Calendar;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziNewsActivity extends AppCompatActivity {

    ListView list;
    int companyId = 0;
    boolean started = false;
    int footerButton;
    Intent intent;
    private ViewPager viewPager;
    private RelativeLayout rlNewsActivity, top, rootLayout;
    private TabLayout tlTabs;
    private GeneralNewsFragment generalNewsFragment = new GeneralNewsFragment();
    private CompaniesNewsFragment companiesNewsFragment = new CompaniesNewsFragment();
    private AssemblyNewsFragments assemblyNewsFragment = new AssemblyNewsFragments();
    private TextView tvNews;
    private int pager;
    private Boolean hasExtras = false;
    private RelativeLayout main;
    private LinearLayout bottom;
    private ImageButton back;

    public MowaziNewsActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mowazi_news);

        intent = getIntent();
        if (intent.hasExtra("pager")) {
            hasExtras = true;
            pager = getIntent().getExtras().getInt("pager");
        }
        tvNews = (TextView) findViewById(R.id.tvNews);
        if (intent.hasExtra("companyId")) {
            companyId = getIntent().getExtras().getInt("companyId");
            tvNews.setText(getResources().getString(R.string.news) + "-"
                    + getIntent().getExtras().getString("companyname"));
        }
        started = true;

        generalNewsFragment = new GeneralNewsFragment();
        Bundle b = new Bundle();
        b.putInt("companyId", companyId);
        generalNewsFragment.setArguments(b);
        companiesNewsFragment = new CompaniesNewsFragment();
        assemblyNewsFragment = new AssemblyNewsFragments();

        findViews();


        Actions.overrideFonts(this, rootLayout, true);
    }

    private void findViews() {

        top = (RelativeLayout) findViewById(R.id.top);
        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        rootLayout = (RelativeLayout) findViewById(R.id.rootLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tlTabs = (TabLayout) findViewById(R.id.tlTabs);
        bottom = (LinearLayout) findViewById(R.id.bottom);

        main = (RelativeLayout) findViewById(R.id.main);
        back = (ImageButton) findViewById(R.id.back);

        if (MyApplication.lang == MyApplication.ARABIC) {

            if (!intent.hasExtra("companyId")) {
                tvNews.setText(getResources().getString(R.string.news));
            }
        } else {

            if (!intent.hasExtra("companyId")) {
                tvNews.setText(getResources().getString(R.string.news));
            }
        }

        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(
                getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

        if (MyApplication.lang == MyApplication.ARABIC)
            viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1,
                    false);
        else
            viewPager.setCurrentItem(viewPager.getAdapter().getCount() + 2,
                    false);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            public void onPageSelected(int position) {
                tlTabs.getTabAt(-position + 2).select();
            }

            public void onPageScrollStateChanged(int state) {

            }
        });

        if (MyApplication.lang == MyApplication.ARABIC) {
            tlTabs.addTab(tlTabs.newTab().setText(
                    getResources().getString(R.string.mowazi_generalNews)));
            tlTabs.addTab(tlTabs.newTab().setText(
                    getResources().getString(R.string.mowazi_companyNews)));
            tlTabs.addTab(tlTabs.newTab().setText(
                    getResources().getString(R.string.mowazi_assemblyNews)));
        } else {
            tlTabs.addTab(tlTabs.newTab().setText(
                    getResources().getString(R.string.mowazi_assemblyNews)));
            tlTabs.addTab(tlTabs.newTab().setText(
                    getResources().getString(R.string.mowazi_companyNews)));
            tlTabs.addTab(tlTabs.newTab().setText(
                    getResources().getString(R.string.mowazi_generalNews)));
        }

        tlTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(-tab.getPosition() + 2);
                // viewPager.setCurrentItem(- tab.getPosition() + 3);
            }

            public void onTabUnselected(TabLayout.Tab tab) {

            }

            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (hasExtras) {
            if (MyApplication.lang == MyApplication.ARABIC)
                viewPager.setCurrentItem(-pager + 2);
            else
                viewPager.setCurrentItem(pager);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        LocalBroadcastManager.getInstance(MowaziNewsActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        if (started)
                            recreate();
                    }
                }, new IntentFilter(MyApplication.class.getName() + "ChangedLanguage")
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void back(View v) {
        finish();
    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (MyApplication.lang == MyApplication.ARABIC) {
                switch (position) {
                    case 0:
                        return assemblyNewsFragment;
                    // case 1:
                    // return companiesToMarketFragments;
                    case 1:
                        return companiesNewsFragment;
                    case 2:
                        return generalNewsFragment;
                }
            } else {
                switch (position) {
                    case 0:
                        return generalNewsFragment;
                    case 1:
                        return companiesNewsFragment;
                    // case 2:
                    // return companiesToMarketFragments;
                    case 2:
                        return assemblyNewsFragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
