package com.ids.fixot.parser;


import com.ids.fixot.model.mowazi.MowaziCompany;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Amal on 5/4/2016.
 */
public class MowaziCompanyIdParser {

    private String JsonString;
    private String lang;

    public MowaziCompanyIdParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public MowaziCompany GetCompany() throws JSONException,
            UnsupportedEncodingException {

        ArrayList<MowaziCompany> companies = new ArrayList<MowaziCompany>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONObject json_data = object.getJSONObject("d");
        MowaziCompany item = new MowaziCompany();

        item.setCompanyId(json_data.getInt("CompanyId"));
        item.setSymbolEn(json_data.getString("SymbolEn"));
        item.setSymbolAr(json_data.getString("SymbolAr"));
        item.setSectorId(json_data.getInt("SectorId"));
        item.setForBid("false");
        item.setDescEn(json_data.getString("DescriptionEn"));
        item.setDescAr(json_data.getString("DescriptionAr"));
        item.setLastUpdate(json_data.getString("LastUpdatedDate"));
        item.setIsIssue(json_data.getString("IsIssue"));

        return item;
    }

}
