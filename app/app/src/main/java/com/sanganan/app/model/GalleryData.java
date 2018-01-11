package com.sanganan.app.model;

/**
 * Created by root on 10/10/16.
 */

public class GalleryData {

    String PhotoPath;
    String PhotoCaption;
    String AddedBy;
    String FirstName;
    String FlatNbr;
    String LIKES;
    String IsLIKES;
    String PhotoId;
    String ResidentRWAID;




    public String getResidentRWAID() {
        return ResidentRWAID;
    }

    public void setResidentRWAID(String residentRWAID) {
        ResidentRWAID = residentRWAID;
    }

    public String getLIKES() {
        return LIKES;
    }

    public void setLIKES(String LIKES) {
        this.LIKES = LIKES;
    }

    public String getIsLIKES() {
        return IsLIKES;
    }

    public void setIsLIKES(String isLIKES) {
        IsLIKES = isLIKES;
    }

    public String getPhotoId() {
        return PhotoId;
    }

    public void setPhotoId(String photoId) {
        PhotoId = photoId;
    }



    public String getPhotoPath() {
        return PhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        PhotoPath = photoPath;
    }

    public String getPhotoCaption() {
        return PhotoCaption;
    }

    public void setPhotoCaption(String photoCaption) {
        PhotoCaption = photoCaption;
    }

    public String getAddedBy() {
        return AddedBy;
    }

    public void setAddedBy(String addedBy) {
        AddedBy = addedBy;
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
