package com.ternyx.yplayer.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ternyx.yplayer.PlayerActivity;
import com.ternyx.yplayer.R;
import com.ternyx.yplayer.data.db.model.Video;

import org.w3c.dom.Text;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {

    private List<Video> videos;
    private Context context;
    private MutableLiveData<Integer> index;
    private RecyclerView rvView;

    public VideoAdapter(List<Video> videos, Context context, MutableLiveData<Integer> index, RecyclerView view) {
        this.videos = videos;
        this.context = context;
        this.index = index;
        this.rvView = view;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View videoView = inflater.inflate(R.layout.item_video, parent, false);
        videoView.setOnClickListener(v -> {
            int pos = rvView.getChildLayoutPosition(v);
            index.postValue(pos);
            Intent intent = new Intent(v.getContext(), PlayerActivity.class);
            intent.putExtra("index", pos);
            v.getContext().startActivity(intent);
        });

        ViewHolder viewHolder = new ViewHolder(videoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = videos.get(position);
        TextView videoTitle = holder.title;
        ImageView thumbnail = holder.thumbnail;

        videoTitle.setText(video.getTitle());
        Glide.with(context)
                .load("https://img.youtube.com/vi/" + video.getVideoId() + "/0.jpg")
                .override(180, 135)
                .into(thumbnail);
    }

    @Override
    public int getItemCount() {
        if (videos != null) {
            return videos.size();
        }
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumbnail;
        public TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.video_thumbnail);
            title = itemView.findViewById(R.id.video_title);
        }
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }
}
