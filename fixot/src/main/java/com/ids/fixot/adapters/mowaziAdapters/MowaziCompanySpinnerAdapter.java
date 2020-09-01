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
import com.ids.fixot.model.mowazi.MowaziCompany;

import java.util.ArrayList;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziCompanySpinnerAdapter extends ArrayAdapter<MowaziCompany> {

    int[] flags;
    private Activity context;
    private ArrayList<MowaziCompany> allCompanies;
    private LayoutInflater inflter;

    public MowaziCompanySpinnerAdapter(Activity applicationContext, ArrayList<MowaziCompany> allCompanies) {

        super(applicationContext, R.layout.need_list_spinner_item);

        this.context = applicationContext;
        this.allCompanies = allCompanies;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return allCompanies.size();
    }

    @Override
    public MowaziCompany getItem(int i) {
        return allCompanies.get(i);
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

        names.setText(MyApplication.lang == MyApplication.ENGLISH ? allCompanies.get(i).getSymbolEn() : allCompanies.get(i).getSymbolAr());

        Actions.overrideFonts(context, llItem, true);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.need_list_spinner_item, null);

        LinearLayout llItem = view.findViewById(R.id.llItem);
        TextView names = view.findViewById(R.id.needlistitem);

        names.setText(MyApplication.lang == MyApplication.ENGLISH ? allCompanies.get(i).getSymbolEn() : allCompanies.get(i).getSymbolAr());

        Actions.overrideFonts(context, llItem, true);
        return view;
    }
}