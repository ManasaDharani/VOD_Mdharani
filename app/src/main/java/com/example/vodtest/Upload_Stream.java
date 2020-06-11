package com.example.vodtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateVideoMutation;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import type.CreateVideoInput;

public class Upload_Stream extends AppCompatActivity {
    String TAG="app";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__stream);
        Button upload=findViewById(R.id.btn_upload_stream);
        upload.setOnClickListener(view -> {
            String url=((EditText) findViewById(R.id.editTxt_url)).getText().toString();
            String name=((EditText) findViewById(R.id.editTxt_name)).getText().toString();
            String desc=((EditText) findViewById(R.id.editText_desc)).getText().toString();
            String filename=getApplicationContext().getFilesDir()+"/livestreamlist.txt";
            TransferObserver downloadObserver = ClientFactory.transferUtility().
                    download("vod-source-3netjtvtxz5j","livestreamlist", new File(filename));
            downloadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        String fileAsString="";
                        try {
                            fileAsString=fileAsString+name+"\n"+url+"\n";
                            // Open given file in append mode.
                            BufferedWriter out = new BufferedWriter(
                                    new FileWriter(filename, true));
                            out.write(fileAsString);
                            out.close();
                        }
                        catch (IOException e) {
                            Log.i("filewrite","exception occoured" + e);
                        }
                        ClientFactory.transferUtility().upload(
                                "livestreamlist",
                                new File(filename)
                                );
                        Upload_Stream.this.finish();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                }

                @Override
                public void onError(int id, Exception ex) {
                }

            });


            //storeLiveStream(url,name,desc);


        });
    }
}
