package com.example.noah.onthefly.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.example.noah.onthefly.R;

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

        email_field = (EditText) findViewById(R.id.email_field);

        checkboxSetup();
        alertSetup();
    }

    protected void checkboxSetup() {
        send_registed_checkbox = (CheckBox)findViewById(R.id.send_to_reg_checkbox);
        send_other_checkbox = (CheckBox)findViewById(R.id.send_to_other_checkbox);
        save_report_checkbox = (CheckBox)findViewById(R.id.save_report_checkbox);
    }

    protected void alertSetup() {
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
        confirmationDialog = passwordResetConfirmation.create();
    }

    protected void forgotPassword(View v) {
        Boolean send_reg = send_registed_checkbox.isChecked();
        Boolean send_other = send_other_checkbox.isChecked();
        Boolean save_local = save_report_checkbox.isChecked();
        if(send_other) {
            String email = email_field.getText().toString();
        }
        confirmationDialog.show();
    }
}
