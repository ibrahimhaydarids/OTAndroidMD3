package com.ids.fixot.activities.mowazi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.LoginFingerPrintActivity;
import com.ids.fixot.adapters.mowaziAdapters.MowaziCompanySpinnerAutoCompleteAdaper;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziOnlineOrder;
import com.ids.fixot.parser.MowaziCompanyNameParser;

import org.json.JSONException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.kxml2.kdom.Element;
import org.kxml2.kdom.Node;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziPlaceOrderActivity extends AppCompatActivity {

    SharedPreferences mshared;
    String companyIdfromCompanies = "-1";
    TextView companyName;
    SharedPreferences.Editor edit;
    DecimalFormat myFormatter;
    String regex = "^\\-?(\\d{0,5}|\\d{0,5}\\.[5]{0,1})$";
    private LinearLayout llCompany, llOrderOptions, llActions, llActivity;
    private RelativeLayout spinnerLayout, rlConfirm, top;
    private String returnedDate = "", monthNext = "";
    private AutoCompleteTextView spinnerCompanies;
    private TextView tvPlaceOrder;
    private int orderType = -1, flag = -1, orderId = 0;
    private EditText etPriceFels, etPriceDinar, etQuantity, etDinarTotal,
            etDinarCommission, etDinarTotalPlusCommssion, etDate;
    private ImageButton btSell, btBuy, btConfirm, menu;
    private ImageButton btIncrement, btDecrement, btEdit, btRefresh, btDelete;
    private RadioButton rbCancel, rbDate;
    private MowaziOnlineOrder order;
    private int mYear, mMonth, mDay, quantity, companyId = -1;
    private ImageView ivDate;
    private ProgressBar pbOrders;
    private MowaziCompany selectedCompany = new MowaziCompany();
    private MowaziCompanySpinnerAutoCompleteAdaper spinnerAdapter;
    private ArrayList<MowaziCompany> allCompanies = new ArrayList<MowaziCompany>();
    private GetCompanies getCompanies;
    private DatePickerDialog datePickerDialog;
    private RelativeLayout main, root_Layout;
    private ImageButton back;
    private int footerButton;
    private ArrayList<MowaziCompany> companiesToTrade = new ArrayList<MowaziCompany>();


    public MowaziPlaceOrderActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        super.onCreate(savedInstanceState);

        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_mowazi_place_order);
        // MyApplication.logged = true;

        mshared = PreferenceManager
                .getDefaultSharedPreferences(MowaziPlaceOrderActivity.this);
        edit = mshared.edit();
        Intent intent = getIntent();
        if (intent.hasExtra("companyId")) {
            companyIdfromCompanies = getIntent().getExtras().getString(
                    "companyId");
            Log.d("COMPANY", "" + companyId);
        }

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                new Locale("US_en"));
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');

        myFormatter = new DecimalFormat("#.###", otherSymbols);
        root_Layout = (RelativeLayout) findViewById(R.id.root_Layout);
        spinnerLayout = (RelativeLayout) findViewById(R.id.spinnerLayout);
        llActivity = (LinearLayout) findViewById(R.id.llActivity);
        rlConfirm = (RelativeLayout) findViewById(R.id.rlConfirm);
        top = (RelativeLayout) findViewById(R.id.top);

        ImageButton menu = findViewById(R.id.menu);
        menu.setVisibility(View.GONE);

        companyName = (TextView) findViewById(R.id.companyName);
        llOrderOptions = (LinearLayout) findViewById(R.id.llOrderOptions);
        llCompany = (LinearLayout) findViewById(R.id.llCompany);
        pbOrders = (ProgressBar) findViewById(R.id.pbOrders);
        llActions = (LinearLayout) findViewById(R.id.llActions);
        back = (ImageButton) findViewById(R.id.back);
        tvPlaceOrder = (TextView) findViewById(R.id.tvPlaceOrder);

        spinnerCompanies = (AutoCompleteTextView) findViewById(R.id.spinnerCompanies);
        MowaziCompany c = new MowaziCompany();
        c.setSymbolAr("-- " + getString(R.string.mowazi_companies) + " --");
        c.setSymbolEn("-- " + getString(R.string.mowazi_companies) + " --");
        allCompanies.add(c);
        spinnerCompanies.setThreshold(1);

        spinnerCompanies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                selectedCompany = spinnerAdapter.getItem(position);
            }
        });

        Log.wtf("tag", "before spinner adapter");

        spinnerAdapter = new MowaziCompanySpinnerAutoCompleteAdaper(
                MowaziPlaceOrderActivity.this, R.layout.need_list_spinner_item,
                companiesToTrade);
        spinnerCompanies.setAdapter(spinnerAdapter);

        spinnerCompanies.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View arg0) {
                spinnerCompanies.showDropDown();
            }
        });

        Log.wtf("tag", "before etPriceFels");


        etPriceFels = (EditText) findViewById(R.id.etPriceFels);

        etPriceDinar = (EditText) findViewById(R.id.etPriceDinar);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etDinarTotal = (EditText) findViewById(R.id.etDinarTotal);
        etDinarCommission = (EditText) findViewById(R.id.etDinarCommission);
        etDinarTotalPlusCommssion = (EditText) findViewById(R.id.etDinarTotalPlusCommssion);
        etDate = (EditText) findViewById(R.id.etDate);

        btSell = (ImageButton) findViewById(R.id.btSell);
        btBuy = (ImageButton) findViewById(R.id.btBuy);
        // menu = (ImageButton) findViewById(R.id.menu);
        btConfirm = (ImageButton) findViewById(R.id.btConfirm);

        btIncrement = (ImageButton) findViewById(R.id.btIncrement);
        btDecrement = (ImageButton) findViewById(R.id.btDecrement);
        btEdit = (ImageButton) findViewById(R.id.btEdit);
        btRefresh = (ImageButton) findViewById(R.id.btRefresh);
        btDelete = (ImageButton) findViewById(R.id.btDelete);

        rbCancel = (RadioButton) findViewById(R.id.rbCancel);
        rbDate = (RadioButton) findViewById(R.id.rbDate);

        ivDate = (ImageView) findViewById(R.id.ivDate);

        if (MyApplication.lang == MyApplication.ARABIC) {
            llOrderOptions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            llOrderOptions.setTextDirection(View.TEXT_DIRECTION_RTL);
            top.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            top.setTextDirection(View.TEXT_DIRECTION_RTL);
            llCompany.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            llCompany.setTextDirection(View.TEXT_DIRECTION_RTL);
            spinnerLayout.setTextDirection(View.TEXT_DIRECTION_RTL);
            spinnerLayout.setTextDirection(View.TEXT_DIRECTION_RTL);

            etDate.setTextDirection(View.TEXT_DIRECTION_RTL);
            etQuantity.setTextDirection(View.TEXT_DIRECTION_RTL);
            etDinarTotalPlusCommssion.setTextDirection(View.TEXT_DIRECTION_RTL);
            etDinarTotal.setTextDirection(View.TEXT_DIRECTION_RTL);
            etDinarCommission.setTextDirection(View.TEXT_DIRECTION_RTL);
            etPriceDinar.setTextDirection(View.TEXT_DIRECTION_RTL);
            etPriceFels.setTextDirection(View.TEXT_DIRECTION_RTL);

            tvPlaceOrder.setText(getResources().getString(R.string.add_order));

        } else {
            etDate.setGravity(Gravity.START);
            etQuantity.setGravity(Gravity.CENTER);
            etDinarTotalPlusCommssion.setGravity(Gravity.START);
            etDinarTotal.setGravity(Gravity.START);
            etDinarCommission.setGravity(Gravity.START);
            etPriceDinar.setGravity(Gravity.START);
            etPriceFels.setGravity(Gravity.START);
        }


        etPriceFels.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!etPriceFels.getText().toString().equals("")) {
                    double dinar = 0;
                    try {
                        dinar = Double.parseDouble(etPriceFels.getText()
                                .toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    etPriceDinar.setText("" + dinar / 1000);

                    if (!etQuantity.getText().toString().equals("")) {
                        calcTotals(etQuantity, Double.parseDouble(etPriceFels
                                .getText().toString()), orderType);
                    }

                } else {
                    etPriceDinar.setText("0");
                }
            }

            public void afterTextChanged(Editable s) {

            }
        });

        etQuantity.addTextChangedListener(new TextWatcher() {

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (!etQuantity.getText().toString().equals("")) {
                    if (!etPriceDinar.getText().toString().equals("")) {
                        calcTotals(etQuantity, Double.parseDouble(etPriceFels
                                .getText().toString()), orderType);
                    }

                }
            }

            public void afterTextChanged(Editable s) {

            }
        });

        btIncrement.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (etQuantity.getText().equals("")) {
                    etQuantity.setText("1");
                }

                if (!etPriceDinar.getText().toString().equals("")
                        && !etPriceDinar.getText().toString().equals("null")) {

                    try {
                        etQuantity.setText(""
                                + (Integer.parseInt(etQuantity.getText()
                                .toString()) + 1));
                    } catch (Exception e) {
                        etQuantity.setText("1");
                    }
                    try {
                        calcTotals(etQuantity, Double.parseDouble(etPriceFels
                                .getText().toString()), orderType);
                    } catch (Exception e) {
                        calcTotals(etQuantity, 0.0, orderType);
                    }

                } else {
                    Log.d("in second", "iff");
                    //
                    try {
                        etQuantity.setText(""
                                + (Integer.parseInt(etQuantity.getText()
                                .toString()) + 1));
                    } catch (Exception e) {
                        etQuantity.setText("1");
                    }
                    try {
                        calcTotals(etQuantity, Double.parseDouble(etPriceFels
                                .getText().toString()), orderType);
                    } catch (Exception e) {
                        calcTotals(etQuantity, 0.0, orderType);
                    }
                }

            }
        });

        btDecrement.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (!etQuantity.getText().toString().equals("")
                        && (!etPriceDinar.getText().toString().equals("") && !etPriceDinar
                        .getText().toString().equals("null"))) {

                    if (Integer.parseInt(etQuantity.getText().toString()) > 0) {
                        etQuantity.setText(""
                                + (Integer.parseInt(etQuantity.getText()
                                .toString()) - 1));
                        try {
                            calcTotals(etQuantity, Double
                                    .parseDouble(etPriceFels.getText()
                                            .toString()), orderType);
                        } catch (Exception e) {
                            calcTotals(etQuantity, 0.0, orderType);
                        }
                    } else {
                        Log.wtf("here", "iam");
                        etQuantity.setText("0");
                    }
                } else {
                    if (!etQuantity.getText().toString().equals("")) {

                        if (Integer.parseInt(etQuantity.getText().toString()) > 0) {
                            etQuantity.setText(""
                                    + (Integer.parseInt(etQuantity.getText()
                                    .toString()) - 1));
                            try {
                                calcTotals(etQuantity, Double
                                        .parseDouble(etPriceFels.getText()
                                                .toString()), orderType);
                            } catch (Exception e) {
                                calcTotals(etQuantity, 0.0, orderType);
                            }
                        }
                    } else {
                        Log.wtf("down", "iam");
                        etQuantity.setText("0");
                    }
                }
            }
        });

        btBuy.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                orderType = 2;
                btBuy.setImageDrawable(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.sell));
                btBuy.setBackground(ContextCompat
                        .getDrawable(MowaziPlaceOrderActivity.this,
                                R.drawable.rounded_blue_button));
                btSell.setImageDrawable(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.background));
                btSell.setBackground(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.rounded_button));
                try {
                    calcTotals(etQuantity, Double.parseDouble(etPriceFels
                            .getText().toString()), orderType);
                } catch (Exception e) {

                }
            }
        });

        btSell.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                orderType = 1;
                btSell.setImageDrawable(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.buy));
                btSell.setBackground(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this,
                        R.drawable.rounded_yellow_button));
                btBuy.setImageDrawable(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.background));
                btBuy.setBackground(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.rounded_button));
                try {
                    calcTotals(etQuantity, Double.parseDouble(etPriceFels
                            .getText().toString()), orderType);
                } catch (Exception e) {

                }
            }
        });
        Calendar cl = Calendar.getInstance();
        mYear = cl.get(Calendar.YEAR);
        mMonth = cl.get(Calendar.MONTH);
        mDay = cl.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        String mm = String.valueOf(monthOfYear);
                        if (mm.length() == 1)
                            mm = "0" + mm;
                        String dd = String.valueOf(dayOfMonth);
                        if (dd.length() == 1)
                            dd = "0" + dd;

                        returnedDate = year + "-" + mm + "-" + dd;
                        etDate.setText(year + "-" + mm + "-" + dd);
                        // returnedDate=returnedDatefom;
                    }
                }, mYear, mMonth, mDay);

        DatePicker dp = datePickerDialog.getDatePicker();
        dp.setMinDate(cl.getTimeInMillis());// get the current day
        String d = "";
        if (dp.getMonth() + 2 <= 10) {
            d = "0" + (dp.getMonth() + 2);
            if (d.equals("010"))
                d = "10";
        } else
            d = String.valueOf(dp.getMonth() + 1);
        if (d.length() == 1)
            d = "0" + d;

        int year, month, days;
        Calendar c2 = Calendar.getInstance();
        c2.add(Calendar.MONTH, 1);
        year = c2.get(Calendar.YEAR);
        month = c2.get(Calendar.MONTH);
        days = c2.get(Calendar.DAY_OF_MONTH);

        etDate.setText(year + "-" + (month + 1) + "-" + (days));

        monthNext = year + "-" + (month + 1) + "-" + (days);
        ivDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                showDateTimePickerFrom(ivDate);
            }
        });

        spinnerCompanies
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    public void onItemSelected(AdapterView<?> parentView,
                                               View view, int position, long id) {
                        if (position != 0) {
                            MowaziCompany c = spinnerAdapter
                                    .getItem(position);
                            selectedCompany = c;
                        } else
                            selectedCompany.setCompanyId(0);
                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }

                });

        getCompanies = new GetCompanies();
        getCompanies.execute();

        flag = getIntent().getExtras().getInt("flag");

        if (flag == 1) {
            disableFields();
            // llCompany.setVisibility(View.GONE);
            order = getIntent().getExtras().getParcelable("order");
            orderId = order.getOrderId();
            orderType = order.getOrderTpeId();
            companyId = order.getCompanyId();

            setCompanyAndDisableSpinner();

            tvPlaceOrder.setText(getResources().getString(R.string.mowazi_orderDetails));
            if (orderType == 1) {
                btSell.setImageDrawable(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.buy));
                btSell.setBackground(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this,
                        R.drawable.rounded_yellow_button));
                btBuy.setImageDrawable(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.background));
            } else {
                btBuy.setImageDrawable(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.sell));
                btBuy.setBackground(ContextCompat
                        .getDrawable(MowaziPlaceOrderActivity.this,
                                R.drawable.rounded_blue_button));
                btSell.setImageDrawable(ContextCompat.getDrawable(
                        MowaziPlaceOrderActivity.this, R.drawable.background));
            }
            btSell.setEnabled(false);
            btBuy.setEnabled(false);

            setFields(order);
            rlConfirm.setVisibility(View.GONE);
            llActions.setVisibility(View.VISIBLE);
            // orderId = getIntent().getExtras().getInt("orderId");
        }

        btConfirm.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (flag == 1) {
                    update();
                } else
                    add();


                Log.wtf("SELECTED COMP ID IS", "is " + selectedCompany.getCompanyId());
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new AlertDialog.Builder(MowaziPlaceOrderActivity.this,
                        AlertDialog.THEME_HOLO_LIGHT)
                        .setMessage(
                                getResources().getString(R.string.mowazi_deleteOrder))
                        .setPositiveButton(android.R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        new DeleteOrder().execute();
                                    }
                                })
                        .setNegativeButton(android.R.string.no,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // do nothing
                                    }
                                }).show();
            }
        });

        btRefresh.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                new RefreshOrder().execute();
            }
        });

        btEdit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                enableFields();
                tvPlaceOrder.setText(getResources().getString(
                        R.string.mowazi_updateOrder));
                llActions.setVisibility(View.GONE);
                rlConfirm.setVisibility(View.VISIBLE);
            }
        });


        RadioGroup rgchoices = (RadioGroup) findViewById(R.id.rgChoices);
        rgchoices
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (checkedId == R.id.rbCancel)
                            etDate.setText("");
                        else if (checkedId == R.id.rbDate)
                            etDate.setText(getCurrentDate());

                    }
                });


        Actions.overrideFonts(this, root_Layout, true);
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String formattedDate = df.format(c.getTime());
        return formattedDate;

    }

    public void setCompanyAndDisableSpinner() {
        companyName.setVisibility(View.VISIBLE);
        spinnerCompanies.setVisibility(View.GONE);
        ImageView arrow = (ImageView) findViewById(R.id.arrow);
        arrow.setVisibility(View.GONE);

        companyName.setText(MyApplication.lang == MyApplication.ARABIC ? order.getCompany().getSymbolAr() : order.getCompany().getSymbolEn());
    }

    public boolean ExpiryDate(String d1)// date 1 is good until date, date 2 is
    // current date
    {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String d2 = df.format(c.getTime());
        boolean expired = false;
        try {
            // If you already have date objects then skip 1

            // 1
            // Create 2 dates starts
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date1 = sdf.parse(d1);
            Date date2 = sdf.parse(d2);
            // Create 2 dates ends
            // 1

            // Date object is having 3 methods namely after,before and equals
            // for comparing
            // after() will return true if and only if date1 is after date 2
            if (date1.after(date2)) {
                expired = false;

            }
            // before() will return true if and only if date1 is before date2
            if (date1.before(date2)) {

                expired = true;
            }

            // equals() returns true if both the dates are equal
            if (date1.equals(date2)) {

                expired = true;
            }

            System.out.println();
        } catch (ParseException ex) {
            ex.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return expired;
    }

    private void createDialog(String message) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(MowaziPlaceOrderActivity.this, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setMessage(message)
                .setCancelable(true)
                .setNegativeButton(MowaziPlaceOrderActivity.this.getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    public void update() {

        if (orderType != -1) {
            if (etPriceFels.getText().length() > 0) {
                if (!etQuantity.getText().toString().equals("0")
                        && !etQuantity.getText().toString().equals("")) {

                    if (order.getGoodUntilDate().equals("null")
                            || rbCancel.isChecked()) {// already till cancel
                        String dateToCheck = "";
                        if (!etDate.getText().toString().equals("")
                                && !rbCancel.isChecked()) {
                            dateToCheck = etDate.getText().toString();
                        } else
                            dateToCheck = order.getExpirationDate().substring(
                                    0, 11);
                        if (ExpiryDate(dateToCheck)) {
                            new AlertDialog.Builder(MowaziPlaceOrderActivity.this)
                                    // .setTitle(getResources().getString(R.string.sendOrderTitle))
                                    .setMessage(
                                            getResources().getString(
                                                    R.string.mowazi_updategoodcancel))
                                    .setPositiveButton(
                                            android.R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {

                                                    rbCancel.setChecked(true);
                                                    new PlaceUpdateOrder()
                                                            .execute();
                                                }
                                            })
                                    .setNegativeButton(
                                            android.R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // do
                                                    // nothing
                                                }
                                            }).show();

                        } else {
                            new AlertDialog.Builder(MowaziPlaceOrderActivity.this)
                                    // .setTitle(getResources().getString(R.string.sendOrderTitle))
                                    .setMessage(
                                            getResources().getString(
                                                    R.string.mowazi_sendOrder))
                                    .setPositiveButton(
                                            android.R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {

                                                    rbCancel.setChecked(true);
                                                    new PlaceUpdateOrder()
                                                            .execute();
                                                }
                                            })
                                    .setNegativeButton(
                                            android.R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // do
                                                    // nothing
                                                }
                                            }).show();
                        }
                    } else if (etDate.getText().length() > 0) {

                        if (flag == 1) {// here updating the order
                            if (checkIfChange(order)) {

                                if (ExpiryDate(etDate.getText().toString())) {
                                    createDialog(getResources().getString(R.string.mowazi_dateAlert));
                                } else {
                                    // case if the date is a good until date
                                    // if(order.getGoodUntilDate())
                                    new AlertDialog.Builder(
                                            MowaziPlaceOrderActivity.this)
                                            // .setTitle(getResources().getString(R.string.sendOrderTitle))
                                            .setMessage(
                                                    getResources().getString(
                                                            R.string.mowazi_sendOrder))
                                            .setPositiveButton(
                                                    android.R.string.yes,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            new PlaceUpdateOrder()
                                                                    .execute();
                                                        }
                                                    })
                                            .setNegativeButton(
                                                    android.R.string.no,
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(
                                                                DialogInterface dialog,
                                                                int which) {
                                                            // do
                                                            // nothing
                                                        }
                                                    }).show();
                                }
                            } else {
                                createDialog(getResources().getString(R.string.mowazi_noChanges));
                            }
                        } else {
                            new AlertDialog.Builder(MowaziPlaceOrderActivity.this)
                                    // .setTitle(getResources().getString(R.string.sendOrderTitle))
                                    .setMessage(
                                            getResources().getString(
                                                    R.string.mowazi_sendOrder))
                                    .setPositiveButton(
                                            android.R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    new PlaceUpdateOrder()
                                                            .execute();
                                                }
                                            })
                                    .setNegativeButton(
                                            android.R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // do nothing
                                                }
                                            }).show();

                        }
                    } else {
                        createDialog(getResources().getString(R.string.mowazi_dateAlert));
                    }
                } else {
                    createDialog(getResources().getString(R.string.mowazi_alertQuantity));
                }
            } else {
                createDialog(getResources().getString(R.string.mowazi_alertPrice));
            }
        } else {
            createDialog(getResources().getString(R.string.mowazi_alertOrder));
        }

    }

    public void add() {
        if (!spinnerCompanies.getText().toString().equals("")) {

            if (orderType != -1) {
                if (etPriceFels.getText().length() > 0) {
                    if (!etQuantity.getText().toString().equals("0")
                            && !etQuantity.getText().toString().equals("")) {

                        if (rbCancel.isChecked()) {

                            new AlertDialog.Builder(MowaziPlaceOrderActivity.this)
                                    // .setTitle(getResources().getString(R.string.sendOrderTitle))
                                    .setMessage(
                                            getResources().getString(
                                                    R.string.mowazi_sendOrder))
                                    .setPositiveButton(
                                            android.R.string.yes,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {

                                                    rbCancel.setChecked(true);
                                                    new PlaceUpdateOrder()
                                                            .execute();
                                                }
                                            })
                                    .setNegativeButton(
                                            android.R.string.no,
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(
                                                        DialogInterface dialog,
                                                        int which) {
                                                    // do
                                                    // nothing
                                                }
                                            }).show();

                        } else if (etDate.getText().length() > 0) {

                            if (ExpiryDate(etDate.getText().toString())) {
                                createDialog(getResources().getString(R.string.mowazi_dateAlert));
                            } else {
                                new AlertDialog.Builder(MowaziPlaceOrderActivity.this)
                                        // .setTitle(getResources().getString(R.string.sendOrderTitle))
                                        .setMessage(
                                                getResources().getString(
                                                        R.string.mowazi_sendOrder))
                                        .setPositiveButton(
                                                android.R.string.yes,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        new PlaceUpdateOrder()
                                                                .execute();
                                                    }
                                                })
                                        .setNegativeButton(
                                                android.R.string.no,
                                                new DialogInterface.OnClickListener() {
                                                    public void onClick(
                                                            DialogInterface dialog,
                                                            int which) {
                                                        // do nothing
                                                    }
                                                }).show();

                            }
                        } else {
                            createDialog(
                                    getResources()
                                            .getString(R.string.mowazi_dateAlert));
                        }
                    } else {
                        createDialog(
                                getResources()
                                        .getString(R.string.mowazi_alertQuantity));
                    }
                } else {
                    createDialog(
                            getResources().getString(R.string.mowazi_alertPrice));
                }
            } else {
                createDialog(getResources()
                        .getString(R.string.mowazi_alertOrder));
            }
        } else
            createDialog(getResources()
                    .getString(R.string.mowazi_alertCompany));

    }

    public void calcTotals(EditText quantity, Double price, int ordertype) {

        // value used to calculate totals

        // case user choose to use market price

        // case choose price

        int qty;

        try {
            qty = Integer.parseInt(quantity.getText().toString());

        } catch (NumberFormatException e) {
            qty = 0;
        }
        Double total = (price * qty) / 1000;
        etDinarTotal.setText(Actions.roundNumber(total, Actions.ThreeDecimal)
                + "");

        Double commission = (total * 0.00125) + 0.5;
        if (commission < 0)
            commission = 0.0;
        etDinarCommission.setText(Actions.roundNumber(commission,
                Actions.ThreeDecimal) + "");

        Double totalCost = 0.0;
        // case sell
        if (ordertype == 1) {
            // subtract
            totalCost = total - commission;
        } else if (ordertype == 2) {
            // add
            totalCost = total + commission;
        } else if (ordertype == -1) {
            // add
            totalCost = total + commission;
        }
        if (totalCost < 0)
            totalCost = 0.0;
        etDinarTotalPlusCommssion.setText(Actions.roundNumber(totalCost,
                Actions.ThreeDecimal) + "");

    }

    private boolean checkIfChange(MowaziOnlineOrder o) {
        if (flag == 0) {
            return true;
        } else {
            if ((o.getOriginalShares().equals(etQuantity.getText().toString()))
                    && o.getPrice() == Double.parseDouble(etPriceDinar
                    .getText().toString())) {
                Log.wtf("Pricesame", "chkDate");

                if (rbCancel.isChecked()) {
                    Log.wtf("rbCancel", "isChecked");
                    if (o.getGoodUntilDate()
                            .substring(0, o.getGoodUntilDate().indexOf("T"))
                            .equals(monthNext)) {
                        Log.wtf("Cancel", "from first");
                        return false;
                    } else {
                        Log.wtf("Cancel", "new");
                        return true;
                    }
                } else {
                    Log.wtf("rbDate", "isChecked");
                    if (o.getGoodUntilDate()
                            .substring(0, o.getGoodUntilDate().indexOf("T"))
                            .equals(etDate.getText().toString())) {
                        Log.wtf("same", "date");
                        return false;
                    } else {
                        Log.wtf("diff", "date");
                        return true;
                    }
                }

            } else {
                Log.wtf("YES", "CHANGE");
                return true;
            }
        }
        // System.exit(1);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public String showDateTimePickerFrom(final View v) {
        datePickerDialog.show();
        return "";
    }

    private void disableFields() {
        etPriceFels.setEnabled(false);
        etQuantity.setEnabled(false);
        btIncrement.setEnabled(false);
        btDecrement.setEnabled(false);
        etDate.setEnabled(false);
    }

    private void returnSpinnerindex(String l) {
        Log.wtf("companyId aaaa", "" + l);
        for (int i = 0; i < companiesToTrade.size(); i++) {

            if (Integer.parseInt(l) == companiesToTrade.get(i).getCompanyId()) {
                spinnerCompanies.setText(MyApplication.lang == MyApplication.ENGLISH ? companiesToTrade
                        .get(i).getSymbolEn() : companiesToTrade.get(i)
                        .getSymbolAr());

                selectedCompany = companiesToTrade.get(i);
                selectedCompany.setCompanyId(Integer.parseInt(l));
                break;
            }
        }

    }

    public String parseDate(String dateFormat) {
        String inputPattern = "yyyy-MM-dd";
        String outputPattern = "MM/dd/yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateFormat);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    private void setFields(MowaziOnlineOrder o) {

        etPriceFels.setText("" + o.getPrice());
        etPriceFels.setFilters(new InputFilter[]{new InputFilter() {

            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned destination, int destinationStart,
                                       int destinationEnd) {
                // TODO Auto-generated method stub
                if (end > start) {
                    // adding: filter
                    // build the resulting text

                    String destinationString = destination.toString();
                    String resultingTxt = destinationString.substring(0,
                            destinationStart)
                            + source.subSequence(start, end)
                            + destinationString.substring(destinationEnd);
                    // return null to accept the input or empty to reject it
                    return resultingTxt.matches(regex) ? null : "";
                }
                return null;
            }
        }});
        etQuantity.setText("" + o.getOriginalShares());
        Log.wtf("date", o.getGoodUntilDate());
        try {
            etDate.setText(""
                    + o.getGoodUntilDate().substring(0,
                    o.getGoodUntilDate().indexOf("T")));
        } catch (Exception e) {
            etDate.setText("");
        }
        quantity = Integer.parseInt(etQuantity.getText().toString());
        etDinarTotal.setText(""
                + myFormatter.format((Double.parseDouble(etPriceDinar.getText()
                .toString()) * quantity)));
        etDinarCommission
                .setText(""
                        + myFormatter.format(((Double.parseDouble(etDinarTotal
                        .getText().toString()) * MyApplication.brokerageSumCoif) + quantity
                        * MyApplication.brokerageQuantityCoif)));
        etDinarTotalPlusCommssion.setText(""
                + myFormatter.format((Double.parseDouble(etDinarCommission
                .getText().toString()) + Double
                .parseDouble(etDinarTotal.getText().toString()))));
        Log.wtf("monthNext", monthNext);
        if (etDate.getText().toString().equals("")) {
            Log.wtf("date", "cancel");
            rbCancel.setChecked(true);
            // etDate.setText(getResources().getString(R.string.cancellation));
        } else {
            Log.wtf("date", etDate.getText().toString());
        }

        try {
            String dd = o.getGoodUntilDate().substring(0,
                    o.getGoodUntilDate().indexOf("T"));
            String[] dateSplitted = dd.split("-");

            mYear = Integer.parseInt(dateSplitted[0]);
            mMonth = Integer.parseInt(dateSplitted[1]) - 1;
            mDay = Integer.parseInt(dateSplitted[2]);
            datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            monthOfYear = monthOfYear + 1;
                            String mm = String.valueOf(monthOfYear);
                            if (mm.length() == 1)
                                mm = "0" + mm;
                            String dd = String.valueOf(dayOfMonth);
                            if (dd.length() == 1)
                                dd = "0" + dd;

                            returnedDate = year + "-" + mm + "-" + dd;
                            etDate.setText(year + "-" + mm + "-" + dd);
                            // returnedDate=returnedDatefom;
                        }
                    }, mYear, mMonth, mDay);
            DatePicker dp = datePickerDialog.getDatePicker();
            // Set the DatePicker minimum date selection to current date
            Calendar cc = Calendar.getInstance();
            cc.set(mYear, mMonth, mDay);
            try {
                dp.setMinDate(cc.getTimeInMillis());// get the current day
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            rbCancel.setChecked(true);
            etDate.setText("");
        }

    }

    private void enableFields() {
        etPriceFels.setEnabled(true);
        etQuantity.setEnabled(true);
        btIncrement.setEnabled(true);
        btDecrement.setEnabled(true);
        etDate.setEnabled(true);
    }

    public void back(View v) {
        finish();
    }

    public String parseDates(String dates) {
        String inputPattern = "MM/dd/yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern,
                Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern,
                Locale.ENGLISH);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dates);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    private void createOrderDialog(final int flag, String message) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(MowaziPlaceOrderActivity.this, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(MowaziPlaceOrderActivity.this.getString(R.string.confirm),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (flag == 1)
                                    MowaziPlaceOrderActivity.this.startActivity(new Intent(MowaziPlaceOrderActivity.this, MowaziMyOrdersActivity.class));
                                else if (flag == 3) {
                                    MowaziPlaceOrderActivity.this.finish();
                                    MowaziPlaceOrderActivity.this.startActivity(new Intent(MowaziPlaceOrderActivity.this, MowaziMyOrdersActivity.class));
                                }
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(MowaziPlaceOrderActivity.this.getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void createOrderDialogDeleted(final int flag, String message) {

        ContextThemeWrapper ctw = new ContextThemeWrapper(MowaziPlaceOrderActivity.this, R.style.AlertDialogCustom);

        AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(MowaziPlaceOrderActivity.this.getString(R.string.save),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (flag == 1)
                                    MowaziPlaceOrderActivity.this.startActivity(new Intent(MowaziPlaceOrderActivity.this, MowaziMyOrdersActivity.class));
                                else if (flag == 3) {
                                    MowaziPlaceOrderActivity.this.finish();
                                    MowaziPlaceOrderActivity.this.startActivity(new Intent(MowaziPlaceOrderActivity.this, MowaziMyOrdersActivity.class));
                                }
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void clearFields() {
        etDate.setText("");
        etPriceDinar.setText("");
        etQuantity.setText("");
        etDinarCommission.setText("");
        etDinarTotal.setText("");
        etDinarTotalPlusCommssion.setText("");
        etPriceFels.setText("");
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

    protected class GetCompanies extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbOrders.setVisibility(View.VISIBLE);
            // llActivity.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetCompaniesToTrade?";

            HashMap<String, String> parameters = new HashMap<String, String>();


            parameters.put("lang", "" + MyApplication.lang);

            result = ConnectionRequests.POST(url, parameters);

            MowaziCompanyNameParser parser = new MowaziCompanyNameParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                companiesToTrade.clear();
                MowaziCompany companyfake = new MowaziCompany();
                companyfake.setSymbolEn("--" + getString(R.string.mowazi_company)
                        + "--");
                companyfake.setSymbolAr("--" + getString(R.string.mowazi_company)
                        + "--");
                // companiesToTrade.add(0, companyfake);
                companiesToTrade.addAll(parser.GetCompanies());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbOrders.setVisibility(View.GONE);
            // llActivity.setVisibility(View.VISIBLE);
            spinnerCompanies.setThreshold(1);
            spinnerAdapter = new MowaziCompanySpinnerAutoCompleteAdaper(
                    MowaziPlaceOrderActivity.this, R.layout.need_list_spinner_item,
                    companiesToTrade);
            spinnerCompanies.setAdapter(spinnerAdapter);

            spinnerCompanies.setOnClickListener(new View.OnClickListener() {

                public void onClick(final View arg0) {
                    spinnerCompanies.showDropDown();
                }
            });

            spinnerCompanies.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    selectedCompany = spinnerAdapter.getItem(position);
                }
            });

            if (!companyIdfromCompanies.equals("-1")) {
                returnSpinnerindex(companyIdfromCompanies);
                // spinnerCompanies.setEnabled(false);
            }
        }
    }

    public class RegisterOrLoginMowazi extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String result = "";

            try {
                final String METHOD_NAME = "RegisterOrLoginFixOT";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // parameters to be passed to the method (parameter name, value)
                Element[] header = new Element[1];

                header[0] = new Element().createElement(NAMESPACE, "FixOtHeader");

                Element username = new Element().createElement(NAMESPACE, "Username");
                username.addChild(Node.TEXT, MyApplication.currentUser.getUsername());
                header[0].addChild(Node.ELEMENT, username);

                Element investorNumber = new Element().createElement(NAMESPACE, "InvestorNumber");
                investorNumber.addChild(Node.TEXT, String.valueOf(MyApplication.currentUser.getInvestorId()));
                header[0].addChild(Node.ELEMENT, investorNumber);

                Element name = new Element().createElement(NAMESPACE, "Name");
                name.addChild(Node.TEXT, MyApplication.currentUser.getUsername());
                header[0].addChild(Node.ELEMENT, name);

                Element random = new Element().createElement(NAMESPACE, "Password");
                random.addChild(Node.TEXT, Actions.MD5(Actions.getRandom()));
                header[0].addChild(Node.ELEMENT, random);

                Element brokerid = new Element().createElement(NAMESPACE, "BrokerID");
                brokerid.addChild(Node.TEXT, MyApplication.mowaziBrokerId);
                header[0].addChild(Node.ELEMENT, brokerid);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;

                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res.getProperty("RegisterOrLoginFixOTResult");

                result = t.getProperty("success").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                if (result.contains("true")) {

                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                    try {

                        MyApplication.mowaziClientID = Integer.parseInt(result.split(",")[0]);
                        MyApplication.editor.putString("lastdate", formattedDate).apply();
                        MyApplication.editor.putInt("clientId", MyApplication.mowaziClientID).apply();

                        if (MyApplication.mshared.getString("oldToken", "").equals("") && MyApplication.mshared.getString("newToken", "").equals("")) {

                            MyApplication.editor.putString("oldToken", result.split(",")[2]);
                            MyApplication.editor.putString("newToken", result.split(",")[2]);
                            MyApplication.editor.putString("expiry", result.split(",")[3]);
                            MyApplication.editor.apply();
                        } else {

                            MyApplication.editor.putString("oldToken", MyApplication.mshared.getString("newToken", ""));
                            MyApplication.editor.putString("newToken", result.split(",")[2]);
                            MyApplication.editor.putString("expiry", result.split(",")[3]);
                            MyApplication.editor.apply();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    MyApplication.editor.apply();
                } else
                    Toast.makeText(getApplicationContext(), "Error when register in mowazi", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    protected class PlaceUpdateOrder extends AsyncTask<Void, Void, String> {

        String quantity = etQuantity.getText().toString();
        String price = etPriceFels.getText().toString();
        String goodUntil = etDate.getText().toString();
        String name = "place";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbOrders.setVisibility(View.VISIBLE);
            // llActivity.setVisibility(View.GONE);

            if (goodUntil == null)
                goodUntil = "";
            if (rbCancel.isChecked())
                goodUntil = "";

            Log.wtf("goodUntil", goodUntil);

        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            // String url = "" + MyApplication.url + "/PlaceUpdateOrder?";
            //
            // HashMap<String, String> parameters = new HashMap<String,
            // String>();
            //
            // parameters.put("IsCurrentUserAdmin", "" + false);
            // parameters.put("orderId", "" + orderId);
            // parameters.put("oldToken", mshared.getString("oldToken",""));
            // parameters.put("newToken", mshared.getString("newToken",""));
            // parameters.put("ClientId", "" + MyApplication.clientId);
            // parameters.put("CompanyId", "" + selectedCompany.getCompanyId());
            // parameters.put("ordertype", "" + orderType);
            // parameters.put("quantity", "" + quantity);
            // parameters.put("price", price);
            // parameters.put("goodUntil", goodUntil);
            //
            // result = ConnectionRequests.POST(url, parameters);
            try {
                final String METHOD_NAME = "PlaceOrderFromBrokerService";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // parameters to be passed to the method (parameter name, value)
                request.addProperty("IsCurrentUserAdmin", "" + false);
                request.addProperty("orderId", "" + orderId);

                request.addProperty("CompanyId",
                        "" + selectedCompany.getCompanyId());

                request.addProperty("ordertype", "" + orderType);

                request.addProperty("quantity", "" + quantity);

                request.addProperty("price", price);

                request.addProperty("goodUntil", goodUntil + "");
                request.addProperty("InvestorNumber", ""
                        + MyApplication.currentUser.getInvestorId());
                request.addProperty("BrokerId", ""
                        + MyApplication.mowaziBrokerId);

                Element[] header = new Element[1];
                header[0] = new Element().createElement(NAMESPACE,
                        "SoapClassService");

                Element username = new Element().createElement(NAMESPACE,
                        "ClientID");
                username.addChild(Node.TEXT, "" + MyApplication.mowaziClientID);
                header[0].addChild(Node.ELEMENT, username);


                Element pass = new Element().createElement(NAMESPACE,
                        "oldToken");
                pass.addChild(Node.TEXT, mshared.getString("oldToken", ""));
                header[0].addChild(Node.ELEMENT, pass);

                Element random = new Element().createElement(NAMESPACE,
                        "newToken");
                random.addChild(Node.TEXT, mshared.getString("newToken", ""));
                header[0].addChild(Node.ELEMENT, random);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;

                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res
                        .getProperty("PlaceOrderFromBrokerServiceResult");

                result = t.getProperty("message").toString();

                // = bank.getProperty("success").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbOrders.setVisibility(View.GONE);
            Log.wtf("result", result);

            if (result.contains("Expired")) {

                Log.wtf("login", "expired");

                RegisterOrLoginMowazi login = new RegisterOrLoginMowazi();
                login.execute();
            } else if (result.contains("Security Issue")) {
                Intent i = new Intent();
                Log.wtf("Security", "Issue");
                // MyApplication.logged = false;
                Actions.logout(MowaziPlaceOrderActivity.this);
                i.putExtra("activity", "placeorder");

                i.setClass(MowaziPlaceOrderActivity.this, LoginFingerPrintActivity.class);

                startActivity(i);
                finish();
                edit.putBoolean("loggedMowazi", false);
                edit.commit();
            } else {

                Log.wtf("else", "else");

                // llActivity.setVisibility(View.VISIBLE);
                if (result.split(",")[0].equals("1")) {
                    Log.wtf("succ", "ess");
                    if (MyApplication.lang == MyApplication.ARABIC) {

                        MowaziPlaceOrderActivity.this
                                .startActivity(new Intent(
                                        MowaziPlaceOrderActivity.this,
                                        MowaziMyOrdersActivity.class));
                        finish();
                    } else {
                        // Actions.CreateOrderDialog(1, MowaziPlaceOrderActivity.this,
                        // result.split(",")[1]);
                        MowaziPlaceOrderActivity.this
                                .startActivity(new Intent(
                                        MowaziPlaceOrderActivity.this,
                                        MowaziMyOrdersActivity.class));
                        finish();
                    }
                } else {
                    if (MyApplication.lang == MyApplication.ARABIC)
                        createOrderDialog(0, result.split(",")[2]);
                    else
                        createOrderDialog(0, result.split(",")[1]);
                }
            }
        }
    }

    protected class DeleteOrder extends AsyncTask<Void, Void, String> {

        String name = "delete";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbOrders.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";

            // String url = "" + MyApplication.url + "/DeleteOrder?";
            //
            // HashMap<String, String> parameters = new HashMap<String,
            // String>();
            //
            // parameters.put("id", "" + orderId);
            // Log.wtf("id", "" + orderId);
            //
            // result = ConnectionRequests.POST(url, parameters);
            // Log.wtf("result", "" + result);
            try {
                final String METHOD_NAME = "DeleteOrder";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // parameters to be passed to the method (parameter name, value)
                request.addProperty("id", "" + orderId);

                Element[] header = new Element[1];
                header[0] = new Element().createElement(NAMESPACE,
                        "SoapClassService");
                Element username = new Element().createElement(NAMESPACE,
                        "ClientID");
                username.addChild(Node.TEXT, "" + MyApplication.mowaziClientID);
                header[0].addChild(Node.ELEMENT, username);
                Element pass = new Element().createElement(NAMESPACE,
                        "oldToken");
                pass.addChild(Node.TEXT, mshared.getString("oldToken", ""));
                header[0].addChild(Node.ELEMENT, pass);
                Element random = new Element().createElement(NAMESPACE,
                        "newToken");
                random.addChild(Node.TEXT, mshared.getString("newToken", ""));
                header[0].addChild(Node.ELEMENT, random);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;

                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res
                        .getProperty("DeleteOrderResult");

                result = t.getProperty("message").toString();

                // = bank.getProperty("success").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbOrders.setVisibility(View.GONE);
            if (result.contains("Expired")) {
                RegisterOrLoginMowazi login = new RegisterOrLoginMowazi();
                login.execute();
            } else if (result.contains("Security Issue")) {
                Intent i = new Intent();

                // MyApplication.logged = false;
                Actions.logout(MowaziPlaceOrderActivity.this);
                i.putExtra("activity", "");

                i.setClass(MowaziPlaceOrderActivity.this, LoginFingerPrintActivity.class);

                startActivity(i);
                finish();
                edit.putBoolean("loggedMowazi", false);
                edit.commit();
            } else {
                try {
                    if (result.split(",")[0].equals("1")) {
                        if (MyApplication.lang == MyApplication.ARABIC)

                            createOrderDialogDeleted(3, result.split(",")[2]);
                        else
                            createOrderDialogDeleted(3, result.split(",")[1]);
                        // finish();
                    } else {
                        if (MyApplication.lang == MyApplication.ARABIC)

                            createOrderDialogDeleted(0, result.split(",")[2]);
                        else

                            createOrderDialogDeleted(0, result.split(",")[1]);
                    }
                } catch (Exception e) {
                    Toast.makeText(MowaziPlaceOrderActivity.this,
                            getResources().getString(R.string.error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    protected class RefreshOrder extends AsyncTask<Void, Void, String> {
        String name = "activate";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbOrders.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = "";
            try {
                final String METHOD_NAME = "ActivateOrder";
                final String NAMESPACE = "http://tempuri.org/";

                // SOAP_ACTION = NAMESPACE + METHOD_NAME
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

                final String URL = MyApplication.mowaziUrl;

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                // parameters to be passed to the method (parameter name, value)
                request.addProperty("orderId", "" + orderId);

                Element[] header = new Element[1];
                header[0] = new Element().createElement(NAMESPACE,
                        "SoapClassService");
                Element username = new Element().createElement(NAMESPACE,
                        "ClientID");
                username.addChild(Node.TEXT, "" + MyApplication.mowaziClientID);
                header[0].addChild(Node.ELEMENT, username);
                Element pass = new Element().createElement(NAMESPACE,
                        "oldToken");
                pass.addChild(Node.TEXT, mshared.getString("oldToken", ""));
                header[0].addChild(Node.ELEMENT, pass);
                Element random = new Element().createElement(NAMESPACE,
                        "newToken");
                random.addChild(Node.TEXT, mshared.getString("newToken", ""));
                header[0].addChild(Node.ELEMENT, random);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.dotNet = true;
                envelope.headerOut = header;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.debug = true;

                httpTransport.call(SOAP_ACTION, envelope);
                result = envelope.getResponse().toString();
                SoapObject res = (SoapObject) envelope.bodyIn;
                SoapObject t = (SoapObject) res
                        .getProperty("DeleteOrderResult");

                result = t.getProperty("message").toString();

                // = bank.getProperty("success").toString();

            } catch (SoapFault sf) {
                System.out.println(sf.faultstring);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbOrders.setVisibility(View.GONE);
            if (result.contains("Expired")) {
                RegisterOrLoginMowazi login = new RegisterOrLoginMowazi();
                login.execute();
            } else if (result.contains("Security Issue")) {
                Intent i = new Intent();

                // MyApplication.logged = false;

                Actions.logout(MowaziPlaceOrderActivity.this);
                i.putExtra("activity", "");

                i.setClass(MowaziPlaceOrderActivity.this, LoginFingerPrintActivity.class);

                startActivity(i);
                finish();
                edit.putBoolean("loggedMowazi", false);
                edit.commit();
            } else {
                if (result.split(",")[0].equals("1")) {
                    if (MyApplication.lang == MyApplication.ARABIC)

                        createOrderDialog(1, result.split(",")[2]);
                    else

                        createOrderDialog(1, result.split(",")[1]);
                } else {
                    if (MyApplication.lang == MyApplication.ARABIC)

                        createOrderDialog(0, result.split(",")[2]);
                    else
                        createOrderDialog(0, result.split(",")[1]);
                }
            }
        }
    }
}

