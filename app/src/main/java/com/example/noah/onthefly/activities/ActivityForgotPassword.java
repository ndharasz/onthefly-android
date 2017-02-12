package com.example.noah.onthefly.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.util.Mailer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityForgotPassword extends AppCompatActivity {

    EditText email_field;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        email_field = (EditText)findViewById(R.id.email_field_forgot_pass);
    }

    public void forgotPassword(View inputButton) {
        final String email = email_field.getText().toString();
        if(!email.equals("")) {
            if(Mailer.isEmailValid(email)) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("ActivityForgotPassword", "Email sent.");
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "Invalid email.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Empty email field.", Toast.LENGTH_LONG).show();
        }
    }
}
