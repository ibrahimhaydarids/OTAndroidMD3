package com.ids.fixot.parser;

import com.ids.fixot.model.mowazi.MowaziOrderType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOrderTypeParser {

    String JsonString;
    String lang;

    public MowaziOrderTypeParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MowaziOrderType> GetOrderTypes() throws JSONException,
            UnsupportedEncodingException {

        ArrayList<MowaziOrderType> allOrderTpes = new ArrayList<MowaziOrderType>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MowaziOrderType item = new MowaziOrderType();
            item.setOrderTypeId(json_data.getInt("OrderTypeId"));
            item.setNameEn(json_data.getString("NameEn"));
            item.setNameAr(json_data.getString("NameAr"));

            allOrderTpes.add(item);
        }

        return allOrderTpes;
    }

}

