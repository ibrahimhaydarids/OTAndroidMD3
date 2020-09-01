package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziNews;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziNewsRecyclerAdapter extends RecyclerView.Adapter<MowaziNewsRecyclerAdapter.RecyclerViewHolder> {

    private List<MowaziNews> allNews;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;
    private int general;

    public MowaziNewsRecyclerAdapter(Activity context, ArrayList<MowaziNews> allNews, RecyclerViewOnItemClickListener itemClickListener, int general) {
        this.allNews = allNews;
        this.context = context;
        this.general = general;
        this.itemClickListener = itemClickListener;
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.mowazi_news_item, null);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder,
                                 final int position) {
        holder.rlNews.setBackgroundColor(ContextCompat.getColor(context,
                android.R.color.white));

        if (MyApplication.lang == MyApplication.ARABIC) {

            holder.rlNews.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            holder.rlNews.setTextDirection(View.TEXT_DIRECTION_RTL);

        } else {

            holder.rlNews.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            holder.rlNews.setTextDirection(View.TEXT_DIRECTION_LTR);

        }

        holder.title.setText(allNews.get(position).getTitle());
        try {
            holder.date.setText(allNews
                    .get(position)
                    .getDate()
                    .subSequence(0,
                            allNews.get(position).getDate().indexOf("T")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            Picasso.with(context)
                    .load("path" + allNews.get(position).getPictureName())
                    .placeholder(R.drawable.micon)
                    .into(holder.img, new com.squareup.picasso.Callback() {

                        public void onSuccess() {
                            // do smth when picture is loaded successfully
                            holder.img.setAlpha((float) 1);
                        }

                        public void onError() {
                            // progress.setVisibility(View.GONE);
                            // do smth when there is picture loading error
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        holder.shareimg.setOnClickListener(v -> {

            String shareBody = MyApplication.mowaziNewsLink
                    + allNews.get(position).getId();
            if (general == 0)
                shareBody = MyApplication.mowaziGeneralNewsLink
                        + "1&NewsID=" + allNews.get(position).getId();
            else if (general == 2)
                shareBody = MyApplication.mowaziGeneralNewsLink
                        + "2&NewsID=" + allNews.get(position).getId();
            Intent sharingIntent = new Intent(
                    Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,
                    allNews.get(position).getTitle());
            sharingIntent.putExtra(Intent.EXTRA_TEXT,
                    shareBody);
            context.startActivity(Intent.createChooser(sharingIntent,
                    "Share"));

        });

        Actions.overrideFonts(context, holder.rlNews, true);
    }

    @Override
    public int getItemCount() {
        return this.allNews.size();
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        protected TextView title, date;
        protected RelativeLayout rlNews;
        protected View itemView;
        ImageView img, shareimg;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            rlNews = itemView.findViewById(R.id.rel);
            img = itemView.findViewById(R.id.img);
            shareimg = itemView.findViewById(R.id.shareimg);

        }

        public void onClick(View view) {
            itemClickListener.onItemClicked(view, getLayoutPosition());
        }
    }
}