package com.ids.fixot.activities.mowazi;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.mowazi.fragment.MoaziAssmeblyResultFragment;
import com.ids.fixot.activities.mowazi.fragment.MoaziPreviousAssemblyFragment;
import com.ids.fixot.activities.mowazi.fragment.MoaziUpcomingAssemblyFragment;
import com.ids.fixot.classes.ZoomOutPageTransformer;


/**
 * Created by dev on 6/14/2016.
 */

public class MoaziAssembliesActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private RelativeLayout rlCommunitiesActivity, top;
    private TabLayout tlTabs;
    private MoaziUpcomingAssemblyFragment upcomingAssembly = new MoaziUpcomingAssemblyFragment();
    private MoaziAssmeblyResultFragment assemblyResult = new MoaziAssmeblyResultFragment();
    private MoaziPreviousAssemblyFragment previousAssembly = new MoaziPreviousAssemblyFragment();
    private TextView tvAssemblies;
    private int pager;
    private Boolean hasExtras=false;
    ListView list;
    private RelativeLayout main;
    private LinearLayout bottom;
    private ImageButton back;
    private DrawerLayout drawer;
    public static Activity act;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moazi_assemblies);

        act = MoaziAssembliesActivity.this;

        Intent intent = getIntent();
        if(intent.hasExtra("pager")) {
            hasExtras = true;
            pager = getIntent().getExtras().getInt("pager");
        }else{
            if (MyApplication.lang==MyApplication.ENGLISH){
                hasExtras = true;
                //hsen , it was 1 i set it to 0
                pager = 0;
            }
        }
       // Actions.InitializeAnFillDrawerList(MoaziAssembliesActivity.this);
        Log.wtf("pager value",""+pager);


        //Actions.setViewResizing(MoaziAssembliesActivity.this);

        rlCommunitiesActivity = findViewById(R.id.rlCommunitiesActivity);
        main = findViewById(R.id.main);
        bottom = findViewById(R.id.bottom);
        back = findViewById(R.id.back);
        drawer = findViewById(R.id.drawer_layout);
        top = findViewById(R.id.top);
        viewPager = findViewById(R.id.viewPager);

        tlTabs = findViewById(R.id.tlTabs);
        tvAssemblies = findViewById(R.id.tvAssemblies);
        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);
        Actions.setLocal(MyApplication.lang, this);

        Actions.setBarColor(this);

        if(MyApplication.lang==MyApplication.ARABIC){
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            drawer.setTextDirection(View.TEXT_DIRECTION_RTL);
            back.setImageResource(R.drawable.arrow);
            tvAssemblies.setText(getResources().getString(R.string.communityHome));
        }else{
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            drawer.setTextDirection(View.TEXT_DIRECTION_LTR);
            //main.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //main.setTextDirection(View.TEXT_DIRECTION_RTL);
            back.setImageResource(R.drawable.arrow);
            //bottom.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //bottom.setTextDirection(View.TEXT_DIRECTION_RTL);
            tvAssemblies.setText(getResources().getString(R.string.communityHome));
        }
        tvAssemblies.setTypeface(null, Typeface.BOLD);
        ScreenSlidePagerAdapter adapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());

//        if(MyApplication.lang.equals("ar"))
//            viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1,false);
//        else
//            viewPager.setCurrentItem(viewPager.getAdapter().getCount() +2 ,false);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                /*tlTabs.getTabAt(-position + 2).select();
                if(MyApplication.lang.equals("ar")) {
                    if (position == 0)
                        if (!MyApplication.logged) {
                            Intent i = new Intent();
                            i.setClass(getApplicationContext(),LoginActivity.class);
                            i.putExtra("activity","assemblies");
                            startActivity(i);
                            finish();
                        }
                }
                else {
                    if (position == 2)
                        if (!MyApplication.logged) {
                            Actions.startActivity(MoaziAssembliesActivity.this, LoginActivity.class, false);
                            Intent i = new Intent();
                            i.setClass(getApplicationContext(),LoginActivity.class);
                            i.putExtra("activity","assemblies");
                            startActivity(i);
                            finish();
                        }
                }*/

                //hsen , added this line because there was no tab selection
                tlTabs.getTabAt( position ).select();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //    if (MyApplication.lang.equals("en")) {
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.upcomingCommunities)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.previousCommunities)));
        tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.resultCommunities)));
//        } else {
//            tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.resultCommunities)));
//            tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.previousCommunities)));
//            tlTabs.addTab(tlTabs.newTab().setText(getResources().getString(R.string.upcomingCommunities)));
//        }

        tlTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition( ));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //hsen , added this line to reach designated fragment from drawer
        if(hasExtras){
            viewPager.setCurrentItem(pager);

        }

        TabLayout.Tab tab = tlTabs.getTabAt(2);
        tlTabs.removeTab(tab);


//        if(hasExtras) {
//
//            if (MyApplication.lang.equals("ar"))
//                viewPager.setCurrentItem(-pager + 2);
//            else
//                viewPager.setCurrentItem(pager);
//        }
    }

    public void showDrawer(View v) {
        if (MyApplication.lang==MyApplication.ENGLISH) {
            //    list.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            // list.setTextDirection(View.TEXT_DIRECTION_LTR);
            drawer.openDrawer(Gravity.LEFT);
        } else {
            drawer.openDrawer(Gravity.RIGHT);
            //  list.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //  list.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        }
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

            // if (MyApplication.lang.equals("en")) {
            switch (position) {
                case 0:
                    return upcomingAssembly;
                case 1:
                    return previousAssembly;
                case 2:
                   return assemblyResult;
            }
//            } else {
//                switch (position) {
//                    case 0:
//                        return assemblyResult;
//                    case 1:
//                        return previousAssembly;
//                    case 2:
//                        return upcomingAssembly;
//                }
//            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
