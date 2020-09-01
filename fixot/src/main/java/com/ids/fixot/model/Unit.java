package com.ids.fixot.model;

/**
 * Created by DEV on 3/5/2018.
 */

public class Unit {

    private Double FromPrice, PriceUnit, ToPrice;
    private int QuantityUnit;

    public Unit() {
    }

    public Double getFromPrice() {
        return FromPrice;
    }

    public void setFromPrice(Double FromPrice) {
        this.FromPrice = FromPrice;
    }

    public Double getToPrice() {
        return ToPrice;
    }

    public void setToPrice(Double ToPrice) {
        this.ToPrice = ToPrice;
    }

    public Double getPriceUnit() {
        return PriceUnit;
    }

    public void setPriceUnit(Double PriceUnit) {
        this.PriceUnit = PriceUnit;
    }

    public int getQuantityUnit() {
        return QuantityUnit;
    }

    public void setQuantityUnit(int QuantityUnit) {
        this.QuantityUnit = QuantityUnit;
    }

}
