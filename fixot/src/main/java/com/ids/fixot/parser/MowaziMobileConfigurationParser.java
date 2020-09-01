package com.ids.fixot.parser;

import com.ids.fixot.model.mowazi.MowaziMobileConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziMobileConfigurationParser {

    private String JsonString;
    private String lang;

    public MowaziMobileConfigurationParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MowaziMobileConfiguration> GetConfigurations()
            throws JSONException, UnsupportedEncodingException {

        ArrayList<MowaziMobileConfiguration> configs = new ArrayList<>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MowaziMobileConfiguration item = new MowaziMobileConfiguration();
            if (json_data.length() > 1) {
                item.setId(json_data.getInt("id"));
                item.setName(json_data.getString("name"));
                item.setValue(json_data.getString("value"));
                configs.add(item);
            }

        }
        return configs;
    }

}
