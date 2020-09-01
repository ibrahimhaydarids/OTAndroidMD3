package com.ids.fixot.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.Stock;

import java.util.ArrayList;

/**
 * Created by user on 3/31/2017.
 */

public class StockTopsRecyclerAdapter extends RecyclerView.Adapter<StockTopsRecyclerAdapter.ViewHolder> {


    private static final int HEADER = 0;
    private static final int ITEM = 1;
    private ArrayList<Stock> allStocks;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public StockTopsRecyclerAdapter(Activity context, ArrayList<Stock> allStocks, RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allStocks = allStocks;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == HEADER) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.stock_top_header, viewGroup, false);
            return new HeaderViewHolder(v);

        } else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.stock_top_item, viewGroup, false);
            return new ItemViewHolder(v);
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (viewHolder.getItemViewType() == ITEM) {

            ItemViewHolder holder = (ItemViewHolder) viewHolder;

            holder.tvSymbolItem.setText(MyApplication.lang == 1 ? allStocks.get(position).getSymbolAr() : allStocks.get(position).getSymbolEn());

            holder.tvAmountItem.setText(String.valueOf(allStocks.get(position).getAmount()));

            holder.tvChangeItem.setText(String.valueOf(allStocks.get(position).getChange()));

            holder.tvChangePercentItem.setText(String.valueOf(allStocks.get(position).getChangePercent()));

            if (position % 2 == 1)
                holder.llItem.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.colorLight));
            else
                holder.llItem.setBackgroundColor(
                        ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return allStocks.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return HEADER;
        else
            return ITEM;
    }


    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View v) {
            super(v);

        }
    }

    private class HeaderViewHolder extends ViewHolder {

        private TextView tvSymbolHeader, tvAmountHeader, tvChangeHeader, tvChangePercentHeader;
        private RelativeLayout top;

        private HeaderViewHolder(View v) {
            super(v);

            this.tvSymbolHeader = v.findViewById(R.id.tvSymbolHeader);
            this.tvAmountHeader = v.findViewById(R.id.tvAmountHeader);
            this.tvChangeHeader = v.findViewById(R.id.tvChangeHeader);
            this.tvChangePercentHeader = v.findViewById(R.id.tvChangePercentHeader);

        }
    }

    private class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        private TextView tvSymbolItem, tvAmountItem, tvChangeItem, tvChangePercentItem;
        private LinearLayout llItem;
        private View v;

        private ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);

            this.llItem = v.findViewById(R.id.llItem);
            this.tvSymbolItem = v.findViewById(R.id.tvSymbolItem);
            this.tvAmountItem = v.findViewById(R.id.tvAmountItem);
            this.tvChangeItem = v.findViewById(R.id.tvChangeItem);
            this.tvChangePercentItem = v.findViewById(R.id.tvChangePercentItem);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}