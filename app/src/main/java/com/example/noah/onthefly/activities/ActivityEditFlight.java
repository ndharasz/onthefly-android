package com.example.noah.onthefly.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.BuddhistCalendar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.fragments.FragmentCargoView;
import com.example.noah.onthefly.fragments.FragmentPassengerView;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.models.Passenger;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.util.Airports;
import com.example.noah.onthefly.util.CustomAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityEditFlight extends FragmentActivity {
    static final int NUM_TABS = 2;

    TabAdapter tabAdapter;
    ViewPager tabPager;

    Flight curFlight;

    Button passengerViewButton;
    Button cargoViewButton;
    Button genReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flight);

        Intent i = getIntent();
        curFlight = (Flight) i.getSerializableExtra("FlightDetails");

        TextView title = (TextView) findViewById(R.id.EditFlightTitle);
        title.setText(curFlight.getDepartAirport() + " \u2192 " + curFlight.getArriveAirport());

        setupFlightDetails();
        setupButtons();
        setupPassengerCargoViews();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    protected void setupFlightDetails(){
    }

    protected void setupButtons() {
        passengerViewButton = (Button) findViewById(R.id.passenger_view_button);
        cargoViewButton = (Button) findViewById(R.id.cargo_view_button);
        passengerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabPager.setCurrentItem(0);
            }
        });
        cargoViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabPager.setCurrentItem(1);
            }
        });

        genReportButton = (Button) findViewById(R.id.gen_report_button);
    }

    protected void setupPassengerCargoViews() {
        tabAdapter = new TabAdapter(getSupportFragmentManager(),
                passengerViewButton, cargoViewButton);

        tabPager = (ViewPager)findViewById(R.id.edit_flight_pager);
        tabPager.setAdapter(tabAdapter);
        tabPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if(position == 0) {
                    passengerViewButton.setBackgroundResource(R.drawable.edit_flight_tab_selected);
                    cargoViewButton.setBackgroundResource(R.drawable.edit_flight_tab);
                    findViewById(R.id.activity_edit_flight).invalidate();
                } else {
                    passengerViewButton.setBackgroundResource(R.drawable.edit_flight_tab);
                    cargoViewButton.setBackgroundResource(R.drawable.edit_flight_tab_selected);
                    findViewById(R.id.activity_edit_flight).invalidate();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public class TabAdapter extends FragmentPagerAdapter {
        FragmentPassengerView passengerView;
        FragmentCargoView cargoView;
        public TabAdapter(FragmentManager fm, Button passengerTab, Button cargoTab) {
            super(fm);
            passengerView = new FragmentPassengerView();
            passengerView.setTabButton(passengerTab);
            cargoView = new FragmentCargoView();
            cargoView.setTabButton(cargoTab);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) {
                passengerView.onHiddenChanged(false);
                cargoView.onHiddenChanged(true);
                return passengerView;
            } else {
                passengerView.onHiddenChanged(true);
                cargoView.onHiddenChanged(false);
                return cargoView;
            }

        }
    }


    @Override
    public void onBackPressed() {
        Intent flightListIntent = new Intent(this, ActivityFlightList.class);
        startActivity(flightListIntent);
        finish();
    }

    protected void generateReport(View v) {
        Intent reportIntent = new Intent(this, ActivityReport.class);
        this.startActivity(reportIntent);
    }

    public void editDetails(View view) {

        View promptView = LayoutInflater.from(this).inflate(R.layout.flight_edit_details_dialog, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        alertDialogBuilder.setView(promptView);
        alertDialogBuilder.setTitle("Edit Flight Details");

        final EditText name = (EditText) promptView.findViewById(R.id.PlaneName);
        name.setText(curFlight.getPlane());

        final EditText depart = (EditText) promptView.findViewById(R.id.DeptAirport);
        depart.setText(curFlight.getDepartAirport());

        final EditText arrive = (EditText) promptView.findViewById(R.id.ArrivalAirport);
        arrive.setText(curFlight.getArriveAirport());

        final EditText date = (EditText) promptView.findViewById(R.id.FlightDate);
        date.setText(curFlight.getDate());

        final EditText time = (EditText) promptView.findViewById(R.id.FlightTime);
        time.setText(curFlight.getTime());

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        curFlight.setArriveAirport(arrive.getText().toString());
                        curFlight.setDepartAirport(depart.getText().toString());

                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
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
}
