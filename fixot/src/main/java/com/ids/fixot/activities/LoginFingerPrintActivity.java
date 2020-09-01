package com.ids.fixot.activities;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.LoginMenuAdapter;
import com.ids.fixot.adapters.LoginQuickLinkAdapter;
import com.ids.fixot.adapters.SiteMapDataRecyclerAdapter;
import com.ids.fixot.fragments.InteractiveLoginFragment;
import com.ids.fixot.fragments.NormalLoginFragment;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by DEV on 4/25/2018.
 */

public class LoginFingerPrintActivity extends AppCompatActivity implements SiteMapDataRecyclerAdapter.RecyclerViewOnItemClickListener, LoginQuickLinkAdapter.RecyclerViewOnItemClickListener {

    FragmentManager fragmentManager;
    SegmentedGroup sgFilter, sgFilterRtl;
    RadioButton rbInteractive, rbNormal;
    RadioButton rbInteractiveRtl, rbNormalRtl;
    LinearLayout llSocialMedia;
    LinearLayout llCbk;
    RecyclerView rvLinks;
    ImageView ivDrawer, logo_name_imageview;
    LinearLayoutManager layoutManager;
    SiteMapDataRecyclerAdapter adapter;
    LoginQuickLinkAdapter adapterQuickLink;
    android.support.v7.app.AlertDialog.Builder builder;
    android.support.v7.app.AlertDialog dialog;
    RecyclerView rvQuickLink;
    FingerprintManager fingerprintManager;
    KeyguardManager keyguardManager;
    LoginMenuAdapter popupAdapter;
    Button btnLang;
    TextView tvQuicklink, tv_OtcMsg; //ivDrawer
    TextView tvForgotPassword, tvForgotUsername, tvReactivateAccount, tvNewAccount,tvContactUs;
    public TextView tvRegisterOTC;
    private boolean started = false, show = false;
    private RelativeLayout rootLayout;
    private ImageButton tvFacebook,tvTwitter,tvYoutube,tvInstagram;

