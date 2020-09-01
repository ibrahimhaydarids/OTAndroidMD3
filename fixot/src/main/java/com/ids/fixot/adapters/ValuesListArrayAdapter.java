package com.ids.fixot.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.ValueItem;

import java.util.ArrayList;

/**
 * Created by Amal on 7/20/2017.
 */

public class ValuesListArrayAdapter extends RecyclerView.Adapter<ValuesListArrayAdapter.RecyclerTickerViewHolder> {

    private ArrayList<ValueItem> allObjects;
    private Activity context;

    public ValuesListArrayAdapter(Activity context, ArrayList<ValueItem> allObjects) {
        this.allObjects = allObjects;
        this.context = context;

    }

    public RecyclerTickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = null;

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.value_list_item, parent, false);
        RecyclerTickerViewHolder rcv = new RecyclerTickerViewHolder(layoutView);
        return rcv;
    }


    @Override
    public void onBindViewHolder(RecyclerTickerViewHolder holder, final int position) {

        ValueItem v = allObjects.get(position);

        holder.tvTitle.setText(v.getTitle());
        holder.tvValue.setText(v.getValue());

        /*if (context instanceof OrderDetailsActivity) {
            holder.tvTitle.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.tvValue.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
        }*/

        holder.tvTitle.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidregular : MyApplication.giloryItaly);
        holder.tvValue.setTypeface(MyApplication.giloryBold);

        if (position % 2 == 0) {

            holder.rlitem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));

        } else {

            holder.rlitem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));

        }
    }

    @Override
    public int getItemCount() {
        return this.allObjects.size();
    }


    public class RecyclerTickerViewHolder extends RecyclerView.ViewHolder {

        protected View itemView;
        private TextView tvTitle;
        private TextView tvValue;
        private TextView tvValue2;
        private LinearLayout rlitem;

        //define and instantiate the constituents of the viewHolder (the gridItem)
        public RecyclerTickerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            rlitem = itemView.findViewById(R.id.linear_layout);

            tvValue2 = itemView.findViewById(R.id.tvvalue2);
            tvValue = itemView.findViewById(R.id.tvValue);
        }


    }

}
