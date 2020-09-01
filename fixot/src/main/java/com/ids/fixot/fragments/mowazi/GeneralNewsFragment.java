package com.ids.fixot.fragments.mowazi;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.mowazi.MowaziNewsDetailsActivity;
import com.ids.fixot.adapters.mowaziAdapters.MowaziNewsRecyclerAdapter;
import com.ids.fixot.model.mowazi.MowaziNews;
import com.ids.fixot.parser.AlmowaziNewsParser;
import com.ids.fixot.parser.GeneralNewsParser;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class GeneralNewsFragment extends Fragment implements
        MowaziNewsRecyclerAdapter.RecyclerViewOnItemClickListener {

    boolean flagLoading = false, pulltoRefresh = false;
    int companyId = 0;
    private LinearLayoutManager llm;
    private RelativeLayout llNewsLayout, rlFromDate, rlToDate;
    private RecyclerView rvNews;
    private TextView tvFromDate, tvToDate, tvNoData;
    private MowaziNewsRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<MowaziNews> allNews = new ArrayList<MowaziNews>();
    private ProgressBar pbNews;
    private TextView btFilter, btClear;
    private String fromDate = "", toDate = "", returnedDatefom, returnedDateto;
    private EditText etSearch;
    private GetNewsTask getNews;
    private int visibleItemCount;
    private int totalItemCount;
    private int mYear, mMonth, mDay;
    private int pastVisibleItems;
    private RelativeLayout searchlayoutall;
    private ImageView ivFilter;
    private TextView tvNews, tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return getActivity().getLayoutInflater().inflate(
                R.layout.fragment_mowazi_news_layout, null);
    }

    public void setImagePath() {
        if (MyApplication.allMowaziMobileConfigurations.size() != 0) {
            for (int i = 0; i < MyApplication.allMowaziMobileConfigurations.size(); i++) {
                if (MyApplication.allMowaziMobileConfigurations.get(i).getId() == 10)
                    MyApplication.mowaziImagePath = MyApplication.allMowaziMobileConfigurations
                            .get(i).getValue();
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle b = this.getArguments();
        companyId = b.getInt("companyId", 0);
        setImagePath();
        llNewsLayout = view.findViewById(R.id.llDealsLayout);
        searchlayoutall = view
                .findViewById(R.id.searchlayoutall);
        pbNews = view.findViewById(R.id.pbLastDeals);
        ivFilter = view.findViewById(R.id.ivFilter);
        tvNews = view.findViewById(R.id.tvDeals);

        if (MyApplication.lang == MyApplication.ARABIC) {
            llNewsLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            llNewsLayout.setTextDirection(View.TEXT_DIRECTION_RTL);
            tvNews.setText(getResources().getString(R.string.mowazi_generalNews));
            tvNews.setTypeface(MyApplication.droidregular);
        } else {

            tvNews.setText(getResources().getString(R.string.mowazi_generalNews));
            tvNews.setTypeface(MyApplication.opensansregular);
        }
        swipeContainer = view
                .findViewById(R.id.swipeContainer);

        swipeContainer
                .setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                    public void onRefresh() {
                        refreshItems();
                        pulltoRefresh = true;
                    }
                });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        llm = new LinearLayoutManager(getActivity());
        rvNews = view.findViewById(R.id.rvDeals);

        etSearch = view.findViewById(R.id.etSearch);

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    search(v);
                    InputMethodManager inputManager = (InputMethodManager) getActivity()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getActivity()
                                    .getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                }
                return false;
            }
        });

        rvNews.setLayoutManager(llm);
        adapter = new MowaziNewsRecyclerAdapter(getActivity(), allNews, this, 1);
        rvNews.setAdapter(adapter);
        // rvNews.addItemDecoration(new
        // SimpleDividerItemDecoration(getActivity(),0,R.drawable.line_divider));

        if (companyId != 0) {
            GetCompanyNewsTask get = new GetCompanyNewsTask();
            get.execute(String.valueOf(companyId));
        } else {
            getNews = new GetNewsTask();
            getNews.execute("0");
        }

        rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) // check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisibleItems = llm.findFirstVisibleItemPosition();

                    if (flagLoading == false) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            flagLoading = true;
                            addNews();
                        }
                    }
                }
            }
        });

        rlFromDate = view.findViewById(R.id.rlFromDate);
        rlToDate = view.findViewById(R.id.rlToDate);
        tvFromDate = view.findViewById(R.id.tvFromDate);
        btFilter = view.findViewById(R.id.tvFilter);
        tvNoData = view.findViewById(R.id.tvNoData);
        tvToDate = view.findViewById(R.id.tvToDate);
        btClear = view.findViewById(R.id.btClear);

        rlFromDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                fromDate = showDateTimePickerFrom(rlFromDate);
                Log.wtf("from date", fromDate);
            }
        });

        rlToDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                toDate = showDateTimePickerFrom(rlToDate);
                Log.wtf("from date", toDate);
            }
        });

        ivFilter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                closeFilter(view);
            }
        });

        btFilter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                search(view);
            }
        });

        btClear.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                clear(view);
            }
        });

        Actions.overrideFonts(getActivity(), llNewsLayout, true);
    }

    public void back(View v) {
        getActivity().finish();
    }

    public void clear(View v) {
        fromDate = "";
        toDate = "";

        tvFromDate.setText(getString(R.string.mowazi_from));
        tvToDate.setText(getString(R.string.mowazi_to));
        etSearch.setText("");
        companyId = 0;
        refreshItems();
    }

    public void closeFilter(View v) {
        if (searchlayoutall.getVisibility() == View.VISIBLE) {
            searchlayoutall.setVisibility(View.GONE);
            fromDate = "";
            toDate = "";
            etSearch.setText("");
            companyId = 0;
        } else {
            searchlayoutall.setVisibility(View.VISIBLE);
            if (returnedDatefom != null)
                fromDate = returnedDatefom;

            if (returnedDateto != null)
                toDate = returnedDateto;
        }

    }

    public void search(View v) {
        allNews.clear();
        // companyId=0;

        if (companyId == 0) {
            getNews = new GetNewsTask();
            getNews.execute("0");
        } else {
            GetCompanyNewsTask get = new GetCompanyNewsTask();
            get.execute("" + companyId);
        }
    }

    public String showDateTimePickerFrom(final View v) {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
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

    private void refreshItems() {
        allNews.clear();

        if (companyId == 0) {
            getNews = new GetNewsTask();
            getNews.execute("0");
        } else {
            GetCompanyNewsTask get = new GetCompanyNewsTask();
            get.execute("" + companyId);
        }
    }

    private void onItemsLoadComplete() {
        Log.d("Recycler", "Refreshed");
        swipeContainer.setRefreshing(false);
    }

    public void onItemClicked(View v, int position) {

        Intent i = new Intent();
        i.setClass(getActivity(), MowaziNewsDetailsActivity.class);
        i.putExtra("title", allNews.get(position).getTitle());
        i.putExtra("content", allNews.get(position).getContent());
        i.putExtra("date", allNews.get(position).getDate());
        i.putExtra("id", allNews.get(position).getId());
        i.putExtra("img", allNews.get(position).getPictureName());
        startActivity(i);
    }

    public void addNews() {

        if (companyId == 0) {
            if (allNews.size() > 0) {
                getNews = new GetNewsTask();
                getNews.execute(String.valueOf(allNews.get(allNews.size() - 1)
                        .getId()));
                flagLoading = false;
            }
        } else {
            GetCompanyNewsTask get = new GetCompanyNewsTask();
            // get.execute(""+companyId);
            get.execute(String.valueOf(allNews.get(allNews.size() - 1).getId()));
            flagLoading = false;
        }
    }

    protected class GetCompanyNewsTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbNews.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetCompanyNews?";

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("lang", MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            parameters.put("companyId", params[0]);

            Log.wtf("Mowazi Unlisted", "url : " + url);
            Log.wtf("Mowazi Unlisted", "parameters : " + parameters);

            result = ConnectionRequests.POST(url, parameters);

            AlmowaziNewsParser parser = new AlmowaziNewsParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                if (pulltoRefresh) {
                    allNews.clear();
                    pulltoRefresh = false;
                }
                allNews.addAll(parser.GetNews());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbNews.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            onItemsLoadComplete();
        }
    }

    protected class GetNewsTask extends AsyncTask<String, Void, String> {
        String searchTxt = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbNews.setVisibility(View.VISIBLE);
            searchTxt = etSearch.getText().toString();
            Log.wtf("pull", "" + pulltoRefresh);
            getActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + MyApplication.GetNews.getValue();

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", MyApplication.lang == MyApplication.ARABIC ? "1" : "2");
            parameters.put("searchText", searchTxt);
            parameters.put("showInactiveNews", "true");
            parameters.put("fromDate", fromDate);
            parameters.put("toDate", toDate);
            parameters.put("id", params[0]);
            parameters.put("top", "100");

            result = ConnectionRequests.POST(url, parameters);

            GeneralNewsParser parser = new GeneralNewsParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            try {
                if (pulltoRefresh) {
                    allNews.clear();
                    pulltoRefresh = false;
                }
                allNews.addAll(parser.GetNews());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);
            pbNews.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
            onItemsLoadComplete();
            try {
                if (allNews.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
                    // tvNoData.setText(getString(R.string.noenglishnews));
                    if (MyApplication.lang == MyApplication.ENGLISH)
                        tvNoData.setText(getString(R.string.mowazi_noenglishnews));
                } else {
                    tvNoData.setVisibility(View.GONE);
                    swipeContainer.setVisibility(View.VISIBLE);
                }
                if (getActivity() != null)
                    getActivity().getWindow().clearFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
