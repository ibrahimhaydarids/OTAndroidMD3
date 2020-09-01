package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Intent;
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
import com.ids.fixot.activities.StockDetailActivity;
import com.ids.fixot.model.Stock;

import java.util.ArrayList;

/**
 * Created by user on 9/27/2017.
 */

public class TopsRecyclerAdapter extends RecyclerView.Adapter<TopsRecyclerAdapter.ItemViewHolder> {

    private ArrayList<Stock> allStocks;
    private Activity context;
    private int type;

    public TopsRecyclerAdapter(Activity context, ArrayList<Stock> allStocks, int type) {
        this.context = context;
        this.type = type;
        this.allStocks = allStocks;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tops_item, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, final int position) {

        Stock stock = allStocks.get(position);
        holder.tvStockSymbol.setText(MyApplication.lang == MyApplication.ENGLISH ? stock.getSecurityId() + " " + stock.getSymbolEn() : stock.getSecurityId() + " " + stock.getSymbolAr());

        try {

            //String last = Actions.formatNumber(Double.parseDouble(stock.getLast()), Actions.TwoDecimal);
            //holder.tvPrice.setText(last);
            holder.tvPrice.setText(stock.getLast());
        } catch (Exception e) {
            e.printStackTrace();
            holder.tvPrice.setText(stock.getLast());
        }


        holder.tvChange.setText(stock.getChange());
        holder.tvChangePercent.setText(stock.getChangePercent());
        holder.tvChangePercent.setTextColor(Actions.textColor(stock.getChangePercent()));

        if (type == MyApplication.TOP_VALUES) {

            holder.tvTradingItem.setVisibility(View.VISIBLE);

            try {
                holder.tvTradingItem.setText(Actions.formatNumber(Double.parseDouble(stock.getAmount()), Actions.ThreeDecimalThousandsSeparator));
                //holder.tvTradingItem.setText(stock.getAmount());
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvTradingItem.setText(stock.getAmount());
            }

            try {
                //holder.tvChange.setText(Actions.formatNumber(Double.parseDouble(stock.getTrades()), Actions.TwoDecimal));
                holder.tvChange.setText(stock.getTrades());
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvChange.setText(stock.getTrades());
            }

            holder.tvChangePercent.setText(stock.getChange());
            holder.tvChangePercent.setTextColor(Actions.textColor(stock.getChange()));

        } else if (type == MyApplication.TOP_TRADES) {

            holder.tvTradingItem.setVisibility(View.VISIBLE);

            try {
                holder.tvTradingItem.setText(Actions.formatNumber(Double.parseDouble(stock.getAmount()), Actions.ThreeDecimalThousandsSeparator));
                //holder.tvTradingItem.setText(stock.getAmount());
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvTradingItem.setText(stock.getAmount());
            }

            try {
                //holder.tvChange.setText(Actions.formatNumber(Double.parseDouble(stock.getTrades()), Actions.TwoDecimal));
                holder.tvChange.setText(stock.getTrades());
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvChange.setText(stock.getTrades());
            }

            holder.tvChangePercent.setText(stock.getChange());
            holder.tvChangePercent.setTextColor(Actions.textColor(stock.getChange()));

        } else if (type == MyApplication.TOP_TRADED) {

            try {
                holder.tvChange.setText(Actions.formatNumber(Double.parseDouble(stock.getVolume()), Actions.NoDecimalThousandsSeparator));
                //holder.tvChange.setText(stock.getVolume());
            } catch (Exception e) {
                e.printStackTrace();
                holder.tvChange.setText(stock.getVolume());
            }
        } else {

            holder.tvTradingItem.setVisibility(View.GONE);
        }

//        if (position % 2 == 1)
//            holder.llLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//        else
//            holder.llLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));


        if (position % 2 == 1) {
            holder.llLayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        } else {
            holder.llLayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
        }

        holder.llLayout.setOnClickListener(v -> {
            Intent i = new Intent(context, StockDetailActivity.class);
            i.putExtra("stockID", Integer.parseInt(allStocks.get(position).getId()));
            context.startActivity(i);
        });

        Actions.overrideFonts(context, holder.llLayout, false);
        Actions.setTypeface(new TextView[]{holder.tvChange, holder.tvChangePercent, holder.tvPrice, holder.tvTradingItem}, MyApplication.giloryBold);
    }

    @Override
    public int getItemCount() {
        return allStocks.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        protected TextView tvStockSymbol, tvPrice, tvChange, tvChangePercent, tvTradingItem;
        protected View v;
        LinearLayout llLayout;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;

            this.tvTradingItem = v.findViewById(R.id.tvTradingItem);
            this.tvStockSymbol = v.findViewById(R.id.tvStockSymbol);
            this.tvPrice = v.findViewById(R.id.tvPrice);
            this.tvChange = v.findViewById(R.id.tvChange);
            this.tvChangePercent = v.findViewById(R.id.tvChangePercent);
            this.llLayout = v.findViewById(R.id.llLayout);
        }


    }

}