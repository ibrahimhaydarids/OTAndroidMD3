package com.ids.fixot.activities.mowazi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.LoginFingerPrintActivity;
import com.ids.fixot.adapters.mowaziAdapters.MowaziCompanySpinnerAutoCompleteAdaper;
import com.ids.fixot.adapters.mowaziAdapters.MowaziOrderTypeSpinnerAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MowaziOrdersRecyclerAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MowaziSectorSpinnerAdapter;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziOnlineOrder;
import com.ids.fixot.model.mowazi.MowaziOrderType;
import com.ids.fixot.model.mowazi.MowaziSector;
import com.ids.fixot.model.mowazi.SimpleDividerItemDecoration;
import com.ids.fixot.parser.MowaziCompanyNameParser;
import com.ids.fixot.parser.MowaziOnlineOrderParser;
import com.ids.fixot.parser.MowaziOrderTypeParser;
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

import static com.ids.fixot.adapters.mowaziAdapters.MowaziOrdersRecyclerAdapter.TYPE_ONLINE;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOnlineOrdersActivity extends Activity implements MowaziOrdersRecyclerAdapter.RecyclerViewOnItemClickListener {

    AutoCompleteTextView spinnerCompanies;
    ArrayList<MowaziCompany> arrayofspinner = new ArrayList<>();
    int footerButton;
    SharedPreferences mshared;
    SharedPreferences.Editor edit;
    LinearLayout llcompany;
    ImageView ivCompany;
    ArrayList<MowaziOnlineOrder> ordersToSort = new ArrayList<MowaziOnlineOrder>();
    private LinearLayoutManager llm;
    private RelativeLayout llOrdersLayout, top;
    private RelativeLayout main;
    private ImageButton back;
    private RecyclerView rvOrders;
    private TextView tvOrders, tvNoData;
    private MowaziOrdersRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<MowaziOnlineOrder> allOrders = new ArrayList<>();
    private int companyId = 0, check = 0, sectorId = 0, orderId = 0;
    private ArrayList<MowaziSector> allSectors = new ArrayList<MowaziSector>();
    private ArrayList<MowaziOrderType> allOrderTypes = new ArrayList<MowaziOrderType>();
    private MowaziOrderTypeSpinnerAdapter orderTypeSpinnerAdapter;
    private MowaziSectorSpinnerAdapter sectorSpinnerAdapter;
    private ArrayList<MowaziCompany> allCompanies = new ArrayList<>();
    private MowaziCompanySpinnerAutoCompleteAdaper spinnerAdapter;
    private boolean flagLoading = false, pulltoRefresh = false;
    private int visibleItemCount;
    private int totalItemCount;
    private int pastVisiblesItems;
    private Spinner spinnerSectors, spinnerTypes;
    private GetOnlineOrders getOnlineOrdersTask;
    private ProgressBar pbOrders;
    private GetCompanies getCompanies;
    private GetSectors getSectors;
    private GetOrderTypes getOrderTypes;
    private RelativeLayout searchlayoutall;
    private MowaziCompany selectedCompany = new MowaziCompany();
    private MowaziSector selectedSector = new MowaziSector();
    private MowaziOrderType selectedOrderType = new MowaziOrderType();
    private TextView tvDeals;
    private boolean started = false;

    // GetMarketStatus mGetMarketStatus;
    // GetMarketTime mGetMarketTime;
    // String marketStatusKey, marketTime;
    public MowaziOnlineOrdersActivity() {
        LocalUtils.updateConfig(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Actions.setLocal(MyApplication.lang, MowaziOnlineOrdersActivity.this);
        setContentView(R.layout.activity_mowazi_online_order);

        mshared = PreferenceManager
                .getDefaultSharedPreferences(MowaziOnlineOrdersActivity.this);
        edit = mshared.edit();

        started = true;

        findViews();

        Actions.overrideFonts(this, llOrdersLayout, true);


    }

    private void findViews() {

        try {

            companyId = getIntent().getExtras().getInt("companyId");
            Log.wtf("companyId", "" + companyId);
        } catch (Exception e) {

            e.printStackTrace();
        }
        llOrdersLayout = findViewById(R.id.llOrdersLayout);
        // drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        top = findViewById(R.id.top);

        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        searchlayoutall = findViewById(R.id.searchlayoutall);
        main = findViewById(R.id.main);
        back = findViewById(R.id.back);
        pbOrders = findViewById(R.id.pbOrders);
        tvOrders = findViewById(R.id.tvOrders);
        tvNoData = findViewById(R.id.tvNoData);
        spinnerCompanies = findViewById(R.id.spinnerCompanies);
        spinnerSectors = findViewById(R.id.spinnerSectors);
        spinnerTypes = findViewById(R.id.spinnerTypes);
        spinnerCompanies.setThreshold(1);
        arrayofspinner.addAll(MyApplication.allMowaziCompanies);

        spinnerAdapter = new MowaziCompanySpinnerAutoCompleteAdaper(
                MowaziOnlineOrdersActivity.this, R.layout.need_list_spinner_item,
                arrayofspinner);

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
                        selectedCompany = c;
                        Actions.closeKeyboard(MowaziOnlineOrdersActivity.this);

                    }

                });


        tvOrders.setText(getResources().getString(R.string.mowazi_mouwaziorders));

        llm = new LinearLayoutManager(MowaziOnlineOrdersActivity.this);
        rvOrders = findViewById(R.id.rvOrders);

        swipeContainer = findViewById(R.id.swipeContainer);

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
        // allOrders.add(new OnlineOrder());
        rvOrders.setLayoutManager(llm);
        adapter = new MowaziOrdersRecyclerAdapter(this, null, allOrders, this, TYPE_ONLINE);
        rvOrders.setAdapter(adapter);

        rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) // check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (flagLoading == false) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            flagLoading = true;
                            addOnlineOrders();
                        }
                    }
                }
            }
        });


        MowaziSector sector = new MowaziSector();
        sector.setId(0);
        sector.setName("-- " + getResources().getString(R.string.sector)
                + " --");
        allSectors.add(sector);
        sectorSpinnerAdapter = new MowaziSectorSpinnerAdapter(MowaziOnlineOrdersActivity.this, allSectors);
        spinnerSectors.setAdapter(sectorSpinnerAdapter);

        MowaziOrderType order = new MowaziOrderType();
        order.setOrderTypeId(0);
        order.setNameEn("-- " + getResources().getString(R.string.mowazi_orderType) + " --");
        order.setNameAr("-- " + getResources().getString(R.string.mowazi_orderType) + " --");
        allOrderTypes.add(order);
        orderTypeSpinnerAdapter = new MowaziOrderTypeSpinnerAdapter(MowaziOnlineOrdersActivity.this, allOrderTypes);
        spinnerTypes.setAdapter(orderTypeSpinnerAdapter);

        spinnerSectors
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parentView,
                                               View view, int position, long id) {

                        if (position != 0) {
                            MowaziSector s = sectorSpinnerAdapter
                                    .getItem(position);
                            selectedSector = s;
                        } else
                            selectedSector.setId(0);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub

                    }

                });

        spinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parentView,
                                       View view, int position, long id) {

                if (position != 0) {
                    MowaziOrderType s = orderTypeSpinnerAdapter
                            .getItem(position);
                    selectedOrderType = s;
                    Log.d("order", "" + s.getOrderTypeId());
                } else
                    selectedOrderType.setOrderTypeId(0);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        rvOrders.addItemDecoration(new SimpleDividerItemDecoration(this, 0,
                R.drawable.line_divider));

        if (getIntent().hasExtra("companyId")) {
            getOnlineOrdersTask = new GetOnlineOrders();
            getOnlineOrdersTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "" + 0);
        } else {
            getOnlineOrdersTask = new GetOnlineOrders();
            getOnlineOrdersTask.executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR, "" + companyId);
        }
    }

    public void back(View v) {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void clear(View v) {

        sectorId = 0;
        spinnerSectors.setSelection(0);
        spinnerTypes.setSelection(0);
        selectedCompany = new MowaziCompany();
        companyId = 0;
        sectorId = 0;
        orderId = 0;
        spinnerCompanies.setText("");
        refreshItems();
    }

    public void closeFilter(View v) {
        if (searchlayoutall.getVisibility() == View.VISIBLE) {
            searchlayoutall.setVisibility(View.GONE);
            companyId = 0;
            sectorId = 0;
            orderId = 0;
        } else {
            searchlayoutall.setVisibility(View.VISIBLE);
            companyId = selectedCompany.getCompanyId();
            sectorId = selectedSector.getId();
            orderId = selectedOrderType.getOrderTypeId();

        }

    }

    public void search(View v) {
        allOrders.clear();
        companyId = selectedCompany.getCompanyId();
        sectorId = selectedSector.getId();
        orderId = selectedOrderType.getOrderTypeId();
        // allOrders.add(new OnlineOrder());
        getOnlineOrdersTask = new GetOnlineOrders();
        getOnlineOrdersTask.execute("0");

    }

    private void refreshItems() {

        swipeContainer.setRefreshing(false);
        getOnlineOrdersTask = new GetOnlineOrders();
        getOnlineOrdersTask.execute("0");

        swipeContainer.setRefreshing(false);
    }

    private void onItemsLoadComplete() {
        Log.d("Recycler", "Refreshed");
        swipeContainer.setRefreshing(false);
    }

    public void addOnlineOrders() {
        if (allOrders.size() > 0) {
            getOnlineOrdersTask = new GetOnlineOrders();
            getOnlineOrdersTask.execute(String.valueOf(allOrders.get(
                    allOrders.size() - 1).getOrderId()));
            flagLoading = false;
        }
    }

    public void onItemClicked(View v, int position) {
        Log.d("Item", "Clicked");
    }

    public void sort(final String type) {
        ordersToSort.addAll(allOrders);
        allOrders.clear();
        Collections.sort(ordersToSort, new Comparator<MowaziOnlineOrder>() {

            public int compare(MowaziOnlineOrder item1, MowaziOnlineOrder item2) {

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

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        // mGetMarketStatus = new GetMarketStatus(this, marketStatusKey);
        // mGetMarketStatus.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // mGetMarketTime = new GetMarketTime(this, marketTime);
        // mGetMarketTime.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Actions.checkSession(this);

        LocalBroadcastManager.getInstance(MowaziOnlineOrdersActivity.this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        if (started)
                            recreate();
                    }
                }, new IntentFilter(MyApplication.class.getName() + "ChangedLanguage")
        );
    }

    protected class GetOnlineOrders extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbOrders.setVisibility(View.VISIBLE);

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";


            try {
                final String METHOD_NAME = "GetOnlineOrders";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // parameters to be passed to the method (parameter name, value)

                request.addProperty("sectorId", "" + sectorId);

                request.addProperty("companyId", "" + companyId);
                request.addProperty("onlyClientOrders", "false");

                request.addProperty("orderTypeId", "" + orderId);
                request.addProperty("orderStatusId", "0");
                request.addProperty("DefaultCountryId", "116");
                request.addProperty("fromDate", "");// fromDate
                request.addProperty("toDate", "");// toDate
                request.addProperty("id", params[0]);
                request.addProperty("top", "20");

                Element[] header = new Element[1];
                header[0] = new Element().createElement(NAMESPACE,
                        "SoapClassService");
                Element username = new Element().createElement(NAMESPACE,
                        "ClientID");
                username.addChild(Node.TEXT, "0");
                header[0].addChild(Node.ELEMENT, username);

                Element pass = new Element().createElement(NAMESPACE,
                        "oldToken");
                pass.addChild(
                        Node.TEXT,
                        mshared.getString("oldToken",
                                mshared.getString("oldToken", "")));
                header[0].addChild(Node.ELEMENT, pass);
                Element random = new Element().createElement(NAMESPACE,
                        "newToken");
                random.addChild(
                        Node.TEXT,
                        mshared.getString("newToken",
                                mshared.getString("newToken", "")));
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
                        .getProperty("GetOnlineOrdersResult");

                result = t.getProperty("message").toString();

                // = bank.getProperty("success").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            MowaziOnlineOrderParser parser = new MowaziOnlineOrderParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");

            try {
                allOrders.clear();
                if (pulltoRefresh) {

                    // allOrders.add(new OnlineOrder());
                    pulltoRefresh = false;
                }
                allOrders.addAll(parser.GetOrders());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("Expired")) {
                RegisterOrLoginMowazi login = new RegisterOrLoginMowazi();
                login.execute();
            } else if (result.contains("Security Issue")) {

                // MyApplication.logged = false;
                Actions.logout(MowaziOnlineOrdersActivity.this);
                Intent i = new Intent();
                i.putExtra("activity", "myorders");

                i.setClass(MowaziOnlineOrdersActivity.this, LoginFingerPrintActivity.class);

                startActivity(i);
                finish();

                edit.putBoolean("loggedMowazi", false);
                edit.commit();
            } else {

                if (!mshared.getString("OrderSortKind", "").equals("")) {
                    if (mshared.getString("OrderSortKind", "").equals("symbol"))
                        sort(mshared.getString("OrderSortType", "asce"));
                }
                adapter.notifyDataSetChanged();
                onItemsLoadComplete();
                pbOrders.setVisibility(View.GONE);

                if (allOrders.size() == 0) {
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
            getSectors.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    protected class GetCompanies extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbOrders.setVisibility(View.VISIBLE);
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
                c.setSymbolEn(getString(R.string.mowazi_companies));
                c.setSymbolAr(getString(R.string.mowazi_companies));
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
            pbOrders.setVisibility(View.GONE);
            spinnerAdapter.notifyDataSetChanged();
            onItemsLoadComplete();
            getSectors = new GetSectors();
            getSectors.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    protected class GetSectors extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbOrders.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetSectors?";

            Log.wtf("aaaaaaa", "aaaaaaaaaaa");

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");

            result = ConnectionRequests.POST(url, parameters);

            MowaziSectorParser parser = new MowaziSectorParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allSectors.clear();
                MowaziSector sector = new MowaziSector();
                sector.setId(0);
                sector.setName(getResources().getString(R.string.sector));
                allSectors.add(sector);
                allSectors.addAll(parser.GetSectors());
            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("Exception", e.getMessage());
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            sectorSpinnerAdapter.notifyDataSetChanged();
            getOrderTypes = new GetOrderTypes();
            getOrderTypes.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            pbOrders.setVisibility(View.GONE);
            onItemsLoadComplete();

        }
    }

    protected class GetOrderTypes extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbOrders.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetOrderTypes?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            //parameters.put("lang",  MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            Log.wtf("aasdaaaaaa", "aaaaaaaasdasdaaaa");

            result = ConnectionRequests.POST(url, parameters);

            MowaziOrderTypeParser parser = new MowaziOrderTypeParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                allOrderTypes.clear();
                MowaziOrderType order = new MowaziOrderType();
                order.setOrderTypeId(0);
                order.setNameEn(getResources().getString(R.string.mowazi_orderType));
                order.setNameAr(getResources().getString(R.string.mowazi_orderType));
                allOrderTypes.add(order);
                allOrderTypes.addAll(parser.GetOrderTypes());
            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("Exception", e.getMessage());
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbOrders.setVisibility(View.GONE);
            orderTypeSpinnerAdapter.notifyDataSetChanged();
            onItemsLoadComplete();

        }
    }

}
