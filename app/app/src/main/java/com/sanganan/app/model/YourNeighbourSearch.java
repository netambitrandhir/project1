package com.sanganan.app.model;

/**
 * Created by raj on 8/9/2016.
 */
public class YourNeighbourSearch {

    String ID;
    String PhoneNbr;
    String EmailID;
    String Password;
    String FlatNbr;
    String FirstName;
    String MiddleName;
    String LastName;
    String ProfilePic;
    String Gender;
    String Occupation;
    String AddedOn;
    String VehicleNbr;
    String IsActive;
    String IsPhonePublic;


    public String getIsPhonePublic() {
        return IsPhonePublic;
    }

    public void setIsPhonePublic(String isPhonePublic) {
        IsPhonePublic = isPhonePublic;
    }

    public String getVehicleNbr() {
        return VehicleNbr;
    }

    public void setVehicleNbr(String vehicleNbr) {
        VehicleNbr = vehicleNbr;
    }


    public String getFlatNbr() {
        return FlatNbr;
    }

    public void setFlatNbr(String flatNbr) {
        FlatNbr = flatNbr;
    }


    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
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
        if (occupation.equalsIgnoreCase("null") || occupation.equalsIgnoreCase("(null)")) {
            Occupation = "";
        } else {
            Occupation = occupation;
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


}