package com.example.efe.fit4ever;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Spinner spinner;
        ArrayAdapter<CharSequence> adapter;
        spinner = (Spinner)findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(this,R.array.gender,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);
    }

    public void signupComplete(View view){
        EditText username = (EditText) findViewById(R.id.usernameSign);
        EditText password = (EditText) findViewById(R.id.passwordSign);
        EditText passwordR = (EditText) findViewById(R.id.passwordRSign);
        EditText email = (EditText) findViewById(R.id.emailSign);
        EditText name = (EditText) findViewById(R.id.nameSign);
        EditText surname = (EditText) findViewById(R.id.surnameSign);
        EditText age = (EditText) findViewById(R.id.ageSign);
        EditText height = (EditText) findViewById(R.id.heightSign);
        EditText weight = (EditText) findViewById(R.id.weightSign);
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        String gender = spinner.getSelectedItem().toString();
        if((username.getText().toString().isEmpty())||(password.getText().toString().isEmpty())||(passwordR.getText().toString().isEmpty())||(email.getText().toString().isEmpty())||(name.getText().toString().isEmpty())||(surname.getText().toString().isEmpty())||(age.getText().toString().isEmpty())||(height.getText().toString().isEmpty())||(weight.getText().toString().isEmpty())) {

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
            if (age.getText().toString().isEmpty()) {
                age.setError("Empty field");
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
