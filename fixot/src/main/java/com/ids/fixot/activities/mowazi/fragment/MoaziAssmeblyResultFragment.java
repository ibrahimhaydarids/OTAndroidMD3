package com.ids.fixot.activities.mowazi.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.mowaziAdapters.MoaziAssembliesRecyclerAdapter;
import com.ids.fixot.adapters.mowaziAdapters.MoaziCompanySpinnerAutoCompleteAdaper;
import com.ids.fixot.adapters.mowaziAdapters.MoaziYearSpinnerAdapter;
import com.ids.fixot.model.mowazi.MoaziAssemblyResult;
import com.ids.fixot.model.mowazi.MoaziCompany;
import com.ids.fixot.model.mowazi.SimpleDividerItemDecoration;
import com.ids.fixot.parser.MoaziAssemblyResultParser;

import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;



/**
 * Created by dev on 6/14/2016.
 */

public class MoaziAssmeblyResultFragment extends Fragment implements MoaziAssembliesRecyclerAdapter.RecyclerViewOnItemClickListener {

    private RecyclerView rvResultAssembly;
    private MoaziAssembliesRecyclerAdapter adapter;
    private LinearLayoutManager llm;
    private SwipeRefreshLayout swipeContainer;
    private GetAssembliesResultTask getAssembliesResults;
    private ProgressBar pbLoading;
    private ArrayList<MoaziAssemblyResult> allAssemblies = new ArrayList<MoaziAssemblyResult>();
    private boolean flagLoading = false, pullToRefresh = false;
    private int visibleItemCount;
    private int totalItemCount, companyId = 0;
    private int pastVisibleItems;
    private Spinner spinnerYears;
    AutoCompleteTextView spinnerCompanies;
    private MoaziCompanySpinnerAutoCompleteAdaper spinnerAdapter;
    private MoaziYearSpinnerAdapter yearsSpinnerAdapter;
    private TextView btFilter, btClear, tvNoData, tv;
    private String year = "0", y;
    private ImageView ivFilter;
    private RelativeLayout searchlayoutall;
    private MoaziCompany selectedcompany = new MoaziCompany();
    boolean loadmore = false;
    SharedPreferences mshared;
    SharedPreferences.Editor edit;
    boolean visible = false;
    Activity activity;
    ArrayList<MoaziCompany> arrayofspinner = new ArrayList<MoaziCompany>();

    /*private ArrayList<MoaziCompany> allCompanies = new ArrayList<MoaziCompany>();
    private ArrayList<String> allYears = new ArrayList<String>();
    private GetCompanies getCompanies;
    private GetYears getYears;*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_moazi_result_assembly, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Actions.setViewResizing(getActivity());
        mshared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        edit = mshared.edit();
        rvResultAssembly = view.findViewById(R.id.rvResultAssembly);
        pbLoading = view.findViewById(R.id.pbLoading);
        tvNoData = view.findViewById(R.id.tvNoData);
        btFilter = view.findViewById(R.id.btFilter);
        btClear = view.findViewById(R.id.btClear);
        tv = getActivity().findViewById(R.id.tvAssemblies);
        if (MyApplication.lang==MyApplication.ENGLISH)
            tv.setTypeface(MyApplication.opensansbold);
        else
            tv.setTypeface(MyApplication.droidbold);
        ivFilter = view.findViewById(R.id.ivFilter);

        spinnerCompanies = view.findViewById(R.id.spinnerCompanies);
        searchlayoutall = view.findViewById(R.id.searchlayoutall);

        /*MoaziCompany c = new MoaziCompany();
        c.setSymbolAr("-- " + getString(R.string.company) + " --");
        c.setSymbolEn("-- " + getString(R.string.company) + " --");
        allCompanies.add(c);*/
   //     spinnerCompanies = (AutoCompleteTextView) findViewById(R.id.spinnerCompanies);
        arrayofspinner.addAll(MyApplication.allCompanies);
        spinnerCompanies.setThreshold(1);
        spinnerAdapter = new MoaziCompanySpinnerAutoCompleteAdaper(getActivity(), R.layout.need_list_spinner_item,arrayofspinner);

