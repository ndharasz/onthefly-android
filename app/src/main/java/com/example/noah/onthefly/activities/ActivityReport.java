package com.example.noah.onthefly.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.noah.onthefly.R;

public class ActivityReport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
    }

    public void send(View inputButton) {
        AlertDialog alertDialog = new AlertDialog.Builder(ActivityReport.this,  AlertDialog.THEME_DEVICE_DEFAULT_DARK).create();
        alertDialog.setTitle("Report Sent!");
        alertDialog.getWindow().setLayout(600, 400);
        EditText getNameField = (EditText) findViewById(R.id.user_input_email);
        String castToString = (String) getNameField.getText().toString();

        alertDialog.setMessage("Your weight and balance report has been sent to " + castToString);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK. Return to Home.",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ActivityReport.this, ActivityFlightList.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
