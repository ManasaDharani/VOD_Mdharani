package com.example.vodtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;


public class DownloadPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_player);
        //Downloads player
        PlayerView playerView = findViewById(R.id.downloadPlayer);
        TextView titleText = findViewById(R.id.download_title);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String path = extras.getString("downloadPath");
        String title = extras.getString("downloadTitle");

            titleText.setText(title);
            SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "VOD"));
            MediaSource videoSource =
                    new ProgressiveMediaSource.Factory(dataSourceFactory)
                            .createMediaSource(Uri.parse(path));
            player.prepare(videoSource);
    }
}
