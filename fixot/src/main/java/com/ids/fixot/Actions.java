package com.ids.fixot;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.Crashlytics;
import com.ids.fixot.activities.ChangePasswordActivity;
import com.ids.fixot.activities.FavoritesActivity;
import com.ids.fixot.activities.LoginFingerPrintActivity;
import com.ids.fixot.activities.MarketIndexActivity;
import com.ids.fixot.activities.MarketQuotes;
import com.ids.fixot.activities.MoreActivity;
import com.ids.fixot.activities.NewsActivity;
import com.ids.fixot.activities.OrdersActivity;
import com.ids.fixot.activities.PortfolioActivity;
import com.ids.fixot.activities.QuickLinksActivity;
import com.ids.fixot.activities.SectorsActivity;
import com.ids.fixot.activities.SettingsActivity;
import com.ids.fixot.activities.SiteMapDataActivity;
import com.ids.fixot.activities.SplashActivity;
import com.ids.fixot.activities.StockActivity;
import com.ids.fixot.activities.TopsActivity;
import com.ids.fixot.activities.mowazi.MowaziHomeActivity;
import com.ids.fixot.classes.AudioPlayer;
import com.ids.fixot.classes.MyExceptionHandler;
import com.ids.fixot.classes.SqliteDb_TimeSales;
import com.ids.fixot.model.BrokerageFee;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.OrderDurationType;
import com.ids.fixot.model.Stock;
import com.ids.fixot.model.StockQuotation;
import com.ids.fixot.model.TimeSale;
import com.ids.fixot.model.mowazi.MoaziViewResizing;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import io.fabric.sdk.android.Fabric;

import io.fabric.sdk.android.Fabric;

import static com.ids.fixot.MyApplication.lang;

/**
 * Created by user on 2/20/2017.
 */

public class Actions {

    public static final String OneDecimal = "#,##0.0";
    public static final String TwoDecimal = "#,##0.00";
    public static final String ThreeDecimal = "#.000";
    public static final String OneDecimalThousandsSeparator = "#,###.0";
    public static final String OneDecimalSeparator = "#.0";
    public static final String NoDecimalSeparator = "#";
    public static final String TwoDecimalThousandsSeparator = "#,###.00";
    public static final String ThreeDecimalThousandsSeparator = "#,##0.000";
    public static final String NoDecimalThousandsSeparator = "#,###";
    public static BroadcastReceiver marketReceiver;
    private static boolean started = false;
    private static Thread thread;
    private static BroadcastReceiver sessionReceiver;
    private static boolean result;

    public static String convertToEnglishDigits(String value) {

        String newValue = value.replace("١", "1").replace("٢", "2").replace("٣", "3").replace("٤", "4").replace("٥", "5")
                .replace("٦", "6").replace("7", "٧").replace("٨", "8").replace("٩", "9").replace("٠", "0")
                .replace("۱", "1").replace("۲", "2").replace("۳", "3").replace("۴", "4").replace("۵", "5")
                .replace("۶", "6").replace("۷", "7").replace("۸", "8").replace("۹", "9").replace("۰", "0");

        return newValue;
    }

