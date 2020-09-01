package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziSector;

import java.util.ArrayList;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziSectorSpinnerAdapter extends ArrayAdapter<MowaziSector> {

    private Activity context;
    private int[] flags;
    private ArrayList<MowaziSector> allSectors;
    private LayoutInflater inflter;

    public MowaziSectorSpinnerAdapter(Activity applicationContext, ArrayList<MowaziSector> allSectors) {

        super(applicationContext, R.layout.sector_spinner_item);
        this.context = applicationContext;
        this.allSectors = allSectors;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return allSectors.size();
    }

    @Override
    public MowaziSector getItem(int i) {
        return allSectors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.sector_spinner_item, null);

        TextView tvItem = view.findViewById(R.id.tvItem);

        tvItem.setText(allSectors.get(i).getName());

        LinearLayout lllayout = view.findViewById(R.id.llLayout);
        lllayout.setBackground(ContextCompat.getDrawable(context, R.drawable.mowazi_ticker_border));
        tvItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_blueButton));

        Actions.overrideFonts(context, lllayout, true);

        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.sector_spinner_item, null);
        LinearLayout lllayout = view.findViewById(R.id.llLayout);
        TextView tvItem = view.findViewById(R.id.tvItem);
        tvItem.setText(allSectors.get(i).getName());

        Actions.overrideFonts(context, lllayout, true);

        return view;
    }
}
