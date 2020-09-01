package com.ids.fixot.parser;

import com.ids.fixot.model.mowazi.AlmowaziDeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by DEV on 3/29/2018.
 */

public class AlmowaziDealParser {

    String JsonString;
    String lang;

    public AlmowaziDealParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<AlmowaziDeal> GetDeals() throws JSONException,
            UnsupportedEncodingException {

        ArrayList<AlmowaziDeal> allDeals = new ArrayList<AlmowaziDeal>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            AlmowaziDeal item = new AlmowaziDeal();
            item.setCompanyId(json_data.getInt("CompanyId"));
            item.setDealId(json_data.getInt("DealId"));
            item.setSymbolEn(json_data.getString("SymbolEn"));
            item.setSymbolAr(json_data.getString("SymbolAr"));
            item.setMinPrice("" + json_data.getInt("MinPrice"));
            item.setAveragePrice("" + json_data.getInt("AveragePrice"));
            item.setMaxPrice("" + json_data.getInt("MaxPrice"));
            item.setVolume("" + json_data.getInt("Volume"));
            item.setCount(json_data.getInt("Count"));
            item.setQuantity(json_data.getInt("Quantity"));
            try {
                item.setDealDate(json_data.getString("DealDate"));
            } catch (Exception e) {

            }

            allDeals.add(item);
        }

        return allDeals;
    }

}
