package com.ids.fixot.parser;

import com.ids.fixot.MyApplication;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziSector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by Amal on 5/4/2016.
 */
public class MowaziCompanyParser {

    private String JsonString;
    private String lang;

    public MowaziCompanyParser(String jsonString, String lang) {
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
                item.setSymbolEn(json_data.getString("SymbolEn"));
                item.setSymbolAr(json_data.getString("SymbolAr"));
                item.setSectorId(json_data.getInt("SectorId"));
                item.setForBid(json_data.getString("ForBids"));
                item.setLastUpdate(json_data.getString("LastUpdatedDate"));
                item.setIsIssue(json_data.getString("IsIssue"));

                String json2 = "{\"e\":[" + json_data.getString("Sector")
                        + "]}";
                JSONObject sectordata = new JSONObject(json2);

                JSONArray sectorarray = sectordata.getJSONArray("e");

                for (int j = 0; j < sectorarray.length(); j++) {
                    JSONObject json_data_sector = sectorarray.getJSONObject(j);
                    MowaziSector sector = new MowaziSector();
                    sector.setId(json_data_sector.getInt("SectorId"));
                    item.setSectorId(json_data_sector.getInt("SectorId"));

                    if (MyApplication.lang == MyApplication.ARABIC) {
                        sector.setName(json_data_sector.getString("NameAr"));
                        item.setSectorName(json_data_sector.getString("NameAr"));
                    } else {
                        item.setSectorName(json_data_sector.getString("NameEn"));
                        sector.setName(json_data_sector.getString("NameEn"));
                    }
                    item.setSector(sector);
                }

                companies.add(item);
            }

        }

        return companies;
    }

}
