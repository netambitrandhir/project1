package com.sanganan.app.model;

/**
 * Created by root on 20/10/16.
 */

public class UserComment {

    String userCommentId;
    String userId;
    String userName;
    String usrFlatNumber;
    String commentText;
    String ratingByUser;
    String ago;
    String userProfilePic;

    public String getUserCommentId() {
        return userCommentId;
    }

    public void setUserCommentId(String userCommentId) {
        this.userCommentId = userCommentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUsrFlatNumber() {
        return usrFlatNumber;
    }

    public void setUsrFlatNumber(String usrFlatNumber) {
        this.usrFlatNumber = usrFlatNumber;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getRatingByUser() {
        return ratingByUser;
    }

    public void setRatingByUser(String ratingByUser) {
        this.ratingByUser = ratingByUser;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        long timeCurrent = System.currentTimeMillis();
        long timeCommented = Long.parseLong(ago)*1000L;

        long diference = timeCurrent-timeCommented;
        long day = diference/(1000*60*60*24);

        if(day<1){
            this.ago = String.valueOf(diference/(1000*60*60))+"h ago";
        }
        else{
            long week = day/7;
            if(week<1){
                this.ago = String.valueOf(day)+"d ago";
            }
            else{
                long month = day/30;
                if(month<1){
                    this.ago = String.valueOf(week)+"w ago";
                }
                else{
                    this.ago = String.valueOf(month)+"m ago";
                }
            }
        }
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }
}
