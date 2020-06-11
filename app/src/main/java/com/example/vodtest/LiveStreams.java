package com.example.vodtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LiveStreams extends AppCompatActivity {

    String streamnames[];
    String streamurls[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_streams);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String filename=getApplicationContext().getFilesDir()+"/livestreamlist.txt";
        TransferObserver downloadObserver = ClientFactory.transferUtility().
                download("vod-source-3netjtvtxz5j","livestreamlist", new File(filename));
        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    List<String> streamnames_list=new ArrayList<>();
                    List<String> streamurls_list=new ArrayList<>();
                    try {
                        InputStream is = new FileInputStream(filename);
                        BufferedReader buf = new BufferedReader(new InputStreamReader(is));
                        String line = buf.readLine();
                        StringBuilder sb = new StringBuilder();
                        while (line != null) {
                            sb.append(line).append("\n");
                            line = buf.readLine();
                            streamnames_list.add(line);
                            line = buf.readLine();
                            streamurls_list.add(line);
                        }
                        streamnames = new String[streamnames_list.size()];
                        streamurls = new String[streamurls_list.size()];
                        for (int i = 0; i < streamnames_list.size(); i++) {
                            streamnames[i] = streamnames_list.get(i);
                            streamurls[i] = streamurls_list.get(i);
                        }
                        ListView streams = findViewById(R.id.streamsList);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LiveStreams.this,
                                android.R.layout.simple_list_item_1, streamnames);
                        streams.setAdapter(adapter);
                        streams.setOnItemClickListener((parent, view, position, id2) -> {
                            String videoPath =  streamurls[position];
                            String videoTitle = streamnames[position];
                            Bundle extras2 = new Bundle();
                            Log.i("livestreams","askhk");
                            extras2.putString("downloadPath", videoPath);
                            extras2.putString("downloadTitle", videoTitle);
                            Intent playDownloadIntent = new Intent(LiveStreams.this, LivestreamPlayer.class);
                            playDownloadIntent.putExtras(extras);
                            startActivity(playDownloadIntent);
                        });

                    }catch(Exception e){Log.i("Exc",e.toString());}
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @Override
            public void onError(int id, Exception ex) {
            }

        });

        }


    }