    public LoginFingerPrintActivity() {
        LocalUtils.updateConfig(this);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Actions.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        Actions.setLocal(MyApplication.lang, this);

        setContentView(R.layout.activity_fingerprint_login);
        Actions.initializeBugsTracking(this);

        started = true;

        findViews();

        show = checkFilterStatus();
        showFilter(false);

        initializeWebRecycler();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        Log.wtf("RECEIVEDD", "UT");
                        MyApplication.webItems = intent.getExtras().getParcelableArrayList("webItems");

                        initializeWebRecycler();

                    }
                }, new IntentFilter("WebItemsService")
        );

        Actions.initializeInstruments(this);
        Actions.overrideFonts(this, rootLayout, false);
        btnLang.setTypeface(MyApplication.droidregular);
    }


    private void initializeWebRecycler() {

        ivDrawer.setVisibility(View.INVISIBLE);
        tvQuicklink.setVisibility(View.INVISIBLE);

        if (MyApplication.webItems.size() == 1 && MyApplication.webItems.get(0).getID() == 0) {

            ivDrawer.setVisibility(View.GONE);
            tvQuicklink.setVisibility(View.GONE);
        } else if (MyApplication.webItems.size() > 0) {

            ivDrawer.setVisibility(View.VISIBLE);
            tvQuicklink.setVisibility(View.VISIBLE);
        }

        try {
            tvForgotPassword.setVisibility(MyApplication.parameter.getForgotPasswordUrl().equals("") ? View.GONE : View.VISIBLE);
            tvForgotUsername.setVisibility(MyApplication.parameter.getForgotUsernameUrl().equals("") ? View.GONE : View.VISIBLE);
            tvReactivateAccount.setVisibility(MyApplication.parameter.getUnlockUserUrl().equals("") ? View.GONE : View.VISIBLE);
            tvNewAccount.setVisibility(MyApplication.parameter.getClientRegistrationUrl().equals("") ? View.GONE : View.VISIBLE);
            tvContactUs.setVisibility(MyApplication.parameter.getContactUsUrl().equals("") ? View.GONE : View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void chooseNormalLogin() {

        sgFilter.setVisibility(View.GONE);
        sgFilterRtl.setVisibility(View.GONE);

        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.container)).commit();
        fragmentManager.beginTransaction()
                .add(R.id.container, new NormalLoginFragment(), getResources().getString(R.string.login))
                .commit();


        new CountDownTimer(35000, 50) {

            @Override
            public void onTick(long arg0) {
            }

            @Override
            public void onFinish() {

                Log.wtf("battal", "blocked");
                showFilter(show);

                MyApplication.editor.putBoolean("fingerprintBlock", false).apply();

                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container, new InteractiveLoginFragment(), getResources().getString(R.string.interactive_login))
                        .commit();
            }
        }.start();
    }


    private void findViews() {

        fragmentManager = getSupportFragmentManager();

        llSocialMedia = findViewById(R.id.llSocialMedia);
        llCbk = findViewById(R.id.llCbk);
        rootLayout = findViewById(R.id.rlLogin);

        tvFacebook = findViewById(R.id.tvFacebook);
        tvTwitter = findViewById(R.id.tvTwitter);
        tvYoutube = findViewById(R.id.tvYoutube);
        tvInstagram = findViewById(R.id.tvInstagram);

        sgFilter = findViewById(R.id.sgFilter);
        rbInteractive = findViewById(R.id.rbInteractive);
        rbNormal = findViewById(R.id.rbNormal);

        sgFilterRtl = findViewById(R.id.sgFilterRtl);
        rbInteractiveRtl = findViewById(R.id.rbInteractiveRtl);
        rbNormalRtl = findViewById(R.id.rbNormalRtl);

        btnLang = findViewById(R.id.btnLang);
        tvQuicklink = findViewById(R.id.tvQl);
        tv_OtcMsg = findViewById(R.id.tv_OtcMsg);
        tv_OtcMsg.setVisibility(MyApplication.isOTC ? View.VISIBLE : View.GONE);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvForgotUsername = findViewById(R.id.tvForgotUsername);
        tvReactivateAccount = findViewById(R.id.tvReactivateAccount);
        tvNewAccount = findViewById(R.id.tvNewAccount);
        tvContactUs = findViewById(R.id.tvContactUs);
        tvRegisterOTC = findViewById(R.id.tvRegisterOTC);
        tvRegisterOTC.setVisibility(MyApplication.isOTC ? View.VISIBLE : View.GONE);

        btnLang.setText(MyApplication.lang != MyApplication.ARABIC ? "عربي" : "English");

        ivDrawer = findViewById(R.id.ivDrawer);
        logo_name_imageview = findViewById(R.id.logo_name_imageview);
        rvLinks = findViewById(R.id.rvLinks);
        adapter = new SiteMapDataRecyclerAdapter(LoginFingerPrintActivity.this, MyApplication.webItems, this);
        rvLinks.setAdapter(adapter);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvLinks.setLayoutManager(layoutManager);

        rbNormal.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        rbInteractive.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        rbNormalRtl.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        rbInteractiveRtl.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        llSocialMedia.setVisibility(BuildConfig.Enable_Media ? View.VISIBLE : View.GONE);
        if(BuildConfig.Enable_Media){
            tvFacebook.setVisibility(MyApplication.parameter.getFacebookLink().isEmpty() ? View.GONE : View.VISIBLE);
            tvTwitter.setVisibility(MyApplication.parameter.getTwitterLink().isEmpty() ? View.GONE : View.VISIBLE);
            tvYoutube.setVisibility(MyApplication.parameter.getYouTubeLink().isEmpty() ? View.GONE : View.VISIBLE);
            tvInstagram.setVisibility(MyApplication.parameter.getRssLink().isEmpty() ? View.GONE : View.VISIBLE);
       }

        llCbk.setVisibility(BuildConfig.BrokerId.equals("662") ? View.VISIBLE : View.GONE);
        popupAdapter = new LoginMenuAdapter(this, MyApplication.webItems);
        Log.wtf("Login", "MyApplication.webItems count = " + MyApplication.webItems.size());

        logo_name_imageview.setImageResource(MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.drawable.logo_name : R.drawable.logo_name_white);
    }


    public void showMenu(View v) {

        builder = new android.support.v7.app.AlertDialog.Builder(this);
        LinearLayoutManager layoutManager;
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout llPrice;

        final View editDialog = inflater.inflate(R.layout.popup_order_duration_type, null);

        rvQuickLink = editDialog.findViewById(R.id.rvDurationType);

        layoutManager = new LinearLayoutManager(this);
        rvQuickLink.setLayoutManager(layoutManager);

        adapterQuickLink = new LoginQuickLinkAdapter(this, MyApplication.webItems, this, 0);
        rvQuickLink.setAdapter(adapterQuickLink);

        builder.setView(editDialog);
        dialog = builder.create();

        dialog.show();
    }

    @Override
    public void onItemClickedd(View v, int position) {
        dialog.dismiss();
        startActivity(new Intent(LoginFingerPrintActivity.this, WebPageActivity.class)
                .putExtra("fromLogin", true)
                .putExtra("webItem", MyApplication.webItems.get(position)));

    }


    public void showRegisterViaMowaziDialog() {



        builder = new android.support.v7.app.AlertDialog.Builder(this);
        LinearLayoutManager layoutManager;
        LayoutInflater inflater = getLayoutInflater();

        LinearLayout llPrice;

        final View editDialogRegisterViaMowazi = inflater.inflate(R.layout.popup_register_via_mowazi, null);

        TextView tv_register = editDialogRegisterViaMowazi.findViewById(R.id.tv_register);
        EditText etTradingNumber = editDialogRegisterViaMowazi.findViewById(R.id.etTradingNumber);
        EditText etEmail = editDialogRegisterViaMowazi.findViewById(R.id.etEmail);
        EditText etFirstName = editDialogRegisterViaMowazi.findViewById(R.id.etFirstName);
        EditText etLastName = editDialogRegisterViaMowazi.findViewById(R.id.etLastName);
        EditText etMobileNumber = editDialogRegisterViaMowazi.findViewById(R.id.etMobileNumber);
        Button btSubmit = editDialogRegisterViaMowazi.findViewById(R.id.btSubmit);
        Button btClose = editDialogRegisterViaMowazi.findViewById(R.id.btClose);

        if(MyApplication.currentUser.getInvestorId()!=0 && MyApplication.isFromLogin){
            etTradingNumber.setText(MyApplication.currentUser.getInvestorId()+" ");
            etTradingNumber.setEnabled(false);
        }



    /*    CheckBox cbTerms = editDialogRegisterViaMowazi.findViewById(R.id.cbTerms);
         TextView tv_view_terms = editDialogRegisterViaMowazi.findViewById(R.id.tv_view_terms);

       tv_view_terms.setOnClickListener(v -> {
                    String url = MyApplication.parameter.getAlmowaziPolicyLink();
                    startActivity(new Intent(LoginFingerPrintActivity.this, PdfDisplayActivity.class).putExtra("url", url));
                }
        );*/

        if (MyApplication.lang == MyApplication.ARABIC) {
            tv_register.setTypeface(MyApplication.droidbold);
            etTradingNumber.setTypeface(MyApplication.droidregular);
            etEmail.setTypeface(MyApplication.droidregular);
            etFirstName.setTypeface(MyApplication.droidregular);
            etLastName.setTypeface(MyApplication.droidregular);
            etMobileNumber.setTypeface(MyApplication.droidregular);
            btSubmit.setTypeface(MyApplication.droidbold);
            btClose.setTypeface(MyApplication.droidregular);

            etTradingNumber.setGravity(Gravity.RIGHT);
            etEmail.setGravity(Gravity.RIGHT);
            etFirstName.setGravity(Gravity.RIGHT);
            etLastName.setGravity(Gravity.RIGHT);
            etMobileNumber.setGravity(Gravity.RIGHT);
        } else {
            tv_register.setTypeface(MyApplication.giloryBold);
            etTradingNumber.setTypeface(MyApplication.giloryItaly);
            etEmail.setTypeface(MyApplication.giloryItaly);
            etFirstName.setTypeface(MyApplication.giloryItaly);
            etLastName.setTypeface(MyApplication.giloryItaly);
            etMobileNumber.setTypeface(MyApplication.giloryItaly);
            btSubmit.setTypeface(MyApplication.giloryBold);
            btClose.setTypeface(MyApplication.giloryItaly);

            etTradingNumber.setGravity(Gravity.LEFT);
            etEmail.setGravity(Gravity.LEFT);
            etFirstName.setGravity(Gravity.LEFT);
            etLastName.setGravity(Gravity.LEFT);
            etMobileNumber.setGravity(Gravity.LEFT);
        }

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

         /*       if (!cbTerms.isChecked())
                    Toast.makeText(LoginFingerPrintActivity.this, getString(R.string.plztermAndcondition), Toast.LENGTH_SHORT).show();

                else */
                    if (!isValidEmail(etEmail.getText().toString()))
                    Toast.makeText(LoginFingerPrintActivity.this, "Please Enter a valid Email", Toast.LENGTH_SHORT).show();

                else {
                    RegisterViaMowaziTask registerViaMowaziTask = new RegisterViaMowaziTask(etTradingNumber.getText().toString(), etEmail.getText().toString(), etFirstName.getText().toString(), etLastName.getText().toString(), etMobileNumber.getText().toString());
                    registerViaMowaziTask.executeOnExecutor(MyApplication.threadPoolExecutor);

                }
            }
        });

        btClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MyApplication.isFromLogin=false;
                            Actions.closeKeyboard(LoginFingerPrintActivity.this);
                        } catch (Exception e) {
                            e.getMessage();
                            e.printStackTrace();
                        }
                    }
                }, 100);
            }
        });


        builder.setView(editDialogRegisterViaMowazi);
        dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                MyApplication.isFromLogin=false;
            }
        });
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 101) {
            if(resultCode == Activity.RESULT_OK){
               // String result=data.getStringExtra("result");
                showRegisterViaMowaziDialog();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                MyApplication.isFromLogin=false;
                Toast.makeText(LoginFingerPrintActivity.this, getString(R.string.plztermAndcondition), Toast.LENGTH_SHORT).show();
            }
        }
    }//onActivityResult


    public void changeLang(View v) {

        int lang = 0;
        String sLang = "";
        if (MyApplication.lang == MyApplication.ARABIC) {
            lang = MyApplication.ENGLISH;
            sLang = "en";
        } else {
            lang = MyApplication.ARABIC;
            sLang = "ar";
        }

        fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.container)).commit();

        MyApplication.instruments.clear();
        Actions.setLocal(lang, LoginFingerPrintActivity.this);
        MyApplication.lang = lang;
        MyApplication.editor.putInt("lang", lang).apply();
        LocalUtils.setLocale(new Locale(sLang));
        LocalUtils.updateConfig(getApplication(), getBaseContext().getResources().getConfiguration());

