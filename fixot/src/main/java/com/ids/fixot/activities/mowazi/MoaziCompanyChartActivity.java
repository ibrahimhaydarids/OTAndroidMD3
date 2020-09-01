package com.ids.fixot.activities.mowazi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.ids.fixot.Actions;
import com.ids.fixot.ConnectionRequests;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.classes.MyMarkerView;
import com.ids.fixot.model.mowazi.MoaziMyMarkerView;
import com.ids.fixot.model.mowazi.MowaziDeal;
import com.ids.fixot.parser.MowaziDealParser;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;


/**
 * Created by user on 1/24/2017.
 */

public class  MoaziCompanyChartActivity extends AppCompatActivity implements OnChartGestureListener, OnChartValueSelectedListener {

    private RelativeLayout main;
    private ImageButton back;
    private NavigationView nav_view;
    private DrawerLayout drawer;
    private TextView tvDeals;
    // private TouchImageView ivChart;
    private ProgressBar pbLoading;
    private boolean extra = false;
    private int companyId = -1;
    private String chartUrl = "", companyName = "";
    ArrayList<MowaziDeal> deals = new ArrayList<MowaziDeal>();
    ArrayList<Entry> values = new ArrayList<>();
    RelativeLayout chartrl;
    LineChart mChart;
    SharedPreferences mshared;
    ImageView filter;
    SharedPreferences.Editor edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moazi_company_chart);

        Actions.setViewResizing(MoaziCompanyChartActivity.this);
        Actions.setBarColor(MoaziCompanyChartActivity.this);

        mshared = PreferenceManager.getDefaultSharedPreferences(MoaziCompanyChartActivity.this);
        edit = mshared.edit();
        drawer = findViewById(R.id.drawer_layout);
        back = findViewById(R.id.back);
        main = findViewById(R.id.main);
        nav_view = findViewById(R.id.nav_view);
        tvDeals = findViewById(R.id.tvDeals);
        // ivChart = (TouchImageView) findViewById(R.id.ivChart);
        filter = findViewById(R.id.filter);
        filter.setVisibility(View.GONE);
        pbLoading = findViewById(R.id.pbLoading);
        chartrl = findViewById(R.id.chartrl);
