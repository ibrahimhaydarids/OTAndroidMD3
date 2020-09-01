package com.ids.fixot.model.mowazi;

/**
 * Created by DEV on 3/28/2018.
 */

public class MowaziCategory {

    private String name;
    private int image, id;

    public MowaziCategory(int id, String name, int image) {
        this.id = id;
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
