package com.ids.fixot.parser;

import com.ids.fixot.model.mowazi.MowaziOrderBook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOrderBookParser {

    private String JsonString;
    private String lang;

    public MowaziOrderBookParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MowaziOrderBook> getOrderBooks() throws JSONException,
            UnsupportedEncodingException {

        ArrayList<MowaziOrderBook> orderbooks = new ArrayList<MowaziOrderBook>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MowaziOrderBook item = new MowaziOrderBook();
            item.setId(json_data.getInt("ID"));
            item.setBidPrice(json_data.getInt("BidPrice"));
            item.setCompanyId(json_data.getInt("CompanyId"));
            item.setAskPrice(json_data.getDouble("AskPrice"));
            item.setBidcount(json_data.getInt("BidsCount"));
            item.setBidQuantity(json_data.getInt("BidQuantity"));
            item.setAskquantity(json_data.getInt("AskQuantity"));
            item.setAskCount(json_data.getInt("AsksCount"));
            item.getCompany().setSymbolAr(json_data.getString("SymbolAr"));
            item.getCompany().setSymbolEn(json_data.getString("SymbolEn"));

            orderbooks.add(item);

        }

        return orderbooks;
    }

}
