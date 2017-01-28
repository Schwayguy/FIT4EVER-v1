package com.example.efe.fit4ever;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.UUID;

public class SignUp extends AppCompatActivity {
    Connection conn;
    Statement statement = null;
    Button btn;
    int yearx, monthx, dayx;
    static final int DIALOG_ID = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sign_up);
        yearx = cal.get(Calendar.YEAR);
        monthx = cal.get(Calendar.MONTH);
        dayx = cal.get(Calendar.DAY_OF_MONTH);
        CONN();
        try {
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        showDialogOnButtonClick();
        Spinner spinner;
        ArrayAdapter<CharSequence> adapter;
        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void showDialogOnButtonClick() {
        btn = (Button) findViewById(R.id.bdButton);
        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, yearx, monthx, dayx);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            yearx = year;
            monthx = monthOfYear + 1;
            dayx = dayOfMonth;
            Toast.makeText(SignUp.this, dayx + "/ " + monthx + "/ " + yearx, Toast.LENGTH_SHORT).show();
        }
    };


    public void signupComplete(View view) throws NoSuchAlgorithmException {
        EditText username = (EditText) findViewById(R.id.usernameSign);
        EditText fatratio = (EditText) findViewById(R.id.fatSign);
        EditText muscleratio = (EditText) findViewById(R.id.muscleSign);
        EditText password = (EditText) findViewById(R.id.passwordSign);
        EditText passwordR = (EditText) findViewById(R.id.passwordRSign);
        EditText email = (EditText) findViewById(R.id.emailSign);
        EditText name = (EditText) findViewById(R.id.nameSign);
        EditText surname = (EditText) findViewById(R.id.surnameSign);
        EditText height = (EditText) findViewById(R.id.heightSign);
        EditText weight = (EditText) findViewById(R.id.weightSign);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        String gender = spinner.getSelectedItem().toString();
        EditText phone = (EditText) findViewById(R.id.phoneSign);

        if ((username.getText().toString().isEmpty()) || (password.getText().toString().isEmpty()) || (passwordR.getText().toString().isEmpty())
                || (email.getText().toString().isEmpty()) || (name.getText().toString().isEmpty()) || (surname.getText().toString().isEmpty())
                || (height.getText().toString().isEmpty()) ||(fatratio.getText().toString().isEmpty()) ||(muscleratio.getText().toString().isEmpty()) || (weight.getText().toString().isEmpty())) {

            if (username.getText().toString().isEmpty()) {
                username.setError("Empty field");
            }
            if (password.getText().toString().isEmpty()) {
                password.setError("Empty field");
            }
            if (passwordR.getText().toString().isEmpty()) {
                passwordR.setError("Empty field");
            }
            if (email.getText().toString().isEmpty()) {
                email.setError("Empty field");
            }
            if (fatratio.getText().toString().isEmpty()) {
                fatratio.setError("Empty field");
            }
            if (muscleratio.getText().toString().isEmpty()) {
                muscleratio.setError("Empty field");
            }
            if (name.getText().toString().isEmpty()) {
                name.setError("Empty field");
            }
            if (surname.getText().toString().isEmpty()) {
                surname.setError("Empty field");
            }
            if (height.getText().toString().isEmpty()) {
                height.setError("Empty field");
            }
            if (weight.getText().toString().isEmpty()) {
                weight.setError("Empty field");
            }
            if (!password.getText().toString().equals(passwordR.getText().toString())) {
                passwordR.setError("Password mismatch");
            }


        } else {
            Time creationDate = new Time(Time.getCurrentTimezone());
            creationDate.setToNow();

            String  uniqueID1 = UUID.randomUUID().toString();
            String  uniqueID2 = UUID.randomUUID().toString();
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] result = md.digest(password.getText().toString().getBytes());
            StringBuffer passbuffer = new StringBuffer();
            for (int i = 0; i < result.length; i++) {
                passbuffer.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
            }

            ResultSet checkUserNameandEmail = null;
            try {
                checkUserNameandEmail = statement.executeQuery("USE [Workout] SELECT * FROM [dbo].[Users] where [Username] ='" + username.getText().toString() + "' OR [Email]='" + email.getText().toString() + "'");

                if (!checkUserNameandEmail.next()) {
                    int genderNo;
                    if(gender.equals("Male")){
                        genderNo=0;
                    }else{genderNo=1;}

                    statement.executeUpdate(" USE [Workout] INSERT INTO [dbo].[Users] ([ID]\n" +
                            "      ,[Username]\n" +
                            "      ,[Password]\n" +
                            "      ,[Email]\n" +
                            "      ,[Name]\n" +
                            "      ,[Surname]\n" +
                            "      ,[Birthdate]\n" +
                            "      ,[Gender]\n" +
                            "      ,[IsActive]\n" +
                            "      ,[CreationDate]\n" +
                            "      ,[Role]\n" +
                            "      ,[Phone]\n" +
                            "      ,[Height]\n" +
                            "      ,[Weight]\n" +
                            "      ,[ViewCount]) VALUES" +
                            " ('" + uniqueID1 + "','" + username.getText().toString() + "','" + passbuffer.toString() + "','" + email.getText().toString() +
                            "','" + name.getText().toString() + "','" +  surname.getText().toString() + "','"  + yearx  + "- " + monthx + "- " + dayx +"','"+  genderNo + "','"  + 1 + "','"
                            + cal.get(Calendar.YEAR) + "-" + String.valueOf(cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "','" + 1 + "','" + phone.getText().toString() + "','"+ height.getText().toString() + "','"
                            + weight.getText().toString() + "','" + 0 +"')");
                    Toast.makeText(this, "Signup complete.", Toast.LENGTH_SHORT).show();

                    statement.executeUpdate(" USE [Workout] INSERT INTO [dbo].[UserWeightChangeLog] ([ID],[UserId],[WeightLoss],[RecordDate],[ProgramID],[FatRatio],[MuscleRatio],[Height])" +
                            "VALUES('" + uniqueID2 + "','" + uniqueID1 + "'," + weight.getText().toString() + ",'" + cal.get(Calendar.YEAR) + "-" + String.valueOf(cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "','00000000-0000-0000-0000-000000000000',"
                            + fatratio.getText().toString() + "," + muscleratio.getText().toString() + ","+height.getText().toString()+")");

                    SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("weight", weight.getText().toString());
                    editor.putString("height", weight.getText().toString());
                    editor.apply();


                    File weightFile = new File(getExternalFilesDir(null),uniqueID1 + ".xls");


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


                    sheet1.setColumnWidth(0, (15 * 500));
                    sheet1.setColumnWidth(1, (15 * 500));
                    sheet1.setColumnWidth(2, (15 * 500));
                    sheet1.setColumnWidth(3, (15 * 500));
                    sheet1.setColumnWidth(4, (15 * 500));
                    sheet1.setColumnWidth(5, (15 * 500));
                    sheet1.setColumnWidth(6, (15 * 500));

                    Row dataRow = sheet1.createRow(1);

                    c = dataRow.createCell(0);
                    c.setCellValue(weight.getText().toString());
                    c = dataRow.createCell(1);
                    c.setCellValue(fatratio.getText().toString());
                    c = dataRow.createCell(2);
                    c.setCellValue(muscleratio.getText().toString());
                    c = dataRow.createCell(3);
                    c.setCellValue(cal.get(Calendar.YEAR) + "-" + String.valueOf(cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH));
                    c = dataRow.createCell(4);
                    c.setCellValue("00000000-0000-0000-0000-000000000000");
                    c = dataRow.createCell(5);
                    c.setCellValue("No Program");
                    c = dataRow.createCell(6);
                    c.setCellValue(0);
                    c = dataRow.createCell(7);
                    c.setCellValue(height.getText().toString());

                    FileOutputStream os = null;
                    os = new FileOutputStream(weightFile);
                    wb.write(os);
                    os.close();
                    finish();


                } else {
                    Toast.makeText(this, "Please try another username or email.", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SignUp Page") // TODO: Define a title for the content shown.
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
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        conn = null;
        String ConnURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
         /*   ConnURL = "jdbc:jtds:sqlserver://fit4ever1.database.windows.net:1433/Workout;user=fit4ever1;password=Bvmguxg2" +
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
}

