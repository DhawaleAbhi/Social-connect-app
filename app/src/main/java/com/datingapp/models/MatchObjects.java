package com.datingapp.models;

import java.io.Serializable;

public class MatchObjects implements Serializable {
    String userId;
    String name;
    String url;

    public MatchObjects(String userId, String name, String url) {
        this.userId = userId;
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
