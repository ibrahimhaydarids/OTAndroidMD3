package com.ids.fixot.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.ids.fixot.adapters.TopsRecyclerAdapter;
import com.ids.fixot.model.Stock;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by DEV on 4/12/2018.
 */

public class TopValuesFragment extends Fragment {

    RecyclerView rvTops;
    SwipeRefreshLayout swipeContainer;
    ArrayList<Stock> allStocks = new ArrayList<>();
    GetTops getTops;
    LinearLayoutManager llm;
    LinearLayout rootLayout;
    LinearLayout topsHeader;
    TextView tvTrading, tvSymbol, tvChange, tvChangePercent, tvPrice;
    private TopsRecyclerAdapter adapter;

    public TopValuesFragment() {

    }

    public static Fragment newInstance(Context context) {

        return new TopValuesFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_tops, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews();

        if (Actions.isNetworkAvailable(getActivity())) {

            getTops = new GetTops();
            getTops.execute();
        } else {

            Actions.CreateDialog(getActivity(), getString(R.string.no_net), false, false);
        }

        Actions.overrideFonts(getActivity(), rootLayout, false);
        Actions.setTypeface(new TextView[]{tvSymbol, tvTrading, tvPrice, tvChange, tvChangePercent}, MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
    }

    private void findViews() {

        rootLayout = getActivity().findViewById(R.id.root_Layout);
        topsHeader = getActivity().findViewById(R.id.tops_header);
        rvTops = getActivity().findViewById(R.id.rvTops);
        tvTrading = getActivity().findViewById(R.id.tvTrading);
        tvSymbol = getActivity().findViewById(R.id.tvSymbol);
        tvChangePercent = getActivity().findViewById(R.id.tvChangePercent);
        tvChange = getActivity().findViewById(R.id.tvChange);
        tvPrice = getActivity().findViewById(R.id.tvLast);

        tvTrading.setVisibility(View.VISIBLE);
        tvChange.setText(getResources().getString(R.string.trades_title));
        tvChangePercent.setText(getResources().getString(R.string.change_title));
        tvTrading.setText(getResources().getString(R.string.amount_title));


        swipeContainer = getActivity().findViewById(R.id.swipeContainer);
        llm = new LinearLayoutManager(getActivity());
        rvTops.setLayoutManager(llm);

        adapter = new TopsRecyclerAdapter(getActivity(), allStocks, MyApplication.TOP_VALUES);
        rvTops.setAdapter(adapter);

        swipeContainer.setOnRefreshListener(() -> {

            allStocks.clear();
            adapter.notifyDataSetChanged();
            getTops = new GetTops();
            getTops.execute();
            swipeContainer.setRefreshing(false);
        });
    }


    private class GetTops extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                MyApplication.showDialog(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.GetStockTops.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();

            try {
                parameters.clear();
                parameters.put("MarketID", MyApplication.marketID);
                parameters.put("count", "12");// MyApplication.count+"");
                parameters.put("key", MyApplication.currentUser.getKey());
                parameters.put("type", "" + MyApplication.TOP_VALUES);
                result = ConnectionRequests.GET(url, getActivity(), parameters);

                try {
                    allStocks.addAll(GlobalFunctions.GetStockTops(result));
                } catch (Exception e) {
                    e.printStackTrace();
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.GetStockTops.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.error_code) + MyApplication.GetStockTops.getKey(), Toast.LENGTH_LONG).show();
                    }
                });

            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.wtf("alStocks", allStocks.size() + "");

            try {

                adapter.notifyDataSetChanged();
                MyApplication.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
