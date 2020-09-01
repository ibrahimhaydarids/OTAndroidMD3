package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziOrderType;

import java.util.ArrayList;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOrderTypeSpinnerAdapter extends ArrayAdapter<MowaziOrderType> {

    private Activity context;
    private int[] flags;
    private ArrayList<MowaziOrderType> allOrderTypes = new ArrayList<MowaziOrderType>();
    private LayoutInflater inflter;

    public MowaziOrderTypeSpinnerAdapter(Activity applicationContext,
                                         ArrayList<MowaziOrderType> allOrderTypes) {
        super(applicationContext, R.layout.need_list_spinner_item);
        this.context = applicationContext;
        this.allOrderTypes = allOrderTypes;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return allOrderTypes.size();
    }

    @Override
    public MowaziOrderType getItem(int i) {
        return allOrderTypes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.need_list_spinner_item, null);

        LinearLayout llItem = view.findViewById(R.id.llItem);
        TextView names = view.findViewById(R.id.needlistitem);

        names.setText(MyApplication.lang == MyApplication.ENGLISH ? allOrderTypes.get(i).getNameEn() : allOrderTypes.get(i).getNameAr());

        Actions.overrideFonts(context, llItem, true);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.need_list_spinner_item, null);

        LinearLayout llItem = view.findViewById(R.id.llItem);
        TextView names = view.findViewById(R.id.needlistitem);

        names.setText(MyApplication.lang == MyApplication.ENGLISH ? allOrderTypes.get(i).getNameEn() : allOrderTypes.get(i).getNameAr());

        Actions.overrideFonts(context, llItem, true);
        return view;
    }
}