    public static void setLocal(int lang, Context context) {
        String languageCode = "en";

        if (lang == MyApplication.ARABIC)
            languageCode = "ar";

        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale);
        } else {
            setSystemLocaleLegacy(config, locale);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            context.getApplicationContext().getResources().updateConfiguration(config,
                    context.getResources().getDisplayMetrics());
    }

    public static void initializeBugsTracking(Activity activity) {

        if (!MyApplication.isDebug)
            Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler(activity));

         Fabric.with(activity, new Crashlytics());
    }

    public static void exitApp(Activity activity) {

        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();

            if (tasks.get(0).getTaskInfo().numActivities == 1 && tasks.get(0).getTaskInfo().topActivity.getClassName().equals(activity.getClass().getName())) {

                showExitDialog(activity, activity.getString(R.string.exit_app_question));
            } else {
                activity.finish();
            }
        } else {

            List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(10);
            if (taskList.get(0).numActivities == 1 && taskList.get(0).topActivity.getClassName().equals(activity.getClass().getName())) {

                showExitDialog(activity, activity.getString(R.string.exit_app_question));
            } else {
                activity.finish();
            }
        }


    }



    public static void forceExitApp(Activity activity) {

        ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            List<ActivityManager.AppTask> tasks = activityManager.getAppTasks();


                showExitDialog(activity, activity.getString(R.string.exit_app_question));

        } else {

            List<ActivityManager.RunningTaskInfo> taskList = activityManager.getRunningTasks(10);

                showExitDialog(activity, activity.getString(R.string.exit_app_question));

        }


    }

    private static void showExitDialog(final Activity activity, String msg) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setMessage(msg);

        builder.setNegativeButton(activity.getString(android.R.string.no),
                (dialog, id) -> dialog.dismiss())

                .setPositiveButton(activity.getString(android.R.string.yes),
                        (dialog, id) -> {
                            dialog.dismiss();
                            activity.finish();

                            try {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void closeKeyboard(Activity context) {
        try {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public static void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
    }

    // used to format number
    public static double roundNumber(double num, String format) {
        DecimalFormat formatter = new DecimalFormat(format, setInEnglish());

        return Double.valueOf(formatter.format(num));
    }

    public static DecimalFormatSymbols setInEnglish() {
        DecimalFormatSymbols custom = new DecimalFormatSymbols(Locale.ENGLISH);
        custom.setDecimalSeparator('.');
        return custom;
    }

    // used to format number
    public static String formatNumber(double num, String format) {
        DecimalFormat formatter = new DecimalFormat(format, setInEnglish());
        return formatter.format(num);
    }

    public static void setLanguage(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setSystemLocale(config, locale);
        } else {
            setSystemLocaleLegacy(config, locale);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1)
            context.getApplicationContext().getResources().updateConfiguration(config,
                    context.getResources().getDisplayMetrics());
    }

    public static String getLanguage() {

        return MyApplication.lang == MyApplication.ENGLISH ? "English" : "Arabic";
    }

    public static int textColor(String num) {
        if (num != null) {
            if (num.contains("-") || num.contains("("))
                return Color.rgb(239, 78, 80);// red

            else if (num.equals("0") || num.equals("0.00") || num.equals("0.0") || num.equals("0.0%") || num.equals("0.00 %") || num.equals("0.00%"))
                return Color.rgb(242, 156, 68);// orange
            else
                return Color.rgb(75, 186, 115); // green
        } else
            return Color.rgb(242, 156, 68);// orange

    }

    public static void initializeInstruments(Context context) {

        MyApplication.instruments.clear();

        Instrument fake = new Instrument();
        fake.setId("-1");
        fake.setInstrumentSymbol("");
        fake.setInstrumentCode("");
        fake.setInstrumentNameAr(context.getResources().getString(R.string.choose_instrument_all));
        fake.setInstrumentNameEn(context.getResources().getString(R.string.choose_instrument_all));

        MyApplication.instruments.add(fake);
    }

    public static void performVibration(Activity activity) {

        Vibrator v = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(MyApplication.VIBRATION_PERIOD, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(MyApplication.VIBRATION_PERIOD);
        }
    }

    public static void playRingtone(Activity activity) {

        AudioPlayer player = new AudioPlayer();
        player.play(activity, R.raw.plucky);
    }

    public static void initializeToolBar(String s, AppCompatActivity c) {

        checkAppService(c);

        Toolbar myToolbar = c.findViewById(R.id.my_toolbar);
        c.setSupportActionBar(myToolbar);
        c.getSupportActionBar().setDisplayShowTitleEnabled(false);


        if (c instanceof MarketIndexActivity) {
            ImageView menu_marketindex = c.findViewById(R.id.menu_marketindex);

            TextView tvmenu_marketindex = c.findViewById(R.id.tvmenu_marketindex);
            menu_marketindex.setColorFilter(ContextCompat.getColor(c, MyApplication.mshared.getBoolean(c.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
            tvmenu_marketindex.setTextColor(ContextCompat.getColor(c, MyApplication.mshared.getBoolean(c.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));

        } else if (c instanceof PortfolioActivity) {

            TextView toolbar_title = c.findViewById(R.id.toolbar_title);
            toolbar_title.setText(s);

            ImageView menu_portfolio = c.findViewById(R.id.menu_portfolio);

            TextView tvmenu_portfolio = c.findViewById(R.id.tvmenu_portfolio);
            menu_portfolio.setColorFilter(ContextCompat.getColor(c, MyApplication.mshared.getBoolean(c.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
            tvmenu_portfolio.setTextColor(ContextCompat.getColor(c, MyApplication.mshared.getBoolean(c.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
        } else if (c instanceof OrdersActivity) {

            TextView toolbar_title = c.findViewById(R.id.toolbar_title);
            toolbar_title.setText(s);
            ImageView menu_orders = c.findViewById(R.id.menu_orders);

            TextView tvmenu_portfolio = c.findViewById(R.id.tvmenu_orders);
            menu_orders.setColorFilter(ContextCompat.getColor(c, MyApplication.mshared.getBoolean(c.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
            tvmenu_portfolio.setTextColor(ContextCompat.getColor(c, MyApplication.mshared.getBoolean(c.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
        } else if (c instanceof FavoritesActivity) {

            TextView toolbar_title = c.findViewById(R.id.toolbar_title);
            toolbar_title.setText(s);

            ImageView menu_stocks = c.findViewById(R.id.menu_stocks);

            TextView tvmenu_portfolio = c.findViewById(R.id.tvmenu_stocks);
//            menu_portfolio.setColorFilter(ContextCompat.getColor(c, R.color.colorDark));
//            tvmenu_portfolio.setTextColor(ContextCompat.getColor(c, R.color.colorDark));
            menu_stocks.setColorFilter(ContextCompat.getColor(c, MyApplication.mshared.getBoolean(c.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
            tvmenu_portfolio.setTextColor(ContextCompat.getColor(c, MyApplication.mshared.getBoolean(c.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));
        } else {

            try {
                TextView toolbar_title = c.findViewById(R.id.toolbar_title);
                toolbar_title.setText(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*else if (c instanceof MowaziHomeActivity) {
            ImageView menu_portfolio = (ImageView) c.findViewById(R.id.menu_mowazi);

            TextView tvmenu_portfolio = (TextView) c.findViewById(R.id.tvmenu_mowazi);
            menu_portfolio.setColorFilter(ContextCompat.getColor(c, R.color.colorDark));
            tvmenu_portfolio.setTextColor(ContextCompat.getColor(c, R.color.colorDark));
        }else if (c instanceof MoreActivity) {
            ImageView menu_portfolio = (ImageView) c.findViewById(R.id.menu_more);

            TextView tvmenu_portfolio = (TextView) c.findViewById(R.id.tvmenu_more);
            menu_portfolio.setColorFilter(ContextCompat.getColor(c, R.color.colorDark));
            tvmenu_portfolio.setTextColor(ContextCompat.getColor(c, R.color.colorDark));
        }*/

        try {
            TextView toolbar_status = c.findViewById(R.id.market_state_value_textview);
            TextView toolbar_time = c.findViewById(R.id.market_time_value_textview);
            if(BuildConfig.label.matches("tijari")) {
                if (MyApplication.marketStatus.getStatusDescriptionAr().equals("مفتوح") ||
                        MyApplication.marketStatus.getStatusDescriptionEn().equals("Open")) {
                    toolbar_status.setTextColor(ContextCompat.getColor(c, R.color.green_color));
                    toolbar_time.setTextColor(ContextCompat.getColor(c, R.color.green_color));
                }else {
                    toolbar_status.setTextColor(ContextCompat.getColor(c, R.color.white));
                    toolbar_time.setTextColor(ContextCompat.getColor(c, R.color.white));
                }

            }
            else {
                toolbar_status.setTextColor(ContextCompat.getColor(c, R.color.white));
                toolbar_time.setTextColor(ContextCompat.getColor(c, R.color.white));
            }
            LinearLayout toolbar_lineairLayoutStatus = c.findViewById(R.id.ll_market_state);
            setMarketStatus(toolbar_lineairLayoutStatus, toolbar_status, c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isMyServiceRunning(Context activity, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void stopAppService(Context activity) {
        try {
            activity.stopService(new Intent(activity, AppService.class));
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Actions.stopAppService", "error : " + e.getMessage());
        }
    }

    public static void setActivityTheme(Activity activity) {

        boolean theme = MyApplication.mshared.getBoolean(activity.getResources().getString(R.string.normal_theme), true);

        Log.wtf("theme normal", "? " + theme);

        activity.setTheme(MyApplication.mshared.getBoolean(activity.getResources().getString(R.string.normal_theme), true) ? R.style.AppTheme_NoActionBar : R.style.AppTheme_NoActionBar_Dark);

        if (!MyApplication.mshared.getBoolean(activity.getResources().getString(R.string.normal_theme), true))
            setBarColor(activity);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setBarColor(Activity act) {
        final int sdk = Build.VERSION.SDK_INT;
        if (sdk >= 21) {
            Window window = act.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            window.setStatusBarColor(act.getResources().getColor(R.color.colorDarkTheme));
        }
    }

    public static void InitializeMarketService(final Activity c) {
        try {

            View includedLayout = c.findViewById(R.id.my_toolbar);

            final TextView marketstatustxt = includedLayout.findViewById(R.id.market_state_value_textview);

            final LinearLayout llmarketstatus = includedLayout.findViewById(R.id.ll_market_state);

            LocalBroadcastManager.getInstance(c).registerReceiver(
                    new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String marketTime = intent.getExtras().getString(AppService.EXTRA_MARKET_TIME);
                            setMarketStatus(llmarketstatus, marketstatustxt, c);
                            Log.wtf("InitializeMarketService", "setMarketStatus: " + MyApplication.marketStatus.getStatusDescriptionAr());

                            if (marketTime != null) {
                                if (marketTime.equals(""))
                                    marketTime = MyApplication.marketStatus.getMarketTime();
                                setMarketTime(marketTime, c);
                                Log.wtf("InitializeMarketService", "setMarketTime: " + marketTime);
                            }
                        }
                    }, new IntentFilter(AppService.ACTION_MARKET_SERVICE)
            );

            Log.wtf("InitializeMarketService", "call from : " + c.getLocalClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void InitializeMarketServiceV2(final Activity c) {

        try {
            View includedLayout = c.findViewById(R.id.my_toolbar);

            final TextView marketstatustxt = includedLayout.findViewById(R.id.market_state_value_textview);
            final LinearLayout llmarketstatus = includedLayout.findViewById(R.id.ll_market_state);

            marketReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String marketTime = intent.getExtras().getString(AppService.EXTRA_MARKET_TIME);
                    setMarketStatus(llmarketstatus, marketstatustxt, c);
                    //Log.wtf("InitializeMarketServiceV2", "setMarketStatus: " + MyApplication.marketStatus.getStatusDescriptionAr());

                    if (marketTime != null) {
                        if (marketTime.equals(""))
                            marketTime = MyApplication.marketStatus.getMarketTime();
                        setMarketTime(marketTime, c);
                        Log.wtf("InitializeMarketServiceV2 2", "setMarketTime: " + marketTime);
                    }
                }
            };
            LocalBroadcastManager.getInstance(c).registerReceiver(marketReceiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));
            Log.wtf("InitializeMarketServiceV2", "call from : " + c.getLocalClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregisterMarketReceiver(final Activity c) {
        try {
            LocalBroadcastManager.getInstance(c).unregisterReceiver(marketReceiver);
            Log.wtf("unregisterMarketReceiver", "succeffull");
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("unregisterMarketReceiver", "error : " + e.getMessage());
        }
    }

    public static void InitializeSessionService(final Activity activity) {
        LocalBroadcastManager.getInstance(activity).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        boolean expired = intent.getExtras().getBoolean(AppService.EXTRA_SESSION, true);
//                        Log.wtf("expired", "is "+expired);
                        if (expired) {
                            stopAppService(activity);
                            showDialog(activity, activity.getResources().getString(R.string.loginExpired));
                        }
                    }
                }, new IntentFilter(AppService.ACTION_SESSION_SERVICE)
        );
    }

    public static void InitializeSessionServiceV2(final Activity activity) {

        try {
            sessionReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    boolean expired = intent.getExtras().getBoolean(AppService.EXTRA_SESSION, true);
                    if (expired) {
                        stopAppService(activity);
                        showDialog(activity, activity.getResources().getString(R.string.loginExpired));
                    }
                }
            };

            LocalBroadcastManager.getInstance(activity).registerReceiver(sessionReceiver, new IntentFilter(AppService.ACTION_SESSION_SERVICE));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void unregisterSessionReceiver(final Activity c) {
        try {
            LocalBroadcastManager.getInstance(c).unregisterReceiver(sessionReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isMarketOpen() {

        return MyApplication.marketStatus.getStatusID() == MyApplication.MARKET_OPEN;
    }

    public static void DownloadFile(String fileURL, File directory) {
        URL url;
        try {
            url = new URL(fileURL);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("portfolionumber", String.valueOf(MyApplication.currentUser.getPortfolioNumber()));
            connection.setRequestProperty("key", MyApplication.currentUser.getKey());
            connection.setRequestProperty("Content-Type", "application/pdf");
            connection.connect();
            InputStream input = new BufferedInputStream(
                    connection.getInputStream());
            OutputStream output = new FileOutputStream(directory);

            byte[] data = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
            connection.disconnect();
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.wtf("exception 1", e.getMessage());
        }

    }

    public static void setMarketTime(final String marketTimestring, Activity c) {

        View includedLayout = c.findViewById(R.id.my_toolbar);

        final TextView markettime = includedLayout.findViewById(R.id.market_time_value_textview);
        try {
            final Date date = AppService.marketDateFormat.parse(marketTimestring);

            CountDownTimer t = new CountDownTimer(Long.MAX_VALUE, 1000) {
                public int cnt = 500;

                @Override
                public void onTick(long millisUntilFinished) {
                    try {
                        date.setTime(date.getTime() + 1000);
                        //   markettime.setText(dateFormat.format(date));
                        // MyApplication.marketStatus.setMarketTime(dateFormat.format(date));

                        String ss = AppService.marketSetDateFormat.format(date); //marketSetDateFormat
                        //Log.wtf("setMarketTime : AppService.marketSetDateFormat.format(date)", "= " + ss);
                        markettime.setText(ss /*AppService.marketSetDateFormat.format(date)*/);
                        markettime.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
                        started = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.wtf("setMarketTime setText error", "e: " + e.getMessage());
                    }
                }

                @Override
                public void onFinish() {

                }
            };
            if (!started || markettime.getText().toString().equals(""))
                t.start();
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("setMarketTime error", "e: " + e.getMessage());
        }

    }

    public static void setMarketStatus(LinearLayout ll, TextView v, Context c) {

        v.setText(MyApplication.lang == MyApplication.ARABIC ? MyApplication.marketStatus.getStatusDescriptionAr() : MyApplication.marketStatus.getStatusDescriptionEn());




        /*if (MyApplication.marketStatus.getStatusID() == MyApplication.MARKET_OPEN) {

            v.setBackground(ContextCompat.getDrawable(c, R.drawable.open_market_status));

        } else if (MyApplication.marketStatus.getStatusID() == MyApplication.MARKET_CLOSED) {

            v.setBackground(ContextCompat.getDrawable(c, R.drawable.closed_market_status));
        } else {

            v.setBackground(ContextCompat.getDrawable(c, R.drawable.other_market_status));
        }*/

        if (ll != null && c instanceof MarketIndexActivity) {
            if (MyApplication.marketStatus.getStatusDescriptionAr().equals("مفتوح") ||
                    MyApplication.marketStatus.getStatusDescriptionEn().equals("Open")) {

                if(BuildConfig.label.matches("tijari")){
                   ll.setBackground(ContextCompat.getDrawable(c, R.drawable.white_open_market));
                   v.setTextColor(ContextCompat.getColor(c, R.color.green_color));
                }
                else {
                    ll.setBackground(ContextCompat.getDrawable(c, R.drawable.open_market_status));
                }
            } else if (MyApplication.marketStatus.getStatusDescriptionAr().equals("مغلق") ||
                    MyApplication.marketStatus.getStatusDescriptionEn().equals("Closed")) {

                ll.setBackground(ContextCompat.getDrawable(c, R.drawable.closed_market_status));
            } else {

                ll.setBackground(ContextCompat.getDrawable(c, R.drawable.other_market_status));
            }
        }
/*
        try {
            if (MyApplication.marketStatus.getStatusDescriptionAr().equals("مفتوح") ||
                    MyApplication.marketStatus.getStatusDescriptionEn().equals("Open")) {

                v.setBackground(ContextCompat.getDrawable(c, R.drawable.open_market_status));
            } else if (MyApplication.marketStatus.getStatusDescriptionAr().equals("مغلق") ||
                    MyApplication.marketStatus.getStatusDescriptionEn().equals("Closed")) {

                v.setBackground(ContextCompat.getDrawable(c, R.drawable.closed_market_status));
            } else {

                v.setBackground(ContextCompat.getDrawable(c, R.drawable.other_market_status));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       try{ v.setBackgroundColor(Color.TRANSPARENT);}catch (Exception e){}
    }

    public static void startActivity(Activity c, Class to, boolean finish) {
        Intent i = new Intent();
        i.setClass(c, to);
        c.startActivity(i);
        if (finish)
            c.finishAffinity();
    }

    public static String GetUniqueID(Context c) {
        String android_id = Settings.Secure.getString(c.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static void CreateDialog(final Activity c, String message, final boolean finish, boolean cancel) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(c, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(c.getString(R.string.confirm), (dialog, id) -> {
                    dialog.cancel();
                    if (finish)
                        c.finish();
                });
        if (cancel)
            builder.setNegativeButton(c.getString(R.string.confirm), (dialog, id) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }

    private static void returnToLogin(Activity activity) {

        MyApplication.IsTimeSaleLoginRetreived=false;
        SqliteDb_TimeSales timeSales_DB = new SqliteDb_TimeSales(activity);
        timeSales_DB.open();
        timeSales_DB.deleteTimeSales();
        timeSales_DB.close();

        MyApplication.timeSales=new ArrayList<>();

        MyApplication.threadPoolExecutor = null;
        MyApplication.threadPoolExecutor = new ThreadPoolExecutor(MyApplication.corePoolSize, MyApplication.maximumPoolSize,
                MyApplication.keepAliveTime, TimeUnit.SECONDS, MyApplication.workQueue);

        Intent i = new Intent(activity, LoginFingerPrintActivity.class);
        activity.startActivity(i);
        activity.finish();
    }

    public static void logout(final Activity activity) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setMessage(activity.getString(R.string.logout_app_question))
                .setCancelable(true)
                .setNegativeButton(activity.getResources().getString(R.string.cancel),
                        (dialog, id) -> dialog.cancel())
                .setPositiveButton(activity.getResources().getString(R.string.confirm),
                        (dialog, id) -> {
                            dialog.cancel();
                            stopAppService(activity);
                            returnToLogin(activity);
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public static void showDialog(final Activity activity, String msg) {//}, final boolean finishActivity, final boolean loadLogin) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(activity, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setMessage(msg);

        builder.setCancelable(false)
                .setPositiveButton(
                        activity.getString(R.string.save),
                        (dialog, id) -> {
                            dialog.dismiss();

                            returnToLogin(activity);
                        });

        AlertDialog alert = builder.create();

        try {
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void overrideFonts(Context context, final View v, boolean isMowazi) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child, isMowazi);
                }
            } else if (v instanceof TextView) {
                if (lang == MyApplication.ARABIC) {
                    /*if (isMowazi){

                        ((TextView) v).setTypeface(MyApplication.droidregular);
                    }else{

                        ((TextView) v).setTypeface(MyApplication.droidbold);
                    }*/
                    ((TextView) v).setTypeface(MyApplication.droidregular);
                } else {
                    if (isMowazi) {

                        ((TextView) v).setTypeface(MyApplication.opensansregular);
                    } else {

                        ((TextView) v).setTypeface(MyApplication.giloryItaly);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTypeface(TextView[] views, Typeface typeface) {

        for (int i = 0; i < views.length; i++) {
            views[i].setTypeface(typeface);
        }
    }

    public static String GetVersionCode(Activity c) {
        String code = "";
        PackageInfo pInfo;
        try {
            pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            code = pInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static String MD5(String pass) {
        MessageDigest md;
        String result = "";
        try {
            // md = MessageDigest.getInstance("MD5");
            // md=MessageDigest.getInstance("SHA-512");
            md = MessageDigest.getInstance("MD5");
            md.update(pass.getBytes());
            byte[] byteData = md.digest();
            md.reset();

            // convert the byte to hex format
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                String hex = Integer.toHexString(0xff & byteData[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
                result = hexString.toString();
            }
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public static String getRandom() {
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        String val = "" + randomPIN;
        return val;
    }

    public static int getVersionNumber(Activity c) {
        PackageInfo pInfo;
        int version = -1;
        try {
            pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return version;
    }

    public static int CheckVersion(Activity c, String newVersion, boolean force) {

        int needUrgentUpdate = -1;

        try {
            PackageInfo pInfo;
            try {
                pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
                double version = Double.parseDouble(pInfo.versionName);

                try {

                    if (Double.parseDouble(newVersion) > version) {
                        if (force) {

                            needUrgentUpdate = 2;
                        } else {
                            needUrgentUpdate = 1;
                        }
                    } else {
                        //continue to app
                        needUrgentUpdate = 0;

                    }
                } catch (Exception e) {
                    Log.d("eee", "" + e);

                }
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return needUrgentUpdate;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android:" + sdkVersion + " (" + release + ")";
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static boolean isReachable() {

        thread = new Thread(() -> {
            try {

                if (!thread.isInterrupted()) {
                    result = hasActiveInternetConnection();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        return result;
    }


    public static void stopThread() {

        thread.interrupt();
    }


    private static boolean hasActiveInternetConnection() {

        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL(MyApplication.baseLink).openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();

            return (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }


    public static int returnDurationIndex(int durationId) {

        int index = -1;

        for (int i = 0; i < MyApplication.allOrderDurationType.size(); i++) {

            OrderDurationType orderDurationType = MyApplication.allOrderDurationType.get(i);
            if (durationId == orderDurationType.getID()) {
                index = i;
                break;
            }
        }

        return index;
    }


    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void checkAppService(Activity act) {

        try {
            if (!isMyServiceRunning(act, AppService.class)) {

                Log.wtf("===========reinitialized==========", "service");
                Intent intent = new Intent(act, AppService.class);
                act.startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showHideFooter(Activity act) {


        try {
            LinearLayout footer = act.findViewById(R.id.footer);
            RelativeLayout rlTickers = act.findViewById(R.id.rlTickers);

            if (BuildConfig.GoToMenu) {

                footer.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rlTickers.setLayoutParams(params);

            } else {

                footer.setVisibility(View.VISIBLE);
            }

            //LinearLayout llMowazi = footer.findViewById(R.id.llMowazi);
            //llMowazi.setVisibility(MyApplication.showMowazi ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void checkLanguage(final Activity act, final boolean started) {

        LocalBroadcastManager.getInstance(act).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {

                        if (started) {
                            act.recreate();
                        }
                    }
                }, new IntentFilter(MyApplication.class.getName() + "ChangedLanguage")
        );
    }


    public static void loadFooter(Activity context, View v) {

        String actName = context.getClass().getSimpleName();

        switch (v.getId()) {

            case R.id.llMarketIndex:

                if (!actName.equals("MarketIndexActivity"))
                    startActivity(context, MarketIndexActivity.class, true);
                break;

            case R.id.llPortfolio:
                if (!actName.equals("PortfolioActivity")) {

                    //startActivity(context, PortfolioActivity.class, true);


                    Intent i = new Intent();
                    i.setClass(context, PortfolioActivity.class);
                    i.putExtra("fromFooter", true);
                    context.startActivity(i);
                    context.finishAffinity();
                }
                break;

            case R.id.llOrders:

                if (!actName.equals("OrdersActivity"))
                    startActivity(context, OrdersActivity.class, true);
                break;

            case R.id.llFavorites:

                if (!actName.equals("FavoritesActivity"))
                    startActivity(context, FavoritesActivity.class, true);
                break;

            case R.id.llMowazi:
                startActivity(context, MowaziHomeActivity.class, true);
                break;

            case R.id.llMore:

                if (!actName.equals("MoreActivity"))
                    startActivity(context, MoreActivity.class, true);
                break;
        }
    }


    public static void goTo(Activity context, View v) {

        switch (v.getId()) {

            case R.id.rlStocks:
                startActivity(context, StockActivity.class, false);
                break;

            case R.id.rlIslamicStocks:
                Intent i = new Intent();
                i.setClass(context, StockActivity.class);
                i.putExtra("isIslamicStocks", true);
                context.startActivity(i);
                break;

            case R.id.rlOffMarketQuotes:
                startActivity(context, MarketQuotes.class, false);
                break;

            case R.id.rlMowazi:
                startActivity(context, MowaziHomeActivity.class, false);
                break;

            case R.id.rlSectors:
                startActivity(context, SectorsActivity.class, false);
                break;

            case R.id.rlTops:

                try {
                    //context.startActivity(new Intent(context, TopsPagerActivity.class));
                    context.startActivity(new Intent(context, TopsActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            case R.id.rlNews:
                startActivity(context, NewsActivity.class, false);
                break;

            case R.id.rlLinks:
                startActivity(context, QuickLinksActivity.class, false);
                break;

            case R.id.rlRegister:
                try {
                    context.startActivity(new Intent(context, SiteMapDataActivity.class).putExtra("url", "url").putExtra("websiteContentId", -1));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.rlFavorites:
                startActivity(context, FavoritesActivity.class, false);
                break;

            case R.id.rlChangePassword:
                try {
//                    context.startActivity(new Intent(context, ChangePasswordActivity.class).putExtra("url", "url").putExtra("websiteContentId", -1));
                    context.startActivity(new Intent(context, ChangePasswordActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.rlSettings:
                try {
                    context.startActivity(new Intent(context, SettingsActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    public static void reloadApp(Activity context) {




        stopAppService(context);
        Intent mStartActivity = new Intent(context, SplashActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }


    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }


    public static void checkSession(Activity context) {
        if (MyApplication.sessionOut != null) {

            Log.wtf("onResume", "date = " + AppService.marketDateFormat.format(MyApplication.sessionOut.getTime()));

            long diffMin = (Calendar.getInstance().getTimeInMillis() - MyApplication.sessionOut.getTimeInMillis()); //result in millis
            Log.wtf("onResume", "diff = " + (diffMin));

            Log.wtf("aaa ", "bbb " + (diffMin / 60000.0));
            Log.wtf("aaa ", "aa " + ((diffMin / 60000) > MyApplication.Session_Out_Period));
            if (((diffMin / 60000) > MyApplication.Session_Out_Period) || MyApplication.marketStatus.isSessionChanged()) { // || MyApplication.webItems.isEmpty()

                Log.wtf("WILL", "RELOAAAAD");
                Actions.reloadApp(context);
            }
            MyApplication.sessionOut = null;
        } else {
            Log.wtf("onResume ", "date == null");
        }
    }


    public static BrokerageFee getBrokerageFeeByInstrumentID(ArrayList<BrokerageFee> allBrokerageFees, String instrumentID) {

        BrokerageFee brokerageFee = new BrokerageFee();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            brokerageFee = allBrokerageFees.stream()
                    .filter(s -> instrumentID.equals(s.getInstrumentId()))
                    .findAny()
                    .orElse(new BrokerageFee());
        } else {

            for (int i = 0; i < allBrokerageFees.size(); i++) {

                if (allBrokerageFees.get(i).getInstrumentId().equals(instrumentID)) {

                    brokerageFee = allBrokerageFees.get(i);
                    break;
                }
            }
        }

        return brokerageFee;

    }


    public static StockQuotation getStockQuotationById(ArrayList<StockQuotation> stockQuotations, int stockID) {

        StockQuotation stockQuotation;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            stockQuotation = stockQuotations.stream()
                    .filter(s -> stockID == s.getStockID())
                    .findAny()
                    .orElse(new StockQuotation());
        } else {

            stockQuotation = new StockQuotation();

            for (int i = 0; i < stockQuotations.size(); i++) {

                if (stockQuotations.get(i).getStockID() == stockID) {

                    stockQuotation = stockQuotations.get(i);
                    break;
                }
            }
        }
        return stockQuotation;
    }


    public static ArrayList<Stock> getStocksTopsByType(ArrayList<Stock> allStocks, int type) {

        ArrayList<Stock> filteredList = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            List<Stock> filteredByType;
            filteredByType = allStocks.stream()
                    .filter(s -> type == s.getTopType())
                    .collect(Collectors.toList());

            filteredList.addAll(filteredByType);
        } else {

            for (int i = 0; i < allStocks.size(); i++) {

                if (allStocks.get(i).getTopType() == type)
                    filteredList.add(allStocks.get(i));
            }
        }
        return filteredList;
    }


    public static ArrayList<StockQuotation> getStocksByIds(ArrayList<StockQuotation> stockQuotations, ArrayList<Integer> stocksIds) {

        ArrayList<StockQuotation> returnedList = new ArrayList<>();

        if (stocksIds.size() == 0)
            return returnedList;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            for (int i = 0; i < stocksIds.size(); i++) {

                returnedList.add(getStockQuotationById(stockQuotations, stocksIds.get(i)));
            }
        } else {

            for (int i = 0; i < stocksIds.size(); i++) {

                int stockId = stocksIds.get(i);

                for (int j = 0; j < stockQuotations.size(); j++) {

                    if (stockId == stockQuotations.get(j).getStockID())
                        returnedList.add(stockQuotations.get(j));
                }
            }

        }
        return returnedList;
    }


    public static ArrayList<StockQuotation> filterStocksByInstrumentID(ArrayList<StockQuotation> stocksList, String instrumentId) {

        if (instrumentId.length() == 0) return stocksList;

        ArrayList<StockQuotation> filteredList = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            List<StockQuotation> filteredByInstrument;
            filteredByInstrument = stocksList.stream()
                    .filter(s -> instrumentId.equals(s.getInstrumentId()))
                    .collect(Collectors.toList());

            filteredList.addAll(filteredByInstrument);
        } else {

            for (int i = 0; i < stocksList.size(); i++) {

                if (stocksList.get(i).getInstrumentId().equals(instrumentId))
                    filteredList.add(stocksList.get(i));
            }
        }

        return filteredList;
    }


    public static ArrayList<StockQuotation> filterStocksByInstruments(ArrayList<StockQuotation> stocksList, ArrayList<Instrument> instruments) {

        ArrayList<StockQuotation> filteredList = new ArrayList<>();
        String[] instrumentIds = new String[instruments.size()];

        for (int i = 0; i < instruments.size(); i++) {
            instrumentIds[i] = (instruments.get(i).getInstrumentCode());
        }

        if (instrumentIds.length != 0) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                List<StockQuotation> filteredByInstrument;
                filteredByInstrument = stocksList.stream()
                        .filter(s -> Arrays.asList(instrumentIds).contains(s.getInstrumentId()))
                        .collect(Collectors.toList());

                filteredList.addAll(filteredByInstrument);
            } else {

                for (int i = 0; i < stocksList.size(); i++) {

                    if (Arrays.asList(instrumentIds).contains(stocksList.get(i).getInstrumentId()))
                        filteredList.add(stocksList.get(i));
                }
            }
        }

        return filteredList;
    }


    public static ArrayList<Stock> filterTopsByInstruments(ArrayList<Stock> stocksList, ArrayList<Instrument> instruments) {

        ArrayList<Stock> filteredList = new ArrayList<>();
        String[] instrumentIds = new String[instruments.size()];

        for (int i = 0; i < instruments.size(); i++) {
            instrumentIds[i] = (instruments.get(i).getInstrumentCode());
        }

        if (instrumentIds.length != 0) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

                List<Stock> filteredByInstrument;
                filteredByInstrument = stocksList.stream()
                        .filter(s -> Arrays.asList(instrumentIds).contains(s.getInstrumentId()))
                        .collect(Collectors.toList());

                filteredList.addAll(filteredByInstrument);
            } else {

                for (int i = 0; i < stocksList.size(); i++) {

                    if (Arrays.asList(instrumentIds).contains(stocksList.get(i).getInstrumentId()))
                        filteredList.add(stocksList.get(i));
                }
            }
        }

        return filteredList;
    }












    /*public static ArrayList<StockQuotation> filterStocksByMarketSegmentID(ArrayList<StockQuotation> stocksList, int marketSegmentId) {

        //if (marketId == 0) return stocksList;

        ArrayList<StockQuotation> filteredList = new ArrayList<>();

        int marketId = 0;
        for (int j=0; j<MyApplication.instruments.size(); j++){
            if(MyApplication.instruments.get(j).getMarketSegmentID() == marketSegmentId){
                marketId = MyApplication.instruments.get(j).getMarketID();
            }
        }

        int finalMarketId = marketId;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            List<StockQuotation> filteredByInstrument;
            filteredByInstrument = stocksList.stream()
                    .filter(s -> s.getMarketId() == finalMarketId)
                    .collect(Collectors.toList());

            filteredList.addAll(filteredByInstrument);
        } else {

            for (int i = 0; i < stocksList.size(); i++) {

                if (stocksList.get(i).getMarketId() == finalMarketId)
                    filteredList.add(stocksList.get(i));
            }
        }

        return filteredList;
    }*/


    public static ArrayList<StockQuotation> filterStocksByIsIslamic(ArrayList<StockQuotation> stocksList) {

        ArrayList<StockQuotation> filteredList = new ArrayList<>();
        Log.wtf("filterStocksByIsIslamic", "stocksList.size = " + stocksList.size());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            List<StockQuotation> filteredByIslamic;
            filteredByIslamic = stocksList.stream()
                    .filter(s -> s.islamic())
                    .collect(Collectors.toList());
            filteredList.addAll(filteredByIslamic);
        } else {

            for (int i = 0; i < stocksList.size(); i++) {

                if (stocksList.get(i).islamic())
                    filteredList.add(stocksList.get(i));
            }
        }

        Log.wtf("filterStocksByIsIslamic", "filteredList.size = " + filteredList.size());
        return filteredList;
    }


    public static ArrayList<Instrument> filterInstrumentsByMarketSegmentID(ArrayList<Instrument> instrumentList, int marketSegmentID) {

        ArrayList<Instrument> filteredList = new ArrayList<>();
        Log.wtf("filterInstrumentsByMarketSegmentID", "instrumentList.size = " + instrumentList.size());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            List<Instrument> filterInstrumentsByMarketSegmentID;

            filterInstrumentsByMarketSegmentID = instrumentList.stream()
                    .filter(s -> s.getMarketSegmentID() == marketSegmentID)
                    .collect(Collectors.toList());
            filteredList.addAll(filterInstrumentsByMarketSegmentID);

        } else {

            for (int i = 0; i < instrumentList.size(); i++) {

                if (instrumentList.get(i).getMarketSegmentID() == marketSegmentID)
                    filteredList.add(instrumentList.get(i));
            }
        }

        Log.wtf("filterInstrumentsByMarketSegmentID", "filteredList.size = " + filteredList.size());
        return filteredList;
    }


    public static ArrayList<StockQuotation> filterStocksBySectorAndInstrumentID(ArrayList<StockQuotation> stocksList, String instrumentId, String sectorId) {

        ArrayList<StockQuotation> filteredList = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            //<editor-fold desc="filter for devices greater or equal to N">
            //<editor-fold desc="filter by instrument">
            List<StockQuotation> filteredByInstrument = new ArrayList<>();
            if (instrumentId.length() == 0) {

                filteredByInstrument.addAll(MyApplication.stockQuotations);
            } else {

                filteredByInstrument = stocksList.stream()
                        .filter(s -> instrumentId.equals(s.getInstrumentId()))
                        .collect(Collectors.toList());
            }
            //</editor-fold>

            //<editor-fold desc="filter by sector Id">
            List<StockQuotation> filteredLists = new ArrayList<>();
            filteredLists = filteredByInstrument.stream()
                    .filter(s -> sectorId.equals(s.getSectorID()))
                    .collect(Collectors.toList());
            //</editor-fold>

            filteredList.addAll(filteredLists);
            //</editor-fold>

        } else {

            //<editor-fold desc="filter for devices less than N">
            if (instrumentId.length() == 0) { //check sector only


                for (int i = 0; i < stocksList.size(); i++) {

                    if (sectorId.equals(stocksList.get(i).getSectorID())) {

                        filteredList.add(stocksList.get(i));
                    }
                }

            } else { //check sector and instrument


                for (int i = 0; i < stocksList.size(); i++) {

                    if (stocksList.get(i).getInstrumentId().equals(instrumentId) && stocksList.get(i).getSectorID().equals(sectorId)) {

                        filteredList.add(stocksList.get(i));
                    }
                }
            }
            //</editor-fold>
        }
        return filteredList;
    }


    public static ArrayList<TimeSale> filterTimeSalesByInstrumentIDAndStockID(ArrayList<TimeSale> timeSales, int stockID, String instrumentId) {

        ArrayList<TimeSale> filteredList = new ArrayList<>();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            if (stockID == 0) { //all stocks

                //<editor-fold desc="filter by instrument">
                List<TimeSale> filteredByInstrument = new ArrayList<>();
                if (instrumentId.length() == 0) { //get all stocks

                    filteredByInstrument.addAll(timeSales);
                } else { //get by instrument

                    filteredByInstrument = timeSales.stream()
                            .filter(s -> instrumentId.equals(s.getInstrumentId()))
                            .collect(Collectors.toList());
                }
                //</editor-fold>

                filteredList.addAll(filteredByInstrument);

            } else {

                //<editor-fold desc="filter by stock Id">
                List<TimeSale> filteredLists;
                filteredLists = timeSales.stream()
                        .filter(s -> stockID == s.getStockID())
                        .collect(Collectors.toList());
                //</editor-fold>

                //<editor-fold desc="filter by instrument">
                List<TimeSale> filteredByInstrument = new ArrayList<>();
                if (instrumentId.length() == 0) {

                    filteredByInstrument.addAll(filteredLists);
                } else {

                    filteredByInstrument = filteredLists.stream()
                            .filter(s -> instrumentId.equals(s.getInstrumentId()))
                            .collect(Collectors.toList());
                }
                //</editor-fold>

                filteredList.addAll(filteredByInstrument);
            }


            return removeDuplicateTimeSales(filteredList);

        } else {

            if (stockID == 0) { //all stocks

                if (instrumentId.length() == 0) {
                    filteredList.addAll(timeSales);
                    return removeDuplicateTimeSales(filteredList);
                } else {

                    for (int i = 0; i < timeSales.size(); i++) {

                        if (timeSales.get(i).getInstrumentId().equals(instrumentId))
                            filteredList.add(timeSales.get(i));
                    }
                }

            } else { //specific stock

                if (instrumentId.length() == 0) { //check stock id

                    for (int i = 0; i < timeSales.size(); i++) {

                        if (timeSales.get(i).getStockID() == stockID)
                            filteredList.add(timeSales.get(i));
                    }
                } else {//check stock and instrument ids

                    for (int i = 0; i < timeSales.size(); i++) {

                        if (timeSales.get(i).getInstrumentId().equals(instrumentId) && stockID == timeSales.get(i).getStockID())
                            filteredList.add(timeSales.get(i));
                    }
                }
            }
            return removeDuplicateTimeSales(filteredList);
        }

        //<editor-fold desc="loop way">
        /*if (stockID == 0) { //all stocks

            if (instrumentId.length() == 0) {
                filteredList.addAll(timeSales);
                return filteredList;
            } else {

                for (int i = 0; i < timeSales.size(); i++) {

                    if (timeSales.get(i).getInstrumentId().equals(instrumentId))
                        filteredList.add(timeSales.get(i));
                }
            }

        } else { //specific stock

            if (instrumentId.length() == 0) { //check stock id

                for (int i = 0; i < timeSales.size(); i++) {

                    if (timeSales.get(i).getStockID() == stockID)
                        filteredList.add(timeSales.get(i));
                }
            } else {//check stock and instrument ids

                for (int i = 0; i < timeSales.size(); i++) {

                    if (timeSales.get(i).getInstrumentId().equals(instrumentId) && stockID == timeSales.get(i).getStockID())
                        filteredList.add(timeSales.get(i));
                }
            }

        }
        return filteredList;*/
        //</editor-fold>
    }





    public static ArrayList<Stock> filterTopsStockByInstrumentID(ArrayList<Stock> timeSales,  String instrumentId) {

        ArrayList<Stock> filteredList = new ArrayList<>();


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {


            //<editor-fold desc="filter by instrument">
            List<Stock> filteredByInstrument = new ArrayList<>();
            if (instrumentId.length() == 0) { //get all stocks

                filteredByInstrument.addAll(timeSales);
            } else { //get by instrument

                filteredByInstrument = timeSales.stream()
                        .filter(s -> instrumentId.equals(s.getInstrumentId()))
                        .collect(Collectors.toList());
            }
            //</editor-fold>

            filteredList.addAll(filteredByInstrument);




            return filteredList;

        } else {



            if (instrumentId.length() == 0) {
                filteredList.addAll(timeSales);
                return filteredList;
            } else {

                for (int i = 0; i < timeSales.size(); i++) {

                    if (timeSales.get(i).getInstrumentId().equals(instrumentId))
                        filteredList.add(timeSales.get(i));
                }
            }


            return filteredList;
        }


    }






    public static ArrayList<TimeSale> filterTimeSalesByInstrumentsAndStockID(ArrayList<TimeSale> timeSales, int stockID, ArrayList<Instrument> instruments) {

        ArrayList<TimeSale> filteredList = new ArrayList<>();
        String[] instrumentIds = new String[instruments.size()];

        for (int i = 0; i < instruments.size(); i++) {
            instrumentIds[i] = (instruments.get(i).getInstrumentCode());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            if (stockID == 0) { //all stocks

                //<editor-fold desc="filter by instrument">
                List<TimeSale> filteredByInstrument = new ArrayList<>();
                if (instrumentIds.length == 0) { //get all stocks

                    //filteredByInstrument.addAll(timeSales);
                } else { //get by instrument

                    filteredByInstrument = timeSales.stream()
                            .filter(s -> Arrays.asList(instrumentIds).contains(s.getInstrumentId()))
                            .collect(Collectors.toList());
                }
                //</editor-fold>

                filteredList.addAll(filteredByInstrument);

            } else {

                //<editor-fold desc="filter by stock Id">
                List<TimeSale> filteredLists;
                filteredLists = timeSales.stream()
                        .filter(s -> stockID == s.getStockID())
                        .collect(Collectors.toList());
                //</editor-fold>

                //<editor-fold desc="filter by instrument">
                List<TimeSale> filteredByInstrument = new ArrayList<>();
                if (instrumentIds.length == 0) {

                    //filteredByInstrument.addAll(filteredLists);
                } else {

                    filteredByInstrument = filteredLists.stream()
                            .filter(s -> Arrays.asList(instrumentIds).contains(s.getInstrumentId()))
                            .collect(Collectors.toList());
                }
                //</editor-fold>

                filteredList.addAll(filteredByInstrument);
            }


            Log.wtf("filterTimeSalesByInstrumentsAndStockID", "filteredList size = " + filteredList.size());
            return removeDuplicateTimeSales(filteredList);

        } else {

            if (stockID == 0) { //all stocks

                if (instrumentIds.length == 0) {
                    //filteredList.addAll(timeSales);
                    return  removeDuplicateTimeSales(filteredList);
                } else {

                    for (int i = 0; i < timeSales.size(); i++) {

                        if (Arrays.asList(instrumentIds).contains(timeSales.get(i).getInstrumentId()))
                            filteredList.add(timeSales.get(i));
                    }
                }

            } else { //specific stock

                if (instrumentIds.length == 0) { //check stock id

                    /*for (int i = 0; i < timeSales.size(); i++) {

                        if (timeSales.get(i).getStockID() == stockID)
                            filteredList.add(timeSales.get(i));
                    }*/
                } else {//check stock and instrument ids

                    for (int i = 0; i < timeSales.size(); i++) {

                        if (Arrays.asList(instrumentIds).contains(timeSales.get(i).getInstrumentId()) && stockID == timeSales.get(i).getStockID())
                            filteredList.add(timeSales.get(i));
                    }
                }
            }




            Log.wtf("filterTimeSalesByInstrumentsAndStockID", "filteredList size = " + filteredList.size());


            return  removeDuplicateTimeSales(filteredList);
        }




        //<editor-fold desc="loop way">
        /*if (stockID == 0) { //all stocks

            if (instrumentId.length() == 0) {
                filteredList.addAll(timeSales);
                return filteredList;
            } else {

                for (int i = 0; i < timeSales.size(); i++) {

                    if (timeSales.get(i).getInstrumentId().equals(instrumentId))
                        filteredList.add(timeSales.get(i));
                }
            }

        } else { //specific stock

            if (instrumentId.length() == 0) { //check stock id

                for (int i = 0; i < timeSales.size(); i++) {

                    if (timeSales.get(i).getStockID() == stockID)
                        filteredList.add(timeSales.get(i));
                }
            } else {//check stock and instrument ids

                for (int i = 0; i < timeSales.size(); i++) {

                    if (timeSales.get(i).getInstrumentId().equals(instrumentId) && stockID == timeSales.get(i).getStockID())
                        filteredList.add(timeSales.get(i));
                }
            }

        }
        return filteredList;*/
        //</editor-fold>
    }







    public static  ArrayList<TimeSale>  removeDuplicateTimeSales(ArrayList<TimeSale> oldArray){
        ArrayList<TimeSale> filteredArray=new ArrayList<>();

        try {
            for (int i = 0; i < oldArray.size(); i++) {
                if (!filteredArray.contains(oldArray.get(i)))
                    filteredArray.add(oldArray.get(i));

            }
            Log.wtf("test_remove_duplicate","new Array");
            return filteredArray;
        }catch (Exception e){
            Log.wtf("test_remove_duplicate","old Array"+e.toString());
            return oldArray;
        }

    }



    public static ArrayList<Stock> filterTopsStockByInstruments(ArrayList<Stock> topStocks, ArrayList<Instrument> instruments) {

        ArrayList<Stock> filteredList = new ArrayList<>();
        String[] instrumentIds = new String[instruments.size()];

        for (int i = 0; i < instruments.size(); i++) {
            instrumentIds[i] = (instruments.get(i).getInstrumentCode());
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {

            //<editor-fold desc="filter by instrument">
            List<Stock> filteredByInstrument = new ArrayList<>();
            if (instrumentIds.length == 0) { //get all stocks

                //filteredByInstrument.addAll(timeSales);
            } else { //get by instrument

                filteredByInstrument = topStocks.stream()
                        .filter(s -> Arrays.asList(instrumentIds).contains(s.getInstrumentId()))
                        .collect(Collectors.toList());
            }
            //</editor-fold>

            filteredList.addAll(filteredByInstrument);



            Log.wtf("filterTimeSalesByInstrumentsAndStockID", "filteredList size = " + filteredList.size());
            return filteredList;

        } else {



            if (instrumentIds.length == 0) {
                //filteredList.addAll(timeSales);
                return filteredList;
            } else {

                for (int i = 0; i < topStocks.size(); i++) {

                    if (Arrays.asList(instrumentIds).contains(topStocks.get(i).getInstrumentId()))
                        filteredList.add(topStocks.get(i));
                }
            }


            Log.wtf("filterTimeSalesByInstrumentsAndStockID", "filteredList size = " + filteredList.size());
            return filteredList;
        }


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


    public static void setViewResizingListRow(View v, Activity c) {
        Typeface type = null;
        String lang="en";
        if (MyApplication.lang==MyApplication.ENGLISH) {
            lang="en";
            type = MyApplication.opensansregular;
        }
        else {
            lang="ar";
            type = MyApplication.droidregular;

        }
        MoaziViewResizing viewResizing = new MoaziViewResizing(MyApplication.screenWidth, type);
        MoaziViewResizing.setListRowTextResizing(v, c, lang);
    }


    public static void setViewResizing(Activity c) {
        Typeface type = null;
        String lang="en";
        if (MyApplication.lang==MyApplication.ENGLISH) {
            lang="en";
            type = MyApplication.opensansregular;
        }
        else {
            type = MyApplication.droidregular;
            lang="ar";
        }
        MoaziViewResizing viewResizing = new MoaziViewResizing(MyApplication.screenWidth, type);
        MoaziViewResizing.setPageTextResizing(c,lang);
        if (MyApplication.Pahse2)
            showFavorite(c);
    }



    public static void showFavorite(Activity activity) {
        try {
            ImageButton favo = activity.findViewById(R.id.favorite);
            ImageButton trade = activity.findViewById(R.id.trade);
            ImageButton order = activity.findViewById(R.id.order);
            favo.setVisibility(View.VISIBLE);

            if (!MyApplication.enablePlaceOrder){
                trade.setVisibility(View.GONE);
                order.setVisibility(View.GONE);
            }

            ImageButton marketWatch = activity.findViewById(R.id.marketWatch);
            marketWatch.setVisibility(MyApplication.isOTC? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void hideKeyboard(Activity c) {
        View view = c.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)c.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    public static boolean expired(SharedPreferences mshared) {
        boolean expired = false;
        try {
            String currentdate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
            String lastdate = mshared.getString("lastdate", "");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            Date date1 = dateFormat.parse(currentdate);
            Date date2 = dateFormat.parse(lastdate);

            long diff = date1.getTime() - date2.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            if (minutes > Integer.parseInt(mshared.getString("expiry", "0")))
                expired = true;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return expired;
    }




    public static final String getMD5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void generateSnack(Activity c, View v, String msg) {

        generateSnack(c,v,msg);
    }


}































































