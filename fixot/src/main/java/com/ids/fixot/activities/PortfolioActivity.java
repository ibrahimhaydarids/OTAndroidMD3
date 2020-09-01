package com.ids.fixot.activities;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.AppService;
import com.ids.fixot.BuildConfig;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MarketStatusReceiver.MarketStatusListener;
import com.ids.fixot.MarketStatusReceiver.marketStatusReceiver;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.PortoflioStockForwardAdapter;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;
import com.ids.fixot.adapters.ValuesGridRecyclerAdapter;
import com.ids.fixot.model.StockSummary;
import com.ids.fixot.model.ValueItem;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.ids.fixot.MyApplication.lang;

/**
 * Created by user on 7/21/2017.
 */

public class PortfolioActivity extends AppCompatActivity implements PortoflioStockForwardAdapter.RecyclerViewOnItemClickListener, MarketStatusListener {

    Toolbar myToolbar;
    ImageView ivBack, ivPortfolio;
    TextView tvToolbarTitle, tvToolbarStatus, tvUserName, tvPortfolioNumber, tvLogout;
    Spinner spSubAccounts;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    SwipeRefreshLayout swipeContainer;
    ScrollView scrollView;
    RelativeLayout rlUserHeader, rlAccountStatement, rootLayout, rlKcc;
    RecyclerView rvUserData, rvStockSummaryForward;
    GridLayoutManager llUserData;
    LinearLayoutManager llstocksForward;
    ValuesGridRecyclerAdapter adapter;
    GetPortfolio getPortfolio = new GetPortfolio();
    ArrayList<StockSummary> arrayOfStocks = new ArrayList<>();
    ArrayList<ValueItem> allValueItems = new ArrayList<>();
    PortoflioStockForwardAdapter portoflioStockForwardAdapter;
    TextView tvNoData;
    LinearLayout llTotalStocks;
    TextView tvTotalValue, tvTotalGain, tvTotalValueText, tvGainLossText;
    private BroadcastReceiver receiver;
    private boolean started = false, showProgress = false;

    public PortfolioActivity() {
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
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_portfolio);
        Actions.initializeBugsTracking(this);

        findViews();

        if (getIntent().hasExtra("fromFooter")) {
            showProgress = getIntent().getExtras().getBoolean("fromFooter");
        }

        Actions.showHideFooter(this);
        started = true;

        tvUserName.setText(MyApplication.lang == MyApplication.ARABIC ? MyApplication.currentUser.getNameAr() : MyApplication.currentUser.getNameEn());
        tvPortfolioNumber.setText(String.valueOf(MyApplication.currentUser.getPortfolioNumber()));

        tvPortfolioNumber.setText(String.valueOf(MyApplication.currentUser.getPortfolioNumber()));

        try {
            tvToolbarStatus.setText(MyApplication.marketStatus.getStatusName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        swipeContainer.setOnRefreshListener(() -> {


            getPortfolio = new GetPortfolio();
            getPortfolio.executeOnExecutor(MyApplication.threadPoolExecutor);
            swipeContainer.setRefreshing(false);
        });


        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
        }

        Actions.initializeToolBar(getString(R.string.portfolio), PortfolioActivity.this);

        Actions.overrideFonts(this, rootLayout, false);

        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);

