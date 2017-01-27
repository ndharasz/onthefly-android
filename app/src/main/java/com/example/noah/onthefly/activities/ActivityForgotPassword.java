package com.example.noah.onthefly.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.noah.onthefly.R;

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
        AlertDialog.Builder passwordResetConfirmation = new AlertDialog.Builder(this,
                R.style.PasswordResetConfirmationDialog);
        passwordResetConfirmation.setTitle("Password has been reset");
        passwordResetConfirmation
                .setMessage("Please check your email to reset your password.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Context c = ((Dialog) dialog).getContext();
                        Intent loginIntent = new Intent(c, ActivityLogin.class);
                        c.startActivity(loginIntent);
                    }
                });
        AlertDialog confirmationDialog = passwordResetConfirmation.create();
        confirmationDialog.show();
    }
}
