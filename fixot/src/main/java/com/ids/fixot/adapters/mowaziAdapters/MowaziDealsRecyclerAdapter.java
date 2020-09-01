package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziDeal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziDealsRecyclerAdapter extends RecyclerView.Adapter<MowaziDealsRecyclerAdapter.ViewHolder> {

    private ArrayList<MowaziDeal> allDeals;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;


    private int type, i = 0;

    private NumberFormat myFormatter;

    public MowaziDealsRecyclerAdapter(Activity context, ArrayList<MowaziDeal> allDeals, RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allDeals = allDeals;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.deals_recycler_item, viewGroup, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        holder.tvCompanyItem.setText(allDeals.get(position).getCompany().trim());
        holder.tvCompanyItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                new Locale("US_en"));
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", otherSymbols);
        DecimalFormat dfPrice = new DecimalFormat("#,##0.0", otherSymbols);
        myFormatter = new DecimalFormat("#,###", otherSymbols);

        holder.tvQuantityItem.setText(myFormatter.format(allDeals.get(position)
                .getQuantity()));
        holder.tvQuantityItem.setTextColor(ContextCompat.getColor(context, R.color.blue));

        String aa = dfPrice.format(allDeals.get(position).getPrice());
        holder.tvPriceItem.setText(aa + " " + context.getResources().getString(R.string.mowazi_dealPrice));
        holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        holder.tvDateItem.setText(allDeals.get(position).getDealDate());
        holder.tvDateItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

    }

    @Override
    public int getItemCount() {
        return allDeals.size();
    }

    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);

        }
    }

    public class ItemViewHolder extends ViewHolder implements
            View.OnClickListener {
        protected TextView tvCompanyItem, tvQuantityItem, tvPriceItem,
                tvDateItem, tvTypeItem;
        protected View v;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);
            this.tvCompanyItem = v.findViewById(R.id.tvCompanyItem);
            this.tvQuantityItem = v
                    .findViewById(R.id.tvQuantityItem);
            this.tvPriceItem = v.findViewById(R.id.tvPriceItem);
            this.tvDateItem = v.findViewById(R.id.tvDateItem);

        }

        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }

}