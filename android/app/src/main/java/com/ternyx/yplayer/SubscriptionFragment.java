package com.ternyx.yplayer;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ternyx.yplayer.adapters.VideoAdapter;
import com.ternyx.yplayer.data.db.model.Video;
import com.ternyx.yplayer.data.net.SubscriptionRepo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SubscriptionFragment extends Fragment {
    private static final String TAG = "SubscriptionFragment";

    private SubscriptionRepo subscriptionRepo;
    private LiveData<List<Video>> subscriptionVideos;
    private MutableLiveData<Integer> activeIndex;

    public static SubscriptionFragment newInstance() {
        SubscriptionFragment fragment = new SubscriptionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subscriptionRepo = ((App)getActivity().getApplication()).appContainer.subRepo;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscription, container, false);

        RecyclerView rvVideos = view.findViewById(R.id.rv_videos);
        this.subscriptionVideos = subscriptionRepo.getVideos();
        this.activeIndex = subscriptionRepo.getActiveVideoIndex();

        VideoAdapter videoAdapter = new VideoAdapter(subscriptionVideos.getValue(), view.getContext(), activeIndex, rvVideos);
        rvVideos.setAdapter(videoAdapter);

        subscriptionVideos.observe(this, (videos) -> {
            videoAdapter.setVideos(videos);
            videoAdapter.notifyDataSetChanged();
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        rvVideos.setLayoutManager(linearLayoutManager);

        return view;
    }
}