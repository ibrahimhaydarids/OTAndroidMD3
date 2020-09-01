package com.ids.fixot.parser;

import com.ids.fixot.model.mowazi.MoaziAssemblyResult;
import com.ids.fixot.model.mowazi.MoaziCommunity;
import com.ids.fixot.model.mowazi.MoaziCompany;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dev on 6/15/2016.
 */

public class MoaziAssemblyResultParser {
    String JsonString;
    String lang;

    public MoaziAssemblyResultParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MoaziAssemblyResult> GetAssemblies()
            throws JSONException {

        ArrayList<MoaziAssemblyResult> assemblies = new ArrayList<MoaziAssemblyResult>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MoaziAssemblyResult item = new MoaziAssemblyResult();
            if (json_data.length() > 1) {
                item.setId(json_data.getInt("Id"));
                item.setYear(json_data.getString("Year"));
                item.setCommunityId(json_data.getInt("CommunityId"));

                item.setCapital(json_data.getString("Capital"));
                /*item.setChangeFrom(json_data.getString("ChangeFrom"));
                item.setChangeTo(json_data.getString("ChangeTo"));
                item.setWinLossPastYear(json_data.getString("WinLossPastYear"));
                item.setWinLossCurrentYear(json_data.getString("WinLossCurrentYear"));
                item.setDistributionCashPastYear(json_data.getString("DistributionCashCurrentYear"));
                item.setDistributionDonationCurrentYear(json_data.getString("DistributionDonationCurrentYear"));
                item.setDistributionDonationPastYear(json_data.getString("DistributionDonationPastYear"));
                item.setDistributionPremiumPastYear(json_data.getString("DistributionPremiumPastYear"));
                item.setDistributionPremiumCurrentYear(json_data.getString("DistributionPremiumCurrentYear"));*/

                String json2 = "{\"e\":[" + json_data.getString("MoaziCommunity") + "]}";
                JSONObject communitydata = new JSONObject(json2);

                JSONArray communityarray = communitydata.getJSONArray("e");

                for (int j = 0; j < communityarray.length(); j++) {
                    JSONObject json_data_community = communityarray.getJSONObject(j);

                    MoaziCommunity community = new MoaziCommunity();

                    try {
                        community.setCommunityID(json_data_community.getInt("CommunityId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        community.setCompanyID(json_data_community.getInt("CompanyId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        community.setCommunityTypeID(json_data_community.getInt("CommunityTypeId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    /*community.setDescriptionAr(json_data_community.getString("DescriptionAr"));
                    community.setDescriptionEn(json_data_community.getString("DescriptionEn"));
                    community.setCreationDate(json_data_community.getString("CreationDate"));*/


                    String json3 = "{\"f\":[" + json_data_community.getString("MoaziCompany") + "]}";
                    JSONObject companydata = new JSONObject(json3);

                    JSONArray companyarray = companydata.getJSONArray("f");

                    for (int k = 0; k < companyarray.length(); k++) {
                        JSONObject json_data_company = companyarray.getJSONObject(k);
                        MoaziCompany company = new MoaziCompany();
                        /*company.setCompanyId(json_data_company.getInt("CompanyId"));
                        company.setDescAr(json_data_company.getString("DescriptionAr"));
                        company.setDescEn(json_data_company.getString("DescriptionEn"));
                        company.setCreationDate(json_data_company.getString("CreationDate"));
                        company.setLastUpdate(json_data_company.getString("LastUpdatedDate"));*/

                        try {
                            company.setSymbolAr(json_data_company.getString("SymbolAr"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            company.setSymbolEn(json_data_company.getString("SymbolEn"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        community.setCompany(company);

                    }
                    item.setCommunity(community);

                }
                assemblies.add(item);
            }

        }

        return assemblies;
    }

}