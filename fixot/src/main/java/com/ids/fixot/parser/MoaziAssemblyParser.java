package com.ids.fixot.parser;

import android.util.Log;

import com.ids.fixot.model.mowazi.MoaziAssembly;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Amal on 6/14/2016.
 */
public class MoaziAssemblyParser {
    String JsonString;
    String lang;

    public MoaziAssemblyParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MoaziAssembly> GetAssemblies()
            throws JSONException {

        ArrayList<MoaziAssembly> assemblies = new ArrayList<MoaziAssembly>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");

        Log.wtf("Array Length: ", "" + jarray.length());

        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MoaziAssembly item = new MoaziAssembly();
            if (json_data.length() > 1) {
                item.setCommunityId(json_data.getInt("CommunityId"));
                item.setDescriptionEn(json_data.getString("DescriptionEn"));
                item.setDescriptionAr(json_data.getString("DescriptionAr"));
                item.setCompanyId(json_data.getInt("CompanyId"));
                item.setCommunityDate(json_data.getString("CommunityDate").substring(0, json_data.getString("CommunityDate").indexOf("T")));
                item.setCommunitytypeId(json_data.getInt("CommunityTypeId"));
                item.setCreationDate(json_data.getString("CreationDate"));
//                item.getCompany().setSymbolAr(json_data.getString("SymbolAr"));
//                item.getCompany().setSymbolEn(json_data.getString("SymbolEn"));
//                String json2 = "{\"e\":[" + json_data.getString("Company") + "]}";
//                JSONObject compani = new JSONObject(json2);
//
//                JSONArray companyArray = compani.getJSONArray("e");
//
//                for (int j = 0; j < companyArray.length(); j++) {
//                    JSONObject json_data_company = companyArray.getJSONObject(j);
//                    Company company = new Company();
//                    company.setCompanyId(json_data_company.getInt("CompanyId"));
//                    company.setDescAr(json_data_company.getString("DescriptionAr"));
//                    company.setDescEn(json_data_company.getString("DescriptionEn"));
//                    company.setSymbolAr(json_data_company.getString("SymbolAr"));
//                    company.setSymbolEn(json_data_company.getString("SymbolEn"));
//                    company.setCreationDate(json_data_company.getString("CreationDate"));
//                    company.setLastUpdate(json_data_company.getString("LastUpdatedDate"));
//                    item.setCompany(company);
//
//                }
                assemblies.add(item);
            }
        }

        return assemblies;
    }

}



