package com.ids.fixot.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.enums.enums;

import java.util.ArrayList;

public class MarketsSpinnerAdapter extends ArrayAdapter<enums.TradingSession> {

    Boolean showArrow;
    private Activity context;
    private ArrayList<enums.TradingSession> Markets;
    private LayoutInflater inflter;

    public MarketsSpinnerAdapter(Activity applicationContext, ArrayList<enums.TradingSession> Markets, Boolean showArrows) {

        super(applicationContext, R.layout.item_market_name);
        this.context = applicationContext;
        this.Markets = Markets;
        this.showArrow = showArrows;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return Markets.size();
    }

    @Override
    public enums.TradingSession getItem(int i) {
        return Markets.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_market_name, null);

        TextView tvItem = view.findViewById(R.id.tvItem);
        RelativeLayout llItem = view.findViewById(R.id.llItem);
        tvItem.setTextSize((MyApplication.lang == MyApplication.ARABIC) ? 14 : 15);
        tvItem.setText(Markets.get(i).toString());
        tvItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));

        Actions.overrideFonts(context, llItem, false);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_market_name, null);

        TextView tvItem = view.findViewById(R.id.tvItem);
        RelativeLayout llItem = view.findViewById(R.id.llItem);

        tvItem.setText(Markets.get(i).toString());
        tvItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));


        Actions.overrideFonts(context, llItem, false);
        return view;
    }

}
