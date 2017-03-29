package com.example.noah.onthefly.activities;

import android.content.Intent;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.fragments.FragmentCargoView;
import com.example.noah.onthefly.fragments.FragmentDetailsView;
import com.example.noah.onthefly.fragments.FragmentPassengerView;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.util.FlightManager;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityEditFlight extends FragmentActivity {
    static final String TAG = "ActivityEditFlight";
    static final int NUM_TABS = 3;

    TabAdapter tabAdapter;
    ViewPager tabPager;

    Flight curFlight;
    FlightManager flightManager;

    Button detailViewButton;
    Button passengerViewButton;
    Button cargoViewButton;
    Button genReportButton;

    ImageView trash;

    private int[] trashCoords;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flight);

        Intent i = getIntent();
        curFlight = (Flight) i.getSerializableExtra("FlightDetails");

        title = (TextView) findViewById(R.id.EditFlightTitle);
        title.setText(curFlight.getDepartAirport() + " \u2192 " + curFlight.getArriveAirport());

        setupButtons();
        setupFlightManager();
        setupTabs();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    protected void setupFlightManager() {
        // Sets up the flight manager utility.  If a fragment alters the values, the listeners
        //   will update the title of the activity.
        flightManager = new FlightManager(curFlight, FirebaseAuth.getInstance().getCurrentUser().getUid());
        flightManager.setAirportChangedListener(new FlightManager.AirportChangedListener() {
            @Override
            public void onAirportsChanged(String dep, String arr) {
                title.setText(dep + " \u2192 " + arr);
            }
        });
    }

    protected void setupButtons() {
        detailViewButton = (Button) findViewById(R.id.details_view_button);
        passengerViewButton = (Button) findViewById(R.id.passenger_view_button);
        cargoViewButton = (Button) findViewById(R.id.cargo_view_button);
        detailViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabPager.setCurrentItem(0);
            }
        });
        passengerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabPager.setCurrentItem(1);
            }
        });
        cargoViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabPager.setCurrentItem(2);
            }
        });

        genReportButton = (Button) findViewById(R.id.gen_report_button);
    }

    protected void setupTabs() {
        tabAdapter = new TabAdapter(getSupportFragmentManager(),
                detailViewButton, passengerViewButton, cargoViewButton);

        tabPager = (ViewPager)findViewById(R.id.edit_flight_pager);
        tabPager.setAdapter(tabAdapter);
        tabPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    detailViewButton.setBackgroundResource(R.drawable.edit_flight_tab_selected);
                    passengerViewButton.setBackgroundResource(R.drawable.edit_flight_tab);
                    cargoViewButton.setBackgroundResource(R.drawable.edit_flight_tab);
                    findViewById(R.id.activity_edit_flight).invalidate();
                    tabAdapter.getDetailsView().initializeFields();
                } else if (position == 1) {
                    detailViewButton.setBackgroundResource(R.drawable.edit_flight_tab);
                    passengerViewButton.setBackgroundResource(R.drawable.edit_flight_tab_selected);
                    cargoViewButton.setBackgroundResource(R.drawable.edit_flight_tab);
                    findViewById(R.id.activity_edit_flight).invalidate();
                } else {
                    detailViewButton.setBackgroundResource(R.drawable.edit_flight_tab);
                    passengerViewButton.setBackgroundResource(R.drawable.edit_flight_tab);
                    cargoViewButton.setBackgroundResource(R.drawable.edit_flight_tab_selected);
                    findViewById(R.id.activity_edit_flight).invalidate();
                    tabAdapter.getCargoView().initializeFields();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        tabPager.setCurrentItem(1);
    }

    public class TabAdapter extends FragmentPagerAdapter {
        FragmentDetailsView detailsView;
        FragmentPassengerView passengerView;
        FragmentCargoView cargoView;
        public TabAdapter(FragmentManager fm, Button detailsTab, Button passengerTab, Button cargoTab) {
            super(fm);
            detailsView = new FragmentDetailsView();
            detailsView.setTabButton(detailsTab);
            detailsView.setFlightManager(flightManager);
            passengerView = new FragmentPassengerView();
            passengerView.setTabButton(passengerTab);
            passengerView.setFlightManager(flightManager);
            cargoView = new FragmentCargoView();
            cargoView.setTabButton(cargoTab);
            cargoView.setFlightManager(flightManager);
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                detailsView.onHiddenChanged(false);
                passengerView.onHiddenChanged(true);
                cargoView.onHiddenChanged(true);
                return detailsView;
            } else if (position == 1) {
                detailsView.onHiddenChanged(true);
                passengerView.onHiddenChanged(false);
                cargoView.onHiddenChanged(true);
                return passengerView;
            } else {
                detailsView.onHiddenChanged(true);
                passengerView.onHiddenChanged(true);
                cargoView.onHiddenChanged(false);
                return cargoView;
            }
        }

        public FragmentDetailsView getDetailsView() {
            return detailsView;
        }

        public FragmentCargoView getCargoView() {
            return cargoView;
        }

        public FragmentPassengerView getPassengerView() {
            return passengerView;
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
        reportIntent.putExtra("flight", curFlight);
        startActivity(reportIntent);
    }

    public FlightManager getFlightManager() {
        return flightManager;
    }

    protected void delete(View v) {
        flightManager.delete();
        Intent flightListIntent = new Intent(this, ActivityFlightList.class);
        startActivity(flightListIntent);
        finish();
    }
}
