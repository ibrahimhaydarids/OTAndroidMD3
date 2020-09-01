package com.ids.fixot.adapters.mowaziAdapters;

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
import com.ids.fixot.model.mowazi.MowaziOnlineOrder;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziMyOrdersRecyclerAdapter extends RecyclerView.Adapter<MowaziMyOrdersRecyclerAdapter.ViewHolder> {

    public static final int HEADER = 0;
    public static final int ITEM = 1;
    private static final String TAG = "CustomAdapter";
    private DecimalFormat myFormatter = new DecimalFormat("#,###");
    private ArrayList<MowaziOnlineOrder> allOrders;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public MowaziMyOrdersRecyclerAdapter(Activity context, ArrayList<MowaziOnlineOrder> allOrders, RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;

        this.allOrders = allOrders;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.my_orders_recycler_item, viewGroup, false);
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
        DecimalFormat dfPrice = new DecimalFormat("#,##0.0", otherSymbols);
        myFormatter = new DecimalFormat("#,###", otherSymbols);

        holder.tvCompanyItem.setText(MyApplication.lang == MyApplication.ENGLISH ? allOrders.get(position).getCompany().getSymbolEn() :
                allOrders.get(position).getCompany().getSymbolAr());

        holder.tvCompanyItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        holder.tvOrderAskItem.setText("" + allOrders.get(position).getOriginalShares());

        holder.tvOrderAskItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        holder.tvOrderQuantityItem.setText("" + df.format(Integer.parseInt(allOrders.get(position).getExecutedShares())));

        holder.tvOrderQuantityItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        String aa = dfPrice.format(allOrders.get(position).getPrice());
        holder.tvOrderPriceItem.setText(aa + "");

        holder.tvOrderPriceItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        if (allOrders.get(position).getOrderTpeId() == 1) {

            holder.tvOfferQuantityItem.setText(context.getResources().getString(R.string.sell));
            holder.tvOfferQuantityItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        } else {
            holder.tvOfferQuantityItem.setText(context.getResources().getString(R.string.buy));
            holder.tvOfferQuantityItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        }

        Actions.overrideFonts(context, holder.llItem, true);
    }

    @Override
    public int getItemCount() {

        return allOrders.size();
    }

    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        protected LinearLayout llItem;

        protected TextView tvCompanyItem, tvOrderAskItem, tvOrderQuantityItem,
                tvOrderPriceItem, tvOfferQuantityItem;
        protected View v;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);
            this.llItem = v.findViewById(R.id.llItem);
            this.tvCompanyItem = v.findViewById(R.id.tvCompanyItem);
            this.tvOrderAskItem = v
                    .findViewById(R.id.tvOrderAskItem);
            this.tvOrderQuantityItem = v
                    .findViewById(R.id.tvOrderQuantityItem);
            this.tvOrderPriceItem = v
                    .findViewById(R.id.tvOrderPriceItem);
            this.tvOfferQuantityItem = v
                    .findViewById(R.id.tvOfferQuantityItem);

        }

        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }

}