package com.example.noah.onthefly.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.fragments.FragmentPasswordReset;

public class ActivityForgotPassword extends AppCompatActivity {

    EditText emailField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailField = (EditText) findViewById(R.id.email_field);
    }

    protected void forgotPassword(View v) {
        String email = emailField.getText().toString();
        FragmentPasswordReset fragmentPasswordReset = new FragmentPasswordReset(this, email);
        fragmentPasswordReset.show();
    }
}
