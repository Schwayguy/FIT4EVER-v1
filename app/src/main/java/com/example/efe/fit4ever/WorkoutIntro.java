package com.example.efe.fit4ever;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

import static android.widget.RelativeLayout.*;
import static com.example.efe.fit4ever.R.drawable.like;

public class WorkoutIntro extends AppCompatActivity {
    Connection conn;
    Statement statement = null;
    String userId;
    EditText usercomment;
    Calendar calendar = Calendar.getInstance();

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_intro);
        Intent intent = getIntent();
        String progId = intent.getStringExtra("PROGID");
        CONN();
        TextView progTitle = (TextView) findViewById(R.id.progTitleText);
        TextView progowner = (TextView) findViewById(R.id.progOwnerText);
        TextView ratingtext = (TextView) findViewById(R.id.ratingText1);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        TextView description = (TextView) findViewById(R.id.description);
        TextView subtitle = (TextView) findViewById(R.id.progSubtitleText);
        try {
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ResultSet result = null;
        try {
            result = statement.executeQuery("select Title, Rate, Information, Subtitle, Name, Surname from Programs INNER JOIN Users on [dbo].[Users].[ID]=[dbo].[Programs].[Creator] AND" +
                    "[dbo].[Programs].[ID] = '" + progId + "'");
        } catch (SQLException e) {
            Log.e("ERRORc", e.getMessage());
        }
        try {
            assert result != null;
            while (result.next()) {
                progTitle.setText(result.getString("Title"));
                progowner.setText("Author: "+result.getString("Name")+" "+result.getString("Surname"));
                ratingtext.setText(result.getString("Rate"));
                ratingBar.setRating(Float.parseFloat(result.getString("Rate")));
                description.setText(result.getString("Information"));
                subtitle.setText(result.getString("Subtitle"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            userId = sharedPref.getString("userId", "");
            LinearLayout layout = (LinearLayout) findViewById(R.id.commentslayout1);
            Log.d("çıkmadı",userId);
            ResultSet tryWorkoutOwner = statement.executeQuery("USE [Workout] SELECT * FROM [dbo].[UPRelation] where [UserID] ='" + userId + "' AND [ProgramId]='" + getIntent().getStringExtra("PROGID") + "'");
            if (tryWorkoutOwner.next()) {
                Log.d("çıkmadı",userId);
                usercomment = new EditText(this);
                usercomment.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, 300));
                usercomment.setPadding(0,10,0,0);
                layout.addView(usercomment);

                Button btnSend = new Button(this);
                btnSend.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                btnSend.setGravity(Gravity.RIGHT);
                btnSend.setText("Send");
                layout.addView(btnSend);
                btnSend.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                            String userId = sharedPref.getString("userId", "");

                            String  uniqueID = UUID.randomUUID().toString();
                            statement.executeUpdate(" USE [Workout] INSERT INTO [dbo].[Comments] " +
                                    "([ID],[Comment],[UserID],[WorkoutID],[LikeCount],[ProgramID],[IsActive],[CreateDate] )\n" +
                                    "   VALUES('"+uniqueID+"','"+usercomment.getText().toString()+"','"+userId+"',null,"+0+",'"+getIntent().getStringExtra("PROGID")+"',"+1+",'"+calendar.get(Calendar.YEAR) + "-" + String.valueOf(calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "')");

                            Toast.makeText(getApplicationContext(),"Comment sent.",Toast.LENGTH_SHORT).show();

                            finish();
                            startActivity(getIntent());

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }


                    }
                });

            }
        } catch (SQLException e) {
                Log.e("ERRORc", e.getMessage());
            }
        try {
            result = statement.executeQuery("select [dbo].[Comments].[ID], [dbo].[Comments].[Comment], [dbo].[Comments].[UserID], [dbo].[Users].[Username], [dbo].[Comments].[LikeCount] ,[dbo].[Comments].[CreateDate]  " +
                    "from [dbo].[Comments] INNER JOIN [dbo].[Users] on  [dbo].[Comments].[IsActive]=1 and" +
                    "[dbo].[Comments].[ProgramID] = '"+progId+"' and [dbo].[Users].[ID]=[dbo].[Comments].[UserID] order by [dbo].[Comments].[CreateDate]");
        } catch (SQLException e) {
            Log.e("ERRORc", e.getMessage());
        }
        try {
            LinearLayout layout = (LinearLayout) findViewById(R.id.commentslayout1);
            assert result != null;
            while (result.next()) {
                TextView comment = new TextView(this);
                comment.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                comment.setText(result.getString("Comment"));
                comment.setPadding(0,10,0,0);
                layout.addView(comment);

                TextView date = new TextView(this);
                date.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                date.setText(result.getString("CreateDate"));
                date.setPadding(0,5,0,0);
                layout.addView(date);

                TextView username = new TextView(this);
                username.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                username.setText(result.getString("LikeCount")+"     "+ result.getString("Username"));
                username.setGravity(Gravity.RIGHT);
                username.setPadding(0,5,0,0);
                layout.addView(username);

                final TextView commentid = new TextView(this);
                commentid.setLayoutParams(new ActionBar.LayoutParams(0,0));
                commentid.setText(result.getString("ID"));
                commentid.setVisibility(View.INVISIBLE);
                layout.addView(commentid);

                Button likeBtn = new Button(this);
                likeBtn.setLayoutParams(new ActionBar.LayoutParams(100,100));
                likeBtn.setGravity(Gravity.CENTER);
                likeBtn.setBackgroundResource(R.drawable.like);
                layout.addView(likeBtn);
                likeBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            statement.executeUpdate(" USE [Workout] UPDATE [dbo].[Comments] SET [dbo].[Comments].[LikeCount]=[dbo].[Comments].[LikeCount]+1 " +
                                    "WHERE [dbo].[Comments].[ID]='"+commentid.getText().toString()+"'");
                            finish();
                            startActivity(getIntent());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Button dislikeBtn = new Button(this);
                dislikeBtn.setLayoutParams(new ActionBar.LayoutParams(100,100));
                dislikeBtn.setGravity(Gravity.LEFT);
                dislikeBtn.setBackgroundResource(R.drawable.dislike);
                layout.addView(dislikeBtn);
                dislikeBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        try {
                            statement.executeUpdate(" USE [Workout] UPDATE [dbo].[Comments] SET [dbo].[Comments].[LikeCount]=[dbo].[Comments].[LikeCount]-1 " +
                                    "WHERE [dbo].[Comments].[ID]='"+commentid.getText().toString()+"'");
                            finish();
                            startActivity(getIntent());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
                View view2 = new View(this);
                view2.setBackgroundColor(0xFFC2BEBF);
                layout.addView(view2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));


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
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        String uniqueID = UUID.randomUUID().toString();

        if(!userId.isEmpty()) {
            try {
                ResultSet tryWorkoutPurchase = statement.executeQuery("USE [Workout] SELECT * FROM [dbo].[UPRelation] where [UserID] ='" + userId + "' AND [ProgramId]='" + getIntent().getStringExtra("PROGID") + "'");
                if (!tryWorkoutPurchase.next()) {
                    statement.executeUpdate(" USE [Workout] INSERT INTO [dbo].[UPRelation] ([ID] ,[ProgramID],[UserID],[RegisterDate]) VALUES" +
                            " ('" + uniqueID + "','" + getIntent().getStringExtra("PROGID") + "','" + userId + "','" + calendar.get(Calendar.YEAR) + "-" +  String.valueOf(calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "')");
                    Toast.makeText(this, "Program purchased.", Toast.LENGTH_SHORT).show();

                    Download();
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
        }else {
            try {
                ResultSet workoutDownloadInfo = statement.executeQuery("USE [Workout] SELECT  WorkoutID , Name, Video, Information ,Rate, Title ,Subtitle , Period ,Repeat, Queue" +
                        " FROM [dbo].[Workouts] INNER JOIN [dbo].[PWRelation] on [dbo].[Workouts].[ID]=[dbo].[PWRelation].[WorkoutID] AND " +
                        "[dbo].[PWRelation].[IsActive]=1 AND[ProgramID]='" + getIntent().getStringExtra("PROGID") + "' order by Queue");

                Workbook wb = new HSSFWorkbook();
                Cell c = null;
                CellStyle cs = wb.createCellStyle();
                cs.setFillForegroundColor(HSSFColor.LIME.index);
                cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                Sheet sheet1 = null;
                sheet1 = wb.createSheet("Workout Sheet");

                Row headerRow = sheet1.createRow(0);

                c = headerRow.createCell(0);
                c.setCellValue("WorkoutID");
                c.setCellStyle(cs);

                c = headerRow.createCell(1);
                c.setCellValue("Name");
                c.setCellStyle(cs);

                c = headerRow.createCell(2);
                c.setCellValue("Video");
                c.setCellStyle(cs);

                c = headerRow.createCell(3);
                c.setCellValue("Information");
                c.setCellStyle(cs);

                c = headerRow.createCell(4);
                c.setCellValue("Rate");
                c.setCellStyle(cs);

                c = headerRow.createCell(5);
                c.setCellValue("Title");
                c.setCellStyle(cs);

                c = headerRow.createCell(6);
                c.setCellValue("Subtitle");
                c.setCellStyle(cs);

                c = headerRow.createCell(7);
                c.setCellValue("Period");
                c.setCellStyle(cs);

                c = headerRow.createCell(8);
                c.setCellValue("Repeat");
                c.setCellStyle(cs);

                c = headerRow.createCell(9);
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
                sheet1.setColumnWidth(9, (15 * 200));

                FileOutputStream os = null;
                int row = 1;
                while (workoutDownloadInfo.next()) {
                    Row dataRow = sheet1.createRow(row);

                    c = dataRow.createCell(0);
                    c.setCellValue(workoutDownloadInfo.getString("WorkoutID"));
                    c = dataRow.createCell(1);
                    c.setCellValue(workoutDownloadInfo.getString("Name"));
                    c = dataRow.createCell(2);
                    c.setCellValue(workoutDownloadInfo.getString("Video"));
                    c = dataRow.createCell(3);
                    c.setCellValue(workoutDownloadInfo.getString("Information"));
                    c = dataRow.createCell(4);
                    c.setCellValue(Float.toString(workoutDownloadInfo.getFloat("Rate")));
                    c = dataRow.createCell(5);
                    c.setCellValue(workoutDownloadInfo.getString("Title"));
                    c = dataRow.createCell(6);
                    c.setCellValue(workoutDownloadInfo.getString("Subtitle"));
                    c = dataRow.createCell(7);
                    c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Period")));
                    c = dataRow.createCell(8);
                    c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Repeat")));
                    c = dataRow.createCell(9);
                    c.setCellValue(Integer.toString(workoutDownloadInfo.getInt("Queue")));

                /*
                URL url = new URL("http://192.168.1.23:11124/"+workoutDownloadInfo.getString("Video"));

                //Open a connection to that URL.
                URLConnection ucon = url.openConnection();

                File file = new File("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/"+workoutDownloadInfo.getString("Video"));
                InputStream is = ucon.getInputStream();
                BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
                FileOutputStream outStream = new FileOutputStream(file);
                byte[] buff = new byte[5 * 1024];

                //Read bytes (and store them) until there is nothing more to read(-1)
                int len;
                while ((len = inStream.read(buff)) != -1)
                {
                    outStream.write(buff,0,len);
                }

                //clean up
                outStream.flush();
                outStream.close();
                inStream.close();
*/

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
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo inet = conMgr.getActiveNetworkInfo();
        if (inet != null) {
            // layout oluştur sqlqueryden sırasıyla comment ekle
            //user comment en üstte olsun
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
