package com.ids.fixot.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.Calendar;

public class ChangePassFromLoginActivity extends AppCompatActivity {

    RelativeLayout rootLayout;
    Toolbar myToolbar;
    TextView tvTitle, tvNewPass, tvVerifyPass;
    EditText etNewPass, etVerifyPass;
    Button btnUpdate;
    String OldPassword = "";
    private boolean started = false;

    public ChangePassFromLoginActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        setContentView(R.layout.activity_change_pass_from_login);

        Actions.initializeBugsTracking(this);
        findViews();
        started = true;

        Intent intent = getIntent();
        OldPassword = intent.getExtras().getString("OldPassword");
        Log.wtf("OldPass", "OldPass = " + OldPassword);


        Actions.initializeToolBar(getString(R.string.change_password), ChangePassFromLoginActivity.this);
        Actions.overrideFonts(this, rootLayout, false);
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

        tvTitle = findViewById(R.id.tvTitle);
        tvNewPass = findViewById(R.id.tvSecondPass_1);
        tvVerifyPass = findViewById(R.id.tvVerifyPass_1);

        etNewPass = findViewById(R.id.etSecondPass_1);
        etVerifyPass = findViewById(R.id.etVerifyPass_1);
        btnUpdate = findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(view -> {
            if (Actions.isNetworkAvailable(ChangePassFromLoginActivity.this)) {
                if (etNewPass.getText().toString().trim().equals("") || etVerifyPass.getText().toString().trim().equals("")) {
                    Actions.CreateDialog(ChangePassFromLoginActivity.this, getString(R.string.errorupdate), false, false);
                } else if (!etNewPass.getText().toString().equals(etVerifyPass.getText().toString())) {
                    Actions.CreateDialog(ChangePassFromLoginActivity.this, getString(R.string.errorVerify), false, false);
                } else {
                    ChangePassFromLoginActivity.UpdatePasswordTask updatePasswordTask = new ChangePassFromLoginActivity.UpdatePasswordTask();
                    updatePasswordTask.execute();
                }
            } else {
                Actions.CreateDialog(ChangePassFromLoginActivity.this, getString(R.string.no_net), false, false);
            }

        });
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
//        Actions.InitializeMarketService(this);
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
            oldPass = OldPassword;
            newPass = etNewPass.getText().toString();
            MyApplication.showDialog(ChangePassFromLoginActivity.this);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.ChangePassword.getValue();

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
                    Toast.makeText(ChangePassFromLoginActivity.this, MyApplication.lang == MyApplication.ENGLISH ? messageEn : messageAr, Toast.LENGTH_SHORT).show();
                    etNewPass.setText("");
                    etVerifyPass.setText("");
                } else {
                    Toast.makeText(ChangePassFromLoginActivity.this, getString(R.string.updatesuccessful), Toast.LENGTH_SHORT).show();
                    Intent i;
                    i = new Intent(ChangePassFromLoginActivity.this, LoginFingerPrintActivity.class);

                    finish();
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
