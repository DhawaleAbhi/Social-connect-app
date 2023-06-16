package com.datingapp.models;

import java.io.Serializable;

public class ChatModel implements Serializable {
    boolean userId;
    String msg;
    String date;

    public ChatModel(boolean userId, String msg, String date) {
        this.userId = userId;
        this.msg = msg;
        this.date = date;
    }

    public boolean isUserId() {
        return userId;
    }

    public void setUserId(boolean userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
