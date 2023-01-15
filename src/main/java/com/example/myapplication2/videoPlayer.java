package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.widget.MediaController;
import android.widget.VideoView;
import android.os.Bundle;

public class videoPlayer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        VideoView videoView = findViewById(R.id.videoView);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        videoView.setVideoPath("android.resource://"+getPackageName()+"/"+ R.raw.play);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.start();
//        videoView.setMediaController(mediaController);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });

    }

}