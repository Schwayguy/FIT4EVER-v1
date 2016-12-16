package com.example.efe.fit4ever;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.text.format.Time;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MainActivity extends AppCompatActivity {
    int curWeight;
    EditText weightText;
    SharedPreferences sharedPref;
    Connection conn;
    int i;
    int j;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo inet = conMgr.getActiveNetworkInfo();
        ResultSet result = null;
        Statement statement = null;
        if (inet != null) {
            CONN();
            try {
                statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                e.printStackTrace();            }

            try {
                result = statement.executeQuery("select * from Programs ");
            } catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            }
        }else{
            Toast.makeText(this,"Starting in offline mode.",Toast.LENGTH_SHORT).show();
        }
        TabHost pencere = (TabHost) findViewById(R.id.tabHost);
        pencere.setup();

        TabHost.TabSpec workouts = pencere.newTabSpec("Workouts");
        workouts.setContent(R.id.workouts);
        workouts.setIndicator("Workouts");

        TabHost.TabSpec myWorkout = pencere.newTabSpec("My Workout");
        myWorkout.setContent(R.id.myWorkout);
        myWorkout.setIndicator("My Workout");

        TabHost.TabSpec login = pencere.newTabSpec("Logın");
        login.setContent(R.id.login);
        login.setIndicator("Logın");

        TabHost.TabSpec profile = pencere.newTabSpec("Profıle");
        profile.setContent(R.id.profile);
        profile.setIndicator("Profıle");

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

        pencere.setCurrentTab(0);

        LinearLayout layout = (LinearLayout) findViewById(R.id.workouts);
        if(inet!=null)
        try {
            result.beforeFirst();
            while (result.next()) {
                Button btnTag = new Button(this);
                btnTag.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                btnTag.setText(result.getString("Title") );
                layout.addView(btnTag);
                final String progId = result.getString("ID");
                btnTag.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

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
        String userId = sharedPref.getString("userId", "");


        pencere.setCurrentTab(1);
        layout = (LinearLayout) findViewById(R.id.myWorkout);

        if (!userId.isEmpty()) {
            Log.d("efe", userId);
            try {
                Statement statement2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                final ResultSet myworkoutsRes = statement2.executeQuery("SELECT ProgramID FROM UPRelation where UserID ='" + userId+"'");
                while (myworkoutsRes.next()) {
                    Button btnWorks = new Button(this);
                    btnWorks.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                        btnWorks.setText(myworkoutsRes.getString("ProgramID"));
                        Log.d("24", myworkoutsRes.getString("ProgramID"));
                        layout.addView(btnWorks);
                        final String progId = myworkoutsRes.getString("ProgramID");

                        btnWorks.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                Intent intent = new Intent(getBaseContext(), MyWorkout.class);
                                intent.putExtra("PROGID", progId);
                                startActivity(intent);

                            }
                        });

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            TextView txt = new TextView(this);
            txt.setText("Login to see your workouts");
            txt.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
            layout.addView(txt);
        }

        pencere.setCurrentTab(0);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void logIn(View view) throws NoSuchAlgorithmException {
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        EditText usernameField = (EditText) findViewById(R.id.usernameField);
        EditText passwordField = (EditText) findViewById(R.id.passwordField);


        String password= passwordField.getText().toString();

        MessageDigest md = MessageDigest.getInstance("SHA-1");

        byte[] result = md.digest(password.getBytes());
        StringBuffer passbuffer = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            passbuffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo inet = conMgr.getActiveNetworkInfo();
        if (inet != null) {

            Statement statement = null;
         try {
              statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
         } catch (SQLException e) {
              e.printStackTrace();
           }
            try {
             ResultSet loginRes = statement.executeQuery("select * from Users where Email ='"+usernameField.getText().toString()+"' AND Password='"+ passbuffer.toString()+"'");
             if(!loginRes.next()){
              Toast.makeText(this,"Wrong email or password.",Toast.LENGTH_SHORT).show();
             }else {
                  editor.putString("lastLoginDate", Calendar.YEAR + "-" + Calendar.MONTH + "-" + Calendar.DAY_OF_MONTH);
                  editor.putString("userId", loginRes.getString("ID"));
                  editor.putString("email", usernameField.getText().toString());
                  editor.putString("password", passwordField.getText().toString());
                  editor.apply();
                  finish();
                  startActivity(getIntent());
                  displayData(view);
             }
         } catch (SQLException e) {
             Log.e("ERROR", e.getMessage());
             Toast.makeText(this,"You need internet connection to login.",Toast.LENGTH_SHORT).show();
         }
        }else{

        }

    }


    public void displayData(View view) {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String name = sharedPref.getString("email", "");
        String pass = sharedPref.getString("password", "");
        String weight = sharedPref.getString("weight", "");
        String loginTime = sharedPref.getString("lastLoginDate", "");
        Toast.makeText(this, name + " " + pass + " " + weight + " " + loginTime, Toast.LENGTH_LONG).show();
    }

    public void logOut(View view) {

        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("email");
        editor.remove("password");
        editor.remove("userId");
        editor.apply();
        finish();
        startActivity(getIntent());
    }

    public void setWeight(View view) {
        weightText = (EditText) findViewById(R.id.weightText);
        String wT = weightText.getText().toString();
        Statement statement = null;
        try {
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!wT.isEmpty()) {
            try {
                statement.executeUpdate("update Users set Weight='"+wT+"' where ID ="+sharedPref.getString("userId", ""));
            }
            catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            }
            curWeight = Integer.parseInt(wT);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(weightText.getWindowToken(), 0);
            Toast.makeText(getApplicationContext(), wT, Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("weight", weightText.getText().toString());
            editor.apply();
        }
    }

    public void startSignUp(View view) {
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo inet = conMgr.getActiveNetworkInfo();
        if (inet != null) {
            Intent intent = new Intent(view.getContext(), SignUp.class);
            startActivityForResult(intent, 0);
        }else{
            Toast.makeText(this,"You need internet connection to signup.",Toast.LENGTH_SHORT).show();
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
            conn = DriverManager.getConnection(ConnURL, "efe", "e1234567");
            System.out.println("connected");
        } catch (SQLException se) {
            Log.e("ERROr1", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERROr2", e.getMessage());
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong while trying to connect to the database, please check your internet connection.", Toast.LENGTH_LONG).show();
        }
        return conn;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.efe.fit4ever/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.efe.fit4ever/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}


