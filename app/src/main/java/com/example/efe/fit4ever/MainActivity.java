package com.example.efe.fit4ever;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
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

import static com.example.efe.fit4ever.R.id.heightText;


public class MainActivity extends AppCompatActivity {
    int curWeight;
    SharedPreferences sharedPref;
    Connection conn;
    Calendar calendar = Calendar.getInstance();
    Statement statement = null;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo inet = conMgr.getActiveNetworkInfo();
        ResultSet result = null;

        if (inet != null) {
            CONN();
            try {
                statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                e.printStackTrace();            }


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

        FileInputStream myInput = null;
        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        File weightFile = new File(getExternalFilesDir(null), sharedPref.getString("userId", "") + ".xls");
        LinearLayout layout = (LinearLayout) findViewById(R.id.historyvertical);
        if (weightFile.exists()) {
            try {
                myInput = new FileInputStream(weightFile);
                POIFSFileSystem myFileSystem = null;
                myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = null;
                myWorkBook = new HSSFWorkbook(myFileSystem);
                HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                int rowNumber = mySheet.getPhysicalNumberOfRows();
                CellReference weightRef = new CellReference("A" + rowNumber);
                CellReference heightRef = new CellReference("H" + rowNumber);
                Row row = mySheet.getRow(weightRef.getRow());
                Cell cell1 = row.getCell(weightRef.getCol());
                Cell cell2 = row.getCell(heightRef.getCol());
                String weight = cell1.toString();
                String height = cell2.toString();
                TextView bmiText = (TextView) findViewById(R.id.bmitext);
                float bmi=0;
                if((!height.equals("0")||(!height.equals(""))||(!weight.equals("")))){
                    bmi = Float.parseFloat(weight) * 10000 / (Float.parseFloat(height) * Float.parseFloat(height));
                }
                bmiText.setText("Your current BMI: " + bmi);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        EditText weightText = (EditText) findViewById(R.id.weightText);
        EditText fatText = (EditText) findViewById(R.id.fatText);
        EditText muscleText = (EditText) findViewById(R.id.muscleText);
        EditText heightText = (EditText) findViewById(R.id.heightText);
        TextView username =(TextView) findViewById(R.id.usernameprofile);
        username.setText( sharedPref.getString("username", ""));



        String name = sharedPref.getString("email", "");
        String pass = sharedPref.getString("password", "");

        final Spinner classspinner;
        final Spinner levelspinner;
        final Spinner ratespinner;
        final Spinner searchspinner;
        ArrayAdapter<CharSequence> adapter;
        classspinner = (Spinner) findViewById(R.id.classspinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.classspinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        classspinner.setAdapter(adapter);
        levelspinner = (Spinner) findViewById(R.id.levelspinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.levelspinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        levelspinner.setAdapter(adapter);

        ratespinner = (Spinner) findViewById(R.id.ratespinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.ratespinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        ratespinner.setAdapter(adapter);

        searchspinner = (Spinner) findViewById(R.id.searchspinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.searchspinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        searchspinner.setAdapter(adapter);


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
        Button searchBtn = (Button)findViewById(R.id.search);
        final EditText searchText=(EditText)findViewById(R.id.searchText);

       // final LinearLayout finalLayout = layout;
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String levelString =levelspinner.getSelectedItem().toString();
                String classString = classspinner.getSelectedItem().toString();
                String rateString = ratespinner.getSelectedItem().toString();
                String searchSpinString = searchspinner.getSelectedItem().toString();
                final String searchString=searchText.getText().toString();
                final LinearLayout layout = (LinearLayout) findViewById(R.id.work1);
                layout.removeAllViews();
                if(inet!=null) {
                    try {
                        ResultSet result;
                        int level = 0;
                        if (levelString.equals("Beginner")) {
                            level = 0;
                            levelString = String.valueOf(level);
                        } else if (levelString.equals("Intermediate")) {
                            level = 1;
                            levelString = String.valueOf(level);
                        } else if (levelString.equals("Advanced")) {
                            level = 2;
                            levelString = String.valueOf(level);
                        }
                        if (rateString.equals("")) {
                            rateString = "";
                        } else {
                            rateString = String.valueOf(Math.round(Float.parseFloat(rateString)));
                        }
                        int category = 0;
                        if (classString.equals("Agility")) {
                            category = 0;
                            classString = String.valueOf(category);
                        } else if (classString.equals("Strength")) {
                            category = 1;
                            classString = String.valueOf(category);
                        } else if (classString.equals("Fitness")) {
                            category = 2;
                            classString = String.valueOf(category);
                        } else if (classString.equals("Fat-burning")) {
                            category = 3;
                            classString = String.valueOf(category);
                        }
                        final String userId = sharedPref.getString("userId", "");
                        String role = sharedPref.getString("role", "");
                        ResultSet result1 = null;
                        ResultSet result2 = null;
                        if (role.equals("2")) {
                            result1 = statement.executeQuery(" select Programs.ID, Programs.Title from Programs INNER JOIN Users on [dbo].[Users].[ID]=[dbo].[Programs].[Creator] and [dbo].[Users].[IsActive]=1 and ProgramLevel LIKE '%" + levelString + "%'" +
                                    " AND ProgramCategory LIKE '%" + classString + "%' AND Title LIKE '%" + searchString + "%' AND Rate LIKE '%" + rateString + "%' and [dbo].[Programs].[Creator]='" + userId + "' order by ProgramLevel");
                        } else {
                            if (searchSpinString.equals("Program")) {
                                result1 = statement.executeQuery(" select Programs.ID, Programs.Title from Programs INNER JOIN Users on [dbo].[Users].[ID]=[dbo].[Programs].[Creator] and [dbo].[Users].[IsActive]=1  and [dbo].[Programs].[IsActive]=1 AND ProgramLevel LIKE '%" + levelString + "%'" +
                                        " AND ProgramCategory LIKE '%" + classString + "%' AND Title LIKE '%" + searchString + "%' AND Rate LIKE '%" + rateString + "%' order by ProgramLevel");
                            } else {
                                result2 = statement.executeQuery("select * from Users where IsActive=1 AND Username LIKE '%" + searchString + "%' AND Role=2 ");
                            }
                        }

                        if (result1!=null){
                        while (result1.next()) {
                            Button btnTag = new Button(getApplicationContext());
                            btnTag.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                            btnTag.setText(result1.getString("Title"));
                            btnTag.setPadding(0, 10, 0, 0);

                            layout.addView(btnTag);
                            final String progId = result1.getString("ID");
                            btnTag.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {

                                    Intent intent = new Intent(getBaseContext(), WorkoutIntro.class);
                                    intent.putExtra("PROGID", progId);
                                    startActivity(intent);

                                }
                            });
                        }
                        }
                        if (result2!=null){
                            while (result2.next()) {
                                Button btnTag = new Button(getApplicationContext());
                                btnTag.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                btnTag.setText(result2.getString("Username"));
                                btnTag.setPadding(0, 10, 0, 0);

                                layout.addView(btnTag);
                                final String trainerId = result2.getString("ID");
                                final String finalClassString = classString;
                                final String finalRateString = rateString;
                                final String finalLevelString = levelString;
                                btnTag.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {
                                        layout.removeAllViews();
                                        try {
                                            ResultSet result3 = statement.executeQuery(" select Programs.ID, Programs.Title from Programs INNER JOIN Users on [dbo].[Users].[ID]=[dbo].[Programs].[Creator] and [dbo].[Users].[IsActive]=1 and ProgramLevel LIKE '%" + finalLevelString + "%'" +
                                                    " AND ProgramCategory LIKE '%" + finalClassString + "%'  AND Rate LIKE '%" + finalRateString + "%' and [dbo].[Programs].[Creator]='" + trainerId + "' order by ProgramLevel");

                                            while (result3.next()) {
                                                Button btnTag1 = new Button(getApplicationContext());
                                                btnTag1.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                                btnTag1.setText(result3.getString("Title"));
                                                btnTag1.setPadding(0, 10, 0, 0);

                                                layout.addView(btnTag1);
                                                final String progId = result3.getString("ID");
                                                btnTag1.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View v) {

                                                        Intent intent = new Intent(getBaseContext(), WorkoutIntro.class);
                                                        intent.putExtra("PROGID", progId);
                                                        startActivity(intent);

                                                    }
                                                });

                                            }
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });

                            }
                    }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"You need internet connection to search programs.",Toast.LENGTH_SHORT).show();
                }

            }
        });



        String userId = sharedPref.getString("userId", "");
        pencere.setCurrentTab(1);
        layout = (LinearLayout) findViewById(R.id.myWorkout);

        if (!userId.isEmpty()) {
                File programsFile = new File(getExternalFilesDir(null), "programs.xls");
                if (programsFile.exists()) {
                    try {
                        myInput = new FileInputStream(programsFile);
                        POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                        HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                        HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                        int rowNumber = mySheet.getPhysicalNumberOfRows();
                        int i;
                        for (i = 2; i <= rowNumber; i++) {
                            CellReference userRef = new CellReference("L" + i);
                            CellReference titleRef = new CellReference("F" + i);
                            CellReference idRef = new CellReference("A" + i);
                            Row row = mySheet.getRow(userRef.getRow());
                            Cell cell = row.getCell(userRef.getCol());
                            Cell cell2 = row.getCell(titleRef.getCol());
                            Cell cell3 = row.getCell(idRef.getCol());
                            if (cell.toString().equals(userId)) {
                                Button btnWorks = new Button(this);
                                btnWorks.setLayoutParams(new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT));
                                btnWorks.setText(cell2.toString());
                                btnWorks.setPadding(0,10,0,0);
                                layout.addView(btnWorks);
                                final String progId = cell3.toString();

                                btnWorks.setOnClickListener(new View.OnClickListener() {
                                    public void onClick(View v) {

                                        Intent intent = new Intent(getBaseContext(), MyWorkout.class);
                                        intent.putExtra("PROGID", progId);
                                        startActivity(intent);

                                    }
                                });
                            }
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            weightFile = new File(getExternalFilesDir(null), sharedPref.getString("userId", "")+ ".xls");
            if (weightFile.exists()) {
                try {
                    myInput = new FileInputStream(weightFile);
                    POIFSFileSystem myFileSystem = null;
                    myFileSystem = new POIFSFileSystem(myInput);
                    HSSFWorkbook myWorkBook = null;
                    myWorkBook = new HSSFWorkbook(myFileSystem);
                    HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                    int rowNumber = mySheet.getPhysicalNumberOfRows();
                    CellReference weightRef = new CellReference("A" + rowNumber);
                    CellReference fatRef = new CellReference("B" + rowNumber);
                    CellReference muscleRef = new CellReference("C" + rowNumber);
                    CellReference heightRef = new CellReference("H" + rowNumber);
                    Row row =  mySheet.getRow(weightRef.getRow());
                    Cell cell1 = row.getCell(weightRef.getCol());
                    Cell cell2 = row.getCell(fatRef.getCol());
                    Cell cell3 = row.getCell(muscleRef.getCol());
                    Cell cell4 = row.getCell(heightRef.getCol());
                    weightText.setHint(cell1.toString());
                    heightText.setHint(cell4.toString());
                    fatText.setHint(cell2.toString());
                    muscleText.setHint(cell3.toString());
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("weight", cell1.toString());
                    editor.putString("height", cell4.toString());
                    editor.apply();

                    Log.d("edittext",weightText.getHint().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    public void logIn(View view) throws NoSuchAlgorithmException, IOException {
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
             ResultSet loginRes = statement.executeQuery("select * from Users where Email ='"+usernameField.getText().toString()+"' AND IsActive = 1 AND Password='"+ passbuffer.toString()+"'");
             if(!loginRes.next()){
              Toast.makeText(this,"Wrong email or password.",Toast.LENGTH_SHORT).show();
             }else {
                 editor.putString("lastLoginDate", calendar.get(Calendar.YEAR) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
                 editor.putString("userId", loginRes.getString("ID"));
                 editor.putString("username", loginRes.getString("Username"));
                 editor.putString("email", usernameField.getText().toString());
                 editor.putString("password", passwordField.getText().toString());
                 editor.putString("weight", loginRes.getString("Weight"));
                 editor.putString("height", loginRes.getString("Height"));
                 editor.putString("role", loginRes.getString("Role"));
                 editor.apply();

                 String role = sharedPref.getString("role", "");
                 if (!role.equals("2")) {
                     File weightFile = new File(getExternalFilesDir(null).getAbsolutePath(), loginRes.getString("ID") + ".xls");
                     File usersFile = new File(getExternalFilesDir(null).getAbsolutePath(), "users.xls");
                     if (weightFile.exists()) {
                         FileInputStream  myInput = new FileInputStream(weightFile);
                         POIFSFileSystem myFileSystem = new POIFSFileSystem(myInput);
                         HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                         HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                         int rowNumber = mySheet.getPhysicalNumberOfRows();
                         int i;
                         for (i = 2; i <= rowNumber; i++) {
                             CellReference cellReference = new CellReference("G" + i);
                             Row row = mySheet.getRow(cellReference.getRow());
                             Cell cell = row.getCell(cellReference.getCol());
                             if (cell.toString().equals("0.0")) {
                                 CellReference weightRef = new CellReference("A" + i);
                                 CellReference fatRef = new CellReference("B" + i);
                                 CellReference muscleRef = new CellReference("C" + i);
                                 CellReference recordRef = new CellReference("D" + i);
                                 CellReference progidRef = new CellReference("E" + i);
                                 CellReference heightRef = new CellReference("H" + i);

                                 Cell cell1 = row.getCell(weightRef.getCol());
                                 Cell cell2 = row.getCell(fatRef.getCol());
                                 Cell cell3 = row.getCell(muscleRef.getCol());
                                 Cell cell4 = row.getCell(recordRef.getCol());
                                 Cell cell5 = row.getCell(progidRef.getCol());
                                 Cell cell6 = row.getCell(heightRef.getCol());
                                 String progid = "00000000-0000-0000-0000-000000000000";
                                 if (!cell5.toString().isEmpty()) {
                                     progid = cell5.toString();
                                 }
                                 String uniqueID = UUID.randomUUID().toString();
                                 Statement statement3 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                 statement3.executeUpdate(" USE [Workout] INSERT INTO [dbo].[UserWeightChangeLog] ([ID],[UserId],[WeightLoss],[RecordDate],[ProgramID],[FatRatio],[MuscleRatio],[Height])" +
                                         "VALUES('" + uniqueID + "','" + sharedPref.getString("userId", "") + "'," + cell1.toString() + ",'" + cell4.toString() + "','" + progid + "'," + cell2.toString() + "," + cell3.toString() + "," + cell6.toString() + ")");

                                 cell.setCellValue(1);


                             }
                             FileOutputStream os = null;
                             os = new FileOutputStream(weightFile);
                             myWorkBook.write(os);
                             os.close();
                         }

                         weightFile.getCanonicalFile().delete();
                     }

                     if (!weightFile.exists()) {
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
                         c.setCellValue("FatRatio");
                         c.setCellStyle(cs);

                         c = headerRow.createCell(2);
                         c.setCellValue("MuscleRatio");
                         c.setCellStyle(cs);

                         c = headerRow.createCell(3);
                         c.setCellValue("RecordDate");
                         c.setCellStyle(cs);

                         c = headerRow.createCell(4);
                         c.setCellValue("ProgramID");
                         c.setCellStyle(cs);

                         c = headerRow.createCell(5);
                         c.setCellValue("ProgramName");
                         c.setCellStyle(cs);

                         c = headerRow.createCell(6);
                         c.setCellValue("IsSent");
                         c.setCellStyle(cs);

                         c = headerRow.createCell(7);
                         c.setCellValue("Height");
                         c.setCellStyle(cs);

                         sheet1.setColumnWidth(0, (15 * 500));
                         sheet1.setColumnWidth(1, (15 * 500));
                         sheet1.setColumnWidth(2, (15 * 500));
                         sheet1.setColumnWidth(3, (15 * 500));
                         sheet1.setColumnWidth(4, (15 * 500));
                         sheet1.setColumnWidth(5, (15 * 500));
                         sheet1.setColumnWidth(6, (15 * 500));
                         sheet1.setColumnWidth(7, (15 * 500));

                         ResultSet userHistoryInfo = statement.executeQuery("USE [Workout] SELECT CAST(WeightLoss as int)as WeightLoss,CAST(RecordDate AS DATE)as RecordDate, ProgramID, CAST(FatRatio as int)as FatRatio, CAST(MuscleRatio as int)as MuscleRatio,CAST(Height as int)as Height from [UserWeightChangeLog] where UserID='" + loginRes.getString("ID") + "' order by RecordDate");
                         int row = 1;
                         while (userHistoryInfo.next()) {
                             Row dataRow = sheet1.createRow(row);

                             c = dataRow.createCell(0);
                             c.setCellValue(userHistoryInfo.getString("WeightLoss"));
                             c = dataRow.createCell(1);
                             c.setCellValue(userHistoryInfo.getString("FatRatio"));
                             c = dataRow.createCell(2);
                             c.setCellValue(userHistoryInfo.getString("MuscleRatio"));
                             c = dataRow.createCell(3);
                             c.setCellValue(userHistoryInfo.getString("RecordDate"));
                             c = dataRow.createCell(4);
                             if(userHistoryInfo.getString("ProgramID").equals("00000000-0000-0000-0000-000000000000")){
                                 c.setCellValue("00000000-0000-0000-0000-000000000000");//kolo
                             }else {
                                 c.setCellValue(userHistoryInfo.getString("ProgramID"));
                             }
                             c = dataRow.createCell(5);
                             c.setCellValue("No Program");
                             c = dataRow.createCell(6);
                             c.setCellValue(1);
                             c = dataRow.createCell(7);
                             c.setCellValue(userHistoryInfo.getString("Height"));
                             row++;
                         }
                         FileOutputStream os = null;
                         os = new FileOutputStream(weightFile);
                         wb.write(os);

                         FileInputStream myInput = null;
                         myInput = new FileInputStream(weightFile);
                         POIFSFileSystem myFileSystem = null;
                         myFileSystem = new POIFSFileSystem(myInput);
                         HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                         final HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                         final int rowNumber = mySheet.getPhysicalNumberOfRows();
                         int i ;
                         for(i=2;i<=rowNumber;i++) {
                             CellReference idRef = new CellReference("E" + i);
                             Row row1 = mySheet.getRow(idRef.getRow());
                             Cell cell1 = row1.getCell(idRef.getCol());
                             if ((!cell1.toString().equals("00000000-0000-0000-0000-000000000000"))) {
                                 CellReference titleRef = new CellReference("F" + i);
                                 ResultSet progTitleInfo = statement.executeQuery("USE [Workout] SELECT Title from Programs where ID = '"+cell1.toString()+"'");
                                 while (progTitleInfo.next()){
                                 Cell cell2 = row1.getCell(titleRef.getCol());
                                 cell2.setCellValue(progTitleInfo.getString("Title"));
                                 os = new FileOutputStream(weightFile);
                                 myWorkBook.write(os);
                                 }
                             }
                         }


                     }
                     if (!usersFile.exists()) {
                         Workbook wb = new HSSFWorkbook();
                         Cell c = null;
                         CellStyle cs = wb.createCellStyle();
                         cs.setFillForegroundColor(HSSFColor.LIME.index);
                         cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

                         Sheet sheet1 = null;
                         sheet1 = wb.createSheet("Users Infos");

                         Row headerRow = sheet1.createRow(0);

                         c = headerRow.createCell(0);
                         c.setCellValue("Email");
                         c.setCellStyle(cs);
                         c = headerRow.createCell(1);
                         c.setCellValue("Password");
                         c.setCellStyle(cs);
                         c = headerRow.createCell(2);
                         c.setCellValue("ID");
                         c.setCellStyle(cs);
                         c = headerRow.createCell(3);
                         c.setCellValue("Username");
                         c.setCellStyle(cs);
                         c = headerRow.createCell(4);
                         c.setCellValue("Weight");
                         c.setCellStyle(cs);
                         c = headerRow.createCell(5);
                         c.setCellValue("Height");
                         c.setCellStyle(cs);
                         c = headerRow.createCell(6);
                         c.setCellValue("Role");
                         c.setCellStyle(cs);
                         sheet1.setColumnWidth(0, (15 * 500));
                         sheet1.setColumnWidth(1, (15 * 500));
                         sheet1.setColumnWidth(2, (15 * 500));
                         sheet1.setColumnWidth(3, (15 * 500));
                         sheet1.setColumnWidth(4, (15 * 500));
                         sheet1.setColumnWidth(5, (15 * 500));
                         sheet1.setColumnWidth(6, (15 * 500));
                         int row = 1;
                         Row dataRow = sheet1.createRow(row);

                         c = dataRow.createCell(0);
                         c.setCellValue(usernameField.getText().toString());
                         c = dataRow.createCell(1);
                         c.setCellValue(passbuffer.toString());
                         c = dataRow.createCell(2);
                         c.setCellValue(sharedPref.getString("userId", ""));
                         c = dataRow.createCell(3);
                         c.setCellValue(sharedPref.getString("username", ""));
                         c = dataRow.createCell(4);
                         c.setCellValue(sharedPref.getString("weight", ""));
                         c = dataRow.createCell(5);
                         c.setCellValue(sharedPref.getString("height", ""));
                         c = dataRow.createCell(6);
                         c.setCellValue(sharedPref.getString("role", ""));
                         FileOutputStream os = null;
                         os = new FileOutputStream(usersFile);
                         wb.write(os);
                     } else {
                         FileInputStream myInput = null;
                         myInput = new FileInputStream(usersFile);
                         POIFSFileSystem myFileSystem = null;
                         myFileSystem = new POIFSFileSystem(myInput);
                         HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                         final HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                         final int rowNumber = mySheet.getPhysicalNumberOfRows();
                         int i = 2;
                         CellReference idRef = new CellReference("C" + i);
                         Row row = mySheet.getRow(idRef.getRow());
                         Cell cell1 = row.getCell(idRef.getCol());
                         while ((!cell1.toString().equals(sharedPref.getString("userId", ""))) && (i < rowNumber)) {
                             i++;
                             idRef = new CellReference("C" + i);
                             row = mySheet.getRow(idRef.getRow());
                             cell1 = row.getCell(idRef.getCol());
                         }
                         if (cell1.toString().equals(sharedPref.getString("userId", ""))) {
                             CellReference weightRef = new CellReference("E" + i);
                             Cell cell2 = row.getCell(weightRef.getCol());
                             cell2.setCellValue(sharedPref.getString("weight", ""));
                             CellReference heightRef = new CellReference("F" + i);
                             Cell cell3 = row.getCell(heightRef.getCol());
                             cell3.setCellValue(sharedPref.getString("height", ""));
                             FileOutputStream os = null;
                             os = new FileOutputStream(usersFile);
                             myWorkBook.write(os);
                         } else {

                             FileOutputStream os = null;
                             Cell c = null;
                             Row dataRow = mySheet.createRow(rowNumber);
                             c = dataRow.createCell(0);
                             c.setCellValue(usernameField.getText().toString());
                             c = dataRow.createCell(1);
                             c.setCellValue(passbuffer.toString());
                             c = dataRow.createCell(2);
                             c.setCellValue(sharedPref.getString("userId", ""));
                             c = dataRow.createCell(3);
                             c.setCellValue(sharedPref.getString("username", ""));
                             c = dataRow.createCell(4);
                             c.setCellValue(sharedPref.getString("weight", ""));
                             c = dataRow.createCell(5);
                             c.setCellValue(sharedPref.getString("height", ""));
                             c = dataRow.createCell(6);
                             c.setCellValue(sharedPref.getString("role", ""));
                             os = new FileOutputStream(usersFile);
                             myWorkBook.write(os);
                             os.close();
                         }
                     }
                 }
                 finish();
                 startActivity(getIntent());
                    displayData(view);
             }

         }catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            File usersFile = new File(getExternalFilesDir(null).getAbsolutePath(),"users.xls");
            if(usersFile.exists()){
                FileInputStream myInput = null;
                myInput = new FileInputStream(usersFile);
                POIFSFileSystem myFileSystem = null;
                myFileSystem = new POIFSFileSystem(myInput);
                HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                final HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                final int rowNumber = mySheet.getPhysicalNumberOfRows();
                FileOutputStream os = null;
                int i=2;
                boolean found = false;
                while ((i <= rowNumber)&&(found==false)) {
                    CellReference emailRef = new CellReference("A" + i);
                    CellReference passRef = new CellReference("B" + i);
                    CellReference idRef = new CellReference("C" + i);
                    CellReference nameRef = new CellReference("D" + i);
                    CellReference weightRef = new CellReference("E" + i);
                    CellReference heightRef = new CellReference("F" + i);
                    CellReference roleRef = new CellReference("G" + i);
                    Row row = mySheet.getRow(emailRef.getRow());
                    Cell cell1 = row.getCell(emailRef.getCol());
                    Cell cell2 = row.getCell(passRef.getCol());
                    Cell cell3 = row.getCell(idRef.getCol());
                    Cell cell4 = row.getCell(nameRef.getCol());
                    Cell cell5 = row.getCell(weightRef.getCol());
                    Cell cell6 = row.getCell(heightRef.getCol());
                    Cell cell7 = row.getCell(roleRef.getCol());
                    if ((cell1.toString().equals(usernameField.getText().toString())&&(cell2.toString().equals(passbuffer.toString())))) {
                        Log.d("login","username kabul");
                        editor.putString("lastLoginDate", calendar.get(Calendar.YEAR) + "-" +  String.valueOf(calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
                        editor.putString("userId", cell3.toString());
                        editor.putString("username", cell4.toString());
                        editor.putString("email", cell1.toString());
                        editor.putString("password", cell2.toString());
                        editor.putString("height", cell6.toString());
                        editor.putString("weight", cell5.toString());
                        editor.putString("role", cell7.toString());
                        editor.apply();
                        found=true;
                        finish();
                        startActivity(getIntent());
                    }else{
                        i++;
                    }

                }
                if(i>rowNumber){
                    Toast.makeText(this,"Wrong email or password",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"You need to login online once",Toast.LENGTH_SHORT).show();
            }

        }

    }


    public void displayData(View view) {
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String name = sharedPref.getString("email", "");
        String pass = sharedPref.getString("password", "");
        String weight = sharedPref.getString("weight", "");
        String loginTime = sharedPref.getString("lastLoginDate", "");
       // Toast.makeText(this, name + " " + pass + " " + weight + " " + loginTime, Toast.LENGTH_LONG).show();
    }

    public void logOut(View view) {

        sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("email");
        editor.remove("password");
        editor.remove("userId");
        editor.remove("username");
        editor.remove("progId");
        editor.remove("weight");
        editor.remove("progname");
        editor.remove("height");
        editor.remove("role");
        editor.remove("lastLoginDate");
        editor.apply();
        finish();
        startActivity(getIntent());
    }

    public void SeeHistory(View view){
        String role = sharedPref.getString("role", "");
        if (!role.equals("2")) {
            Intent intent = new Intent(getBaseContext(), WorkoutHistory.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"Trainers aren't allowed to change their body information.",Toast.LENGTH_SHORT).show();
        }
    }

    public void setWeight(View view) {
        String role = sharedPref.getString("role", "");
        if (!role.equals("2")) {
            EditText weightText = (EditText) findViewById(R.id.weightText);
            String wT = weightText.getText().toString();
            EditText fatText = (EditText) findViewById(R.id.fatText);
            String fT = fatText.getText().toString();
            EditText muscleText = (EditText) findViewById(R.id.muscleText);
            String mT = muscleText.getText().toString();
            EditText heightText = (EditText) findViewById(R.id.heightText);
            String hT = heightText.getText().toString();
        Statement statement = null;

        if (wT.isEmpty()||fT.isEmpty()||mT.isEmpty()||hT.isEmpty()) {
            if (weightText.getText().toString().isEmpty()) {
               weightText.setError("Empty field");
            }
            if (fatText.getText().toString().isEmpty()) {
                fatText.setError("Empty field");
            }
            if (muscleText.getText().toString().isEmpty()) {
                muscleText.setError("Empty field");
            }
            if (heightText.getText().toString().isEmpty()) {
                heightText.setError("Empty field");
            }
        }else {
            try {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo inet = conMgr.getActiveNetworkInfo();
                if (inet != null) {
                    try {
                        statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                ResultSet.CONCUR_UPDATABLE);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    statement.executeUpdate("update Users set Weight=" + wT + ", Height=" + hT + " where ID ='" + sharedPref.getString("userId", "") + "'");//HEİGHT DEĞİŞTİRİRKEN BURAYA DA BAK
                }
                File weightFile = new File(getExternalFilesDir(null), sharedPref.getString("userId", "") + ".xls");
                if (weightFile.exists()) {
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
                    c.setCellValue(fT);//ratio burada
                    c = dataRow.createCell(2);
                    c.setCellValue(mT);
                    c = dataRow.createCell(3);
                    c.setCellValue(calendar.get(Calendar.YEAR) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
                    c = dataRow.createCell(4);
                    c.setCellValue(sharedPref.getString("progId", ""));
                    c = dataRow.createCell(5);
                    c.setCellValue(sharedPref.getString("progname", ""));
                    c = dataRow.createCell(6);
                    c.setCellValue(0);
                    c = dataRow.createCell(7);
                    c.setCellValue(hT);

                    os = new FileOutputStream(weightFile);
                    myWorkBook.write(os);
                    os.close();
                }
                File usersFile = new File(getExternalFilesDir(null), "users.xls");
                if (weightFile.exists()) {
                    FileInputStream myInput = null;
                    myInput = new FileInputStream(usersFile);
                    POIFSFileSystem myFileSystem = null;
                    myFileSystem = new POIFSFileSystem(myInput);
                    HSSFWorkbook myWorkBook = new HSSFWorkbook(myFileSystem);
                    final HSSFSheet mySheet = myWorkBook.getSheetAt(0);
                    final int rowNumber = mySheet.getPhysicalNumberOfRows();
                    FileOutputStream os = null;
                    int i = 2;
                    CellReference idRef = new CellReference("C" + i);
                    Row row = mySheet.getRow(idRef.getRow());
                    Cell cell1 = row.getCell(idRef.getCol());
                    while (!cell1.toString().equals(sharedPref.getString("userId", ""))) {
                        i++;
                        idRef = new CellReference("C" + i);
                        row = mySheet.getRow(idRef.getRow());
                        cell1 = row.getCell(idRef.getCol());
                    }

                    CellReference weightRef = new CellReference("E" + i);
                    CellReference heightRef = new CellReference("F" + i);
                    Cell cell2 = row.getCell(weightRef.getCol());
                    cell2.setCellValue(wT);
                    Cell cell3 = row.getCell(heightRef.getCol());
                    cell3.setCellValue(hT);
                    os = new FileOutputStream(usersFile);
                    myWorkBook.write(os);
                    os.close();
                }

            } catch (SQLException e) {
                Log.e("ERROR", e.getMessage());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            curWeight = Integer.parseInt(wT);
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(weightText.getWindowToken(), 0);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("weight", wT);
            editor.putString("height", hT);
            editor.apply();


            ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo inet = conMgr.getActiveNetworkInfo();
            if (inet != null) {
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
                            CellReference cellReference = new CellReference("G" + i);
                            Row row = mySheet.getRow(cellReference.getRow());
                            Cell cell = row.getCell(cellReference.getCol());
                            if (cell.toString().equals("0.0")) {
                                CellReference weightRef = new CellReference("A" + i);
                                CellReference fatRef = new CellReference("B" + i);
                                CellReference muscleRef = new CellReference("C" + i);
                                CellReference recordRef = new CellReference("D" + i);
                                CellReference progidRef = new CellReference("E" + i);
                                CellReference heightRef = new CellReference("H" + i);

                                Cell cell1 = row.getCell(weightRef.getCol());
                                Cell cell2 = row.getCell(fatRef.getCol());
                                Cell cell3 = row.getCell(muscleRef.getCol());
                                Cell cell4 = row.getCell(recordRef.getCol());
                                Cell cell5 = row.getCell(progidRef.getCol());
                                Cell cell6 = row.getCell(heightRef.getCol());
                                String progid = "00000000-0000-0000-0000-000000000000";
                                if (!cell5.toString().isEmpty()) {
                                    progid = cell5.toString();
                                }
                                String uniqueID = UUID.randomUUID().toString();
                                Statement statement3 = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                                statement3.executeUpdate(" USE [Workout] INSERT INTO [dbo].[UserWeightChangeLog] ([ID],[UserId],[WeightLoss],[RecordDate],[ProgramID],[FatRatio],[MuscleRatio],[Height])" +
                                        "VALUES('" + uniqueID + "','" + sharedPref.getString("userId", "") + "'," + cell1.toString() + ",'" + cell4.toString() + "','" + progid + "'," + cell2.toString() + "," + cell3.toString() + "," + cell6.toString() + ")");

                                cell.setCellValue(1);
                                Log.d("weight", cell1.toString());

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
            }

            finish();
            startActivity(getIntent());
        }
        }else{
            Toast.makeText(this,"Trainers aren't allowed to change their body information.",Toast.LENGTH_SHORT).show();
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

    @Override

    public void onBackPressed() {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setMessage("Are you sure you want to exit?");
        alertDlg.setCancelable(false);
        alertDlg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.super.onBackPressed();
                    }
                }
        );
        alertDlg.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int which) {

            }

        });

        alertDlg.create().show();

    }
}


