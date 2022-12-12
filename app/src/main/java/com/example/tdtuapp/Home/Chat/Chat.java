package com.example.tdtuapp.Home.Chat;

public class Chat {
    private String sender, msg, time;

    public Chat(String sender, String msg, String time) {
        this.sender = sender;
        this.msg = msg;
        this.time = time;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
