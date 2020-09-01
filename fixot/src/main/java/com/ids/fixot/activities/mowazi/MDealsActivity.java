package com.ids.fixot.activities.mowazi;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.mowaziAdapters.MowaziCompanySpinnerAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MowaziDealsRecyclerAdapter;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziDeal;
import com.ids.fixot.model.mowazi.SimpleDividerItemDecoration;
import com.ids.fixot.parser.MowaziCompanyNameParser;
import com.ids.fixot.parser.MowaziDealParser;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by DEV on 3/28/2018.
 */

public class MDealsActivity extends AppCompatActivity implements
        MowaziDealsRecyclerAdapter.RecyclerViewOnItemClickListener {

    int footerButton;
    private LinearLayoutManager llm;
    private RelativeLayout llDealsLayout, top, rlFromDate, rlToDate;
    private RecyclerView rvDeals;
    private TextView tvDeals, tvFromDate, tvToDate;
    private String fromDate = "", toDate = "", returnedDatefom = "",
            returnedDateto = "";
    private Spinner spinnerCompanies;
    private MowaziDealsRecyclerAdapter adapter;
    private TextView tvFilter;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<MowaziCompany> allCompanies = new ArrayList<>();
    private ArrayList<MowaziDeal> allDeals = new ArrayList<>();
    private boolean flagLoading = false, pulltoRefresh = false;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisibleItems;
    private int mYear, mMonth, mDay;
    private ProgressBar pbLastDeals;
    private GetDeals getDeals;
    private GetCompanies getCompanies;
    private MowaziCompanySpinnerAdapter spinnerAdapter;
    private RelativeLayout searchlayoutall;
    private int companyId = 0;
    private MowaziCompany selectedcompany = new MowaziCompany();
    private TextView tvNodata;
    private RelativeLayout main;
    private ImageButton back;
    private DrawerLayout drawer;
    private boolean extra = false;
    private boolean started = false;

    public MDealsActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_mowazi_deals);

        Intent intent = getIntent();

        llDealsLayout = (RelativeLayout) findViewById(R.id.llDealsLayout);
        top = (RelativeLayout) findViewById(R.id.top);
        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        started = true;
        main = (RelativeLayout) findViewById(R.id.main);
        back = (ImageButton) findViewById(R.id.back);
        rlFromDate = (RelativeLayout) findViewById(R.id.rlFromDate);
        rlToDate = (RelativeLayout) findViewById(R.id.rlToDate);

        tvNodata = (TextView) findViewById(R.id.tvNoData);
        pbLastDeals = (ProgressBar) findViewById(R.id.pbLastDeals);
        tvDeals = (TextView) findViewById(R.id.tvDeals);
        extra = intent.hasExtra("companyId");
        if (extra) {
            companyId = getIntent().getExtras().getInt("companyId");
            tvDeals.setText(getResources().getString(R.string.lastdeals) + "-" + getIntent().getExtras().getString("companyname"));
        }

        tvFromDate = (TextView) findViewById(R.id.tvFromDate);
        tvToDate = (TextView) findViewById(R.id.tvToDate);
        searchlayoutall = (RelativeLayout) findViewById(R.id.searchlayoutall);
        spinnerCompanies = (Spinner) findViewById(R.id.spinnerCompanies);
        MowaziCompany c = new MowaziCompany();
        c.setSymbolAr("--" + getString(R.string.mowazi_companies) + "---");
        c.setSymbolEn("--" + getString(R.string.mowazi_companies) + "---");
        allCompanies.add(c);
        spinnerAdapter = new MowaziCompanySpinnerAdapter(MDealsActivity.this, allCompanies);
        spinnerAdapter.setDropDownViewResource(R.layout.need_list_spinner_item);
        spinnerCompanies.setAdapter(spinnerAdapter);

        if (MyApplication.lang == MyApplication.ARABIC) {

            if (!extra)
                tvDeals.setText(getResources().getString(R.string.lastdeals));
        } else {

            if (!extra)
                tvDeals.setText(getResources().getString(R.string.lastdeals));
        }


        llm = new LinearLayoutManager(MDealsActivity.this);
        rvDeals = (RecyclerView) findViewById(R.id.rvDeals);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    public void onRefresh() {
                        pulltoRefresh = true;
                        refreshItems();

                    }
                });
        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        rlFromDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                showDateTimePickerFrom(rlFromDate);

            }
        });

        rlToDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                showDateTimePickerFrom(rlToDate);

            }
        });

        rvDeals.setLayoutManager(llm);
        // allDeals.add(new Deal());
        adapter = new MowaziDealsRecyclerAdapter(this, allDeals, this);
        rvDeals.setAdapter(adapter);
        rvDeals.addItemDecoration(new SimpleDividerItemDecoration(this, 0, R.drawable.line_divider));

        rvDeals.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) // check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisibleItems = llm.findFirstVisibleItemPosition();

                    if (flagLoading == false) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            flagLoading = true;
                            addLastDeals();
                        }
                    }
                }
            }
        });

        spinnerCompanies
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parentView,
                                               View view, int position, long id) {

                        if (position != 0) {
                            MowaziCompany c = spinnerAdapter
                                    .getItem(position);
                            selectedcompany = c;
                        } else
                            selectedcompany.setCompanyId(0);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        Actions.checkSession(this);

        LocalBroadcastManager.getInstance(MDealsActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        if (started)
                            recreate();
                    }
                }, new IntentFilter(MyApplication.class.getName() + "ChangedLanguage")
        );
        getCompanies = new GetCompanies();
        getCompanies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void back(View v) {
        finish();
    }

    public void closeFilter(View v) {
        if (searchlayoutall.getVisibility() == View.VISIBLE) {
            searchlayoutall.setVisibility(View.GONE);
            fromDate = "";
            toDate = "";
            Intent intent = getIntent();
            if (intent.hasExtra("companyId")) // try on 232 al razi company
                companyId = getIntent().getExtras().getInt("companyId");
            else
                companyId = 0;
        } else {
            searchlayoutall.setVisibility(View.VISIBLE);
            companyId = selectedcompany.getCompanyId();
            fromDate = returnedDatefom;
            toDate = returnedDateto;
        }

    }

    public void search(View v) {
        allDeals.clear();
        companyId = selectedcompany.getCompanyId();

        // allDeals.add(new Deal());
        getDeals = new GetDeals();
        getDeals.execute("0");
    }

    public void clear(View v) {
        fromDate = "";
        toDate = "";

        Intent intent = getIntent();
        if (intent.hasExtra("companyId")) // try on 232 al razi company
            companyId = getIntent().getExtras().getInt("companyId");
        else
            companyId = 0;

        tvFromDate.setText(getString(R.string.mowazi_from));
        tvToDate.setText(getString(R.string.mowazi_to));
        refreshItems();
    }

    public String showDateTimePickerFrom(final View v) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        String mm = String.valueOf(monthOfYear);
                        if (mm.length() == 1)
                            mm = "0" + mm;
                        String dd = String.valueOf(dayOfMonth);
                        if (dd.length() == 1)
                            dd = "0" + dd;

                        if (v == rlFromDate) {
                            returnedDatefom = year + "-" + mm + "-" + dd;
                            tvFromDate.setText(year + "-" + mm + "-" + dd);
                            fromDate = returnedDatefom;

                        } else {
                            returnedDateto = year + "-" + mm + "-" + dd;
                            tvToDate.setText(year + "-" + mm + "-" + dd);
                            toDate = returnedDateto;
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        return "";
    }

    private void refreshItems() {
        getDeals = new GetDeals();
        getDeals.execute("0");
    }

    private void onItemsLoadComplete() {
        Log.d("Recycler", "Refreshed");
        swipeContainer.setRefreshing(false);
    }

    public void onItemClicked(View v, int position) {
        Log.d("Item", "Clicked");
    }

    public void addLastDeals() {
        if (allDeals.size() > 0) {
            getDeals = new GetDeals();

            getDeals.execute(String.valueOf(allDeals.get(allDeals.size() - 1)
                    .getDealId()));
            flagLoading = false;
        }
    }

    protected class GetDeals extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbLastDeals.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetDeals?";

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("companyId", "" + companyId);
            parameters.put("sectorId", "0");
            parameters.put("defaultCountryId", "116");
            parameters.put("fromDate", fromDate);
            parameters.put("toDate", toDate);
            parameters.put("id", params[0]);
            parameters.put("top", "100");

            result = ConnectionRequests.POST(url, parameters);

            MowaziDealParser parser = new MowaziDealParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                if (pulltoRefresh) {
                    allDeals.clear();
                    // allDeals.add(new Deal());
                    pulltoRefresh = false;
                }

                allDeals.addAll(parser.GetDeals());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
            onItemsLoadComplete();
            if (allDeals.size() == 0) {
                tvNodata.setVisibility(View.VISIBLE);
                swipeContainer.setVisibility(View.GONE);
            } else {
                tvNodata.setVisibility(View.GONE);
                swipeContainer.setVisibility(View.VISIBLE);
            }
            pbLastDeals.setVisibility(View.GONE);

        }
    }

    protected class GetCompanies extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLastDeals.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetCompaniesName?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", "" + MyApplication.lang);
            result = ConnectionRequests.POST(url, parameters);

            MowaziCompanyNameParser parser = new MowaziCompanyNameParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                allCompanies.clear();
                MowaziCompany c = new MowaziCompany();
                c.setSymbolAr("--" + getString(R.string.mowazi_companies) + "--");
                c.setSymbolEn("--" + getString(R.string.mowazi_companies) + "--");
                allCompanies.add(c);
                allCompanies.addAll(parser.GetCompanies());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbLastDeals.setVisibility(View.GONE);
            spinnerAdapter = new MowaziCompanySpinnerAdapter(MDealsActivity.this, allCompanies);
            spinnerAdapter.setDropDownViewResource(R.layout.need_list_spinner_item);
            spinnerCompanies.setAdapter(spinnerAdapter);

            onItemsLoadComplete();
            getDeals = new GetDeals();
            getDeals.execute("0");

        }
    }
}
