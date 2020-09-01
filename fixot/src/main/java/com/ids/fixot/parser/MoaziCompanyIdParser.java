package com.ids.fixot.parser;


import com.ids.fixot.MyApplication;
import com.ids.fixot.model.Sector;
import com.ids.fixot.model.mowazi.MoaziCompany;
import com.ids.fixot.model.mowazi.MoaziSector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



/**
 * Created by Amal on 5/4/2016.
 */
public class MoaziCompanyIdParser {
    String JsonString;
    String lang;

    public MoaziCompanyIdParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public MoaziCompany GetCompany()
            throws JSONException {

        ArrayList<MoaziCompany> companies = new ArrayList<MoaziCompany>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONObject json_data = object.getJSONObject("d");
        //for (int i = 0; i < jarray.length(); i++) {
        //JSONObject json_data = jarray.getJSONObject(i);
        MoaziCompany item = new MoaziCompany();

        //if(json_data.length()>1) {
        item.setCompanyId(json_data.getInt("CompanyId"));
        //   item.setNameEn(json_data.getString("NameEn"));
        //  item.setNameAr(json_data.getString("NameAr"));
        item.setSymbolEn(json_data.getString("SymbolEn"));
        item.setSymbolAr(json_data.getString("SymbolAr"));
        item.setChartUrl(json_data.getString("chartUrl"));
        item.setSectorId(json_data.getInt("SectorId"));
        // item.setCountryId(json_data.getInt("CountryId"));

        item.setForBid(json_data.getString("ForBids"));
       // item.setForBid("false");//json_data.getString("ForBids"));

        //item.setIsStoped(json_data.getString("IsStopped"));

        // item.setStoppedDate(json_data.getString("StoppedDate"));
        // item.setCreationDate(json_data.getString("CreationDate"));
        item.setDescEn(json_data.getString("DescriptionEn"));
        item.setDescAr(json_data.getString("DescriptionAr"));
        item.setLastUpdate(json_data.getString("LastUpdatedDate"));
        // item.setUntransformed(json_data.getString("UnTransformed"));

        //item.setIsIssue(json_data.getString("IsIssue"));
        item.setIsIssue(json_data.getString("IsIssue"));

        String json2 = "{\"e\":[" + json_data.getString("Sector") + "]}";
        JSONObject sectordata = new JSONObject(json2);

        JSONArray sectorarray = sectordata.getJSONArray("e");

        for (int j = 0; j < sectorarray.length(); j++) {
            JSONObject json_data_sector = sectorarray.getJSONObject(j);
            MoaziSector sector = new MoaziSector();


            if(MyApplication.lang==MyApplication.ARABIC) {
                sector.setName(json_data_sector.getString("NameAr"));
            }
            else {
                sector.setName(json_data_sector.getString("NameEn"));
            }
            item.setSectorName(json_data_sector.getString("NameEn"));
            item.setSectorNameAr(json_data_sector.getString("NameAr"));
            item.setSector(sector);
        }

        return item;
    }

}



