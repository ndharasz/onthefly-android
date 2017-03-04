package com.example.noah.onthefly.fragments;

import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.interfaces.CallsDatePicker;
import com.example.noah.onthefly.interfaces.CallsTimePicker;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.util.ArrayAdapterWithHint;
import com.example.noah.onthefly.util.FlightManager;

import java.util.List;

/**
 * Created by brian on 3/3/17.
 */

public class FragmentDetailsView extends Fragment implements CallsDatePicker, CallsTimePicker {
    private static final String TAG = "FragmentDetailsView";
    Button tab;
    View view;

    Spinner planeSpinner;
    EditText departureAirport;
    EditText arrivalAirport;
    EditText flightDate;
    EditText departureTime;

    private FragmentDatePicker datePickerFragment;
    private FragmentTimePicker timePickerFragment;

    private FlightManager flightManager;

    public FragmentDetailsView setTabButton(Button tab) {
        this.tab = tab;
        return new FragmentDetailsView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details_view, container, false);
        setupFields(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializeFields();
    }

    public void setFlightManager(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    private void setupFields(View parent) {
        this.planeSpinner = (Spinner) parent.findViewById(R.id.choose_plane_spinner);
        this.departureAirport = (EditText) parent.findViewById(R.id.depPick);
        this.arrivalAirport = (EditText) parent.findViewById(R.id.arrPick);
        this.flightDate = (EditText) parent.findViewById(R.id.flight_date);
        this.departureTime = (EditText) parent.findViewById(R.id.flight_time);
    }

    public void initializeFields() {
        final List<String> planesList = Plane.readAllPlaneNames(view.getContext());
        planesList.add(0, "Choose plane");

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> planeArrayAdapter = new ArrayAdapterWithHint<String>(
                view.getContext(), android.R.layout.simple_spinner_item, planesList);

        // Set selected plane to the plane in the database
        planeSpinner.setAdapter(planeArrayAdapter);
        int selectedPlane = planesList.indexOf(flightManager.getPlane());
        planeSpinner.setSelection(selectedPlane);

        // Set the date/time onClick listeners
        flightDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(v);
            }
        });
        departureTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker(v);
            }
        });

        // Put the flight values in the corresponding fields
        departureAirport.setText(flightManager.getDepartureAirport());
        arrivalAirport.setText(flightManager.getArrivalAirport());
        flightDate.setText(flightManager.getDate());
        departureTime.setText(flightManager.getTime());


    }

    public void showDatePicker(View v) {
        if (datePickerFragment == null) {
            datePickerFragment = new FragmentDatePicker();
            datePickerFragment.setParent(this);
            datePickerFragment.show(getFragmentManager(), "datePickerFragment");
        }
    }

    public void hideDatePicker(String date) {
        if (datePickerFragment != null) {
            if (date.compareTo("") != 0) {
                flightDate.setText(date);
                flightManager.setDate(date);
            }
            datePickerFragment.dismiss();
            datePickerFragment = null;
            Log.d("Tag", "DatePicker dismissed");
        }
    }

    public void showTimePicker(View v) {
        if (timePickerFragment == null) {
            timePickerFragment = new FragmentTimePicker();
            timePickerFragment.setParent(this);
            timePickerFragment.show(getFragmentManager(), "datePickerFragment");
        }
    }

    public void hideTimePicker(String time) {
        if (timePickerFragment != null) {
            if (time.compareTo("") != 0) {
                departureTime.setText(time);
                flightManager.setTime(time);
            }
            timePickerFragment.dismiss();
            timePickerFragment = null;
        }
    }


//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//        Log.d("Hidden called?", "YES");
//        if(hidden) {
//            tab.setBackgroundResource(R.drawable.edit_flight_tab);
//        } else {
//            tab.setBackgroundResource(R.drawable.edit_flight_tab_selected);
//        }
//    }
}