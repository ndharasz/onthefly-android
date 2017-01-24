package com.example.noah.onthefly.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.noah.onthefly.R;

public class ActivityCreateAccount extends AppCompatActivity {
    EditText firstName_input;
    EditText lastName_input;
    EditText email_input;
    EditText pass_input;
    EditText confirmPass_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        inputSetup();
    }

    protected void inputSetup() {
        firstName_input = (EditText) findViewById(R.id.first_name_input);
        lastName_input = (EditText) findViewById(R.id.last_name_input);
        email_input = (EditText) findViewById(R.id.email_input);
        pass_input = (EditText) findViewById(R.id.password_input);
        confirmPass_input = (EditText) findViewById(R.id.confirm_password_input);
    }

    protected void createAccount(View v) {
        Intent loginIntent = new Intent(this, ActivityLogin.class);
        this.startActivity(loginIntent);
    }
}
