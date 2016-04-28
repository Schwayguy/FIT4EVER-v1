package com.example.efe.fit4ever;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;




public class MainActivity extends AppCompatActivity {
    int curWeight ;
    EditText weightText;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

        //Sonra ayır

        TabHost.TabSpec profile = pencere.newTabSpec("Profıle");
        profile.setContent(R.id.profile);
        profile.setIndicator("Profıle");
        pencere.addTab(profile);

        sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        String weight= sharedPref.getString("weight","");
        if(weight==""){
            curWeight=0;
        }else{
            curWeight =Integer.parseInt(weight);
        }
        weightText =(EditText)findViewById(R.id.weightText) ;
        weightText.setHint(Integer.toString(curWeight));

        String name= sharedPref.getString("username","");
        String pass= sharedPref.getString("password","");

        if((name=="")&&(pass=="")) {
            pencere.clearAllTabs();
            pencere.addTab(workouts);
            pencere.addTab(myWorkout);
            pencere.addTab(login);
        }else {
            pencere.clearAllTabs();
            pencere.addTab(workouts);
            pencere.addTab(myWorkout);
            pencere.addTab(profile);
        }

    }

    public void logIn (View view){
        sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        EditText usernameField = (EditText) findViewById(R.id.usernameField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);
        editor.putString("lastLoginDate",now.toString());
        editor.putString("username",usernameField.getText().toString());
        editor.putString("password",passwordField.getText().toString());
        editor.apply();
        finish();
        startActivity(getIntent());

        displayData(view);
    }



    public void displayData(View view){
        SharedPreferences sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        String name= sharedPref.getString("username","");
        String pass= sharedPref.getString("password","");
        String weight= sharedPref.getString("weight","");
        String loginTime = sharedPref.getString("lastLoginDate","");
        Toast.makeText(this,name+" "+pass+" "+weight+" "+loginTime,Toast.LENGTH_LONG).show();
    }

    public void logOut(View view){
        sharedPref = getSharedPreferences("userInfo",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("username");
        editor.remove("password");
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
}


