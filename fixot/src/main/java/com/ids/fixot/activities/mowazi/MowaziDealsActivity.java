package com.ids.fixot.activities.mowazi;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.LoginFingerPrintActivity;
import com.ids.fixot.adapters.mowaziAdapters.AlMowaziDealsRecyclerAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MowaziCompanySpinnerAutoCompleteAdaper;
import com.ids.fixot.adapters.mowaziAdapters.MowaziSectorSpinnerAdapter;
import com.ids.fixot.model.mowazi.AlmowaziDeal;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziSector;
import com.ids.fixot.model.mowazi.SimpleDividerItemDecoration;
import com.ids.fixot.parser.AlmowaziDealParser;
import com.ids.fixot.parser.MowaziCompanyNameParser;
import com.ids.fixot.parser.MowaziSectorParser;

import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziDealsActivity extends Activity implements AlMowaziDealsRecyclerAdapter.RecyclerViewOnItemClickListener {

    GetSectors getSectors;
    Spinner spinnerSectors;
    MowaziSector selectedsector = new MowaziSector();
    MowaziSectorSpinnerAdapter sectorSpinnerAdapter;
    SharedPreferences mshared;
    SharedPreferences.Editor edit;
    LinearLayout llcompany;
    ImageView ivCompany;
    int footerButton;
    ArrayList<MowaziCompany> arrayofspinner = new ArrayList<MowaziCompany>();
    private LinearLayoutManager llm;
    private RelativeLayout llDealsLayout, top, rlFromDate, rlToDate;
    private RecyclerView rvDeals;
    private TextView tvDeals, tvFromDate, tvToDate;
    private String fromDate = "", toDate = "", returnedDatefom = "",
            returnedDateto = "";
    private AutoCompleteTextView spinnerCompanies;
    private AlMowaziDealsRecyclerAdapter adapter;
    private TextView tvFilter, tvNoData;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<MowaziCompany> allCompanies = new ArrayList<>();
    private ArrayList<AlmowaziDeal> allDeals = new ArrayList<AlmowaziDeal>();
    private ArrayList<AlmowaziDeal> dealToSort = new ArrayList<AlmowaziDeal>();
    private boolean flagLoading = false, pulltoRefresh = false, loadMore = false;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisibleItems;
    private int mYear, mMonth, mDay;
    private ProgressBar pbLastDeals;
    private GetDeals getDeals;
    private GetCompanies getCompanies;
    private ArrayList<MowaziSector> allSectors = new ArrayList<>();
    private MowaziCompanySpinnerAutoCompleteAdaper spinnerAdapter;
    private RelativeLayout searchlayoutall;
    private int sectorId = 0;
    private int companyId = 0;
    private MowaziCompany selectedcompany = new MowaziCompany();
    private RelativeLayout main;
    private ImageButton back;
    private DrawerLayout drawer;

    public MowaziDealsActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Actions.setLocal(MyApplication.lang, this);

        setContentView(R.layout.activity_almowazi_deals);

        mshared = PreferenceManager.getDefaultSharedPreferences(MowaziDealsActivity.this);
        edit = mshared.edit();

        findViews();
        InitializeHeader();

        Actions.overrideFonts(this, llDealsLayout, true);
    }

    private void findViews() {

        llDealsLayout = findViewById(R.id.llDealsLayout);
        top = findViewById(R.id.top);

        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        main = findViewById(R.id.main);

        back = findViewById(R.id.back);
        tvNoData = findViewById(R.id.tvNoData);
        rlFromDate = findViewById(R.id.rlFromDate);
        rlToDate = findViewById(R.id.rlToDate);

        pbLastDeals = findViewById(R.id.pbLastDeals);
        tvDeals = findViewById(R.id.tvDeals);
        tvFromDate = findViewById(R.id.tvFromDate);

        tvToDate = findViewById(R.id.tvToDate);
        searchlayoutall = findViewById(R.id.searchlayoutall);
        spinnerCompanies = findViewById(R.id.spinnerCompanies);

        spinnerCompanies.setThreshold(1);
        arrayofspinner.addAll(MyApplication.allMowaziCompanies);
        spinnerAdapter = new MowaziCompanySpinnerAutoCompleteAdaper(MowaziDealsActivity.this, R.layout.need_list_spinner_item, arrayofspinner);

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
        spinnerCompanies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                MowaziCompany c = spinnerAdapter.getItem(position);
                selectedcompany = c;
                Actions.closeKeyboard(MowaziDealsActivity.this);
            }

        });

        llm = new LinearLayoutManager(MowaziDealsActivity.this);
        rvDeals = findViewById(R.id.rvDeals);
        swipeContainer = findViewById(R.id.swipeContainer);

        swipeContainer.setColorSchemeColors(0, 0, 0, 0);
        swipeContainer.setProgressBackgroundColor(android.R.color.transparent);
        spinnerSectors = findViewById(R.id.spinnerSectors);
        MowaziSector sector = new MowaziSector();
        sector.setId(0);
        sector.setName("-- " + getResources().getString(R.string.sector)
                + " --");
        allSectors.add(sector);
        sectorSpinnerAdapter = new MowaziSectorSpinnerAdapter(MowaziDealsActivity.this, allSectors);
        spinnerSectors.setAdapter(sectorSpinnerAdapter);
        spinnerSectors.setSelection(0);

        swipeContainer.setRefreshing(false);
        swipeContainer
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    public void onRefresh() {

                        swipeContainer.setRefreshing(false);
                        pulltoRefresh = true;
                        refreshItems();

                        swipeContainer.setRefreshing(false);

                    }
                });

        swipeContainer.setRefreshing(false);

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
        // allDeals.add(new AlmowaziDeal());
        adapter = new AlMowaziDealsRecyclerAdapter(this, allDeals, this);
        rvDeals.setAdapter(adapter);
        rvDeals.addItemDecoration(new SimpleDividerItemDecoration(this, 0,
                R.drawable.line_divider));

        // getCompanies = new GetCompanies();
        // getCompanies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        getDeals = new GetDeals();
        getDeals.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");

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

        llcompany = findViewById(R.id.llcompany);

        ivCompany = findViewById(R.id.ivcompany);

        llcompany.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (ivCompany.getTag().equals("desc")) {
                    ivCompany.setRotation(180);
                    ivCompany.setImageDrawable(ContextCompat.getDrawable(
                            MowaziDealsActivity.this, R.drawable.updown));

                    ivCompany.setTag("asce");
                    sort("asce");
                    edit.putString("AlmowaziDealsSortKind", "symbol");
                    edit.putString("AlmowaziDealsSortType", "asce");
                    edit.commit();
                } else {
                    ivCompany.setRotation(ivCompany.getRotation() - 180);
                    ivCompany.setImageDrawable(ContextCompat.getDrawable(
                            MowaziDealsActivity.this, R.drawable.updown));

                    ivCompany.setTag("desc");
                    sort("desc");
                    edit.putString("AlmowaziDealsSortKind", "symbol");
                    edit.putString("AlmowaziDealsSortType", "desc");
                    edit.commit();
                }

            }
        });
        setImageForSorting();
    }

    public void setImageForSorting() {
        if (!mshared.getString("AlmowaziDealsSortKind", "").equals("")) {
            if (mshared.getString("AlmowaziDealsSortType", "").equals("asce")) {

                ivCompany.setImageDrawable(ContextCompat.getDrawable(
                        MowaziDealsActivity.this, R.drawable.updown));

                ivCompany.setRotation(180);
            } else {
                ivCompany.setImageDrawable(ContextCompat.getDrawable(
                        MowaziDealsActivity.this, R.drawable.updown));
            }
        }
    }

    public void sort(final String type) {
        dealToSort.addAll(allDeals);
        allDeals.clear();
        Collections.sort(dealToSort, new Comparator<AlmowaziDeal>() {

            public int compare(AlmowaziDeal item1, AlmowaziDeal item2) {

                if (type.equals("asce")) {
                    if (MyApplication.lang == MyApplication.ENGLISH) {
                        if (item2.getSymbolEn() != null)
                            return item1.getSymbolEn().compareTo(
                                    item2.getSymbolEn());
                        else
                            return 1;
                    } else {
                        if (item2.getSymbolAr() != null)
                            return item1.getSymbolAr().compareTo(
                                    item2.getSymbolAr());
                        else
                            return 1;
                    }
                } else {

                    if (MyApplication.lang == MyApplication.ENGLISH) {
                        if (item2.getSymbolEn() != null)
                            return item2.getSymbolEn().compareTo(
                                    item1.getSymbolEn());
                        else
                            return 1;
                    } else {
                        if (item2.getSymbolAr() != null)
                            return item2.getSymbolAr().compareTo(
                                    item1.getSymbolAr());
                        else
                            return 1;
                    }
                }
            }

        });
        allDeals.addAll(dealToSort);
        adapter.notifyDataSetChanged();
        dealToSort.clear();

    }


    public void clear(View v) {
        fromDate = "";
        toDate = "";
        tvFromDate.setText(getString(R.string.mowazi_from));
        tvToDate.setText(getString(R.string.mowazi_to));
        spinnerCompanies.setSelection(0);
        spinnerSectors.setSelection(0);
        spinnerCompanies.setText("");
        sectorId = 0;

        Intent intent = getIntent();
        if (intent.hasExtra("companyId")) // try on 232 al razi company
            companyId = getIntent().getExtras().getInt("companyId");
        else
            companyId = 0;
        refreshItems();
    }

    public void back(View v) {
        finish();
    }

    public void showDrawer(View v) {
        if (MyApplication.lang == MyApplication.ENGLISH) {
            drawer.openDrawer(Gravity.LEFT);
        } else {
            drawer.openDrawer(Gravity.RIGHT);
        }
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
        sectorId = selectedsector.getId();
        // allDeals.add(new AlmowaziDeal());
        getDeals = new GetDeals();
        getDeals.execute("0");
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

        swipeContainer.setRefreshing(false);
        getDeals = new GetDeals();
        getDeals.execute("0");

        swipeContainer.setRefreshing(false);
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
            loadMore = true;
            getDeals.execute(String.valueOf(allDeals.get(allDeals.size() - 1)
                    .getDealId()));
            flagLoading = false;
        }
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

    protected class GetDeals extends AsyncTask<String, Void, String> {
        String param0 = "0";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if (!pulltoRefresh)
                pbLastDeals.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";
            param0 = params[0];
            try {
                final String METHOD_NAME = "GetDealSummary";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // parameters to be passed to the method (parameter name, value)
                // request.addProperty("ClientID", ""+MyApplication.clientId);
                // parameters.put("ClientID", "0");
                request.addProperty("countryId", "116");

                request.addProperty("lang", MyApplication.lang);
                request.addProperty("CompanyId", companyId + "");
                request.addProperty("sectorId", sectorId + "");
                request.addProperty("fromDate", fromDate);
                request.addProperty("toDate", toDate);
                request.addProperty("id", params[0]);
                request.addProperty("top", "20");
                Element[] header = new Element[1];
                header[0] = new Element().createElement(NAMESPACE,
                        "SoapClassService");

                Element username = new Element().createElement(NAMESPACE,
                        "ClientID");
                // username.addChild(Node.TEXT, "0");// +
                // MyApplication.MowaziClientID);
                username.addChild(Node.TEXT, "0");// +
                // MyApplication.MowaziClientID);
                Log.d("iddd", "" + MyApplication.mowaziClientID);

                header[0].addChild(Node.ELEMENT, username);
                Element pass = new Element().createElement(NAMESPACE,
                        "oldToken");
                pass.addChild(Node.TEXT, mshared.getString("oldToken", ""));

                header[0].addChild(Node.ELEMENT, pass);
                Element random = new Element().createElement(NAMESPACE,
                        "newToken");
                random.addChild(Node.TEXT, mshared.getString("newToken", ""));

                header[0].addChild(Node.ELEMENT, random);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;

                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res
                        .getProperty("GetDealSummaryResult");

                result = t.getProperty("message").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            AlmowaziDealParser parser = new AlmowaziDealParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allDeals.clear();
                if (pulltoRefresh) {

                    // if(!loadMore)
                    // allDeals.add(new AlmowaziDeal());
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
            Log.wtf("result", result);
            if (result.contains("Expired")) {
                RegisterOrLoginMowazi login = new RegisterOrLoginMowazi();
                login.execute();
            } else if (result.contains("Security Issue")) {

                // MyApplication.logged = false;
                Actions.logout(MowaziDealsActivity.this);
                Intent i = new Intent();
                i.putExtra("activity", "deals");

                i.setClass(MowaziDealsActivity.this, LoginFingerPrintActivity.class);

                startActivity(i);
                finish();
                edit.putBoolean("loggedMowazi", false);
                edit.commit();
            } else {
                if (!mshared.getString("AlmowaziDealsSortKind", "").equals("")) {
                    if (mshared.getString("AlmowaziDealsSortKind", "").equals(
                            "symbol"))
                        sort(mshared.getString("AlmowaziDealsSortType", "asce"));
                }
                adapter.notifyDataSetChanged();
                onItemsLoadComplete();
                pbLastDeals.setVisibility(View.GONE);

                Log.d("LastDeals", "" + allDeals.size());

                if (allDeals.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    swipeContainer.setVisibility(View.VISIBLE);
                }
            }

            getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);


            getSectors = new GetSectors();
            getSectors.execute();
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

            onItemsLoadComplete();

            getSectors = new GetSectors();
            getSectors.execute();

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

            MowaziSectorParser parser = new MowaziSectorParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                allSectors.clear();
                MowaziSector sector = new MowaziSector();
                sector.setId(0);
                sector.setName("-- "
                        + getResources().getString(R.string.sector) + " --");
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
            pbLastDeals.setVisibility(View.GONE);
            sectorSpinnerAdapter.notifyDataSetChanged();

        }
    }

    public class RegisterOrLoginMowazi extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String result = "";

            try {
                final String METHOD_NAME = "RegisterOrLoginFixOT";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // parameters to be passed to the method (parameter name, value)
                Element[] header = new Element[1];

                header[0] = new Element().createElement(NAMESPACE, "FixOtHeader");

                Element username = new Element().createElement(NAMESPACE, "Username");
                username.addChild(Node.TEXT, MyApplication.currentUser.getUsername());
                header[0].addChild(Node.ELEMENT, username);

                Element investorNumber = new Element().createElement(NAMESPACE, "InvestorNumber");
                investorNumber.addChild(Node.TEXT, String.valueOf(MyApplication.currentUser.getInvestorId()));
                header[0].addChild(Node.ELEMENT, investorNumber);

                Element name = new Element().createElement(NAMESPACE, "Name");
                name.addChild(Node.TEXT, MyApplication.currentUser.getUsername());
                header[0].addChild(Node.ELEMENT, name);

                Element random = new Element().createElement(NAMESPACE, "Password");
                random.addChild(Node.TEXT, Actions.MD5(Actions.getRandom()));
                header[0].addChild(Node.ELEMENT, random);

                Element brokerid = new Element().createElement(NAMESPACE, "BrokerID");
                brokerid.addChild(Node.TEXT, MyApplication.mowaziBrokerId);
                header[0].addChild(Node.ELEMENT, brokerid);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;

                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res.getProperty("RegisterOrLoginFixOTResult");

                result = t.getProperty("success").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (result.contains("true")) {

                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    try {

                        MyApplication.mowaziClientID = Integer.parseInt(result.split(",")[0]);
                        MyApplication.editor.putString("lastdate", formattedDate).apply();
                        MyApplication.editor.putInt("clientId", MyApplication.mowaziClientID).apply();

                        if (MyApplication.mshared.getString("oldToken", "").equals("") && MyApplication.mshared.getString("newToken", "").equals("")) {

                            MyApplication.editor.putString("oldToken", result.split(",")[2]);
                            MyApplication.editor.putString("newToken", result.split(",")[2]);
                            MyApplication.editor.putString("expiry", result.split(",")[3]);
                            MyApplication.editor.apply();
                        } else {

                            MyApplication.editor.putString("oldToken", MyApplication.mshared.getString("newToken", ""));
                            MyApplication.editor.putString("newToken", result.split(",")[2]);
                            MyApplication.editor.putString("expiry", result.split(",")[3]);
                            MyApplication.editor.apply();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    MyApplication.editor.apply();
                } else
                    Toast.makeText(getApplicationContext(), "Error when register in mowazi", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
