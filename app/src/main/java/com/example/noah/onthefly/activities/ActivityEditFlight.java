package com.example.noah.onthefly.activities;

import android.content.Intent;
import android.icu.util.BuddhistCalendar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.fragments.FragmentCargoView;
import com.example.noah.onthefly.fragments.FragmentPassengerView;

public class ActivityEditFlight extends FragmentActivity {
    static final int NUM_TABS = 2;

    TabAdapter tabAdapter;
    ViewPager tabPager;

    Button passengerViewButton;
    Button cargoViewButton;
    Button genReportButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flight);

        setupFlightDetails();
        setupButtons();
        setupPassengerCargoViews();
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
}
