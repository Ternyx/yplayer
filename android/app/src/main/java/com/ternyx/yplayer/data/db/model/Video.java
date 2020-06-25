package com.ternyx.yplayer.data.db.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

@Entity
public class Video {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "video_id")
    private String videoId;

    @ColumnInfo(name = "video_published_at")
    private Date videoPublishedAt;

    @ColumnInfo(name = "channel_id")
    private String channelId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "video_category")
    private Integer videoCategory;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return videoId.equals(video.videoId) &&
                Objects.equals(videoPublishedAt, video.videoPublishedAt) &&
                Objects.equals(channelId, video.channelId) &&
                Objects.equals(title, video.title) &&
                Objects.equals(videoCategory, video.videoCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, videoPublishedAt, channelId, title, videoCategory);
    }

    @NonNull
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(@NonNull String videoId) {
        this.videoId = videoId;
    }

    public Date getVideoPublishedAt() {
        return videoPublishedAt;
    }

    public void setVideoPublishedAt(Date videoPublishedAt) {
        this.videoPublishedAt = videoPublishedAt;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getVideoCategory() {
        return videoCategory;
    }

    public void setVideoCategory(Integer videoCategory) {
        this.videoCategory = videoCategory;
    }

    public Video() {

    }

    public Video(@NonNull String videoId, Date videoPublishedAt, String channelId, String title, Integer videoCategory) {
        this.videoId = videoId;
        this.videoPublishedAt = videoPublishedAt;
        this.channelId = channelId;
        this.title = title;
        this.videoCategory = videoCategory;
    }

    public Video(@NonNull String videoId, Date videoPublishedAt) {
        this.videoId = videoId;
        this.videoPublishedAt = videoPublishedAt;
    }
}
