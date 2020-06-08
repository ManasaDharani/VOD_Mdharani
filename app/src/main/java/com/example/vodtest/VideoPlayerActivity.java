package com.example.vodtest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.amazonaws.amplify.generated.graphql.GetUserQuery;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

import javax.annotation.Nonnull;

public class VideoPlayerActivity extends AppCompatActivity {

    boolean fullscreen = false;
    String sub;
    String name;
    String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        //Player
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String title = extras.getString("EXTRA_TITLE");
        String genre = extras.getString("EXTRA_GENRE");
        String url = extras.getString("EXTRA_URL");
        sub = extras.getString("EXTRA_SUB");
        TextView titleText = findViewById(R.id.text_title);
        TextView genreText = findViewById(R.id.text_genre);
        PlayerView playerView = findViewById(R.id.player_view);
        titleText.setText(title);
        genreText.setText(genre);
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "VOD"));
        MediaSource videoSource =
                new HlsMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(url));
        player.prepare(videoSource);

        //Downloads
        Button downloadButton = findViewById(R.id.btn_download);
        downloadButton.setTransformationMethod(null);
        downloadButton.setOnClickListener(view -> downloadVideo());

        //Fullscreen player
        ImageView fullscreenButton = playerView.findViewById(R.id.exo_fullscreen_icon);
        fullscreenButton.setOnClickListener(view -> {
            if(fullscreen) {
                fullscreenButton.setImageDrawable(ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_fullscreen_black_24dp));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                if(getSupportActionBar() != null){
                    getSupportActionBar().show();
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = (int) ( 200 * getApplicationContext().getResources().getDisplayMetrics().density);
                playerView.setLayoutParams(params);
                fullscreen = false;
            }else{
                fullscreenButton.setImageDrawable(ContextCompat.getDrawable(VideoPlayerActivity.this, R.drawable.ic_fullscreen_exit_black_24dp));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                if(getSupportActionBar() != null){
                    getSupportActionBar().hide();
                }
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                playerView.setLayoutParams(params);
                fullscreen = true;
            }
        });

        GetUserQuery getUserQuery = GetUserQuery.builder()
                .id(sub)
                .build();

        ClientFactory.appSyncClient().query(getUserQuery)
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(queryCallback);

        LinearLayout profileInfo = findViewById(R.id.userInfoLayout);
        profileInfo.setOnClickListener(v -> {
//            Intent viewProfileIntent = new Intent(VideoPlayerActivity.this, OtherProfileActivity.class);
//            viewProfileIntent.putExtra("sub", sub);
//            startActivity(viewProfileIntent);
        });

    }

    public void downloadVideo() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String mp4Url = extras.getString("EXTRA_MP4URL");
        String title = extras.getString("EXTRA_TITLE");
        Thread downloadThread = new Thread(() -> {
            try {
                URL url = new URL(mp4Url);
                String path = this.getFilesDir().getPath()+"/"+title+".mp4";
                FileUtils.copyURLToFile(url, new File(path));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        downloadThread.start();
    }
    private GraphQLCall.Callback<GetUserQuery.Data> queryCallback = new GraphQLCall.Callback<GetUserQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<GetUserQuery.Data> response) {

            runOnUiThread(() -> {
                ImageView profileImage = findViewById(R.id.profilePictureDisplay);
                TextView nameText = findViewById(R.id.text_displayName);
                TextView locationText = findViewById(R.id.text_displayLocation);
                name = response.data().getUser().name();
                nameText.setText(name);

                if (response.data().getUser().pictureUrl() != null) {
                    String picUrl = response.data().getUser().pictureUrl();
                    Picasso.get().load(picUrl).into(profileImage);
                } else {
                    Picasso.get().load("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png").into(profileImage);
                }
                if (response.data().getUser().location() != null) {
                    location = response.data().getUser().location();
                    locationText.setText(location);
                } else {
                    String locationStr = "Location not specified";
                    locationText.setText(locationStr);
                }
            });

        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e("VOD", e.toString());
        }
    };
}
