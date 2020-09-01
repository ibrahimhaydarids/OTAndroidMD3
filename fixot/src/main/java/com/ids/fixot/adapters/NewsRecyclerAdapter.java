package com.ids.fixot.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.NewsDetailsActivity;
import com.ids.fixot.model.NewsItem;

import java.util.ArrayList;

import static com.ids.fixot.MyApplication.lang;


/**
 * Created by dev on 10/4/2016.
 */

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.RecyclerViewHolder> {

    private ArrayList<NewsItem> news;
    private Activity context;

    public NewsRecyclerAdapter(Activity context, ArrayList<NewsItem> newss) {
        this.news = newss;
        this.context = context;
    }

    public static boolean isProbablyArabic(String s) {
        for (int i = 0; i < s.length(); ) {
            int c = s.codePointAt(i);
            if (c >= 0x0600 && c <= 0x06E0)
                return true;
            i += Character.charCount(c);
        }
        return false;
    }

    public static void overrideFonts(Context context, final View v, boolean isMowazi) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child, isMowazi);
                }
            } else if (v instanceof TextView) {
                if (lang == MyApplication.ARABIC) {
                    ((TextView) v).setTypeface(MyApplication.droidbold);
                } else {
                    ((TextView) v).setTypeface(MyApplication.giloryBold);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        holder.tvHead.setText(news.get(position).getHead());
        holder.tvTime.setText(news.get(position).getTime());

        holder.tvHead.setGravity(Gravity.RIGHT);
        holder.tvTime.setGravity(Gravity.RIGHT);
        holder.llItem.setOnClickListener(v -> {

            Bundle b = new Bundle();
            b.putParcelable("newsItem", news.get(position));
            Intent i = new Intent();
            i.putExtras(b);

            i.setClass(context, NewsDetailsActivity.class);
            context.startActivity(i);
        });

//        if (position % 2 == 1)
//            holder.llItems.setBackgroundColor(ContextCompat.getColor(context, R.color.colorLight));
//        else
//            holder.llItems.setBackgroundColor(ContextCompat.getColor(context, R.color.white));

        if (position % 2 == 0) {
            holder.llItems.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.white : R.color.colorDarkTheme));
        } else {
            holder.llItems.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorLight : R.color.colorLightInv));
        }

        overrideFonts(context, holder.tvHead, false);

        if (isProbablyArabic(news.get(position).getHead())) {
            //Log.wtf("News","is arabic");
            holder.tvHead.setTypeface(MyApplication.droidbold);
        }

    }

    @Override
    public int getItemCount() {
        return this.news.size();
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView;

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return (rcv);
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        public TextView tvHead, tvTime;
        protected View itemView;
        RelativeLayout llItem;
        LinearLayout llItems;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            llItem = itemView.findViewById(R.id.llItem);
            llItems = itemView.findViewById(R.id.llItems);
            tvHead = itemView.findViewById(R.id.tvHead);
            tvTime = itemView.findViewById(R.id.tvTime);

        }

    }
}