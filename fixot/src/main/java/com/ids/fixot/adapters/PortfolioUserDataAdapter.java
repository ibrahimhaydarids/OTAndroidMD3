package com.ids.fixot.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.CashSummary;
import com.ids.fixot.model.Portfolio;
import com.ids.fixot.model.StockSummary;

/**
 * Created by user on 7/21/2017.
 */

public class PortfolioUserDataAdapter extends RecyclerView.Adapter<PortfolioUserDataAdapter.RecyclerViewHolder> {

    private Activity context;
    private Portfolio portfolio;

    public PortfolioUserDataAdapter(Activity context, Portfolio portfolio) {
        this.portfolio = portfolio;
        this.context = context;
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {

        CashSummary cashSummary = portfolio.getCashSummary();

        switch (position) {

            case 0:
                holder.tvTitle.setText(context.getResources().getString(R.string.limit_title));
                holder.tvValue.setText(cashSummary.getLimit());
                break;

            case 1:
                holder.tvTitle.setText(context.getResources().getString(R.string.credit_title));
                holder.tvValue.setText(String.valueOf(cashSummary.getCredit()));
                break;

            case 2:
                holder.tvTitle.setText(context.getResources().getString(R.string.debit_title));
                holder.tvValue.setText(String.valueOf(cashSummary.getDebit()));
                break;

            case 3:
                holder.tvTitle.setText(context.getResources().getString(R.string.pending_title));
                holder.tvValue.setText(String.valueOf(cashSummary.getPendingPurchaseOrders()));
                break;

            case 4:
                holder.tvTitle.setText(context.getResources().getString(R.string.purchase_power_title));
                holder.tvValue.setText(String.valueOf(cashSummary.getPurchasePower()));
                break;

            case 5:
                holder.tvTitle.setText(context.getResources().getString(R.string.bank_balance_title));
                holder.tvValue.setText(String.valueOf(cashSummary.getBankAccountBalance()));
                break;

            case 6:
                holder.tvTitle.setText(context.getResources().getString(R.string.available_withdrawal));

                try {
                    //holder.tvValue.setText(Actions.formatNumber(Double.parseDouble(cashSummary.getAvailableForWithdrawal()), Actions.ThreeDecimalThousandsSeparator));
                    holder.tvValue.setText(String.valueOf(cashSummary.getBankAccountBalance()));
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.tvValue.setText(String.valueOf(cashSummary.getBankAccountBalance()));
                }
                break;

            case 7:
                holder.tvTitle.setText(context.getResources().getString(R.string.amount_under_issue));

                try {
                    //holder.tvValue.setText(Actions.formatNumber(Double.parseDouble(cashSummary.getAmountUnderIssue()), Actions.ThreeDecimalThousandsSeparator));
                    holder.tvValue.setText(cashSummary.getAmountUnderIssue());
                } catch (Exception e) {
                    e.printStackTrace();
                    holder.tvValue.setText("0.0");
                }

                break;

            case 8:
                holder.tvTitle.setText(context.getResources().getString(R.string.Total_portfolio_title));
                holder.tvValue.setText(String.valueOf(0.0));
                double total;
                StockSummary summary = null;
                try {
                    summary = portfolio.getAllStockSummaries().get(portfolio.getAllStockSummaries().size() - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    summary = new StockSummary();
                }
                try {
                    total = Double.parseDouble(cashSummary.getCredit())
                            - Double.parseDouble(cashSummary.getDebit())
                            + Double.parseDouble(summary.getTotalCost());
                    //+ Double.parseDouble(cashSummary.getTotalCost());
                } catch (Exception e) {
                    total = 0;//Double.parseDouble(cashSummary.getCredit()) - Double.parseDouble(cashSummary.getDebit());
                }

                //holder.tvValue.setText(Actions.formatNumber(total,Actions.ThreeDecimalThousandsSeparator));
                holder.tvValue.setText(String.valueOf(total));

                break;
        }

        //Actions.overrideFonts(context, holder.llItem, false);

        holder.tvTitle.setTypeface(MyApplication.lang == MyApplication.ARABIC ? MyApplication.droidregular : MyApplication.giloryItaly);
        holder.tvValue.setTypeface(MyApplication.giloryBold);

    }

    @Override
    public int getItemCount() {
        return 9;
    }

    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView;

        layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_user_data_item, parent, false);
        RecyclerViewHolder rcv = new RecyclerViewHolder(layoutView);
        return (rcv);
    }

    public interface RecyclerViewOnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvValue;
        private View itemView;
        private LinearLayout llItem;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            llItem = itemView.findViewById(R.id.llItem);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}