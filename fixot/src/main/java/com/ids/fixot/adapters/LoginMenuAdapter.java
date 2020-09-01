package com.ids.fixot.adapters;

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
import com.ids.fixot.model.WebItem;

import java.util.ArrayList;

public class LoginMenuAdapter extends ArrayAdapter<WebItem> {

    private Activity context;
    private ArrayList<WebItem> webItems;

    public LoginMenuAdapter(Activity context, ArrayList<WebItem> webItems) {
        super(context, R.layout.item_site_map_data, webItems);
        this.context = context;
        this.webItems = webItems;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.item_site_map_data, null, true);
            holder = new ViewHolder();

            holder.llItem = rowView.findViewById(R.id.llItem);
            holder.tvItem = rowView.findViewById(R.id.tvItem);
            //holder.separator =  rowView.findViewById(R.id.separator);

            rowView.setTag(holder);

        } else {
            holder = (ViewHolder) rowView.getTag();

        }

        final WebItem webItem = webItems.get(position);

        holder.tvItem.setText(MyApplication.lang == MyApplication.ENGLISH ? webItem.getTitleEn() : webItem.getTitleAr());
        //holder.separator.setVisibility(position == webItemArrayList.size() - 1 ? View.GONE : View.VISIBLE);
        Actions.overrideFonts(context, holder.llItem, false);

        return rowView;

    }

    private class ViewHolder {
        private LinearLayout llItem;
        private TextView tvItem;
        //private View separator;

    }

}
