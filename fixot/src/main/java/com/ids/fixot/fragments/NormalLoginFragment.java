package com.ids.fixot.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.ChangePassFromLoginActivity;
import com.ids.fixot.activities.LoginFingerPrintActivity;
import com.ids.fixot.activities.MarketIndexActivity;
import com.ids.fixot.activities.MenuActivity;
import com.ids.fixot.activities.TimeSalesActivity;
import com.ids.fixot.classes.MessageEvent;
import com.ids.fixot.classes.SqliteDb_TimeSales;
import com.ids.fixot.model.TimeSale;
import com.ids.fixot.services.GetTradeService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;

import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.view.Fingerprint;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;

/**
 * Created by DEV on 4/25/2018.
 */

public class NormalLoginFragment extends Fragment {

    Button btLogin;
    RelativeLayout rootLayout;
    AlertDialog alert;
    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;
    String OldPassword = "";
    Boolean fingerEnable;
    Boolean loginFinger;
    public EditText etUsername, etPassword;
    private CheckBox cbRemember;
    private boolean firstTimeLogin = true;

    public static Fragment newInstance(Context context) {
        return new NormalLoginFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_normal_login, null);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fingerEnable = checkFingerStatus();
        loginFinger = fingerEnable;
        findViews();

        if ((MyApplication.mshared.getBoolean("saveusernamepassword", false)
                //&& !MyApplication.mshared.getBoolean(getResources().getString(R.string.allow_finger_print), false))
                //||
                //MyApplication.mshared.getBoolean("fingerprintBlock", false)) {
        )) {
            cbRemember.setChecked(true);

            etUsername.setText(MyApplication.mshared.getString("etUsername", ""));
            etPassword.setText(fingerEnable ? "" : MyApplication.mshared.getString("etPassword", ""));
        }

        firstTimeLogin = MyApplication.mshared.getBoolean("firstLogin", true);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if ((MyApplication.mshared.getBoolean("saveusernamepassword", false))) {
                    etPassword.setText(fingerEnable ? "" : MyApplication.mshared.getString("etPassword", ""));
                }
            }
        }, 100);

    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if ( (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) || (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) ) {
//
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if ((MyApplication.mshared.getBoolean("saveusernamepassword", false) )){
//                        etPassword.setText(MyApplication.mshared.getString("etPassword", ""));
//                    }
//                }
//            }, 100);
//        }
//    }


    private void findViews() {

        btLogin = getActivity().findViewById(R.id.btLogin);
        etUsername = getActivity().findViewById(R.id.etUsername);
        etPassword = getActivity().findViewById(R.id.etPassword);
        cbRemember = getActivity().findViewById(R.id.cbRemember);
        rootLayout = getActivity().findViewById(R.id.rlLogin);

        if (fingerEnable) {
            btLogin.setText(getResources().getString(R.string.biometric_login));
        }

        btLogin.setOnClickListener(view -> {

            if (Actions.isNetworkAvailable(getActivity())) {

                if (etUsername.getText().toString().trim().equals("") || (etPassword.getText().toString().trim().equals("") && !loginFinger)) { //
                    Actions.CreateDialog(getActivity(), getString(R.string.errorlogin), false, false);
                } else if (loginFinger) {
                    showFingerprintDialog(getActivity(), getResources().getString(R.string.cancel), false);
                }
//                else if(!etUsername.getText().toString().trim().equals(MyApplication.mshared.getString("etUsername", ""))){
//                    if(etPassword.getText().toString().trim().equals("")) {
//                        Actions.CreateDialog(getActivity(), getString(R.string.errorlogin), false, false);
//                    }else{
//                        LoginTask login = new LoginTask();
//                        login.execute();
//                    }
//                }
                else {
                    LoginTask login = new LoginTask();
                    login.execute();
                }
            } else {
                Actions.CreateDialog(getActivity(), getString(R.string.no_net), false, false);
            }

        });

//        etPassword.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(fingerEnable){
//                    if(s.length() != 0 ){
//                        btLogin.setText(getResources().getString(R.string.login));
//                    }else{
//                        btLogin.setText(getResources().getString(R.string.biometric_login));
//                    }
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) { }
//        });
//
//        etUsername.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if(fingerEnable){
//                    if(s.length() != 0 ){
//                        btLogin.setText(getResources().getString(R.string.login));
//                    }else{
//                        btLogin.setText(getResources().getString(R.string.biometric_login));
//                    }
//                }
//            }
//            @Override
//            public void afterTextChanged(Editable s) { }
//        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    if(loginFinger) {
                    loginFinger = false;
                    //     etPassword.setText("");
                    btLogin.setText(getResources().getString(R.string.login));
//                    }
                }
            }
        });

        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    if(loginFinger) {
                    loginFinger = false;
                    //  etPassword.setText("");
                    btLogin.setText(getResources().getString(R.string.login));
//                    }
                }
            }
        });

        Actions.overrideFonts(getActivity(), rootLayout, false);
    }

