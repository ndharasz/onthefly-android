package com.example.noah.onthefly.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.noah.onthefly.R;

public class ActivityCreateFlight extends AppCompatActivity {
    EditText firstNameField;
    EditText lastNameField;
    EditText emailField;
    EditText passwordField;
    EditText confirmPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acivity_create_flight);
        inputSetup();
    }

    protected void inputSetup() {
    }
}
