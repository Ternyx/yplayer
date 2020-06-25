package com.ternyx.yplayer.data.net;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.ternyx.yplayer.App;
import com.ternyx.yplayer.R;
import com.ternyx.yplayer.data.db.model.Video;
import com.ternyx.yplayer.utils.SubscriptionSettings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SubscriptionRepo {
    private static final String TAG = "SubscriptionRepo";
    private OkHttpClient client;

    private MutableLiveData<List<Video>> subscriptionVideos = new MutableLiveData<>();
    private MutableLiveData<Integer> activeIndex = new MutableLiveData<>();
    private ObjectMapper jsonObjectMapper;

    public SubscriptionRepo(OkHttpClient client, ObjectMapper jsonObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.client = client;
    }

    public LiveData<List<Video>> getVideos() {
        if (subscriptionVideos.getValue() == null) {
            fetchVideos();
        }
        return subscriptionVideos;
    }

    public MutableLiveData<Integer> getActiveVideoIndex() {
        return activeIndex;
    }

    private void fetchVideos() {
        Request req = new Request.Builder()
                .url("https://placeholder/api/youtube/subscriptionVideos")
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // TODO proper error handling
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                List<Video> videos = jsonObjectMapper.readValue(response.body().string(), new TypeReference<List<Video>>() {});
                response.close();
                subscriptionVideos.postValue(videos);
            }
        });
    }


}
