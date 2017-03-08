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

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.activities.ActivityEditFlight;
import com.example.noah.onthefly.models.Passenger;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.util.FlightManager;
import com.example.noah.onthefly.widgets.PlaneView;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FragmentPassengerView extends Fragment {
    Button tab;
    private PlaneView pilotView;
    private PlaneView passengerView;
    private FlightManager flightManager;

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
        Plane plane = Plane.readFromFile(getContext(), flightManager.getPlane());

        List<Double> passengerArms = new LinkedList<>();
        passengerArms.add(plane.getPilotSeatsArm());
        for (Double arm : plane.getRowArms()) {
            passengerArms.add(arm);
        }

        View v = inflater.inflate(R.layout.fragment_passenger_view, container, false);
        LinearLayout passengerLayout = (LinearLayout) v.findViewById(R.id.passenger_view);

        int numColumns = 2; // Are we to assume this will always be 2?..
        passengerView = new PlaneView(getContext(), numColumns, plane.getNumSeats(), passengerArms);

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

        for (int i = 0; i < plane.getNumSeats(); i++) {
            Passenger passenger = flightManager.getPassenger(i);
            passengerView.setPassenger(i, passenger);
        }

        return v;
    }

    public void setFlightManager(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    public double calculateMoment() {
        return pilotView.calculateMoment() + passengerView.calculateMoment();
    }
}
