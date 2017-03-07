package com.example.noah.onthefly.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.fragments.FragmentDatePicker;
import com.example.noah.onthefly.fragments.FragmentTimePicker;
import com.example.noah.onthefly.interfaces.CallsDatePicker;
import com.example.noah.onthefly.interfaces.CallsTimePicker;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.util.Airports;
import com.example.noah.onthefly.util.ArrayAdapterWithHint;
import com.example.noah.onthefly.util.CustomAdapter;
import com.example.noah.onthefly.util.GlobalVars;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.widget.AutoCompleteTextView;

public class ActivityCreateFlight extends AppCompatActivity implements CallsDatePicker, CallsTimePicker {

    private Spinner plane_spinner;
    private EditText dateField;
    private EditText timeField;
    private FragmentDatePicker datePickerFragment;
    private FragmentTimePicker timePickerFragment;
    private AutoCompleteTextView departures;
    private AutoCompleteTextView arrivals;
    private EditText duration;
    private EditText start_fuel;
    private EditText flow_rate;
    private EditText taxi_fuel;
    private String[] dept;
    private String[] arr;


    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flight);

        airportSetup();
        dateTimeSetup();
        spinnerSetup();
        inputSetup();
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    protected void airportSetup() {
        dept = Airports.getAirports();
        arr = Airports.getAirports();

        departures = (AutoCompleteTextView)findViewById(R.id.depPick);
        arrivals = (AutoCompleteTextView)findViewById(R.id.arrPick);

        CustomAdapter deptAdapter = new
                CustomAdapter(this,android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(dept)));

        departures.setAdapter(deptAdapter);
        departures.setThreshold(2);

        CustomAdapter arrAdapter = new
                CustomAdapter(this,android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(arr)));

        arrivals.setAdapter(arrAdapter);
        arrivals.setThreshold(2);
    }

    protected  void dateTimeSetup() {
        dateField = (EditText) findViewById(R.id.flight_date);
        timeField = (EditText) findViewById(R.id.flight_time);
    }

    protected void spinnerSetup() {

        plane_spinner = (Spinner) findViewById(R.id.choose_plane_spinner);
      
        final List<String> planesList = Plane.readAllPlaneNames(this);
        planesList.add(0, "Choose plane");

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> planeArrayAdapter = new ArrayAdapterWithHint<String>(
                this, android.R.layout.simple_spinner_item, planesList);

        plane_spinner.setAdapter(planeArrayAdapter);

    }
  
    protected void inputSetup() {
        duration = (EditText)findViewById(R.id.flight_duration);
        start_fuel = (EditText)findViewById(R.id.start_fuel);
        flow_rate = (EditText)findViewById(R.id.fuel_flow);
        taxi_fuel = (EditText)findViewById(R.id.taxi_fuel);
    }

    public void showDatePicker(View v) {
        if (datePickerFragment == null) {
            datePickerFragment = new FragmentDatePicker();
            datePickerFragment.setParent(this);
            datePickerFragment.show(getSupportFragmentManager(), "datePickerFragment");
        }
    }

    public void hideDatePicker(String date) {
        if (datePickerFragment != null) {
            if (date.compareTo("") != 0) {
                dateField.setText(date);
            }
            datePickerFragment.dismiss();
            datePickerFragment = null;
            Log.d("Tag", "DatePicker dismissed");
        }
    }

    public void showTimePicker(View v) {
        if (timePickerFragment == null) {
            timePickerFragment = new FragmentTimePicker();
            timePickerFragment.setParent(this);
            timePickerFragment.show(getSupportFragmentManager(), "datePickerFragment");
        }
    }

    public void hideTimePicker(String time) {
        if (timePickerFragment != null) {
            if (time.compareTo("") != 0) {
                timeField.setText(time);
            }
            timePickerFragment.dismiss();
            timePickerFragment = null;
        }
    }

    protected void submit(View v) {
        String plane = plane_spinner.getSelectedItem().toString();
        String date = dateField.getText().toString();
        String time = timeField.getText().toString();
        String dept_loc = departures.getText().toString();
        String arr_loc = arrivals.getText().toString();
        double flight_duration = Double.parseDouble(duration.getText().toString());
        double starting_fuel = Double.parseDouble(start_fuel.getText().toString());
        double fuel_flow = Double.parseDouble(flow_rate.getText().toString());
        double taxi_usage = Double.parseDouble(taxi_fuel.getText().toString());
        Log.d("TEST", Boolean.toString(dateAfterToday()));

        if (dept_loc.matches("") || arr_loc.matches("") ||
                date.matches("") || time.matches("") || plane.matches("Choose A Plane")) {
            Toast.makeText(this, "Please fill empty fields.", Toast.LENGTH_LONG).show();
        } else if (!Airports.isAirportValid(dept_loc) || !Airports.isAirportValid(arr_loc)) {
            Toast.makeText(this, "Please make sure you are selecting an airport from the dropdown menu. "
            + "No extra characters should be included in your arrival or departure airport entries.", Toast.LENGTH_LONG).show();
        } else if(dateAfterToday()) {
            Log.d("TEST", "Reached2");
            String parsed_dept_loc = parsePlaneCode(dept_loc);
            String parsed_arr_loc = parsePlaneCode(arr_loc);

            Flight newFlight = new Flight(plane, parsed_dept_loc, parsed_arr_loc, date, time,
                    mAuth.getCurrentUser().getUid(), flight_duration, starting_fuel, fuel_flow, taxi_usage);
            DatabaseReference pushRef = mDatabase.child(GlobalVars.FLIGHT_DB).push();
            pushRef.setValue(newFlight);
            newFlight.setKey(pushRef.getKey());
            Log.d("TEST", "Reached3");
            Intent editFlightIntent = new Intent(this, ActivityEditFlight.class);
            editFlightIntent.putExtra("FlightDetails", newFlight);
            this.startActivity(editFlightIntent);
        }

    }

    protected boolean dateAfterToday() {
        String date = dateField.getText().toString();
        String time = timeField.getText().toString();
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm a");
        try {
            Date given = formatter.parse(date+" "+time);
            Date today = formatter.parse(formatter.format(new Date()));
            return given.after(today);
        } catch (Exception e) {
            Log.e("Date Error", e.getMessage());
            //this should not happen since we format the date ourselves
        }
        return false;
    }

    public String parsePlaneCode(String airport) {
        if (airport.contains("(") && airport.contains(")")
                && airport.indexOf('(') < airport.lastIndexOf(')')) {
            return airport.substring(airport.indexOf('(') + 1, airport.lastIndexOf(')'));
        } else {
            return airport;
        }
    }
}