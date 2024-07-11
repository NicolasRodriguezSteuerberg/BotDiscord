package com.nsteuerberg.Bot.de.NSteuerberg.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_scores")
public class UserScore {
    @EmbeddedId
    private UserScoreKey userScoreKey;
    private int score;

    // constructores
    public UserScore() {}

    /**
     * Constructor
     * @param userScoreKey UserScoreKey: clave compuesta por el id del usuario y el id del servidor
     */
    public UserScore(UserScoreKey userScoreKey) {
        this.userScoreKey = userScoreKey;
        this.score = 0;
    }

    public UserScore(UserScoreKey userScoreKey, int score) {
        this.userScoreKey = userScoreKey;
        this.score = score;
    }

    public void setUserId(UserScoreKey userScoreKey) {
        this.userScoreKey = userScoreKey;
    }

    public String getUserId() {
        return userScoreKey.getUserId();
    }

    public String getServerId(){
        return userScoreKey.getServerId();
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
