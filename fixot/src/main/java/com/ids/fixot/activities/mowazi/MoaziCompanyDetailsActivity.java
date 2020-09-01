package com.ids.fixot.activities.mowazi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.MoaziSharedPreference;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.NewsActivity;
import com.ids.fixot.model.mowazi.AlmowaziDeal;
import com.ids.fixot.model.mowazi.MoaziCompany;
import com.ids.fixot.model.mowazi.MoaziCompanyNotifications;
import com.ids.fixot.model.mowazi.MoaziSector;
import com.ids.fixot.model.mowazi.MowaziDeal;
import com.ids.fixot.model.mowazi.MowaziNews;
import com.ids.fixot.model.mowazi.MowaziOnlineOrder;
import com.ids.fixot.parser.MoaziCompanyIdParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by Amal on 6/6/2016.
 */
public class MoaziCompanyDetailsActivity extends AppCompatActivity {
    ListView list;
    ArrayList<MoaziCompany> companies = new ArrayList<MoaziCompany>();
    ProgressBar progress;

    private boolean flag_loading = false;
    TableLayout table;
    LinearLayout relabout, allcompanydeallayout, chartlayout, financialstatmentlayout, newslayout, orderlayout, livelayout;
    //  RelativeLayout llCompOptions;
    private RelativeLayout top;
    LinearLayout rlTrade;
    MoaziCompany company;
    private MoaziSector sector;
    private ImageView ivTrade, ivFavorite;
    private MoaziSharedPreference settings;
    private RelativeLayout main;
    private ImageButton back;
    private DrawerLayout drawer;
    private TextView tvTradeAct, tvSave;
    String companyID, companyName;
    private TextView companyname;
    String forbid;


    private ArrayList<String> notificationsIDs = new ArrayList<String>();
    private CheckBox cbGeneralNewsNotifications, cbTradesNotifications, cbOrdersNotification;
    private boolean newsNotifications = false, tradesNotifications = false, ordersNotifications = false;
    private MoaziCompanyNotifications companyNotifications;
    private ArrayList<MoaziCompanyNotifications> allCompanyNotifications = new ArrayList<MoaziCompanyNotifications>();
    private String notifications = "MoaziCompanyNotifications";
    private SharedPreferences mshared;
    private SharedPreferences.Editor edit;
    private NotificationsSettings getNotificationsSettings;
    ScrollView mscScrollView;
    private RelativeLayout rlGeneralNews, rlTrades, rlOrders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_moazi_companydetail);
        settings = new MoaziSharedPreference();
        Actions.setViewResizing(MoaziCompanyDetailsActivity.this);
        Actions.setBarColor(MoaziCompanyDetailsActivity.this);
        makeTranslation();
        //   company = getIntent().getExtras().getParcelable("company");
        companyID = getIntent().getExtras().getString("companyId");
        companyName = getIntent().getExtras().getString("companyname");
        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);
        Log.wtf("companyId", companyID + "");
        Log.wtf("on", "create");
        ivTrade = findViewById(R.id.ivTrade);
        tvTradeAct = findViewById(R.id.tvTradeAct);
        main = findViewById(R.id.main);

        rlOrders = findViewById(R.id.rlOrders);
        rlTrades = findViewById(R.id.rlTrades);
        rlGeneralNews = findViewById(R.id.rlGeneralNews);

        rlTrade = findViewById(R.id.llPlaceOrderLayout);

        if (MyApplication.enablePlaceOrder) {


            try {
                forbid = getIntent().getExtras().getString("forbid");
                if (forbid.equals("false")) {
                    ivTrade.setVisibility(View.GONE);
                    rlTrade.setVisibility(View.GONE);
                }
            } catch (Exception e) {

                e.printStackTrace();
            }
        } else {

            rlTrade.setVisibility(View.GONE);
        }
        back = findViewById(R.id.back);
        drawer = findViewById(R.id.drawer_layout);
        ivFavorite = findViewById(R.id.ivFavorite);
        tvSave = findViewById(R.id.tvSave);
        progress = findViewById(R.id.progress);
        mscScrollView = findViewById(R.id.scroll);


        mshared = PreferenceManager.getDefaultSharedPreferences(MoaziCompanyDetailsActivity.this);
        edit = mshared.edit();


       // Actions.InitializeAnFillDrawerList(MoaziCompanyDetailsActivity.this);
        companyname = findViewById(R.id.companyname);


        /*if(MyApplication.lang.equals("ar")){
            companyname.setText(company.getNameAr());
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            drawer.setTextDirection(View.TEXT_DIRECTION_RTL);
            back.setImageResource(R.drawable.arrow);
        }else{
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            companyname.setText(company.getNameEn());
            drawer.setTextDirection(View.TEXT_DIRECTION_LTR);
            back.setImageResource(R.drawable.arrowright);
        }
        companyname.setTypeface(null, Typeface.BOLD);*/


        if (MyApplication.lang==MyApplication.ARABIC) {
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            drawer.setTextDirection(View.TEXT_DIRECTION_RTL);
            back.setImageResource(R.drawable.arrow);

            companyname.setTypeface(MyApplication.droidbold);

        } else {
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            drawer.setTextDirection(View.TEXT_DIRECTION_LTR);
            back.setImageResource(R.drawable.arrow);
            companyname.setTypeface(MyApplication.opensansbold);

        }

