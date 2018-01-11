package com.sanganan.app.model;

import java.util.ArrayList;

/**
 * Created by root on 12/10/16.
 */

public class GalleryParent {

    String date;
    ArrayList<GalleryData> dataArrayList;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<GalleryData> getDataArrayList() {
        return dataArrayList;
    }

    public void setDataArrayList(ArrayList<GalleryData> dataArrayList) {
        this.dataArrayList = dataArrayList;
    }


}
