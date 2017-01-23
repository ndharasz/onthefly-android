package com.example.noah.onthefly.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.noah.onthefly.R;

public class ActivityLogin extends AppCompatActivity {
    EditText usernameField;
    EditText passwordField;
    Button login;
    Button forgotPass;
    public static final String READ = "agreed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        new Legal(this).show();

        inputSetup();
        buttonSetup();
    }

    protected void inputSetup() {
        usernameField = (EditText) findViewById(R.id.username_input);
        passwordField = (EditText) findViewById(R.id.password_input);
    }

    protected void buttonSetup() {
        login = (Button) findViewById(R.id.log_in_button);
        forgotPass = (Button) findViewById(R.id.forgot_password_button);
    }

    protected void login(View loginButton) {
        Intent flightListIntent = new Intent(this, ActivityCreateFlight.class);
        this.startActivity(flightListIntent);
    }
}