/*        if (MyApplication.Pahse2) {
            final LinearLayout linear = findViewById(R.id.llNotificationLayout);
            linear.setVisibility(View.VISIBLE);
            // llCompOptions.setVisibility(View.VISIBLE);
            chartlayout.setVisibility(View.VISIBLE);
            ivFavorite.setVisibility(View.VISIBLE);
        }*/
        getCompanyById get = new getCompanyById();
        get.execute();

    }

    public void GoAboutUs(View v) {


        Intent i = new Intent();
        i.setClass(MoaziCompanyDetailsActivity.this, MowaziAboutUsActivity.class);
        Bundle b = new Bundle();
        b.putParcelable("company", company);
        i.putExtras(b);
        startActivity(i);

    }

    public void showHideLinear(View v) {
        final RelativeLayout linear = findViewById(R.id.rlNotification);
        final RelativeLayout rlOptions = findViewById(R.id.rlOptions);
        if (linear.getVisibility() == View.VISIBLE)
            linear.setVisibility(View.GONE);
        else {
            linear.setVisibility(View.VISIBLE);

            mscScrollView.postDelayed(new Runnable() {
                public void run() {
                    mscScrollView.fullScroll(View.FOCUS_DOWN);
                }
            }, 500);
        }
    }

    public class getCompanyById extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            String url = "" + MyApplication.mowaziUrl + "/GetCompany?";
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("id", "" + companyID);


            result = ConnectionRequests.POST(url, parameters);
            MoaziCompanyIdParser parser = new MoaziCompanyIdParser(result,  MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                company = parser.GetCompany();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            try {


                if (MyApplication.lang==MyApplication.ARABIC) {

                    companyname.setText(company.getSymbolAr());
                } else {

                    companyname.setText(company.getSymbolEn());

                }
                allcompanydeallayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (MyApplication.lang==MyApplication.ENGLISH)
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MDealsActivity.class).putExtra("companyId", company.getCompanyId()).putExtra("companyname", company.getSymbolEn()));
                        else
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MDealsActivity.class).putExtra("companyId", company.getCompanyId()).putExtra("companyname", company.getSymbolAr()));
                    }
                });

                newslayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (MyApplication.lang==MyApplication.ENGLISH)
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziNewsActivity.class)
                                    .putExtra("companyId", company.getCompanyId())
                                    .putExtra("companyname", company.getSymbolEn())
                                    .putExtra("pager", 1));
                        else
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziNewsActivity.class)
                                    .putExtra("companyId", company.getCompanyId())
                                    .putExtra("companyname", company.getSymbolAr())
                                    .putExtra("pager", 1));
                    }
                });

                orderlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (MyApplication.lang==MyApplication.ENGLISH)
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziOnlineOrdersActivity.class).putExtra("companyId", company.getCompanyId()).putExtra("companyname", company.getSymbolEn()));
                        else
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziOnlineOrdersActivity.class).putExtra("companyId", company.getCompanyId()).putExtra("companyname", company.getSymbolAr()));
                    }
                });




                ivFavorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (settings.isFound(MoaziCompanyDetailsActivity.this, company)) {
                            settings.removeFavorite(MoaziCompanyDetailsActivity.this, company);
                            ivFavorite.setImageResource(R.drawable.off);
                            ivFavorite.setColorFilter(ContextCompat.getColor(MoaziCompanyDetailsActivity.this, R.color.buttonGray));
                            Toast.makeText(MoaziCompanyDetailsActivity.this, getResources().getString(R.string.favoriteRemoved), Toast.LENGTH_LONG).show();
                        } else {
                            settings.addFavorite(MoaziCompanyDetailsActivity.this, company);
                            ivFavorite.setImageResource(R.drawable.fav);
                            ivFavorite.setColorFilter(ContextCompat.getColor(MoaziCompanyDetailsActivity.this, R.color.colorPrimaryDark));
                            Toast.makeText(MoaziCompanyDetailsActivity.this, getResources().getString(R.string.favoriteAdd), Toast.LENGTH_LONG).show();
                        }

                    }
                });

                ivTrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    /*    if (!MyApplication.logged)
                            Actions.CreateLoginDialog(MoaziCompanyDetailsActivity.this, getString(R.string.youmustlogin), "placeorder");
                        else
*/
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziPlaceOrderActivity.class).putExtra("companyId", companyID));
                    }
                });


                rlTrade.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                /*        if (!MyApplication.logged)
                            Actions.CreateLoginDialog(MoaziCompanyDetailsActivity.this, getString(R.string.youmustlogin), "placeorder");
                        else*/
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziPlaceOrderActivity.class).putExtra("companyId", companyID));
                    }
                });

                chartlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (MyApplication.lang==MyApplication.ENGLISH)
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MoaziCompanyChartActivity.class)
                                    .putExtra("companyId", company.getCompanyId())
                                    .putExtra("chartUrl", company.getChartUrl())
                                    .putExtra("companyname", company.getSymbolEn()));
                        else
                            startActivity(new Intent(MoaziCompanyDetailsActivity.this, MoaziCompanyChartActivity.class)
                                    .putExtra("companyId", company.getCompanyId())
                                    .putExtra("chartUrl", company.getChartUrl())
                                    .putExtra("companyname", company.getSymbolAr()));
                    }
                });

                cbOrdersNotification = findViewById(R.id.cbOrdersNotification);
                cbTradesNotifications = findViewById(R.id.cbTradesNotifications);
                cbGeneralNewsNotifications = findViewById(R.id.cbGeneralNewsNotifications);

                rlGeneralNews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cbGeneralNewsNotifications.setChecked(!cbGeneralNewsNotifications.isChecked());

                    }
                });

                rlOrders.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cbOrdersNotification.setChecked(!cbOrdersNotification.isChecked());
                    }
                });

                rlTrades.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        cbTradesNotifications.setChecked(!cbTradesNotifications.isChecked());
                    }
                });


                int comparison = settings.isFoundLaunchSettings(MoaziCompanyDetailsActivity.this, company.getCompanyId());
                if (comparison != -1) {
                    Log.wtf("company id", "" + comparison);


                    companyNotifications = settings.getSettings(MoaziCompanyDetailsActivity.this).get(comparison);


                    cbOrdersNotification.setChecked(companyNotifications.isOrdersNotifications());
                    ordersNotifications = companyNotifications.isOrdersNotifications();
                    if (ordersNotifications) {
                        notificationsIDs.add("2");
                    } else {
                        notificationsIDs.remove("2");
                    }

                    cbTradesNotifications.setChecked(companyNotifications.isTradesNotifications());
                    tradesNotifications = companyNotifications.isTradesNotifications();
                    if (tradesNotifications) {
                        notificationsIDs.add("1");
                    } else {
                        notificationsIDs.add("1");
                    }

                    cbGeneralNewsNotifications.setChecked(companyNotifications.isNewsNotifications());
                    newsNotifications = companyNotifications.isNewsNotifications();
                    if (newsNotifications) {
                        notificationsIDs.add("3");
                    } else {
                        notificationsIDs.add("3");
                    }

                } else {
                    Log.wtf("other ", "company");
                }

                cbOrdersNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            ordersNotifications = true;
                            notificationsIDs.add("2");

                            saveClick();
                        } else {
                            ordersNotifications = false;
                            notificationsIDs.remove("2");

                            saveClick();
                        }
                    }
                });

                cbTradesNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            tradesNotifications = true;
                            notificationsIDs.add("1");

                            saveClick();
                        } else {
                            tradesNotifications = false;
                            notificationsIDs.remove("1");

                            saveClick();
                        }
                    }
                });

                cbGeneralNewsNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            newsNotifications = true;
                            notificationsIDs.add("3");

                            saveClick();
                        } else {
                            newsNotifications = false;
                            notificationsIDs.remove("3");

                            saveClick();
                        }
                    }
                });

                tvSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        saveClick();
                /*companyNotifications = new MoaziCompanyNotifications();
                companyNotifications.setCompanyId(company.getCompanyId());
                companyNotifications.setNewsNotifications(cbGeneralNewsNotifications.isChecked());
                companyNotifications.setOrdersNotifications(cbOrdersNotification.isChecked());
                companyNotifications.setTradesNotifications(cbTradesNotifications.isChecked());

                if(settings.isFoundSettings(MoaziCompanyDetailsActivity.this,companyNotifications)){
                    Log.wtf("object", "found");
                    settings.removeSettings(MoaziCompanyDetailsActivity.this,companyNotifications);
                    Log.wtf("object", "deleted");
                    settings.addSettings(MoaziCompanyDetailsActivity.this,companyNotifications);
                    Log.wtf("object", "re added");
                }else{
                    Log.wtf("object", "added");
                    settings.addSettings(MoaziCompanyDetailsActivity.this,companyNotifications);
                }*/
                    }
                });
                if (settings.isFound(MoaziCompanyDetailsActivity.this, company)) {
                    ivFavorite.setImageResource(R.drawable.fav);
                    ivFavorite.setColorFilter(ContextCompat.getColor(MoaziCompanyDetailsActivity.this, R.color.colorPrimaryDark));
                    Log.wtf("fav", "comp");
                } else {
                    ivFavorite.setImageResource(R.drawable.off);
                    ivFavorite.setColorFilter(ContextCompat.getColor(MoaziCompanyDetailsActivity.this, R.color.buttonGray));
                    Log.wtf("not fav", "comp");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void saveClick() {
        if (settings.isFound(MoaziCompanyDetailsActivity.this, company)) {
            ivFavorite.setImageResource(R.drawable.fav);
            ivFavorite.setColorFilter(ContextCompat.getColor(MoaziCompanyDetailsActivity.this, R.color.colorPrimaryDark));
        } else {
            ivFavorite.setImageResource(R.drawable.off);
            ivFavorite.setColorFilter(ContextCompat.getColor(MoaziCompanyDetailsActivity.this, R.color.buttonGray));
        }

        Log.wtf("notifications", notificationItems(notificationsIDs));
        getNotificationsSettings = new NotificationsSettings();
        getNotificationsSettings.execute();
    }

    public void makeTranslation() {
        table = findViewById(R.id.table);
        relabout = findViewById(R.id.relaboutus);
        //  llCompOptions = (RelativeLayout) findViewById(R.id.llCompOptions);
        top = findViewById(R.id.top);
        allcompanydeallayout = findViewById(R.id.aldeallayout);
        chartlayout = findViewById(R.id.chartlayout);
        financialstatmentlayout = findViewById(R.id.financiallayout);
        newslayout = findViewById(R.id.newslayout);
        orderlayout = findViewById(R.id.orderslayout);
        livelayout = findViewById(R.id.livelayout);

        if (MyApplication.lang==MyApplication.ARABIC) {
            top.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            top.setTextDirection(View.TEXT_DIRECTION_RTL);
            relabout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            // llCompOptions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            // llCompOptions.setTextDirection(View.TEXT_DIRECTION_RTL);
            allcompanydeallayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            chartlayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            financialstatmentlayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            newslayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            orderlayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        allcompanydeallayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziDealsActivity.class).putExtra("companyId", Integer.parseInt(companyID)).putExtra("companyname", companyName));
            }
        });

        newslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziNewsActivity.class).putExtra("companyId", Integer.parseInt(companyID)).putExtra("companyname", companyName));
            }
        });

        orderlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MoaziCompanyDetailsActivity.this, MowaziOrdersSummaryActivity.class).putExtra("companyId", Integer.parseInt(companyID)).putExtra("companyname", companyName));
            }
        });



