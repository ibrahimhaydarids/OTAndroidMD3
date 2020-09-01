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
import com.ids.fixot.model.OffMarketQuotes;

import java.util.ArrayList;


public class MarketQuotesRecyclerAdapter extends RecyclerView.Adapter<MarketQuotesRecyclerAdapter.ItemViewHolder> {


    private ArrayList<OffMarketQuotes> allTrades;
    private Activity context;

    public MarketQuotesRecyclerAdapter(Activity context, ArrayList<OffMarketQuotes> allTrades) {
        this.context = context;
        this.allTrades = allTrades;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.market_quotes_header, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, final int position) {

        ItemViewHolder holder = viewHolder;

        OffMarketQuotes trade = allTrades.get(position);

        holder.tvSymbol.setText(MyApplication.lang == MyApplication.ARABIC ? trade.getSymbolAr() : trade.getSymbolEn());

        holder.tvPriceItem.setText(String.valueOf(trade.getOffMarketLastPrice()));

        holder.tvQuantityItem.setText(String.valueOf(trade.getOffMarketLastQuantity()));

        holder.tvVolumeItem.setText(String.valueOf(trade.getOffMarketVolumeToday()));

        holder.tvValueItem.setText(String.valueOf(trade.getOffMarketValueToday()));

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        } else {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));

        }

        holder.llItem.setOnClickListener(v -> context.startActivity(new Intent(context, StockDetailActivity.class)
                .putExtra("stockID", Integer.parseInt(trade.getStockID()))
                .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? trade.getSectorNameAr() : trade.getSectorNameEn())));

        Actions.overrideFonts(context, holder.llItem, false);
//        holder.tvStockItem.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        Actions.setTypeface(new TextView[]{holder.tvSymbol, holder.tvPriceItem, holder.tvQuantityItem, holder.tvValueItem, holder.tvVolumeItem}, MyApplication.giloryBold);
    }

    @Override
    public int getItemCount() {
        return allTrades.size();
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        protected View v;
        private TextView tvSymbol, tvPriceItem, tvQuantityItem, tvVolumeItem, tvValueItem;
        private LinearLayout llItem;

        private ItemViewHolder(View v) {
            super(v);
            this.v = v;

            this.llItem = v.findViewById(R.id.llItem);
            this.tvSymbol = v.findViewById(R.id.tvStockHeader);
            this.tvPriceItem = v.findViewById(R.id.tvPriceHeader);
            this.tvQuantityItem = v.findViewById(R.id.tvQuantityHeader);
            this.tvVolumeItem = v.findViewById(R.id.tvVolumeHeader);
            this.tvValueItem = v.findViewById(R.id.tvValueHeader);
        }
    }


}