//    @Override
//    public void onResume(){
//        super.onResume();
//
//        Intent intent = getActivity().getIntent();
//        OldPassword = intent.getExtras().getString("OldPassword");
//
//    }

    private boolean checkFingerPrint() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getActivity().getSystemService(FINGERPRINT_SERVICE);

            try {
                if (fingerprintManager.isHardwareDetected()) {

                    if (fingerprintManager.hasEnrolledFingerprints()) {

                        return keyguardManager.isKeyguardSecure();
                    }
                } else {

                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return false;
    }

    private void showFingerprintDialog(final Activity activity, String cancelText, final boolean firstTime) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View fingerPrintDialog = inflater.inflate(R.layout.fragment_fingerprint, null);
        final AppCompatTextView tvStatus = fingerPrintDialog.findViewById(R.id.tvStatus);
        final Fingerprint fingerprint = fingerPrintDialog.findViewById(R.id.fingerprint);

        fingerPrintDialog.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        fingerPrintDialog.setTextDirection(View.TEXT_DIRECTION_LTR);

        fingerprint.delayAfterError(1000);
        fingerprint.callback(new FingerprintCallback() {
            @Override
            public void onAuthenticationSucceeded() {

                Actions.playRingtone(getActivity());
                MyApplication.editor.putBoolean(activity.getResources().getString(R.string.allow_finger_print), true).apply();
                setStatus(getActivity(), tvStatus, getResources().getString(R.string.fingerprint_success), R.color.green_color);

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    //Do something after 100ms fingerprint.cancel();
                  try {
                      alert.dismiss();
                  }catch (Exception e){}

                    LoginTask login = new LoginTask();
                    login.execute();

//                    Intent i;
//
//                    if (BuildConfig.GoToMenu)
//                        i = new Intent(getActivity(), MenuActivity.class); //Go to main menu, no footer in all App
//                    else
//                        i = new Intent(getActivity(), MarketIndexActivity.class);
//
//                    startActivity(i);
//                    getActivity().finish();
                }, 1000);
            }

            @Override
            public void onAuthenticationFailed() {

                try {
                    setStatus(getActivity(), tvStatus, getResources().getString(R.string.fingerprint_failure), R.color.red_color);
                } catch (Exception e) {
                }
            }

            @Override
            public void onAuthenticationError(int errorCode, String error) {
                try {
                    setStatus(getActivity(), tvStatus, getResources().getString(R.string.fingerprint_failure), R.color.red_color);
                } catch (Exception e) {
                }
            }
        });

        fingerprint.authenticate();

        builder.setView(fingerPrintDialog)
                .setCancelable(false)
                .setNegativeButton(cancelText, (dialog, id) -> {
                    dialog.cancel();
                    fingerprint.cancel();
                    if (firstTime) {
                        MyApplication.editor.putBoolean(activity.getResources().getString(R.string.allow_finger_print), false).apply();

                    /*GetPortfolio getPortfolioAsync = new GetPortfolio();
                    getPortfolioAsync.executeOnExecutor(MyApplication.threadPoolExecutor);*/

                        Intent i;

                        if (BuildConfig.GoToMenu)
                            i = new Intent(getActivity(), MenuActivity.class); //Go to main menu, no footer in all App
                        else
                            i = new Intent(getActivity(), MarketIndexActivity.class);

                        startActivity(i);
                        getActivity().finish();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    //</editor-fold>

    private void setStatus(Context context, AppCompatTextView tvStatus, String text, int textColorId) {
        tvStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), textColorId, context.getTheme()));
        tvStatus.setText(text);
    }

    private boolean checkFingerStatus() {

        boolean show = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            keyguardManager = (KeyguardManager) getActivity().getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getActivity().getSystemService(FINGERPRINT_SERVICE);

            try {
                if (fingerprintManager.isHardwareDetected()) {

                    if (fingerprintManager.hasEnrolledFingerprints()) {

                        if (keyguardManager.isKeyguardSecure()) {

                            //check if allowed and Remembered
                            show = MyApplication.mshared.getBoolean("saveusernamepassword", false)
                                    && MyApplication.mshared.getBoolean(getResources().getString(R.string.allow_finger_print), false);

                        } else {

                            show = false;
                            Log.wtf("Lockscreen", "Not enabled");
                        }
                    } else {

                        show = false;
                        Log.wtf("No", "No fingerprint configured");
                    }
                } else {

                    show = false;
                    Log.wtf("Device doesn't", "support fingerprint authentication");
                }
            } catch (Exception e) {
                e.printStackTrace();
                show = false;
            }
        } else {

            show = false;
        }

        return show;
    }

    private void showFingerprintDialogs(final Activity activity, String cancelText, final boolean firstTime) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);
        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);

        LayoutInflater inflater = activity.getLayoutInflater();
        final View fingerPrintDialog = inflater.inflate(R.layout.fragment_fingerprint, null);
        final AppCompatTextView tvStatus = fingerPrintDialog.findViewById(R.id.tvStatus);
        final Fingerprint fingerprint = fingerPrintDialog.findViewById(R.id.fingerprint);

        //fingerPrintDialog.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        //fingerPrintDialog.setTextDirection(View.TEXT_DIRECTION_LTR);

        fingerprint.delayAfterError(1000);
        fingerprint.callback(new FingerprintCallback() {
            @Override
            public void onAuthenticationSucceeded() {

                Actions.playRingtone(getActivity());
                MyApplication.editor.putBoolean(activity.getResources().getString(R.string.allow_finger_print), true).apply();
                setStatus(getActivity(), tvStatus, getResources().getString(R.string.fingerprint_success), R.color.green_color);

                MyApplication.editor.putBoolean("fingerprintBlock", false).apply();

                new LoginTask().executeOnExecutor(MyApplication.threadPoolExecutor);
            }

            @Override
            public void onAuthenticationFailed() {

                Actions.performVibration(getActivity());
                setStatus(getActivity(), tvStatus, getResources().getString(R.string.fingerprint_failure), R.color.red_color);

                //MyApplication.editor.putBoolean("fingerprintBlock", false).apply();
            }

            @Override
            public void onAuthenticationError(int errorCode, String error) {

                Actions.performVibration(getActivity());
                setStatus(getActivity(), tvStatus, getResources().getString(R.string.fingerprint_failure), R.color.red_color);

                Log.wtf("errorCode", "is " + errorCode);
                Log.wtf("error", "is " + error);
            }
        });

        fingerprint.tryLimit(5, () -> {

            //MyApplication.editor.putBoolean("fingerprintCanceled", true).apply();
            alert.dismiss();
            fingerprint.cancel();
            MyApplication.editor.putBoolean("fingerprintBlock", true).apply();
            ((LoginFingerPrintActivity) getActivity()).chooseNormalLogin();
            Log.wtf("onTryLimitReached", "is onTryLimitReached");
        });

        fingerprint.authenticate();

        builder.setView(fingerPrintDialog)
                .setCancelable(false)
                .setNegativeButton(cancelText, (dialog, id) -> {
                    dialog.cancel();
                    fingerprint.cancel();
                    if (firstTime) {
                        MyApplication.editor.putBoolean(activity.getResources().getString(R.string.allow_finger_print), false).apply();
                    }
                });
        alert = builder.create();
        alert.show();

    }


    public void callLogin(){
         LoginTask login = new LoginTask();
         login.execute();
    }

    public class LoginTask extends AsyncTask<Void, Void, String> {

        String username = "", password = "";
        String random = Actions.getRandom();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = etUsername.getText().toString();
            if (etPassword.getText().toString().trim().equals("")) {
                password = MyApplication.mshared.getString("etPassword", "");
            } else {
                password = etPassword.getText().toString();
            }
            Log.wtf("login", "password = " + password);
            OldPassword = password;
            try {
                MyApplication.showDialog(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.Login.getValue();

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("Username").value(username)
                        .key("Password").value(Actions.MD5(Actions.MD5(password) + random))
                        .key("Random").value(random)
                        .key("Key").value(getString(R.string.beforekey))
                        .key("DeviceType").value("1")
                        .endObject();

                Log.wtf("login", "stringer = " + stringer);
                result = ConnectionRequests.POSTWCF(url, stringer);
                //Log.wtf("Login","result = " + result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //  load();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                MyApplication.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                MyApplication.currentUser = GlobalFunctions.GetUserInfo(result);
                MyApplication.editor.putString(getResources().getString(R.string.afterkey), MyApplication.currentUser.getKey()).apply();
                MyApplication.afterKey = MyApplication.currentUser.getKey();


                MyApplication.editor.putBoolean("fingerprintBlock", false).apply();
                if (MyApplication.currentUser.getStatus()==0||MyApplication.currentUser.getStatus()==52) {

                    Log.wtf("status_id",MyApplication.currentUser.getStatus()+"aaaaaaa");

                    for (int i = 0; i < MyApplication.currentUser.getSubAccounts().size(); i++) {
                        if (MyApplication.currentUser.getSubAccounts().get(i).isDefault()) {

                            MyApplication.selectedSubAccount = MyApplication.currentUser.getSubAccounts().get(i);
                            break;
                        }
                    }

                    try {
                        Actions.checkAppService(getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    MyApplication.currentUser.setUsername(username);

                    if (cbRemember.isChecked()) {
                        MyApplication.editor.putBoolean("saveusernamepassword", true).apply();
                        MyApplication.editor.putString("etUsername", etUsername.getText().toString().trim()).apply();
                        MyApplication.editor.putString("etPassword", password).apply();
                        MyApplication.editor.putBoolean("firstLogin", false).apply();
                    } else {
                        MyApplication.editor.putBoolean("saveusernamepassword", false).apply();
                        MyApplication.editor.putString("etUsername", "").apply();
                        MyApplication.editor.putString("etPassword", "").apply();
                    }

//                    MyApplication.editor.putBoolean("firstLogin", false).apply();
                    if (firstTimeLogin && cbRemember.isChecked() && checkFingerPrint()) {

                        showFingerprintDialog(getActivity(), getResources().getString(R.string.skip), firstTimeLogin);
                        firstTimeLogin = false;
                    } else {

                        firstTimeLogin = false;
                        Intent i;

            /*            GetTrades getTrades = new GetTrades();
                        getTrades.executeOnExecutor(MyApplication.threadPoolExecutor);*/

                        getActivity().startService( new Intent(getActivity(), GetTradeService.class));




                        if (MyApplication.currentUser.isResetPassword()) {
                            MyApplication.editor.putString("etPassword", "").apply();
                            etPassword.setText("");
                            i = new Intent(getActivity(), ChangePassFromLoginActivity.class);
                            i.putExtra("OldPassword", OldPassword);
                            startActivity(i);
                        } else {

                            //MyApplication.currentUser.setStatus(52);

                            if(MyApplication.isOTC) {
                                if (MyApplication.currentUser.getStatus() == 0) {
                                    getActivity().finish();
                                    if (BuildConfig.GoToMenu)
                                        i = new Intent(getActivity(), MenuActivity.class); //Go to main menu, no footer in all App
                                    else
                                        i = new Intent(getActivity(), MarketIndexActivity.class);
                                    startActivity(i);
                                    getActivity().finish();
                                } else if(MyApplication.currentUser.getStatus() == 52) {
                                    MyApplication.lastPasswordUsed=etPassword.getText().toString();
                                    if (MyApplication.lang == MyApplication.ARABIC)
                                        openRegistrationPopup(MyApplication.currentUser.getMessageAr());

                                    else
                                        openRegistrationPopup(MyApplication.currentUser.getMessageEn());


                                }else {
                                    if (MyApplication.lang == MyApplication.ARABIC)
                                        Actions.CreateDialog(getActivity(), MyApplication.currentUser.getMessageAr(), false, false);
                                    else
                                        Actions.CreateDialog(getActivity(), MyApplication.currentUser.getMessageEn(), false, false);

                                }

                            }else {
                                if (BuildConfig.GoToMenu)
                                    i = new Intent(getActivity(), MenuActivity.class); //Go to main menu, no footer in all App
                                else
                                    i = new Intent(getActivity(), MarketIndexActivity.class);

                                startActivity(i);
                                getActivity().finish();
                            }



                        }

                    }
                }
                else {

                    if (MyApplication.lang == MyApplication.ARABIC)
                        Actions.CreateDialog(getActivity(), MyApplication.currentUser.getMessageAr(), false, false);
                    else
                        Actions.CreateDialog(getActivity(), MyApplication.currentUser.getMessageEn(), false, false);
                }
            } catch (Exception e) {
                e.printStackTrace();

                try {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.Login.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception es) {
                    es.printStackTrace();
                }
            }
        }
    }


    private void openRegistrationPopup(String message){
        ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(getActivity().getString(R.string.confirm), (dialog, id) -> {
                    MyApplication.isFromLogin=true;
                    ((LoginFingerPrintActivity)getActivity()).tvRegisterOTC.performClick();
                    dialog.cancel();
                 });
            builder.setNegativeButton(getActivity().getString(R.string.cancel), (dialog, id) ->{dialog.cancel();
            MyApplication.isFromLogin=false;
            });
        AlertDialog alert = builder.create();
        alert.show();

    }

    //<editor-fold desc="Get TimeSales">
    private class GetTrades extends AsyncTask<Void, String, String> {

        ArrayList<TimeSale> retrievedTimeSales;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            retrievedTimeSales = new ArrayList<>();
            MyApplication.timeSalesTimesTamp = "0";
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";

            String url = MyApplication.link + MyApplication.GetTrades.getValue(); // this method uses key after login
            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("stockId", "");
            parameters.put("instrumentId", "");
            parameters.put("MarketID", MyApplication.marketID);
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));
            parameters.put("FromTS", MyApplication.timeSalesTimesTamp);

            Log.wtf("GetTrades Login url", "is: " + url);
            Log.wtf("GetTrades Login parameters", "is: " + parameters);
            try {

                result = ConnectionRequests.GET(url, getActivity(), parameters);
                retrievedTimeSales = GlobalFunctions.GetTimeSales(result,true);

                try{Log.wtf("test_login","size:"+retrievedTimeSales.size());}catch (Exception e){
                    Log.wtf("test_login",e.toString());
                }
                MyApplication.timeSales = new ArrayList<>();
                MyApplication.timeSales.addAll(retrievedTimeSales);
                SqliteDb_TimeSales timeSales_DB = new SqliteDb_TimeSales(getActivity());
                timeSales_DB.open();
                timeSales_DB.deleteTimeSales();
                timeSales_DB.insertTimeSalesList(retrievedTimeSales);
                timeSales_DB.close();



                Log.wtf("NormalLoginFragment", "insertTimeSalesList size = " + retrievedTimeSales.size());
                Log.wtf("NormalLoginFragment", "insertTimeSalesList size = " + retrievedTimeSales.size());

            } catch (Exception e) {
                Log.wtf("NormalLoginFragment",e.toString());
                e.printStackTrace();
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.GetTrades.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });

                } catch (Exception es) {
                    es.printStackTrace();
                }

                try{
                    Log.wtf("test_call_loginagain","true");
               /*     GetTrades getTrades = new GetTrades();
                    getTrades.executeOnExecutor(MyApplication.threadPoolExecutor);*/
                }catch (Exception e1){}

            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
             MyApplication.IsTimeSaleLoginRetreived=true;
             EventBus.getDefault().post(new MessageEvent("1"));
             Log.wtf("loginretreived","true");
           //  Toast.makeText(getActivity(),"retreived",Toast.LENGTH_LONG).show();
//            try {
//
//                if (retrievedTimeSales.size() > 0) {
//
//                    //MyApplication.timeSales.addAll(0, retrievedTimeSales);
//                    if (MyApplication.timeSales.size() == 0) {
//                        MyApplication.timeSales = (retrievedTimeSales);
//                        Log.wtf("timeSales login", "MyApplication.timeSales = retrievedTimeSales , size =  " + MyApplication.timeSales.size());
//                    }
                    Log.wtf("timeSales login size", "is: " + MyApplication.timeSales.size());
                    Log.wtf("AAAAsync Login Timestamp", "is: " + MyApplication.timeSalesTimesTamp);
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }


}
