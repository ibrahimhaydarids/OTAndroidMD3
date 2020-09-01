package com.ids.fixot.activities.mowazi.fragment;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.adapters.mowaziAdapters.MoaziAssembliesRecyclerAdapter;
import com.ids.fixot.model.mowazi.MoaziAssembly;
import com.ids.fixot.model.mowazi.SimpleDividerItemDecoration;
import com.ids.fixot.parser.MoaziAssemblyParser;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


/**
 * Created by dev on 6/14/2016.
 */

public class MoaziPreviousAssemblyFragment extends Fragment implements MoaziAssembliesRecyclerAdapter.RecyclerViewOnItemClickListener {

    private RecyclerView rvPreviousAssembly;
    private MoaziAssembliesRecyclerAdapter adapter;
    private LinearLayoutManager llm;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar pbLoading;
    private GetPreviousAssembliesTask getPreviousAssemblies;
    private ArrayList<MoaziAssembly> allAssemblies = new ArrayList<MoaziAssembly>();
    private RelativeLayout rlFromDate, rlToDate;
    private boolean flagLoading = false, pullToRefresh = false;
    private int visibleItemCount, mYear, mMonth, mDay;
    private TextView tvFromDate, tvToDate, tv, tvDeals;
    private String fromDate = "", toDate = "", returnedDatefom = "", returnedDateto = "";
    private int totalItemCount;
    private int pastVisibleItems;
    private TextView btFilter, btClear, tvNoData;
    private RelativeLayout searchlayoutall;
    private ImageView ivFilter;
    boolean loadmore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_moazi_previous_assembly, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Actions.setViewResizing(getActivity());
        rvPreviousAssembly = view.findViewById(R.id.rvPreviousAssembly);
        pbLoading = view.findViewById(R.id.pbLoading);
        ivFilter = view.findViewById(R.id.ivFilter);
        tvNoData = view.findViewById(R.id.tvNoData);
        tv = getActivity().findViewById(R.id.tvAssemblies);

