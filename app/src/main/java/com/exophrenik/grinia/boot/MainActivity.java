package com.exophrenik.grinia.boot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;


import com.exophrenik.grinia.R;
import com.exophrenik.grinia.scan.ScanScreen;
import com.exophrenik.grinia.login.LoginScreen;
import com.exophrenik.grinia.register.RegisterScreen;



public class MainActivity extends AppCompatActivity {

    private Intent nextScreen;
    private boolean onlineMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Login button functionality.
        final Button loginButton = (Button)findViewById(R.id.Login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //setContentView(R.layout.activity_login_screen);
                nextScreen = new Intent(getApplicationContext(),LoginScreen.class);
                nextScreen.putExtra("onlineMode",onlineMode);
                startActivity(nextScreen);
            }
        });

        //Register button functionality.
        Button registerButton = (Button)findViewById(R.id.Register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextScreen = new Intent(getApplicationContext(),RegisterScreen.class);
                nextScreen.putExtra("onlineMode",onlineMode);
                startActivity(nextScreen);
            }
        });


        //Guest button functionality.
        Button guestButton = (Button)findViewById(R.id.Guest);
        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextScreen = new Intent(getApplicationContext(),ScanScreen.class);
                nextScreen.putExtra("onlineMode",onlineMode);
                nextScreen.putExtra("username","root");
                startActivity(nextScreen);
            }
        });

        final Switch offlineSwitch = (Switch) findViewById(R.id.offlineSwitch);
        offlineSwitch.setTextOff("offline");
        offlineSwitch.setTextOn("online");

        offlineSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextScreen = new Intent(getApplicationContext(),ScanScreen.class);
                if (offlineSwitch.isChecked()) {
                   onlineMode = true;
                    Log.d("RED","online");
                }
                else{
                   onlineMode = false;
                    Log.d("RED","offline");
                }

            }
        });

    }
}
