package com.ternyx.yplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.net.Uri;
import android.os.Bundle;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.YoutubeException;
import com.github.kiulian.downloader.model.YoutubeVideo;
import com.github.kiulian.downloader.model.formats.Format;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.ternyx.yplayer.data.db.model.Video;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PlayerActivity extends AppCompatActivity {
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private DataSource.Factory dataSourceFactory;
    private YoutubeDownloader downloader;
    private ConcatenatingMediaSource concatenatingMediaSource;

    private LiveData<List<Video>> videos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        downloader = new YoutubeDownloader();
        this.dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.64 Safari/537.36");
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        concatenatingMediaSource = new ConcatenatingMediaSource();
        player.prepare(concatenatingMediaSource);
        player.setPlayWhenReady(true);
        playerView = findViewById(R.id.video_view);
        playerView.setPlayer(player);
        videos = ((App) getApplication()).appContainer.subRepo.getVideos();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player.isPlaying()) {
            player.stop(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        int index = getIntent().getIntExtra("index", -1);
        if (index == -1) {
            return;
        }
        Video video = videos.getValue().get(index);
        try {
            CompletableFuture<YoutubeVideo> ytVid = CompletableFuture.supplyAsync(() -> {
                try {
                    return downloader.getVideo(video.getVideoId());
                } catch (YoutubeException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });

            YoutubeVideo ytVideo = ytVid.get();

            List<Format> formats = ytVideo.formats().stream()
                    .filter(f -> f.itag().id() < 132)
                    .sorted((a, b) -> b.itag().videoQuality().compareTo(a.itag().videoQuality()))
                    .collect(Collectors.toList());
            if (formats.size() <= 0) {
                return;
            }
            String url = formats.get(0).url();

            concatenatingMediaSource.addMediaSource(new ProgressiveMediaSource.Factory(dataSourceFactory)
                .setTag(video)
                .createMediaSource(Uri.parse(url)));
            player.setPlayWhenReady(true);

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}