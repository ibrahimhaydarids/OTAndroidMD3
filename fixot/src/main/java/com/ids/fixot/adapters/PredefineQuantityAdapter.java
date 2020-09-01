package com.ids.fixot.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.R;

/**
 * Created by user on 04/01/2019.
 */

public class PredefineQuantityAdapter extends RecyclerView.Adapter<PredefineQuantityAdapter.MyViewHolder> {

    int selectedPosition;
    private int[] dataSet;
    private Context jcontext;
    private RecyclerViewOnItemClickListener itemClickListener;


    public PredefineQuantityAdapter(Context scontext, int[] data, RecyclerViewOnItemClickListener mitemClickListener, int selectedPos) {
        this.dataSet = data;
        this.jcontext = scontext;
        this.itemClickListener = mitemClickListener;
        this.selectedPosition = selectedPos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView textVtext = holder.textViewtext;
        textVtext.setText(Actions.formatNumber(dataSet[position], Actions.NoDecimalThousandsSeparator));
    }

    @Override
    public int getItemCount() {
        return dataSet.length;
    }

    public boolean isEnabled(int position) {
        return position == 1 || position == 5 || Actions.isMarketOpen();
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClickedd(View v, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewtext;
        private View itemView;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);
            this.textViewtext = itemView.findViewById(R.id.tvItem);
        }


        @Override
        public void onClick(View v) {
            itemClickListener.onItemClickedd(v, getLayoutPosition());
        }
    }


}