package com.example.efe.fit4ever;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TrainerProfile extends AppCompatActivity {
    Connection conn;
    Statement statement = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_profile);

        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo inet = conMgr.getActiveNetworkInfo();
        if (inet != null) {
            CONN();
            try {
                statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        TextView nameText = (TextView)findViewById(R.id.nameText);
        TextView usernameText = (TextView)findViewById(R.id.usernameText);
        TextView emailText = (TextView)findViewById(R.id.emailText);
        TextView birthText = (TextView)findViewById(R.id.birthText);
        TextView description = (TextView)findViewById(R.id.description2);
        ImageView profileimg = (ImageView) findViewById(R.id.imageView2);
        ResultSet result = null;

        try {
            result = statement.executeQuery("select Username, Name, Surname, Email, TrainerInfo, ImageUrl,CAST(Birthdate AS DATE)as Birthdate from Users where [dbo].[Users].[ID] = '"+getIntent().getStringExtra("CREATOR")+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            while (result.next()){
                nameText.setText(result.getString("Name")+" "+result.getString("Surname"));
                usernameText.setText(result.getString("Username"));
                emailText.setText(result.getString("Email"));
                birthText.setText(result.getString("Birthdate"));
                description.setText(result.getString("TrainerInfo"));
                if(result.getString("ImageUrl")==null){
                    profileimg.setImageResource(R.drawable.profile);
                }else{
                    URL newurl = new URL("http://192.168.1.23:11124/Assets/ProfileImages/"+result.getString("ImageUrl"));
                    profileimg.setImageBitmap(BitmapFactory.decodeStream(newurl.openConnection() .getInputStream()));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
          /*  ConnURL = "jdbc:jtds:sqlserver://fit4ever1.database.windows.net:1433/Workout;user=fit4ever1;password=Bvmguxg2" +
                    ";encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            conn = DriverManager.getConnection(ConnURL, "fit4ever1", "Bvmguxg2");*/
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
}
