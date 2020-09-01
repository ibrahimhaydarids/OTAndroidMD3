package com.ids.fixot.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.StockOrderBookRecyclerAdapter;
import com.ids.fixot.model.StockOrderBook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by DEV on 4/12/2018.
 */

public class OrderBookFragment extends Fragment {

    RecyclerView rvOrders;
    private ArrayList<StockOrderBook> allOrders = new ArrayList<>();
    LinearLayoutManager llm;
    LinearLayout rootLayout;
    LinearLayout order_book_header;

    TextView tvAskNumberHeader, tvAskQtyHeader, tvPriceHeader, tvBidNumberHeader, tvBidQtyHeader;

    StockOrderBookRecyclerAdapter adapter;
    GetStockOrderBook getStockOrderBook;
    public CountDownTimer requestTimer;

    int stockId = 0;
    private boolean started = false;
    boolean running = true;

    public OrderBookFragment() {

    }

    public static Fragment newInstance(Context context) {

        return new OrderBookFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        started = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        running = false;

        try{requestTimer.cancel();}catch (Exception e){}
        Actions.unregisterMarketReceiver(getActivity());
        Actions.unregisterSessionReceiver(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        try{requestTimer.cancel();}catch (Exception e){}
        MyApplication.sessionOut = Calendar.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();

        try{requestTimer.start();}catch (Exception e){}
        Log.wtf("Orders Activity OnResume", "Actions.isNetworkAvailable(this) : " + Actions.isNetworkAvailable(getActivity()) + " / running = " + running);
        if (Actions.isNetworkAvailable(getActivity())) {

            getStockOrderBook = new GetStockOrderBook();
            getStockOrderBook.executeOnExecutor(MyApplication.threadPoolExecutor);

        } else {
            Actions.CreateDialog(getActivity(), getString(R.string.no_net), false, false);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            getStockOrderBook.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getStockOrderBook);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("getStockOrderBookByOrder ex", e.getMessage());
        }

        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        stockId = this.getArguments().getInt("stockId");
        return inflater.inflate(R.layout.fragment_order_book, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();

        if (Actions.isNetworkAvailable(getActivity())) {

            getStockOrderBook= new GetStockOrderBook();
            getStockOrderBook.executeOnExecutor(MyApplication.threadPoolExecutor);

        } else {

            Actions.CreateDialog(getActivity(), getString(R.string.no_net), false, false);
        }

        Actions.overrideFonts(getActivity(), rootLayout, false);
        Actions.setTypeface(new TextView[]{tvAskNumberHeader, tvAskQtyHeader, tvAskNumberHeader, tvPriceHeader, tvBidNumberHeader, tvBidQtyHeader},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);


    }

    private void findViews() {

        rvOrders = getActivity().findViewById(R.id.rvOrders);

        rootLayout = getActivity().findViewById(R.id.root_Layout);
        order_book_header = getActivity().findViewById(R.id.order_book_header);

        tvAskNumberHeader = getActivity().findViewById(R.id.tvAskNumberHeader);
        tvAskQtyHeader = getActivity().findViewById(R.id.tvTimeHeader);
        tvPriceHeader = getActivity().findViewById(R.id.tvPriceHeader);
        tvBidNumberHeader = getActivity().findViewById(R.id.tvBidNumberHeader);
        tvBidQtyHeader = getActivity().findViewById(R.id.tvBidQtyHeader);

        llm = new LinearLayoutManager(getActivity());
        rvOrders.setLayoutManager(llm);

        adapter = new StockOrderBookRecyclerAdapter(getActivity() ,allOrders,true);
        rvOrders.setLayoutManager(llm);
        rvOrders.setAdapter(adapter);

    }


    private class GetStockOrderBook extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.wtf("stock_request","byPrice");
            try {
//                if(showLoading)
//                    MyApplication.showDialog(StockOrderBookActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {


            String result = "";
            String url = MyApplication.link + MyApplication.GetStockOrderBook.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("stockId", "" + stockId);
            parameters.put("key", MyApplication.currentUser.getKey());


            Log.wtf("GetStockOrderBook", "GetStockOrderBook");
            try {
                result = ConnectionRequests.GET(url, getActivity(), parameters);

                publishProgress(result);

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.GetStockOrderBook.getKey(), Toast.LENGTH_LONG).show();
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

            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            try {
                allOrders.clear();
                allOrders.addAll(GlobalFunctions.GetStockOrderBooks(values[0]));

                getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());

                MyApplication.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.wtf("stocksSize", allOrders.size() + "");
            adapter.notifyDataSetChanged();
            setTimer(1000);

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }


    private void setTimer(int timeInMilliSecond){

        try{requestTimer.cancel();}catch (Exception e){}
        requestTimer = new CountDownTimer(timeInMilliSecond, 1000) {

            @Override
            public void onTick(long arg0) {
            }

            @Override
            public void onFinish() {

                try {

                    getStockOrderBook = new GetStockOrderBook();
                    getStockOrderBook.executeOnExecutor(MyApplication.threadPoolExecutor);
                   // MyApplication.threadPoolExecutor.getQueue().remove(getStockOrderBook);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.wtf("getStockOrderBook ex", e.getMessage());
                }

            }

        };
        requestTimer.start();

    }

}
