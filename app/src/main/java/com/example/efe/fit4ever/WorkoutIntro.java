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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import net.sourceforge.jtds.jdbc.UniqueIdentifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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

        //String filepath = Environment.(Environment.DIRECTORY_DOWNLOADS).toString();
      //  File logFile = new File(filepath+ "/"+getIntent().getStringExtra("PROGID") + ".txt");

        File logFile = new File(getExternalFilesDir(null),getIntent().getStringExtra("PROGID") + ".txt");
        // şuan her zaman yeni dosya indiriyo sonra düzelt
        //if(!logFile.exists()){
            try {
             logFile.createNewFile();
            } catch (IOException e) {
              e.printStackTrace();
            }
      //  }else{
      //      Toast.makeText(this,"It already exists.",Toast.LENGTH_LONG).show();
      //  }
        try {
            ResultSet workoutDownloadInfo = statement.executeQuery("USE [Workout] SELECT  WorkoutID , Name, Information ,Rate, Title ,Subtitle , Period ,Repeat, Queue" +
                    " FROM [dbo].[Workouts] INNER JOIN [dbo].[PWRelation] on [dbo].[Workouts].[ID]=[dbo].[PWRelation].[WorkoutID] AND [ProgramID]='" + getIntent().getStringExtra("PROGID") + "' order by Queue");
            FileWriter fstream = new FileWriter(logFile);
            BufferedWriter out = new BufferedWriter(fstream);
            while (workoutDownloadInfo.next()) {
                String workoutIDCon =workoutDownloadInfo.getString("WorkoutID");
            //    Toast.makeText(this,workoutIDCon+" "+Integer.toString(workoutDownloadInfo.getInt("Queue")), Toast.LENGTH_SHORT).show();
                out.write(workoutDownloadInfo.getString("WorkoutID") + ", ");
                out.write(workoutDownloadInfo.getString("Name") + ", ");
                out.write(workoutDownloadInfo.getString("Information") + ", ");
                out.write(Float.toString(workoutDownloadInfo.getFloat("Rate")) + ", ");
                out.write(workoutDownloadInfo.getString("Title") + ", ");
                out.write(workoutDownloadInfo.getString("Subtitle") + ", ");
                out.write(Integer.toString(workoutDownloadInfo.getInt("Period")) + ", ");
                out.write(Integer.toString(workoutDownloadInfo.getInt("Repeat")) + ", ");
                out.write(Integer.toString(workoutDownloadInfo.getInt("Queue")) + ", ");
                out.newLine();
            }
            Toast.makeText(this, "Download successful.", Toast.LENGTH_SHORT).show();
            out.close();
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