        if (MyApplication.lang == MyApplication.ARABIC) {

            tvUserName.setText(MyApplication.currentUser.getNameAr());
            tvUserName.setTypeface(MyApplication.droidbold);
            tvPortfolioNumber.setTypeface(MyApplication.droidbold);
        } else {

            tvUserName.setText(MyApplication.currentUser.getNameEn());
            tvUserName.setTypeface(MyApplication.giloryBold);
            tvPortfolioNumber.setTypeface(MyApplication.giloryBold);
        }
    }

    /*private void setInfoText(){
        String info = MyApplication.selectedSubAccount.toString() + " - " + MyApplication.selectedSubAccount.getPortfolioName();
        tvInfo.setText(info);
    }*/

    private void findViews() {

        spSubAccounts = findViewById(R.id.spSubAccounts);
        llTotalStocks = findViewById(R.id.llTotalStocks);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvTotalGain = findViewById(R.id.tvTotalGain);
        tvGainLossText = findViewById(R.id.tvGainLossText);
        tvTotalValueText = findViewById(R.id.tvTotalValueText);

        rootLayout = findViewById(R.id.rootLayout);
        myToolbar = findViewById(R.id.my_toolbar);
        rlAccountStatement = findViewById(R.id.rlAccountStatement);
        rlKcc = findViewById(R.id.rlKcc);
        tvNoData = findViewById(R.id.tvNoData);
        rlUserHeader = findViewById(R.id.rlUserHeader);

        tvToolbarTitle = findViewById(R.id.toolbar_title);
        tvUserName = rlUserHeader.findViewById(R.id.tvUserName);
        tvPortfolioNumber = rlUserHeader.findViewById(R.id.tvPortfolioNumber);
        ivPortfolio = rlUserHeader.findViewById(R.id.ivPortfolio);
        tvToolbarStatus = findViewById(R.id.toolbar_status);

        ivBack = findViewById(R.id.ivBack);
        rvUserData = findViewById(R.id.rvUserData);
        scrollView = findViewById(R.id.scrollView);
        swipeContainer = findViewById(R.id.swipeContainer);

        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(PortfolioActivity.this));
        tvLogout.setVisibility((BuildConfig.GoToMenu) ? View.GONE : View.VISIBLE);

        llUserData = new GridLayoutManager(this, MyApplication.GRID_VALUES_SPAN_COUNT);


        llstocksForward = new LinearLayoutManager(PortfolioActivity.this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        rvStockSummaryForward = findViewById(R.id.rvStockSummaryForward);

        rvStockSummaryForward.setLayoutManager(llstocksForward);

        rlAccountStatement.setOnClickListener(v -> startActivity(new Intent(PortfolioActivity.this, AccountStatementActivity.class).putExtra("isAccountStatement", true)));

        ivBack.setVisibility((BuildConfig.GoToMenu) ? View.VISIBLE : View.GONE);


        rlKcc.setOnClickListener(v ->
                        checkPermissions()
                //startActivity(new Intent(PortfolioActivity.this, AccountStatementActivity.class).putExtra("isAccountStatement", false))
        );

        adapter = new ValuesGridRecyclerAdapter(PortfolioActivity.this, allValueItems);
        rvUserData.setAdapter(adapter);
        rvUserData.setNestedScrollingEnabled(false);

        arrayOfStocks.clear();
        arrayOfStocks.add(new StockSummary());//header
        portoflioStockForwardAdapter = new PortoflioStockForwardAdapter(PortfolioActivity.this, arrayOfStocks, this);
        rvStockSummaryForward.setAdapter(portoflioStockForwardAdapter);
        rvStockSummaryForward.setNestedScrollingEnabled(false);


        subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, MyApplication.currentUser.getSubAccounts());
        spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
        spSubAccounts.setSelection(returnAccountIndex());
        spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);
                getPortfolio = new GetPortfolio();
                getPortfolio.executeOnExecutor(MyApplication.threadPoolExecutor);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private int returnAccountIndex() {

        int index = -1;

        for (int i = 0; i < MyApplication.currentUser.getSubAccounts().size(); i++) {
            if (MyApplication.currentUser.getSubAccounts().get(i).getPortfolioId() == MyApplication.selectedSubAccount.getPortfolioId()) {
                index = i;
            }
        }

        return index;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        /*getPortfolio = new GetPortfolio();
        getPortfolio.executeOnExecutor(MyApplication.threadPoolExecutor);*/
        showProgress = false;

        Actions.checkLanguage(this, started);

        Actions.InitializeSessionServiceV2(this);
        // Actions.InitializeMarketServiceV2(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            getPortfolio.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getPortfolio);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Portfolio ex", e.getMessage());
        }

        try {
            System.gc();
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void back(View v) {
        this.finish();
    }

//    @Override
//    public void onBackPressed() {
//        //super.onBackPressed();
//    }

    @Override
    public void onBackPressed() {
        Actions.exitApp(this);
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    @Override
    public void onItemClicked(View v, int position) {

        if (position > 0) {

            try {
                int stockID = Integer.parseInt(MyApplication.portfolio.getAllStockSummaries().get(position - 1).getStockId());

                if (stockID != -1)
                    startActivity(new Intent(PortfolioActivity.this, StockDetailActivity.class).putExtra("stockID", stockID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPermissions() {

        if (ContextCompat.checkSelfPermission(PortfolioActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(PortfolioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(PortfolioActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {

            new LoadKcc().executeOnExecutor(MyApplication.threadPoolExecutor);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            Log.wtf("permissions", "length " + permissions.length);
            Log.wtf("grantResults", "length " + grantResults.length);

            if (grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) { //GRANTED
                Log.wtf("granted", "granted");
                new LoadKcc().executeOnExecutor(MyApplication.threadPoolExecutor);

            } else {

                Log.wtf("not", "granted");
            }
        }
    }

    public void initializePortfolioStockList() {

        if (MyApplication.portfolio.getAllStockSummaries().size() != 0) {
            tvNoData.setVisibility(View.GONE);

            llTotalStocks.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(getResources().getString(R.string.normal_theme), true)
                    ? R.color.white : R.color.colorDarkTheme));
            llTotalStocks.setVisibility(View.VISIBLE);

            arrayOfStocks.addAll(MyApplication.portfolio.getAllStockSummaries());
            portoflioStockForwardAdapter.notifyDataSetChanged();

            double value = 0;
            double gainLossValue = 0;

            try {

                for (int i = 0; i < arrayOfStocks.size(); i++) {

                    StockSummary stockSummary = arrayOfStocks.get(i);

                    if (stockSummary.getId() != -1) {

                        String totalCost = stockSummary.getTotalCost().replace(",", "");
                        value += Double.parseDouble(totalCost);

                        String gainLoss = stockSummary.getUnrealized();
                        if (!gainLoss.contains("(")) {

                            String gainValue = gainLoss.replace(",", "");
                            gainLossValue += Double.parseDouble(gainValue);

                        } else {

                            gainLoss = gainLoss.replaceAll("[()]", "");
                            String gainValue = gainLoss.replace(",", "");
                            gainLossValue -= Double.parseDouble(gainValue);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            tvTotalValue.setText(Actions.formatNumber(value, Actions.ThreeDecimalThousandsSeparator));
            tvTotalGain.setText(Actions.formatNumber(gainLossValue, Actions.ThreeDecimalThousandsSeparator));

            tvTotalGain.setTextColor(Actions.textColor(tvTotalGain.getText().toString()));

            tvTotalValue.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
            tvTotalGain.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
            tvTotalValueText.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
            tvGainLossText.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        } else {

            //no data
            tvNoData.setVisibility(View.VISIBLE);

            llTotalStocks.setVisibility(View.GONE);
        }

    }

    private class GetPortfolio extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            allValueItems.clear();
            adapter.notifyDataSetChanged();
            arrayOfStocks.clear();

            StockSummary fakeStock = new StockSummary();
            fakeStock.setId(-1);
            arrayOfStocks.add(fakeStock);

//            StockSummary stockss = new StockSummary();
//            stockss.setUnrealized("-45");
//            stockss.setUnrealizedPercent("+45");
//            stockss.setAvailableShares("+10");
//            stockss.setBid("+55");
//            stockss.setLast("-55");
//            arrayOfStocks.add(stockss);//header
            portoflioStockForwardAdapter.notifyDataSetChanged();

            if (showProgress)
                MyApplication.showDialog(PortfolioActivity.this);

        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetPortfolio.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("userId", MyApplication.selectedSubAccount.getUserId() + "");
            parameters.put("portfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
            parameters.put("key", MyApplication.mshared.getString(getString(R.string.afterkey), ""));
            parameters.put("Lang", Actions.getLanguage());

            try {
                result = ConnectionRequests.GET(url, PortfolioActivity.this, parameters);
                Log.wtf("porfolio", "result = " + result);
                MyApplication.portfolio = GlobalFunctions.GetPortfolio(result);
            } catch (IOException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetPortfolio.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetPortfolio.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            MyApplication.dismiss();

            allValueItems.addAll(MyApplication.portfolio.getCashSummary().getAllvalueItems());
            adapter.notifyDataSetChanged();

            rvUserData.setLayoutManager(llUserData);
            if (allValueItems.size() % 2 != 0) {

                llUserData.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {

                        //Log.wtf("position" , "is " + position);

                        if (position == allValueItems.size() - 1)
                            return MyApplication.GRID_VALUES_SPAN_COUNT;
                        else return 1;
                    }
                });
            } else {


                llUserData.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {

                        //Log.wtf("position" , "is " + position);

                        if (position == allValueItems.size() - 1) return 1;
                        else return 1;
                    }
                });
            }

            initializePortfolioStockList();
        }
    }

    protected class LoadKcc extends AsyncTask<Void, Void, Void> {

        File file;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MyApplication.showDialog(PortfolioActivity.this);
        }

        @Override
        protected Void doInBackground(Void... a) {
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "pdf");
            folder.mkdir();
            file = new File(folder, "download.pdf");
            try {
                file.createNewFile();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            //Actions.DownloadFile(MyApplication.baseLink + "LastStatement.aspx&portfolionumber=" + MyApplication.currentUser.getPortfolioNumber() + "&key=" + MyApplication.currentUser.getKey(), file);
            Actions.DownloadFile(MyApplication.baseLink + "LastStatement.aspx", file);
            return null;

        }

        @Override
        protected void onPostExecute(Void result) {

            MyApplication.dismiss();

            if (file.exists()) {

                //Uri path = Uri.fromFile(file);
                Uri path = FileProvider.getUriForFile(PortfolioActivity.this, getApplicationContext().getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(path, "application/pdf");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(PortfolioActivity.this, "No Application Available to View PDF", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

}
