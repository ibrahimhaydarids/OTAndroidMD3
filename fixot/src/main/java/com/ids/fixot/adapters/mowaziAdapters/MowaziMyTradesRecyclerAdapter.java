package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.AlmowaziDeal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziMyTradesRecyclerAdapter extends RecyclerView.Adapter<MowaziMyTradesRecyclerAdapter.ViewHolder> {

    public static final int HEADER = 0;
    public static final int ITEM = 1;
    private static final String TAG = "CustomAdapter";
    private DecimalFormat myFormatter = new DecimalFormat("#,###");
    private ArrayList<AlmowaziDeal> allDeals;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public MowaziMyTradesRecyclerAdapter(Activity context,
                                         ArrayList<AlmowaziDeal> allDeals,
                                         RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allDeals = allDeals;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.mowazi_deals_item, viewGroup, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                new Locale("US_en"));
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", otherSymbols);
        myFormatter = new DecimalFormat("#,###", otherSymbols);
        DecimalFormat dfPrice = new DecimalFormat("#,##0.0", otherSymbols);

        if (MyApplication.lang == MyApplication.ARABIC)
            holder.tvCompanyItem.setText(allDeals.get(position).getSymbolAr());
        else
            holder.tvCompanyItem.setText(allDeals.get(position).getSymbolEn());

        holder.tvCompanyItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        holder.tvQuantityItem.setText(""
                + myFormatter.format(allDeals.get(position).getQuantity()));
        holder.tvQuantityItem.setTextColor(ContextCompat.getColor(context, R.color.blue));

        Float lafloat = Float.parseFloat(allDeals.get(position)
                .getAveragePrice());
        String aa = dfPrice.format(lafloat);
        holder.tvAverageItem.setText(aa);

        // holder.tvAverageItem.setText(allDeals.get(position).getAveragePrice());
        holder.tvAverageItem.setTextColor(ContextCompat.getColor(context,
                R.color.mowazi_dark_blue));

        holder.tvCountItem.setText("" + allDeals.get(position).getDealDate());
        holder.tvCountItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));
        holder.tvValue.setText(""
                + myFormatter.format(Integer.parseInt(allDeals.get(position)
                .getVolume())));
        holder.tvValue.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

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
        protected TextView tvCompanyItem, tvQuantityItem, tvAverageItem,
                tvCountItem, tvValue;
        protected View v;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);

            this.tvCompanyItem = v.findViewById(R.id.tvCompanyItem);
            this.tvQuantityItem = v
                    .findViewById(R.id.tvQuantityItem);
            this.tvAverageItem = v.findViewById(R.id.tvAverageItem);
            this.tvCountItem = v.findViewById(R.id.tvCountItem);
            this.tvValue = v.findViewById(R.id.tvValue);
        }

        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }

}