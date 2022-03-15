package com.example.arcmessenger.model;

public class UserDetail {
    private String userID;
    private String userName;
    private String userPhone;
    private String imgProfile;
    private String imgCover;
    private String status;
    private String bio;

    public UserDetail() {
    }

    public UserDetail(String userID, String userName, String userPhone, String imgProfile, String imgCover, String status, String bio) {
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.imgProfile = imgProfile;
        this.imgCover = imgCover;
        this.status = status;
        this.bio = bio;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getImgCover() {
        return imgCover;
    }

    public void setImgCover(String imgCover) {
        this.imgCover = imgCover;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
