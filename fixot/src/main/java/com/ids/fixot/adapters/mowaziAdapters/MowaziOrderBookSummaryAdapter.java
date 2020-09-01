package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.ids.fixot.activities.mowazi.MowaziCompanyDetailsActivity;
import com.ids.fixot.activities.mowazi.MowaziOrdersSummaryActivity;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziOnlineOrder;
import com.ids.fixot.model.mowazi.MowaziOrderBook;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOrderBookSummaryAdapter extends RecyclerView.Adapter<MowaziOrderBookSummaryAdapter.ViewHolder> {

    public static final int TYPE_SUMMARY = 0;
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    private static final String TAG = "CustomAdapter";
    ArrayList<MowaziCompany> OrdersToSort = new ArrayList<MowaziCompany>();
    private ArrayList<MowaziOrderBook> allOrders;
    private ArrayList<MowaziOnlineOrder> allOnlineOrders;
    private int type;
    private Activity context;
    private DecimalFormat myFormatter;
    private RecyclerViewOnItemClickListener itemClickListener;

    public MowaziOrderBookSummaryAdapter(Activity context,
                                         ArrayList<MowaziOrderBook> allOrders,
                                         ArrayList<MowaziOnlineOrder> allOnlineOrders,
                                         RecyclerViewOnItemClickListener itemClickListener, int type) {
        this.context = context;
        this.type = type;

        if (allOnlineOrders == null)
            this.allOrders = allOrders;
        else
            this.allOnlineOrders = allOnlineOrders;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.order_book_recycler_item, viewGroup, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        holder.tvCompanyItem.setText(MyApplication.lang == MyApplication.ARABIC ? allOrders.get(position).getCompany().getSymbolAr() : allOrders.get(position).getCompany().getSymbolEn());

        holder.tvCompanyItem.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putParcelable("company", allOrders.get(position).getCompany());
                b.putString("companyid", allOrders.get(position).getCompanyId()
                        + "");
                i.putExtras(b);
                i.setClass(context, MowaziCompanyDetailsActivity.class);
                context.startActivity(i);
            }
        });

        holder.tvCompanyOrderBook
                .setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        context.startActivity(new Intent(context, MowaziOrdersSummaryActivity.class)
                                .putExtra("companyId", allOrders.get(position).getCompany().getCompanyId())
                                .putExtra("companyname", MyApplication.lang == MyApplication.ENGLISH ? allOrders.get(position).getCompany().getSymbolEn() :
                                        allOrders.get(position).getCompany().getSymbolAr()));
                        context.finish();
                    }
                });

        holder.tvCompanyOrderBook.setVisibility(View.GONE);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                new Locale("US_en"));
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("#,###", otherSymbols);
        myFormatter = new DecimalFormat("#,###", otherSymbols);
        DecimalFormat dfPrice = new DecimalFormat("#,##0.0", otherSymbols);
        holder.tvCompanyItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        holder.tvOrderBidQuantityItem.setText(""
                + df.format(allOrders.get(position).getAskquantity()));
        holder.tvOrderBidQuantityItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        holder.tvOrderBidPriceItem.setText(""
                + dfPrice.format(allOrders.get(position).getAskPrice()));
        holder.tvOrderBidPriceItem.setTextColor(ContextCompat.getColor(context,
                R.color.blue));

        String aa = df.format(allOrders.get(position).getBidQuantity());
        holder.tvOrderAskQuantityItem.setText(aa);

        holder.tvOrderAskQuantityItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

        holder.tvOrderAskPriceItem.setText(""
                + dfPrice.format(allOrders.get(position).getBidPrice()));
        holder.tvOrderAskPriceItem.setTextColor(ContextCompat.getColor(context,
                R.color.red_color));

        Actions.overrideFonts(context, holder.llItem, true);
    }

    @Override
    public int getItemCount() {
        if (allOnlineOrders == null)
            return allOrders.size();
        else
            return allOnlineOrders.size();
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

        protected LinearLayout llItem;
        protected TextView tvCompanyOrderBook, tvCompanyItem,
                tvOrderAskPriceItem, tvOrderAskQuantityItem,
                tvOrderBidPriceItem, tvOrderBidQuantityItem;
        protected View v;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);
            this.llItem = v.findViewById(R.id.llItem);
            this.tvCompanyItem = v.findViewById(R.id.tvCompanyItem);
            this.tvOrderBidQuantityItem = v
                    .findViewById(R.id.tvOrderBidQuantityItem);
            this.tvOrderBidPriceItem = v
                    .findViewById(R.id.tvOrderBidPriceItem);
            this.tvOrderAskPriceItem = v
                    .findViewById(R.id.tvOrderAskPriceItem);
            this.tvOrderAskQuantityItem = v
                    .findViewById(R.id.tvOrderAskQuantityItem);

            this.tvCompanyOrderBook = v
                    .findViewById(R.id.tvCompanyOrderBook);

        }

        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }

}