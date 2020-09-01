package com.ids.fixot.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.ids.fixot.adapters.InstrumentsRecyclerAdapter;
import com.ids.fixot.adapters.MarketsSpinnerAdapter;
import com.ids.fixot.adapters.OrderStatusAdapterSpinner;
import com.ids.fixot.adapters.OrdersRecyclerAdapter;
import com.ids.fixot.adapters.OrdersRecyclerAdapter.RefreshInterface;
import com.ids.fixot.adapters.SubAccountsSpinnerAdapter;
import com.ids.fixot.model.Instrument;
import com.ids.fixot.model.OnlineOrder;
import com.ids.fixot.model.OrderFilter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import static com.ids.fixot.MyApplication.lang;

/**
 * Created by user on 4/4/2017.
 */

public class OrdersActivity extends AppCompatActivity implements OrdersRecyclerAdapter.RecyclerViewOnItemClickListener, RefreshInterface, MarketStatusListener {

    ImageView ivBack;
    Toolbar myToolbar;
    RecyclerView rvOrders;
    LinearLayoutManager llm;
    ProgressBar progressBar;
    OrdersRecyclerAdapter adapter;
    RelativeLayout rootLayout;
    Spinner spSubAccounts;
    Spinner spStatus;
    SubAccountsSpinnerAdapter subAccountsSpinnerAdapter;
    GetUserOrders getUserOrders;
    TextView tvSymbolHeader, tvPriceHeader, tvQuantityHeader, tvExecutedQuantityHeader, tvActionHeader, tvStatusHeader, tvLogout, tvInstruments;
    int ii = 0;
    private BroadcastReceiver receiver;
    private ArrayList<OnlineOrder> allOrders = new ArrayList<>();
    private boolean started = false;
    private boolean running = true;
    ArrayList<String> arrayFilers=new ArrayList<>();

    ArrayList<OrderFilter> newArrayFilter=new ArrayList<>();

    private boolean isSelectedFilter=false;
    private ImageView ivDescriptionStatusArrow;
    private Activity activity;

    public OrdersActivity() {
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
         activity=this;
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_orders);
        Actions.initializeBugsTracking(this);

        findViews();

        Actions.initializeToolBar(getString(R.string.orders), OrdersActivity.this);

        Actions.showHideFooter(this);
        Actions.overrideFonts(this, rootLayout, false);

        started = true;

