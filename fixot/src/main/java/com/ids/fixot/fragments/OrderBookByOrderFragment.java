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
import com.ids.fixot.adapters.StockOrderBookByOrderRecyclerAdapter;
import com.ids.fixot.model.StockOrderBook;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by DEV on 4/12/2018.
 */

public class OrderBookByOrderFragment extends Fragment  implements SwipeRefreshLayout.OnRefreshListener {

    RecyclerView rvOrders,rvOrdersSecond;
    SwipeRefreshLayout swipeContainer;
    private ArrayList<StockOrderBook> allOrders = new ArrayList<>();
    private ArrayList<StockOrderBook> allOrdersFirst = new ArrayList<>();
    private ArrayList<StockOrderBook> allOrdersSecond = new ArrayList<>();
    LinearLayoutManager llm,llms;
    LinearLayout rootLayout;
    LinearLayout order_book_header,order_book_header_second;

    TextView tvTimeHeader, tvPriceHeader, tvQtyHeader,tvTimeHeaderSecond, tvPriceHeaderSecond, tvQtyHeaderSecond;

    StockOrderBookByOrderRecyclerAdapter adapter,secondAdapter;
    GetStockOrderBook getStockOrderBook;
    public CountDownTimer requestTimer;

    int stockId = 0;
    boolean running = false;

    public OrderBookByOrderFragment() {

    }

    public static Fragment newInstance(Context context) {

        return new OrderBookByOrderFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStop() {
        super.onStop();

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

            if(running == true){
                getStockOrderBook.cancel(true);

                getStockOrderBook = new GetStockOrderBook();
                getStockOrderBook.executeOnExecutor(MyApplication.threadPoolExecutor);
            }

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
        return inflater.inflate(R.layout.fragment_order_book_by_order, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();

        Actions.overrideFonts(getActivity(), rootLayout, false);
        Actions.setTypeface(new TextView[]{tvTimeHeader, tvPriceHeader, tvQtyHeader,tvTimeHeaderSecond, tvPriceHeaderSecond, tvQtyHeaderSecond},
                MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);


    }

    private void findViews() {

        rvOrders = getActivity().findViewById(R.id.rvOrders);
        rvOrdersSecond = getActivity().findViewById(R.id.rvOrdersSecond);

        rootLayout = getActivity().findViewById(R.id.root_Layout);
        order_book_header = getActivity().findViewById(R.id.order_book_by_order_header);
        order_book_header_second  = getActivity().findViewById(R.id.order_book_second_header);

        tvTimeHeader = getActivity().findViewById(R.id.tvTimeHeader);
        tvPriceHeader = getActivity().findViewById(R.id.tvPriceHeader);
        tvQtyHeader = getActivity().findViewById(R.id.tvQtyHeader);

        tvTimeHeaderSecond = getActivity().findViewById(R.id.tvTimeHeaderS);
        tvPriceHeaderSecond = getActivity().findViewById(R.id.tvPriceHeaderS);
        tvQtyHeaderSecond = getActivity().findViewById(R.id.tvQtyHeaderS);

       // swipeContainer = getActivity().findViewById(R.id.swipeContainer);
        adapter =  new StockOrderBookByOrderRecyclerAdapter(getActivity() ,allOrdersFirst,false);
        llm = new LinearLayoutManager(getActivity());
        rvOrders.setLayoutManager(llm);
        rvOrders.setAdapter(adapter);

        swipeContainer = getActivity().findViewById(R.id.swipeContainerSecond);
        secondAdapter = new StockOrderBookByOrderRecyclerAdapter(getActivity() ,allOrdersSecond,true);
        llms = new LinearLayoutManager(getActivity());
        rvOrdersSecond.setLayoutManager(llms);
        rvOrdersSecond.setAdapter(secondAdapter);

        swipeContainer.setOnRefreshListener(this);


        if (Actions.isNetworkAvailable(getActivity())) {

            getStockOrderBook= new GetStockOrderBook();
            getStockOrderBook.executeOnExecutor(MyApplication.threadPoolExecutor);

        } else {

            Actions.CreateDialog(getActivity(), getString(R.string.no_net), false, false);
        }

    }


    @Override
    public void onRefresh() {
        swipeContainer.setRefreshing(true);

        getStockOrderBook.cancel(true);
        allOrders.clear();
        secondAdapter.notifyDataSetChanged();


        getStockOrderBook= new GetStockOrderBook();
        getStockOrderBook.executeOnExecutor(MyApplication.threadPoolExecutor);

        swipeContainer.setRefreshing(false);


    }

    private class GetStockOrderBook extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.wtf("stock_request","byOrder");

            try {
            //    MyApplication.showDialog(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetStockOrderBookByOrder.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("StockIDs", "" + stockId);
            parameters.put("key", MyApplication.currentUser.getKey());
            parameters.put("tradingSession", "0");
            parameters.put("MarketID", MyApplication.marketID);
            parameters.put("Tstamp", "0");

            Log.wtf("GetOrderBookByOrder", "GetOrderBookByOrder");
            try {
                result = ConnectionRequests.GET(url, getActivity(), parameters);

                publishProgress(result);

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.GetStockOrderBookByOrder.getKey(), Toast.LENGTH_LONG).show();
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
            //   }

            return result;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            try {
                allOrders.clear();
                allOrders.addAll(GlobalFunctions.GetStockOrderBooks(values[0]));

                allOrdersFirst.clear();
                allOrdersSecond.clear();

                for (int i = 0; i < allOrders.size(); i++) {

                    if (allOrders.get(i).getTradeType() == 2) {
                        allOrdersFirst.add(allOrders.get(i));
                    }
                    else if (allOrders.get(i).getTradeType() == 1) {
                        allOrdersSecond.add(allOrders.get(i));
                    }
                }

                getActivity().runOnUiThread(() ->
                    adapter.notifyDataSetChanged()
                );

                getActivity().runOnUiThread(() ->
                    secondAdapter.notifyDataSetChanged()
                );


                MyApplication.dismiss();
                MyApplication.dismiss();

                running = true;

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
        }
    }

}