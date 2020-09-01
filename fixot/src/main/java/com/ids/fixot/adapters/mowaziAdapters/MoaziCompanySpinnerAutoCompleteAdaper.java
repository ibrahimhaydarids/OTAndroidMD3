package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;


import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MoaziCompany;

import java.util.ArrayList;


/**
 * Created by Amal on 6/17/2016.
 */
public class MoaziCompanySpinnerAutoCompleteAdaper extends ArrayAdapter<MoaziCompany> {

    Activity context;
    private int resource, textViewResourceId;
    private ArrayList<MoaziCompany> items, tempItems, suggestions;

    public MoaziCompanySpinnerAutoCompleteAdaper(Activity context, int textViewResourceId, ArrayList<MoaziCompany> items) {
        super(context, textViewResourceId, items);
        this.context = context;
        this.resource = resource;
        this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<>(items); // this makes the difference.
        suggestions = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.need_list_spinner_item, parent, false);
            Actions.setViewResizingListRow(view, context);
        }
        MoaziCompany people = items.get(position);
        if (people != null) {
            TextView lblName = view.findViewById(R.id.needlistitem);
            if (lblName != null) {
                if (MyApplication.lang==MyApplication.ARABIC)

                    lblName.setText(people.getSymbolAr().trim());
                else if (MyApplication.lang==MyApplication.ENGLISH)
                    lblName.setText(people.getSymbolEn().trim());
            }
        }
        // Actions.setViewResizingListRow(view,context);
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

    public ArrayList<MoaziCompany> getItems() {
        return items;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = "";
            if (MyApplication.lang==MyApplication.ARABIC)
                str = ((MoaziCompany) resultValue).getSymbolAr().trim();
            else if (MyApplication.lang==MyApplication.ENGLISH)
                str = ((MoaziCompany) resultValue).getSymbolEn().trim();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();

                for (MoaziCompany people : tempItems) {

                    String v = "";

                    if (MyApplication.lang==MyApplication.ARABIC)
                        v = people.getSymbolAr().trim();
                    else if (MyApplication.lang==MyApplication.ENGLISH)
                        v = people.getSymbolEn().trim();
                    if (v.toLowerCase().contains(constraint.toString().toLowerCase())) {
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
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<MoaziCompany> filterList = (ArrayList<MoaziCompany>) results.values;
            clear();
            if (results != null && results.count > 0) {

                for (MoaziCompany people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }
            } else {
                try {
                    if (constraint != null)
                    {

                    }
                    else
                    {
                        addAll(tempItems);
                    }

                }
                catch (Exception e)
                {

                }


            }
//            else
//            {
//                try {
//                    if(constraint.toString().equals("")) {
//                        addAll(tempItems);
//                        notifyDataSetChanged();
//                    }
//                }
//                catch (Exception e)
//                {
//
//                }
//            }
            notifyDataSetChanged();
        }
    };
}