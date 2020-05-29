package com.shyam.videoplayerdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {
    VideoView videoView;
    Button button;
    ProgressDialog progressDialog;
    private static final String videoLink = "https://developers.google.com/training/images/tacoma_narrows.mp4";
//        private static final String videoLink = "http://www.youtube.com/watch?v=Iuo9PpGE04Y&list=PLLYz8uHU480j37APNXBdPz7YzAi4XlQUF";
    private int currentPosition = 0;
    public static final String PLAYBACK_TIME = "play_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressDialog = new ProgressDialog(MainActivity.this);
        button = findViewById(R.id.btn);
        videoView = findViewById(R.id.videoView);
        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(PLAYBACK_TIME);
        }
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ActivityWebView.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializePlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PLAYBACK_TIME, videoView.getCurrentPosition());
    }

    private void releasePlayer() {
        videoView.stopPlayback();
    }

    private void initializePlayer() {
        progressDialog.setMessage("buffering");
        progressDialog.show();
        Uri videoUri = getMedia(videoLink);
        videoView.setVideoURI(videoUri);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
                if (currentPosition > 0) {
                    videoView.seekTo(currentPosition);
                } else {
                    videoView.seekTo(1);
                }
                videoView.start();
            }
        });
    }

    private Uri getMedia(String videoLink) {
        if (URLUtil.isValidUrl(videoLink)) {
            return Uri.parse(videoLink);
        } else {
            return Uri.parse("android.resource://" + getPackageName() + "/raw/" + videoLink);
        }
    }
}
