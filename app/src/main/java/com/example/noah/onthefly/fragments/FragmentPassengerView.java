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
import com.example.noah.onthefly.widgets.PlaneView;

import java.util.Arrays;
import java.util.List;

public class FragmentPassengerView extends Fragment {
    Button tab;
    private PlaneView pilotView;
    private PlaneView passengerView;

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
        return v;
    }

    public double calculateMoment() {
        return pilotView.calculateMoment() + passengerView.calculateMoment();
    }
}
