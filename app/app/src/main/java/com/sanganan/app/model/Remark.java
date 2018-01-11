package com.sanganan.app.model;

/**
 * Created by pranav on 29/12/16.
 */

public class Remark {

    String ID;
    String Remark;
    String CompID;
    String RemarkDate;
    String EnteredBy;
    String name;
    String flat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getCompID() {
        return CompID;
    }

    public void setCompID(String compID) {
        CompID = compID;
    }

    public String getRemarkDate() {
        return RemarkDate;
    }

    public void setRemarkDate(String remarkDate) {
        RemarkDate = remarkDate;
    }

    public String getEnteredBy() {
        return EnteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        EnteredBy = enteredBy;
    }
}
