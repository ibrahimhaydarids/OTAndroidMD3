package com.ids.fixot.adapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.StockOrderBook;

import java.util.ArrayList;

/**
 * Created by user on 10/4/2017.
 */

public class OrderBookRecyclerAdapter extends RecyclerView.Adapter<OrderBookRecyclerAdapter.ViewHolder> {


    private ArrayList<StockOrderBook> allOrders;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public OrderBookRecyclerAdapter(Activity context, ArrayList<StockOrderBook> allOrders, RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allOrders = allOrders;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.order_book_item, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        StockOrderBook order = allOrders.get(position);

        holder.tvAskNumberItem.setText(String.valueOf(order.getAsk()));
        holder.tvAskQtyItem.setText(order.getAskQuantity());
        holder.tvPriceItem.setText(order.getPrice());
        holder.tvBidNumberItem.setText(String.valueOf(order.getBid()));
        holder.tvBidQtyItem.setText(order.getBidQuantity());


        if (position % 2 == 1)
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
        else
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        Actions.overrideFonts(context, holder.llItem, false);
        Actions.setTypeface(new TextView[]{holder.tvAskNumberItem, holder.tvAskQtyItem, holder.tvPriceItem, holder.tvBidNumberItem, holder.tvBidQtyItem}, MyApplication.giloryBold);

    }

    @Override
    public int getItemCount() {
        return allOrders.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View v) {
            super(v);
        }
    }

    private class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        protected View v;
        TextView tvAskNumberItem, tvAskQtyItem, tvPriceItem, tvBidQtyItem, tvBidNumberItem;
        LinearLayout llItem;

        private ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);

            this.llItem = v.findViewById(R.id.llItem);
            this.tvAskNumberItem = v.findViewById(R.id.tvAskNumberItem);
            this.tvPriceItem = v.findViewById(R.id.tvPriceItem);
            this.tvAskQtyItem = v.findViewById(R.id.tvAskQtyItem);
            this.tvBidQtyItem = v.findViewById(R.id.tvBidQtyItem);
            this.tvBidNumberItem = v.findViewById(R.id.tvBidNumberItem);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}
