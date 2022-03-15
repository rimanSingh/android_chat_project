package com.example.arcmessenger.manageChatAndImage.interfaces;

import com.example.arcmessenger.model.chat.Chat;

import java.util.List;

public interface OnReadChatCallback {
    void onSuccess(List<Chat> list);
    void onFailed();
}
