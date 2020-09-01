package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.ids.fixot.adapters.OrderBookRecyclerAdapter;
import com.ids.fixot.fragments.OrderBookByOrderFragment;
import com.ids.fixot.fragments.OrderBookFragment;
import com.ids.fixot.model.StockQuotation;

import org.json.JSONException;
import org.json.JSONObject;



public class StockOrderBookActivity extends AppCompatActivity implements OrderBookRecyclerAdapter.RecyclerViewOnItemClickListener, MarketStatusListener {

    FragmentManager fragmentManager;
    LinearLayoutManager llm;
    LinearLayout rootLayout;
    int stockId = 0, ii = 0;
    StockQuotation stock = new StockQuotation();
    boolean isFavorite = false ;
    ImageView ivFavorite;
    TextView tvStockName;
    private BroadcastReceiver receiver;
    private Button btOrderPrice,btOrderByOrder;
    private TextView tvlast;
    public boolean showLoading=true;
    private boolean started = false;

    public StockOrderBookActivity() {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));

        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_stock_order_book);
        Actions.initializeBugsTracking(this);


        findViews();

        if (getIntent().hasExtra("stockId")) {
            stockId = getIntent().getExtras().getInt("stockId");
            String barTitle = getString(R.string.order_book) + "-" + getIntent().getExtras().getString("stockName");
            Actions.initializeToolBar(barTitle, StockOrderBookActivity.this);
            //  tvStockTitle.setVisibility(View.GONE);

            stock = Actions.getStockQuotationById(MyApplication.stockQuotations, getIntent().getExtras().getInt("stockId"));
            stock.setStockID(getIntent().getExtras().getInt("stockId"));

            if (getIntent().getExtras().getString("isFavorite") != null) {
                isFavorite = getIntent().getExtras().getString("isFavorite").equals("1");
            }

            Log.wtf("getIntent().getExtras().getInt(\"isFavorite\")", "is " + getIntent().getExtras().getInt("isFavorite"));
            Log.wtf("getIntent().getExtras().getString(\"isFavorite\")", "is " + getIntent().getExtras().getString("isFavorite"));
            ivFavorite.setImageResource(isFavorite ? R.drawable.added_to_favorites : R.drawable.add_to_favorites);

            setStockName(getIntent().getExtras().getString("securityId") + " - " + getIntent().getExtras().getString("stockName")); //getInt("stockId")


            try {
                if(getIntent().getExtras().getDouble("last") ==0){
                    //  tvlast.setText(MyApplication.lastId+"");
                    tvlast.setText(getLastFromId());
                }else
                    tvlast.setText(getIntent().getExtras().getDouble("last")+"");
            }catch (Exception e){

            }

        } else {
            Actions.initializeToolBar(getString(R.string.order_book), StockOrderBookActivity.this);
        }

        Actions.showHideFooter(this);

        setListeners();

        Actions.overrideFonts(this, rootLayout, false);
        Actions.setTypeface(new TextView[]{  tvStockName},  MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

        btOrderByOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showLoading=true;
                setOrderByOderActive();

                Bundle bundle = new Bundle();
                bundle.putInt("stockId", stockId );

                OrderBookByOrderFragment orderBookByOrderFragment = new OrderBookByOrderFragment();
                orderBookByOrderFragment.setArguments(bundle);


                if(getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                }


                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container, orderBookByOrderFragment, "OrderBookByOrderFragment")
                        .commit();


