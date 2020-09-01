
package com.ids.fixot.model.mowazi;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.ids.fixot.R;

import java.util.ArrayList;




/**
 * Custom implementation of the MarkerView.
 * 
 * @author Philipp Jahoda
 */
public class MoaziMyMarkerView extends MarkerView {

    private TextView tvContent;
    Context c;
    ArrayList<MowaziDeal> deals = new ArrayList<MowaziDeal>();
    public MoaziMyMarkerView(Context context, int layoutResource, ArrayList<MowaziDeal> deals) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
        this.deals = deals;
        c=context;
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Html.fromHtml(""+c.getString(R.string.price) + ":" + Utils.formatNumber(ce.getHigh(), 0, true) + " " +c.getString(R.string.date) + ":"+ deals.get(e.getXIndex()).getDealDate() + ""));
        } else {

            tvContent.setText(""+"Price" + ":" +"" + Utils.formatNumber(e.getVal(), 0, true) +" Date:"   +deals.get(e.getXIndex()).getDealDate() + "");
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
