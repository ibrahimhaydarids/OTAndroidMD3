package com.ids.fixot.model.mowazi;

/**
 * Created by DEV on 3/29/2018.
 */

public class MowaziOrderType {

    private int orderTypeId;
    private String nameEn, nameAr;

    public MowaziOrderType() {

    }

    public int getOrderTypeId() {
        return orderTypeId;
    }

    public void setOrderTypeId(int orderTypeId) {
        this.orderTypeId = orderTypeId;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }

    public String getNameAr() {
        return nameAr;
    }

    public void setNameAr(String nameAr) {
        this.nameAr = nameAr;
    }
}
