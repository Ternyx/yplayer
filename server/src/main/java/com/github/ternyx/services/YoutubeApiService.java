package com.github.ternyx.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.ternyx.models.SubscriptionSettings;
import com.github.ternyx.models.Video;
import com.github.ternyx.utils.YoutubeApiTemplateCustomizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * YoutubeApiService
 */
@Service
public class YoutubeApiService {
    @Autowired
    UserManager userManager;

    private final String YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3";
    private RestTemplate restTemplate;

    @Autowired
    public YoutubeApiService(UserManager userManager) {
        this.userManager = userManager;

        restTemplate = new RestTemplateBuilder(new YoutubeApiTemplateCustomizer(userManager))
            .rootUri(YOUTUBE_BASE_URL)
            .build();
    }

    public List<Video> getSubscriptionVideos(SubscriptionSettings settings) {
        List<String> channelIds = getUserSubscriptionChannels();
        List<String> userUploadPlaylistIds = channelIdsToUserUploadPlaylists(channelIds);
        List<Video> subscriptionVideos = getSubscriptionVideos(userUploadPlaylistIds, settings);

        return subscriptionVideos;
    }

    public List<String> getUserSubscriptionChannels() {
        final String primaryUrl =
                "/subscriptions?part=snippet&fields=nextPageToken,items/snippet/resourceId/channelId&mine=true&maxResults=50";

        List<String> channelIds = new ArrayList<>();
        String nextPageToken = null;

        do {
            String url = (nextPageToken == null) ? primaryUrl :
                primaryUrl + "&pageToken=" + nextPageToken; 

            JsonNode rootNode = restTemplate.getForObject(url, JsonNode.class);

            JsonNode nextPageTokenNode = rootNode.get("nextPageToken");
            nextPageToken = (nextPageTokenNode == null) ? null : nextPageTokenNode.asText();

            JsonNode itemsArray = rootNode.get("items");
            for (int i = 0; i < itemsArray.size(); i++) {
                channelIds.add(itemsArray
                        .get(i)
                        .get("snippet")
                        .get("resourceId")
                        .get("channelId")
                        .asText());
            }

        } while (nextPageToken != null);

        return channelIds;
    }

    private List<String> channelIdsToUserUploadPlaylists(List<String> channelIds) {
        return channelIds.stream()
            .map(c -> "UU" + c.substring(2))
            .collect(Collectors.toList());
    }

    private List<Video> getSubscriptionVideos(List<String> userUploadPlaylistIds, SubscriptionSettings settings) {
        return userUploadPlaylistIds.stream()
            // maybe divide requests across threads? 
            .map(uuId -> CompletableFuture.supplyAsync(() -> getUserUploadVideos(uuId, settings), Executors.newSingleThreadExecutor()))
            .map(CompletableFuture::join)
            // user uploads are returned as sorted lists; min heap to merge & sort?
            .flatMap(videoList -> videoList.stream())
            .sorted((a, b) -> b.getVideoPublishedAt().compareTo(a.getVideoPublishedAt())) // naive solution
            .collect(Collectors.toList());
    }

    private List<Video> getUserUploadVideos(String userUploadPlaylistId, SubscriptionSettings settings) {
        final int MAX_VIDEOS = 50; // arbitrary limit to stay somewhat within api cost quota 

        final String primaryUrl = "/playlistItems?part=contentDetails,snippet" 
            + "&fields=nextPageToken,items(contentDetails(videoId,videoPublishedAt),snippet/title)"
            + "&maxResults=50"
            + "&playlistId=" + userUploadPlaylistId;

        List<Video> targetVideos = new ArrayList<>();

        String nextPageToken = null;
        int fetchedVideoCount = 0;

        boolean hasReachedStart = false;
        boolean hasReachedEnd = false;

        do {
            String url = (nextPageToken == null) ? primaryUrl :
                primaryUrl + "&pageToken=" + nextPageToken;

            JsonNode rootNode = restTemplate.getForObject(url, JsonNode.class);

            JsonNode nextPageTokenNode = rootNode.get("nextPageToken");
            nextPageToken = (nextPageTokenNode == null) ? null : nextPageTokenNode.asText(); 
            JsonNode itemsArray = rootNode.get("items");
            int i;

            for (i = 0; i < itemsArray.size(); i++) {
                JsonNode contentDetails = itemsArray.get(i).get("contentDetails");

                Instant videoPublishedAt =
                        Instant.parse(contentDetails.get("videoPublishedAt").asText());

                hasReachedStart = videoPublishedAt.compareTo(settings.getStartDate()) <= 0;
                hasReachedEnd = videoPublishedAt.compareTo(settings.getEndDate()) <= 0;

                if (hasReachedStart && hasReachedEnd) {  
                    return targetVideos; // user uploads are sorted, no need to check other items
                }
                
                if (!hasReachedStart) {
                    continue;
                }

                JsonNode snippet = itemsArray.get(i).get("snippet");

                String title = snippet.get("title").asText();
                String videoId = contentDetails.get("videoId").asText();
                String channelId = "UC" + userUploadPlaylistId.substring(2);

                targetVideos.add(new Video(videoId, videoPublishedAt, channelId, title));
            }
            fetchedVideoCount += i;

        } while (nextPageToken != null && fetchedVideoCount < MAX_VIDEOS);

        return targetVideos;
    }
}
