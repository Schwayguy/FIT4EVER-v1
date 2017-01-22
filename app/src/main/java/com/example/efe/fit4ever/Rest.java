package com.example.efe.fit4ever;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class Rest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest);
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        final TextView resttext=(TextView)findViewById(R.id.resttext);
        Log.d("resttime",getIntent().getStringExtra("RESTTIME"));
          new CountDownTimer((long) (1000*Float.parseFloat(getIntent().getStringExtra("RESTTIME"))), 1000) {
            public void onTick(long millisUntilFinished) {
                resttext.setText(String.valueOf((int) (millisUntilFinished / 1000)));
            }
            public void onFinish() {
                MediaPlayer m = new MediaPlayer();
                try{
                    AssetFileDescriptor descriptor = Rest.this.getAssets().openFd("bell.wav");
                    m.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength() );
                    descriptor.close();
                    m.prepare();
                    m.start();
                } catch(Exception e){
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Continue workout", Toast.LENGTH_SHORT).show();
                finish();
            }
        }.start();
    }

}
