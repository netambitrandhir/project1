package com.sanganan.app.model;

/**
 * Created by pranav on 30/11/16.
 */

public class CalloutData {

    String ID;
    String SenderID;
    String DateSent;
    String Description;
    String UserID;
    String FirstName;
    String FlatNbr;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSenderID() {
        return SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID;
    }

    public String getDateSent() {
        return DateSent;
    }

    public void setDateSent(String dateSent) {
        DateSent = dateSent;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getFlatNbr() {
        return FlatNbr;
    }

    public void setFlatNbr(String flatNbr) {
        FlatNbr = flatNbr;
    }
}