//                    getStockOrderBookByOrder = new GetStockOrderBookByOrder();
//                    getStockOrderBookByOrder.executeOnExecutor(MyApplication.threadPoolExecutor);

            }
        });


        btOrderPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putInt("stockId", stockId );

                OrderBookFragment oderBookFragment = new OrderBookFragment();
                oderBookFragment.setArguments(bundle);

                if(getSupportFragmentManager().findFragmentById(R.id.container) != null) {
                    getSupportFragmentManager()
                            .beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.container)).commit();
                }

                showLoading=true;
                setOrderPriceActive();

                fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container, oderBookFragment, "OrderBookFragment")
                        .commit();

            }
        });


        Bundle bundle = new Bundle();
        bundle.putInt("stockId", stockId );

        OrderBookFragment oderBookFragment = new OrderBookFragment();
        oderBookFragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.container, oderBookFragment, "OrderBookFragment")
                .commit();
   }

    private void setStockName(String stockName) {
        tvStockName.setText(stockName);
        if (MyApplication.lang == MyApplication.ARABIC) {
            tvStockName.setTypeface(MyApplication.droidbold);
        } else {
            tvStockName.setTypeface(MyApplication.giloryBold);
        }
    }


    private void findViews() {

        rootLayout = findViewById(R.id.rootLayout);

        //  tvStockTitle = findViewById(R.id.market_time_value_textview);

        tvStockName = findViewById(R.id.stockName);
        ivFavorite = findViewById(R.id.ivFavorite);
        ivFavorite.setOnClickListener(v -> new StockOrderBookActivity.AddRemoveFavoriteStock().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));

        btOrderByOrder=(Button)findViewById(R.id.btOrderBookOrder);
        btOrderPrice=(Button)findViewById(R.id.btOrderBookPrice);
        tvlast=(TextView)findViewById(R.id.tvlast);


    }

    private String getLastFromId(){
        String last="";
       try {
           for (int i=0;i< MyApplication.stockQuotations.size();i++){
               if(MyApplication.stockQuotations.get(i).getStockID()==stockId) {
                   last = MyApplication.stockQuotations.get(i).getLast() + "";
                   return last;

               }

           }
          return last;
       }catch (Exception e){
           return "";
       }

    }

    private void setOrderPriceActive(){
        btOrderPrice.setBackgroundResource(R.drawable.order_book_border_active);
        btOrderPrice.setTextColor(getResources().getColor(R.color.colorDark));

        btOrderByOrder.setBackgroundResource(R.drawable.order_book_border_disable);
        btOrderByOrder.setTextColor(getResources().getColor(R.color.colorValues));

        setListeners();
    }


    private void setOrderByOderActive(){
        btOrderByOrder.setBackgroundResource(R.drawable.order_book_border_active);
        btOrderByOrder.setTextColor(getResources().getColor(R.color.colorDark));


        btOrderPrice.setBackgroundResource(R.drawable.order_book_border_disable);
        btOrderPrice.setTextColor(getResources().getColor(R.color.colorValues));

        setListeners();

    }

    public void goToTrade(View v) {

        switch (v.getId()) {

            case R.id.btSell:

                Bundle sellBundle = new Bundle();
                sellBundle.putParcelable("stockQuotation", stock);
                sellBundle.putInt("action", MyApplication.ORDER_SELL);
                Intent sellIntent = new Intent(StockOrderBookActivity.this, TradesActivity.class);
                sellIntent.putExtras(sellBundle);
                StockOrderBookActivity.this.startActivity(sellIntent);
                break;

            case R.id.btBuy:

                Bundle buyBundle = new Bundle();
                buyBundle.putParcelable("stockQuotation", stock);
                buyBundle.putInt("action", MyApplication.ORDER_BUY);
                Intent buyIntent = new Intent(StockOrderBookActivity.this, TradesActivity.class);
                buyIntent.putExtras(buyBundle);
                StockOrderBookActivity.this.startActivity(buyIntent);
                break;
        }

    }

    private void setListeners() {

//        llm = new LinearLayoutManager(StockOrderBookActivity.this);
//        adapter = new StockOrderBookRecyclerAdapter(StockOrderBookActivity.this, allOrders,isOrderByPrice);

    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
    }

    public void back(View v) {

        finish();
    }

    public void close(View v) {
        this.finish();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);
        Actions.checkLanguage(this, started);
        Actions.InitializeSessionServiceV2(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onItemClicked(View v, int position) {

    }

    private class AddRemoveFavoriteStock extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            try {
                MyApplication.showDialog(StockOrderBookActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.AddFavoriteStocks.getValue();

            if (isFavorite) {
                url = MyApplication.link + MyApplication.RemoveFavoriteStocks.getValue();
            }

            String stringer = "{\"StockIDs\":[\"" + stock.getStockID() + "\"],\"UserID\":" + MyApplication.currentUser.getId()
                    + ",\"key\":\"" + getString(R.string.beforekey) + "\"}";

            result = ConnectionRequests.POSTWCF2(url, stringer);

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

            JSONObject object = null;
            try {
                object = new JSONObject(result);
                String success = object.getString("MessageEn");
                if (success.equals("Success")) {

                    if (!isFavorite) {
                        Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.save_success), false, false);
                        ivFavorite.setImageResource(R.drawable.added_to_favorites);
                        isFavorite = true;
                    } else {
                        Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.delete_success), false, false);
                        ivFavorite.setImageResource(R.drawable.add_to_favorites);
                        isFavorite = false;
                    }
                } else {
                    Actions.CreateDialog(StockOrderBookActivity.this, getString(R.string.error), false, false);
                }
            } catch (JSONException e) {
                e.printStackTrace();


                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (isFavorite) {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.RemoveFavoriteStocks.getKey(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.AddFavoriteStocks.getKey(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        }
    }


}
