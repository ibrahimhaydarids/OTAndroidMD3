package com.ids.fixot.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.Sector;

import java.util.ArrayList;

/**
 * Created by user on 10/11/2017.
 */

public class SectorSpinnerAdapter extends ArrayAdapter<Sector> {

    private Activity context;
    private ArrayList<Sector> allSectors;
    private LayoutInflater inflter;

    public SectorSpinnerAdapter(Activity applicationContext, ArrayList<Sector> allSectors) {
        super(applicationContext, R.layout.sector_spinner_item);
        this.context = applicationContext;
        this.allSectors = allSectors;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return allSectors.size();
    }

    @Override
    public Sector getItem(int i) {
        return allSectors.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        view = inflter.inflate(R.layout.sector_spinner, null);

        TextView tvItem = view.findViewById(R.id.tvItem);

        if (MyApplication.lang == MyApplication.ARABIC) {

            tvItem.setText(allSectors.get(i).getNameAr());
            tvItem.setTypeface(MyApplication.droidregular);
        } else {

            tvItem.setText(allSectors.get(i).getNameEn());
            tvItem.setTypeface(MyApplication.giloryItaly);
        }

//        tvItem.setTextColor(ContextCompat.getColor(context, R.color.colorValues));
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.sector_spinner_item, null);

        View vista = view;

        if (vista == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            vista = inflater.inflate(R.layout.sector_spinner_item, null);
        }

        TextView tvItem = view.findViewById(R.id.tvItem);
        if (MyApplication.lang == MyApplication.ARABIC) {

            tvItem.setText(allSectors.get(i).getNameAr());
            tvItem.setTypeface(MyApplication.droidregular);
        } else {

            tvItem.setText(allSectors.get(i).getNameEn());
            tvItem.setTypeface(MyApplication.giloryItaly);
        }

//        LinearLayout lllayout =  view.findViewById(R.id.llLayout);
//        lllayout.setBackgroundColor(ContextCompat.getColor(context, R.color.red_color));

        return vista; // view ;
    }
}
