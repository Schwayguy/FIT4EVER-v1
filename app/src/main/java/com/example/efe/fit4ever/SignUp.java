package com.example.efe.fit4ever;

import android.app.DatePickerDialog;
import android.app.Dialog;
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

import java.util.Calendar;

public class SignUp extends AppCompatActivity {
    Button btn;
    int yearx, monthx, dayx;
    static final int DIALOG_ID=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        final Calendar cal = Calendar.getInstance();
        yearx = cal.get(Calendar.YEAR);
        monthx = cal.get(Calendar.MONTH);
        dayx = cal.get(Calendar.DAY_OF_MONTH);

        showDialogOnButtonClick();
        Spinner spinner;
        ArrayAdapter<CharSequence> adapter;
        spinner = (Spinner)findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.gender,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

    }

    public void showDialogOnButtonClick(){
        btn = (Button) findViewById(R.id.bdButton);
        btn.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(DIALOG_ID);
                    }
                }
        );

    }
    @Override
    protected Dialog onCreateDialog(int id){
        if(id==DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener,yearx,monthx,dayx);
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            yearx = year;
            monthx = monthOfYear + 1;
            dayx = dayOfMonth;
            Toast.makeText(SignUp.this,dayx+"/ "+monthx+"/ "+yearx,Toast.LENGTH_SHORT).show();
        }
    };


    public void signupComplete(View view){
        EditText username = (EditText) findViewById(R.id.usernameSign);
        EditText password = (EditText) findViewById(R.id.passwordSign);
        EditText passwordR = (EditText) findViewById(R.id.passwordRSign);
        EditText email = (EditText) findViewById(R.id.emailSign);
        EditText name = (EditText) findViewById(R.id.nameSign);
        EditText surname = (EditText) findViewById(R.id.surnameSign);
        EditText height = (EditText) findViewById(R.id.heightSign);
        EditText weight = (EditText) findViewById(R.id.weightSign);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        String gender = spinner.getSelectedItem().toString();
        if((username.getText().toString().isEmpty())||(password.getText().toString().isEmpty())||(passwordR.getText().toString().isEmpty())||(email.getText().toString().isEmpty())||(name.getText().toString().isEmpty())||(surname.getText().toString().isEmpty())||(height.getText().toString().isEmpty())||(weight.getText().toString().isEmpty())) {

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
            if (!password.getText().toString().equals(passwordR.getText().toString())) {
                passwordR.setError("Password mismatch");
            }

            //password, username, email uzunluğu belirle


        }else{
            Time creationDate = new Time(Time.getCurrentTimezone());
            creationDate.setToNow();
            //signupu gerçekleştir
        }





    }
}
