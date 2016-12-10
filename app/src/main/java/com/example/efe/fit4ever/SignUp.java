package com.example.efe.fit4ever;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        final Calendar cal = Calendar.getInstance();
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
                || (height.getText().toString().isEmpty()) || (weight.getText().toString().isEmpty())|| (phone.getText().toString().isEmpty())) {

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
            if (phone.getText().toString().isEmpty()) {
                weight.setError("Empty field");
            }
            if (!password.getText().toString().equals(passwordR.getText().toString())) {
                passwordR.setError("Password mismatch");
            }


        } else {
            Time creationDate = new Time(Time.getCurrentTimezone());
            creationDate.setToNow();

            String  uniqueID = UUID.randomUUID().toString();
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
                            " ('" + uniqueID + "','" + username.getText().toString() + "','" + passbuffer.toString() + "','" + email.getText().toString() +
                            "','" + name.getText().toString() + "','" +  surname.getText().toString() + "','"  + yearx  + "- " + monthx + "- " + dayx +"','"+  genderNo + "','"  + 1 + "','"
                            + Calendar.YEAR + "-" + Calendar.MONTH + "-" + Calendar.DAY_OF_MONTH + "','" + 1 + "','" + phone.getText().toString() + "','"+ height.getText().toString() + "','"
                            + weight.getText().toString() + "','" + 0 +"')");
                    Toast.makeText(this, "Signup complete.", Toast.LENGTH_SHORT).show();

                    finish();


                } else {
                    Toast.makeText(this, "Please try another username or email.", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
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

