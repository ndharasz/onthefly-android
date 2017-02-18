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
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.models.Flight;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.zip.Inflater;

import static com.example.noah.onthefly.R.layout.item_flight;

public class ActivityFlightList extends AppCompatActivity {
    private SharedPreferences loginPrefs;
    private SharedPreferences.Editor loginPrefsEditor;
    private TableLayout flightTable;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference flightListReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_list);
        flightTable = (TableLayout) findViewById(R.id.flight_table);

        firebaseDatabase = FirebaseDatabase.getInstance();
        flightListReference = firebaseDatabase.getReference("flights");
        flightListReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Flight flight = (Flight) dataSnapshot.getValue(Flight.class);
                // ignore this data if it doesn't belong to this user
                if (!flight.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    return;
                }

                TableRow v = (TableRow) LayoutInflater.from(ActivityFlightList.this).inflate(item_flight, null);

                ((Button)v.findViewById(R.id.upcoming_flight_edit)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ActivityFlightList.this, ActivityEditFlight.class));
                    }
                });

                ((TextView) v.findViewById(R.id.upcoming_flight_date))
                        .setText(getDisplayDate(flight.getDate()));
                ((TextView) v.findViewById(R.id.upcoming_flight_arrival))
                        .setText(flight.getArriveAirport());
                ((TextView) v.findViewById(R.id.upcoming_flight_depart))
                        .setText(flight.getDepartAirport());
                flightTable.addView(v);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Changing a child can only happen in another screen
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Flight flight = (Flight) dataSnapshot.getValue(Flight.class);
                if (!flight.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    return;
                }
                // Also can't happen unless there's a delete button in the list
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // takes in the database date and displays it
    public String getDisplayDate(String date) {
        if (date.contains("-")) {
            return date.substring(0, date.lastIndexOf('-'));
        } else {
            return date;
        }
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