        swipeContainer = view.findViewById(R.id.swipeContainer);
        rlFromDate = view.findViewById(R.id.rlFromDate);
        searchlayoutall = view.findViewById(R.id.searchlayoutall);
        rlToDate = view.findViewById(R.id.rlToDate);
        btFilter = view.findViewById(R.id.tvFilter);
        btClear = view.findViewById(R.id.btClear);
        tvFromDate = view.findViewById(R.id.tvFromDate);
        tvToDate = view.findViewById(R.id.tvToDate);
        tvDeals = view.findViewById(R.id.tvDeals);
        swipeContainer.setRefreshing(false);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeContainer.setRefreshing(false);
                refreshItems();
                pullToRefresh = true;
                swipeContainer.setRefreshing(false);
            }
        });

        swipeContainer.setRefreshing(false);

        if (MyApplication.lang == MyApplication.ENGLISH) {
            tv.setTypeface(MyApplication.opensansbold);
            tvDeals.setTypeface(MyApplication.opensansregular);
        } else {

            tv.setTypeface(MyApplication.droidbold);
            tvDeals.setTypeface(MyApplication.droidregular);
        }

        adapter = new MoaziAssembliesRecyclerAdapter(getActivity(), allAssemblies, null, this, MoaziAssembliesRecyclerAdapter.TYPE_PREVIOUS);
        rvPreviousAssembly.setAdapter(adapter);
        llm = new LinearLayoutManager(getActivity());
        rvPreviousAssembly.setLayoutManager(llm);
        Log.d("Before PreviousAssembly", "task");
        pbLoading.setVisibility(View.VISIBLE);


        getPreviousAssemblies = new GetPreviousAssembliesTask();
        //getPreviousAssemblies.execute("0");
        getPreviousAssemblies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");

        rvPreviousAssembly.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisibleItems = llm.findFirstVisibleItemPosition();

                    if (flagLoading == false) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            Log.d("in flag", "loading");
                            flagLoading = true;
                            addItems();
                        }
                    }
                }
            }
        });

        rlFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromDate = showDateTimePickerFrom(rlFromDate);
                Log.wtf("from date", fromDate);
            }
        });

        rlToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDate = showDateTimePickerFrom(rlToDate);
                Log.wtf("from date", toDate);
            }
        });

        btFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(view);
            }
        });

        ivFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeFilter(view);
            }
        });
        btClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear(view);
            }
        });

        rvPreviousAssembly.addItemDecoration(new SimpleDividerItemDecoration(getContext(), 1, R.drawable.line_divider));
    }

    public void back(View v) {
        getActivity().finish();
    }

    public void search(View v) {
        allAssemblies.clear();
        //allAssemblies.add(new MoaziAssembly());
        Log.wtf("fromDate", "" + fromDate);
        Log.wtf("toDate", "" + toDate);
        getPreviousAssemblies = new GetPreviousAssembliesTask();
        //getPreviousAssemblies.execute("0");
        getPreviousAssemblies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");
    }

    public void closeFilter(View v) {
        if (searchlayoutall.getVisibility() == View.VISIBLE) {
            searchlayoutall.setVisibility(View.GONE);
            fromDate = "";
            toDate = "";
        } else {
            searchlayoutall.setVisibility(View.VISIBLE);
            fromDate = returnedDatefom;
            toDate = returnedDateto;
        }
        Log.wtf("fromDate", "" + fromDate);
        Log.wtf("toDate", "" + toDate);
    }

    public void clear(View v) {
        fromDate = "";
        toDate = "";
        tvFromDate.setText(getString(R.string.from));
        tvToDate.setText(getString(R.string.to));

    }

    public String showDateTimePickerFrom(final View v) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        String mm = String.valueOf(monthOfYear);
                        if (mm.length() == 1)
                            mm = "0" + mm;
                        String dd = String.valueOf(dayOfMonth);
                        if (dd.length() == 1)
                            dd = "0" + dd;
                        if (v == rlFromDate) {
                            returnedDatefom = year + "-" + mm + "-" + dd;
                            tvFromDate.setText(year + "-" + mm + "-" + dd);
                            fromDate = returnedDatefom;

                        } else {
                            returnedDateto = year + "-" + mm + "-" + dd;
                            tvToDate.setText(year + "-" + mm + "-" + dd);
                            toDate = returnedDateto;
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

        return "";
    }

    public void addItems() {
        if (allAssemblies.size() > 0) {
            getPreviousAssemblies = new GetPreviousAssembliesTask();
            //getPreviousAssemblies.execute(String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId()));
            getPreviousAssemblies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId()));
            flagLoading = false;
            loadmore = true;
        }
    }

    private void onItemsLoadComplete() {
        Log.d("Recycler", "Refreshed");
        swipeContainer.setRefreshing(false);
    }

    public void refreshItems() {
        swipeContainer.setRefreshing(false);
        getPreviousAssemblies = new GetPreviousAssembliesTask();
        //getPreviousAssemblies.execute("0");
        getPreviousAssemblies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onItemClicked(View v, int position) {
        Log.d("Item", "Clicked");
    }

    protected class GetPreviousAssembliesTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pullToRefresh)
                pbLoading.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetAssemblies?";
            String lang;
            if (MyApplication.lang == MyApplication.ENGLISH)
                lang = "en";
            else
                lang = "ar";
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("fromDate", fromDate);
            parameters.put("toDate", toDate);
            parameters.put("countryId", "0");
            parameters.put("lang", lang);
            parameters.put("typeId", "2");
            parameters.put("top", "50");
            parameters.put("id", params[0]);
            result = ConnectionRequests.POST(url, parameters);

            MoaziAssemblyParser parser = new MoaziAssemblyParser(result, lang);
            try {
                if (pullToRefresh) {
                    allAssemblies.clear();
                    pullToRefresh = false;
                }
                if (!loadmore)
                    allAssemblies.add(new MoaziAssembly());
                allAssemblies.addAll(parser.GetAssemblies());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;

        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            try {

                pbLoading.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                onItemsLoadComplete();

                if (allAssemblies.size() == 1) {
                    tvNoData.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
                } else {
                    tvNoData.setVisibility(View.GONE);
                    swipeContainer.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null)
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
