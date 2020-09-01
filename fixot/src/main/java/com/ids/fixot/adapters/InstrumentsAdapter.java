package com.ids.fixot.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ids.fixot.MyApplication;
import com.ids.fixot.R;
import com.ids.fixot.model.Instrument;

import java.util.ArrayList;

/**
 * Created by DEV on 3/5/2018.
 */

public class InstrumentsAdapter extends ArrayAdapter<Instrument> {

    Boolean showArrow;
    private Activity context;
    private ArrayList<Instrument> allInstruments;
    private LayoutInflater inflter;

    public InstrumentsAdapter(Activity applicationContext, ArrayList<Instrument> allInstruments, Boolean showArrows) {
        super(applicationContext, R.layout.instrument_item);
        this.context = applicationContext;
        this.allInstruments = allInstruments;
        this.showArrow = showArrows;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return allInstruments.size();
    }

    @Override
    public Instrument getItem(int i) {
        return allInstruments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.instrument_item, null);

        TextView names = view.findViewById(R.id.tvItem);
        names.setText(MyApplication.lang == MyApplication.ENGLISH ? allInstruments.get(i).getInstrumentNameEn() : allInstruments.get(i).getInstrumentNameAr());

        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.instrument_item, null);

        TextView names = view.findViewById(R.id.tvItem);
        names.setText(MyApplication.lang == MyApplication.ENGLISH ? allInstruments.get(i).getInstrumentNameEn() : allInstruments.get(i).getInstrumentNameAr());

        return view;
    }

}
