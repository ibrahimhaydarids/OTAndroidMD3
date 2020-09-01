package com.ids.fixot.model.mowazi;

public class MoaziAssembly {
    private int CommunityId;
    private String DescriptionEn;
    private String DescriptionAr;
    private int companyId;
    private String CommunityDate;
    private int communitytypeId;
    private String creationDate;
    private MoaziCompany company=new MoaziCompany();

    public int getCommunityId() {
        return CommunityId;
    }

    public String getDescriptionEn() {
        return DescriptionEn;
    }

    public String getDescriptionAr() {
        return DescriptionAr;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getCommunityDate() {
        return CommunityDate;
    }

    public int getCommunitytypeId() {
        return communitytypeId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public MoaziCompany getCompany() {
        return company;
    }

    public void setCommunityId(int communityId) {
        CommunityId = communityId;
    }

    public void setDescriptionEn(String descriptionEn) {
        DescriptionEn = descriptionEn;
    }

    public void setDescriptionAr(String descriptionAr) {
        DescriptionAr = descriptionAr;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public void setCommunityDate(String communityDate) {
        CommunityDate = communityDate;
    }

    public void setCommunitytypeId(int communitytypeId) {
        this.communitytypeId = communitytypeId;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setCompany(MoaziCompany company) {
        this.company = company;
    }

    public MoaziAssembly() {
    }
}