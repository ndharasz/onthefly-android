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
import com.example.noah.onthefly.models.Passenger;
import com.example.noah.onthefly.util.FlightManager;
import com.example.noah.onthefly.widgets.PlaneView;

import java.util.Arrays;
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
        // TODO: make this not hardcoded, because that's stupid.
        Double[] pilotArmsArray = {12.0};
        Double[] passengerArmsArray = {50.0, 60.0, 70.0, 80.0};

        List<Double> pilotArms = Arrays.asList(pilotArmsArray);
        List<Double> passengerArms = Arrays.asList(passengerArmsArray);

        View v = inflater.inflate(R.layout.fragment_passenger_view, container, false);
        LinearLayout pilotLayout = (LinearLayout) v.findViewById(R.id.pilot_view);
        LinearLayout passengerLayout = (LinearLayout) v.findViewById(R.id.passenger_view);

        pilotView = new PlaneView(getContext(), 2, 2, pilotArms);
        passengerView = new PlaneView(getContext(), 2, 8, passengerArms);

        pilotLayout.addView(pilotView);
        passengerLayout.addView(passengerView);

        // Listen for passengers added
        pilotView.setOnPassengerAddedListener(new PlaneView.PassengerAddedListener() {
            @Override
            public void onPassengerAdded(int seat, Passenger pilot) {
                flightManager.setPassenger(seat + 1, pilot);
            }
        });
        passengerView.setOnPassengerAddedListener(new PlaneView.PassengerAddedListener() {
            @Override
            public void onPassengerAdded(int seat, Passenger passenger) {
                flightManager.setPassenger(seat + 3, passenger);
            }
        });

        // Listen for passengers swapped
        pilotView.setOnPassengerMovedListener(new PlaneView.PassengerMovedListener() {
            @Override
            public void onPassengerMoved(int newSeat, Passenger pilot) {
                flightManager.setPassenger(newSeat + 1, pilot);
            }
        });
        passengerView.setOnPassengerMovedListener(new PlaneView.PassengerMovedListener() {
            @Override
            public void onPassengerMoved(int newSeat, Passenger passenger) {
                flightManager.setPassenger(newSeat + 3, passenger);
            }
        });
        return v;
    }

    public void setFlightManager(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    public double calculateMoment() {
        return pilotView.calculateMoment() + passengerView.calculateMoment();
    }
}