        Actions.setTypeface(new TextView[]{tvSymbolHeader, tvPriceHeader, tvInstruments, tvQuantityHeader, tvExecutedQuantityHeader, tvActionHeader, tvStatusHeader},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        tvLogout.setTypeface((lang == MyApplication.ARABIC) ? MyApplication.droidbold : MyApplication.giloryBold);
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    public void back(View v) {
        finish();
    }


    private void findViews() {

/*        OrderFilter o1=new OrderFilter(0,getString(R.string.all));
        OrderFilter o2=new OrderFilter(0,getString(R.string.order_filter_1));
        OrderFilter o3=new OrderFilter(0,getString(R.string.order_filter_2));
        OrderFilter o4=new OrderFilter(0,getString(R.string.order_filter_3));
        newArrayFilter=new ArrayList<>();
        newArrayFilter.add(o1);
        newArrayFilter.add(o2);
        newArrayFilter.add(o3);
        newArrayFilter.add(o4);*/


        spStatus=findViewById(R.id.spFilter);
        ivDescriptionStatusArrow=(ImageView)findViewById(R.id.ivDescriptionStatusArrow);
        ivDescriptionStatusArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spStatus.performClick();
            }
        });
        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);
        rootLayout = findViewById(R.id.rootLayout);
        myToolbar = findViewById(R.id.my_toolbar);
        ivBack = myToolbar.findViewById(R.id.ivBack);
        ivBack.setVisibility((BuildConfig.GoToMenu) ? View.VISIBLE : View.GONE);

        tvLogout = myToolbar.findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(v -> Actions.logout(OrdersActivity.this));
        tvLogout.setVisibility((BuildConfig.GoToMenu) ? View.GONE : View.VISIBLE);


        tvInstruments = findViewById(R.id.tvInstruments);
        tvSymbolHeader = findViewById(R.id.tvSymbolHeader);
        tvPriceHeader = findViewById(R.id.tvPriceHeader);
        tvQuantityHeader = findViewById(R.id.tvQuantityHeader);
        tvExecutedQuantityHeader = findViewById(R.id.tvExecutedQuantityHeader);
        tvActionHeader = findViewById(R.id.tvActionHeader);
        tvStatusHeader = findViewById(R.id.tvStatusHeader);

        try {
            llm = new LinearLayoutManager(OrdersActivity.this);
            adapter = new OrdersRecyclerAdapter(OrdersActivity.this, allOrders, this, this);
            rvOrders.setLayoutManager(llm);
            rvOrders.setAdapter(adapter);
        } catch (Exception e) {
            Log.wtf("OrdersRecyclerAdapter error", "error = " + e.getMessage());
        }

        spSubAccounts = findViewById(R.id.spSubAccounts);
        subAccountsSpinnerAdapter = new SubAccountsSpinnerAdapter(this, MyApplication.currentUser.getSubAccounts());
        spSubAccounts.setAdapter(subAccountsSpinnerAdapter);
        spSubAccounts.setSelection(returnAccountIndex());
        spSubAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.selectedSubAccount = subAccountsSpinnerAdapter.getItem(position);

                if (Actions.isNetworkAvailable(OrdersActivity.this)) {
                    getUserOrders = new GetUserOrders();
                    getUserOrders.executeOnExecutor(MyApplication.threadPoolExecutor);
                } else {
                    Actions.CreateDialog(OrdersActivity.this, getString(R.string.no_net), false, false);
                }
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
    protected void onStop() {
        super.onStop();
        running = false;
        Actions.unregisterMarketReceiver(this);
        Actions.unregisterSessionReceiver(this);
    }

    @Override
    public void refreshData() {

        allOrders.clear();
        adapter.notifyDataSetChanged();
        getUserOrders = new GetUserOrders();
        getUserOrders.executeOnExecutor(MyApplication.threadPoolExecutor);
    }

    @Override
    public void onItemClicked(View v, int position) {

        Bundle b = new Bundle();
        b.putParcelable("order", allOrders.get(position));
        Intent i = new Intent();
        i.putExtras(b);
        i.setClass(OrdersActivity.this, OrderDetailsActivity.class);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
        Actions.InitializeSessionServiceV2(this);
        //  Actions.InitializeMarketServiceV2(this);

        Actions.checkLanguage(this, started);
        running = true;
        Log.wtf("Orders Activity OnResume", "Actions.isNetworkAvailable(this) : " + Actions.isNetworkAvailable(this) + " / running = " + running);

    }


    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    public void onBackPressed() {
        if (BuildConfig.GoToMenu) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            getUserOrders.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getUserOrders);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Orders ex", e.getMessage());
        }
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private class GetUserOrders extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetUserOrders.getValue(); // this method uses key after login

            while (running) {

                Log.wtf("Thread", "runnig " + ii);
                ii++;
                HashMap<String, String> parameters = new HashMap<String, String>();
                parameters.put("userId", MyApplication.selectedSubAccount.getUserId() + "");
                parameters.put("portfolioId", MyApplication.selectedSubAccount.getPortfolioId() + "");
                parameters.put("key", MyApplication.currentUser.getKey());
                parameters.put("Lang", Actions.getLanguage());

                try {
                    result = ConnectionRequests.GET(url, OrdersActivity.this, parameters);
                    publishProgress(result);

                } catch (Exception e) {
                    e.printStackTrace();
                    if (MyApplication.isDebug) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetUserOrders.getKey(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                try {

                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            try {
                allOrders.clear();
                ArrayList<OnlineOrder> retrievedOrders = GlobalFunctions.GetOnlineOrders(values[0]);
                allOrders.addAll(retrievedOrders);
               try {
                   if (arrayFilers.size() == 0)
                       setfilterArray();
               }catch (Exception e){
                   setfilterArray();
               }
                runOnUiThread(() -> adapter.notifyDataSetChanged());

                Log.wtf("allOrders", "ss " + retrievedOrders.size());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    private void setfilterArray(){
/*       if(!containsFilter(getString(R.string.all)))
           arrayFilers.add(getString(R.string.all));
        for(int i=0;i<allOrders.size();i++){
            if(!containsFilter(allOrders.get(i).getStatusDescription()))
                arrayFilers.add(allOrders.get(i).getStatusDescription());
        }*/
        arrayFilers=new ArrayList<>();
        arrayFilers.add(getString(R.string.all));
        arrayFilers.add(getString(R.string.order_filter_1));
        arrayFilers.add(getString(R.string.order_filter_2));
        arrayFilers.add(getString(R.string.order_filter_3));
        setSpinner();
    }








    private boolean containsFilter(String statusDescription){
        boolean isContain=false;
        for (int i=0;i<arrayFilers.size();i++){
            if(arrayFilers.get(i).matches(statusDescription))
                isContain=true;
        }
        return isContain;
    }

    private void setSpinner(){

        OrderStatusAdapterSpinner StatusAdapter=new OrderStatusAdapterSpinner(this,arrayFilers);
        spStatus.setAdapter(StatusAdapter);


        spStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSelectedFilter = false;
                String selectedStatus=arrayFilers.get(position);
                ArrayList<OnlineOrder> arrayFiltered=new ArrayList<>();
                if( arrayFilers.get(position) == getString(R.string.all)){
                    arrayFiltered = allOrders;
                    adapter = new OrdersRecyclerAdapter(OrdersActivity.this, allOrders, OrdersActivity.this, OrdersActivity.this);
                    rvOrders.setAdapter(adapter);
                }else{
                    filterOrders(position);
                }

              }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }





    private void filterOrders(int indexOrdrStatus){
        ArrayList<OnlineOrder> arrayFiltered=new ArrayList<>();

        if(indexOrdrStatus==0){
            arrayFiltered.addAll(allOrders);
        }else {
            for(int i=0;i<allOrders.size();i++){
                if(allOrders.get(i).getOrderStatusTypeID()==indexOrdrStatus)
                    arrayFiltered.add(allOrders.get(i));
            }
        }


        adapter = new OrdersRecyclerAdapter(OrdersActivity.this, arrayFiltered, this, this);
        rvOrders.setAdapter(adapter);

    }
}