//        Intent intent = new Intent(MyApplication.class.getName() + "ChangedLanguage");

        LoginFingerPrintActivity.this.recreate();
    }

    private boolean checkFilterStatus() {

        boolean show = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

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

    private void showFilter(boolean show) {

        if (show) {

            if (MyApplication.lang == MyApplication.ENGLISH) {

                //<editor-fold desc="ltr layout">
                sgFilter.setVisibility(View.VISIBLE);
                sgFilterRtl.setVisibility(View.GONE);

                if (rbInteractive.isChecked()) {

                    rbInteractive.setTextColor(ContextCompat.getColor(this, R.color.white));

                } else {

                    rbInteractive.setTextColor(ContextCompat.getColor(this, R.color.colorDark));
                }

                if (rbNormal.isChecked()) {

                    rbNormal.setTextColor(ContextCompat.getColor(this, R.color.white));

                } else {

                    rbNormal.setTextColor(ContextCompat.getColor(this, R.color.colorDark));
                }


                rbInteractive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {

                            rbInteractive.setTextColor(ContextCompat.getColor(LoginFingerPrintActivity.this, R.color.white));

                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                    .replace(R.id.container, new InteractiveLoginFragment(), getResources().getString(R.string.interactive_login))
                                    .commit();

                        } else {

                            rbInteractive.setTextColor(ContextCompat.getColor(LoginFingerPrintActivity.this, R.color.colorDark));
                        }
                    }
                });

                rbNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {

                            rbNormal.setTextColor(ContextCompat.getColor(LoginFingerPrintActivity.this, R.color.white));

                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                    .replace(R.id.container, new NormalLoginFragment(), getResources().getString(R.string.login))
                                    .commit();

                        } else {

                            rbNormal.setTextColor(ContextCompat.getColor(LoginFingerPrintActivity.this, R.color.colorDark));
                        }
                    }
                });
                //</editor-fold>

                rbInteractive.setChecked(true);
            } else {

                //<editor-fold desc="rtl layout">

                sgFilter.setVisibility(View.GONE);
                sgFilterRtl.setVisibility(View.VISIBLE);


                if (rbInteractiveRtl.isChecked()) {

                    rbInteractiveRtl.setTextColor(ContextCompat.getColor(this, R.color.white));

                } else {

                    rbInteractiveRtl.setTextColor(ContextCompat.getColor(this, R.color.colorDark));
                }

                if (rbNormalRtl.isChecked()) {

                    rbNormalRtl.setTextColor(ContextCompat.getColor(this, R.color.white));

                } else {

                    rbNormalRtl.setTextColor(ContextCompat.getColor(this, R.color.colorDark));
                }


                rbInteractiveRtl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {

                            rbInteractiveRtl.setTextColor(ContextCompat.getColor(LoginFingerPrintActivity.this, R.color.white));

                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                    .replace(R.id.container, new InteractiveLoginFragment(), getResources().getString(R.string.interactive_login))
                                    .commit();

                        } else {

                            rbInteractiveRtl.setTextColor(ContextCompat.getColor(LoginFingerPrintActivity.this, R.color.colorDark));
                        }
                    }
                });

                rbNormalRtl.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {

                            rbNormalRtl.setTextColor(ContextCompat.getColor(LoginFingerPrintActivity.this, R.color.white));

                            fragmentManager.beginTransaction()
                                    .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                                    .replace(R.id.container, new NormalLoginFragment(), getResources().getString(R.string.login))
                                    .commit();

                        } else {

                            rbNormalRtl.setTextColor(ContextCompat.getColor(LoginFingerPrintActivity.this, R.color.colorDark));
                        }
                    }
                });
                //</editor-fold>

                rbInteractiveRtl.setChecked(true);
            }
        } else {

            sgFilter.setVisibility(View.GONE);
            sgFilterRtl.setVisibility(View.GONE);

            fragmentManager.beginTransaction()
                    .add(R.id.container, new NormalLoginFragment(), getResources().getString(R.string.login))
                    .commit();

        }
    }

    public void loadPage(View v) {

        String url = "";
        int websiteContentId = -1;

        Intent browserIntent; //= new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        switch (v.getId()) {

            case R.id.tvForgotPassword:
                url = MyApplication.parameter.getForgotPasswordUrl();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tvForgotUsername:
                url = MyApplication.parameter.getForgotUsernameUrl();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tvReactivateAccount:
                url = MyApplication.parameter.getUnlockUserUrl();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tvNewAccount:
                url = MyApplication.parameter.getClientRegistrationUrl();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tvContactUs:
                url = MyApplication.parameter.getContactUsUrl();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tvFacebook:
                url = MyApplication.parameter.getFacebookLink();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tvTwitter:
                url = MyApplication.parameter.getTwitterLink();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);
                websiteContentId = 1;

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tvInstagram:
                url = MyApplication.parameter.getRssLink();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.tvYoutube:
                url = MyApplication.parameter.getYouTubeLink();

