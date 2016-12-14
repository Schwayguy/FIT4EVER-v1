package com.example.efe.fit4ever;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PlayWorkout extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    int i = 2;
    int j = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_workout);
        Button btnPlay = (Button) findViewById(R.id.buttonNext);
        File logFile = new File(getExternalFilesDir(null), getIntent().getStringExtra("PROGID") + ".xls");
        FileInputStream myInput = null;
        try {
            myInput = new FileInputStream(logFile);
            POIFSFileSystem myFileSystem = null;
            myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            int rowNumber = mySheet.getPhysicalNumberOfRows();
            while ( i <= rowNumber) {
                CellReference idRef = new CellReference("A" + i);
                CellReference nameRef = new CellReference("B" + i);
                CellReference infoRef = new CellReference("C" + i);
                CellReference periodRef = new CellReference("G" + i);
                CellReference repeatRef = new CellReference("H" + i);
                Row row = mySheet.getRow(idRef.getRow());
                Cell cell1 = row.getCell(idRef.getCol());
                Cell cell2 = row.getCell(nameRef.getCol());
                Cell cell3 = row.getCell(infoRef.getCol());
                final Cell cell4 = row.getCell(periodRef.getCol());
                Cell cell5 = row.getCell(repeatRef.getCol());
                btnPlay.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(j>=Integer.parseInt(cell4.toString())){
                            j=0;
                            i++;
                        }else{
                            j++;
                            Log.d("ee", String.valueOf(j));
                        }
                    }

                });
                TextView workoutName = (TextView)findViewById(R.id.workoutName);
                workoutName.setText(cell2.toString());


                VideoView videoView = (VideoView) findViewById(R.id.videoView);
                videoView.setVideoPath("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/67.mp4");

                videoView.start();
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("PlayWorkout Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}