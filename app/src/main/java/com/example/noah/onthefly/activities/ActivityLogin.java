package com.example.noah.onthefly.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.noah.onthefly.R;

public class ActivityLogin extends AppCompatActivity {
    EditText usernameField;
    EditText passwordField;
    Button login;
    Button forgotPass;
    CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputSetup();
        buttonSetup();
    }

    protected void inputSetup() {
        usernameField = (EditText) findViewById(R.id.username_input);
        passwordField = (EditText) findViewById(R.id.password_input);
        passwordField.setTypeface(Typeface.DEFAULT);
        passwordField.setTransformationMethod(new PasswordTransformationMethod());
    }

    protected void buttonSetup() {
        login = (Button) findViewById(R.id.log_in_button);
        forgotPass = (Button) findViewById(R.id.forgot_password_button);
        rememberMe = (CheckBox) findViewById(R.id.remember_me_checkbox);
    }

    protected void login(View loginButton) {
        Intent flightListIntent = new Intent(this, ActivityFlightList.class);
        this.startActivity(flightListIntent);
    }

    protected void createAccount(View v) {
        Intent createAccountIntent = new Intent(this, ActivityCreateAccount.class);
        this.startActivity(createAccountIntent);
    }
}
