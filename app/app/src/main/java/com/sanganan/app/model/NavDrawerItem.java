package com.sanganan.app.model;

/**
 * Created by root on 29/8/16.
 */
public class NavDrawerItem {


    public String title;
    public int image;

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }


    private String count = "0";
    // boolean to set visiblity of the counter
    private boolean isCounterVisible = false;

    public NavDrawerItem() {
    }

    public NavDrawerItem(String title) {
        this.title = title;

    }

    public NavDrawerItem(String title, boolean isCounterVisible, String count) {
        this.title = title;

        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }

    public NavDrawerItem(String titlr, int img) {
        this.title = titlr;
        this.image = img;
    }


    public String getTitle() {
        return this.title;
    }


    public String getCount() {
        return this.count;
    }

    public boolean getCounterVisibility() {
        return this.isCounterVisible;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setCount(String count) {
        this.count = count;
    }

    public void setCounterVisibility(boolean isCounterVisible) {
        this.isCounterVisible = isCounterVisible;
    }
}
