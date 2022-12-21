package com.example.tdtuapp.Home.ChatPreview;

public class ChatPreview {
    private String avatar, name, lastMessage, time, chatKey, username;
    private boolean isSeen, isOnline;

    public ChatPreview(String avatar, String name, String lastMessage, String time, String chatKey, boolean isSeen, boolean isOnline) {
        this.avatar = avatar;
        this.name = name;
        this.lastMessage = lastMessage;
        this.time = time;
        this.chatKey = chatKey;
        this.isSeen = isSeen;
        this.isOnline = isOnline;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChatKey() {
        return chatKey;
    }

    public void setChatKey(String chatKey) {
        this.chatKey = chatKey;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }
}
