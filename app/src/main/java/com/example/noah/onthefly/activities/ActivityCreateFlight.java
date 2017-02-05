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

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.fragments.FragmentDatePicker;
import com.example.noah.onthefly.fragments.FragmentTimePicker;
import com.example.noah.onthefly.interfaces.CallsDatePicker;
import com.example.noah.onthefly.interfaces.CallsTimePicker;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityCreateFlight extends AppCompatActivity implements CallsDatePicker, CallsTimePicker {

    private Spinner plane_spinner;
    private Spinner dept_loc_spinner;
    private Spinner arr_loc_spinner;
    private EditText dateField;
    private EditText timeField;
    private DialogFragment datePickerFragment;
    private DialogFragment timePickerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_flight);

        dateTimeSetup();
        spinnerSetup();
    }

    protected  void dateTimeSetup() {
        dateField = (EditText) findViewById(R.id.flight_date);
        timeField = (EditText) findViewById(R.id.flight_time);
    }

    protected void spinnerSetup() {

        plane_spinner = (Spinner) findViewById(R.id.choose_plane_spinner);
        dept_loc_spinner = (Spinner) findViewById(R.id.choose_dept_airport_spinner);
        arr_loc_spinner = (Spinner) findViewById(R.id.choose_arr_airport_spinner);

        final List<String> planesList = new ArrayList<>(Arrays.asList(new String[]{
                "Choose A Plane", "Plane 1", "Plane 2"
        }));
        final List<String> deptList = new ArrayList<>(Arrays.asList(new String[] {
                "Departure Location", "ATL", "SEA", "MIA"
        }));
        final List<String> arrList = new ArrayList<>(Arrays.asList(new String[] {
                "Arrival Location", "ATL", "SEA", "MIA"
        }));

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

    protected void submit(View v) {
        String plane = plane_spinner.getSelectedItem().toString();
        String dept_loc = dept_loc_spinner.getSelectedItem().toString();
        String arr_loc = arr_loc_spinner.getSelectedItem().toString();
        String date = dateField.getText().toString();
        String time = timeField.getText().toString();


        if (dept_loc.matches("Departure Location") || arr_loc.matches("Arrival Location") || date.matches("") || time.matches("") || plane.matches("Choose A Plane")) {
            android.app.AlertDialog notValid = new android.app.AlertDialog.Builder(ActivityCreateFlight.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
            notValid.setTitle("Flight Creation Error");
            notValid.setMessage("One or more of your fields were empty or invalid.");
            notValid.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            notValid.show();
            return;
        }


        if(!dateBefore()) {
            Intent editFlightIntent = new Intent(this, ActivityEditFlight.class);
            this.startActivity(editFlightIntent);
        }

    }

    protected boolean dateBefore() throws IllegalArgumentException {
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

            if (parsedDate.before(today)) {
                throw new IllegalArgumentException();
            }

        } catch (ParseException e) {
            //error in parse

        } catch (IllegalArgumentException e) {
            android.app.AlertDialog notValid = new android.app.AlertDialog.Builder(ActivityCreateFlight.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
            notValid.setTitle("Input Error");
            notValid.setMessage("You have entered a flight date before the current date. On the Fly complies with FAA regulations on Weight and Balance Report creation prior to flight.");
            notValid.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "Re-enter Date",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            notValid.show();
            return true;
        }
        return false;
    }


}
