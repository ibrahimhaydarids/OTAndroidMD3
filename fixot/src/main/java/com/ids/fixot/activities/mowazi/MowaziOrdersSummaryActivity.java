package com.ids.fixot.activities.mowazi;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.mowaziAdapters.MowaziCompanySpinnerAutoCompleteAdaper;
import com.ids.fixot.adapters.mowaziAdapters.MowaziOrderBookSummaryAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MowaziSectorSpinnerAdapter;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziOrderBook;
import com.ids.fixot.model.mowazi.MowaziSector;
import com.ids.fixot.model.mowazi.SimpleDividerItemDecoration;
import com.ids.fixot.parser.MowaziCompanyNameParser;
import com.ids.fixot.parser.MowaziOrderBookParser;
import com.ids.fixot.parser.MowaziSectorParser;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static com.ids.fixot.adapters.mowaziAdapters.MowaziOrderBookSummaryAdapter.TYPE_SUMMARY;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziOrdersSummaryActivity extends AppCompatActivity implements MowaziOrderBookSummaryAdapter.RecyclerViewOnItemClickListener {

    AutoCompleteTextView spinnerCompanies;
    ArrayList<MowaziCompany> arrayofspinner = new ArrayList<>();
    int footerButton;
    LinearLayout llcompany;
    ArrayList<MowaziOrderBook> ordersToSort = new ArrayList<>();
    ImageView ivCompany;
    SharedPreferences.Editor edit;
    SharedPreferences mshared;
    private LinearLayoutManager llm;
    private RelativeLayout llOrdersLayout, top;
    private RecyclerView rvOrders;
    private TextView tvOrders, tvNoData;
    private MowaziOrderBookSummaryAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<MowaziOrderBook> allOrders = new ArrayList<>();
    private boolean flagLoading = false, pulltoRefresh = false;
    private int visibleItemCount;
    private int totalItemCount;
    private int companyId = 0, sectorId = 0;
    private Spinner spinnerSectors;
    private ArrayList<MowaziSector> allSectors = new ArrayList<MowaziSector>();
    private MowaziCompanySpinnerAutoCompleteAdaper spinnerAdapter;
    private MowaziSectorSpinnerAdapter sectorSpinnerAdapter;
    private ArrayList<MowaziCompany> allCompanies = new ArrayList<>();
    private int pastVisiblesItems;
    private GetOrderBookSummary getOrderBookSummary;
    private ProgressBar pbOrders;
    private GetCompanies getCompanies;
    private GetSectors getSectors;
    private RelativeLayout drawer;
    private MowaziCompany selectedcompany = new MowaziCompany();
    private MowaziSector selectedsector = new MowaziSector();
    private RelativeLayout searchlayoutall;
    private TextView btFilter;
    private RelativeLayout main;
    private ImageButton back;
    private boolean started = false;

    public MowaziOrdersSummaryActivity() {
        LocalUtils.updateConfig(this);
    }

    // GetMarketTime mGetMarketTime;
    // String marketStatusKey, marketTime;
    @SuppressLint("ResourceAsColor")
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_mowazi_order_book);

        mshared = PreferenceManager.getDefaultSharedPreferences(MowaziOrdersSummaryActivity.this);
        edit = mshared.edit();

        findViews();

        InitializeHeader();

        Actions.overrideFonts(this, llOrdersLayout, true);
    }

    private void findViews() {
        llOrdersLayout = (RelativeLayout) findViewById(R.id.llOrdersLayout);
        top = (RelativeLayout) findViewById(R.id.top);

        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        pbOrders = (ProgressBar) findViewById(R.id.pbOrders);
        tvOrders = (TextView) findViewById(R.id.tvOrders);
        Intent intent = getIntent();
        if (intent.hasExtra("companyId")) { // try on 232 al razi company
            companyId = getIntent().getExtras().getInt("companyId");
            tvOrders.setText(getResources().getString(R.string.orders) + "-"
                    + getIntent().getExtras().getString("companyname"));
        }

        btFilter = (TextView) findViewById(R.id.tvFilter);
        tvNoData = (TextView) findViewById(R.id.tvNoData);

        started = true;
        searchlayoutall = (RelativeLayout) findViewById(R.id.searchlayoutall);
        main = (RelativeLayout) findViewById(R.id.main);
        back = (ImageButton) findViewById(R.id.back);

        spinnerCompanies = (AutoCompleteTextView) findViewById(R.id.spinnerCompanies);
        arrayofspinner.addAll(MyApplication.allMowaziCompanies);
        spinnerAdapter = new MowaziCompanySpinnerAutoCompleteAdaper(MowaziOrdersSummaryActivity.this, R.layout.need_list_spinner_item, arrayofspinner);

        spinnerCompanies.setAdapter(spinnerAdapter);
        spinnerCompanies.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View arg0) {
                if (spinnerCompanies.getAdapter().getCount() == 0) {

                    arrayofspinner.addAll(MyApplication.allMowaziCompanies);
                    spinnerAdapter.notifyDataSetChanged();
                }
                spinnerCompanies.showDropDown();
            }
        });
        spinnerCompanies
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        MowaziCompany c = spinnerAdapter.getItem(position);
                        selectedcompany = c;
                        Actions.closeKeyboard(MowaziOrdersSummaryActivity.this);

                    }

                });
        spinnerSectors = (Spinner) findViewById(R.id.spinnerSectors);
        MowaziSector sector = new MowaziSector();
        sector.setId(0);
        sector.setName("-- " + getResources().getString(R.string.sector)
                + " --");
        allSectors.add(sector);
        sectorSpinnerAdapter = new MowaziSectorSpinnerAdapter(MowaziOrdersSummaryActivity.this, allSectors);
        spinnerSectors.setAdapter(sectorSpinnerAdapter);
        spinnerSectors.setSelection(0);

        tvOrders.setTypeface(MyApplication.lang == MyApplication.ENGLISH ? MyApplication.opensansbold : MyApplication.droidbold);
        llm = new LinearLayoutManager(MowaziOrdersSummaryActivity.this);
        rvOrders = (RecyclerView) findViewById(R.id.rvOrders);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeColors(0, 0, 0, 0);
        swipeContainer.setRefreshing(false);
        swipeContainer
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    public void onRefresh() {

                        swipeContainer.setRefreshing(false);
                        refreshItems();
                        pulltoRefresh = true;

                        swipeContainer.setRefreshing(false);
                    }
                });

        swipeContainer.setRefreshing(false);

        rvOrders.setLayoutManager(llm);

        // allOrders.add(new OrderBook());
        adapter = new MowaziOrderBookSummaryAdapter(this, allOrders, null, this, TYPE_SUMMARY);
        rvOrders.setAdapter(adapter);
        rvOrders.addItemDecoration(new SimpleDividerItemDecoration(this, 0,
                R.drawable.line_divider));
        getCompanies = new GetCompanies();
        getCompanies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        getOrderBookSummary = new GetOrderBookSummary();
        getOrderBookSummary.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");

        spinnerSectors
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parentView,
                                               View view, int position, long id) {

                        if (position != 0) {
                            MowaziSector sector = sectorSpinnerAdapter
                                    .getItem(position);
                            selectedsector = sector;
                        } else
                            selectedsector.setId(0);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }
                });

    }

    public void InitializeHeader() {

        llcompany = (LinearLayout) findViewById(R.id.llcompany);

        ivCompany = (ImageView) findViewById(R.id.ivcompany);

        llcompany.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (ivCompany.getTag().equals("desc")) {
                    ivCompany.setImageDrawable(ContextCompat.getDrawable(
                            MowaziOrdersSummaryActivity.this, R.drawable.updown));
                    ivCompany.setRotation(180);

                    ivCompany.setTag("asce");
                    sort("asce");
                    edit.putString("OrderSummarySortKind", "symbol");
                    edit.putString("OrderSummarySortType", "asce");
                    edit.commit();
                } else {
                    ivCompany.setRotation(ivCompany.getRotation() - 180);
                    ivCompany.setImageDrawable(ContextCompat.getDrawable(
                            MowaziOrdersSummaryActivity.this, R.drawable.updown));

                    ivCompany.setTag("desc");
                    sort("desc");
                    edit.putString("OrderSummarySortKind", "symbol");
                    edit.putString("OrderSummarySortType", "desc");
                    edit.commit();
                }

            }
        });
        setImageForSorting();
    }

    public void setImageForSorting() {
        if (!mshared.getString("OrderSummarySortKind", "").equals("")) {
            if (mshared.getString("OrderSummarySortType", "").equals("asce")) {

                ivCompany.setImageDrawable(ContextCompat.getDrawable(
                        MowaziOrdersSummaryActivity.this, R.drawable.updown));

                ivCompany.setRotation(180);
            } else {
                ivCompany.setImageDrawable(ContextCompat.getDrawable(
                        MowaziOrdersSummaryActivity.this, R.drawable.updown));
            }
        }
    }

    public void sort(final String type) {
        ordersToSort.addAll(allOrders);
        allOrders.clear();
        Collections.sort(ordersToSort, new Comparator<MowaziOrderBook>() {

            public int compare(MowaziOrderBook item1, MowaziOrderBook item2) {

                if (type.equals("asce")) {
                    if (MyApplication.lang == MyApplication.ENGLISH) {
                        if (item2.getCompany().getSymbolEn() != null)
                            return item1
                                    .getCompany()
                                    .getSymbolEn()
                                    .compareTo(item2.getCompany().getSymbolEn());
                        else
                            return 1;
                    } else {
                        if (item2.getCompany().getSymbolAr() != null)
                            return item1
                                    .getCompany()
                                    .getSymbolAr()
                                    .compareTo(item2.getCompany().getSymbolAr());
                        else
                            return 1;
                    }
                } else {

                    if (MyApplication.lang == MyApplication.ENGLISH) {
                        if (item2.getCompany().getSymbolEn() != null)
                            return item2
                                    .getCompany()
                                    .getSymbolEn()
                                    .compareTo(item1.getCompany().getSymbolEn());
                        else
                            return 1;
                    } else {
                        if (item2.getCompany().getSymbolAr() != null)
                            return item2
                                    .getCompany()
                                    .getSymbolAr()
                                    .compareTo(item1.getCompany().getSymbolAr());
                        else
                            return 1;
                    }
                }
            }

        });
        allOrders.addAll(ordersToSort);
        adapter.notifyDataSetChanged();
        ordersToSort.clear();

    }

    public void clear(View v) {
        sectorId = 0;
        companyId = 0;
        spinnerSectors.setSelection(0);
        selectedcompany = new MowaziCompany();
        spinnerCompanies.setSelection(0);
        spinnerCompanies.setText("");
        Intent intent = getIntent();
        if (intent.hasExtra("companyId")) // try on 232 al razi company
            companyId = getIntent().getExtras().getInt("companyId");
        else
            companyId = 0;
        refreshItems();
    }

    private void refreshItems() {

        swipeContainer.setRefreshing(false);

        getOrderBookSummary = new GetOrderBookSummary();
        getOrderBookSummary.execute("" + companyId);

        swipeContainer.setRefreshing(false);
    }

    public void search(View v) {
        allOrders.clear();
        // allOrders.add(new OrderBook());
        companyId = selectedcompany.getCompanyId();
        sectorId = selectedsector.getId();

        getOrderBookSummary = new GetOrderBookSummary();
        getOrderBookSummary.execute("0");
    }

    public void closeFilter(View v) {
        if (searchlayoutall.getVisibility() == View.VISIBLE) {
            searchlayoutall.setVisibility(View.GONE);
            sectorId = 0;
            Intent intent = getIntent();
            if (intent.hasExtra("companyId")) // try on 232 al razi company
                companyId = getIntent().getExtras().getInt("companyId");
            else
                companyId = 0;
        } else {
            searchlayoutall.setVisibility(View.VISIBLE);
            companyId = selectedcompany.getCompanyId();
            sectorId = selectedsector.getId();
        }

    }

    private void onItemsLoadComplete() {
        Log.d("Recycler", "Refreshed");
        swipeContainer.setRefreshing(false);
    }

    public void addOrders() {
        if (allOrders.size() > 0) {
            getOrderBookSummary = new GetOrderBookSummary();
            getOrderBookSummary.execute(String.valueOf(allOrders.get(
                    allOrders.size() - 1).getId()));
            flagLoading = false;
        }
    }

    public void onItemClicked(View v, int position) {

        Intent i = new Intent();
        Bundle b = new Bundle();

        b.putString("company", MyApplication.lang == MyApplication.ARABIC ? allOrders.get(position).getCompany().getSymbolAr() : allOrders.get(position).getCompany().getSymbolEn());
        b.putInt("bidquantity", allOrders.get(position).getBidQuantity());

        b.putDouble("bidprice", allOrders.get(position).getBidPrice());
        b.putInt("askquantity", allOrders.get(position).getAskquantity());
        b.putDouble("askprice", allOrders.get(position).getAskPrice());

        b.putInt("bidcount", allOrders.get(position).getBidCount());
        b.putInt("askcount", allOrders.get(position).getAskCount());

        i.putExtras(b);
        i.setClass(getApplicationContext(), MowaziOrderDetailPopupActivity.class);

        startActivity(i);

    }

    public void back(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        LocalBroadcastManager.getInstance(MowaziOrdersSummaryActivity.this).registerReceiver(
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

    protected class GetOrderBookSummary extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbOrders.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetOrderBookSummary?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("sectorId", "" + sectorId);
            parameters.put("companyId", companyId + "");
            parameters.put("countryId", "116");
            parameters.put("id", params[0]);
            parameters.put("top", "100");

            result = ConnectionRequests.POST(url, parameters);
            MowaziOrderBookParser parser = new MowaziOrderBookParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allOrders.clear();
                if (pulltoRefresh) {

                    // allOrders.add(new OrderBook());
                    pulltoRefresh = false;
                }
                allOrders.addAll(parser.getOrderBooks());
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
            if (!mshared.getString("OrderSummarySortKind", "").equals("")) {
                if (mshared.getString("OrderSummarySortKind", "").equals(
                        "symbol"))
                    sort(mshared.getString("OrderSummarySortKind", "asce"));
            }
            pbOrders.setVisibility(View.GONE);
            onItemsLoadComplete();

            if (allOrders.size() == 0) {
                tvNoData.setVisibility(View.VISIBLE);
                swipeContainer.setVisibility(View.GONE);
            } else {
                tvNoData.setVisibility(View.GONE);
                swipeContainer.setVisibility(View.VISIBLE);
            }

            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    protected class GetCompanies extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetCompaniesName?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", "" + MyApplication.lang);

            result = ConnectionRequests.POST(url, parameters);

            MowaziCompanyNameParser parser = new MowaziCompanyNameParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allCompanies.clear();
                MowaziCompany c = new MowaziCompany();
                c.setSymbolEn("-- " + getString(R.string.mowazi_company) + " --");
                c.setSymbolAr("-- " + getString(R.string.mowazi_company) + " --");
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
            // pbOrders.setVisibility(View.GONE);
            spinnerAdapter.notifyDataSetChanged();

            getSectors = new GetSectors();
            getSectors.execute();
            pbOrders.setVisibility(View.GONE);

        }
    }

    protected class GetSectors extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetSectors?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");

            result = ConnectionRequests.POST(url, parameters);

            MowaziSectorParser parser = new MowaziSectorParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allSectors.clear();
                MowaziSector sector = new MowaziSector();
                sector.setId(0);
                sector.setName("-- " + getResources().getString(R.string.sector) + " --");
                allSectors.add(sector);
                allSectors.addAll(parser.GetSectors());
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
            pbOrders.setVisibility(View.GONE);
            sectorSpinnerAdapter.notifyDataSetChanged();

        }
    }

}
