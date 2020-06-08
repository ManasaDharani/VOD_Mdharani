package com.example.vodtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

public class DownloadsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);
        ListView downloads = findViewById(R.id.downloadsList);
        String path = this.getFilesDir().getPath();
        File dir = new File(path);
        File[] files = dir.listFiles();
        String[] filenames = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filenames[i] = files[i].getName();
        }
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
