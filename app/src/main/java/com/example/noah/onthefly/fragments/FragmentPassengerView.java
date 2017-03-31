package com.example.noah.onthefly.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.activities.ActivityEditFlight;
import com.example.noah.onthefly.models.Passenger;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.models.WeightAndBalance;
import com.example.noah.onthefly.util.FlightManager;
import com.example.noah.onthefly.widgets.PlaneView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;

public class FragmentPassengerView extends Fragment {
    private static final String TAG = "FragmentPassengerView";

    Button tab;
    private PlaneView passengerView;
    private FlightManager flightManager;

    private static int fragHeight;
    private static int fragWidth;

    public FragmentPassengerView setTabButton(Button tab) {

        this.tab = tab;
        return new FragmentPassengerView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        flightManager = ((ActivityEditFlight) getActivity()).getFlightManager();
        final Plane plane = Plane.readFromFile(getContext(), flightManager.getPlane());

        List<Double> passengerArms = new LinkedList<>();
        passengerArms.add(plane.getPilotSeatsArm());
        for (Double arm : plane.getRowArms()) {
            passengerArms.add(arm);
        }

        RelativeLayout passengerLayout = (RelativeLayout)inflater.inflate(
                R.layout.fragment_passenger_view, container, false);

        int numColumns = 2; // Are we to assume this will always be 2?..
        passengerView = new PlaneView(getContext(), numColumns, plane.getNumSeats(), passengerArms);

        fragHeight = this.getHeight();
        fragWidth = this.getWidth();
        Log.d("FRAGMENT DIMENS", + fragHeight + " " + fragWidth);

        passengerLayout.addView(passengerView);

        passengerView.setOnPassengerAddedListener(new PlaneView.PassengerAddedListener() {
            @Override
            public void onPassengerAdded(int seat, Passenger passenger) {
                flightManager.setPassenger(seat + 1, passenger);
            }
        });

        passengerView.setOnPassengerRemovedListener(new PlaneView.PassengerRemovedListener() {
            @Override
            public void onPassengerRemoved(int newSeat) {
                flightManager.removePassenger(newSeat + 1);
            }
        });

        passengerView.setOnActionEndedListener(new PlaneView.ActionEndedListener() {
            @Override
            public void onActionEnded() {
                flightManager.save();
            }
        });

        for (int i = 0; i < plane.getNumSeats(); i++) {
            Passenger passenger = flightManager.getPassenger(i);
            passengerView.setPassenger(i, passenger);
        }

        return passengerLayout;
    }

    public void setFlightManager(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    public void warn(boolean b) {
        if (b) {

            Log.d(TAG, "DANGER! Plane cannot fly as is");
        } else {
            
            Log.d(TAG, "Plane is safe to fly");
        }
    }

    public static int getHeight() {
        return fragHeight;
    }

    public static int getWidth() {
        return fragWidth;
    }
}
