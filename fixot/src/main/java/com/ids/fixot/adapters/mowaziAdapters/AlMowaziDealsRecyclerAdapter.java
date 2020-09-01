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
import com.ids.fixot.model.mowazi.AlmowaziDeal;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by DEV on 3/29/2018.
 */

public class AlMowaziDealsRecyclerAdapter extends RecyclerView.Adapter<AlMowaziDealsRecyclerAdapter.ViewHolder> {

    public static final int HEADER = 0;
    public static final int ITEM = 1;
    private static final String TAG = "CustomAdapter";
    SimpleDateFormat output = null, input;
    private ArrayList<AlmowaziDeal> allDeals;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;
    private DecimalFormat myFormatter = new DecimalFormat("#,###");

    public AlMowaziDealsRecyclerAdapter(Activity context,
                                        ArrayList<AlmowaziDeal> allDeals,
                                        RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allDeals = allDeals;
        this.itemClickListener = itemClickListener;
        input = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        output = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mowazi_deals_item, viewGroup, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
                new Locale("US_en"));
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("0.000", otherSymbols);
        DecimalFormat dfPrice = new DecimalFormat("#,##0.0", otherSymbols);
        myFormatter = new DecimalFormat("#,###", otherSymbols);
        if (MyApplication.lang == MyApplication.ARABIC)
            holder.tvCompanyItem.setText(allDeals.get(position).getSymbolAr());
        else
            holder.tvCompanyItem.setText(allDeals.get(position).getSymbolEn()
                    .trim());

        holder.tvCompanyItem.setTextColor(ContextCompat.getColor(context,
                R.color.mowazi_dark_blue));
        // DecimalFormat df = new DecimalFormat("0.00");
        Float lafloat = Float.parseFloat(allDeals.get(position)
                .getAveragePrice());
        String aa = dfPrice.format(lafloat);
        holder.tvAverageItem.setText(aa);
        holder.tvAverageItem.setTextColor(ContextCompat.getColor(context,
                R.color.mowazi_dark_blue));

        holder.tvQuantityItem.setText(""
                + myFormatter.format(allDeals.get(position).getQuantity()));
        holder.tvQuantityItem.setTextColor(ContextCompat.getColor(context,
                R.color.blue));

        holder.tvValue.setText(""
                + myFormatter.format(Integer.parseInt(allDeals.get(position)
                .getVolume()) * MyApplication.brokerageQuantityCoif));
        holder.tvValue.setTextColor(ContextCompat.getColor(context,
                R.color.mowazi_dark_blue));

        holder.tvCountItem.setText("" + allDeals.get(position).getDealDate());
        try {
            String date = allDeals.get(position).getDealDate();
            Date oneWayTripDate = input.parse(date); // parse input

            holder.tvCountItem.setText(output.format(oneWayTripDate));

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvCountItem.setTextColor(ContextCompat.getColor(context,
                R.color.mowazi_dark_blue));

        Actions.overrideFonts(context, holder.llItem, true);
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

    public class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        protected LinearLayout llItem;

        protected TextView tvCompanyItem, tvQuantityItem, tvAverageItem,
                tvCountItem, tvValue;
        protected View v;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);

            this.llItem = v.findViewById(R.id.llItem);
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