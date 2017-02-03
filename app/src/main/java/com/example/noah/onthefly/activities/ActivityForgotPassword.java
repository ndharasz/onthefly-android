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

import com.example.noah.onthefly.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityForgotPassword extends AppCompatActivity {

    CheckBox send_registed_checkbox;
    CheckBox send_other_checkbox;
    EditText email_field;
    CheckBox save_report_checkbox;
    AlertDialog confirmationDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        //checkboxSetup();
        //alertSetup();
    }
/*
    protected void checkboxSetup() {
        send_registed_checkbox = (CheckBox)findViewById(R.id.send_to_reg_checkbox);
        send_other_checkbox = (CheckBox)findViewById(R.id.send_to_other_checkbox);
        save_report_checkbox = (CheckBox)findViewById(R.id.save_report_checkbox);
    }

    protected void alertSetup() {
        AlertDialog.Builder passwordResetConfirmation = new AlertDialog.Builder(this,
                R.style.PasswordResetConfirmationDialog);
        String castToString = (String) email_field.getText().toString();
        passwordResetConfirmation.setTitle("Password Reset");
        passwordResetConfirmation
                .setMessage("Use the link sent to " + castToString + " to reset your password.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Context c = ((Dialog) dialog).getContext();
                        Intent loginIntent = new Intent(c, ActivityLogin.class);
                        c.startActivity(loginIntent);
                    }
                });
        confirmationDialog = passwordResetConfirmation.create();
    }
    */

    public void isEmailValid(String email) throws IllegalArgumentException{
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        if (!isValid) {
            throw new IllegalArgumentException();
        }
    }

    public void forgotPassword(View inputButton) {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(ActivityForgotPassword.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Password Reset");
        alertDialog.getWindow().setLayout(600, 400);
        EditText address = (EditText) findViewById(R.id.email_field);

        try {

            isEmailValid(address.getText().toString());

            alertDialog.setMessage("Check " + address.getText().toString() + " to reset your password.");
            alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Return to Login",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ActivityForgotPassword.this, ActivityLogin.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } catch (IllegalArgumentException e) {
            android.app.AlertDialog notValid = new android.app.AlertDialog.Builder(ActivityForgotPassword.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
            notValid.setTitle("Invalid Email");
            notValid.setMessage("The email address you entered was not valid or you entered an illegal character.");
            notValid.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Try Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ActivityForgotPassword.this, ActivityForgotPassword.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
            notValid.show();
        }

        resetPass(address.getText().toString());
    }

    public void resetPass(String emailAddress) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("ActivityForgotPassword", "Email sent.");
                        }
                    }
                });
    }


}
