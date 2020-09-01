package com.ids.fixot.classes;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.ids.fixot.R;
import com.ids.fixot.model.ChartData;

/**
 * Created by DEV on 3/5/2018.
 */

public class MyMarkerView extends MarkerView {

    Context c;
    ChartData chartdata;
    private TextView tvContent;

    public MyMarkerView(Context context, int layoutResource, ChartData chartdata) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
        this.chartdata = chartdata;
        c = context;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(e.getVal() + "");
        } else {

            tvContent.setText(e.getVal() + "");
        }


    }

    @Override
    public int getXOffset(float xpos) {
        return 0;
    }

    @Override
    public int getYOffset(float ypos) {
        return 0;
    }

}
