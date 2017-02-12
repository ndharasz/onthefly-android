package com.example.noah.onthefly.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.noah.onthefly.R;

import java.util.zip.Inflater;

public class ActivityFlightList extends AppCompatActivity {
    private SharedPreferences loginPrefs;
    private SharedPreferences.Editor loginPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);
//      for future dynamic adding usage:
//        LayoutInflater item = (LayoutInflater)
//                this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//        View v = item.inflate(R.layout.item_flight, (ViewGroup)findViewById(R.id.upcoming_flight_list));
//        ((Button)v.findViewById(R.id.upcoming_flight_edit)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(ActivityFlightList.this, ActivityEditFlight.class));
//            }
//        });
    }

    protected void logout(View v) {
        loginPrefs = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPrefs.edit();
        loginPrefsEditor.putBoolean("saveLogin", false);
        loginPrefsEditor.commit();
        Intent logoutIntent = new Intent(this, ActivityLogin.class);
        this.startActivity(logoutIntent);
    }

    protected void createFlight(View v) {
        Intent createFlightIntent = new Intent(this, ActivityCreateFlight.class);
        this.startActivity(createFlightIntent);
    }
}
