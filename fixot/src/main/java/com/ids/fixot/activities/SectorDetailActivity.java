package com.ids.fixot.activities;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
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
import com.ids.fixot.adapters.ValuesListArrayAdapter;
import com.ids.fixot.classes.MyMarkerView;
import com.ids.fixot.model.ChartData;
import com.ids.fixot.model.Sector;
import com.ids.fixot.model.ValueItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by user on 10/4/2017.
 */

public class SectorDetailActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener, MarketStatusListener {

    SwipeRefreshLayout swipeContainer;
    NestedScrollView nsScroll;
    RelativeLayout rootLayout;
    TextView stockName, tvSectorName;
    Sector sector;
    RecyclerView rvValueList;
    ImageView ivFavorite;
    GridLayoutManager llmValues;
    GetSectorIndex getSectorIndex;
    boolean running = true;
    TextView title, value, secondValue;
    GetChartData mGetChartData;
    ImageView ivExpand;
    ImageView ivImage;
    private BroadcastReceiver receiver;
    private ArrayList<ValueItem> allValueItems = new ArrayList<>();
    private ValuesListArrayAdapter valueListAdapter;
    private boolean started = false, connected = false;
    private LineChart mChart;
    private ChartData chartdata;

    public SectorDetailActivity() {
        LocalUtils.updateConfig(this);
    }

    public void loadFooter(View v) {

        Actions.loadFooter(this, v);
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        receiver = new marketStatusReceiver(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(AppService.ACTION_MARKET_SERVICE));


        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_sector_details);
        Actions.initializeBugsTracking(this);

        Actions.initializeToolBar(getString(R.string.sectors), SectorDetailActivity.this);
        Actions.showHideFooter(this);
        sector = getIntent().getExtras().getParcelable("sector");
        findViews();
        started = true;

        if (!Actions.isNetworkAvailable(this)) {

            Actions.CreateDialog(this, getString(R.string.no_net), false, false);
            connected = false;
        } else {

            connected = true;
        }

        Actions.overrideFonts(this, rootLayout, false);
    }

    public void back(View v) {
        this.finish();
    }

    public void findViews() {

        swipeContainer = findViewById(R.id.swipeContainer);
        nsScroll = findViewById(R.id.nsScroll);
        tvSectorName = findViewById(R.id.tvSectorName);
        title = findViewById(R.id.title);
        value = findViewById(R.id.value);
        secondValue = findViewById(R.id.secondValue);
        rootLayout = findViewById(R.id.rootLayout);
        ivFavorite = findViewById(R.id.ivFavorite);
        stockName = findViewById(R.id.toolbar_title);
        rvValueList = findViewById(R.id.valueList);
        mChart = findViewById(R.id.chart1);
        ivExpand = findViewById(R.id.ivExpand);
        ivImage = findViewById(R.id.ivImage);

        mChart.getLayoutParams().height = MyApplication.screenWidth / 2;
        mChart.setGridBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        mChart.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));

        tvSectorName.setText((MyApplication.lang == MyApplication.ENGLISH ? sector.getNameEn() : sector.getNameAr()));
