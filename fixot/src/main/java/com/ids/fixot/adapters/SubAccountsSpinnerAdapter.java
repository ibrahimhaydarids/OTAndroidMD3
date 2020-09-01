package com.ids.fixot.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.R;
import com.ids.fixot.model.SubAccount;

import java.util.ArrayList;

public class SubAccountsSpinnerAdapter extends ArrayAdapter<SubAccount> {

    private Activity context;
    private ArrayList<SubAccount> subAccounts;
    private LayoutInflater inflter;

    public SubAccountsSpinnerAdapter(Activity applicationContext, ArrayList<SubAccount> allSectors) {

        super(applicationContext, R.layout.item_sub_account);
        this.context = applicationContext;
        this.subAccounts = allSectors;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return subAccounts.size();
    }

    @Override
    public SubAccount getItem(int i) {
        return subAccounts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.item_sub_account, null);

        TextView tvItem = view.findViewById(R.id.tvItem);
        RelativeLayout llItem = view.findViewById(R.id.llItem);

        tvItem.setText(subAccounts.get(i).toString());

        /*if (i%2 == 0 ){
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorDarkTheme));
        }else{
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorLightInv));
        }*/

        Actions.overrideFonts(context, llItem, false);

        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_sub_account, null);

        TextView tvItem = view.findViewById(R.id.tvItem);
        RelativeLayout llItem = view.findViewById(R.id.llItem);

        tvItem.setText(subAccounts.get(i).toString());

        /*if (i%2 == 0 ){
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorDarkTheme));
        }else{
            llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorLightInv));
        }*/

        Actions.overrideFonts(context, llItem, false);


        return view;
    }
}
