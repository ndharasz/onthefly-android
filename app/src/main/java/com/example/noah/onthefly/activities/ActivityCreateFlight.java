package com.example.noah.onthefly.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
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
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.util.Airports;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
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
    private DialogFragment datePickerFragment;
    private DialogFragment timePickerFragment;
    private AutoCompleteTextView departures;
    private AutoCompleteTextView arrivals;
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
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    protected void airportSetup() {
        dept = Airports.getAirports();
        arr = Airports.getAirports();

        departures = (AutoCompleteTextView)findViewById(R.id.depPick);
        arrivals = (AutoCompleteTextView)findViewById(R.id.arrPick);

        ArrayAdapter deptAdapter = new
                ArrayAdapter(this,android.R.layout.simple_list_item_1,dept);

        departures.setAdapter(deptAdapter);
        departures.setThreshold(1);

        ArrayAdapter arrAdapter = new
                ArrayAdapter(this,android.R.layout.simple_list_item_1,arr);

        arrivals.setAdapter(arrAdapter);
        arrivals.setThreshold(1);
    }

    protected  void dateTimeSetup() {
        dateField = (EditText) findViewById(R.id.flight_date);
        timeField = (EditText) findViewById(R.id.flight_time);
    }

    protected void spinnerSetup() {

        plane_spinner = (Spinner) findViewById(R.id.choose_plane_spinner);

        final List<String> planesList = new ArrayList<>(Arrays.asList(new String[]{
                "Choose A Plane", "Plane 1", "Plane 2"
        }));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> planeArrayAdapter = new ArrayAdapterWithHint<String>(
                this, android.R.layout.simple_spinner_item, planesList);

        plane_spinner.setAdapter(planeArrayAdapter);

    }

    private class ArrayAdapterWithHint<T> extends ArrayAdapter {
        public ArrayAdapterWithHint(
                Context c, int resource, List<T> objects) {
            super(c, resource, objects);
            this.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        }

        @Override
        public boolean isEnabled(int position) {
            if (position == 0) {
                // Disable the first item from Spinner
                // First item will be use for hint
                return false;
            } else {
                return true;
            }
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
            if (position == 0) {
                // Set the hint text color gray
                tv.setTextColor(Color.GRAY);
            } else {
                tv.setTextColor(Color.BLACK);
            }
            return view;
        }
    }

    public void showDatePicker(View v) {
        if (datePickerFragment == null) {
            datePickerFragment = new FragmentDatePicker();
            datePickerFragment.show(getFragmentManager(), "datePickerFragment");
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
            timePickerFragment.show(getFragmentManager(), "datePickerFragment");
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


        if (dept_loc.matches("") || arr_loc.matches("") ||
                date.matches("") || time.matches("") || plane.matches("Choose A Plane")) {
            Toast.makeText(this, "Please fill empty fields.", Toast.LENGTH_LONG).show();
        } else if (!Airports.isAirportValid(dept_loc) || !Airports.isAirportValid(arr_loc)) {
            Toast.makeText(this, "Please make sure you are selecting an airport from the dropdown menu. "
            + "No extra characters should be included in your arrival or departure airport entries.", Toast.LENGTH_LONG).show();

        } else if(!dateBefore()) {
            Flight newFlight = new Flight(plane, dept_loc, arr_loc, date, time,
                    mAuth.getCurrentUser().getUid());
            mDatabase.child("flights").push().setValue(newFlight);

            Intent editFlightIntent = new Intent(this, ActivityEditFlight.class);
            this.startActivity(editFlightIntent);
        }

    }

    protected boolean dateBefore() {
        String date = dateField.getText().toString();
        String time = timeField.getText().toString();
        SimpleDateFormat date12Format = new SimpleDateFormat("hh:mm a");
        SimpleDateFormat date24Format = new SimpleDateFormat("HH:mm");

        try {
            String update = date24Format.format(date12Format.parse(time));
            date = date + " " + update;
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy hh:mm");
            Date parsedDate = formatter.parse(date);
            return parsedDate.before(today);
        } catch (Exception e) {
            //this should not happen since we format the date ourselves
        }
        return false;
    }
}