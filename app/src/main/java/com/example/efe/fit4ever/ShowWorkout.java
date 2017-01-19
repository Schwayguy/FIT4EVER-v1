package com.example.efe.fit4ever;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

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

public class ShowWorkout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_workout);
        String progId= getIntent().getStringExtra("PROGID");
        String workoutId= getIntent().getStringExtra("WORKOUTID");
        String rowId=getIntent().getStringExtra("ROWID");


        File logFile = new File(getExternalFilesDir(null), progId + ".xls");
        FileInputStream myInput = null;


        try {
            myInput = new FileInputStream(logFile);
            int i= Integer.parseInt(rowId);
            POIFSFileSystem myFileSystem = null;
            myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            final HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            final int rowNumber = mySheet.getPhysicalNumberOfRows();

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
            Cell cell4 = row.getCell(periodRef.getCol());
            final Cell cell5 = row.getCell(repeatRef.getCol());
            Cell cell6 = row.getCell(videoRef.getCol());
            int minute =0;
            int seconds=0;
            if(Float.parseFloat(cell5.toString()) >= 30) {
                seconds = (int) Float.parseFloat(cell5.toString());
                if (Float.parseFloat(cell5.toString()) >= 60) {
                    minute = (int) (Float.parseFloat(cell5.toString()) / 60);
                    seconds = (int) (Float.parseFloat(cell5.toString()) % 60);
                }
                TextView repcount = (TextView) findViewById(R.id.repcount2);
                if(seconds>=10){
                repcount.setText("Repeat for "+minute + ":"+ seconds);
                }else{
                    repcount.setText("Repeat for "+minute + ":0"+ seconds);
                }
            }else {

            TextView repcount = (TextView) findViewById(R.id.repcount2);
            repcount.setText("Repeat "+String.valueOf((int)Float.parseFloat(cell5.toString()))+" times");

        }

        TextView workoutName = (TextView) findViewById(R.id.workoutName2);
        workoutName.setText(cell2.toString());


        TextView workoutInfo = (TextView) findViewById(R.id.workoutinfo2);
        workoutInfo.setText(cell3.toString());

        VideoView videoView = (VideoView) findViewById(R.id.videoView2);
        videoView.setVideoPath("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/"+cell6.toString());

        videoView.start();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });



    } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
