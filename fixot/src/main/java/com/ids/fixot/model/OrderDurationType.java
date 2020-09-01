package com.ids.fixot.model;

import com.ids.fixot.MyApplication;

/**
 * Created by Amal on 4/3/2017.
 */

public class OrderDurationType {

    private String descriptionAr, descriptionEn;
    private int ID;

    public OrderDurationType() {
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        if (MyApplication.lang == MyApplication.ARABIC) {
            return getDescriptionAr();
        } else {
            return getDescriptionEn();
        }
    }
}
