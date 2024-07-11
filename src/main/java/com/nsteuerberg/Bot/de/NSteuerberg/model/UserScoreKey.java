package com.nsteuerberg.Bot.de.NSteuerberg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class UserScoreKey implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "server_id")
    private String serverId;

    public UserScoreKey() {}

    public UserScoreKey(String userId, String serverId) {
        this.userId = userId;
        this.serverId = serverId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
}
