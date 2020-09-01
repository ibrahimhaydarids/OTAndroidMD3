package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.R;

import java.util.ArrayList;

/**
 * Created by dev on 6/21/2016.
 */

public class MoaziYearSpinnerAdapter extends ArrayAdapter<String> {
    private Activity context;
    private int flags[];
    private ArrayList<String> allYears;
    private LayoutInflater inflter;

    public MoaziYearSpinnerAdapter(Activity applicationContext, ArrayList<String> allYears) {
        super(applicationContext, R.layout.need_list_spinner_item);
        this.context = applicationContext;

        this.allYears = allYears;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return allYears.size();
    }

    @Override
    public String getItem(int i) {
        return allYears.get(i).toString();
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.need_list_spinner_item, null);

        TextView names = view.findViewById(R.id.needlistitem);
        names.setText(allYears.get(i).toString());
        Actions.setViewResizingListRow(view,context);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.need_list_spinner_item, null);

        TextView names = view.findViewById(R.id.needlistitem);
        names.setText(allYears.get(i).toString());
        Actions.setViewResizingListRow(view,context);
        return view;
    }
}