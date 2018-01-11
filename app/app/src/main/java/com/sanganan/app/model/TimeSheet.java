package com.sanganan.app.model;

/**
 * Created by root on 23/3/17.
 */

public class TimeSheet {

    String ID;
    String time;
    String is_in;
    String image;
    String profession;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIs_in() {
        return is_in;
    }

    public void setIs_in(String is_in) {
        this.is_in = is_in;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}