//        tvSectorName.setText(sector.getSectorID() + " - " + ( MyApplication.lang == MyApplication.ENGLISH ? sector.getNameEn() : sector.getNameAr()));

        /*if (MyApplication.lang == MyApplication.ENGLISH)
            stockName.setText(sector.getNameEn());
        else
            stockName.setText(sector.getNameAr());*/

        ivExpand.setOnClickListener(view -> {

            startActivity(new Intent(SectorDetailActivity.this, FullChartActivity.class)
                    .putExtra("isSector", true)
                    .putExtra("sectorId", sector.getSectorID())
            );
        });

        valueListAdapter = new ValuesListArrayAdapter(SectorDetailActivity.this, allValueItems);

        rvValueList.setAdapter(valueListAdapter);

        llmValues = new GridLayoutManager(SectorDetailActivity.this, MyApplication.VALUES_SPAN_COUNT);//LinearLayoutManager.VERTICAL, false);
        rvValueList.setLayoutManager(llmValues);
        rvValueList.setNestedScrollingEnabled(false);

        swipeContainer.setOnRefreshListener(() -> {

            if (connected) {

                getSectorIndex = new GetSectorIndex();
                getSectorIndex.executeOnExecutor(MyApplication.threadPoolExecutor);

                mGetChartData = new GetChartData();
                mGetChartData.executeOnExecutor(MyApplication.threadPoolExecutor);
            }

            swipeContainer.setRefreshing(false);
        });
    }


    public void goToStockPage(View v) {

        SectorDetailActivity.this.startActivity(new Intent(SectorDetailActivity.this, StockActivity.class)
                .putExtra("sectorId", sector.getSectorID())
                .putExtra("sectorName", MyApplication.lang == MyApplication.ARABIC ? sector.getNameAr() : sector.getNameEn()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);

        if (connected) {

            /*allValueItems.clear();
            valueListAdapter.notifyDataSetChanged();*/

            getSectorIndex = new GetSectorIndex();
            getSectorIndex.executeOnExecutor(MyApplication.threadPoolExecutor);

            mGetChartData = new GetChartData();
            mGetChartData.executeOnExecutor(MyApplication.threadPoolExecutor);
        }

        Actions.checkLanguage(this, started);

//Actions.InitializeSessionService(this);
//Actions.InitializeMarketService(this);
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
            getSectorIndex.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(getSectorIndex);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("Sector Index ex", e.getMessage());
        }

        try {
            mGetChartData.cancel(true);
            MyApplication.threadPoolExecutor.getQueue().remove(mGetChartData);
        } catch (Exception e) {
            e.printStackTrace();
            Log.wtf("SectortChart ex", e.getMessage());
        }

        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //<editor-fold desc="chart functions">
    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    private ArrayList<ValueItem> GetSectorDetails(String JsonString)
            throws JSONException, UnsupportedEncodingException {

        JSONObject object = new JSONObject(JsonString);
        allValueItems.clear();


        String msgdata = object.getString("ResponseMessage");
        JSONObject jsondata_msg = new JSONObject(msgdata);
        String success = jsondata_msg.getString("MessageEn");
        if (success.equals("Success")) {
            try {
                JSONObject json_data = object.getJSONObject("SectorDetails");

                sector.setChange(json_data.getString("Change"));
                sector.setChangePercent(json_data.getString("ChangePercent"));
                sector.setSectorID(json_data.getString("SectorID"));
                sector.setNameAr(MyApplication.lang == MyApplication.ARABIC ? json_data.getString("NameAr") : json_data.getString("NameEn"));
                sector.setNameEn(MyApplication.lang == MyApplication.ARABIC ? json_data.getString("NameAr") : json_data.getString("NameEn"));
                sector.setValue(json_data.getString("Value"));

                JSONArray json_fields = json_data.getJSONArray("Field");
                for (int i = 0; i < json_fields.length(); i++) {

                    JSONObject field = json_fields.getJSONObject(i);
                    ValueItem stockValueItem = new ValueItem(field.getString("Key"), field.getString("Value"));
                    allValueItems.add(stockValueItem);
                }
            } catch (Exception e) {
                Log.d("ee", e + "");
            }
        }
        return allValueItems;
    }
    //</editor-fold>

    public class MyValueFormatter implements YAxisValueFormatter {

        public MyValueFormatter() {

        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return String.valueOf(value).replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "4").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "8").replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0");
        }
    }

    private class GetSectorIndex extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            String url = MyApplication.link + MyApplication.LoadSectorDetails.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("sectorId", sector.getSectorID());
            parameters.put("Lang", MyApplication.lang == MyApplication.ENGLISH ? "English" : "Arabic");
            parameters.put("key", getResources().getString(R.string.beforekey));

            try {

                result = ConnectionRequests.GET(url, SectorDetailActivity.this, parameters);

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.LoadSectorDetails.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            return result;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //<editor-fold desc="sector details">
            try {

                allValueItems.clear();
                valueListAdapter.notifyDataSetChanged();
                allValueItems = GetSectorDetails(result);

                try {
                    title.setText(sector.getValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    value.setText(sector.getChange());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                double change = 0;

                String changeP = "";
                String changeValue = "";

                try {

                    try {
                        changeValue = sector.getChange().substring(1, sector.getChange().length() - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        changeValue = "0";
                    }

                    double changes = Double.parseDouble(sector.getChangePercent().split("%")[0]);
                    if (changes == 0) {
                        ivImage.setVisibility(View.GONE);
                    } else {
                        ivImage.setVisibility(View.VISIBLE);
                        ivImage.setImageResource(changes > 0 ? R.drawable.up_green_arrow : R.drawable.down_red_arrow);
                    }

                    change = Double.parseDouble(changeValue);
                    if (sector.getChangePercent() == null || sector.getChangePercent().equals("null")) {
                        sector.setChangePercent("0");
                    }
                    String changePercent = sector.getChangePercent();
                    changeP = sector.getChangePercent().replace("%", "").trim();

                    secondValue.setText(changePercent);
                } catch (Exception e) {
                    e.printStackTrace();

                    change = 0;
                    String changePercent = sector.getChangePercent();
                    changeP = "0";
                    Log.wtf("EXception: changeP", "is " + changeP);
                    secondValue.setText(changePercent);

                }


                //<editor-fold desc="comparison on changePercent">
                try {
                    if (Double.parseDouble(changeP) == 0) {

                        title.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.other_market));
                        value.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.other_market));
                        secondValue.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.other_market));
                    } else if (Double.parseDouble(changeP) < 0) {

                        title.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.red_color));
                        value.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.red_color));
                        secondValue.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.red_color));
                    } else {

                        title.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.green_color));
                        value.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.green_color));
                        secondValue.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.green_color));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //</editor-fold>


                try {
                    if (MyApplication.lang == MyApplication.ARABIC) {
                        title.setTypeface(MyApplication.giloryBold);
                        value.setTypeface(MyApplication.giloryBold);
                        secondValue.setTypeface(MyApplication.giloryBold);
                    } else {
                        title.setTypeface(MyApplication.giloryBold);
                        value.setTypeface(MyApplication.giloryBold);
                        secondValue.setTypeface(MyApplication.giloryBold);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                valueListAdapter.notifyDataSetChanged();
                llmValues = new GridLayoutManager(SectorDetailActivity.this, MyApplication.VALUES_SPAN_COUNT);//LinearLayoutManager.VERTICAL, false);
                rvValueList.setLayoutManager(llmValues);
                //valueListAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
//                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.LoadSectorDetails.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
//                        Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.LoadSectorDetails.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            //</editor-fold>
        }
    }

    public class GetChartData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mChart.setNoDataText("Loading...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetSectorChartData.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("sectorID", "" + sector.getSectorID());
            parameters.put("key", getString(R.string.beforekey));

            try {
                result = ConnectionRequests.GET(url, SectorDetailActivity.this, parameters);

                chartdata = new ChartData();
                chartdata = GlobalFunctions.GetChartData(true, result);
                publishProgress(params);
            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetSectorChartData.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }


            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {

                if (chartdata.getChartvalues().size() == 1 && chartdata.getChartvalues().get(0).getValue() == 0) {
                    mChart.setVisibility(View.GONE);
                    ivExpand.setVisibility(View.GONE);

                    Log.wtf("1 ", "fake data");
                    mChart.setNoDataText(getString(R.string.noData));
                } else {
                    mChart.setVisibility(View.VISIBLE);
                    ivExpand.setVisibility(View.VISIBLE);

                    //<editor-fold desc="normal case">
                    if (chartdata.getChartvalues().size() != 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<String> xVals = new ArrayList<String>();
                                ArrayList<Entry> yVals = new ArrayList<>();


                                for (int i = 0; i <= chartdata.getChartvalues().size() - 1; i++) {

                                    xVals.add(i, chartdata.getChartvalues().get(i).getDate().substring(
                                            chartdata.getChartvalues().get(i).getDate().indexOf(" ")
                                    ));
                                    float f = (float) chartdata.getChartvalues().get(i).getValue();


                                    yVals.add(new Entry(f, i));
                                }

                                LineDataSet set1 = new LineDataSet(yVals, "");
                                set1.setAxisDependency(YAxis.AxisDependency.LEFT);

                                YAxis yAxis = mChart.getAxisLeft();
                                yAxis.setStartAtZero(false);
                                yAxis.setSpaceBottom(20f);
                                yAxis.setValueFormatter(new MyValueFormatter());
                                yAxis.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, MyApplication.mshared.getBoolean(SectorDetailActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));

//                            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//                                set1.setColor(ContextCompat.getColor(MarketIndexActivity.this, R.color.colorDark));
                                set1.setColor(ContextCompat.getColor(SectorDetailActivity.this, MyApplication.mshared.getBoolean(SectorDetailActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));

                                set1.setValueTextColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.colorDark));

                                set1.setDrawCircles(false);
                                set1.setDrawValues(false);

                                set1.setFillAlpha(65);
                                set1.setFillColor(ContextCompat.getColor(SectorDetailActivity.this, R.color.colorDark));
                                set1.setHighLightColor(Color.rgb(244, 117, 117));
                                set1.setDrawCircleHole(false);


                                final LineData data = new LineData(xVals, set1);
                                data.setValueTextColor(Color.WHITE);
                                data.setValueTextSize(9f);


                                mChart.getAxisRight().setEnabled(false);

                                mChart.setPinchZoom(false);
                                mChart.getAxisLeft().setStartAtZero(false);
                                mChart.setDescriptionColor(Color.TRANSPARENT);
                                //  mChart.getAxisLeft().setAxisMinValue((float) minValueChart);
                                //  mChart.getAxisLeft().setAxisLineWidth((float) step);
                                mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                mChart.setOnChartGestureListener(SectorDetailActivity.this);
                                mChart.setOnChartValueSelectedListener(SectorDetailActivity.this);
                                mChart.setDescriptionTypeface(MyApplication.giloryItaly);

                                MyMarkerView mv = new MyMarkerView(SectorDetailActivity.this, R.layout.custom_marker_view, chartdata);

                                mv.setKeepScreenOn(true);

                                mChart.setMarkerView(mv);
                                // Set the marker to the chart

                                mChart.setData(data);


                                mChart.getLegend().setEnabled(false);
                                mChart.getAxisLeft().setStartAtZero(false);

                                XAxis xAxis = mChart.getXAxis();
                                xAxis.setTextColor(ContextCompat.getColor(SectorDetailActivity.this, MyApplication.mshared.getBoolean(SectorDetailActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));

                                mChart.invalidate();

                                //  mChart.setVisibleXRangeMaximum(10);
                                mChart.getLayoutParams().height = MyApplication.screenWidth / 2;
                            }
                        });

                    } else {
                        mChart.setNoDataText(getString(R.string.noData));
                    }
                    //</editor-fold>
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (MyApplication.isDebug) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetSectorChartData.getKey(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }
    }

}
