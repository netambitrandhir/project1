package com.sanganan.app.model;

/**
 * Created by root on 30/5/17.
 */

public class Comment {

    String SocietyFeedID;
    String ResidentRWAID;
    String PostFeedCommnet;
    String CommentBy;
    String FlatNbr;
    String CreatDate;
    String IsActive;
    String CommentProfilePic;

    public String getSocietyFeedID() {
        return SocietyFeedID;
    }

    public void setSocietyFeedID(String societyFeedID) {
        SocietyFeedID = societyFeedID;
    }

    public String getResidentRWAID() {
        return ResidentRWAID;
    }

    public void setResidentRWAID(String residentRWAID) {
        ResidentRWAID = residentRWAID;
    }

    public String getPostFeedCommnet() {
        return PostFeedCommnet;
    }

    public void setPostFeedCommnet(String postFeedCommnet) {
        PostFeedCommnet = postFeedCommnet;
    }

    public String getCommentBy() {
        return CommentBy;
    }

    public void setCommentBy(String commentBy) {
        CommentBy = commentBy;
    }

    public String getFlatNbr() {
        return FlatNbr;
    }

    public void setFlatNbr(String flatNbr) {
        FlatNbr = flatNbr;
    }

    public String getCreatDate() {
        return CreatDate;
    }

    public void setCreatDate(String creatDate) {
        long timeCurrent = System.currentTimeMillis();
        long timeCommented = Long.parseLong(creatDate) * 1000L;

        long diference = timeCurrent - timeCommented;
        long day = diference / (1000 * 60 * 60 * 24);

        if (diference <= 0) {
            this.CreatDate = "just now";
        } else {

            if (day < 1) {
                long timeAgoHour = diference / (1000 * 60 * 60);
                if (timeAgoHour >= 1) {
                    this.CreatDate = String.valueOf(timeAgoHour) + " h ago";
                } else {
                    long timeAgoMin = diference / (1000 * 60);

                    this.CreatDate = String.valueOf(timeAgoMin) + " min ago";
                }
            } else {
                long week = day / 7;
                if (week < 1) {
                    this.CreatDate = String.valueOf(day) + " d ago";
                } else {
                    long month = day / 30;
                    if (month < 1) {
                        this.CreatDate = String.valueOf(week) + " w ago";
                    } else {
                        this.CreatDate = String.valueOf(month) + " m ago";
                    }
                }
            }
        }
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getCommentProfilePic() {
        return CommentProfilePic;
    }

    public void setCommentProfilePic(String commentProfilePic) {
        CommentProfilePic = commentProfilePic;
    }
}
