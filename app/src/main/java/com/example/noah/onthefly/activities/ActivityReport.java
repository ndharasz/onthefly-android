package com.example.noah.onthefly.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.noah.onthefly.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    public void send(View inputButton) {
        AlertDialog alertDialog = new AlertDialog.Builder(ActivityReport.this,  AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Report Sent!");
        alertDialog.getWindow().setLayout(600, 400);
        EditText getNameField = (EditText) findViewById(R.id.user_input_email);
        String castToString = (String) getNameField.getText().toString();

        try {
            isEmailValid(castToString);
            alertDialog.setMessage("Your weight and balance report has been sent to " + castToString + ".");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK. Return to Home.",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ActivityReport.this, ActivityFlightList.class);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } catch (IllegalArgumentException e) {
            android.app.AlertDialog notValid = new android.app.AlertDialog.Builder(ActivityReport.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
            notValid.setTitle("Invalid Email");
            notValid.setMessage("The email address you entered was not valid or you entered an illegal character.");
            notValid.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Try Again",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            notValid.show();
        }
    }

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
}
