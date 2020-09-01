package com.ids.fixot.parser;

import com.ids.fixot.model.mowazi.MowaziDeal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Amal on 2/19/2016.
 */
public class MowaziDealParser {

    private String JsonString;
    private String lang;

    public MowaziDealParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MowaziDeal> GetDeals() throws JSONException,
            UnsupportedEncodingException {

        ArrayList<MowaziDeal> deals = new ArrayList<MowaziDeal>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MowaziDeal item = new MowaziDeal();
            item.setDealId(json_data.getInt("DealId"));
            item.setCompanyId(json_data.getInt("CompanyId"));
            item.setBuyerId(json_data.getInt("BuyerId"));
            item.setSellerId(json_data.getInt("SellerId"));
            item.setPrice(json_data.getInt("Price"));
            item.setBuyOrderId(json_data.getInt("BuyOrderId"));
            item.setSellOrderId(json_data.getInt("SellOrderId"));
            item.setQuantity(json_data.getInt("Quantity"));

            item.setDealDate(json_data.getString("DealDate").substring(0,
                    json_data.getString("DealDate").indexOf("T")));
            if (lang.equals("en"))
                item.setCompany(json_data.getString("CommentEn"));
            else
                item.setCompany(json_data.getString("CommentAr"));
            deals.add(item);
        }
        return deals;
    }

}