        spinnerCompanies.setAdapter(spinnerAdapter);
        spinnerCompanies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View arg0) {
                spinnerCompanies.showDropDown();
            }
        });


        spinnerYears = view.findViewById(R.id.spinnerYears);
        //allYears.add("--" + getResources().getString(R.string.year) + "--");
        yearsSpinnerAdapter = new MoaziYearSpinnerAdapter(getActivity(), MyApplication.allYears);
        spinnerYears.setAdapter(yearsSpinnerAdapter);
        spinnerYears.setSelection(0);

        Log.d("allAssemblies", "" + allAssemblies.size());
        adapter = new MoaziAssembliesRecyclerAdapter(getActivity(), null, allAssemblies, this, MoaziAssembliesRecyclerAdapter.TYPE_RESULT);
        rvResultAssembly.setAdapter(adapter);
        llm = new LinearLayoutManager(getActivity());
        rvResultAssembly.setLayoutManager(llm);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                refreshItems();
                pullToRefresh = true;
                swipeContainer.setRefreshing(false);
            }
        });
        swipeContainer.setRefreshing(false);
        rvResultAssembly.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisibleItems = llm.findFirstVisibleItemPosition();

                    if (flagLoading == false) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            flagLoading = true;
                            additems();
                        }
                    }
                }
            }
        });


        spinnerCompanies.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //  if (position != 0) {
                MoaziCompany c = spinnerAdapter.getItem(position);
                selectedcompany = c;
                Actions.hideKeyboard(getActivity());
                //} else {
                //     selectedcompany.setCompanyId(0);
                // }
            }


        });

        spinnerYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int position, long id) {
                if (position != 0) {
                    y = yearsSpinnerAdapter.getItem(position);
                    year = y;
                } else
                    year = "0";
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                year = "0";
            }

        });

        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear(view);
            }
        });

        btFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(view);
            }
        });

        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter(view);
            }
        });

        rvResultAssembly.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 1, R.drawable.line_divider));

        pbLoading.setVisibility(View.VISIBLE);




    }

    public void clear(View v) {

        spinnerCompanies.setSelection(0);
        spinnerYears.setSelection(0);
        allAssemblies.clear();
        companyId=0;
        year="0";
        refreshItems();
    }

    public void back(View v) {
        getActivity().finish();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mshared = PreferenceManager.getDefaultSharedPreferences(context);
        edit = mshared.edit();
    }

    public void closeFilter(View v) {
        if (searchlayoutall.getVisibility() == View.VISIBLE) {
            searchlayoutall.setVisibility(View.GONE);
            companyId = 0;
            year = "0";
        } else {
            searchlayoutall.setVisibility(View.VISIBLE);
            companyId = selectedcompany.getCompanyId();
            //year = y;
        }
        Log.wtf("companyId", "" + companyId);
        Log.wtf("year", "" + year);

    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        mshared = PreferenceManager.getDefaultSharedPreferences(getActivity());
        edit = mshared.edit();
    }

    public void search(View v) {
        if (visible) {
            if (Actions.expired(mshared)) {
                LoginAsynck login = new LoginAsynck();
                login.execute("0");
            } else {
                allAssemblies.clear();
                companyId = selectedcompany.getCompanyId();

                getAssembliesResults = new GetAssembliesResultTask();
                getAssembliesResults.execute("0");
            }
        } else {
            allAssemblies.clear();
            companyId = selectedcompany.getCompanyId();

            getAssembliesResults = new GetAssembliesResultTask();
            getAssembliesResults.execute("0");
        }
    }

    public void additems() {
        if (visible) {
            if (Actions.expired(mshared)) {
                LoginAsynck login = new LoginAsynck();
                login.execute(String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId()));
            } else {
                if (allAssemblies.size() > 0) {
                    GetAssembliesResultTask get = new GetAssembliesResultTask();
                    get.execute(String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId()));
                    flagLoading = false;
                    loadmore = true;
                }
            }
        } else {
            if (allAssemblies.size() > 0) {
                GetAssembliesResultTask get = new GetAssembliesResultTask();
                get.execute(String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId()));
                flagLoading = false;
                loadmore = true;
            }
        }
    }

    private void onItemsLoadComplete() {

        Log.d("Recycler", "Refreshed");
        swipeContainer.setRefreshing(false);
    }

    public void refreshItems() {
        swipeContainer.setRefreshing(false);
        if (visible) {
            if (Actions.expired(mshared)) {
                LoginAsynck login = new LoginAsynck();
                login.execute("0");
            } else {
                getAssembliesResults = new GetAssembliesResultTask();
                getAssembliesResults.execute("0");
            }
        } else {
            getAssembliesResults = new GetAssembliesResultTask();
            getAssembliesResults.execute("0");
        }
        swipeContainer.setRefreshing(false);
        Actions.hideKeyboard(getActivity());
    }

    @Override
    public void onItemClicked(View v, int position) {
        Log.d("Item", "Clicked");
    }

    protected class GetAssembliesResultTask extends AsyncTask<String, Void, String> {
        String param = "0";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pullToRefresh)
                pbLoading.setVisibility(View.VISIBLE);

            try{

                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }catch (Exception e){

                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

//            String url = "" + MyApplication.url + "/GetAssembliesResults?";
//            HashMap<String, String> parameters = new HashMap<String, String>();
//            parameters.put("year", year);
//            parameters.put("companyId", ""+companyId);
//            parameters.put("top", "20");
//            parameters.put("Id", params[0]);
//            result = ConnectionRequests.POST(url, parameters);
            param = params[0];
            try {
                final String METHOD_NAME = "GetAssembliesResults";
                final String NAMESPACE = "http://tempuri.org/";

                //SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                //parameters to be passed to the method (parameter name, value)

//                request.addProperty("oldToken", mshared.getString("oldToken",""));
//                request.addProperty("newToken", mshared.getString("newToken",""));
                //            parameters.put("clientID", "" + MyApplication.clientId);
                request.addProperty("year", year);
                request.addProperty("companyId", "" + companyId);
                request.addProperty("top", "50");
                request.addProperty("Id", params[0]);
                Element[] header = new Element[1];
                header[0] = new Element().createElement(NAMESPACE, "SoapClassService");
                Element username = new Element().createElement(NAMESPACE, "ClientID");
                username.addChild(Node.TEXT, "" + MyApplication.mowaziClientID);
                header[0].addChild(Node.ELEMENT, username);
                Element pass = new Element().createElement(NAMESPACE, "oldToken");
                pass.addChild(Node.TEXT, mshared.getString("oldToken", ""));
                header[0].addChild(Node.ELEMENT, pass);
                Element random = new Element().createElement(NAMESPACE, "newToken");
                random.addChild(Node.TEXT, mshared.getString("newToken", ""));
                header[0].addChild(Node.ELEMENT, random);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;


                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res.getProperty("GetAssembliesResultsResult");

                result = t.getProperty("message").toString();

                //  = bank.getProperty("success").toString();


            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


            MoaziAssemblyResultParser parser = new MoaziAssemblyResultParser(result,(MyApplication.lang==MyApplication.ENGLISH)?"en":"ar");
            try {
                allAssemblies.clear();
                if (pullToRefresh) {

                    pullToRefresh = false;
                }
                if (!loadmore && allAssemblies.size()==0)
                    allAssemblies.add(new MoaziAssemblyResult());
                allAssemblies.addAll(parser.GetAssemblies());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            try{

                if (visible) {
                    if (result.contains("Expired")) {
                        LoginAsynck login = new LoginAsynck();
                        login.execute(param);
                    }
          /*          else if (result.contains("Security Issue")) {
                        MyApplication.logged = false;
                        Intent i = new Intent();
                        i.putExtra("activity", "");
                        i.setClass(getActivity(), LoginActivity.class);
                        startActivity(i);
                        getActivity().finish();

                        edit.putBoolean("loggedMowazi", false);
                        edit.commit();
                    } */


                    else {
                        Log.d("allAssemblies", "" + allAssemblies.size());
                        adapter.notifyDataSetChanged();
                        pbLoading.setVisibility(View.GONE);
                        onItemsLoadComplete();

                        if (allAssemblies.size() == 1) {
                            tvNoData.setVisibility(View.VISIBLE);
                            swipeContainer.setVisibility(View.GONE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                            swipeContainer.setVisibility(View.VISIBLE);
                        }
                    }
                }
                else {
                    Log.d("allAssemblies", "" + allAssemblies.size());
                    adapter.notifyDataSetChanged();
                    pbLoading.setVisibility(View.GONE);
                    onItemsLoadComplete();

                    if (allAssemblies.size() == 1) {
                        tvNoData.setVisibility(View.VISIBLE);
                        swipeContainer.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                        swipeContainer.setVisibility(View.VISIBLE);
                    }
                }
                if (getActivity() != null)
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {

            visible = isVisibleToUser;
            if(visible) {
          /*      if(!MyApplication.logged) {
                    try {
                        pbLoading.setVisibility(View.GONE);
                    }catch (Exception e)
                    {

                    }
                    Actions.CreateLoginDialog(getActivity(),getString(R.string.youmustlogin),"");
                }
                else {*/

                    mshared = PreferenceManager.getDefaultSharedPreferences(activity);
                    edit = mshared.edit();
                    if (Actions.expired(mshared)) {
                        LoginAsynck login = new LoginAsynck();
                        login.execute("0");
                    } else {

                        getAssembliesResults = new GetAssembliesResultTask();
                        getAssembliesResults.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"0");
                    }
                }
          //  }
        }catch (Exception e){
            e.printStackTrace();
        }


//        if(isVisibleToUser)
//        {
//            if (Actions.expired(mshared)) {
//                LoginAsynck login = new LoginAsynck();
//                login.execute("0");
//            }
//        }
    }

    //<editor-fold desc="Get Companies">
    /*protected class GetCompanies extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.url + "/GetCompaniesName?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            if (MyApplication.lang.equals("en")) {
                parameters.put("lang", "" + 1);
            } else {
                parameters.put("lang", "" + 2);
            }

            result = ConnectionRequests.POST(url, parameters);

            CompanyNameParser parser = new CompanyNameParser(result, MyApplication.lang);
            try {
                allCompanies.clear();
                MoaziCompany c = new MoaziCompany();
                c.setSymbolEn("-- " + getActivity().getResources().getString(R.string.company) + " --");
                c.setSymbolAr("-- " + getActivity().getResources().getString(R.string.company) + " --");
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
            try{

                spinnerAdapter.notifyDataSetChanged();
                //pbLoading.setVisibility(View.GONE);

                getYears = new GetYears();
                getYears.execute();

                onItemsLoadComplete();
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }*/
    //</editor-fold>

    //<editor-fold desc="GetYears">
    /*protected class GetYears extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            *//*if (!pullToRefresh)
                pbLoading.setVisibility(View.VISIBLE);*//*
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.url + "/GetAssembliesYear?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            result = ConnectionRequests.POST(url, parameters);

            YearParser parser = new YearParser(result, MyApplication.lang);
            try {
                allYears.clear();
                allYears.add("--" + getResources().getString(R.string.year) + "--");
                allYears.addAll(parser.GetYears());
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

            try{

                if (visible) {

                    yearsSpinnerAdapter.notifyDataSetChanged();
                    onItemsLoadComplete();
                    //pbLoading.setVisibility(View.GONE);

                    *//*if (Actions.expired(mshared)) {
                        LoginAsynck login = new LoginAsynck();
                        login.execute("0");
                    } else {

                        getAssembliesResults = new GetAssembliesResultTask();
                        getAssembliesResults.execute("0");
                    }*//*
                } else {

                    Log.wtf("Not","Visible");

                    yearsSpinnerAdapter.notifyDataSetChanged();
                    onItemsLoadComplete();
                    pbLoading.setVisibility(View.GONE);
                    getAssembliesResults = new GetAssembliesResultTask();
                    getAssembliesResults.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"0");
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }*/
    //</editor-fold>

    public class LoginAsynck extends AsyncTask<String, Void, String> {
        String rand = "" + Actions.getRandom();
        String param = "0";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);

        }


        @Override
        protected String doInBackground(String... params) {
            String result = "";
//
//            String url = "" + MyApplication.url + "/Login?";
//
//            HashMap<String, String> parameters = new HashMap<String, String>();
//            // parameters.put("companyId", "" + companyId);
//            parameters.put("username",mshared.getString("usernameMowazi",""));
//            try {
//                parameters.put("password", Actions.getMD5(  Actions.getMD5(mshared.getString("passwordMowazi","")) +rand ) );
//            } catch (NoSuchAlgorithmException e) {
//                e.printStackTrace();
//            }
//            parameters.put("UserRandom", "" + rand);
//
//
//            result = ConnectionRequests.POST(url, parameters);
//            param=params[0];
            try {
                final String METHOD_NAME = "Login";
                final String NAMESPACE = "http://tempuri.org/";

                //SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                //parameters to be passed to the method (parameter name, value)


                Element[] header = new Element[1];
                header[0] = new Element().createElement(NAMESPACE, "LoginHeader");
                Element username = new Element().createElement(NAMESPACE, "Username");
                username.addChild(Node.TEXT, mshared.getString("usernameMowazi", ""));
                header[0].addChild(Node.ELEMENT, username);
                Element pass = new Element().createElement(NAMESPACE, "Password");
                pass.addChild(Node.TEXT, Actions.getMD5(Actions.getMD5(mshared.getString("passwordMowazi", "")) + rand));
                header[0].addChild(Node.ELEMENT, pass);
                Element random = new Element().createElement(NAMESPACE, "UserRandom");
                random.addChild(Node.TEXT, "" + rand);
                header[0].addChild(Node.ELEMENT, random);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;


                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res.getProperty("LoginResult");

                result = t.getProperty("success").toString();

                //  = bank.getProperty("success").toString();


            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            param = params[0];
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            pbLoading.setVisibility(View.GONE);

            String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
            try {
                MyApplication.mowaziClientID = Integer.parseInt(s.split(",")[0]);
                edit.putString("lastdate", formattedDate);
                edit.putInt("clientId", MyApplication.mowaziClientID);
                if (mshared.getString("oldToken", "").equals("") && mshared.getString("newToken", "").equals("")) {
                    edit.putString("oldToken", s.split(",")[2]);
                    edit.putString("newToken", s.split(",")[2]);
                    edit.putString("expiry", s.split(",")[3]);
                    edit.commit();
                } else {
                    edit.putString("oldToken", mshared.getString("newToken", ""));
                    edit.putString("newToken", s.split(",")[2]);
                    edit.putString("expiry", s.split(",")[3]);
                    edit.commit();
                }
            } catch (Exception e) {

            }
            getAssembliesResults = new GetAssembliesResultTask();
            getAssembliesResults.execute(param);


        }
    }
}
