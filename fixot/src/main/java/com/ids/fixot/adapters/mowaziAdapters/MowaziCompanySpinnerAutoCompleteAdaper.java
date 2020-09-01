package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziCompany;

import java.util.ArrayList;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziCompanySpinnerAutoCompleteAdaper extends ArrayAdapter<MowaziCompany> {

    private Activity context;
    private int resource, textViewResourceId;
    private ArrayList<MowaziCompany> items, tempItems, suggestions;
    private Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = "";

            str = MyApplication.lang == MyApplication.ENGLISH ? ((MowaziCompany) resultValue).getSymbolEn().trim() : ((MowaziCompany) resultValue).getSymbolAr().trim();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();

                for (MowaziCompany people : tempItems) {

                    String v = "";

                    v = MyApplication.lang == MyApplication.ARABIC ? people.getSymbolAr().trim() : people.getSymbolEn().trim();

                    if (v.toLowerCase().contains(
                            constraint.toString().toLowerCase())) {
                        suggestions.add(people);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            ArrayList<MowaziCompany> filterList = (ArrayList<MowaziCompany>) results.values;
            clear();
            if (results != null && results.count > 0) {

                for (MowaziCompany people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            } else {
                try {
                    if (constraint == null) {

                        addAll(tempItems);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            notifyDataSetChanged();
        }
    };

    public MowaziCompanySpinnerAutoCompleteAdaper(Activity context, int textViewResourceId, ArrayList<MowaziCompany> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<MowaziCompany>(items); // this makes the difference.
        suggestions = new ArrayList<MowaziCompany>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.need_list_spinner_item, parent,
                    false);
        }
        MowaziCompany people = items.get(position);

        LinearLayout llItem = context.findViewById(R.id.llItem);

        if (people != null) {
            TextView lblName = view.findViewById(R.id.needlistitem);

            try {

                if (MyApplication.lang == MyApplication.ARABIC) {

                    lblName.setText(people.getSymbolAr().trim());
                    lblName.setTypeface(MyApplication.droidregular);
                } else {

                    lblName.setText(people.getSymbolEn().trim());
                    lblName.setTypeface(MyApplication.opensansregular);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        Actions.overrideFonts(context, llItem, true);

        return view;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public ArrayList<MowaziCompany> getItems() {
        return items;
    }
}