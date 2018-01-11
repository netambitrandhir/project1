package com.sanganan.app.model;

/**
 * Created by root on 5/10/16.
 */

public class ApprovedMember {

    String typeLiving;
    String status;
    String Id;
    String PhoneNbr;
    String EmailID;
    String FlatNbr;
    String ApprovalStatus;
    String ApprovedBy;
    String ApprovedOn;
    String FirstName;
    String MiddleName;
    String LastName;
    String ProfilePic;
    String Gender;
    String Occupation;
    String AddedOn;
    String IsActive;

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    String Password;


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getPhoneNbr() {
        return PhoneNbr;
    }

    public void setPhoneNbr(String phoneNbr) {
        PhoneNbr = phoneNbr;
    }

    public String getEmailID() {
        return EmailID;
    }

    public void setEmailID(String emailID) {
        EmailID = emailID;
    }

    public String getFlatNbr() {
        return FlatNbr;
    }

    public void setFlatNbr(String flatNbr) {
        if(!flatNbr.equalsIgnoreCase("null")) {
            FlatNbr = flatNbr;
        }else{
            FlatNbr = "";
        }
    }

    public String getApprovalStatus() {
        return ApprovalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        ApprovalStatus = approvalStatus;
    }

    public String getApprovedBy() {
        return ApprovedBy;
    }

    public void setApprovedBy(String approvedBy) {
        ApprovedBy = approvedBy;
    }

    public String getApprovedOn() {
        return ApprovedOn;
    }

    public void setApprovedOn(String approvedOn) {
        ApprovedOn = approvedOn;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {

        if(!firstName.equalsIgnoreCase("null")) {
            FirstName = firstName;
        }else{
            FirstName = "";
        }
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getOccupation() {
        return Occupation;
    }

    public void setOccupation(String occupation) {

        if(!occupation.equalsIgnoreCase("null")) {
            Occupation = occupation;
        }else{
            Occupation = "";
        }


    }

    public String getAddedOn() {
        return AddedOn;
    }

    public void setAddedOn(String addedOn) {
        AddedOn = addedOn;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getTypeLiving() {
        return typeLiving;
    }

    public void setTypeLiving(String typeLiving) {
        this.typeLiving = typeLiving;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


}