/*        if (MyApplication.enablePlaceOrder){
            livelayout.setVisibility(View.GONE);
        }else {

            livelayout.setVisibility(View.VISIBLE);
        }*/
    }

    public void back(View v) {
        finish();
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



    private String notificationItems(ArrayList<String> list) {
        String items = "";
        for (int i = 0; i < list.size(); i++) {
            items += list.get(i);
            items += ",";
        }
        if (!items.equals(""))
            items = items.substring(0, items.length() - 1);
        return items;
    }

    protected class NotificationsSettings extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/UpdateUserNotificationSetting?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("companyId", "" + company.getCompanyId());
            parameters.put("deviceId", mshared.getString("DeviceId", ""));
            parameters.put("type", notificationItems(notificationsIDs));

            result = ConnectionRequests.POST(url, parameters);

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            try {

                progress.setVisibility(View.GONE);
                if (result.equals("True")) {
                    Actions.generateSnack(MoaziCompanyDetailsActivity.this, drawer, getResources().getString(R.string.notification_save));

                    companyNotifications = new MoaziCompanyNotifications();
                    companyNotifications.setCompanyId(company.getCompanyId());
                    companyNotifications.setNewsNotifications(newsNotifications);
                    companyNotifications.setOrdersNotifications(ordersNotifications);
                    companyNotifications.setTradesNotifications(tradesNotifications);

                    if (settings.isFoundSettings(MoaziCompanyDetailsActivity.this, companyNotifications)) {
                        Log.wtf("object", "found");
                        settings.removeSettings(MoaziCompanyDetailsActivity.this, companyNotifications);
                        Log.wtf("object", "deleted");
                        settings.addSettings(MoaziCompanyDetailsActivity.this, companyNotifications);
                        Log.wtf("object", "re added");
                    } else {
                        Log.wtf("object", "added");
                        settings.addSettings(MoaziCompanyDetailsActivity.this, companyNotifications);
                    }

                } else {
                    Actions.generateSnack(MoaziCompanyDetailsActivity.this, drawer, getResources().getString(R.string.error));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
