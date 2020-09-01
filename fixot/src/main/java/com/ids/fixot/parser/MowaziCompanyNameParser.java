package com.ids.fixot.parser;

import com.ids.fixot.model.mowazi.MowaziCompany;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by dev on 6/17/2016.
 */

public class MowaziCompanyNameParser {

    private String JsonString;
    private String lang;

    public MowaziCompanyNameParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MowaziCompany> GetCompanies() throws JSONException,
            UnsupportedEncodingException {

        ArrayList<MowaziCompany> companies = new ArrayList<MowaziCompany>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MowaziCompany item = new MowaziCompany();
            if (json_data.length() > 1) {
                item.setCompanyId(json_data.getInt("CompanyId"));
                item.setSymbolAr(json_data.getString("SymbolAr"));
                item.setSymbolEn(json_data.getString("SymbolEn"));
                companies.add(item);
            }

        }

        return companies;
    }

}
