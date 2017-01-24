package com.example.noah.onthefly.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.noah.onthefly.R;

public class ActivityFlightList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);
    }

    protected void logout (View v) {
        Intent logoutIntent = new Intent(this, ActivityLogin.class);
        this.startActivity(logoutIntent);
    }

    protected void createFlight(View v) {
        Intent createFlightIntent = new Intent(this, ActivityCreateFlight.class);
        this.startActivity(createFlightIntent);
    }
}
