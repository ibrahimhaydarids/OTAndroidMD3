package com.ids.fixot.activities.mowazi;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOrderDetailPopupActivity extends Activity {

    TextView company, bidcount, bidprice, askcount, askprice, bidquantity,
            askquantity;
    private DecimalFormat myFormatter;


    public MowaziOrderDetailPopupActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Actions.setLocal(MyApplication.lang, this);

        setContentView(R.layout.mowazi_order_detail_popup);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                new Locale("US_en"));
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", otherSymbols);
        myFormatter = new DecimalFormat("#,###", otherSymbols);
        company = findViewById(R.id.company);
        bidcount = findViewById(R.id.bidcount);

        bidprice = findViewById(R.id.bidprice);

        askcount = findViewById(R.id.askcount);
        askprice = findViewById(R.id.askprice);
        bidquantity = findViewById(R.id.bidquantity);
        askquantity = findViewById(R.id.askquantity);

        try {
            company.setText(getIntent().getExtras().getString("company"));
            bidcount.setText(getIntent().getExtras().getInt("bidcount") + "");
            double bidpricec = getIntent().getExtras().getDouble("bidprice");
            String bidpricetxt = df.format(bidpricec);
            askprice.setText(bidpricetxt + "");
            askcount.setText(getIntent().getExtras().getInt("askcount") + "");

            double askpricee = getIntent().getExtras().getDouble("askprice");
            String askpricetxt = df.format(askpricee);
            bidprice.setText(askpricetxt + "");

            int bidq = getIntent().getExtras().getInt("bidquantity");
            String bidqtxt = df.format(bidq);
            askquantity.setText(bidqtxt + "");

            int askq = getIntent().getExtras().getInt("askquantity");
            String askqtxt = df.format(askq);
            bidquantity.setText(askqtxt + "");

        } catch (Exception e) {
            Log.d("eeee", e + "");

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.sessionOut = Calendar.getInstance();
    }

    public void close(View v) {
        this.finish();
    }
}
