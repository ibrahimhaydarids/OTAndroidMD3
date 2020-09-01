package com.ids.fixot.parser;

import com.ids.fixot.model.mowazi.MowaziSector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by dev on 6/20/2016.
 */

public class MowaziSectorParser {

    private String JsonString;
    private String lang;

    public MowaziSectorParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MowaziSector> GetSectors() throws JSONException,
            UnsupportedEncodingException {

        ArrayList<MowaziSector> sectors = new ArrayList<MowaziSector>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MowaziSector item = new MowaziSector();
            if (json_data.length() > 1) {
                item.setId(json_data.getInt("id"));
                item.setName(json_data.getString("name"));
                sectors.add(item);
            }

        }

        return sectors;
    }
}
