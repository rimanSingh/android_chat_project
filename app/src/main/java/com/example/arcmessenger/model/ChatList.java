package com.example.arcmessenger.model;

public class ChatList {
    private String userID;
    private String userName;
    private String userMessage;
    private String userDate;
    private String urlImage;

    public ChatList() {
    }

    public ChatList(String userID, String userName, String userMessage, String userDate, String urlImage) {
        this.userID = userID;
        this.userName = userName;
        this.userMessage = userMessage;
        this.userDate = userDate;
        this.urlImage = urlImage;
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

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getUserDate() {
        return userDate;
    }

    public void setUserDate(String userDate) {
        this.userDate = userDate;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }
}
