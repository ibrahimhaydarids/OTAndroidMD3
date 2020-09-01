package com.ids.fixot.activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
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
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.GlobalFunctions;
import com.ids.fixot.LocalUtils;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.classes.MyMarkerView;
import com.ids.fixot.model.ChartData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FullChartActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    ImageView ivCollapse;
    boolean running = true, isSector = false;
    GetSectorChartData getSectorChartData;
    GetStockChartData getStockChartData;
    int stockId = 0;
    private LineChart mChart;
    private ChartData chartdata;
    private String sectorId = "0000";

    public FullChartActivity() {
        LocalUtils.updateConfig(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Actions.setActivityTheme(this);
        Actions.setLocal(MyApplication.lang, this);
        setContentView(R.layout.activity_full_chart);
        Actions.initializeBugsTracking(this);

        findViews();

        isSector = getIntent().hasExtra("isSector");

        if (isSector) {

            sectorId = getIntent().getExtras().getString("sectorId");

            getSectorChartData = new GetSectorChartData();
            getSectorChartData.executeOnExecutor(MyApplication.threadPoolExecutor);
        } else {

            stockId = getIntent().getExtras().getInt("stockId");

            getStockChartData = new GetStockChartData();
            getStockChartData.executeOnExecutor(MyApplication.threadPoolExecutor);
        }

//        mChart.setBackgroundColor(this.getResources().getColor(R.color.red_color));
        mChart.setGridBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
//        mChart.setBackgroundColor(ContextCompat.getColor(this, MyApplication.mshared.getBoolean(this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            Runtime.getRuntime().gc();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        running = false;

        if (isSector) {

            if (getSectorChartData != null)
                getSectorChartData.cancel(true);

            try {
                MyApplication.threadPoolExecutor.getQueue().remove(getSectorChartData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            if (getStockChartData != null)
                getStockChartData.cancel(true);

            try {
                MyApplication.threadPoolExecutor.getQueue().remove(getStockChartData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void findViews() {

        ivCollapse = findViewById(R.id.ivCollapse);
        mChart = findViewById(R.id.chart1);

        ivCollapse.setOnClickListener(view -> finish());
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

    @Override
    protected void onResume() {
        super.onResume();

        Actions.checkSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        running = false;
        MyApplication.sessionOut = Calendar.getInstance();
    }

    private class MyValueFormatter implements YAxisValueFormatter {

        public MyValueFormatter() {

        }

        @Override
        public String getFormattedValue(float value, YAxis yAxis) {
            return String.valueOf(value).replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "4").replaceAll("٤", "4").replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "8").replaceAll("٨", "8").replaceAll("٩", "9").replaceAll("٠", "0");
        }
    }
    //</editor-fold>

    private class GetSectorChartData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetSectorChartData.getValue(); // this method uses key after login

            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("sectorId", sectorId);
            parameters.put("key", getResources().getString(R.string.beforekey));

            while (running) {
                try {
                    result = ConnectionRequests.GET(url, FullChartActivity.this, parameters);

                    chartdata = GlobalFunctions.GetChartData(true, result);

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

                publishProgress(params);
                SystemClock.sleep(5000);
            }

            return null;

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            try {

                if (chartdata.getChartvalues().size() == 1 && chartdata.getChartvalues().get(0).getValue() == 0) {

                    mChart.setNoDataText(getString(R.string.noData));
                } else {

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


                                YAxis yAxis = mChart.getAxisLeft(); // get the left or right axis
                                yAxis.setStartAtZero(false);
                                yAxis.setSpaceBottom(20f);
                                yAxis.setValueFormatter(new MyValueFormatter());
                                yAxis.setTextColor(ContextCompat.getColor(FullChartActivity.this, MyApplication.mshared.getBoolean(FullChartActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));


                                set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                                set1.setColor(ContextCompat.getColor(FullChartActivity.this, R.color.colorDark));
                                set1.setValueTextColor(ContextCompat.getColor(FullChartActivity.this, R.color.colorDark));

//                                set1.setColor(ContextCompat.getColor(FullChartActivity.this, MyApplication.mshared.getBoolean(FullChartActivity.this.getResources().getString(R.string.normal_theme), true) ?  R.color.colorDark  : R.color.colorValuesInv));
//                                set1.setValueTextColor(ContextCompat.getColor(FullChartActivity.this, MyApplication.mshared.getBoolean(FullChartActivity.this.getResources().getString(R.string.normal_theme), true) ?  R.color.colorDark  : R.color.colorValuesInv));

                                set1.setLineWidth(3f);
                                set1.setDrawCircles(false);
                                set1.setCircleSize(3);
                                set1.setDrawValues(false);

                                set1.setFillAlpha(65);
                                //set1.setFillColor(ColorTemplate.getHoloBlue());
//                                set1.setFillColor(ContextCompat.getColor(FullChartActivity.this, R.color.colorDark));

                                set1.setFillColor(ContextCompat.getColor(FullChartActivity.this, R.color.colorDark));
//                                set1.setFillColor(ContextCompat.getColor(FullChartActivity.this, MyApplication.mshared.getBoolean(FullChartActivity.this.getResources().getString(R.string.normal_theme), true) ?  R.color.colorDark  : R.color.colorValuesInv));
                                set1.setHighLightColor(Color.rgb(244, 117, 117));
                                set1.setDrawCircleHole(false);

                                final LineData data = new LineData(xVals, set1);
//                                data.setValueTextColor(Color.WHITE);
                                data.setValueTextColor(ContextCompat.getColor(FullChartActivity.this, MyApplication.mshared.getBoolean(FullChartActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
                                data.setValueTextSize(9f);

                                mChart.getAxisRight().setEnabled(false);
                                mChart.setPinchZoom(false);
                                mChart.getAxisLeft().setStartAtZero(false);

                                mChart.setDescriptionColor(Color.TRANSPARENT);
                                mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                mChart.setOnChartGestureListener(FullChartActivity.this);
                                mChart.setOnChartValueSelectedListener(FullChartActivity.this);
                                //    mChart.setDescriptionTypeface(MyApplication.giloryItaly);

                                MyMarkerView mv = new MyMarkerView(FullChartActivity.this, R.layout.custom_marker_view, chartdata);

                                mv.setKeepScreenOn(true);

                                mChart.setMarkerView(mv);
                                // Set the marker to the chart

                                //    mChart.getY().setTypeface(MyApplication.giloryItaly);


                                mChart.setData(data);
                                mChart.getAxisLeft().setTypeface(MyApplication.giloryItaly);

                                //hsen remove dot
                                mChart.getLegend().setEnabled(false);
                                mChart.getAxisLeft().setStartAtZero(false);
                                mChart.invalidate();

                                XAxis xAxis = mChart.getXAxis();
                                xAxis.setTextColor(ContextCompat.getColor(FullChartActivity.this, MyApplication.mshared.getBoolean(FullChartActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));


                                mChart.getXAxis().setTypeface(MyApplication.giloryItaly);
                                mChart.getAxisRight().setTypeface(MyApplication.giloryItaly);

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
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    public class GetStockChartData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mChart.setNoDataText("Loading...");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            try {

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

                            YAxis yAxis = mChart.getAxisLeft(); // get the left or right axis
                            yAxis.setStartAtZero(false);
                            yAxis.setSpaceBottom(20f);

                            yAxis.setValueFormatter(new MyValueFormatter());
                            yAxis.setTextColor(ContextCompat.getColor(FullChartActivity.this, MyApplication.mshared.getBoolean(FullChartActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));

                            set1.setColor(ContextCompat.getColor(FullChartActivity.this, R.color.colorDark));
                            set1.setValueTextColor(ContextCompat.getColor(FullChartActivity.this, R.color.colorDark));

                            //  set1.setLineWidth(1.5f);
                            set1.setDrawCircles(false);
                            set1.setCircleSize(3);
                            set1.setDrawValues(false);

                            set1.setFillAlpha(65);
                            set1.setFillColor(ContextCompat.getColor(FullChartActivity.this, R.color.colorDark));
                            set1.setHighLightColor(Color.rgb(244, 117, 117));
                            set1.setDrawCircleHole(false);

                            final LineData data = new LineData(xVals, set1);
                            data.setValueTextColor(R.color.red_color);//Color.white
                            data.setValueTextSize(9f);

                            mChart.getAxisRight().setEnabled(false);
                            mChart.setPinchZoom(false);
                            mChart.getAxisLeft().setStartAtZero(false);
                            mChart.setDescriptionColor(Color.TRANSPARENT);
                            mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                            mChart.setOnChartGestureListener(FullChartActivity.this);
                            mChart.setOnChartValueSelectedListener(FullChartActivity.this);
                            mChart.setDescriptionTypeface(MyApplication.giloryItaly);

                            MyMarkerView mv = new MyMarkerView(FullChartActivity.this, R.layout.custom_marker_view, chartdata);

                            mv.setKeepScreenOn(true);

                            mChart.setMarkerView(mv);

                            mChart.setData(data);

                            mChart.getLegend().setEnabled(false);
                            mChart.getAxisLeft().setStartAtZero(false);

                            XAxis xAxis = mChart.getXAxis();
                            xAxis.setTextColor(ContextCompat.getColor(FullChartActivity.this, MyApplication.mshared.getBoolean(FullChartActivity.this.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorValuesInv));

                            mChart.invalidate();

                            //  mChart.setVisibleXRangeMaximum(10);
                            mChart.getLayoutParams().height = MyApplication.screenWidth / 2;
                        }
                    });
                } else {
                    mChart.setData(null);
                    mChart.setNoDataText(getString(R.string.noData));
                    mChart.invalidate();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = "";
            String url = MyApplication.link + MyApplication.GetStockChartData.getValue(); // this method uses key after login


            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("stockId", "" + stockId);
            parameters.put("key", getString(R.string.beforekey));
            parameters.put("MarketId", MyApplication.marketID);

            while (running) {

                if (isCancelled())
                    break;

                try {
                    result = ConnectionRequests.GET(url, FullChartActivity.this, parameters);
                    chartdata = GlobalFunctions.GetStockChartData(result);

                    publishProgress(params);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (MyApplication.isDebug) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.error_code) + MyApplication.GetStockChartData.getKey(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

                SystemClock.sleep(5000);
            }
            return null;
        }
    }
}
