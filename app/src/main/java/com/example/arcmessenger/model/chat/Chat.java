package com.example.arcmessenger.model.chat;

public class Chat {
    private String dateTime;
    private String textMessage;
    private String url;
    private String messageType;
    private String sender;
    private String receiver;

    public Chat() {
    }

    public Chat(String dateTime, String textMessage, String url, String messageType, String sender, String receiver) {
        this.dateTime = dateTime;
        this.textMessage = textMessage;
        this.url = url;
        this.messageType = messageType;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