//                browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//                startActivity(browserIntent);

                try {
                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
                            .putExtra("fromLogin", true)
                            .putExtra("url", url));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.ivCbk:
                try {
                    this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.cbk.mobilebanking")));
                    this.finish();
                } catch (android.content.ActivityNotFoundException anfe) {
                    this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cbk.mobilebanking&hl=en")));
                    this.finish();
                }
                break;

            case R.id.tvCbk:
                try {
                    this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.cbk.mobilebanking")));
                    this.finish();
                } catch (android.content.ActivityNotFoundException anfe) {
                    this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cbk.mobilebanking&hl=en")));
                    this.finish();
                }
//                url = "https://play.google.com/store/apps/details?id=com.cbk.mobilebanking&hl=en";
//                try {
//                    startActivity(new Intent(LoginFingerPrintActivity.this, SiteMapDataActivity.class)
//                            .putExtra("fromLogin", true)
//                            .putExtra("url", url));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;

            case R.id.tvRegisterOTC:
                String url1 = MyApplication.parameter.getAlmowaziPolicyLink();
                startActivityForResult(new Intent(LoginFingerPrintActivity.this,Activity_agreement.class).putExtra("url", url1),101);
             //   showRegisterViaMowaziDialog();
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Actions.checkSession(this);

        Actions.checkLanguage(this, started);
    }


