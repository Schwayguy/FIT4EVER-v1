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
import android.widget.TabHost;
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
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    int curWeight ;
    EditText weightText;
    SharedPreferences sharedPref;
    Connection conn;
    int i;
    int j;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CONN();
        Statement statement = null;
        try {
            statement = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet result = null;
        try {
            result = statement.executeQuery("select * from Programs ");
        } catch (SQLException e) {
            Log.e("ERROR", e.getMessage());
        }

        TabHost pencere = (TabHost) findViewById(R.id.tabHost);
        pencere.setup();

        TabHost.TabSpec workouts = pencere.newTabSpec("Workouts");
        workouts.setContent(R.id.workouts);
        workouts.setIndicator("Workouts");
        pencere.addTab(workouts);

        TabHost.TabSpec myWorkout = pencere.newTabSpec("My Workout");
        myWorkout.setContent(R.id.myWorkout);
        myWorkout.setIndicator("My Workout");
        pencere.addTab(myWorkout);

        TabHost.TabSpec login = pencere.newTabSpec("Logın");
        login.setContent(R.id.login);
        login.setIndicator("Profıle");
        pencere.addTab(login);

        TabHost.TabSpec profile = pencere.newTabSpec("Profıle");
        profile.setContent(R.id.profile);
        profile.setIndicator("Profıle");
        pencere.addTab(profile);

        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String weight = sharedPref.getString("weight", "");
        if (weight == "") {
            curWeight = 0;
        } else {
            curWeight = Integer.parseInt(weight);
        }
        weightText = (EditText) findViewById(R.id.weightText);
        weightText.setHint(Integer.toString(curWeight));

        String name = sharedPref.getString("email", "");
        String pass = sharedPref.getString("password", "");


        if ((name == "") && (pass == "")) {
            pencere.clearAllTabs();
            pencere.addTab(workouts);
            pencere.addTab(myWorkout);
            pencere.addTab(login);
        } else {
            pencere.clearAllTabs();
            pencere.addTab(workouts);
            pencere.addTab(myWorkout);
            pencere.addTab(profile);
        }


        LinearLayout layout = (LinearLayout) findViewById(R.id.workouts);
        try
        {
            result.beforeFirst();
            while (result.next()) {
                Button btnTag = new Button(this);
                btnTag.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                btnTag.setText(result.getString("ProgramName") + " by " + result.getString("ProgramOwner"));
                layout.addView(btnTag);
                final String progId =result.getString("ProgramId");
                btnTag.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v)
                    {

                        Intent intent = new Intent(getBaseContext(), WorkoutIntro.class);
                        intent.putExtra("PROGID", progId);
                        startActivity(intent);

                    }
                });

            }
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String userId =sharedPref.getString("userId", "");
        layout = (LinearLayout) findViewById(R.id.myWorkout);
        if(!userId.isEmpty()){
            Log.d("efe",userId);
            try
            {
                Statement statement2 = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet myworkoutsRes= statement2.executeQuery("SELECT ProgramId FROM WorkoutsOfUsers where UserID ="+userId);
                while (myworkoutsRes.next()) {
                    Button btnWork = new Button(this);
                    btnWork.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                    btnWork.setText(myworkoutsRes.getString("ProgramId"));
                    Log.d("24",myworkoutsRes.getString("ProgramId"));
                   // layout.addView(btnWork); //NULLpOİNTEReXCEPTİON
                    final String progId =myworkoutsRes.getString("ProgramId");
                    btnWork.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v)
                        {

                            //Intent intent = new Intent(getBaseContext(), WorkoutIntro.class);
                            //intent.putExtra("PROGID", progId);
                            //startActivity(intent);

                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void logIn (View view){
        sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        EditText usernameField = (EditText) findViewById(R.id.usernameField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);

        Statement statement = null;
        try {
            statement = conn.createStatement( ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ResultSet loginRes = statement.executeQuery("select * from Programs ");
        } catch (SQLException e) {
            Log.e("ERROR", e.getMessage());
        }



        editor.putString("lastLoginDate",Calendar.YEAR+"-"+Calendar.MONTH+"-"+Calendar.DAY_OF_MONTH);
        editor.putString("userId","'e9bee8be-8897-42db-930d-892a4699f0dc'");
        editor.putString("email",usernameField.getText().toString());
        editor.putString("password",passwordField.getText().toString());
        editor.apply();
        finish();
        startActivity(getIntent());

        displayData(view);
    }



    public void displayData(View view){
        SharedPreferences sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        String name= sharedPref.getString("email","");
        String pass= sharedPref.getString("password","");
        String weight= sharedPref.getString("weight","");
        String loginTime = sharedPref.getString("lastLoginDate","");
        Toast.makeText(this,name+" "+pass+" "+weight+" "+loginTime,Toast.LENGTH_LONG).show();
    }

    public void logOut(View view){

        sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("email");
        editor.remove("password");
        editor.remove("userId");
        editor.apply();
        finish();
        startActivity(getIntent());
    }

    public void setWeight(View view){
        weightText =(EditText)findViewById(R.id.weightText) ;
        String wT = weightText.getText().toString();
        if(!wT.isEmpty()){
            curWeight = Integer.parseInt(wT);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(weightText.getWindowToken(),0);
            Toast.makeText(getApplicationContext(), wT, Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("weight",weightText.getText().toString());
            editor.apply();
        }
    }

    public void startSignUp(View view){
        Intent intent = new Intent(view.getContext(),SignUp.class);
        startActivityForResult(intent,0);
    }
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        conn = null;
        String ConnURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            ConnURL = "jdbc:jtds:sqlserver://192.168.1.21:1433/Fit4ever";
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

}


