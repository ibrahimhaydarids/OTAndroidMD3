package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziHomepageAdapter extends RecyclerView.Adapter<MowaziHomepageAdapter.RecyclerViewHolder> {

    private List<MowaziCategory> itemList;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public MowaziHomepageAdapter(Activity context, ArrayList<MowaziCategory> itemList, RecyclerViewOnItemClickListener itemClickListener) {

        this.itemList = itemList;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, null);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        holder.tvTextGrid.setText(itemList.get(position).getName());
        holder.ivGrid.setImageResource(itemList.get(position).getImage());
        holder.llGridItem.setTag(itemList.get(position).getName());

        Actions.overrideFonts(context, holder.llGridItem, true);
    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected View itemView;
        private TextView tvTextGrid;
        private ImageView ivGrid;
        private LinearLayout llGridItem;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);
            tvTextGrid = itemView.findViewById(R.id.tvTextGrid);
            ivGrid = itemView.findViewById(R.id.ivGrid);
            llGridItem = itemView.findViewById(R.id.llGridItem);
        }

        public void onClick(View view) {
            itemClickListener.onItemClicked(view, getLayoutPosition());
        }
    }
}