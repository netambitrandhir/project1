package com.sanganan.app.model;

/**
 * Created by root on 7/3/17.
 */

public class TabData {
    private String tabData;
    private boolean isTabActive;
    private int tabImage;
    private String tabName;

    public String getTabName() {
        return tabName;
    }

    public void setTabName(String tabName) {
        this.tabName = tabName;
    }


    public int getTabImage() {
        return tabImage;
    }

    public void setTabImage(int tabImage) {
        this.tabImage = tabImage;
    }

    public boolean isTabActive() {
        return isTabActive;
    }

    public void setTabActive(boolean tabActive) {
        isTabActive = tabActive;
    }


    public String getTabData() {
        return tabData;
    }

    public void setTabData(String tabData) {
        this.tabData = tabData;
    }

    public boolean getIsTabActive() {
        return isTabActive;
    }

    public void setIsTabActive(boolean isTabActive) {
        this.isTabActive = isTabActive;
    }
}
