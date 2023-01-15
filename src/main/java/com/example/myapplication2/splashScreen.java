package com.example.myapplication2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Thread thread = new Thread(){
            @Override
            public void run() {
                try{
                    sleep(3000);
                }
                catch(Exception e)
                {

                }
                finally
                {
                    startActivity(new Intent(splashScreen.this,MainActivity.class));
                }
            }
        };
        thread.start();

    }
}