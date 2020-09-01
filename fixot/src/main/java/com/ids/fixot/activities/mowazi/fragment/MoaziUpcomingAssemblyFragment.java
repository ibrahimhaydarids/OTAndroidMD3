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

public class MoaziUpcomingAssemblyFragment extends Fragment implements MoaziAssembliesRecyclerAdapter.RecyclerViewOnItemClickListener {

    private RecyclerView rvUpcomingAssembly;
    private MoaziAssembliesRecyclerAdapter adapter;
    private LinearLayoutManager llm;
    private ProgressBar pbLoading;
    private TextView btFilter,btClear,tvNoData;
    private int mYear, mMonth, mDay;
    private TextView tvFromDate, tvToDate,tv;
    private String fromDate = "", toDate = "", returnedDatefom = "", returnedDateto = "";
    private ArrayList<MoaziAssembly> allAssemblies = new ArrayList<MoaziAssembly>();
    private GetUpcomingAssembliesTask getUpcomingAssemblies;
    private SwipeRefreshLayout swipeContainer;
    private boolean flagLoading = false, pullToRefresh = false;
    private int visibleItemCount;
    private RelativeLayout rlFromDate, rlToDate;
    private int totalItemCount;
    private int pastVisibleItems;
    private RelativeLayout searchlayoutall;
    private ImageView ivFilter;
    boolean loadmore = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_moazi_upcoming_assembly, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvUpcomingAssembly = view.findViewById(R.id.rvUpcomingAssembly);
        pbLoading = view.findViewById(R.id.pbLoading);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        rlFromDate = view.findViewById(R.id.rlFromDate);
        rlToDate = view.findViewById(R.id.rlToDate);
        ivFilter = view.findViewById(R.id.ivFilter);
        searchlayoutall = view.findViewById(R.id.searchlayoutall);
        btFilter = view.findViewById(R.id.tvFilter);
        tvNoData = view.findViewById(R.id.tvNoData);
        tvFromDate = view.findViewById(R.id.tvFromDate);
        tvToDate = view.findViewById(R.id.tvToDate);
        btClear = view.findViewById(R.id.btClear);
      //  Actions.setViewResizing(getActivity());
        tv = getActivity().findViewById(R.id.tvAssemblies);
        if(MyApplication.lang==MyApplication.ENGLISH)
            tv.setTypeface(MyApplication.opensansbold);
        else
            tv.setTypeface(MyApplication.droidbold);

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
        allAssemblies.add(new MoaziAssembly());
        adapter = new MoaziAssembliesRecyclerAdapter(getActivity(), allAssemblies, null, this, MoaziAssembliesRecyclerAdapter.TYPE_UPCOMING);
        rvUpcomingAssembly.setAdapter(adapter);
        llm = new LinearLayoutManager(getActivity());
        rvUpcomingAssembly.setLayoutManager(llm);

        Log.d("Before upcomingAssembly", "task");
        //pbLoading.setVisibility(View.VISIBLE);


        getUpcomingAssemblies = new GetUpcomingAssembliesTask();
        //getUpcomingAssemblies.execute("0");
        getUpcomingAssemblies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"0");

        rvUpcomingAssembly.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisibleItems = llm.findFirstVisibleItemPosition();

                    if (flagLoading == false) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
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

        rvUpcomingAssembly.addItemDecoration(new SimpleDividerItemDecoration(getContext(),1,R.drawable.line_divider));
    }

    public void clear(View v) {
        fromDate = "";
        toDate = "";
        tvFromDate.setText(getString(R.string.from)) ;
        tvToDate.setText(getString(R.string.to));

    }

    public void search(View v) {
        pbLoading.setVisibility(View.VISIBLE);
        allAssemblies.clear();
        allAssemblies.add(new MoaziAssembly());
        getUpcomingAssemblies = new GetUpcomingAssembliesTask();
        //getUpcomingAssemblies.execute("0");
        getUpcomingAssemblies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"0");
        pbLoading.setVisibility(View.GONE);

    }

    public void back(View v) {
        getActivity().finish();
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
            getUpcomingAssemblies = new GetUpcomingAssembliesTask();
            //getUpcomingAssemblies.execute(String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId()));
            getUpcomingAssemblies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId())));
            Log.wtf("LastId", String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId()));
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
        getUpcomingAssemblies = new GetUpcomingAssembliesTask();
        //getUpcomingAssemblies.execute("0");
        getUpcomingAssemblies.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "0");
        swipeContainer.setRefreshing(false);
    }

    @Override
    public void onItemClicked(View v, int position) {
        Log.d("Item", "Clicked");
    }

    protected class GetUpcomingAssembliesTask extends AsyncTask<String, Void, String> {

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

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("fromDate", fromDate);
            parameters.put("toDate", toDate);
            parameters.put("countryId", "116");
            parameters.put("lang", MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            parameters.put("typeId", "1");
            parameters.put("top", "50");
            parameters.put("id", params[0]);
            result = ConnectionRequests.POST(url, parameters);

            MoaziAssemblyParser parser = new MoaziAssemblyParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                if (pullToRefresh) {
                    allAssemblies.clear();
                    allAssemblies.add(new MoaziAssembly());
                    pullToRefresh = false;
                }
                Log.wtf("allAssembliesSizeBef",""+allAssemblies.size());
                allAssemblies.addAll(parser.GetAssemblies());
                Log.wtf("allAssembliesSizeAft",""+allAssemblies.size());
                Log.wtf("LastIdbg", String.valueOf(allAssemblies.get(allAssemblies.size() - 1).getCommunityId()));
            } catch (JSONException e) {
                e.printStackTrace();
            }   catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }


        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.wtf("upcomming_result",result);
            try{
                pbLoading.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                onItemsLoadComplete();

                if(allAssemblies.size() == 1){
                    tvNoData.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
                }
                else {
                    tvNoData.setVisibility(View.GONE);
                    swipeContainer.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null)
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
