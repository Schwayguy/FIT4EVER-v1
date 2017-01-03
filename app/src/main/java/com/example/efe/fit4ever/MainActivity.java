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

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    int curWeight;
    EditText weightText;
    TextView bmiText;
    SharedPreferences sharedPref;
    Connection conn;
    Calendar calendar = Calendar.getInstance();
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
                result = statement.executeQuery(" select * from Programs where IsActive=1 ");
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

        weightText = (EditText) findViewById(R.id.weightText);
        weightText.setHint(weight);

        String height = sharedPref.getString("height", "");
        bmiText = (TextView) findViewById(R.id.bmitext);
        if(!height.isEmpty()) {
            float bmi = Float.parseFloat(weight) * 10000 / (Float.parseFloat(height) * Float.parseFloat(height));
            bmiText.setText("Your current BMI: " + bmi);
        }

        TextView username =(TextView) findViewById(R.id.usernameprofile);
        username.setText( sharedPref.getString("username", ""));

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
            assert result != null;
            result.beforeFirst();
            while (result.next()) {
                Button btnTag = new Button(this);
                btnTag.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                btnTag.setText(result.getString("Title") );
                btnTag.setPadding(0,10,0,0);
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

        if ((!userId.isEmpty())&&(inet!=null)) {
            Log.d("efe", userId);
            try {
                Statement statement2 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                final ResultSet myworkoutsRes = statement2.executeQuery("SELECT ProgramID, Title FROM [dbo].[Programs] INNER JOIN [dbo].[UPRelation] on " +
                        "[dbo].[Programs].[ID]=[dbo].[UPRelation].[ProgramID] and [dbo].[UPRelation].[UserID] ='" + userId+"'");
                while (myworkoutsRes.next()) {
                    Button btnWorks = new Button(this);
                    btnWorks.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                        btnWorks.setText(myworkoutsRes.getString("Title"));
                        Log.d("24", myworkoutsRes.getString("ProgramID"));
                        btnWorks.setPadding(0,10,0,0);
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

        if(inet!= null) {

            FileInputStream myInput = null;
            File weightFile = new File(getExternalFilesDir(null), sharedPref.getString("userId", "") + ".xls");
            if (weightFile.exists()) {
                try {
                    myInput = new FileInputStream(weightFile);
                    POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                    HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                    HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                    int rowNumber = mySheet.getPhysicalNumberOfRows();
                    int i;
                    for (i = 2; i <= rowNumber; i++) {
                        CellReference cellReference = new CellReference("E" + i);
                        Row row = mySheet.getRow(cellReference.getRow());
                        Cell cell = row.getCell(cellReference.getCol());
                        if (cell.toString().equals("0.0")) {
                            CellReference weightRef = new CellReference("A" + i);
                            CellReference recordRef = new CellReference("B" + i);
                            CellReference progidRef = new CellReference("C" + i);

                            Cell cell1 = row.getCell(weightRef.getCol());
                            Cell cell2 = row.getCell(recordRef.getCol());
                            Cell cell3 = row.getCell(progidRef.getCol());
                            if (!cell3.toString().isEmpty()) {
                                String uniqueID = UUID.randomUUID().toString();
                                Statement statement3 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                statement3.executeUpdate(" USE [Workout] INSERT INTO [dbo].[UserWeightChangeLog] ([ID]\n" +
                                        "      ,[UserId]\n" +
                                        "      ,[WeightLoss]\n" +
                                        "      ,[RecordDate]\n" +
                                        "      ,[ProgramID])" +
                                        "VALUES('" + uniqueID + "','" + sharedPref.getString("userId", "") + "'," + cell1.toString() + ",'" + cell2.toString() + "','" + cell3.toString() + "')");

                                cell.setCellValue(1);
                            }
                        }
                        FileOutputStream os = null;
                        os = new FileOutputStream(weightFile);
                        myWorkBook.write(os);
                        os.close();
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }        // ATTENTION: This was auto-generated to implement the App Indexing API.
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
                  editor.putString("lastLoginDate", calendar.get(Calendar.YEAR) + "-" +  String.valueOf(calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
                  editor.putString("userId", loginRes.getString("ID"));
                  editor.putString("username", loginRes.getString("Username"));
                  editor.putString("email", usernameField.getText().toString());
                  editor.putString("password", passwordField.getText().toString());
                  editor.putString("weight", loginRes.getString("Weight"));
                  editor.putString("height", loginRes.getString("Height"));
                  editor.apply();


                 File weightFile = new File(getExternalFilesDir(null).getAbsolutePath(),loginRes.getString("ID") + ".xls");
                 if(!weightFile.exists()) {
                     Workbook wb = new HSSFWorkbook();
                     Cell c = null;
                     CellStyle cs = wb.createCellStyle();
                     cs.setFillForegroundColor(HSSFColor.LIME.index);
                     cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                     Sheet sheet1 = null;
                     sheet1 = wb.createSheet("Weight Changes");

                     Row headerRow = sheet1.createRow(0);

                     c = headerRow.createCell(0);
                     c.setCellValue("WeightLoss");
                     c.setCellStyle(cs);

                     c = headerRow.createCell(1);
                     c.setCellValue("RecordDate");
                     c.setCellStyle(cs);

                     c = headerRow.createCell(2);
                     c.setCellValue("ProgramID");
                     c.setCellStyle(cs);

                     c = headerRow.createCell(3);
                     c.setCellValue("ProgramName");
                     c.setCellStyle(cs);

                     c = headerRow.createCell(4);
                     c.setCellValue("IsSent");
                     c.setCellStyle(cs);


                     sheet1.setColumnWidth(0, (15 * 500));
                     sheet1.setColumnWidth(1, (15 * 500));
                     sheet1.setColumnWidth(2, (15 * 500));
                     sheet1.setColumnWidth(3, (15 * 500));
                     sheet1.setColumnWidth(4, (15 * 500));

                     ResultSet userHistoryInfo= statement.executeQuery("USE [Workout] SELECT WeightLoss,RecordDate, ProgramID,Title from [UserWeightChangeLog] INNER JOIN [Programs] on UserID='"+loginRes.getString("ID")+"'" +
                             "and [dbo].[Programs].[ID]=[dbo].[UserWeightChangeLog].[ProgramID] ");
                     int row = 1;
                     while (userHistoryInfo.next()) {
                         Row dataRow = sheet1.createRow(row);

                         c = dataRow.createCell(0);
                         c.setCellValue(userHistoryInfo.getString("WeightLoss"));
                         c = dataRow.createCell(1);
                         c.setCellValue(userHistoryInfo.getString("RecordDate"));
                         c = dataRow.createCell(2);
                         c.setCellValue(userHistoryInfo.getString("ProgramID"));
                         c = dataRow.createCell(3);
                         c.setCellValue(userHistoryInfo.getString("Title"));
                         c = dataRow.createCell(4);
                         c.setCellValue(1);
                         row++;
                     }
                     FileOutputStream os = null;
                     os = new FileOutputStream(weightFile);
                     wb.write(os);

                 }else{
                     // Toast.makeText(this,getExternalFilesDir(null).getAbsolutePath(),Toast.LENGTH_SHORT).show();
                 }



                 finish();
                  startActivity(getIntent());
               //   displayData(view);
             }
         } catch (SQLException e) {
             Log.e("ERROR", e.getMessage());
             Toast.makeText(this,"You need internet connection to login.",Toast.LENGTH_SHORT).show();
         } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
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
        editor.remove("username");
        editor.remove("progId");
        editor.remove("progname");
        editor.remove("height");
        editor.apply();
        finish();
        startActivity(getIntent());
    }

    public void SeeHistory(View view){
        Intent intent = new Intent(getBaseContext(), WorkoutHistory.class);
        startActivity(intent);
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
                statement.executeUpdate("update Users set Weight="+wT+" where ID ='"+sharedPref.getString("userId", "")+"'");

                File weightFile = new File(getExternalFilesDir(null), sharedPref.getString("userId", "")+ ".xls");
                if(weightFile.exists()) {
                    FileInputStream myInput = null;
                    myInput = new FileInputStream(weightFile);
                    POIFSFileSystem myFileSystem = null;
                    myFileSystem = new POIFSFileSystem(myInput);
                    HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                    final HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                    final int rowNumber = mySheet.getPhysicalNumberOfRows();
                    FileOutputStream os = null;
                    Cell c = null;
                    Row dataRow = mySheet.createRow(rowNumber);
                    c = dataRow.createCell(0);
                    c.setCellValue(wT);
                    c = dataRow.createCell(1);
                    c.setCellValue(calendar.get(Calendar.YEAR) + "-" +  String.valueOf(calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
                    c = dataRow.createCell(2);
                    c.setCellValue(sharedPref.getString("progId", ""));
                    c = dataRow.createCell(3);
                    c.setCellValue(sharedPref.getString("progname", ""));
                    c = dataRow.createCell(4);
                    c.setCellValue(0);

                    os = new FileOutputStream(weightFile);
                    myWorkBook.write(os);
                    os.close();
                }
            }
            catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            curWeight = Integer.parseInt(wT);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(weightText.getWindowToken(), 0);
            Toast.makeText(getApplicationContext(), wT, Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("weight", weightText.getText().toString());
            editor.apply();

            finish();
            startActivity(getIntent());

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
           /* ConnURL = "jdbc:jtds:sqlserver://fit4ever1.database.windows.net:1433/Workout;user=fit4ever1;password=Bvmguxg2" +
                    ";encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
            conn = DriverManager.getConnection(ConnURL, "fit4ever1", "Bvmguxg2");*/
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


