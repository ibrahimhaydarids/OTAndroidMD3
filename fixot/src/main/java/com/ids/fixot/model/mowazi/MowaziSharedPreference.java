package com.ids.fixot.model.mowazi;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziSharedPreference {

    private static final String PREFS_NAME = "MOWAZI_APP";
    private static final String FAVORITES = "Company_Favorite";
    private static final String NOTIFICATION_SETTINGS = "Company_Notification";

    public MowaziSharedPreference() {
        super();
    }

    // This four methods are used for maintaining favorites.
    public void saveFavorites(Context context, List<MowaziCompany> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        for (MowaziCompany item : favorites) {
            Log.d("List item", item.toString());
        }

        editor.apply();
    }

    public boolean isFound(Context context, MowaziCompany company) {

        List<MowaziCompany> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<MowaziCompany>();
        for (int i = 0; i < favorites.size(); i++) {

            if (favorites.get(i).equals(company)) {
                return true;
            }

        }
        return false;
    }

    public void addFavorite(Context context, MowaziCompany company) {

        List<MowaziCompany> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<MowaziCompany>();

        for (int i = 0; i < favorites.size(); i++) {

            if (favorites.get(i).equals(company)) {
                Log.d("Already", "exists");
                return;
            }
            /*if(favorites.get(i).getCompanyId() == company.getCompanyId()){
                Log.d("Already","exists");
                return;
            }*/
        }
        Log.d("before", "add");
        favorites.add(company);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, MowaziCompany company) {
        ArrayList<MowaziCompany> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(company);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<MowaziCompany> getFavorites(Context context) {
        SharedPreferences settings;
        List<MowaziCompany> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            MowaziCompany[] favoriteItems = gson.fromJson(jsonFavorites,
                    MowaziCompany[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<MowaziCompany>(favorites);
        } else
            return null;

        return (ArrayList<MowaziCompany>) favorites;
    }


    public void saveSettings(Context context, List<MowaziCompanyNotifications> companyNotifications) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonSettings = gson.toJson(companyNotifications);

        editor.putString(NOTIFICATION_SETTINGS, jsonSettings);

        for (MowaziCompanyNotifications item : companyNotifications) {
            Log.d("List item", item.toString());
        }

        editor.commit();
    }

    public boolean isFoundSettings(Context context, MowaziCompanyNotifications companyNotification) {
        List<MowaziCompanyNotifications> notificationsSettings = getSettings(context);
        if (notificationsSettings == null)
            notificationsSettings = new ArrayList<MowaziCompanyNotifications>();
        for (int i = 0; i < notificationsSettings.size(); i++) {

            if (notificationsSettings.get(i).equals(companyNotification)) {
                return true;
            }

        }
        return false;
    }

    public int isFoundLaunchSettings(Context context, int id) {
        List<MowaziCompanyNotifications> notificationsSettings = getSettings(context);
        if (notificationsSettings == null)
            notificationsSettings = new ArrayList<MowaziCompanyNotifications>();
        else {
            Log.wtf("notificationsSettings ", "" + notificationsSettings.size());
        }
        for (int i = 0; i < notificationsSettings.size(); i++) {

            if (notificationsSettings.get(i).getCompanyId() == id) {
                return i;
            }

        }
        return -1;
    }

    public void addSettings(Context context, MowaziCompanyNotifications company) {
        List<MowaziCompanyNotifications> companyNotifications = getSettings(context);
        if (companyNotifications == null)
            companyNotifications = new ArrayList<MowaziCompanyNotifications>();

        for (int i = 0; i < companyNotifications.size(); i++) {

            if (companyNotifications.get(i).equals(company)) {
                Log.d("Already", "exists");
                return;
            }
            /*if(favorites.get(i).getCompanyId() == company.getCompanyId()){
                Log.d("Already","exists");
                return;
            }*/
        }
        Log.d("before", "add");
        companyNotifications.add(company);
        saveSettings(context, companyNotifications);
    }

    public void removeSettings(Context context, MowaziCompanyNotifications company) {
        ArrayList<MowaziCompanyNotifications> notificationsSettings = getSettings(context);
        if (notificationsSettings != null) {
            notificationsSettings.remove(company);
            saveSettings(context, notificationsSettings);
        }
    }

    public ArrayList<MowaziCompanyNotifications> getSettings(Context context) {
        SharedPreferences settings;
        List<MowaziCompanyNotifications> companyNotifications;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(NOTIFICATION_SETTINGS)) {
            String jsonSettings = settings.getString(NOTIFICATION_SETTINGS, null);
            Gson gson = new Gson();
            MowaziCompanyNotifications[] settingsItems = gson.fromJson(jsonSettings,
                    MowaziCompanyNotifications[].class);

            companyNotifications = Arrays.asList(settingsItems);
            companyNotifications = new ArrayList<MowaziCompanyNotifications>(companyNotifications);
        } else
            return null;

        return (ArrayList<MowaziCompanyNotifications>) companyNotifications;
    }

}
