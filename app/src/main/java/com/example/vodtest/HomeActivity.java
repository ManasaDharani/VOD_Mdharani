package com.example.vodtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.amazonaws.amplify.generated.graphql.ListVideosQuery;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class HomeActivity extends AppCompatActivity{

    RecyclerView mRecyclerView;
    MyAdapter mAdapter;
    private ArrayList<ListVideosQuery.Item> mVideos;
    private final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recycler_view);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Set up the video player when a video is clicked
        MyAdapter.RecyclerViewClickListener mListener = (view, position) -> {
            Log.i(TAG, mVideos.get(position).id());
            String title = mVideos.get(position).title();
            String genre = mVideos.get(position).genre();
            String hlsUrl = mVideos.get(position).hlsUrl();
            String mp4Url = mVideos.get(position).mp4Urls().get(0);
            String sub = mVideos.get(position).sub();
            Intent playVideoIntent = new Intent(HomeActivity.this, VideoPlayerActivity.class);
            Bundle extras = new Bundle();
            extras.putString("EXTRA_TITLE", title);
            extras.putString("EXTRA_GENRE", genre);
            extras.putString("EXTRA_URL", hlsUrl);
            extras.putString("EXTRA_MP4URL", mp4Url);
            extras.putString("EXTRA_SUB", sub);
            playVideoIntent.putExtras(extras);
            startActivity(playVideoIntent);
        };

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this, mListener);
        mRecyclerView.setAdapter(mAdapter);

        ClientFactory.init(this);

        //Upload button
        FloatingActionButton btnUpload = findViewById(R.id.btn_uploadvideo);
        btnUpload.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(this, view);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.upload_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId()==R.id.video){
                        Intent uploadIntent = new Intent(HomeActivity.this, UploadVideoActivity.class);
                        HomeActivity.this.startActivity(uploadIntent);
                    }
                    else{
                        Intent uploadIntent = new Intent(HomeActivity.this, Upload_Stream.class);
                        HomeActivity.this.startActivity(uploadIntent);
                    }
                    return true;
                }
            });
            popup.show();
//            Intent uploadIntent = new Intent(HomeActivity.this, UploadVideoActivity.class);
//            HomeActivity.this.startActivity(uploadIntent);
        });

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            query();
            pullToRefresh.setRefreshing(false);
        });

        //Bottom navigation bar
        BottomNavigationView navigation = findViewById(R.id.bottom_bar);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    break;
                case R.id.action_profile:
                    Intent a = new Intent(HomeActivity.this,MyProfileActivity.class);
                    startActivity(a);
                    overridePendingTransition(0,0);
                    break;
                case R.id.action_activity3:
                    Intent b = new Intent(HomeActivity.this,Activity3.class);
                    startActivity(b);
                    overridePendingTransition(0,0);
                    break;
            }
            return true;
        });
        MenuItem item = navigation.getMenu().findItem(R.id.action_home);
        item.setChecked(true);


    }

    @Override
    public void onResume() {
        super.onResume();

        // Query list data when we return to the screen
        query();
    }

    public void query(){
        ClientFactory.appSyncClient().query(ListVideosQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(queryCallback);
    }

    private GraphQLCall.Callback<ListVideosQuery.Data> queryCallback = new GraphQLCall.Callback<ListVideosQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListVideosQuery.Data> response) {

            mVideos = new ArrayList<>(response.data().listVideos().items());

            Log.i(TAG, "Retrieved list items: " + mVideos.toString());

            runOnUiThread(() -> {
                mAdapter.setItems(mVideos);
                mAdapter.notifyDataSetChanged();
            });
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
        }
    };

}
