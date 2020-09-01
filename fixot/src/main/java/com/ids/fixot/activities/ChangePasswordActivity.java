package com.ids.fixot.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.xml.sax.XMLReader;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.ids.fixot.MyApplication.lang;

public class ChangePasswordActivity extends AppCompatActivity implements MarketStatusListener {

    RelativeLayout rootLayout;
    Toolbar myToolbar;
    TextView tvlogout;
    TextView tvTitle, tvOldPass, tvNewPass, tvVerifyPass, tvComplexPass;
    EditText etOldPass, etNewPass, etVerifyPass;
    Button btnUpdate;
    Boolean enableComplexPass = false;
    private BroadcastReceiver receiver;
    private boolean started = false;

    public ChangePasswordActivity() {
        LocalUtils.updateConfig(this);
    }

    public static boolean IsWhiteSpace(String inputdata) {
        String whiteSpace = " \t\n\r", sChar;

        for (int i = 0; i < inputdata.trim().length(); i++) {
            sChar = inputdata.substring(i, i + 1);
            if (whiteSpace.indexOf(sChar) != -1)
                return true;
        }
        return false;
    }

    @Override
    public void refreshMarketTime(String status, String time, Integer color) {

        final TextView marketstatustxt = findViewById(R.id.market_state_value_textview);
        final LinearLayout llmarketstatus = findViewById(R.id.ll_market_state);
        final TextView markettime = findViewById(R.id.market_time_value_textview);
        if(BuildConfig.label.matches("tijari") && (MyApplication.marketStatus.getStatusDescriptionAr().equals("مفتوح") ||
                        MyApplication.marketStatus.getStatusDescriptionEn().equals("Open"))) {
            marketstatustxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green_color));
            markettime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green_color));
        }else {
            marketstatustxt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            markettime.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }
        marketstatustxt.setText(status);
        markettime.setText(time);
        llmarketstatus.setBackground(ContextCompat.getDrawable(this, color));

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_change_password);
        Actions.initializeBugsTracking(this);
        Actions.showHideFooter(this);

        enableComplexPass = MyApplication.parameter.isComplexPasswordEnabled();
        findViews();

        started = true;

        Actions.initializeToolBar(getString(R.string.change_password), ChangePasswordActivity.this);
        Actions.overrideFonts(this, rootLayout, false);

        tvlogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findViews() {
        rootLayout = findViewById(R.id.rootLayout);

        myToolbar = findViewById(R.id.my_toolbar);
        tvlogout = myToolbar.findViewById(R.id.tvLogout);
        tvlogout.setVisibility(View.GONE);

        tvTitle = findViewById(R.id.tvTitle);
        tvOldPass = findViewById(R.id.tvFirstPass_1);
        tvNewPass = findViewById(R.id.tvSecondPass_1);
        tvVerifyPass = findViewById(R.id.tvVerifyPass_1);
        tvComplexPass = findViewById(R.id.tvComplexPass);

        etOldPass = findViewById(R.id.etFirstPass_1);
        etNewPass = findViewById(R.id.etSecondPass_1);
        etVerifyPass = findViewById(R.id.etVerifyPass_1);
        btnUpdate = findViewById(R.id.btnUpdate);


        tvComplexPass.setVisibility(View.VISIBLE);
        try {
            String htmlString = ((MyApplication.lang == MyApplication.ARABIC) ? (enableComplexPass ? MyApplication.CmplxPassStringAr : MyApplication.PassStringAr) : (enableComplexPass ? MyApplication.CmplxPassStringEn : MyApplication.PassStringEn));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvComplexPass.setText(Html.fromHtml(htmlString, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvComplexPass.setText(Html.fromHtml(htmlString, null, new UlTagHandler()));// setText(Html.fromHtml(htmlString));
            }
        } catch (Exception e) {
            Log.wtf("Change Password", "Error set html Text -> " + e.getMessage());
        }


        btnUpdate.setOnClickListener(view -> {
            if (Actions.isNetworkAvailable(ChangePasswordActivity.this)) {
                if (etOldPass.getText().toString().trim().equals("") || etNewPass.getText().toString().trim().equals("") || etVerifyPass.getText().toString().trim().equals("")) {
                    Actions.CreateDialog(ChangePasswordActivity.this, getString(R.string.errorupdate), false, false);
                } else if (!etNewPass.getText().toString().equals(etVerifyPass.getText().toString())) {
                    Actions.CreateDialog(ChangePasswordActivity.this, getString(R.string.errorVerify), false, false);
                } else if (IsValidPass()) {
                    UpdatePasswordTask updatePasswordTask = new UpdatePasswordTask();
                    updatePasswordTask.execute();
                }
            } else {
                Actions.CreateDialog(ChangePasswordActivity.this, getString(R.string.no_net), false, false);
            }
        });
    }

    private Boolean IsValidPass() {
        try {
            if (etNewPass.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.errorupdate), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (IsControlChr(etNewPass.getText().toString().trim())) {
                Toast.makeText(this, getString(R.string.PasswordNoCtrlChr), Toast.LENGTH_SHORT).show();
                return false;
            }

            if ((etNewPass.length() > MyApplication.parameter.getMaximumPasswordLength()
                    || etNewPass.length() < MyApplication.parameter.getMinimumPasswordLength())
                    && !etNewPass.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.PasswordLength) + " " + MyApplication.parameter.getMinimumPasswordLength()
                        + " " + getString(R.string.and) + " " + MyApplication.parameter.getMaximumPasswordLength(), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (IsWhiteSpace(etNewPass.getText().toString().trim())) // Manager.IsWhiteSpace(txtPassword.Text.Trim())) //etNewPass.getText().toString().matches(".*\\w.*")
            {
                Toast.makeText(this, getString(R.string.PasswordContainSpaces), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (enableComplexPass) {
                String sPasswordCompare = "ABCDEFGHIJKLMNOPQRSTUVWXYZYXWVUTSRQPONMLKJIHGFEDCBA";

                if (IsSorted(etNewPass.getText().toString(), sPasswordCompare)) {
                    Toast.makeText(this, getString(R.string.PasswordNotSortedAlphab), Toast.LENGTH_SHORT).show();
                    return false;
                }

                sPasswordCompare = "012345678909876543210";

                if (IsSorted(etNewPass.getText().toString(), sPasswordCompare)) {
                    Toast.makeText(this, getString(R.string.PasswordNotSortedNumeric), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (IsRepeated(etNewPass.getText().toString())) {
                    Toast.makeText(this, getString(R.string.PasswordNo2RepeatedChar), Toast.LENGTH_SHORT).show();
                    return false;
                }

                if (!IsAlphaNumeric(etNewPass.getText().toString())) {
                    Toast.makeText(this, getString(R.string.PasswordAlphanumeric), Toast.LENGTH_SHORT).show();
                    return false;
                }
            }

            return true;
        } catch (Exception ex) {
            Log.wtf("Error", "in ManageAccountInformation.IsValidPass", ex);
            return false;
        }
    }


    public Boolean IsControlChr(String inputData) {
        String sChar, CtrlChr = "$()/|?,;:'~<>\\+=.[]{}";

        for (int i = 0; i < inputData.trim().length(); i++) {
            sChar = inputData.substring(i, i + 1);
            if (CtrlChr.indexOf(sChar) != -1)
                return true;
        }
        return false;
    }

    public Boolean IsSorted(String stext, String sCompareVar) {
        Boolean bSorted = false;
        String sChar;

        for (int i = 0; i < stext.length() && bSorted == false; i++) {
            if (stext.substring(i).length() >= 3) {
                sChar = stext.substring(i, i + 3);
                if (sCompareVar.toLowerCase().indexOf(sChar.toLowerCase()) != -1)
                    bSorted = true;
            }
        }
        return bSorted;
    }

    public Boolean IsRepeated(String sText) {
        int code, ncount = 1;
        byte[] b = sText.getBytes(StandardCharsets.US_ASCII);
        code = b[0];

        for (int i = 1; i <= b.length - 1; i++) {
            if (code == b[i]) {
                ncount = ncount + 1;
            } else {
                code = b[i];
                ncount = 1;
            }

            if (ncount == 3) {
                return true;
            }
        }
        return false;
    }

    public Boolean IsAlphaNumeric(String sText) {
        Boolean bAlphaNum = false;
        String sChar, sAlpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ", sNum = "0123456789";

        sText = sText.toUpperCase(); //Convert sText To uppercase to make comparisons easier.

        for (int i = 0; i < sText.length() && bAlphaNum == false; i++) {
            sChar = sText.substring(i, i + 1);
            if (sAlpha.indexOf(sChar) != -1)
                bAlphaNum = true;
        }

        if (bAlphaNum == false)
            return bAlphaNum;

        bAlphaNum = false;

        for (int i = 0; i < sText.length() && bAlphaNum == false; i++) {
            sChar = sText.substring(i, i + 1);
            if (sNum.indexOf(sChar) != -1)
                bAlphaNum = true;
        }

        return bAlphaNum;
    }

    public void loadFooter(View v) {
        Actions.loadFooter(this, v);
    }

    public void back(View v) {
        View view = this.findViewById(android.R.id.content);
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.checkLanguage(this, started);
        Actions.InitializeSessionService(this);
        //  Actions.InitializeMarketService(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    public void onBackPressed() {
        Actions.exitApp(this);
    }

    private class UpdatePasswordTask extends AsyncTask<Void, Void, String> {

        String oldPass = "", newPass = "";
        String random = Actions.getRandom();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            oldPass = etOldPass.getText().toString();
            newPass = etNewPass.getText().toString();
            MyApplication.showDialog(ChangePasswordActivity.this);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.ChangePassword.getValue();
            // MyApplication.link + MyApplication.Login.getValue();

            JSONStringer stringer = null;
            try {
                stringer = new JSONStringer()
                        .object()
                        .key("OldPassword").value(Actions.MD5(Actions.MD5(oldPass) + random) + "")
                        .key("Password").value(Actions.MD5(newPass) + "")
                        .key("UserID").value(MyApplication.currentUser.getId() + "")
                        .key("random").value(random + "")
                        .key("key").value(MyApplication.afterKey)
                        .endObject();
                //MyApplication.currentUser.getKey())
                Log.wtf("stringer", "stringer = " + stringer);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                result = ConnectionRequests.POSTWCF(url, stringer);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    if (MyApplication.isDebug) {
                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + "ChangePassword", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception es) {
                    Log.wtf("Appservice ", "Error - ChangePassword : " + es.getMessage());
                }
            }
            Log.wtf("result", "result = " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            MyApplication.dismiss();

            JSONObject object = null;
            try {
                object = new JSONObject(result);
                String status = object.getString("Status");
                String messageAr = object.getString("MessageAr");
                String messageEn = object.getString("MessageEn");
                if (!status.equals("0")) {
                    Log.wtf("Invalid", "Request");
                    Toast.makeText(ChangePasswordActivity.this, MyApplication.lang == MyApplication.ENGLISH ? messageEn : messageAr, Toast.LENGTH_SHORT).show();
                    etOldPass.setText("");
                    etNewPass.setText("");
                    etVerifyPass.setText("");
                } else {
                    Toast.makeText(ChangePasswordActivity.this, getString(R.string.updatesuccessful), Toast.LENGTH_SHORT).show();

                    MyApplication.editor.putString("etPassword", "").apply();
                    MyApplication.editor.putBoolean("saveusernamepassword", false).apply();

                    MyApplication.threadPoolExecutor = null;
                    MyApplication.threadPoolExecutor = new ThreadPoolExecutor(MyApplication.corePoolSize, MyApplication.maximumPoolSize,
                            MyApplication.keepAliveTime, TimeUnit.SECONDS, MyApplication.workQueue);

                    finishAffinity();
                    Intent i = new Intent(ChangePasswordActivity.this, LoginFingerPrintActivity.class);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


class UlTagHandler implements Html.TagHandler {
    @Override
    public void handleTag(boolean opening, String tag, Editable output,
                          XMLReader xmlReader) {
        if (tag.equals("ul") && !opening) output.append("\n");
        if (tag.equals("li") && opening) output.append("\n\t•");
    }
}