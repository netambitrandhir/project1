package com.sanganan.app.model;

import java.util.ArrayList;

/**
 * Created by Randhir Patel on 6/6/17.
 */

public class PostModel {

    private String ID;
    private String Description;
    private String FeedType;
    private String LikesCount;
    private String CommentCount;
    private String PostRefID;
    private String ResidentRwaID;
    private String RWAID;
    private String ResidentName;
    private String ResidentFlatNo;
    private String DateCreated;
    private String ProfilePic;
    private String IsLike;
    private String PhotoPath;
    private String UserId;
    private ArrayList<String> PhotoPathList;
    private boolean isbanner;
    private boolean isTabData;

    public boolean getIsTabData() {
        return isTabData;
    }

    public void setTabData(boolean tabData) {
        isTabData = tabData;
    }

    public boolean getIsbanner() {
        return isbanner;
    }

    public void setIsbanner(boolean isbanner) {
        this.isbanner = isbanner;
    }




    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public ArrayList<String> getPhotoPathList() {
        return PhotoPathList;
    }

    public void setPhotoPathList(ArrayList<String> photoPathList) {
        PhotoPathList = photoPathList;
    }

    public String getIsLike() {
        return IsLike;
    }

    public void setIsLike(String isLike) {
        IsLike = isLike;
    }

    public String getPhotoPath() {
        return PhotoPath;
    }

    public void setPhotoPath(String photoPath) {
        PhotoPath = photoPath;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getFeedType() {
        return FeedType;
    }

    public void setFeedType(String feedType) {
        FeedType = feedType;
    }

    public String getLikesCount() {
        return LikesCount;
    }

    public void setLikesCount(String likesCount) {
        LikesCount = likesCount;
    }

    public String getCommentCount() {
        return CommentCount;
    }

    public void setCommentCount(String commentCount) {
        CommentCount = commentCount;
    }

    public String getPostRefID() {
        return PostRefID;
    }

    public void setPostRefID(String postRefID) {
        PostRefID = postRefID;
    }

    public String getResidentRwaID() {
        return ResidentRwaID;
    }

    public void setResidentRwaID(String residentRwaID) {
        ResidentRwaID = residentRwaID;
    }

    public String getRWAID() {
        return RWAID;
    }

    public void setRWAID(String RWAID) {
        this.RWAID = RWAID;
    }

    public String getResidentName() {
        return ResidentName;
    }

    public void setResidentName(String residentName) {
        ResidentName = residentName;
    }

    public String getResidentFlatNo() {
        return ResidentFlatNo;
    }

    public void setResidentFlatNo(String residentFlatNo) {
        ResidentFlatNo = residentFlatNo;
    }

    public String getDateCreated() {
        return DateCreated;
    }

    public void setDateCreated(String dateCreated) {
        DateCreated = dateCreated;
    }
}
