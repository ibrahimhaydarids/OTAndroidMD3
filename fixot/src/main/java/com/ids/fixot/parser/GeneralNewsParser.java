package com.ids.fixot.parser;

import com.ids.fixot.MyApplication;
import com.ids.fixot.model.mowazi.MowaziNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class GeneralNewsParser {

    private String JsonString;
    private String lang;

    public GeneralNewsParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MowaziNews> GetNews()
            throws JSONException, UnsupportedEncodingException {

        ArrayList<MowaziNews> news = new ArrayList<MowaziNews>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            if (MyApplication.lang == MyApplication.ENGLISH)
                if (json_data.getString("TitleEn").equals("")) {
                    continue;
                }
            MowaziNews item = new MowaziNews();
            item.setId(json_data.getInt("NewsId"));
            try {
                item.setCompanyId(json_data.getInt("CompanyID"));
            } catch (Exception e) {

                e.printStackTrace();
            }
            try {
                item.setPictureName(json_data.getString("PictureName"));
            } catch (Exception e) {

                e.printStackTrace();
            }
            item.setOnHomePage(json_data.getString("OnHomePage"));
            try {
                if (MyApplication.lang == MyApplication.ENGLISH) {
                    item.setTitle(json_data.getString("TitleEn"));
                } else {
                    item.setTitle(json_data.getString("TitleAr"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                item.setTitle("---");
            }
            try {
                if (MyApplication.lang == MyApplication.ENGLISH) {
                    item.setContent(json_data.getString("ContentEn"));
                } else {
                    item.setContent(json_data.getString("ContentAr"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                item.setDate(json_data.getString("CreationDate"));
            } catch (Exception e) {

                e.printStackTrace();
            }
            try {
                item.setIsActive(json_data.getString("IsActive"));
            } catch (Exception e) {

                e.printStackTrace();
            }

            news.add(item);

        }


        return news;
    }

}

