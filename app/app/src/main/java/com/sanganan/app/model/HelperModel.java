package com.sanganan.app.model;

import java.util.ArrayList;

/**
 * Created by root on 28/9/16.
 */
public class HelperModel {
    String ID;
    String RWAID;
    String Name;
    String AddedbyName;
    String addedbyFlatNbr;
    String ServiceOffered;
    String ResidentialAddress;
    String PrimaryContactNbr;
    String PhoneNbr2;
    String EmailId;
    String AddedBy;
    String AddedOn;
    String ProfilePhoto;
    String PoilceVerificationScanImage1;
    String PoliceVerificationScanImage2;
    String PoliceVerificationScanImage3;
    String EntryCardExpiry;
    String IsActive;
    String helperId;
    String notifyStatusValue;
    String is_in;
    String subscribe;
    String helperQRCode;

    public String getHelperQRCode() {
        return helperQRCode;
    }

    public void setHelperQRCode(String helperQRCode) {
        this.helperQRCode = helperQRCode;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getIs_in() {
        return is_in;
    }

    public void setIs_in(String is_in) {
        this.is_in = is_in;
    }

    public String getNotifyStatusValue() {
        return notifyStatusValue;
    }

    public void setNotifyStatusValue(String notifyStatusValue) {
        this.notifyStatusValue = notifyStatusValue;
    }


    public String getAddedbyName() {
        return AddedbyName;
    }

    public void setAddedbyName(String addedbyName) {
        if (addedbyName.equalsIgnoreCase("null") || addedbyName.equalsIgnoreCase("(null)")) {
            AddedbyName = "";
        } else {
            AddedbyName = addedbyName;
        }
    }

    public String getAddedbyFlatNbr() {
        return addedbyFlatNbr;
    }

    public void setAddedbyFlatNbr(String addedbyFlatNbr) {
        this.addedbyFlatNbr = addedbyFlatNbr;
    }


    public String getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    String serviceCharge;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    String rating;

    ArrayList<UserComment> userCommentList = new ArrayList<>();

    public ArrayList<UserComment> getUserCommentList() {
        return userCommentList;
    }

    public void setUserCommentList(ArrayList<UserComment> userCommentList) {
        this.userCommentList = userCommentList;
    }


    public String getHelperId() {
        return helperId;
    }

    public void setHelperId(String helperId) {
        this.helperId = helperId;
    }

    public String getPrimaryContactNbr() {
        return PrimaryContactNbr;
    }

    public void setPrimaryContactNbr(String primaryContactNbr) {
        PrimaryContactNbr = primaryContactNbr;
    }

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

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getServiceOffered() {
        return ServiceOffered;
    }

    public void setServiceOffered(String serviceOffered) {
        ServiceOffered = serviceOffered;
    }

    public String getResidentialAddress() {
        return ResidentialAddress;
    }

    public void setResidentialAddress(String residentialAddress) {
        if (residentialAddress.equalsIgnoreCase("null") || residentialAddress.equalsIgnoreCase("(null)")) {
            ResidentialAddress = "";
        } else {
            ResidentialAddress = residentialAddress;
        }
    }

    public String getPhoneNbr2() {
        return PhoneNbr2;
    }

    public void setPhoneNbr2(String phoneNbr2) {
        PhoneNbr2 = phoneNbr2;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getAddedBy() {
        return AddedBy;
    }

    public void setAddedBy(String addedBy) {
        AddedBy = addedBy;
    }

    public String getAddedOn() {
        return AddedOn;
    }

    public void setAddedOn(String addedOn) {
        AddedOn = addedOn;
    }

    public String getProfilePhoto() {
        return ProfilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        ProfilePhoto = profilePhoto;
    }

    public String getPoilceVerificationScanImage1() {
        return PoilceVerificationScanImage1;
    }

    public void setPoilceVerificationScanImage1(String poilceVerificationScanImage1) {
        PoilceVerificationScanImage1 = poilceVerificationScanImage1;
    }

    public String getPoliceVerificationScanImage2() {
        return PoliceVerificationScanImage2;
    }

    public void setPoliceVerificationScanImage2(String policeVerificationScanImage2) {
        PoliceVerificationScanImage2 = policeVerificationScanImage2;
    }

    public String getPoliceVerificationScanImage3() {
        return PoliceVerificationScanImage3;
    }

    public void setPoliceVerificationScanImage3(String policeVerificationScanImage3) {
        PoliceVerificationScanImage3 = policeVerificationScanImage3;
    }

    public String getEntryCardExpiry() {
        return EntryCardExpiry;
    }

    public void setEntryCardExpiry(String entryCardExpiry) {
        EntryCardExpiry = entryCardExpiry;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }


}
