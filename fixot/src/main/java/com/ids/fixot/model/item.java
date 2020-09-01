package com.ids.fixot.model;


public class item {


    private String key, value;

    public item() {
    }

    public item(String Key, String Value) {
        this.key = Key;
        this.value = Value;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String Key) {
        this.key = Key;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String Value) {
        this.value = Value;
    }

}
