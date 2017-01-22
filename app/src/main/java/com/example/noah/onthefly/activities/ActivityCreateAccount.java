package com.example.noah.onthefly.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.noah.onthefly.R;

public class ActivityCreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        inputSetup();
    }

    protected void inputSetup() {
    }
}
