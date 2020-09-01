package com.ids.fixot.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.WebItem;

import java.util.ArrayList;

/**
 * Created by user on 4/3/2017.
 */

public class SiteRecyclerAdapter extends RecyclerView.Adapter<SiteRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<WebItem> webItems;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public SiteRecyclerAdapter(Activity context, ArrayList<WebItem> webItems, RecyclerViewOnItemClickListener itemClickListener) {
        this.webItems = webItems;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        holder.tvHead.setText(MyApplication.lang == MyApplication.ARABIC ? webItems.get(position).getTitleAr() : webItems.get(position).getTitleEn());
//        holder.tvTime.setText(webItems.get(position).getTitleAr());
        Actions.overrideFonts(context, holder.llItem, false);
    }

    @Override
    public int getItemCount() {
        return this.webItems.size();
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView;

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return (rcv);
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvHead, tvTime;
        protected View itemView;
        RelativeLayout llItem;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);

            llItem = itemView.findViewById(R.id.llItem);
            tvHead = itemView.findViewById(R.id.tvHead);
            tvTime = itemView.findViewById(R.id.tvTime);

            tvTime.setVisibility(View.GONE);
            llItem.setPadding(8, 8, 8, 8);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(view, getLayoutPosition());
        }
    }
}