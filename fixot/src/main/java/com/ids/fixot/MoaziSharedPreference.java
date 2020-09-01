package com.ids.fixot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.ids.fixot.model.mowazi.MoaziCompany;
import com.ids.fixot.model.mowazi.MoaziCompanyNotifications;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dev on 7/29/2016.
 */

public class MoaziSharedPreference {

    public static final String PREFS_NAME = "MOWAZI_APP";
    public static final String FAVORITES = "Company_Favorite";
    public static final String NOTIFICATION_SETTINGS = "Company_Notification";

    public MoaziSharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<MoaziCompany> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        for (MoaziCompany item : favorites) {
            Log.d("List item",item.toString());
        }

        editor.commit();
    }

    public boolean isFound(Context context, MoaziCompany company){
        List<MoaziCompany> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<MoaziCompany>();
        for (int i=0; i<favorites.size(); i++){

            if(favorites.get(i).equals(company) ){
                return true;
            }

        }
        return false;
    }

    public void addFavorite(Context context, MoaziCompany company) {
        List<MoaziCompany> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<MoaziCompany>();

        for (int i=0; i<favorites.size(); i++){

            if(favorites.get(i).equals(company)){
                Log.d("Already","exists");
                return;
            }
            /*if(favorites.get(i).getCompanyId() == company.getCompanyId()){
                Log.d("Already","exists");
                return;
            }*/
        }
        Log.d("before","add");
        favorites.add(company);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, MoaziCompany company) {
        ArrayList<MoaziCompany> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(company);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<MoaziCompany> getFavorites(Context context) {
        SharedPreferences settings;
        List<MoaziCompany> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            MoaziCompany[] favoriteItems = gson.fromJson(jsonFavorites,
                    MoaziCompany[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<MoaziCompany>(favorites);
        } else
            return null;

        return (ArrayList<MoaziCompany>) favorites;
    }





    public void saveSettings(Context context, List<MoaziCompanyNotifications> companyNotifications) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonSettings = gson.toJson(companyNotifications);

        editor.putString(NOTIFICATION_SETTINGS, jsonSettings);

        for (MoaziCompanyNotifications item : companyNotifications) {
            Log.d("List item",item.toString());
        }

        editor.commit();
    }

    public boolean isFoundSettings(Context context, MoaziCompanyNotifications companyNotification){
        List<MoaziCompanyNotifications> notificationsSettings = getSettings(context);
        if (notificationsSettings == null)
            notificationsSettings = new ArrayList<MoaziCompanyNotifications>();
        for (int i=0; i<notificationsSettings.size(); i++){

            if(notificationsSettings.get(i).equals(companyNotification) ){
                return true;
            }

        }
        return false;
    }

    public int isFoundLaunchSettings(Context context, int id){
        List<MoaziCompanyNotifications> notificationsSettings = getSettings(context);
        if (notificationsSettings == null)
            notificationsSettings = new ArrayList<MoaziCompanyNotifications>();
        else{
            Log.wtf("notificationsSettings ", ""+notificationsSettings.size());
        }
        for (int i=0; i<notificationsSettings.size(); i++){

            if(notificationsSettings.get(i).getCompanyId() == id){
                return i;
            }

        }
        return -1;
    }

    public void addSettings(Context context, MoaziCompanyNotifications company) {
        List<MoaziCompanyNotifications> companyNotifications = getSettings(context);
        if (companyNotifications == null)
            companyNotifications = new ArrayList<MoaziCompanyNotifications>();

        for (int i=0; i<companyNotifications.size(); i++){

            if(companyNotifications.get(i).equals(company)){
                Log.d("Already","exists");
                return;
            }
            /*if(favorites.get(i).getCompanyId() == company.getCompanyId()){
                Log.d("Already","exists");
                return;
            }*/
        }
        Log.d("before","add");
        companyNotifications.add(company);
        saveSettings(context, companyNotifications);
    }

    public void removeSettings(Context context, MoaziCompanyNotifications company) {
        ArrayList<MoaziCompanyNotifications> notificationsSettings = getSettings(context);
        if (notificationsSettings != null) {
            notificationsSettings.remove(company);
            saveSettings(context, notificationsSettings);
        }
    }

    public ArrayList<MoaziCompanyNotifications> getSettings(Context context) {
        SharedPreferences settings;
        List<MoaziCompanyNotifications> companyNotifications;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(NOTIFICATION_SETTINGS)) {
            String jsonSettings = settings.getString(NOTIFICATION_SETTINGS, null);
            Gson gson = new Gson();
            MoaziCompanyNotifications[] settingsItems = gson.fromJson(jsonSettings,
                    MoaziCompanyNotifications[].class);

            companyNotifications = Arrays.asList(settingsItems);
            companyNotifications = new ArrayList<MoaziCompanyNotifications>(companyNotifications);
        } else
            return null;

        return (ArrayList<MoaziCompanyNotifications>) companyNotifications;
    }

}
