package com.ids.fixot.fragments.mowazi;

import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

public class CompaniesNewsFragment extends Fragment implements
        MowaziNewsRecyclerAdapter.RecyclerViewOnItemClickListener {

    boolean flagLoading = false, pulltoRefresh = false;
    private LinearLayoutManager llm;
    private RelativeLayout llNewsLayout, llFields;
    private LinearLayout llPickers;
    private RecyclerView rvNews;
    private MowaziNewsRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private ArrayList<MowaziNews> allNews = new ArrayList<MowaziNews>();
    private ProgressBar pbNews;
    private GetCompanyNewsTask getNews;
    private int visibleItemCount;
    private int totalItemCount;
    private int companyId = 0;
    private int pastVisibleItems;
    private ImageView ivFilter;
    private TextView tvNews, tvNoData, tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_mowazi_news_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent.hasExtra("companyId"))
            companyId = getActivity().getIntent().getExtras()
                    .getInt("companyId");
        Log.wtf("companyId", "" + companyId);
        setImagePath();
        // Actions.setViewResizing(getActivity());
        llNewsLayout = view.findViewById(R.id.llDealsLayout);
        llPickers = view.findViewById(R.id.llPickers);
        llFields = view.findViewById(R.id.llFields);
        llPickers.setVisibility(View.GONE);
        llFields.setVisibility(View.GONE);
        pbNews = view.findViewById(R.id.pbLastDeals);
        ivFilter = view.findViewById(R.id.ivFilter);
        tvNews = view.findViewById(R.id.tvDeals);
        tvNoData = view.findViewById(R.id.tvNoData);
        ivFilter.setVisibility(View.GONE);

        if (MyApplication.lang == MyApplication.ARABIC) {
            llNewsLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            llNewsLayout.setTextDirection(View.TEXT_DIRECTION_RTL);
            tvNews.setText(getResources().getString(R.string.mowazi_companyNews));
            tvNews.setTypeface(MyApplication.droidregular);
        } else {

            tvNews.setText(getResources().getString(R.string.mowazi_companyNews));
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

        rvNews.setLayoutManager(llm);
        adapter = new MowaziNewsRecyclerAdapter(getActivity(), allNews, this, 2);
        rvNews.setAdapter(adapter);
        // rvNews.addItemDecoration(new
        // SimpleDividerItemDecoration(getActivity(),0,R.drawable.line_divider));

        getNews = new GetCompanyNewsTask();
        getNews.execute("0");

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
        // Actions.setViewResizingListRowForMowazi(view, getActivity());

        Actions.overrideFonts(getActivity(), llNewsLayout, true);
    }

    private void refreshItems() {
        allNews.clear();
        getNews = new GetCompanyNewsTask();
        getNews.execute("0");
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

        if (allNews.size() > 0) {
            getNews = new GetCompanyNewsTask();
            getNews.execute(String.valueOf(allNews.get(allNews.size() - 1)
                    .getId()));
            flagLoading = false;
        }
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

    protected class GetCompanyNewsTask extends AsyncTask<String, Void, String> {

        boolean isHaveNoEng = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!pulltoRefresh)
                pbNews.setVisibility(View.VISIBLE);
            getActivity().getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            String url = "" + MyApplication.mowaziUrl + "/GetCompanyNews?";

            HashMap<String, String> parameters = new HashMap<String, String>();

            parameters.put("lang", MyApplication.lang == MyApplication.ARABIC ? "1" : "2");
            parameters.put("companyId", "" + companyId);
            parameters.put("top", "15");
            parameters.put("id", params[0]);

            Log.wtf("Mowazi Unlisted", "url : " + url);
            Log.wtf("Mowazi Unlisted", "parameters : " + parameters);

            result = ConnectionRequests.POST(url, parameters);

            AlmowaziNewsParser parser = new AlmowaziNewsParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
            isHaveNoEng = parser.isHaveNoEn();
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
            try {
                pbNews.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                onItemsLoadComplete();

                if (allNews.size() == 0) {
                    tvNoData.setVisibility(View.VISIBLE);
                    swipeContainer.setVisibility(View.GONE);
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