package com.example.noah.onthefly.activities;

import android.app.DialogFragment;
import android.content.Context;
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

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.fragments.FragmentDatePicker;
import com.example.noah.onthefly.fragments.FragmentTimePicker;
import com.example.noah.onthefly.interfaces.CallsDatePicker;
import com.example.noah.onthefly.interfaces.CallsTimePicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityCreateFlight extends AppCompatActivity implements CallsDatePicker, CallsTimePicker {

    private EditText dateField;
    private EditText timeField;
    private DialogFragment datePickerFragment;
    private DialogFragment timePickerFragment;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flight);

        dateField = (EditText) findViewById(R.id.flight_date);
        timeField = (EditText) findViewById(R.id.flight_time);

        Spinner plane_spinner = (Spinner) findViewById(R.id.choose_plane_spinner);
        String[] planes = new String[]{
                "Choose a plane", "Plane 1", "Plane 2"
        };
        final List<String> planesList = new ArrayList<>(Arrays.asList(planes));

        Spinner dept_loc_spinner = (Spinner) findViewById(R.id.choose_dept_airport_spinner);
        String[] dept_locs = new String[] {
                "Departure location", "ATL", "SEA", "MIA"
        };
        final List<String> deptList = new ArrayList<>(Arrays.asList(dept_locs));

        Spinner arr_loc_spinner = (Spinner) findViewById(R.id.choose_arr_airport_spinner);
        String[] arr_locs = new String[] {
                "Arrical location", "ATL", "SEA", "MIA"
        };
        final List<String> arrList = new ArrayList<>(Arrays.asList(arr_locs));

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> planeArrayAdapter = new ArrayAdapterWithHint<String>(
                this, android.R.layout.simple_spinner_item, planesList);
        final ArrayAdapter<String> deptArrayAdapter = new ArrayAdapterWithHint<String>(
                this, android.R.layout.simple_spinner_item, deptList);
        final ArrayAdapter<String> arrArrayAdapter = new ArrayAdapterWithHint<String>(
                this, android.R.layout.simple_spinner_item, arrList);
        plane_spinner.setAdapter(planeArrayAdapter);
        dept_loc_spinner.setAdapter(deptArrayAdapter);
        arr_loc_spinner.setAdapter(arrArrayAdapter);
    }

    protected void showDate(View v) {
        showDatePicker(v);
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

    protected void showTime(View v) {
        showTimePicker(v);
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

    public void submit(View v) {
        Intent editFlightIntent = new Intent(this, ActivityEditFlight.class);
        this.startActivity(editFlightIntent);
    }


}
