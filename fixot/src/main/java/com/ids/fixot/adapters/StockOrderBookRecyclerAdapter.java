package com.ids.fixot.adapters;

import android.app.Activity;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ids.fixot.Actions;
import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.StockOrderBook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 3/30/2017.
 */

public class StockOrderBookRecyclerAdapter extends RecyclerView.Adapter<StockOrderBookRecyclerAdapter.ViewHolder> {

    double maxAsk = 0.0;
    double maxBid = 0.0;
    private ArrayList<StockOrderBook> allOrders;
    private Activity context;
    private boolean orderByPrice=true;

    public StockOrderBookRecyclerAdapter(Activity context, ArrayList<StockOrderBook> allOrders,boolean orderByPrice) {
        this.context = context;
        this.allOrders = allOrders;
        this.orderByPrice=orderByPrice;
    }

    @Override
    public StockOrderBookRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_book_item, viewGroup, false);
        return new StockOrderBookRecyclerAdapter.ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        ItemViewHolder holder = (ItemViewHolder) viewHolder;

        StockOrderBook order = allOrders.get(position);
        StockOrderBook maxAskStock, maxBidStock;

        try {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                maxAskStock = Collections.max(allOrders.subList(0, allOrders.size() - 1), Comparator.comparing(s -> s.getAskValue()));
                maxBidStock = Collections.max(allOrders.subList(0, allOrders.size() - 1), Comparator.comparing(s -> s.getBidValue()));

                maxAsk = maxAskStock.getAskValue();
                maxBid = maxBidStock.getBidValue();

            } else {

                maxAskStock = new StockOrderBook();
                maxBidStock = new StockOrderBook();

                for (int i = 0; i < allOrders.size() - 1; i++) {

                    if (allOrders.get(i).getAskValue() > maxAskStock.getAskValue()) {

                        maxAskStock = allOrders.get(i);
                    }

                    if (allOrders.get(i).getBidValue() > maxBidStock.getBidValue()) {

                        maxBidStock = allOrders.get(i);
                    }

                }

                maxAsk = maxAskStock.getAskValue();
                maxBid = maxBidStock.getBidValue();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        if(orderByPrice) {
            try {
                if (order.getAsk() != null && !order.getAsk().isEmpty() && !order.getAsk().matches("0") && !order.getAsk().matches("null"))
                    holder.tvAskNumberItem.setText(order.getAsk());
                else
                    holder.tvAskNumberItem.setText("");
            } catch (Exception e) {
                holder.tvAskNumberItem.setText("");
            }
        }else {
            try {
                if (order.getAskQuantity() != null && !order.getAskQuantity().isEmpty() && !order.getAskQuantity().matches("0") && !order.getAskQuantity().matches("null"))
                    holder.tvAskNumberItem.setText(getTimeFromDate(order.getOrderTime()));
                else
                    holder.tvAskNumberItem.setText("1");
            } catch (Exception e) {
                Log.wtf("bob",e.toString());
                holder.tvAskNumberItem.setText("2");
            }
        }





        try {
            if (order.getAskQuantity() != null && !order.getAskQuantity().isEmpty() && !order.getAskQuantity().matches("0"))
                holder.tvAskQtyItem.setText(order.getAskQuantity());
            else
                holder.tvAskQtyItem.setText("");
        } catch (Exception e) {
            holder.tvAskQtyItem.setText("");
        }


        try {
            if (order.getPrice() != null && !order.getPrice().isEmpty())
                holder.tvPriceItem.setText(order.getPrice());
            else
                holder.tvPriceItem.setText("");
        } catch (Exception e) {
            holder.tvPriceItem.setText("");
        }



        if(orderByPrice) {
            try {
                if (order.getBid() != null && !order.getBid().isEmpty() && !order.getBid().matches("0") && !order.getBid().matches("null"))
                    holder.tvBidNumberItem.setText(order.getBid());
                else
                    holder.tvBidNumberItem.setText("");
            } catch (Exception e) {
                holder.tvBidNumberItem.setText("");
            }
        }else {
            try {
                if (order.getBidQuantity() != null && !order.getBidQuantity().isEmpty() && !order.getBidQuantity().matches("0") && !order.getBidQuantity().matches("null"))
                    holder.tvBidNumberItem.setText(getTimeFromDate(order.getOrderTime()));
                else
                    holder.tvBidNumberItem.setText("");
            } catch (Exception e) {
                holder.tvBidNumberItem.setText("");
            }
        }






        try {
            if (order.getBidQuantity() != null && !order.getBidQuantity().isEmpty() && !order.getBidQuantity().matches("0"))
                holder.tvBidQtyItem.setText(order.getBidQuantity());
            else
                holder.tvBidQtyItem.setText("");
        } catch (Exception e) {
            holder.tvBidQtyItem.setText("");
        }








        if (order.getAskQuantity().length() > 0 && order.getBidQuantity().length() > 0 && !order.getAskQuantity().matches("0")&& !order.getBidQuantity().matches("0")) { //all gray

            holder.separator.setVisibility(View.GONE);
            holder.tvAskQtyItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));
            holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));

            holder.tvBidNumberItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));
            holder.tvBidQtyItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.colorValues : R.color.colorValuesInv));

            holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.gray : R.color.grayInv));


        } else {

            holder.separator.setVisibility(View.VISIBLE);
            if (position % 2 == 1) {

                if (order.getAskQuantity().length() > 0 && !order.getAskQuantity().matches("0")){

                    double askPercentage = (order.getAskValue() * 100) / maxAsk;
                    if (askPercentage < 10.0) {
                        askPercentage = askPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(askPercentage == 100 ? ContextCompat.getColor(context, R.color.red_color) : ContextCompat.getColor(context, R.color.light_red_color));
                    double width = MyApplication.screenWidth * ((askPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) width, 2);
                    params.gravity = Gravity.START;
                    holder.separator.setLayoutParams(params);

                    if (order.getAskQuantity().equals("0")) {
                        holder.separator.setVisibility(View.GONE);
                    } else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }

                    holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.red_color : R.color.light_white));
                    holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, R.color.odd_red_color));
                } else {


                    double bidPercentage = (order.getBidValue() * 100) / maxBid;
                    if (bidPercentage < 10.0) {
                        bidPercentage = bidPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(bidPercentage == 100 ? ContextCompat.getColor(context, R.color.green_color) : ContextCompat.getColor(context, R.color.light_green_color));
                    double width = MyApplication.screenWidth * ((bidPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) width, 2);
                    params.gravity = Gravity.END;
                    holder.separator.setLayoutParams(params);

                    if (order.getBidQuantity().equals("0")) {
                        holder.separator.setVisibility(View.GONE);
                    } else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }


                    //holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                    holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?
                            R.color.green_color : R.color.light_white));
                    holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, R.color.odd_green_color));
                }
            } else {

                if (order.getAskQuantity().length() > 0 && !order.getAskQuantity().matches("0")) {

                    double askPercentage = (order.getAskValue() * 100) / maxAsk;
                    if (askPercentage < 10.0) {
                        askPercentage = askPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(askPercentage == 100 ? ContextCompat.getColor(context, R.color.red_color) : ContextCompat.getColor(context, R.color.light_red_color));
                    double width = MyApplication.screenWidth * ((askPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) width, 2);
                    params.gravity = Gravity.START;
                    holder.separator.setLayoutParams(params);

                    if (order.getAskQuantity().equals("0")) {
                        holder.separator.setVisibility(View.GONE);
                    } else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }

                    holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ?
                            R.color.red_color : R.color.light_white));
                    holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, R.color.even_red_color));
                } else {

                    double bidPercentage = (order.getBidValue() * 100) / maxBid;
                    if (bidPercentage < 10.0) {
                        bidPercentage = bidPercentage + 10;
                    }
                    holder.separator.setBackgroundColor(bidPercentage == 100 ? ContextCompat.getColor(context, R.color.green_color) : ContextCompat.getColor(context, R.color.light_green_color));
                    double width = MyApplication.screenWidth * ((bidPercentage * 0.6) / 100);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) width, 2);
                    params.gravity = Gravity.END;
                    holder.separator.setLayoutParams(params);

                    if (order.getBidQuantity().equals("0")) {
                        holder.separator.setVisibility(View.GONE);
                    } else {

                        holder.separator.setVisibility(View.VISIBLE);
                    }

                    //holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, R.color.green_color));
                    holder.tvPriceItem.setTextColor(ContextCompat.getColor(context, MyApplication.mshared.getBoolean(context.getResources().getString(R.string.normal_theme), true) ? R.color.green_color : R.color.light_white));
                    holder.tvPriceItem.setBackgroundColor(ContextCompat.getColor(context, R.color.even_green_color));
                }
            }


        }

        Actions.overrideFonts(context, holder.llItem, false);
        Actions.setTypeface(new TextView[]{holder.tvAskNumberItem, holder.tvAskQtyItem, holder.tvPriceItem, holder.tvBidNumberItem, holder.tvBidQtyItem}, MyApplication.giloryBold);

    }




    public String getTimeFromDate(String dateTime) throws ParseException {
       // Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
        String newString="";
      try {
          Date date = new SimpleDateFormat("M/dd/yyyy HH:mm:ss a", Locale.ENGLISH).parse(dateTime);
           newString = new SimpleDateFormat("HH:mm:ss a", Locale.ENGLISH).format(date); // 9:00
      }catch (Exception e){
            Log.wtf("date_exception_1",e.toString());
          try{
              Date date = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a", Locale.ENGLISH).parse(dateTime);
               newString = new SimpleDateFormat("HH:mm:ss a", Locale.ENGLISH).format(date); // 9:00
          }catch (Exception e1){
              Log.wtf("date_exception_2",e1.toString());
              try{
                  Date date = new SimpleDateFormat("MM/dd/yyyy H:mm:ss a", Locale.ENGLISH).parse(dateTime);
                  newString = new SimpleDateFormat("HH:mm:ss a", Locale.ENGLISH).format(date); // 9:00
              }catch (Exception e3){
                  Log.wtf("date_exception_3",e3.toString());
              }
          }



      }

        return newString;
    }

    @Override
    public int getItemCount() {
        return allOrders.size();
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    public interface RecyclerViewOnItemClickListener {

        void onItemClicked(View v, int position);

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(View v) {
            super(v);
        }
    }

    private class ItemViewHolder extends ViewHolder {

        protected View v;
        protected View separator;
        TextView tvAskNumberItem, tvAskQtyItem, tvPriceItem, tvBidQtyItem, tvBidNumberItem;
        LinearLayout llItem;

        private ItemViewHolder(View v) {
            super(v);
            this.v = v;

            this.separator = v.findViewById(R.id.separator);
            this.llItem = v.findViewById(R.id.llItem);
            this.tvAskNumberItem = v.findViewById(R.id.tvAskNumberItem);
            this.tvPriceItem = v.findViewById(R.id.tvPriceItem);
            this.tvAskQtyItem = v.findViewById(R.id.tvAskQtyItem);
            this.tvBidQtyItem = v.findViewById(R.id.tvBidQtyItem);
            this.tvBidNumberItem = v.findViewById(R.id.tvBidNumberItem);
        }

    }
}
