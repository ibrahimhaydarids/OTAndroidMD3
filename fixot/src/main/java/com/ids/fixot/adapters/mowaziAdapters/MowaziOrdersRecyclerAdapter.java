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

public class MowaziOrdersRecyclerAdapter extends RecyclerView.Adapter<MowaziOrdersRecyclerAdapter.ViewHolder> {

    public static final int TYPE_SUMMARY = 0;
    public static final int TYPE_ONLINE = 1;
    public static final int HEADER = 0;
    public static final int ITEM = 1;
    private static final String TAG = "CustomAdapter";
    ArrayList<MowaziCompany> OrdersToSort = new ArrayList<>();
    private ArrayList<MowaziOrderBook> allOrders;
    private ArrayList<MowaziOnlineOrder> allOnlineOrders;
    private int type;
    private Activity context;
    private DecimalFormat myFormatter;
    private RecyclerViewOnItemClickListener itemClickListener;

    public MowaziOrdersRecyclerAdapter(Activity context,
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

        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mowazi_orders_recycler_item, viewGroup, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        if (type == TYPE_SUMMARY) {

            holder.tvCompanyItem.setText(MyApplication.lang == MyApplication.ENGLISH ? allOrders.get(position).getCompany().getSymbolEn() : allOrders.get(position).getCompany().getSymbolAr());

            holder.tvCompanyItem.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    b.putString("companyid", allOrders.get(position)
                            .getCompany().getCompanyId()
                            + "");
                    b.putString("forbid", allOrders.get(position).getCompany()
                            .getForBid()
                            + "");
                    i.putExtras(b);
                    i.setClass(context, MowaziCompanyDetailsActivity.class);
                    context.startActivity(i);
                }
            });

            holder.tvCompanyOrderBook.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    context.startActivity(new Intent(context, MowaziOrdersSummaryActivity.class)
                            .putExtra("companyId", allOrders.get(position).getCompany().getCompanyId())
                            .putExtra("companyname", MyApplication.lang == MyApplication.ARABIC ? allOrders.get(position).getCompany().getSymbolAr()
                                    : allOrders.get(position).getCompany().getSymbolEn()));
                }
            });

            holder.tvCompanyOrderBook.setVisibility(View.GONE);

            holder.tvCompanyItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

            holder.tvOrderAskItem.setText("" + allOrders.get(position).getAskCount());

            holder.tvOrderAskItem.setTextColor(ContextCompat.getColor(context, R.color.mowazi_dark_blue));

            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                    new Locale("US_en"));
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("0.000", otherSymbols);
            myFormatter = new DecimalFormat("#,###", otherSymbols);
            // String output =
            // myFormatter.format(allOrders.get(position).getAskquantity());
            holder.tvOrderQuantityItem.setText(""
                    + myFormatter.format(allOrders.get(position)
                    .getAskquantity()));
            holder.tvOrderQuantityItem.setTextColor(ContextCompat.getColor(
                    context, R.color.blue));

            // Float
            // lafloat=Float.parseFloat(allOrders.get(position).getAskPrice());
            String aa = df.format(allOrders.get(position).getAskPrice());
            holder.tvOrderPriceItem.setText(aa);
            // holder.tvOrderPriceItem.setText("" +
            // allOrders.get(position).getAskPrice());
            holder.tvOrderPriceItem.setTextColor(ContextCompat.getColor(
                    context, R.color.mowazi_dark_blue));

            holder.tvOfferQuantityItem.setText(""
                    + myFormatter.format(allOrders.get(position)
                    .getBidQuantity()));
            holder.tvOfferQuantityItem.setTextColor(ContextCompat.getColor(
                    context, R.color.red_color));

            holder.tvOrderBidItem.setText(""
                    + allOrders.get(position).getBidCount());
            holder.tvOrderBidItem.setTextColor(ContextCompat.getColor(context,
                    R.color.red_color));


        } else {

            holder.tvCompanyItem.setText(MyApplication.lang == MyApplication.ARABIC ? allOnlineOrders.get(position).getCompany().getSymbolAr() :
                    allOnlineOrders.get(position).getCompany().getSymbolEn());

            holder.tvCompanyItem.setTextColor(ContextCompat.getColor(context,
                    R.color.mowazi_dark_blue));

            if (allOnlineOrders.get(position).getOrderTpeId() == 1)
                holder.tvOrderAskItem.setText(context.getResources().getString(
                        R.string.sell));
            else
                holder.tvOrderAskItem.setText(context.getResources().getString(
                        R.string.buy));
            holder.tvOrderAskItem.setTextColor(ContextCompat.getColor(context,
                    R.color.mowazi_dark_blue));

            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                    new Locale("US_en"));
            otherSymbols.setDecimalSeparator('.');
            otherSymbols.setGroupingSeparator(',');
            DecimalFormat df = new DecimalFormat("#,###", otherSymbols);
            DecimalFormat dfPrice = new DecimalFormat("#,##0.0", otherSymbols);
            myFormatter = new DecimalFormat("#,###", otherSymbols);
            // Float
            // lafloat=Float.parseFloat(allOrders.get(position).getAskPrice());
            String aa = dfPrice
                    .format(allOnlineOrders.get(position).getPrice());

            holder.tvOrderQuantityItem.setText(""
                    + df.format(Integer.parseInt(allOnlineOrders.get(position)
                    .getOriginalShares())));
            holder.tvOrderQuantityItem.setTextColor(ContextCompat.getColor(
                    context, R.color.mowazi_dark_blue));

            holder.tvOrderPriceItem.setText(aa);
            holder.tvOrderPriceItem.setTextColor(ContextCompat.getColor(
                    context, R.color.mowazi_dark_blue));

            holder.tvOfferQuantityItem.setText(""
                    + allOnlineOrders.get(position).getExecutedShares());
            holder.tvOfferQuantityItem.setTextColor(ContextCompat.getColor(
                    context, R.color.mowazi_dark_blue));

            holder.tvOrderBidItem.setText(""
                    + myFormatter.format(Integer.parseInt(allOnlineOrders.get(
                    position).getOriginalShares())
                    - Integer.parseInt(allOnlineOrders.get(position)
                    .getExecutedShares())));
            holder.tvOrderBidItem.setTextColor(ContextCompat.getColor(context,
                    R.color.mowazi_dark_blue));
            holder.tvCompanyOrderBook.setVisibility(View.INVISIBLE);
            // }
        }

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

    public class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        protected LinearLayout llItem;
        protected TextView tvCompanyOrderBook, tvCompanyItem, tvOrderAskItem,
                tvOrderQuantityItem, tvOrderPriceItem, tvOfferQuantityItem,
                tvOrderBidItem;
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
            this.tvOrderBidItem = v
                    .findViewById(R.id.tvOrderBidItem);
            this.tvCompanyOrderBook = v
                    .findViewById(R.id.tvCompanyOrderBook);

        }

        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}