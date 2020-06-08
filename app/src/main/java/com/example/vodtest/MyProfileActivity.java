package com.example.vodtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.amplify.generated.graphql.GetUserQuery;
import com.amazonaws.amplify.generated.graphql.ListUsersQuery;
import com.amazonaws.amplify.generated.graphql.ListVideosQuery;
import com.amazonaws.amplify.generated.graphql.UpdateUserMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class MyProfileActivity extends AppCompatActivity {

    String[] items = new String[]{"Downloads","Live Streams","Sign Out"};
    String sub;
    String name;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myprofile_activity);

        //Get user info
        Thread getDataThread = new Thread(() -> {
            try {
                sub = AWSMobileClient.getInstance().getUserAttributes().get("sub");
                if (sub != null) {
                    Log.i("VOD", sub);
                    GetUserQuery getUserQuery = GetUserQuery.builder()
                            .id(sub)
                            .build();

                    ClientFactory.appSyncClient().query(getUserQuery)
                            .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                            .enqueue(queryCallback);
                } else {
                    Log.i("VOD", "sub null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getDataThread.start();

        ListView listView = findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (items[position] == "Sign Out") { //Sign out
                AWSMobileClient.getInstance().signOut();
                Intent authIntent = new Intent(MyProfileActivity.this, AuthenticationActivity.class);
                finish();
                startActivity(authIntent);
            }
            if (items[position] == "Downloads") { //List Downloads
                Intent downloadsIntent = new Intent(MyProfileActivity.this, DownloadsActivity.class);
                startActivity(downloadsIntent);
            }
            if(items[position] == "Live Streams"){
                Intent downloadsIntent = new Intent(MyProfileActivity.this, LiveStreams.class);
                startActivity(downloadsIntent);
            }
        });

        Button editProfileButton = findViewById(R.id.editProfileBtn);
        editProfileButton.setOnClickListener(v -> {
//            Intent editProfileIntent = new Intent(MyProfileActivity.this, EditProfileActivity.class);
//            Bundle profileInfo = new Bundle();
//            profileInfo.putString("sub", sub);
//            profileInfo.putString("name",name);
//            if (location != null) {
//                profileInfo.putString("location", location);
//            }
//            editProfileIntent.putExtras(profileInfo);
//            startActivity(editProfileIntent);
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_home:
                    Intent a = new Intent(MyProfileActivity.this, HomeActivity.class);
                    startActivity(a);
                    overridePendingTransition(0, 0);
                    break;
                case R.id.action_profile:
                    break;
                case R.id.action_activity3:
                    Intent b = new Intent(MyProfileActivity.this, Activity3.class);
                    startActivity(b);
                    overridePendingTransition(0, 0);
                    break;
            }
            return true;
        });
        MenuItem item = navigation.getMenu().findItem(R.id.action_profile);
        item.setChecked(true);


    }
    @Override
    public void onResume() {
        super.onResume();

        // Query list data when we return to the screen
        Thread getDataThread = new Thread(() -> {
            try {
                sub = AWSMobileClient.getInstance().getUserAttributes().get("sub");
                if (sub != null) {
                    Log.i("VOD", sub);
                    GetUserQuery getUserQuery = GetUserQuery.builder()
                            .id(sub)
                            .build();

                    ClientFactory.appSyncClient().query(getUserQuery)
                            .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                            .enqueue(queryCallback);
                } else {
                    Log.i("VOD", "sub null");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        getDataThread.start();
    }

    private GraphQLCall.Callback<GetUserQuery.Data> queryCallback = new GraphQLCall.Callback<GetUserQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<GetUserQuery.Data> response) {

            runOnUiThread(() -> {
                ImageView profileImage = findViewById(R.id.profilePicture);
                TextView nameText = findViewById(R.id.textName);
                TextView locationText = findViewById(R.id.textLocation);
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
