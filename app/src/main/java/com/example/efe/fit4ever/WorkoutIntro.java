package com.example.efe.fit4ever;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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

import org.apache.poi.hssf.record.formula.functions.T;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import net.sourceforge.jtds.jdbc.UniqueIdentifier;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
    String progId;
    EditText usercomment;
    Calendar calendar = Calendar.getInstance();
    ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_workout_intro);
        Intent intent = getIntent();
        progId = intent.getStringExtra("PROGID");
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

        int five,four,three,two,one,addition= 0;
        float progratevalue = 0;
        try {
            result = statement.executeQuery("USE [Workout]  select * from [dbo].[Rates] where [dbo].[Rates].[ID] = '" + progId + "'");
            if (result.next())
            {
                one = Integer.parseInt(result.getString("One"));
                two = Integer.parseInt(result.getString("Two"));
                three = Integer.parseInt(result.getString("Three"));
                four = Integer.parseInt(result.getString("Four"));
                five = Integer.parseInt(result.getString("Five"));
                addition = one + two + three + four + five;
                if(addition==0){
                    addition++;
                }
                progratevalue = ((one * 1) + (two * 2) + (three * 3) + (four * 4) + (five * 5)) / addition;
            }

        } catch (SQLException e) {
            Log.e("ERRORrate", e.getMessage());
        }
        try {
            result = statement.executeQuery("select Title, Rate, Information, Subtitle, Username, Creator from Programs INNER JOIN Users on [dbo].[Users].[ID]=[dbo].[Programs].[Creator] AND" +
                    "[dbo].[Programs].[ID] = '" + progId + "'");
        } catch (SQLException e) {
            Log.e("ERRORc", e.getMessage());
        }
        try {
            assert result != null;
            while (result.next()) {
                progTitle.setText(result.getString("Title"));
                progowner.setText("Author: "+result.getString("Username"));
                ratingtext.setText(result.getString("Rate"));
                ratingBar.setRating(Float.parseFloat(result.getString("Rate")));
                description.setText(result.getString("Information"));
                subtitle.setText(result.getString("Subtitle"));

                final String creatorId= result.getString("Creator");
                progowner.setClickable(true);
                progowner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), TrainerProfile.class);
                        intent.putExtra("CREATOR",creatorId );
                        startActivity(intent);
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            statement.executeUpdate(" USE [Workout] UPDATE [dbo].[Programs] SET [dbo].[Programs].[ViewCount]=[dbo].[Programs].[ViewCount]+1" +
                    " , [dbo].[Programs].[Rate]= "+(int)progratevalue+" WHERE [dbo].[Programs].[ID]='"+progId+"'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String role = sharedPref.getString("role", "");
        userId = sharedPref.getString("userId", "");
        CheckBox idCheck=(CheckBox)findViewById(R.id.idBox);
        CheckBox infoCheck=(CheckBox)findViewById(R.id.infoBox);
        if (!role.equals("2")) {
            try {
                ResultSet tryWorkoutPurchase = statement.executeQuery("USE [Workout] SELECT * FROM [dbo].[UPRelation] where [UserID] ='" + userId + "' AND [ProgramId]='" + getIntent().getStringExtra("PROGID") + "'");
                if (!tryWorkoutPurchase.next()) {
                          idCheck.setVisibility(View.VISIBLE);
                          infoCheck.setVisibility(View.VISIBLE);

                      }else{
                          Button purchaseBtn=(Button)findViewById(R.id.purchaseBtn);
                          purchaseBtn.setText("Download");
                          idCheck.setVisibility(View.INVISIBLE);
                          infoCheck.setVisibility(View.INVISIBLE);
                          idCheck.setClickable(false);
                          infoCheck.setClickable(false);
                      }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }else{
            Button purchaseBtn=(Button)findViewById(R.id.purchaseBtn);
            purchaseBtn.setText("Download");
            idCheck.setVisibility(View.INVISIBLE);
            infoCheck.setVisibility(View.INVISIBLE);
            idCheck.setClickable(false);
            infoCheck.setClickable(false);
        }

        try {
            sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            userId = sharedPref.getString("userId", "");
            LinearLayout layout = (LinearLayout) findViewById(R.id.commentslayout1);
            ResultSet tryWorkoutOwner = statement.executeQuery("USE [Workout] SELECT * FROM [dbo].[UPRelation] where [UserID] ='" + userId + "' AND [ProgramId]='" + getIntent().getStringExtra("PROGID") + "'");
            if (tryWorkoutOwner.next()) {
                RatingBar ratingBar2 = new RatingBar(this);
                ratingBar2.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                ratingBar2.setMax(5);
                ratingBar2.setIsIndicator(false);
                layout.addView(ratingBar2);

                final TextView rateText2 = new TextView(this);
                rateText2.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT));
                layout.addView(rateText2);

                ratingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar2, float rating,
                                                boolean fromUser) {

                        rateText2.setText(String.valueOf(rating));
                        String rateColumn = null;
                        int rateval = (int)rating;
                        switch (rateval)
                        {
                            case 1: {  rateColumn ="One";  break; }
                            case 2: {  rateColumn ="Two";  break; }
                            case 3: {  rateColumn ="Three"; break; }
                            case 4: {  rateColumn ="Four";  break; }
                            case 5: {  rateColumn ="Five";  break; }

                        }
                        try {
                            statement.executeUpdate(" USE [Workout] UPDATE [dbo].[Rates] SET [dbo].[Rates].["+rateColumn+"]=[dbo].[Rates].["+rateColumn+"]+1 " +
                                    "WHERE [dbo].[Rates].[ID]='"+progId+"'");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                });
                View view2 = new View(this);
                view2.setBackgroundColor(0xFFC2BEBF);
                layout.addView(view2, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));

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
            result = statement.executeQuery("select [dbo].[Comments].[ID], [dbo].[Comments].[Comment], [dbo].[Comments].[UserID],[dbo].[UPRelation].[IDPermission], [dbo].[Users].[Username], [dbo].[Comments].[LikeCount] ,CAST([dbo].[Comments].[CreateDate] AS DATE)as CreateDate  " +
                    "from [dbo].[Comments] INNER JOIN [dbo].[Users] on  [dbo].[Comments].[IsActive]=1 and" +
                    "[dbo].[Comments].[ProgramID] = '"+progId+"' and [dbo].[Users].[ID]=[dbo].[Comments].[UserID] INNER JOIN [dbo].[UPRelation] on [dbo].[UPRelation].[ProgramID] = '"+progId+"' and [dbo].[Users].[ID]=[dbo].[UPRelation].[UserID] order by [dbo].[Comments].[CreateDate]");
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
                if(result.getString("IDPermission").equals("1")){
                    username.setText(result.getString("LikeCount")+"     "+ result.getString("Username"));
                }else{
                    username.setText(result.getString("LikeCount")+"     Anonymous");
                }
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
            /*ConnURL = "jdbc:jtds:sqlserver://fit4ever1.database.windows.net:1433/Workout;user=fit4ever1;password=Bvmguxg2" +
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

    public void Purchase(View view) {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        String uniqueID = UUID.randomUUID().toString();

        if(!userId.isEmpty()) {
            try {
                String role = sharedPref.getString("role", "");
                if (!role.equals("2")) {
                    ResultSet tryWorkoutPurchase = statement.executeQuery("USE [Workout] SELECT * FROM [dbo].[UPRelation] where [UserID] ='" + userId + "' AND [ProgramId]='" + getIntent().getStringExtra("PROGID") + "'");
                    if (!tryWorkoutPurchase.next()) {
                        int idP=0;
                        int infoP=0;
                        CheckBox idCheck=(CheckBox)findViewById(R.id.idBox);
                        CheckBox infoCheck=(CheckBox)findViewById(R.id.infoBox);
                        if(idCheck.isChecked()){
                            idP=1;
                        }
                        if(infoCheck.isChecked()){
                            infoP=1;
                        }
                        statement.executeUpdate(" USE [Workout] INSERT INTO [dbo].[UPRelation] ([ID] ,[ProgramID],[UserID],[RegisterDate],InfoPermission,IDPermission) VALUES" +
                                " ('" + uniqueID + "','" + getIntent().getStringExtra("PROGID") + "','" + userId + "','" + calendar.get(Calendar.YEAR) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH) + "',"+infoP+","+idP+" )");
                        statement.executeUpdate(" USE [Workout] UPDATE [dbo].[Programs] SET IsEditable=0 where ID='"+getIntent().getStringExtra("PROGID")+"'");
                        Toast.makeText(this, "Program purchased.", Toast.LENGTH_SHORT).show();

                        Download();


                    } else {
                        File logFile = new File(getExternalFilesDir(null),getIntent().getStringExtra("PROGID") + ".xls");
                        if(!logFile.exists()){
                            Download();
                        }else{
                            File programsFile = new File(getExternalFilesDir(null),"programs.xls");
                            FileInputStream myInput = null;
                            myInput = new FileInputStream(programsFile);
                            POIFSFileSystem myFileSystem = null;
                            myFileSystem = new POIFSFileSystem(myInput);
                            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                            int rowNumber = mySheet.getPhysicalNumberOfRows();
                            int i = 2;
                            CellReference idRef = new CellReference("A" + i);
                            CellReference progRef = new CellReference("L" + i);
                            Row row = mySheet.getRow(idRef.getRow());
                            Cell cell1 = row.getCell(idRef.getCol());
                            Cell cell2 = row.getCell(progRef.getCol());
                            while ((!cell1.toString().equals(getIntent().getStringExtra("PROGID")))&&(!cell2.toString().equals(userId))&&(i<=rowNumber)) {
                                i++;
                                idRef = new CellReference("A" + i);
                                progRef = new CellReference("L" + i);
                                row = mySheet.getRow(idRef.getRow());
                                cell1 = row.getCell(idRef.getCol());
                                cell2 = row.getCell(progRef.getCol());
                            }
                            if(i==rowNumber){
                                Download();
                            }else{
                                Toast.makeText(this, "This program already exists on your device.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                }else{
                    Download();
                }
            } catch (SQLException e) {
                Log.e("ERRORp", e.getMessage());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "You need to login to make purchases.", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(getBaseContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void Download() {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String userId = sharedPref.getString("userId", "");
        File logFile = new File(getExternalFilesDir(null),getIntent().getStringExtra("PROGID") + ".xls");
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

                    if(!workoutDownloadInfo.getString("Video").equals("rest.mp4")) {
                        String url = "http://192.168.1.23:11124/Assets/Videos/"+workoutDownloadInfo.getString("Video");
                        new WorkoutIntro.DownloadFileFromURL().execute(url);
                    }
                    Log.d("videoid", workoutDownloadInfo.getString("Video"));
                    row++;
                }
                os = new FileOutputStream(logFile);
                wb.write(os);

                Toast.makeText(this, "Download successful.", Toast.LENGTH_SHORT).show();
                os.close();
                AddToDevice();

            } catch (SQLException e) {
                Log.e("error download", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    public void AddToDevice(){
        try {
            ResultSet selectProgram = statement.executeQuery("USE [Workout] SELECT [dbo].[Programs].[ID],[dbo].[Programs].[Rate],[dbo].[Programs].[Creator]," +
                    " [dbo].[Users].[Username],[dbo].[Programs].[IsActive],[dbo].[Programs].[Title],[dbo].[Programs].[Information],[dbo].[Programs].[IsDeleted]," +
                    "[dbo].[Programs].[CreateDate],[dbo].[Programs].[ProgramLevel],[dbo].[Programs].[ProgramCategory]" +
                    " FROM [dbo].[Programs] INNER JOIN [Users] on [dbo].[Users].[ID]=[dbo].[Programs].[Creator] and " +
                    "[dbo].[Programs].[ID]='"+getIntent().getStringExtra("PROGID")+"'");

        File programsFile = new File(getExternalFilesDir(null).getAbsolutePath(),"programs.xls");
            SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        if(!programsFile.exists()) {
            Workbook myWorkBook = new HSSFWorkbook();
            Cell c = null;
            CellStyle cs = myWorkBook.createCellStyle();
            cs.setFillForegroundColor(HSSFColor.LIME.index);
            cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            Sheet sheet1 = null;
            sheet1 = myWorkBook.createSheet("Programs Infos");
            Row headerRow = sheet1.createRow(0);

            c = headerRow.createCell(0);
            c.setCellValue("ID");
            c.setCellStyle(cs);
            c = headerRow.createCell(1);
            c.setCellValue("Rate");
            c.setCellStyle(cs);
            c = headerRow.createCell(2);
            c.setCellValue("CreatorID");
            c.setCellStyle(cs);
            c = headerRow.createCell(3);
            c.setCellValue("CreatorName");
            c.setCellStyle(cs);
            c = headerRow.createCell(4);
            c.setCellValue("IsActive");
            c.setCellStyle(cs);
            c = headerRow.createCell(5);
            c.setCellValue("Title");
            c.setCellStyle(cs);
            c = headerRow.createCell(6);
            c.setCellValue("Information");
            c.setCellStyle(cs);
            c = headerRow.createCell(7);
            c.setCellValue("IsDeleted");
            c.setCellStyle(cs);
            c = headerRow.createCell(8);
            c.setCellValue("CreateDate");
            c.setCellStyle(cs);
            c = headerRow.createCell(9);
            c.setCellValue("ProgramLevel");
            c.setCellStyle(cs);
            c = headerRow.createCell(10);
            c.setCellValue("ProgramCategory");
            c.setCellStyle(cs);
            c = headerRow.createCell(11);
            c.setCellValue("UserID");
            c.setCellStyle(cs);
            sheet1.setColumnWidth(0, (15 * 500));
            sheet1.setColumnWidth(1, (15 * 500));
            sheet1.setColumnWidth(2, (15 * 500));
            sheet1.setColumnWidth(3, (15 * 500));
            sheet1.setColumnWidth(4, (15 * 500));
            sheet1.setColumnWidth(5, (15 * 500));
            sheet1.setColumnWidth(6, (15 * 500));
            sheet1.setColumnWidth(7, (15 * 500));
            sheet1.setColumnWidth(8, (15 * 500));
            sheet1.setColumnWidth(9, (15 * 500));
            sheet1.setColumnWidth(10, (15 * 500));
            sheet1.setColumnWidth(11, (15 * 500));

            FileOutputStream os = null;
            os = new FileOutputStream(programsFile);
            myWorkBook.write(os);
            os.close();
        }
            FileInputStream myInput = null;
            myInput = new FileInputStream(programsFile);
            POIFSFileSystem myFileSystem = null;
            myFileSystem = new POIFSFileSystem(myInput);
            HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
            HSSFSheet mySheet = myWorkBook.getSheetAt(0);
            int rowNumber = mySheet.getPhysicalNumberOfRows();
            FileOutputStream os = null;
            Cell c = null;
            while(selectProgram.next()) {
                Row dataRow = mySheet.createRow(rowNumber);
                c = dataRow.createCell(0);
                c.setCellValue(selectProgram.getString("ID"));
                c = dataRow.createCell(1);
                c.setCellValue(selectProgram.getString("Rate"));
                c = dataRow.createCell(2);
                c.setCellValue(selectProgram.getString("Creator"));
                c = dataRow.createCell(3);
                c.setCellValue(selectProgram.getString("Username"));
                c = dataRow.createCell(4);
                c.setCellValue(selectProgram.getString("IsActive"));
                c = dataRow.createCell(5);
                c.setCellValue(selectProgram.getString("Title"));
                c = dataRow.createCell(6);
                c.setCellValue(selectProgram.getString("Information"));
                c = dataRow.createCell(7);
                c.setCellValue(selectProgram.getString("IsDeleted"));
                c = dataRow.createCell(8);
                c.setCellValue(selectProgram.getString("CreateDate"));
                c = dataRow.createCell(9);
                c.setCellValue(selectProgram.getString("ProgramLevel"));
                c = dataRow.createCell(10);
                c.setCellValue(selectProgram.getString("ProgramCategory"));
                c = dataRow.createCell(11);
                c.setCellValue(sharedPref.getString("userId", ""));
            }
            os = new FileOutputStream(programsFile);
            myWorkBook.write(os);
            os.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                String video = url.getFile().toString();
                video= video.replace("/Assets/Videos/","");
               Log.d("fileanmevid",video);
                OutputStream output = new FileOutputStream("/storage/emulated/0/Android/data/com.example.efe.fit4ever/files/"+video);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            removeDialog(progress_bar_type);
        }

    }
}
