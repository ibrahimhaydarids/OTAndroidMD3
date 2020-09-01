package com.ids.fixot.adapters;

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
import com.ids.fixot.model.Instrument;

import java.util.ArrayList;

/**
 * Created by Amal on 3/31/2017.
 */

public class InstrumentsRecyclerAdapter extends RecyclerView.Adapter<InstrumentsRecyclerAdapter.ViewHolder> {

    private ArrayList<Instrument> allInstruments = new ArrayList<>();
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public InstrumentsRecyclerAdapter(Activity context, ArrayList<Instrument> alInstrumentss, RecyclerViewOnItemClickListener itemClickListener) {
        this.context = context;
        this.allInstruments = alInstrumentss;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.instrument_market_item, viewGroup, false);
        return new ItemViewHolder(v);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        Actions.overrideFonts(context, holder.lllayout, false);

        holder.tvTitle.setText(allInstruments.get(position).getInstrumentName());

//        if (MyApplication.lang == MyApplication.ARABIC){
//            holder.tvSector.setText(allInstruments.get(position).getNameAr());
//            holder.tvInstrument.setTypeface(MyApplication.droidbold);
//        }else{
//            holder.tvInstrument.setText(allInstruments.get(position).getNameEn());
//            holder.tvInstrument.setTypeface(MyApplication.giloryBold);
//        }

        if (allInstruments.get(position).getIsSelected()) {
            holder.tvTitle.setBackground(context.getResources().getDrawable(R.drawable.border_instrument_select));
//            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.colorDarkInv));
            holder.tvTitle.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorDark : R.color.colorDarkInv));

        } else {
            holder.tvTitle.setBackground(context.getResources().getDrawable(R.drawable.border_instrument_unselect));
            holder.tvTitle.setTextColor(context.getResources().getColor(R.color.darkgray));
        }
//        holder.tvTitle.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?  R.color.colorDark  : R.color.colorDarkInv));
    }

    @Override
    public int getItemCount() {
        return allInstruments.size();
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

        protected View v;
        TextView tvTitle;
        LinearLayout lllayout;

        public ItemViewHolder(View v) {
            super(v);
            this.v = v;
            this.v.setOnClickListener(this);

            this.tvTitle = itemView.findViewById(R.id.tvInstrumentName);
            this.lllayout = v.findViewById(R.id.llInstrumentName);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }
}
