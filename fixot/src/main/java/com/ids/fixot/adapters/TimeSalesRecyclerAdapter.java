package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.StockDetailActivity;
import com.ids.fixot.activities.StockOrderBookActivity;
import com.ids.fixot.model.TimeSale;

import java.util.ArrayList;


public class TimeSalesRecyclerAdapter extends RecyclerView.Adapter<TimeSalesRecyclerAdapter.ItemViewHolder> {


    private ArrayList<TimeSale> allTrades;
    private Activity context;

    public TimeSalesRecyclerAdapter(Activity context, ArrayList<TimeSale> allTrades) {
        this.context = context;
        this.allTrades = allTrades;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.time_sales_item, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder viewHolder, final int position) {

        ItemViewHolder holder = viewHolder;

        TimeSale trade = allTrades.get(position);

        SpannableString content = new SpannableString(MyApplication.lang == MyApplication.ARABIC ? trade.getStockSymbolAr() : trade.getStockSymbolEn());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        holder.tvStockItem.setText(content); //String.valueOf(position) + " - " +

        holder.tvPriceItem.setText(trade.getPrice());//Actions.formatNumber(allTrades.get(position).getPrice(), Actions.ThreeDecimalThousandsSeparator));

        holder.tvQuantityItem.setText(trade.getQuantity());//Actions.formatNumber(trade.getQuantity(),Actions.NoDecimalThousandsSeparator ));

        holder.tvChangeItem.setText(trade.getChange());//Actions.formatNumber(Double.parseDouble(allTrades.get(position).getChange()),Actions.ThreeDecimalThousandsSeparator));

        holder.tvTimeItem.setText(String.valueOf(trade.getTradeTime()));

        if (trade.getOrderType() == 1) {

            holder.tvTypeItem.setText(context.getString(R.string.buy));
        } else if (trade.getOrderType() == 2) {

            holder.tvTypeItem.setText(context.getString(R.string.sell));
        } else {

            holder.tvTypeItem.setText("");
        }

        holder.ivImage.setImageResource(trade.getArrow());

//        holder.llItem.setBackgroundColor(position % 2 != 0 ? ContextCompat.getColor(context, R.color.timesales_dark_row) : ContextCompat.getColor(context, R.color.timesales_light_row));

        if (position % 2 == 0) {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.timesales_light_row : R.color.colorDarkTheme));
        } else {
            holder.llItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.timesales_dark_row : R.color.grayInv));
        }

        holder.tvStockItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, StockDetailActivity.class).putExtra("stockID", trade.getStockID()));

            }
        });

        holder.llItem.setOnClickListener(v -> context.startActivity(new Intent(context, StockOrderBookActivity.class)
                .putExtra("stockId", trade.getStockID())
                .putExtra("last",0)
                .putExtra("securityId", trade.getSecurityId())
                .putExtra("stockName", MyApplication.lang == MyApplication.ARABIC ? trade.getStockSymbolAr() : trade.getStockSymbolEn())));

        //Actions.overrideFonts(context, holder.llItem, false);
        holder.tvStockItem.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        holder.tvTypeItem.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);
        Actions.setTypeface(new TextView[]{holder.tvChangeItem, holder.tvPriceItem, holder.tvQuantityItem, holder.tvTimeItem}, MyApplication.giloryBold);
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
        private TextView tvStockItem, tvPriceItem, tvQuantityItem, tvChangeItem, tvTimeItem, tvTypeItem;
        private ImageView ivImage;
        private LinearLayout llItem;

        private ItemViewHolder(View v) {
            super(v);
            this.v = v;

            this.llItem = v.findViewById(R.id.llItem);
            this.tvTypeItem = v.findViewById(R.id.tvTypeItem);
            this.tvStockItem = v.findViewById(R.id.tvStockItem);
            this.tvPriceItem = v.findViewById(R.id.tvPriceItem);
            this.tvQuantityItem = v.findViewById(R.id.tvQuantityItem);
            this.tvChangeItem = v.findViewById(R.id.tvChangeItem);
            this.tvTimeItem = v.findViewById(R.id.tvTimeItem);
            this.ivImage = v.findViewById(R.id.ivImage);

        }

    }


}
