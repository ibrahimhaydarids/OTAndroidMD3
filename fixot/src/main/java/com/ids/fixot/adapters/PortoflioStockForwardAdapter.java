package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.TradesActivity;
import com.ids.fixot.model.StockSummary;

import java.util.ArrayList;

/**
 * Created by Amal on 7/25/2017.
 */

public class PortoflioStockForwardAdapter extends RecyclerView.Adapter<PortoflioStockForwardAdapter.RecyclerTickerViewHolder> {

    private ArrayList<StockSummary> allObjects;
    private Activity context;
    private PortoflioStockForwardAdapter.RecyclerViewOnItemClickListener itemClickListener;


    public PortoflioStockForwardAdapter(Activity context, ArrayList<StockSummary> allObjects, RecyclerViewOnItemClickListener itemClickListener) {
        this.allObjects = allObjects;
        this.context = context;
        this.itemClickListener = itemClickListener;

    }

    public RecyclerTickerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = null;

        if (viewType == 0)
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_stock_header, parent, false);
        else if (viewType == 2)
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_stock_item, parent, false);
        else
            layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nodatalayout, parent, false);
        RecyclerTickerViewHolder rcv = new RecyclerTickerViewHolder(layoutView);

        return rcv;
    }

    @Override
    public int getItemViewType(int position) {

        return position == 0 ? 0 : 2;
    }

    @Override
    public void onBindViewHolder(PortoflioStockForwardAdapter.RecyclerTickerViewHolder holder, final int position) {

        if (position > 0) { //stock item_spinner

            final StockSummary stock = allObjects.get(position);

            try {
                holder.trades_button.setOnClickListener(v -> {
                    context.startActivity(new Intent(context, TradesActivity.class)
                            .putExtra("stockID", Integer.parseInt(stock.getStockId()))
                            .putExtra("action", MyApplication.ORDER_SELL));
//                            Log.wtf("Portfolio trades_button","stockID = " + Integer.parseInt(stock.getStockId()) + " / " + stock.getStockId());
                });

                holder.symbol_title.setText(MyApplication.lang == MyApplication.ARABIC ? stock.getSymbolAr() : stock.getSymbolEn());
                holder.symbol_code.setText(stock.getSecurityId()); //getStockId
                holder.quantity_cost_title.setText(String.valueOf(stock.getShareCount()));
                holder.volume_cost_title.setText(stock.getTotalMarket());
                //holder.quantity_cost_title.setText(Actions.formatNumber(stock.getAvailableShares(), Actions.NoDecimalThousandsSeparator));
                //holder.volume_cost_title.setText(Actions.formatNumber(Double.parseDouble(stock.getTotalCost()), Actions.TwoDecimal));
                //holder.unrealized_title.setText(Actions.formatNumber(stock.getUnrealized(), Actions.TwoDecimalThousandsSeparator));

                holder.unrealized_title.setText(String.valueOf(stock.getUnrealized()));
                holder.unrealized_title.setTextColor(Actions.textColor(String.valueOf(stock.getUnrealized())));
                holder.unrealized_percent.setText(stock.getUnrealizedPercent());
                holder.unrealized_percent.setTextColor(Actions.textColor(stock.getUnrealizedPercent()));

                String bidNumber = stock.getAverageCost() + "";
                holder.bid_ask_title.setText(bidNumber);
                String last = stock.getLast() + "";
                holder.tvLast.setText(last);

            } catch (Exception e) {
                e.printStackTrace();
            }

            if (position % 2 == 0) {

//                holder.rlItem.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//                holder.trades_button.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));

                holder.rlItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
//                holder.trades_button.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorLight  : R.color.colorLightTheme));

            } else {

//                holder.rlItem.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//                holder.trades_button.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

                holder.rlItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
//                holder.trades_button.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.white  : R.color.colorDarkTheme));
            }

            Actions.overrideFonts(context, holder.rlItem, false);
            holder.symbol_title.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
            holder.symbol_code.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidregular : MyApplication.giloryItaly);
            holder.trades_button.setTypeface(MyApplication.giloryBold);
            holder.tvLast.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidregular : MyApplication.giloryItaly);

            Actions.setTypeface(new TextView[]{holder.quantity_cost_title, holder.bid_ask_title, holder.unrealized_title}
                    , MyApplication.giloryBold);

            Actions.setTypeface(new TextView[]{holder.volume_cost_title, holder.unrealized_percent}
                    , MyApplication.giloryItaly);

        } else {

            Actions.setTypeface(new TextView[]{holder.symbol_title, holder.quantity_cost_title, holder.bid_ask_title, holder.tvLast, holder.unrealized_title, holder.unrealized_title_prcnt, holder.cost_titles},
                    MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        }

        if (position == 0 && MyApplication.lang == MyApplication.ENGLISH) {
            holder.symbol_title.setPadding(10, 10, 10, 10);
        }

    }

    @Override
    public int getItemCount() {
        return this.allObjects.size();
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class RecyclerTickerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected View itemView;
        TextView unrealized_title, unrealized_percent, bid_ask_title, tvLast, quantity_cost_title, volume_cost_title, symbol_title, symbol_code, unrealized_title_prcnt, cost_titles;
        TextView rem_cost, due_date, cost, b_price, org_qtty, rem_qtty, num, date, symbol;
        Button trades_button;
        private LinearLayout rlitem, rlItem;

        //define and instantiate the constituents of the viewHolder (the gridItem)
        public RecyclerTickerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);


            trades_button = itemView.findViewById(R.id.trades_button);
            unrealized_title = itemView.findViewById(R.id.unrealized_title);
            unrealized_percent = itemView.findViewById(R.id.unrealized_percent);
            rlItem = itemView.findViewById(R.id.rlItem);
            rlitem = itemView.findViewById(R.id.linear_layout);
            bid_ask_title = itemView.findViewById(R.id.bid_ask_title);
            tvLast = itemView.findViewById(R.id.tvLast);
            quantity_cost_title = itemView.findViewById(R.id.quantity_cost_title);
            volume_cost_title = itemView.findViewById(R.id.volume_cost_title);
            symbol_title = itemView.findViewById(R.id.symbol_title);
            symbol_code = itemView.findViewById(R.id.symbol_code);
            rem_cost = itemView.findViewById(R.id.rem_cost);
            unrealized_title_prcnt = itemView.findViewById(R.id.unrealized_title_prcnt);
            cost_titles = itemView.findViewById(R.id.cost_titles);

            cost = itemView.findViewById(R.id.cost);
            due_date = itemView.findViewById(R.id.due_date);
            b_price = itemView.findViewById(R.id.b_price);
            org_qtty = itemView.findViewById(R.id.org_qtty);
            rem_qtty = itemView.findViewById(R.id.rem_qtty);
            num = itemView.findViewById(R.id.num);
            date = itemView.findViewById(R.id.date);
            symbol = itemView.findViewById(R.id.symbol);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(view, getLayoutPosition());
        }

    }

}
