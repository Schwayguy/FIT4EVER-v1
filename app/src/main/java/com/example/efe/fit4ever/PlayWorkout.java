package com.example.efe.fit4ever;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
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
    int i ;
    int j ;
    Cell cell4;
    Cell cell5;
    CountDownTimer yourCountDownTimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_workout);
        final Button btnPlay = (Button) findViewById(R.id.buttonNext);
        final SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();
        File logFile = new File(getExternalFilesDir(null), getIntent().getStringExtra("PROGID") + ".xls");
        FileInputStream myInput = null;
        ;
       // final Button start= (Button) findViewById(R.id.buttonstop);
       // final Button stop= (Button) findViewById(R.id.buttonstart);


        try {
            myInput = new FileInputStream(logFile);
            POIFSFileSystem myFileSystem = null;
            myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            final HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            final int rowNumber = mySheet.getPhysicalNumberOfRows();
            final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar2);
            seekBar.setVisibility(View.INVISIBLE);
            i=2;
            j=1;
            CellReference idRef = new CellReference("A" + i);
            CellReference nameRef = new CellReference("B" + i);
            CellReference videoRef = new CellReference("C" + i);
            CellReference infoRef = new CellReference("D" + i);
            CellReference periodRef = new CellReference("H" + i);
            CellReference repeatRef = new CellReference("I" + i);
            Row row = mySheet.getRow(idRef.getRow());
            Cell cell1 = row.getCell(idRef.getCol());
            Cell cell2 = row.getCell(nameRef.getCol());
            Cell cell3 = row.getCell(infoRef.getCol());
            cell4 = row.getCell(periodRef.getCol());
            cell5 = row.getCell(repeatRef.getCol());
            Cell cell6 = row.getCell(videoRef.getCol());
            if((Integer.parseInt(cell4.toString())==0)||(Integer.parseInt(cell4.toString())==1)){
                i++;
                j = 0;
            }

            int minute =0;
            int seconds=0;
            if(Float.parseFloat(cell5.toString()) > 30) {
                seekBar.setVisibility(View.VISIBLE);
                seconds = (int) Float.parseFloat(cell5.toString());
                if(Float.parseFloat(cell5.toString()) >= 60){
                    minute = (int) (Float.parseFloat(cell5.toString())/60);
                    seconds = (int) (Float.parseFloat(cell5.toString())%60);
                }
                final TextView repcount = (TextView) findViewById(R.id.repcount);
                final int finalSeconds = seconds;
                final int finalMinute = minute;
                CountDownTimer yourCountDownTimer= new  CountDownTimer((long) (1000 * (Float.parseFloat(cell5.toString()) + 1.0)), 1000) {
                    int k1 = 0;
                    int k2=0;
                    int k3=0;
                    public void onTick(long millisUntilFinished) {
                        seekBar.setVisibility(View.VISIBLE);
                        seekBar.setMax((int) Float.parseFloat(cell5.toString()));
                        repcount.setText(k1+":"+k2+"                                                                "+ finalMinute + ":"+ finalSeconds);
                        seekBar.setProgress(k3);
                        k2++;
                        k3++;
                        if(k2==60){
                            k1++;
                            k2=0;
                        }
                        btnPlay.setClickable(false);
                    }

                    public void onFinish() {
                        seekBar.setProgress(k3);
                        repcount.setText(k1+":"+k2+"                                                                 "+ finalMinute + ":"+ finalSeconds);
                        btnPlay.setClickable(true);
                    }
                }.start();
            }else {

                TextView repcount = (TextView) findViewById(R.id.repcount);
                repcount.setText("Repeat "+String.valueOf((int)Float.parseFloat(cell5.toString()))+" times");
                seekBar.setVisibility(View.INVISIBLE);

            }

                TextView workoutName = (TextView) findViewById(R.id.workoutName);
            workoutName.setText(cell2.toString());


            TextView workoutInfo = (TextView) findViewById(R.id.workoutinfo);
            workoutInfo.setText(cell3.toString());

            VideoView videoView = (VideoView) findViewById(R.id.videoView);
            videoView.setVideoPath("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/67.mp4");//+cell6.toString()

            videoView.start();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                }
            });
            btnPlay.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if (i > rowNumber) {
                            Toast.makeText(getApplicationContext(), "Great job!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                        CellReference idRef = new CellReference("A" + i);
                        CellReference nameRef = new CellReference("B" + i);
                        CellReference videoRef = new CellReference("C" + i);
                        CellReference infoRef = new CellReference("D" + i);
                        CellReference periodRef = new CellReference("H" + i);
                        CellReference repeatRef = new CellReference("I" + i);
                        Row row = mySheet.getRow(idRef.getRow());
                        Cell cell1 = row.getCell(idRef.getCol());
                        Cell cell2 = row.getCell(nameRef.getCol());
                        Cell cell3 = row.getCell(infoRef.getCol());
                        cell4 = row.getCell(periodRef.getCol());
                        cell5 = row.getCell(repeatRef.getCol());
                        Cell cell6 = row.getCell(videoRef.getCol());
                        if((Integer.parseInt(cell4.toString())==0)||(Integer.parseInt(cell4.toString())==1)){
                            i++;
                            j = 0;
                        }
                        else if (j+1 > Integer.parseInt(cell4.toString())) {
                            j = 0;
                            i++;
                        } else {
                            j++;
                        }
                        Log.d("aaab", String.valueOf(j)+" "+cell4.toString()+" "+cell2.toString()+" "+String.valueOf(i));




                         if(cell2.toString().equals("Rest")){
                             Intent intent = new Intent(getBaseContext(), Rest.class);
                             intent.putExtra("RESTTIME", cell5.toString());
                             startActivity(intent);
                             j=0;
                            }else{

                             int minute =0;
                             int seconds=0;
                            if(Float.parseFloat(cell5.toString()) > 30) {
                                seekBar.setVisibility(View.VISIBLE);
                                seconds = (int) Float.parseFloat(cell5.toString());
                                if(Float.parseFloat(cell5.toString()) >= 60){
                                    minute = (int) (Float.parseFloat(cell5.toString())/60);
                                    seconds = (int) (Float.parseFloat(cell5.toString())%60);
                                }
                                final TextView repcount = (TextView) findViewById(R.id.repcount);
                                final int finalSeconds = seconds;
                                final int finalMinute = minute;
                                new CountDownTimer((long) (1000 * (Float.parseFloat(cell5.toString()) + 1.0)), 1000) {
                                    int k1 = 0;
                                    int k2=0;
                                    int k3=0;
                                    public void onTick(long millisUntilFinished) {
                                        seekBar.setVisibility(View.VISIBLE);
                                        seekBar.setMax((int) Float.parseFloat(cell5.toString()));
                                        repcount.setText(k1+":"+k2+"                                                                "+ finalMinute + ":"+ finalSeconds);
                                        seekBar.setProgress(k3);
                                        k2++;
                                        k3++;
                                        if(k2==60){
                                            k1++;
                                            k2=0;
                                        }
                                        btnPlay.setClickable(false);
                                    }

                                    public void onFinish() {
                                        seekBar.setProgress(k3);
                                        repcount.setText(k1+":"+k2+"                                                                 "+ finalMinute + ":"+ finalSeconds);
                                        btnPlay.setClickable(true);
                                    }
                                }.start();

                            }
                             if(Float.parseFloat(cell4.toString()) > 30) {
                                 seekBar.setVisibility(View.VISIBLE);
                                 seconds = (int) Float.parseFloat(cell5.toString());
                                 if(Float.parseFloat(cell4.toString()) >= 60){
                                     minute = (int) (Float.parseFloat(cell4.toString())/60);
                                     seconds = (int) (Float.parseFloat(cell4.toString())%60);
                                 }
                                 final TextView repcount = (TextView) findViewById(R.id.repcount);
                                 final int finalSeconds = seconds;
                                 final int finalMinute = minute;
                                 new CountDownTimer((long) (1000 * (Float.parseFloat(cell4.toString()) + 1.0)), 1000) {
                                     int k1 = 0;
                                     int k2=0;
                                     int k3=0;
                                     public void onTick(long millisUntilFinished) {
                                         seekBar.setVisibility(View.VISIBLE);
                                         seekBar.setMax((int) Float.parseFloat(cell4.toString()));
                                         repcount.setText(k1+":"+k2+"                                                                "+ finalMinute + ":"+ finalSeconds);
                                         seekBar.setProgress(k3);
                                         k2++;
                                         k3++;
                                         if(k2==60){
                                             k1++;
                                             k2=0;
                                         }
                                         btnPlay.setClickable(false);
                                     }

                                     public void onFinish() {
                                         seekBar.setProgress(k3);
                                         repcount.setText(k1+":"+k2+"                                                                 "+ finalMinute + ":"+ finalSeconds);
                                         btnPlay.setClickable(true);
                                     }
                                 }.start();



                             }else {

                                TextView repcount = (TextView) findViewById(R.id.repcount);
                                repcount.setText("Repeat "+String.valueOf((int)Float.parseFloat(cell5.toString()))+" times");
                                 seekBar.setVisibility(View.INVISIBLE);


                            }
                                TextView workoutName = (TextView) findViewById(R.id.workoutName);
                                workoutName.setText(cell2.toString());

                                TextView workoutInfo = (TextView) findViewById(R.id.workoutinfo);
                                workoutInfo.setText(cell3.toString());

                                VideoView videoView = (VideoView) findViewById(R.id.videoView);
                                videoView.setVideoPath("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/67.mp4");//+cell6.toString()

                                videoView.start();
                                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        mp.setLooping(true);
                                    }
                                });

                            }
                        }
                    }
            });


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