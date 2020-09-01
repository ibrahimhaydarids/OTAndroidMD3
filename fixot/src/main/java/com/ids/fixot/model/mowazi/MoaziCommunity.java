package com.ids.fixot.model.mowazi;

/**
 * Created by dev on 6/14/2016.
 */

public class MoaziCommunity {
    private int communityID, companyID,adminID, pictureID, communityTypeID;
    private String descriptionEn,descriptionAr, communityDate, creationDate;
    private MoaziCompany company;

    public MoaziCommunity(){

    }

    public int getCommunityID() {
        return communityID;
    }

    public void setCommunityID(int communityID) {
        this.communityID = communityID;
    }

    public int getCompanyID() {
        return companyID;
    }

    public void setCompanyID(int companyID) {
        this.companyID = companyID;
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public int getPictureID() {
        return pictureID;
    }

    public void setPictureID(int pictureID) {
        this.pictureID = pictureID;
    }

    public int getCommunityTypeID() {
        return communityTypeID;
    }

    public void setCommunityTypeID(int communityTypeID) {
        this.communityTypeID = communityTypeID;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionAr() {
        return descriptionAr;
    }

    public void setDescriptionAr(String descriptionAr) {
        this.descriptionAr = descriptionAr;
    }

    public String getCommunityDate() {
        return communityDate;
    }

    public void setCommunityDate(String communityDate) {
        this.communityDate = communityDate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public MoaziCompany getCompany() {
        return company;
    }

    public void setCompany(MoaziCompany company) {
        this.company = company;
    }
}
