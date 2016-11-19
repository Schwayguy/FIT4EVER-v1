package com.example.efe.fit4ever;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Calendar;

public class WorkoutIntro extends AppCompatActivity {
    Connection conn;
    Statement statement = null;
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
        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar1);
        try {
            statement = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet result = null;
        try {
            result = statement.executeQuery("select * from Programs  where ID = "+progId);
        } catch (SQLException e) {
            Log.e("ERROR", e.getMessage());
        }
        try {
            assert result != null;
            while(result.next()){
                progname.setText(result.getString("Title"));
              //  progowner.setText("Author: "+result.getString("Creator"));
                ratingtext.setText(result.getString("Rate"));
                ratingBar.setRating(Float.parseFloat(result.getString("Rate")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        conn = null;
        String ConnURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://192.168.1.23:1433/Workout";
            conn = DriverManager.getConnection(ConnURL,"efe","e1234567");
            System.out.println("connected");
        } catch (SQLException se) {
            Log.e("ERRO1", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO2", e.getMessage());
        } catch(Exception e){
            Toast.makeText(this,"Something went wrong while trying to connect to the database, please check your internet connection.",Toast.LENGTH_LONG).show();
        }
        return conn;
    }

    public void Purchase(View view){
        SharedPreferences sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        try {
            ResultSet tryWorkoutPurchase= statement.executeQuery("USE [Workout] SELECT * FROM [dbo].[UPRelations] where [UserID] ="+userId+" AND [ProgramId]="+ getIntent().getStringExtra("PROGID"));
            if(!tryWorkoutPurchase.next()){
             statement.executeUpdate(" USE [Fit4ever]\n" +
                     "\n" +
                     "\n" +
                     "INSERT INTO [dbo].[WorkoutsOfUsers]\n" +
                     "           ([WorkoutID]\n" +
                     "           ,[UserID]\n" +
                     "           ,[ProgramId]\n" +
                     "           ,[RegisteredDate]\n" +
                     "     VALUES\n" +
                     "           (9,"+userId+","+ getIntent().getStringExtra("PROGID")+",'"+Calendar.YEAR+"-"+Calendar.MONTH+"-"+Calendar.DAY_OF_MONTH+"',NULL)\n" +
                     "\n" );
            }else {Toast.makeText(this,"You already own this workout.",Toast.LENGTH_SHORT).show();}
        } catch (SQLException e) {
            Log.e("ERROR", e.getMessage());
        }
    }
}
