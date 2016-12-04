package com.example.efe.fit4ever;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;
import android.os.StrictMode;
import android.util.Log;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import net.sourceforge.jtds.jdbc.UniqueIdentifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Calendar;
import java.util.UUID;

public class WorkoutIntro extends AppCompatActivity {
    Connection conn;
    Statement statement = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_intro);
        Intent intent = getIntent();
        String progId = intent.getStringExtra("PROGID");
        CONN();
        TextView progname = (TextView) findViewById(R.id.progNameText);
        TextView progowner = (TextView) findViewById(R.id.progOwnerText);
        TextView ratingtext = (TextView) findViewById(R.id.ratingText1);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        try {
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet result = null;
        try {
            result = statement.executeQuery("select * from Programs  where ID = '" + progId + "'");
        } catch (SQLException e) {
            Log.e("ERRORc", e.getMessage());
        }
        try {
            assert result != null;
            while (result.next()) {
                progname.setText(result.getString("Title"));
                //  progowner.setText("Author: "+result.getString("Creator"));
                ratingtext.setText(result.getString("Rate"));
                ratingBar.setRating(Float.parseFloat(result.getString("Rate")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        conn = null;
        String ConnURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://192.168.1.23:1433/Workout";
            conn = DriverManager.getConnection(ConnURL, "efe", "e1234567");
            System.out.println("connected");
        } catch (SQLException se) {
            Log.e("ERRO1", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO2", e.getMessage());
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong while trying to connect to the database, please check your internet connection.", Toast.LENGTH_LONG).show();
        }
        return conn;
    }

    public void Purchase(View view) {
        Download();// bunu sonra düzelt kontrol sağla
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        String uniqueID = UUID.randomUUID().toString();

        if(!userId.isEmpty()) {

            try {
                ResultSet tryWorkoutPurchase = statement.executeQuery("USE [Workout] SELECT * FROM [dbo].[UPRelation] where [UserID] ='" + userId + "' AND [ProgramId]='" + getIntent().getStringExtra("PROGID") + "'");
                if (!tryWorkoutPurchase.next()) {
                    statement.executeUpdate(" USE [Workout] INSERT INTO [dbo].[UPRelation] ([ID] ,[ProgramID],[UserID],[RegisterDate]) VALUES" +
                            " ('" + uniqueID + "','" + getIntent().getStringExtra("PROGID") + "','" + userId + "','" + Calendar.YEAR + "-" + Calendar.MONTH + "-" + Calendar.DAY_OF_MONTH + "')");
                    Toast.makeText(this, "Program purchased.", Toast.LENGTH_SHORT).show();

                    //MyWorkout'a gönder


                } else {
                    Toast.makeText(this, "You already own this workout.", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
                Log.e("ERRORp", e.getMessage());
            }
        }else{
            Toast.makeText(this, "You need to login to make purchases.", Toast.LENGTH_SHORT).show();
        }
    }

    public void Download() {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        File logFile = new File(getExternalFilesDir(null),getIntent().getStringExtra("PROGID") + ".xls");

        // şuan her zaman yeni dosya indiriyo sonra düzelt
        if(logFile.exists()){
            Toast.makeText(this,"It already exists.",Toast.LENGTH_SHORT).show();
        }
        try {
            ResultSet workoutDownloadInfo = statement.executeQuery("USE [Workout] SELECT  WorkoutID , Name, Information ,Rate, Title ,Subtitle , Period ,Repeat, Queue" +
                    " FROM [dbo].[Workouts] INNER JOIN [dbo].[PWRelation] on [dbo].[Workouts].[ID]=[dbo].[PWRelation].[WorkoutID] AND [ProgramID]='" + getIntent().getStringExtra("PROGID") + "' order by Queue");

            Workbook wb = new HSSFWorkbook();
            Cell c = null;
            CellStyle cs = wb.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.LIME.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            Sheet sheet1 = null;
            sheet1 = wb.createSheet("Workout Sheet");

            Row headerRow = sheet1.createRow(0);

            c = headerRow .createCell(0);
            c.setCellValue("WorkoutID");
            c.setCellStyle(cs);

            c = headerRow .createCell(1);
            c.setCellValue("Name");
            c.setCellStyle(cs);

            c = headerRow .createCell(2);
            c.setCellValue("Information");
            c.setCellStyle(cs);

            c = headerRow .createCell(3);
            c.setCellValue("Rate");
            c.setCellStyle(cs);

            c = headerRow .createCell(4);
            c.setCellValue("Title");
            c.setCellStyle(cs);

            c = headerRow .createCell(5);
            c.setCellValue("Subtitle");
            c.setCellStyle(cs);

            c = headerRow .createCell(6);
            c.setCellValue("Period");
            c.setCellStyle(cs);

            c = headerRow .createCell(7);
            c.setCellValue("Repeat");
            c.setCellStyle(cs);

            c = headerRow .createCell(8);
            c.setCellValue("Queue");
            c.setCellStyle(cs);

            sheet1.setColumnWidth(0, (15 * 700));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));
            sheet1.setColumnWidth(6, (15 * 200));
            sheet1.setColumnWidth(7, (15 * 200));
            sheet1.setColumnWidth(8, (15 * 200));

            FileOutputStream os = null;
            int row = 1;
            while (workoutDownloadInfo.next()) {
                Row dataRow = sheet1.createRow(row);

                c = dataRow.createCell(0);
                c.setCellValue(workoutDownloadInfo.getString("WorkoutID"));
                c = dataRow.createCell(1);
                c.setCellValue(workoutDownloadInfo.getString("Name"));
                c = dataRow.createCell(2);
                c.setCellValue(workoutDownloadInfo.getString("Information"));
                c = dataRow.createCell(3);
                c.setCellValue(Float.toString(workoutDownloadInfo.getFloat("Rate")));
                c = dataRow.createCell(4);
                c.setCellValue(workoutDownloadInfo.getString("Title") );
                c = dataRow.createCell(5);
                c.setCellValue(workoutDownloadInfo.getString("Subtitle"));
                c = dataRow.createCell(6);
                c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Period")));
                c = dataRow.createCell(7);
                c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Repeat")) );
                c = dataRow.createCell(8);
                c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Queue")));
                row++;
            }
                os = new FileOutputStream(logFile);
                wb.write(os);

            Toast.makeText(this, "Download successful.", Toast.LENGTH_SHORT).show();
            os.close();
        } catch (SQLException e) {
            Log.e("error download", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("WorkoutIntro Page") // TODO: Define a title for the content shown.
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
