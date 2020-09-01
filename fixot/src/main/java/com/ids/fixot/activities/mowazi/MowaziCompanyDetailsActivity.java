package com.ids.fixot.activities.mowazi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziSector;
import com.ids.fixot.parser.MowaziCompanyIdParser;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by Amal on 6/6/2016.
 */
public class MowaziCompanyDetailsActivity extends AppCompatActivity {

    ArrayList<MowaziCompany> companies = new ArrayList<MowaziCompany>();
    ProgressBar progress;
    TableLayout table;
    LinearLayout relabout, allcompanydeallayout, chartlayout,
            financialstatmentlayout, newslayout, orderlayout, llCompOptions;
    MowaziCompany company = new MowaziCompany();
    ImageView ivTrade, ivFavorite;
    RelativeLayout main;
    ImageButton back;
    int footerButton;
    String companyID;
    RelativeLayout rlTrade;
    String forbid;
    private boolean flag_loading = false;
    private RelativeLayout top, llOrdersLayout;
    private MowaziSector sector;
    private TextView companyname;

    public MowaziCompanyDetailsActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mowazi_companydetail);


        llOrdersLayout = (RelativeLayout) findViewById(R.id.llOrdersLayout);
        relabout = (LinearLayout) findViewById(R.id.relaboutus);
        ivTrade = (ImageView) findViewById(R.id.ivTrade);
        main = (RelativeLayout) findViewById(R.id.main_bar);
        back = (ImageButton) findViewById(R.id.back);

        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        companyname = (TextView) findViewById(R.id.companyname);

        allcompanydeallayout = (LinearLayout) findViewById(R.id.aldeallayout);
        newslayout = (LinearLayout) findViewById(R.id.newslayout);
        orderlayout = (LinearLayout) findViewById(R.id.orderslayout);
        ivTrade = (ImageView) findViewById(R.id.ivTrade);
        allcompanydeallayout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (MyApplication.lang == MyApplication.ENGLISH)

                    startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                            MDealsActivity.class).putExtra("companyId",
                            company.getCompanyId()).putExtra("companyname",
                            company.getSymbolEn()));
                else

                    startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                            MDealsActivity.class).putExtra("companyId",
                            company.getCompanyId()).putExtra("companyname",
                            company.getSymbolAr()));
            }
        });

        newslayout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                if (MyApplication.lang == MyApplication.ENGLISH)
                    startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                            MowaziNewsActivity.class).putExtra("companyId",
                            company.getCompanyId()).putExtra("companyname",
                            company.getSymbolEn()));

                else
                    startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                            MowaziNewsActivity.class).putExtra("companyId",
                            company.getCompanyId()).putExtra("companyname",
                            company.getSymbolAr()));

            }
        });

        orderlayout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (MyApplication.lang == MyApplication.ENGLISH)

                    startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                            MowaziOrdersSummaryActivity.class).putExtra("companyId",
                            company.getCompanyId()).putExtra("companyname",
                            company.getSymbolEn()));
                else

                    startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                            MowaziOrdersSummaryActivity.class).putExtra("companyId",
                            company.getCompanyId()).putExtra("companyname",
                            company.getSymbolAr()));
            }
        });


        rlTrade = (RelativeLayout) findViewById(R.id.rlTrade);

        rlTrade.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                        MowaziPlaceOrderActivity.class).putExtra("companyId",
                        company.getCompanyId() + "").putExtra("flag", 0));
            }
        });
        try {
            forbid = getIntent().getExtras().getString("forbid");
            if (forbid.equals("false")) {
                ivTrade.setVisibility(View.GONE);
                rlTrade.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        companyID = getIntent().getExtras().getString("companyid");

        //menu_favorite.setChecked(true);
        getCompanyById get = new getCompanyById();
        get.execute();

        //Actions.initializeToolBar(getResources().getString(R.string.mowazi_company), this);
        Actions.overrideFonts(this, llOrdersLayout, true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void back(View v) {
        finish();
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
            MowaziCompanyIdParser parser = new MowaziCompanyIdParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                company = parser.GetCompany();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);

            companyname.setText(MyApplication.lang == MyApplication.ARABIC ? company.getSymbolAr() : company.getSymbolEn());

            relabout.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent i = new Intent();
                    i.setClass(MowaziCompanyDetailsActivity.this, MowaziAboutUsActivity.class);
                    Bundle b = new Bundle();
                    b.putParcelable("company", company);
                    i.putExtras(b);
                    startActivity(i);

                }
            });


            allcompanydeallayout.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    startActivity(new Intent(MowaziCompanyDetailsActivity.this, MDealsActivity.class)
                            .putExtra("companyId", company.getCompanyId())
                            .putExtra("companyname", MyApplication.lang == MyApplication.ENGLISH ? company.getSymbolEn() : company.getSymbolAr()));
                }
            });

            newslayout.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                            MowaziNewsActivity.class)
                            .putExtra("companyId", company.getCompanyId())
                            .putExtra("pager", 1)
                            .putExtra("companyname", MyApplication.lang == MyApplication.ENGLISH ? company.getSymbolEn() : company.getSymbolAr()));
                }
            });

            orderlayout.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    startActivity(new Intent(MowaziCompanyDetailsActivity.this,
                            MowaziOrdersSummaryActivity.class)
                            .putExtra("companyId", company.getCompanyId())
                            .putExtra("companyname", MyApplication.lang == MyApplication.ENGLISH ? company.getSymbolEn() : company.getSymbolAr()));
                }
            });

        }
    }

}
