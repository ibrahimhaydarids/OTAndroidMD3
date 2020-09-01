package com.ids.fixot.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.WebItem;

import java.util.ArrayList;

/**
 * Created by DEV on 2/16/2018.
 */

public class SiteMapDataRecyclerAdapter extends RecyclerView.Adapter<SiteMapDataRecyclerAdapter.RecyclerTickerViewHolder> {

    private ArrayList<WebItem> webItemArrayList;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public SiteMapDataRecyclerAdapter(Activity context, ArrayList<WebItem> webItemArrayList, RecyclerViewOnItemClickListener itemClickListener) {
        this.webItemArrayList = webItemArrayList;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public RecyclerTickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_site_map_data, null);
        RecyclerTickerViewHolder rcv = new RecyclerTickerViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerTickerViewHolder holder, final int position) {

        final WebItem webItem = webItemArrayList.get(position);

        holder.tvItem.setText(MyApplication.lang == MyApplication.ENGLISH ? webItem.getTitleEn() : webItem.getTitleAr());
        //holder.separator.setVisibility(position == webItemArrayList.size() - 1 ? View.GONE : View.VISIBLE);


        Actions.overrideFonts(context, holder.llItem, false);
    }

    @Override
    public int getItemCount() {
        return this.webItemArrayList.size();
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class RecyclerTickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected View itemView, separator;
        private TextView tvItem;
        private LinearLayout llItem;

        //define and instantiate the constituents of the viewHolder (the gridItem)
        public RecyclerTickerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);
            separator = itemView.findViewById(R.id.separator);
            llItem = itemView.findViewById(R.id.llItem);
            tvItem = itemView.findViewById(R.id.tvItem);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(view, getLayoutPosition());
        }
    }


}
