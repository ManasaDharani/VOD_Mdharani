package com.example.vodtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LiveStreams extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_streams);
        ListView streams = findViewById(R.id.streamsList);
        String path = this.getFilesDir().getPath()+"/liveStreams";
        File dir = new File(path);
        File[] files = dir.listFiles();
        String[] streamurls = new String[files.length];
        String[] streamnames=new String[files.length];
        for (int i = 0; i < files.length; i++) {
            try {
                InputStream is = new FileInputStream(files[i]);
                BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                String line = buf.readLine();
                StringBuilder sb = new StringBuilder();
                sb.append(line);
                streamurls[i]=sb.toString();
                streamnames[i]=files[i].getName();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //filenames[i] = files[i].getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, streamurls);
        streams.setAdapter(adapter);
        streams.setOnItemClickListener((parent, view, position, id) -> {
            String videoPath = streamurls[position];
            String videoTitle = streamnames[position];
            Bundle extras = new Bundle();
            extras.putString("downloadPath", videoPath);
            extras.putString("downloadTitle", videoTitle);
            Intent playDownloadIntent = new Intent(LiveStreams.this, LivestreamPlayer.class);
            playDownloadIntent.putExtras(extras);
            startActivity(playDownloadIntent);
        });
    }
}
