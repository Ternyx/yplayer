package com.github.ternyx.controllers;

import java.util.List;
import com.github.ternyx.models.SubscriptionSettings;
import com.github.ternyx.models.Video;
import com.github.ternyx.services.YoutubeApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * YoutubeApiController
 */
@RestController
@RequestMapping("/api/youtube")
public class YoutubeApiController {

    @Autowired
    YoutubeApiService youtubeApiService;

    @GetMapping("/subscriptionVideos")
    public List<Video> getSubscriptionVideos(SubscriptionSettings settings) {
        return youtubeApiService.getSubscriptionVideos(settings);
    }
}
