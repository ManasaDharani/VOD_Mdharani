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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.File;
import java.io.FileWriter;

public class Upload_Stream extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload__stream);
        Button upload=findViewById(R.id.btn_upload_stream);
        upload.setOnClickListener(view -> {
            String url=((EditText) findViewById(R.id.editTxt_url)).getText().toString();
            String name=((EditText) findViewById(R.id.editTxt_name)).getText().toString();
            String desc=((EditText) findViewById(R.id.editText_desc)).getText().toString();

            storeLiveStream(url,name,desc);


        });
    }
    public void storeLiveStream(String url,String name,String desc){
        Log.i("file",name);
        File file = new File(getApplicationContext().getFilesDir(),"liveStreams");
        if(!file.exists()){
            file.mkdir();
        }
        try{
            File stream = new File(file, name);
            String key = name;
            FileWriter writer = new FileWriter(stream);
            writer.append(url);
            writer.flush();
            writer.close();
            Log.i("file","written");
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.addUserMetadata("url", url);
            objectMetadata.addUserMetadata("name", name);
            objectMetadata.addUserMetadata("desc", desc);
            TransferObserver uploadObserver = ClientFactory.transferUtility().upload(
                    key,
                    new File(file, name),
                    objectMetadata);
            uploadObserver.setTransferListener(new TransferListener() {

                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        // Handle a completed upload.
                        //Log.d(TAG, "Upload is completed. ");
                        Toast.makeText(Upload_Stream.this, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                        Upload_Stream.this.finish();

                        // Upload is successful. Save the rest and send the mutation to server.
                        //save();
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                    int percentDone = (int)percentDonef;
                    String progressString = "Upload progress: "+percentDone+"%";
                    ((TextView) findViewById(R.id.text_upload_progress_stream)).setText(progressString);
                }

                @Override
                public void onError(int id, Exception ex) {
                    // Handle errors
                   // Log.e(TAG, "Failed to upload video. ", ex);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Upload_Stream.this, "Failed to upload video", Toast.LENGTH_LONG).show();
                        }
                    });
                }

            });

        }catch (Exception e){
            e.printStackTrace();

        }

    }
}
