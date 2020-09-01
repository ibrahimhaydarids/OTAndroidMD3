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
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.mowazi.MowaziCompany;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziCompanyRecyclerArrayAdapter extends RecyclerView.Adapter<MowaziCompanyRecyclerArrayAdapter.ViewHolder> {

    private List<MowaziCompany> companies;
    private Activity context;
    private RecyclerViewOnItemClickListener itemClickListener;
    private SimpleDateFormat output = null, input;

    public MowaziCompanyRecyclerArrayAdapter(Activity context, ArrayList<MowaziCompany> companies, RecyclerViewOnItemClickListener itemClickListener) {

        this.companies = companies;
        this.context = context;
        this.itemClickListener = itemClickListener;

        input = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        output = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        RecyclerViewHolder holder = (RecyclerViewHolder) viewHolder;
        MowaziCompany company = companies.get(position);

        try {
            String date = company.getLastUpdate().substring(0, company.getLastUpdate().indexOf("T"));
            try {
                Date oneWayTripDate = input.parse(date);                 // parse input

                holder.lastupdate.setText(output.format(oneWayTripDate));

            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            holder.lastupdate.setText("--");
        }

        if (MyApplication.lang == MyApplication.ENGLISH) {

            holder.name.setText(company.getSymbolEn());
            //holder.companySector.setText(company.getSectorName());
        } else {

            holder.name.setText(company.getSymbolAr());
            //holder.companySector.setText(company.getSectorNameAr());
        }
        holder.companySector.setText(company.getSectorName());

        try {

            holder.forbid.setImageResource(company.getForBid().equals("true") ? R.drawable.marked : R.drawable.clearmark);
            holder.untransformed.setImageResource(company.getIsIssue().equals("true") ? R.drawable.marked : R.drawable.clearmark);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Actions.overrideFonts(context, holder.rel, true);

    }


    @Override
    public int getItemCount() {
        return this.companies.size();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView;

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_list_item, parent, false);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return (rcv);
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class RecyclerViewHolder extends ViewHolder implements View.OnClickListener {

        public TextView name, lastupdate, companySector;
        public ImageView forbid, untransformed;
        public ImageView arrow;
        protected View itemView;
        LinearLayout rel;


        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.itemView.setOnClickListener(this);
            forbid = itemView.findViewById(R.id.forbid);
            lastupdate = itemView.findViewById(R.id.lastupdate);
            companySector = itemView.findViewById(R.id.companySector);
            untransformed = itemView.findViewById(R.id.untransformd);
            name = itemView.findViewById(R.id.namesymbol);
            rel = itemView.findViewById(R.id.background);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(view, getLayoutPosition());
        }
    }
}
