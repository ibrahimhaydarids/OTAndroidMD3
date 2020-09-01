package com.ids.fixot.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.ids.fixot.classes.SqliteDb_TimeSales;
import com.ids.fixot.model.TimeSale;

import org.json.JSONException;
import org.json.JSONStringer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import me.aflak.libraries.callback.FingerprintCallback;
import me.aflak.libraries.view.Fingerprint;

/**
 * Created by DEV on 4/25/2018.
 */

public class InteractiveLoginFragment extends Fragment {

    LinearLayout rootLayout;
    EditText etUsername;
    Button btLogin;
    AlertDialog alert;

    String OldPassword = "";

    public static Fragment newInstance(Context context) {

        return new NormalLoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.wtf("loginfragment", "interactive");
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_interactive_login, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();
        showFingerprintDialog(getActivity(), getResources().getString(R.string.cancel), false);
        Actions.overrideFonts(getActivity(), rootLayout, false);

    }

    private void findViews() {

        rootLayout = getActivity().findViewById(R.id.rootLayout);
        btLogin = getActivity().findViewById(R.id.btLogin);
        etUsername = getActivity().findViewById(R.id.etUsername);
        etUsername.setClickable(false);
        etUsername.setFocusable(false);
        etUsername.setText(MyApplication.mshared.getString("etUsername", ""));


        btLogin.setOnClickListener(view -> showFingerprintDialog(getActivity(), getResources().getString(R.string.cancel), false));

    }

    private void showFingerprintDialog(final Activity activity, String cancelText, final boolean firstTime) {

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

    private void setStatus(Context context, AppCompatTextView tvStatus, String text, int textColorId) {
        tvStatus.setTextColor(ResourcesCompat.getColor(context.getResources(), textColorId, context.getTheme()));
        tvStatus.setText(text);
    }
    //</editor-fold>

    private class LoginTask extends AsyncTask<Void, Void, String> {

        String username = "", password = "";
        String random = Actions.getRandom();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            username = MyApplication.mshared.getString("etUsername", "");
            password = MyApplication.mshared.getString("etPassword", "");
            OldPassword = password;

            MyApplication.showDialog(getActivity());
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
            } catch (JSONException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.Login.getKey(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            result = ConnectionRequests.POSTWCF(url, stringer);
            //  load();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            MyApplication.dismiss();
            try {
                MyApplication.currentUser = GlobalFunctions.GetUserInfo(result);
                MyApplication.editor.putString(getResources().getString(R.string.afterkey), MyApplication.currentUser.getKey()).apply();
                MyApplication.afterKey = MyApplication.currentUser.getKey();
                Log.wtf("===========LOGIN==========", MyApplication.currentUser.getKey());
                Log.wtf("===========LOGIN2==========", MyApplication.mshared.getString(getResources().getString(R.string.afterkey), ""));


                if (MyApplication.currentUser.getMessageEn().equals("Success")) {

                    try {
                        Actions.checkAppService(getActivity());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < MyApplication.currentUser.getSubAccounts().size(); i++) {
                        if (MyApplication.currentUser.getSubAccounts().get(i).isDefault()) {

                            MyApplication.selectedSubAccount = MyApplication.currentUser.getSubAccounts().get(i);
                            break;
                        }
                    }

                    MyApplication.currentUser.setUsername(username);


                    GetTrades getTrades = new GetTrades();
                    getTrades.executeOnExecutor(MyApplication.threadPoolExecutor);

                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        //Do something after 100ms fingerprint.cancel();
                        alert.dismiss();

                        Intent i;

                        if (MyApplication.currentUser.isResetPassword()) {

                            MyApplication.editor.putString("etPassword", "").apply();
                            i = new Intent(getActivity(), ChangePassFromLoginActivity.class);
                            i.putExtra("OldPassword", OldPassword);
                        } else {
                            getActivity().finish();
                            if (BuildConfig.GoToMenu)
                                i = new Intent(getActivity(), MenuActivity.class); //Go to main menu, no footer in all App
                            else
                                i = new Intent(getActivity(), MarketIndexActivity.class);
                        }

                        startActivity(i);
//                        getActivity().finish();
                    }, 1000);

                } else {

                    if (MyApplication.lang == MyApplication.ARABIC)
                        Actions.CreateDialog(getActivity(), MyApplication.currentUser.getMessageAr(), false, false);
                    else
                        Actions.CreateDialog(getActivity(), MyApplication.currentUser.getMessageEn(), false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.Login.getKey(), Toast.LENGTH_LONG).show();
                    }
                });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.Login.getKey(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    //<editor-fold desc="Get TimeSales">
    private class GetTrades extends AsyncTask<Void, String, String> {

        ArrayList<TimeSale> retrievedTimeSales;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            retrievedTimeSales = new ArrayList<>();
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

            Log.wtf("Async Timestamp", "is: " + MyApplication.timeSalesTimesTamp);

            try {
                result = ConnectionRequests.GET(url, getActivity(), parameters);
                retrievedTimeSales = GlobalFunctions.GetTimeSales(result,true);


                SqliteDb_TimeSales timeSales_DB = new SqliteDb_TimeSales(getActivity());
                timeSales_DB.open();
                timeSales_DB.deleteTimeSales();
                timeSales_DB.insertTimeSalesList(retrievedTimeSales);
                timeSales_DB.close();
                Log.wtf("NormalLoginFragment", "insertTimeSalesList size = " + retrievedTimeSales.size());


            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.GetTrades.getKey(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //MyApplication.isTimeSaleLoginRetreived=true;
            try {

                if (retrievedTimeSales.size() > 0) {

                    MyApplication.timeSales.addAll(0, retrievedTimeSales);
                    Log.wtf("timeSales size", "is: " + MyApplication.timeSales.size());
                }

            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.GetTrades.getKey(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

}
