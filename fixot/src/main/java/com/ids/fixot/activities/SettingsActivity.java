package com.ids.fixot.activities;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Amal on 4/6/2017.
 */

public class SettingsActivity extends AppCompatActivity implements MarketStatusListener {

    RelativeLayout rlLayout, changePassword, layoutFingerPrint, layoutVersionNumber, layoutPushNotification;
    Switch switchNot, switchFingerprint, switchDarckTheme;
    ImageView ivArrow;
    RadioButton rbArabic, rbEnglish;
    TextView tvVersionNumber,tvChangeFont;
    String versionNumber = "";
    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;
    RelativeLayout layoutThemes;
    private BroadcastReceiver receiver;
    private Boolean darkThemeChanged=false;

    public SettingsActivity() {
        LocalUtils.updateConfig(this);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_setting);
        Actions.initializeBugsTracking(this);

        findViews();

        setListeners();

        Actions.initializeToolBar(getString(R.string.settings), SettingsActivity.this);
        Actions.overrideFonts(this, rlLayout, false);
        Actions.showHideFooter(this);

        rbArabic.setTypeface(MyApplication.droidbold);
    }

    private void findViews() {

        ivArrow = findViewById(R.id.ivArrow);
        tvVersionNumber = findViewById(R.id.tvVersionNumber);
        layoutVersionNumber = findViewById(R.id.layoutVersionNumber);
        layoutFingerPrint = findViewById(R.id.layoutFingerPrint);
        layoutPushNotification = findViewById(R.id.layoutPushNotification);
        changePassword = findViewById(R.id.changePassword);
        rlLayout = findViewById(R.id.rlLayout);
        switchNot = findViewById(R.id.switchNot);
        switchFingerprint = findViewById(R.id.switchFingerprint);
        switchDarckTheme = findViewById(R.id.switchDarckTheme);
        rbArabic = findViewById(R.id.rbArabic);
        rbEnglish = findViewById(R.id.rbEnglish);
        layoutThemes = findViewById(R.id.layoutThemes);
        tvChangeFont=findViewById(R.id.tvChangeFont);

        /*changePassword.setVisibility(View.VISIBLE);
        ivArrow.setRotation(MyApplication.lang == MyApplication.ENGLISH ? 180f : 0f);*/

        try {

            versionNumber = getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " (" + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode + ")";

            tvVersionNumber.setText(versionNumber);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        switchFingerprint.setChecked(MyApplication.mshared.getBoolean(getResources().getString(R.string.allow_finger_print), false));
        switchDarckTheme.setChecked(!MyApplication.mshared.getBoolean(getResources().getString(R.string.normal_theme), true));

//        layoutThemes.setVisibility(View.GONE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

            try {
                if (fingerprintManager.isHardwareDetected()) {

                    if (fingerprintManager.hasEnrolledFingerprints()) {

                        if (keyguardManager.isKeyguardSecure()) {
                            //check if allowed and Remembered
                            layoutFingerPrint.setVisibility(View.VISIBLE);
                            layoutVersionNumber.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this, R.color.colorLight));
                            layoutVersionNumber.setBackgroundColor(ContextCompat.getColor(SettingsActivity.this, MyApplication.mshared.getBoolean(SettingsActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightTheme));

                        } else {

                            layoutFingerPrint.setVisibility(View.GONE);
                        }
                    }
                } else {

                    layoutFingerPrint.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                layoutFingerPrint.setVisibility(View.GONE);
            }
        } else {
            layoutFingerPrint.setVisibility(View.GONE);
        }

        tvChangeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
     //   Toast.makeText(getApplicationContext(),"asdasdas",Toast.LENGTH_LONG).show();
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

    private void setListeners() {

        if (MyApplication.lang == MyApplication.ARABIC)
            rbArabic.setChecked(true);
        else
            rbEnglish.setChecked(true);


        rbArabic.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                MyApplication.instruments.clear();
                Actions.setLocal(MyApplication.ARABIC, SettingsActivity.this);
                MyApplication.lang = MyApplication.ARABIC;
                MyApplication.editor.putInt("lang", MyApplication.ARABIC).apply();
                LocalUtils.setLocale(new Locale("ar"));
                LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

                Intent intent = new Intent(MyApplication.class.getName() + "ChangedLanguage");
                LocalBroadcastManager.getInstance(SettingsActivity.this).sendBroadcast(intent);

                SettingsActivity.this.recreate();

            }
        });

        rbEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                MyApplication.instruments.clear();
                Actions.setLocal(MyApplication.ENGLISH, SettingsActivity.this);
                MyApplication.lang = MyApplication.ENGLISH;
                MyApplication.editor.putInt("lang", MyApplication.ENGLISH).apply();
                LocalUtils.setLocale(new Locale("en"));
                LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

                Intent intent = new Intent(MyApplication.class.getName() + "ChangedLanguage");
                LocalBroadcastManager.getInstance(SettingsActivity.this).sendBroadcast(intent);

                SettingsActivity.this.recreate();
            }
        });
        layoutPushNotification.setOnClickListener(view -> switchNot.performClick());


        switchFingerprint.setOnClickListener(v -> {

            Log.wtf("Clicked on switch", "now");
            if (switchFingerprint.isChecked()) {
                MyApplication.editor.putBoolean(getResources().getString(R.string.allow_finger_print), true).apply();
            } else {

                MyApplication.editor.putBoolean(getResources().getString(R.string.allow_finger_print), false).apply();
            }
        });
        layoutFingerPrint.setOnClickListener(view -> switchFingerprint.performClick());


        switchDarckTheme.setOnClickListener(v -> {
            darkThemeChanged=!darkThemeChanged;
            Log.wtf("Clicked on switchDarckTheme", "now");
            if (switchDarckTheme.isChecked()) {
                MyApplication.editor.putBoolean(getResources().getString(R.string.normal_theme), false).apply();
            } else {
                MyApplication.editor.putBoolean(getResources().getString(R.string.normal_theme), true).apply();
            }
             SettingsActivity.this.recreate();
       /*     finish();
            startActivity(getIntent());*/

        });
    }

    public void back(View v) {

       // if(darkThemeChanged)
           startActivity(new Intent(SettingsActivity.this,MoreActivity.class));
           finish();
     //   else
        //    finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //Actions.setActivityTheme(this);
        //finish();
        //startActivity(getIntent());

        Actions.checkSession(this);
        //Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //  Actions.InitializeMarketServiceV2(this);

    }

    @Override
    public void onBackPressed() {

      //  if(darkThemeChanged)
            startActivity(new Intent(SettingsActivity.this,MoreActivity.class));
            finish();
 /*       else
            super.onBackPressed();*/
    }

    @Override
    protected void onStop() {
        super.onStop();

        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }
}
