package com.example.vodtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        ListView downloads = findViewById(R.id.downloadsList);
        String path = this.getFilesDir().getPath();
        File dir = new File(path);
        File[] files = dir.listFiles();
        List<String> fileslist=new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            CognitoUserPool userpool = new CognitoUserPool(DownloadsActivity.this, new AWSConfiguration(DownloadsActivity.this));
            String username = userpool.getCurrentUser().getUserId();
            //Get downloaded files of only the signed in user
            Log.i("downloads", String.valueOf(files[i].getName().indexOf("_")));
            if(files[i].getName()!="liveStreams"){
                //filenames[i]=files[i].getName();
                try {
                    Log.i("downloads",files[i].getName().substring(0, files[i].getName().indexOf('_')));
                    if (files[i].getName().substring(0, files[i].getName().indexOf('_')).equals(username)) {
                        //filenames[k] = files[i].getName().substring(files[i].getName().indexOf('_') + 1);
                        Log.i("downloads", files[i].getName());
                        fileslist.add(files[i].getName().substring(files[i].getName().indexOf('_') + 1));
                    }
                }
                catch(StringIndexOutOfBoundsException e){}
            }

        }
        String[] filenames = new String[fileslist.size()];
        for(int i=0;i<fileslist.size();i++)
            filenames[i]=fileslist.get(i);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, filenames);
        downloads.setAdapter(adapter);
        downloads.setOnItemClickListener((parent, view, position, id) -> {
            String videoPath = path + "/"+filenames[position];
            String videoTitle = filenames[position].substring(0,filenames[position].length()-4);
            Bundle extras = new Bundle();
            extras.putString("downloadPath", videoPath);
            extras.putString("downloadTitle", videoTitle);
            Log.i("VOD", videoPath);
            Log.i("VOD", videoTitle);
            Intent playDownloadIntent = new Intent(DownloadsActivity.this, DownloadPlayerActivity.class);
            playDownloadIntent.putExtras(extras);
            startActivity(playDownloadIntent);
        });


    }
}
