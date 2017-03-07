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

        Double[] pilotArmsArray = {plane.getPilotSeatsArm()};

        List<Double> pilotArms = Arrays.asList(pilotArmsArray);
        List<Double> passengerArms = plane.getRowArms();

        View v = inflater.inflate(R.layout.fragment_passenger_view, container, false);
        LinearLayout pilotLayout = (LinearLayout) v.findViewById(R.id.pilot_view);
        LinearLayout passengerLayout = (LinearLayout) v.findViewById(R.id.passenger_view);

        int numPilotSeats = 2; // Are we to assume this will always be 2?
        int numColumns = 2; // likewise..
        pilotView = new PlaneView(getContext(), numColumns, numPilotSeats, pilotArms);
        passengerView = new PlaneView(getContext(), numColumns, plane.getNumSeats() - numPilotSeats, passengerArms);

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

        for (int i = 0; i < numPilotSeats; i++) {
            Passenger passenger = flightManager.getPassenger(i);
            pilotView.setPassenger(i, passenger);
        }

        for (int i = 0; i < plane.getNumSeats() - numPilotSeats; i++) {
            Passenger passenger = flightManager.getPassenger(i + numPilotSeats);
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
