package com.example.efe.fit4ever;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

public class MyWorkout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_workout);

        ImageView flowchart = (ImageView) findViewById(R.id.flowchart);
        Bitmap circle = BitmapFactory.decodeResource(getResources(), R.drawable.circle);
        int circleHeight = circle.getHeight();
        Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
        int arrowHeight = arrow.getHeight();
        Bitmap vertextop = BitmapFactory.decodeResource(getResources(), R.drawable.vertextop);
        int vertexTheight = vertextop.getHeight();
        Bitmap line = BitmapFactory.decodeResource(getResources(), R.drawable.line);
        int lineheight = line.getHeight();
        Bitmap vertexbot = BitmapFactory.decodeResource(getResources(), R.drawable.vertexbot);
        int vertexBheight = vertexbot.getHeight();


        Bitmap combinedBitmap = drawTwo(circleHeight,arrowHeight,circle, arrow);

        //bu fonksiyonu tüm resimler çizildikten sonra çağır


        File logFile = new File(getExternalFilesDir(null),getIntent().getStringExtra("PROGID") + ".xls");
        FileInputStream myInput = null;
        if(logFile.exists()) {
            try {
                myInput = new FileInputStream(logFile);
                POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int rowNumber = mySheet.getPhysicalNumberOfRows();
                int i;
                for(i=1 ;i<rowNumber;i++) {
                    CellReference cellReference = new CellReference("A"+i);
                    Row row = mySheet.getRow(cellReference.getRow());
                    Cell cell = row.getCell(cellReference.getCol());
                    Log.d("try", "cell Value: " + cell.toString());

                    Bitmap workout = drawMiddle(vertexTheight,lineheight,vertexBheight,arrowHeight, vertextop, line, vertexbot,arrow);

                    int workHeight = workout.getHeight();
                    int combHeight = combinedBitmap.getHeight();
                    combinedBitmap = drawTwo(combHeight,workHeight,combinedBitmap,workout);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        combinedBitmap = drawTwo(combinedBitmap.getHeight(),circleHeight,combinedBitmap,circle);
        Bitmap resized = Bitmap.createScaledBitmap(combinedBitmap,(int)(combinedBitmap.getWidth()*0.2), (int)(combinedBitmap.getHeight()*0.2), true);
        //Bitmap resized = Bitmap.createBitmap(combinedBitmap,0,0,(int)(combinedBitmap.getWidth()*0.5), (int)(combinedBitmap.getHeight()*0.5));
        flowchart.setImageBitmap(resized);
    }

    public Bitmap drawTwo(int b1Height, int b2Height, Bitmap b1, Bitmap b2) {
        Bitmap drawnBitmap = null;

        try {
            drawnBitmap = Bitmap.createBitmap(1000, b1Height+b2Height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(drawnBitmap);
            // JUST CHANGE TO DIFFERENT Bitmaps and coordinates .
            canvas.drawBitmap(b1, 0, 0, null);
            canvas.drawBitmap(b2, 0, b1Height, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawnBitmap;
    }

    public Bitmap drawMiddle(int b1Height, int b2Height,int b3Height, int b4Height, Bitmap b1, Bitmap b2, Bitmap b3, Bitmap b4) {
        Bitmap drawnBitmap = null;

        try {
            drawnBitmap = Bitmap.createBitmap(1000, b1Height+b2Height+b3Height+b4Height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(drawnBitmap);
            // JUST CHANGE TO DIFFERENT Bitmaps and coordinates .


            canvas.drawBitmap(b1, 0, 0, null);
            canvas.drawBitmap(b2, 0, b1Height, null);
            canvas.drawBitmap(b3, 0, b1Height+b2Height, null);
            canvas.drawBitmap(b4, 0, b1Height+b2Height+b3Height, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawnBitmap;
    }
}