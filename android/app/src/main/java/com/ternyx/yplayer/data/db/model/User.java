package com.ternyx.yplayer.data.db.model;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class User {
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    @NonNull
    private int userId;

    @ColumnInfo(name = "google_id")
    private String googleId;

    @ColumnInfo(name = "channel_id")
    private String channelId;

    @ColumnInfo(name = "session_token")
    private String sessionToken;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", googleId='" + googleId + '\'' +
                ", channelId='" + channelId + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                '}';
    }

    public User(int userId, String googleId, String channelId, String sessionToken) {
        this.userId = userId;
        this.googleId = googleId;
        this.channelId = channelId;
        this.sessionToken = sessionToken;
    }

    public User() { }
}
