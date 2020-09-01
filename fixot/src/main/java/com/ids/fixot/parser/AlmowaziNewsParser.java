package com.ids.fixot.parser;


import com.ids.fixot.MyApplication;
import com.ids.fixot.model.mowazi.MowaziNews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AlmowaziNewsParser {

    private String JsonString;
    private String lang;
    private boolean haveNoEn = false;

    public AlmowaziNewsParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public boolean isHaveNoEn() {
        return haveNoEn;
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
                    if (!json_data.getString("TitleAr").equals(""))
                        haveNoEn = true;
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
                item.setPictureName(json_data.getString("PictureURL"));
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
                item.setTitleEn(json_data.getString("TitleEn"));
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
                item.setTitle("---");
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
