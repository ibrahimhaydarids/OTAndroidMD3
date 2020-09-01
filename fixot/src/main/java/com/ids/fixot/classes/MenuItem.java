package com.ids.fixot.classes;

/**
 * Created by DEV on 4/9/2018.
 */

public class MenuItem {

    private int id, imageId;
    private String name;

    public MenuItem() {

    }

    public MenuItem(int id, int imageId, String name) {
        this.id = id;
        this.imageId = imageId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
