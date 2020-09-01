package com.ids.fixot.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.enums.enums;
import com.ids.fixot.model.WebItem;

import org.json.JSONException;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;


/**
 * Created by user on 2/20/2017.
 */

public class SplashActivity extends AppCompatActivity {

    boolean firstRun = true;
    LinearLayout llLanguage;
    TextView tvVersionNumber;
    Button btArabic, btEnglish;
    AddDevice addDevice;
    GetParameters getParameters;
    GetSiteMap getSiteMap;
    GetBrokerageFrees getBrokerageFrees;
    GetInstruments getInstruments;
    GetUnits getUnits;
    ImageView iv_logo;

    CheckIfWebserviceAvailable checkIfWebserviceAvailable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toast.makeText(getApplicationContext(),"asdsad222",Toast.LENGTH_LONG).show();

      //Actions.trustEveryone();

        try {

            Actions.setActivityTheme(this);
        } catch (Exception e) {
            e.getMessage();
        }

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_splash);
//        Log.wtf("normal theme","" + MyApplication.mshared.getBoolean(getResources().getString(R.string.normal_theme), true));

        try {
            firstRun = MyApplication.mshared.getInt("lang", 0) == 0;
        } catch (Exception e) {
            e.printStackTrace();
            firstRun = false;
        }


        MyApplication.instruments.clear();
        MyApplication.instrumentsHashmap.clear();

        findViews();

        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        } else {

            checkIfWebserviceAvailable = new CheckIfWebserviceAvailable();
            checkIfWebserviceAvailable.executeOnExecutor(MyApplication.threadPoolExecutor);

        }

        try {

            Actions.overrideFonts(this, llLanguage, false);
        } catch (Exception e) {
            e.getMessage();
        }

      //  MyApplication.showOTC = (BuildConfig.BrokerId.equals("150") || BuildConfig.BrokerId.equals("140"));
       // MyApplication.isOTC = (BuildConfig.BrokerId.equals("150") || BuildConfig.BrokerId.equals("140"));
        MyApplication.marketID= (MyApplication.isOTC ? Integer.toString(enums.MarketType.KWOTC.getValue()) : Integer.toString(enums.MarketType.XKUW.getValue()));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        /*try {
            getBrokerageFrees.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("exception", "brokerage");
        }

        try {
            getParameters.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("exception", "parameters");
        }

        try {
            getSiteMap.cancel(true);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("exception", "sitemap");
        }*/

        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void findViews() {

        llLanguage = findViewById(R.id.llLanguage);
        tvVersionNumber = findViewById(R.id.tvVersionNumber);
        btArabic = findViewById(R.id.btArabic);
        btEnglish = findViewById(R.id.btEnglish);
        iv_logo = findViewById(R.id.iv_logo);

        iv_logo.setImageResource(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.drawable.logo_name : R.drawable.logo_name_white);

        String versionNumber = this.getResources().getString(R.string.current_version) + " " + Actions.GetVersionCode(SplashActivity.this);

        tvVersionNumber.setText(versionNumber);
        btArabic.setOnClickListener(v -> {

            MyApplication.lang = MyApplication.ARABIC;
            MyApplication.editor.putInt("lang", MyApplication.lang).apply();
            LocalUtils.setLocale(new Locale("ar"));
            LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

            //startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            startActivity(new Intent(SplashActivity.this, LoginFingerPrintActivity.class));
            finish();
        });

        btEnglish.setOnClickListener(v -> {

            MyApplication.lang = MyApplication.ENGLISH;
            MyApplication.editor.putInt("lang", MyApplication.lang).apply();
            LocalUtils.setLocale(new Locale("en"));
            LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

            //startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            startActivity(new Intent(SplashActivity.this, LoginFingerPrintActivity.class));
            finish();
        });
    }

    private void sendBroadcastUpdatedList(ArrayList<WebItem> webItems) {

        Intent intent = new Intent("WebItemsService");
        intent.putParcelableArrayListExtra("webItems", webItems);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //<editor-fold desc="check for update dialogs">
    public void onCreateDialogForPlayStore(final Activity activity) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        TextView textView;
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View textEntryView = inflater.inflate(R.layout.item_dialog, null);
        textView = textEntryView.findViewById(R.id.dialogMsg);
        textView.setGravity(Gravity.CENTER);
        textView.setText(activity.getResources().getString(R.string.update_message));
        builder.setTitle(activity.getResources().getString(R.string.update_title));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout

        builder.setView(textEntryView)

                // Add action buttons
                .setNegativeButton(activity.getResources().getString(R.string.update_button), (dialog, id) -> {
                    dialog.dismiss();


                    final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
                    try {

                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        activity.finish();

                    } catch (android.content.ActivityNotFoundException anfe) {

                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        activity.finish();

                    }
                });
        final AlertDialog d = builder.create();
        d.setOnShowListener(arg0 -> {
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SplashActivity.this, R.color.colorDark));
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTransformationMethod(null);
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
        });
        d.setCancelable(false);
        d.show();
    }

    public void onCreateDialogForPlayStoreNoForce(final Activity activity) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        TextView textView;
        // Get the layout inflater
        LayoutInflater inflater = activity.getLayoutInflater();
        final View textEntryView = inflater.inflate(R.layout.item_dialog,
                null);
        textView = textEntryView.findViewById(R.id.dialogMsg);
        textView.setGravity(Gravity.CENTER);
        textView.setText(activity.getResources().getString(R.string.update_message));
        builder.setTitle(activity.getResources().getString(R.string.update_title));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(textEntryView)

                // Add action buttons
                .setPositiveButton(activity.getResources().getString(R.string.update_button), (dialog, id) -> {
                    dialog.dismiss();
                    final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        activity.finish();

                    } catch (android.content.ActivityNotFoundException anfe) {
                        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        activity.finish();

                    }
                })

                .setNegativeButton(activity.getResources().getString(R.string.update_cancel), (dialog, id) -> {
                    dialog.dismiss();
                    //get sitemap data

                    if (firstRun) {//first run and no language selected

                        llLanguage.setVisibility(View.VISIBLE);
                        llLanguage.setAlpha(0.0f);
                        llLanguage.animate()
                                .setDuration(1000)
                                .translationY(llLanguage.getHeight())
                                .alpha(1.0f)
                                .setListener(null);
                    } else {

                        //Actions.startActivity(SplashActivity.this, LoginActivity.class, true);
                        Actions.startActivity(SplashActivity.this, LoginFingerPrintActivity.class, true);
                    }
                });

        final AlertDialog d = builder.create();
        d.setOnShowListener(arg0 -> {
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(SplashActivity.this, R.color.colorDark));
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setTransformationMethod(null);
            d.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);

            d.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(SplashActivity.this, R.color.colorDark));
            d.getButton(AlertDialog.BUTTON_POSITIVE).setTransformationMethod(null);
            d.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
        });
        d.setCancelable(false);

        //Actions.setLocalSplash(MyApplication.lang, activity);
        d.show();

    }

    private class AddDevice extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String url = MyApplication.link + MyApplication.AddDevice2.getValue();

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("DeviceID").value(0)
                        .key("DeviceType").value(1)
                        .key("EnableNotifications").value(true)
                        .key("IMEI").value(Actions.GetUniqueID(SplashActivity.this))
                        .key("Model").value(Actions.getDeviceName())
                        .key("Token").value(MyApplication.mshared.getString(getString(R.string.regId), ""))
                        .key("UserID").value(0)
                        .key("MarketId").value(MyApplication.marketID)
                        .endObject();

            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.AddDevice2.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            String result = ConnectionRequests.POSTWCF(url, stringer);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            getSiteMap = new GetSiteMap();
            getSiteMap.executeOnExecutor(MyApplication.threadPoolExecutor);

            getParameters = new GetParameters();
            getParameters.executeOnExecutor(MyApplication.threadPoolExecutor);

            getBrokerageFrees = new GetBrokerageFrees();
            getBrokerageFrees.executeOnExecutor(MyApplication.threadPoolExecutor);

            getInstruments = new GetInstruments();
            getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);

            getUnits = new GetUnits();
            getUnits.executeOnExecutor(MyApplication.threadPoolExecutor);
        }
    }

    private class CheckIfWebserviceAvailable extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            String result = " WebService is Unavailable";
            String url = MyApplication.link + MyApplication.GetParameters.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("deviceType", "1");
            parameters.put("key", getString(R.string.beforekey));
            parameters.put("MarketId", MyApplication.marketID);

            try {
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                Log.wtf("CheckIfWebserviceAvailable ", " success result = " + result);
                MyApplication.parameter = GlobalFunctions.GetParameters(result);

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (MyApplication.isDebug) {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + 888, Toast.LENGTH_LONG).show();
                        }

                        //return this code and comment next code
                        GetAlternativeWebserviceUrl getAlternativeWebserviceUrl = new GetAlternativeWebserviceUrl();
                        getAlternativeWebserviceUrl.executeOnExecutor(MyApplication.threadPoolExecutor);
                    }
                });
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                Log.wtf("CheckIfWebserviceAvailable", "result = " + result);

                if (firstRun) { //first run and no language selected

                    addDevice = new AddDevice();
                    addDevice.execute();

                } else {

                    getSiteMap = new GetSiteMap();
                    getSiteMap.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getParameters = new GetParameters();
                    getParameters.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getBrokerageFrees = new GetBrokerageFrees();
                    getBrokerageFrees.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getInstruments = new GetInstruments();
                    getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getUnits = new GetUnits();
                    getUnits.executeOnExecutor(MyApplication.threadPoolExecutor);
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("exception", e.getMessage());
            }
        }
    }

    private class GetAlternativeWebserviceUrl extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.AlternativeWebserviceLink;
            HashMap<String, String> parameters = new HashMap<String, String>();

            try {
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                Log.wtf("GetAlternativeWebserviceUrl ", "result = " + result);
                String altUrl = GlobalFunctions.GetWebserviceUrl(result);
                Log.wtf("Alternative", "url = " + altUrl);

                MyApplication.link = altUrl + "Services/DataService.svc";
                MyApplication.baseLink = altUrl + "Mobile/";

                Log.wtf("alternativelink",MyApplication.link);
                Log.wtf("alternativebase",MyApplication.baseLink);
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + 999, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (firstRun) { //first run and no language selected

                    addDevice = new AddDevice();
                    addDevice.execute();

                } else {

                    getSiteMap = new GetSiteMap();
                    getSiteMap.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getParameters = new GetParameters();
                    getParameters.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getBrokerageFrees = new GetBrokerageFrees();
                    getBrokerageFrees.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getInstruments = new GetInstruments();
                    getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getUnits = new GetUnits();
                    getUnits.executeOnExecutor(MyApplication.threadPoolExecutor);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("exception", e.getMessage());
            }
        }
    }

    private class GetParameters extends AsyncTask<Void, Void, String> {

        int needsUpdate = 0;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetParameters.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("deviceType", "1");
            parameters.put("key", getString(R.string.beforekey));

            try {
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                Log.wtf("GetParameters ", "result = " + result);

                MyApplication.parameter = GlobalFunctions.GetParameters(result);
                MyApplication.defaultPriceType = Integer.parseInt(MyApplication.parameter.getDefaultPriceOnTrade());

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetParameters.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (MyApplication.parameter.getServerVersionNumber().length() > 0) {

                    MyApplication.showMowazi = MyApplication.parameter.isEnableMowazi();

                    MyApplication.mowaziBrokerId = MyApplication.parameter.getMowaziBrokerId();

                   // MyApplication.mowaziUrl = MyApplication.parameter.getMowaziServiceLink();
                      MyApplication.mowaziUrl = "https://www.almowazi.com/AlmowaziDevelopmentService/servicedata.asmx";


                    //Commemted to be change once uploaded 26_10_2018 : MKobaissy

                    needsUpdate = Actions.CheckVersion(SplashActivity.this, MyApplication.parameter.getServerVersionNumber(), MyApplication.parameter.isForceUpdate());

                    if (needsUpdate == 0) {

                        if (firstRun) {//first run and no language selected

                            llLanguage.setVisibility(View.VISIBLE);
                            llLanguage.setAlpha(0.0f);
                            llLanguage.animate()
                                    .setDuration(1000)
                                    .translationY(llLanguage.getHeight())
                                    .alpha(1.0f)
                                    .setListener(null);

                        } else {

                            //Actions.startActivity(SplashActivity.this, LoginActivity.class, true);
                            Actions.startActivity(SplashActivity.this, LoginFingerPrintActivity.class, true);
                        }

                    } else if (needsUpdate == 1) {

                        onCreateDialogForPlayStoreNoForce(SplashActivity.this);
                    } else if (needsUpdate == 2) {

                        onCreateDialogForPlayStore(SplashActivity.this);
                    }
                }
            } catch (Exception e) {

                e.printStackTrace();

                Log.wtf("exception", e.getMessage());

                if (firstRun) {//first run and no language selected

                    addDevice = new AddDevice();
                    addDevice.execute();

                } else {

                    getSiteMap = new GetSiteMap();
                    getSiteMap.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getParameters = new GetParameters();
                    getParameters.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getBrokerageFrees = new GetBrokerageFrees();
                    getBrokerageFrees.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getInstruments = new GetInstruments();
                    getInstruments.executeOnExecutor(MyApplication.threadPoolExecutor);

                    getUnits = new GetUnits();
                    getUnits.executeOnExecutor(MyApplication.threadPoolExecutor);

                }
            }

        }
    }

    private class GetSiteMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.webItems.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetSiteMapData.getValue(); // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("Language", MyApplication.lang == MyApplication.ENGLISH ? "english" : "arabic");
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("MarketId", MyApplication.marketID);
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);

                Log.wtf("GetSiteMapData ", "result = " + result);
                MyApplication.webItems = new ArrayList<>();
                MyApplication.webItems.addAll(GlobalFunctions.GetSiteMapData(result));
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetSiteMapData.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.wtf("webItems size", MyApplication.webItems.size() + "");
            sendBroadcastUpdatedList(MyApplication.webItems);
        }
    }

    public class GetBrokerageFrees extends AsyncTask<String, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.allBrokerageFees.clear();
        }

        @Override
        protected Void doInBackground(String... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetBrokerageFees.getValue(); // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("MarketId", MyApplication.marketID);
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);
                //Log.wtf("GetBrokerageFees ","result = " + result);
                MyApplication.allBrokerageFees = new ArrayList<>();
                MyApplication.allBrokerageFees.addAll(GlobalFunctions.GetBrokerageFeeList(result));

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetBrokerageFees.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;

        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.wtf("brokerage size", "" + MyApplication.allBrokerageFees.size());
        }

    }

    private class GetInstruments extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... a) {


            String result = "";
            String url = MyApplication.link + MyApplication.GetInstruments.getValue(); // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("id", "0");
                parameters.put("key", getResources().getString(R.string.beforekey));
                parameters.put("MarketId", MyApplication.marketID);
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);

                MyApplication.instruments = new ArrayList<>();
                MyApplication.instruments.addAll(GlobalFunctions.GetInstrumentsList(result));
                Log.wtf("Splash", "MyApplication.instruments count = " + MyApplication.instruments.size());
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetInstruments.getKey(), Toast.LENGTH_LONG).show();
                            Log.wtf("GetInstruments", "error : " + e.getMessage());
                        }
                    });
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private class GetUnits extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... a) {


            String result = "";
            String url = MyApplication.link + MyApplication.GetUnits.getValue();// "/GetUnits?"; // this method uses key after login

            try {
                HashMap<String, String> parameters = new HashMap<String, String>();
//                parameters.put("id", "0" );
                parameters.put("key", getResources().getString(R.string.beforekey));
                result = ConnectionRequests.GET(url, SplashActivity.this, parameters);

                MyApplication.units = new ArrayList<>();
                MyApplication.units.addAll(GlobalFunctions.GetUnitsList(result));
                Log.wtf("MyApplication.units", "count = " + MyApplication.units.size());

            } catch (Exception e) {
                e.printStackTrace();
                Log.wtf("request error " + MyApplication.GetUnits.getValue(), ": " + e.getMessage());
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetUnits.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
    //</editor-fold>
}