/*

        Intent intent = getIntent();
        extra = intent.hasExtra("companyId");
        if (extra) {
            companyId = getIntent().getExtras().getInt("companyId");
            chartUrl = getIntent().getExtras().getString("chartUrl");
            //chartUrl = "Charts/DealsChart_221.jpg";
            companyName = getIntent().getExtras().getString("companyname");
        }
        if (MyApplication.lang==MyApplication.ARABIC) {
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            drawer.setTextDirection(View.TEXT_DIRECTION_RTL);
            back.setImageResource(R.drawable.arrow);
            tvDeals.setText(getResources().getString(R.string.chart) + "-" + companyName);
            tvDeals.setTypeface(MyApplication.droidbold);
        } else {
            drawer.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            drawer.setTextDirection(View.TEXT_DIRECTION_LTR);
            back.setImageResource(R.drawable.arrowright);
            tvDeals.setText(getResources().getString(R.string.chart) + "-" + companyName);
            tvDeals.setTypeface(MyApplication.opensansbold);
        }


        mChart = findViewById(R.id.linechart);

      //  final ImageButton imgbtn = findViewById(R.id.imgbtn);

        int sqid = mshared.getInt("sqid", 0);
        if (sqid >= 3) {
            edit.putInt("sqid", 3).apply();
            imgbtn.setVisibility(View.GONE);
        } else
            edit.putInt("sqid", sqid + 1).apply();

        sqid = sqid + 1;

        new MaterialShowcaseView.Builder(this)
                .setTarget(imgbtn).setDismissOnTouch(true)
                .setDismissText(getString(R.string.gotit))
                .setContentText(getString(R.string.charthint)).setFadeDuration(500).setListener(new IShowcaseListener() {
            @Override
            public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {

            }

            @Override
            public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                imgbtn.setVisibility(View.GONE);
            }
        })
                .setDelay(1000) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(sqid + "") // provide a unique ID used to ensure it is only shown once
                .show();

        if(sqid==3)
            imgbtn.setVisibility(View.GONE);
        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, sqid + "");

        sequence.setConfig(config);

        sequence.addSequenceItem(imgbtn,
                "This is button one", "GOT IT");

        sequence.start();
        GetChartValues getChartValues = new GetChartValues();

        getChartValues.execute();*/
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if (lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)

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


    public class GetChartValues extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            String result = "";
            try {
                String url = "" + MyApplication.mowaziUrl + "/GetChartValues?";

                HashMap<String, String> parameters = new HashMap<String, String>();

                parameters.put("companyId", "" + companyId);

                result = ConnectionRequests.POST(url, parameters);
                MowaziDealParser parser = new MowaziDealParser(result, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");

                deals.addAll(parser.GetDeals());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            pbLoading.setVisibility(View.GONE);

//
//            for (int i=0;i<deals.size();i++) {
//
//                float y = (float)deals.get(i).getPrice();
//
//                values.add(new Entry(i, y));
//            }
//
//            // create a dataset and give it a type
//            LineDataSet set1 = new LineDataSet(values, "DataSet 1");
//            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
//            set1.setColor(ColorTemplate.getHoloBlue());
//            set1.setValueTextColor(ColorTemplate.getHoloBlue());
//            set1.setLineWidth(1.5f);
//            set1.setDrawCircles(false);
//            set1.setDrawValues(false);
//            set1.setFillAlpha(65);
//            set1.setFillColor(ColorTemplate.getHoloBlue());
//            set1.setHighLightColor(Color.rgb(244, 117, 117));
//            set1.setDrawCircleHole(false);
//
//            // create a data object with the datasets
//            LineData data = new LineData(set1);
//            data.setValueTextColor(Color.WHITE);
//            data.setValueTextSize(9f);
//
//            // set data
//            mChart.setData(data);
            if (deals.size() != 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<String> xVals = new ArrayList<String>();
                        ArrayList<Entry> yVals = new ArrayList<>();
                        DecimalFormat decimalFormat = new DecimalFormat("#.#");

                        for (int i = 0; i <= deals.size() - 1; i++) {

                            xVals.add(i, deals.get(i).getDealDate());
                            float f = (float) deals.get(i).getPrice();


                            yVals.add(new Entry(Float.valueOf(decimalFormat.format(f)), i));
                        }


                        LineDataSet set1 = new LineDataSet(yVals, "");
                        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set1.setColor(ColorTemplate.getHoloBlue());
                        set1.setValueTextColor(ColorTemplate.getHoloBlue());
                        //  set1.setLineWidth(1.5f);
                        set1.setDrawCircles(false);
                        set1.setDrawValues(false);

                        set1.setFillAlpha(65);
                        set1.setFillColor(ColorTemplate.getHoloBlue());
                        set1.setHighLightColor(Color.rgb(244, 117, 117));
                        set1.setDrawCircleHole(false);


                        final LineData data = new LineData(xVals, set1);
                        data.setValueTextColor(Color.WHITE);
                        data.setValueTextSize(9f);
                        MowaziDeal minValue = new MowaziDeal();
                        MowaziDeal maxValue = new MowaziDeal();
                        for (MowaziDeal x : deals) {
                            maxValue = (maxValue.getPrice() == 0 || x.getPrice() > maxValue.getPrice()) ? x : maxValue;
                        }
                        for (MowaziDeal x : deals) {
                            minValue = (minValue.getPrice() == 0 || x.getPrice() < minValue.getPrice()) ? x : minValue;
                        }
                        double step = (maxValue.getPrice() - minValue.getPrice()) / 10;
                        double minValueChart, maxValueChart;
                        minValueChart = minValue.getPrice() - step;
                        maxValueChart = maxValue.getPrice() + step;
//            mChart.getAxisLeft().setAxisMinValue((float)minValueChart);
//            mChart.getAxisLeft().setAxisMaxValue((float)maxValueChart);
                        mChart.getAxisLeft().setYOffset(10);
                        mChart.getAxisRight().setEnabled(false);
                        //   mChart.getXAxis().setXOffset(10);
                        mChart.setPinchZoom(false);
                        mChart.getAxisLeft().setStartAtZero(false);
                        mChart.setDescriptionColor(Color.TRANSPARENT);
                        mChart.getAxisLeft().setAxisMinValue((float) minValueChart);
                        //  mChart.getAxisLeft().setAxisLineWidth((float) step);
                        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        mChart.setOnChartGestureListener(MoaziCompanyChartActivity.this);
                        mChart.setOnChartValueSelectedListener(MoaziCompanyChartActivity.this);
                        MoaziMyMarkerView mv = new MoaziMyMarkerView(MoaziCompanyChartActivity.this, R.layout.custom_marker_view, deals);

                        mv.setKeepScreenOn(true);

                        mChart.setMarkerView(mv);
                        // Set the marker to the chart

                        mChart.setData(data);
                        mChart.invalidate();
                        mChart.setVisibleXRangeMaximum(10);
                    }
                });

            }
        }
    }


    public void back(View v) {
        finish();
    }

    public void showDrawer(View v) {
        if (MyApplication.lang==MyApplication.ENGLISH) {
            //    list.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            // list.setTextDirection(View.TEXT_DIRECTION_LTR);
            drawer.openDrawer(Gravity.LEFT);
        } else {
            drawer.openDrawer(Gravity.RIGHT);
            //  list.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //  list.setTextDirection(View.TEXT_DIRECTION_ANY_RTL);
        }
    }


}