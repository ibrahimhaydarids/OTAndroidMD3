package com.ids.fixot.activities.mowazi;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.mowaziAdapters.MowaziHomepageAdapter;
import com.ids.fixot.model.mowazi.MowaziCategory;
import com.ids.fixot.parser.MowaziCompanyNameParser;
import com.ids.fixot.parser.MowaziMobileConfigurationParser;

import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;


public class MowaziHomeActivity extends AppCompatActivity implements MowaziHomepageAdapter.RecyclerViewOnItemClickListener {

    public String value;
    public double dinar;
    public double brokeragesumcoif;
    public double brokeragequantitycoif;
    Toolbar myToolbar;
    ArrayList<MowaziCategory> categories = new ArrayList<>();
    RecyclerView rvGrid;
    GridLayoutManager glm;
    int numOfRows;
    MowaziHomepageAdapter adapter;
    RelativeLayout rootLayout;
    GetVersion getVersion;
    GetCompanies getCompanies;
    private boolean started = false;

    public MowaziHomeActivity() {
        LocalUtils.updateConfig(this);
    }

    public static void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());

        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_mowazi_home);
        started = true;

        findViews();
        Actions.initializeToolBar(getString(R.string.menu_mowazi), MowaziHomeActivity.this);


        if (Actions.isNetworkAvailable(this)) {

//            Toast.makeText(getApplicationContext(), "RegisterOrLoginMowazi.execute", Toast.LENGTH_SHORT).show();
            new RegisterOrLoginMowazi().execute();

        } else {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }


        Actions.overrideFonts(this, rootLayout, true);
    }

    private void findViews() {

        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        rootLayout = (RelativeLayout) findViewById(R.id.root_layout);

        numOfRows = 3;
        rvGrid = (RecyclerView) findViewById(R.id.rvGrid);
        glm = new GridLayoutManager(this, numOfRows);

        //<editor-fold desc="initialize homepage">
        categories.add(new MowaziCategory(1, getString(R.string.mowazi_companies), R.drawable.mowazi_companies));

//        categories.add(new MowaziCategory(2, getString(R.string.mowazi_place_order), R.drawable.mowazi_place_order));

        categories.add(new MowaziCategory(3, getString(R.string.mowazi_news), R.drawable.mowazi_news));

//        categories.add(new MowaziCategory(4, getString(R.string.mowazi_orders), R.drawable.mowazi_neworders));

//        categories.add(new MowaziCategory(5, getString(R.string.mowazi_latest_orders), R.drawable.mowazi_orders));

//        categories.add(new MowaziCategory(6, getString(R.string.mowazi_trades), R.drawable.mowazi_deals));

//        categories.add(new MowaziCategory(7, getString(R.string.mowazi_my_orders), R.drawable.mowazi_my_order));

//        categories.add(new MowaziCategory(8, getString(R.string.mowazi_my_trade), R.drawable.mowazi_coins));

         categories.add(new MowaziCategory(9, getString(R.string.communityHome), R.drawable.n_community));

        //</editor-fold>

        adapter = new MowaziHomepageAdapter(MowaziHomeActivity.this, categories, this);
        rvGrid.setAdapter(adapter);
        rvGrid.setHasFixedSize(true);
        rvGrid.setLayoutManager(glm);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.checkLanguage(this, started);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void GoToMowazi(View v) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=ids.almouwazi")));
        } catch (android.content.ActivityNotFoundException anfe) {
            startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=ids.almouwazi")));
        }
    }

    public void back(View v) {
        finish();
        //Actions.startActivity(MowaziHomeActivity.this, MarketIndexActivity.class, true);
    }

    @Override
    public void onBackPressed() {
        finish();
        //Actions.startActivity(MowaziHomeActivity.this, MarketIndexActivity.class, true);
    }

    public void onItemClicked(View v, int position) {

        MowaziCategory category = categories.get(position);

        switch (category.getId()) {

            case 1: //companies
                startActivity(new Intent(MowaziHomeActivity.this, MowaziCompaniesActivity.class));
                break;

            case 2: //place an order || 0 for place order || 1 for order details
                startActivity(new Intent(MowaziHomeActivity.this, MowaziPlaceOrderActivity.class).putExtra("flag", 0));
                break;

            case 3: //news
                startActivity(new Intent(MowaziHomeActivity.this, MowaziNewsActivity.class).putExtra("pager", 1));
                break;

            case 4: //orders summary
                startActivity(new Intent(MowaziHomeActivity.this, MowaziOrdersSummaryActivity.class));
                break;

            case 5: //latest orders
                startActivity(new Intent(MowaziHomeActivity.this, MowaziOnlineOrdersActivity.class));
                break;

            case 6: //latest deals
                startActivity(new Intent(MowaziHomeActivity.this, MowaziDealsActivity.class));
                break;


            case 7: //my orders
                startActivity(new Intent(MowaziHomeActivity.this, MowaziMyOrdersActivity.class));
                break;

            case 8: //my trades
                startActivity(new Intent(MowaziHomeActivity.this, MowaziMyTradesActivity.class));
                break;

            case 9: //Community
                startActivity(new Intent(MowaziHomeActivity.this, MoaziAssembliesActivity.class));
                break;

            default:
                break;
        }
    }

    public class GetCompanies extends AsyncTask<Void, Void, String> {

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

            MowaziCompanyNameParser parser = new MowaziCompanyNameParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                MyApplication.allMowaziCompanies.clear();

                /*MowaziCompany c = new MowaziCompany();
                c.setSymbolEn("--" + MowaziHomeActivity.this.getResources().getString(R.string.mowazi_company) + "--");
                c.setSymbolAr("--" + MowaziHomeActivity.this.getResources().getString(R.string.mowazi_company) + "--");
                MyApplication.allMowaziCompanies.add(0, c);*/

                MyApplication.allMowaziCompanies.addAll(parser.GetCompanies());
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
            Log.wtf("Done Loading", "Companies");
        }
    }

    public class GetVersion extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetMobileConfiguration?";
            HashMap<String, String> parameters = new HashMap<String, String>();

            result = ConnectionRequests.POST(url, parameters);

            MowaziMobileConfigurationParser parser = new MowaziMobileConfigurationParser(result, MyApplication.lang == MyApplication.ENGLISH ? "en" : "ar");
            try {
                MyApplication.allMowaziMobileConfigurations.addAll(parser.GetConfigurations());
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            for (int i = 0; i < MyApplication.allMowaziMobileConfigurations.size(); i++) {

                if (MyApplication.allMowaziMobileConfigurations.get(i).getId() == 1)
                    value = MyApplication.allMowaziMobileConfigurations.get(i).getValue();

                if (MyApplication.allMowaziMobileConfigurations.get(i).getId() == 5)

                    dinar = Double.parseDouble(MyApplication.allMowaziMobileConfigurations.get(i).getValue());

                if (MyApplication.allMowaziMobileConfigurations.get(i).getId() == 6)
                    brokeragesumcoif = Double.parseDouble(MyApplication.allMowaziMobileConfigurations
                            .get(i).getValue());

                if (MyApplication.allMowaziMobileConfigurations.get(i).getId() == 7)

                    brokeragequantitycoif = Double.parseDouble(MyApplication.allMowaziMobileConfigurations.get(i).getValue());
                else
                    try {
                        if (MyApplication.allMowaziMobileConfigurations.get(i).getId() == 8)

                            MyApplication.mowaziNewsLink = MyApplication.allMowaziMobileConfigurations.get(i).getValue();
                        else if (MyApplication.allMowaziMobileConfigurations.get(i).getId() == 11)

                            MyApplication.mowaziGeneralNewsLink = MyApplication.allMowaziMobileConfigurations.get(i).getValue();
                    } catch (Exception e) {

                    }
            }
            MyApplication.dinar = dinar;
            MyApplication.brokerageQuantityCoif = brokeragequantitycoif;
            MyApplication.brokerageSumCoif = brokeragesumcoif;

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

                    getVersion = new GetVersion();
//                    Toast.makeText(getApplicationContext(), "getVersion.executeOnExecutor", Toast.LENGTH_SHORT).show();
                    getVersion.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    getCompanies = new GetCompanies();
//                    Toast.makeText(getApplicationContext(), "getCompanies.executeOnExecutor", Toast.LENGTH_SHORT).show();
                    getCompanies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else {

                    if (!MyApplication.trustEveryone) {
                        MyApplication.trustEveryone = true;
                        trustEveryone();
                        new RegisterOrLoginMowazi().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error when register in mowazi", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
