package com.ids.fixot.adapters;

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
import com.ids.fixot.classes.MenuItem;

import java.util.ArrayList;

/**
 * Created by DEV on 4/9/2018.
 */

public class MenuRecyclerAdapter extends RecyclerView.Adapter<MenuRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<MenuItem> items;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;

    public MenuRecyclerAdapter(Activity context, ArrayList<MenuItem> items, RecyclerViewOnItemClickListener itemClickListener) {

        this.items = items;
        this.context = context;
        this.itemClickListener = itemClickListener;
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {


        MenuItem item = items.get(position);
        holder.tvItem.setText(item.getName());
        holder.ivItem.setImageResource(item.getImageId());

        Actions.overrideFonts(context, holder.llLayout, false);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView;

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_page, parent, false);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return (rcv);
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvItem;
        private ImageView ivItem;
        private View itemView, view;
        private LinearLayout llLayout;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);

            llLayout = itemView.findViewById(R.id.llLayout);
            tvItem = itemView.findViewById(R.id.tvItem);
            ivItem = itemView.findViewById(R.id.ivItem);

        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(view, getLayoutPosition());
        }
    }

}
