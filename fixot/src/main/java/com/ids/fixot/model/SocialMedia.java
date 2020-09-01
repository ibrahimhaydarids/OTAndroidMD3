package com.ids.fixot.model;

/**
 * Created by user on 4/3/2017.
 */

public class SocialMedia {

    private int type, socialMediaID;
    private String iconUrl, link;

    public SocialMedia() {

    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSocialMediaID() {
        return socialMediaID;
    }

    public void setSocialMediaID(int socialMediaID) {
        this.socialMediaID = socialMediaID;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
