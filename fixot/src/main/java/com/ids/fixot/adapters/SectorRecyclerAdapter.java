package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.StockActivity;
import com.ids.fixot.model.Sector;

import java.util.ArrayList;

/**
 * Created by Amal on 3/31/2017.
 */

public class SectorRecyclerAdapter extends RecyclerView.Adapter<SectorRecyclerAdapter.ViewHolder> {


    private ArrayList<Sector> allSectors;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public SectorRecyclerAdapter(Activity context, ArrayList<Sector> allSectors, RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allSectors = allSectors;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.sector_index_item, viewGroup, false);
        return new ItemViewHolder(v);


    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {


        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        Actions.overrideFonts(context, holder.lllayout, false);
        if (MyApplication.lang == MyApplication.ARABIC) {

            try {
                holder.tvSector.setText(allSectors.get(position).getNameAr());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvSector.setTypeface(MyApplication.droidbold);
        } else {

            try {
                holder.tvSector.setText(allSectors.get(position).getNameEn());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvSector.setTypeface(MyApplication.giloryBold);
        }

        try {

            String price = Actions.formatNumber(Double.parseDouble(allSectors.get(position).getValue()), Actions.ThreeDecimal);
            holder.tvIndexValue.setText(price);
        } catch (Exception e) {
            e.printStackTrace();
            //holder.tvIndexValue.setText(String.valueOf(allSectors.get(position).getValue()));
        }

        try {

            //String changePercent = Actions.formatNumber(Double.parseDouble(allSectors.get(position).getChangePercent()), Actions.OneDecimal) + "%";
            holder.tvChangePercent.setText(allSectors.get(position).getChangePercent());
            holder.tvChangePercent.setTextColor(Actions.textColor(allSectors.get(position).getChangePercent()));
        } catch (Exception e) {
            e.printStackTrace();
            //holder.tvChangePercent.setText(String.valueOf(allSectors.get(position).getChangePercent()));
        }

        holder.btStocks.setOnClickListener(v -> context.startActivity(new Intent(context, StockActivity.class)
                .putExtra("sectorId", allSectors.get(position).getSectorID())
                .putExtra("sectorName", MyApplication.lang == MyApplication.ARABIC ? allSectors.get(position).getNameAr() : allSectors.get(position).getNameEn())));


        Actions.setTypeface(new TextView[]{holder.tvChangePercent, holder.tvIndexValue}, MyApplication.giloryBold);
//        holder.btStocks.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidbold : MyApplication.giloryBold);

//        if (position % 2 == 1) {
//
//            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
////            holder.btStocks.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//        }else {
//
//            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
////            holder.btStocks.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
//        }

        if (position % 2 == 0) {
            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        } else {
            holder.lllayout.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
        }

    }

    @Override
    public int getItemCount() {
        return allSectors.size();
    }

    @Override
    public int getItemViewType(int position) {

        return 1;
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

        protected TextView tvSector, tvIndexValue, tvChangePercent;
        protected ImageButton btStocks;
        protected View v;
        LinearLayout lllayout;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);

            this.tvSector = v.findViewById(R.id.tvSector);
            this.tvIndexValue = v.findViewById(R.id.tvIndexValue);
            this.tvChangePercent = v.findViewById(R.id.tvChangePercent);
            this.btStocks = v.findViewById(R.id.btStocks);
            this.lllayout = v.findViewById(R.id.lllayout);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}
