package com.github.ternyx.models;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Video
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Video {

    private String videoId;
    private Instant videoPublishedAt;
    private String channelId;
    private String title;

    public Video(String videoId, Instant videoPublishedAt) {
        this.videoId = videoId;
        this.videoPublishedAt = videoPublishedAt;
    }

    public Video(String videoId, Instant videoPublishedAt, String channelId) {
        this.videoId = videoId;
        this.videoPublishedAt = videoPublishedAt;
        this.channelId = channelId;
    }


}
