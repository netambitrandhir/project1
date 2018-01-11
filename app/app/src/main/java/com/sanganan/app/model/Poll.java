package com.sanganan.app.model;

/**
 * Created by pranav on 29/11/16.
 */

public class Poll {

    String Id;
    String ResidentRWAID;
    String Question;
    String DateAdded;
    String ExpirationDt;
    String IsActive;
    String FirstName;
    String FlatNbr;
    String ResponseID;

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    String UserID;

    public String getYesNbr() {
        return YesNbr;
    }

    public void setYesNbr(String yesNbr) {
        YesNbr = yesNbr;
    }

    public String getNoNbr() {
        return NoNbr;
    }

    public void setNoNbr(String noNbr) {
        NoNbr = noNbr;
    }

    String YesNbr;
    String NoNbr;


    public String getResponseID() {
        return ResponseID;
    }

    public void setResponseID(String responseID) {
        ResponseID = responseID;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getResidentRWAID() {
        return ResidentRWAID;
    }

    public void setResidentRWAID(String residentRWAID) {
        ResidentRWAID = residentRWAID;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getDateAdded() {
        return DateAdded;
    }

    public void setDateAdded(String dateAdded) {
        DateAdded = dateAdded;
    }

    public String getExpirationDt() {
        return ExpirationDt;
    }

    public void setExpirationDt(String expirationDt) {
        ExpirationDt = expirationDt;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
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
