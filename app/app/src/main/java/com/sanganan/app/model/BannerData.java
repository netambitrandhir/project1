package com.sanganan.app.model;

/**
 * Created by pranav on 16/2/17.
 */

public class BannerData {

    String ID;
    String RWAID;
    String BannerImageUrl;
    String BannerImageSmallUrl;
    String DateUploded;
    String StartDate;
    String EndDate;
    String ExternalLink;
    String Addedby;
    String IsActive;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRWAID() {
        return RWAID;
    }

    public void setRWAID(String RWAID) {
        this.RWAID = RWAID;
    }

    public String getBannerImageUrl() {
        return BannerImageUrl;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        BannerImageUrl = bannerImageUrl;
    }

    public String getBannerImageSmallUrl() {
        return BannerImageSmallUrl;
    }

    public void setBannerImageSmallUrl(String bannerImageSmallUrl) {
        BannerImageSmallUrl = bannerImageSmallUrl;
    }

    public String getDateUploded() {
        return DateUploded;
    }

    public void setDateUploded(String dateUploded) {
        DateUploded = dateUploded;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getExternalLink() {
        return ExternalLink;
    }

    public void setExternalLink(String externalLink) {
        ExternalLink = externalLink;
    }

    public String getAddedby() {
        return Addedby;
    }

    public void setAddedby(String addedby) {
        Addedby = addedby;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }
}
