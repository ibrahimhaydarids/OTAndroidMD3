package com.ids.fixot.adapters.mowaziAdapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.activities.mowazi.MoaziCompanyDetailsActivity;
import com.ids.fixot.activities.mowazi.MowaziCompanyDetailsActivity;
import com.ids.fixot.model.mowazi.MoaziAssembly;
import com.ids.fixot.model.mowazi.MoaziAssemblyResult;
import com.ids.fixot.model.mowazi.MoaziViewResizing;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * Created by dev on 6/14/2016.
 */

public class MoaziAssembliesRecyclerAdapter extends RecyclerView.Adapter<MoaziAssembliesRecyclerAdapter.ViewHolder> {

    private ArrayList<MoaziAssembly> allAssemblies;
    private ArrayList<MoaziAssemblyResult> allAssemblyResults;
    private Activity context;

    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
            new Locale("US_en"));

    DecimalFormat df = new DecimalFormat("#,###", otherSymbols);
    private DecimalFormat myFormatter = new DecimalFormat("#,###",otherSymbols);
    private RecyclerViewOnItemClickListener itemClickListener;
    //,i=0, j=0;
    private int type;
    public static final int TYPE_UPCOMING = 1;
    public static final int TYPE_PREVIOUS = 2;
    public static final int TYPE_RESULT = 3;

    public static final int HEADER = 0;
    public static final int ITEM = 1;
    SimpleDateFormat output = null, input;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class HeaderViewHolder extends ViewHolder {

        protected TextView tvCompanyHeader, tvTypeHeader, tvDateHeader;
        protected TextView tvOrganizationHeader, tvYearHeader, tvCapitalHeader;
        protected int type;
        protected View v;

        public HeaderViewHolder(View v, int type) {
            super(v);
            this.type = type;
            this.v = v;

            if (type == TYPE_RESULT) {
                tvOrganizationHeader = v.findViewById(R.id.tvOrganizationHeader);
                tvTypeHeader = v.findViewById(R.id.tvTypeHeader);
                tvYearHeader = v.findViewById(R.id.tvYearHeader);
                tvCapitalHeader = v.findViewById(R.id.tvCapitalHeader);
            } else {
                tvCompanyHeader = v.findViewById(R.id.tvCompanyHeader);
                tvTypeHeader = v.findViewById(R.id.tvTypeHeader);
                tvDateHeader = v.findViewById(R.id.tvDateHeader);
            }

            MoaziViewResizing.setListRowTextResizing(itemView, context, MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
        }

    }

    public class ItemViewHolder extends ViewHolder implements View.OnClickListener {

        protected TextView tvCompanyItem, tvTypeItem, tvDateItem;
        protected TextView tvOrganizationItem, tvYearItem, tvCapitalItem;
        protected LinearLayout llAssemblyItem; //same layoutId for both
        protected int type;
        protected View v;

        public ItemViewHolder(View v, int type) {
            super(v);
            this.type = type;
            this.v = v;
            this.v.setOnClickListener(this);

            if (type == TYPE_RESULT) {
                tvOrganizationItem = v.findViewById(R.id.tvOrganizationItem);
                tvTypeItem = v.findViewById(R.id.tvTypeItem);
                tvYearItem = v.findViewById(R.id.tvYearItem);
                tvCapitalItem = v.findViewById(R.id.tvCapitalItem);
            } else {
                tvCompanyItem = v.findViewById(R.id.tvCompanyItem);
                tvTypeItem = v.findViewById(R.id.tvTypeItem);
                tvDateItem = v.findViewById(R.id.tvDateItem);
            }
            llAssemblyItem = v.findViewById(R.id.llAssemblyItem);
            MoaziViewResizing.setListRowTextResizing(itemView, context,MyApplication.lang == MyApplication.ARABIC ? "ar" : "en");
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClicked(v, getLayoutPosition());
        }
    }


    public MoaziAssembliesRecyclerAdapter(Activity context, ArrayList<MoaziAssembly> allAssemblies, ArrayList<MoaziAssemblyResult> allAssemblyResults, RecyclerViewOnItemClickListener itemClickListener, int type) {
        this.context = context;
        this.type = type;
        if (allAssemblies != null)
            this.allAssemblies = allAssemblies;
        else
            this.allAssemblyResults = allAssemblyResults;

        this.itemClickListener = itemClickListener;

        input = new SimpleDateFormat("yyyy-MM-dd");
        output = new SimpleDateFormat("dd-MM-yyyy");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;

        if (type == TYPE_RESULT) {
            if (viewType == HEADER) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.moazi_assembly_result_recycler_header, viewGroup, false);
                return new HeaderViewHolder(v, type);
            } else {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.moazi_assembly_result_recycler_item, viewGroup, false);
                return new ItemViewHolder(v, type);
            }
        } else {
            if (viewType == HEADER) {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.moazi_assemblies_recycler_header, viewGroup, false);
                return new HeaderViewHolder(v, type);
            } else {
                v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.moazi_assemblies_recycler_item, viewGroup, false);
                return new ItemViewHolder(v, type);
            }
        }

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if (type == TYPE_RESULT) {
            if (viewHolder.getItemViewType() == HEADER) {
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

                holder.tvOrganizationHeader.setText(context.getResources().getString(R.string.organization));
                holder.tvOrganizationHeader.setTextColor(ContextCompat.getColor(context, R.color.white));

                holder.tvYearHeader.setText(context.getResources().getString(R.string.year));
                holder.tvYearHeader.setTextColor(ContextCompat.getColor(context, R.color.white));

                holder.tvTypeHeader.setText(context.getResources().getString(R.string.orgType));
                holder.tvTypeHeader.setTextColor(ContextCompat.getColor(context, R.color.white));

                holder.tvCapitalHeader.setText(context.getResources().getString(R.string.capitalKWD));
                holder.tvCapitalHeader.setTextColor(ContextCompat.getColor(context, R.color.white));

            } else {
                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                try {
                    if (MyApplication.lang==MyApplication.ENGLISH)
                        holder.tvOrganizationItem.setText("" + allAssemblyResults.get(position).getCommunity().getCompany().getSymbolEn());
                    else
                        holder.tvOrganizationItem.setText("" + allAssemblyResults.get(position).getCommunity().getCompany().getSymbolAr());
                } catch (Exception e) {

                }
                holder.tvOrganizationItem.setTextColor(ContextCompat.getColor(context, R.color.darkblue));

                holder.tvYearItem.setText("" + allAssemblyResults.get(position).getYear());
                holder.tvYearItem.setTextColor(ContextCompat.getColor(context, R.color.darkblue));

                switch (allAssemblyResults.get(position).getCommunity().getCommunityTypeID()) {

                    case 0:
                        holder.tvTypeItem.setText("0");
                        break;
                    case 1:
                        holder.tvTypeItem.setText(context.getResources().getString(R.string.normalAssembly));
                        break;
                    case 2:
                        holder.tvTypeItem.setText(context.getResources().getString(R.string.unnormalAssembly));
                        break;
                    case 3:
                        holder.tvTypeItem.setText(context.getResources().getString(R.string.normUnnormAssembly));
                        break;
                    case 4:
                        holder.tvTypeItem.setText(context.getResources().getString(R.string.establishedAssembly));
                        break;

                }
                holder.tvTypeItem.setTextColor(ContextCompat.getColor(context, R.color.darkblue));

                holder.tvCapitalItem.setText("" + myFormatter.format(Integer.parseInt(allAssemblyResults.get(position).getCapital())));
                holder.tvCapitalItem.setTextColor(ContextCompat.getColor(context, R.color.darkblue));
            }

        } else {
            if (viewHolder.getItemViewType() == HEADER) {
                HeaderViewHolder holder = (HeaderViewHolder) viewHolder;

                holder.tvCompanyHeader.setText(context.getResources().getString(R.string.company));
                holder.tvCompanyHeader.setTextColor(ContextCompat.getColor(context, R.color.white));

                holder.tvTypeHeader.setText(context.getResources().getString(R.string.communityType));
                holder.tvTypeHeader.setTextColor(ContextCompat.getColor(context, R.color.white));

                holder.tvDateHeader.setText(context.getResources().getString(R.string.date));
                holder.tvDateHeader.setTextColor(ContextCompat.getColor(context, R.color.white));

            } else {
                ItemViewHolder holder = (ItemViewHolder) viewHolder;

                try {
                    if (MyApplication.lang==MyApplication.ENGLISH)
                        holder.tvCompanyItem.setText(allAssemblies.get(position).getDescriptionEn());
                    else
                        holder.tvCompanyItem.setText(allAssemblies.get(position).getDescriptionAr());
                } catch (Exception e) {
                    holder.tvCompanyItem.setText("");
                }
                holder.tvCompanyItem.setTextColor(ContextCompat.getColor(context, R.color.darkblue));
                holder.llAssemblyItem.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                /*Intent i = new Intent();
                Bundle b = new Bundle();
                b.putParcelable("company", allOrders.get(position).getCompany());
                i.putExtras(b);
                i.setClass(context, CompanyDetailsActivity.class);
                context.startActivity(i);*/

                        if (MyApplication.lang==MyApplication.ENGLISH)
                            context.startActivity(new Intent(context,
                                    MowaziCompanyDetailsActivity.class)
                                    .putExtra("companyId", ""+allAssemblies.get(position).getCompanyId())
                                    .putExtra("companyname", allAssemblies.get(position).getDescriptionEn()));
                        else
                            context.startActivity(new Intent(context,
                                    MowaziCompanyDetailsActivity.class)
                                    .putExtra("companyId", ""+allAssemblies.get(position).getCompanyId())
                                    .putExtra("companyname", allAssemblies.get(position).getDescriptionAr()));


                    }
                });
                switch (allAssemblies.get(position).getCommunitytypeId()) {

                    case 0:
                        holder.tvTypeItem.setText("0");
                        break;
                    case 1:
                        holder.tvTypeItem.setText(context.getResources().getString(R.string.normalAssembly));
                        break;
                    case 2:
                        holder.tvTypeItem.setText(context.getResources().getString(R.string.unnormalAssembly));
                        break;
                    case 3:
                        holder.tvTypeItem.setText(context.getResources().getString(R.string.normUnnormAssembly));
                        break;
                    case 4:
                        holder.tvTypeItem.setText(context.getResources().getString(R.string.establishedAssembly));
                        break;

                }
                holder.tvTypeItem.setTextColor(ContextCompat.getColor(context, R.color.darkblue));

              //  holder.tvDateItem.setText(allAssemblies.get(position).getCommunityDate());//.substring(0,allAssemblies.get(position).getCommunityDate().indexOf("T")));
                try {
                    String date = allAssemblies.get(position).getCommunityDate();
                    Date oneWayTripDate = input.parse(date);                 // parse input

                    holder.tvDateItem.setText(output.format(oneWayTripDate));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                holder.tvDateItem.setTextColor(ContextCompat.getColor(context, R.color.darkblue));

            }
        }
    }

    @Override
    public int getItemCount() {
        if (allAssemblies != null)
            return allAssemblies.size();
        else
            return allAssemblyResults.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return HEADER;
        else
            return ITEM;
    }
}