//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    public void onItemClicked(View v, int position) {
        startActivity(new Intent(LoginFingerPrintActivity.this, WebPageActivity.class)
                .putExtra("fromLogin", true)
                .putExtra("webItem", MyApplication.webItems.get(position)));
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

    public Boolean isValidEmail(String target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

/*    public Boolean isValidEmail(String email){
        Boolean valid = false;
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (email.matches(emailPattern) && email.length() > 0) {
            valid = true;
        }
        return valid;
    }*/

    private class RegisterViaMowaziTask extends AsyncTask<Void, Void, String> {

        String TradingNumber = "", Email = "", FName = "", LName = "", MobileNumber = "";

        public RegisterViaMowaziTask(String TradingNumbers, String Emails, String FNames, String LNames, String MobileNumbers) {
            TradingNumber = TradingNumbers;
            Email = Emails;
            FName = FNames;
            LName = LNames;
            MobileNumber = MobileNumbers;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyApplication.showDialog(LoginFingerPrintActivity.this);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            // String url = "https://almowazi.com/TradingWebservice/WSTradingClient.asmx/SRV_SaveNewUserMowazi?";
            String url = MyApplication.parameter.getAlmowaziRegistrationLink() + "?";
            Log.wtf("link_reg", "link" + url);
            Log.wtf("broker_id", MyApplication.parameter.getMowaziBrokerId());
            // https://www.almowazi.com/md3iphoneservice/services/dataservice.svc

            HashMap<String, String> parameters = new HashMap<String, String>();

            try {
                parameters.clear();
                parameters.put("TradingNumber", TradingNumber);
                parameters.put("Email", Email);
                parameters.put("FName", FName);
                parameters.put("LName", LName);
                parameters.put("MobileNumber", MobileNumber);
                parameters.put("broker_ID", MyApplication.parameter.getMowaziBrokerId());

                result = ConnectionRequests.GET(url, LoginFingerPrintActivity.this, parameters);

                for (Map.Entry<String, String> map : parameters.entrySet()) {
                    Log.wtf("Login RegisterViaMowaziTask", "parameters : " + map.getKey() + "= " + map.getValue());
                }
                Log.wtf("Login", "RegisterViaMowaziTask result = " + result);

                try {
                    publishProgress(params);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
//                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + "SRV_SaveNewUserMowazi", Toast.LENGTH_LONG).show();
                            Toast.makeText(LoginFingerPrintActivity.this, getResources().getString(R.string.check_data), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    public void run() {
//                        Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + "SRV_SaveNewUserMowazi", Toast.LENGTH_LONG).show();
                        Toast.makeText(LoginFingerPrintActivity.this, getResources().getString(R.string.check_data), Toast.LENGTH_SHORT).show();
                    }
                });
            }

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

                JSONObject jsonobj = new JSONObject(result);

                if (jsonobj.getString("Status").equals("Success")) {
                    Toast.makeText(LoginFingerPrintActivity.this, (MyApplication.lang == MyApplication.ARABIC ? jsonobj.getString("ArabicMessage") : jsonobj.getString("EnglishMessage")), Toast.LENGTH_SHORT).show();

                   boolean callLogin=false;
                    if(MyApplication.isFromLogin) {
                        dialog.dismiss();
                        callLogin=true;
                    }else {
                        dialog.dismiss();
                    }
                    final Handler handler = new Handler();
                    boolean finalCallLogin = callLogin;
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if(finalCallLogin) {
                                   FragmentManager fm = getSupportFragmentManager();
                                   NormalLoginFragment fragment = (NormalLoginFragment)fm.findFragmentByTag(getResources().getString(R.string.login));
                                   fragment.callLogin();
                                 }
                                Actions.closeKeyboard(LoginFingerPrintActivity.this);

                            } catch (Exception e) {
                                e.getMessage();
                                e.printStackTrace();
                            }
                        }
                    }, 100);
                } else {
                    Toast.makeText(LoginFingerPrintActivity.this, "please check your data entry", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    //    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        try {
//            fragmentManager.beginTransaction().remove(fragmentManager.findFragmentById(R.id.container)).commit();
//            super.onSaveInstanceState(outState);
//        }catch (Exception e){
//            Log.wtf("fragmentManager","error : " + e.getMessage());
//
//        }
//    }

//    private boolean isPortrait2Landscape() {
//        return isDevicePortrait() && (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
//    }

//    private boolean isDevicePortrait() {
//        return (findViewById(R.id.container) != null);
//    }


}
