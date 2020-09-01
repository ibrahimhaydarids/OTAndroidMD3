package com.ids.fixot.parser;

import android.util.Log;

import com.ids.fixot.model.mowazi.MowaziClient;
import com.ids.fixot.model.mowazi.MowaziCompany;
import com.ids.fixot.model.mowazi.MowaziOnlineOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOnlineOrderParser {

    String JsonString;
    String lang;

    public MowaziOnlineOrderParser(String jsonString, String lang) {
        this.JsonString = jsonString;
        this.lang = lang;
    }

    public ArrayList<MowaziOnlineOrder> GetOrders() throws JSONException,
            UnsupportedEncodingException {

        ArrayList<MowaziOnlineOrder> orders = new ArrayList<MowaziOnlineOrder>();
        JsonString = "{\"d\":" + JsonString + "}";
        JSONObject object = new JSONObject(JsonString);

        JSONArray jarray = object.getJSONArray("d");
        for (int i = 0; i < jarray.length(); i++) {
            JSONObject json_data = jarray.getJSONObject(i);
            MowaziOnlineOrder item = new MowaziOnlineOrder();
            if (json_data.length() > 1) {
                item.setOrderId(json_data.getInt("OrderId"));
                item.setClientId(json_data.getInt("ClientId"));
                item.setCompanyId(json_data.getInt("CompanyId"));
                item.setPrice(json_data.getDouble("Price"));
                item.setOriginalShares(json_data.getString("OriginalShares"));
                item.setExecutedShares(json_data.getString("ExecutedShares"));

                if (json_data.getString("MinimumSharesToBook").equals("null")) {
                    item.setMinimumSharesToBook("0");
                } else {
                    item.setMinimumSharesToBook(json_data
                            .getString("MinimumSharesToBook"));
                }
                item.setOrderTpeId(json_data.getInt("OrderTypeId"));
                item.setOrderStatusId(json_data.getInt("OrderStatusId"));

                // item.setDeleted(json_data.getString("IsDeleted"));

                item.setOrderDate(json_data.getString("OrderDate"));
                item.setExpirationDate(json_data.getString("ExpirationDate"));
                item.setActivationDate(json_data.getString("ActivationDate"));
                item.setGoodUntilDate(json_data.getString("GoodUntilDate"));

                try {
                    String json2 = "{\"e\":[" + json_data.getString("Client")
                            + "]}";
                    JSONObject clientdata = new JSONObject(json2);

                    JSONArray clientarray = clientdata.getJSONArray("e");

                    for (int j = 0; j < clientarray.length(); j++) {
                        try {

                            JSONObject json_data_client = clientarray
                                    .getJSONObject(j);
                            MowaziClient client = new MowaziClient();
                            client.setClientId(json_data_client
                                    .getInt("ClientId"));
                            client.setFirstName(json_data_client
                                    .getString("FirstName"));
                            client.setMiddleName(json_data_client
                                    .getString("MiddleName"));
                            client.setLastName(json_data_client
                                    .getString("LastName"));
                            client.setTitleId(json_data_client
                                    .getInt("TitleId"));
                            client.setCreationDate(json_data_client
                                    .getString("CreationDate"));
                            client.setNationality(json_data_client
                                    .getInt("Nationality"));
                            item.setClient(client);
                        } catch (Exception e) {

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String json3 = "{\"f\":[" + json_data.getString("Company")
                        + "]}";
                JSONObject compani = new JSONObject(json3);

                JSONArray companyArray = compani.getJSONArray("f");

                for (int j = 0; j < companyArray.length(); j++) {

                    try {
                        JSONObject json_data_company = companyArray
                                .getJSONObject(j);
                        MowaziCompany company = new MowaziCompany();
                        company.setCompanyId(json_data_company
                                .getInt("CompanyId"));
                        company.setSymbolEn(json_data_company
                                .getString("SymbolEn"));
                        company.setSymbolAr(json_data_company
                                .getString("SymbolAr"));
                        item.setCompany(company);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                orders.add(item);

            }

        }

        Log.wtf("Size", "" + orders.size());
        return orders;
    }

}
