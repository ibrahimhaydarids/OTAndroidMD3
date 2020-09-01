package com.ids.fixot.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Window;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by DEV on 3/26/2018.
 */

public class ErrorAlertActivity extends Activity {

    String errorMessage;
    boolean serverLoss = false;
    AlertDialog connectionAlert;

    public ErrorAlertActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);


        if (getIntent().hasExtra("errorMessage")) {

            errorMessage = getIntent().getExtras().getString("errorMessage");
        } else {

            errorMessage = getString(R.string.no_net);
        }

        serverLoss = getIntent().hasExtra("serverLoss");

        try {
            AlertDialog.Builder builderalert = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom));
            builderalert
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNegativeButton(getString(android.R.string.ok), (dialog, id) -> {
                        connectionAlert.cancel();

                        if (!serverLoss) {
                            finish();
                        } else {

                            Actions.stopThread();
                            ErrorAlertActivity.this.stopService(new Intent(ErrorAlertActivity.this, AppService.class));

                            MyApplication.threadPoolExecutor = null;
                            MyApplication.threadPoolExecutor = new ThreadPoolExecutor(MyApplication.corePoolSize, MyApplication.maximumPoolSize,
                                    MyApplication.keepAliveTime, TimeUnit.SECONDS, MyApplication.workQueue);

                            Intent i = new Intent(ErrorAlertActivity.this, LoginFingerPrintActivity.class);
                            ErrorAlertActivity.this.startActivity(i);
                            ErrorAlertActivity.this.finish();
                        }
                    });
            connectionAlert = builderalert.create();
            connectionAlert.show();
        } catch (Exception e) {
            Log.wtf("ErrorAlertActivity", "error : " + e.getMessage());
        }

    }
}
