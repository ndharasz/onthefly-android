package com.example.noah.onthefly.fragments;

import android.content.Context;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.interfaces.CallsDatePicker;
import com.example.noah.onthefly.interfaces.CallsTimePicker;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.util.Airports;
import com.example.noah.onthefly.util.ArrayAdapterWithHint;
import com.example.noah.onthefly.util.CustomAdapter;
import com.example.noah.onthefly.util.FlightManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by brian on 3/3/17.
 */

public class FragmentDetailsView extends Fragment implements CallsDatePicker, CallsTimePicker {
    private static final String TAG = "FragmentDetailsView";
    Button tab;
    View view;

    Spinner planeSpinner;
    AutoCompleteTextView departureAirport;
    AutoCompleteTextView arrivalAirport;
    EditText flightDate;
    EditText departureTime;
    EditText duration;
    EditText fuelAmount;
    EditText flowRate;
    EditText taxiFuel;

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
        this.departureAirport = (AutoCompleteTextView) parent.findViewById(R.id.depPick);
        this.arrivalAirport = (AutoCompleteTextView) parent.findViewById(R.id.arrPick);
        this.flightDate = (EditText) parent.findViewById(R.id.flight_date);
        this.departureTime = (EditText) parent.findViewById(R.id.flight_time);
        this.duration = (EditText) parent.findViewById(R.id.flight_duration);
        this.fuelAmount = (EditText) parent.findViewById(R.id.start_fuel);
        this.flowRate = (EditText) parent.findViewById(R.id.fuel_flow);
        this.taxiFuel = (EditText) parent.findViewById(R.id.taxi_fuel);

        String[] airports = Airports.getAirports();
        CustomAdapter deptAdapter = new
                CustomAdapter(getContext(),android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(airports)));

        departureAirport.setAdapter(deptAdapter);
        departureAirport.setThreshold(2);

        CustomAdapter arrAdapter = new
                CustomAdapter(getContext(),android.R.layout.simple_list_item_1, new ArrayList<String>(Arrays.asList(airports)));

        arrivalAirport.setAdapter(arrAdapter);
        arrivalAirport.setThreshold(2);
    }

    public void initializeFields() {
        final List<String> planesList = Plane.readAllPlaneNames(view.getContext());
        planesList.add(0, "Choose plane");

        // Initializing an ArrayAdapter
        final ArrayAdapter<String> planeArrayAdapter = new ArrayAdapterWithHint<String>(
                view.getContext(), android.R.layout.simple_spinner_item, planesList);

        // Set selected plane to the plane in the database
        planeSpinner.setAdapter(planeArrayAdapter);
        final int selectedPlane = planesList.indexOf(flightManager.getPlane());
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
        duration.setText(String.valueOf(flightManager.getFlightDuration()));
        fuelAmount.setText(String.valueOf(flightManager.getStartFuel()));
        flowRate.setText(String.valueOf(flightManager.getFuelFlow()));
        taxiFuel.setText(String.valueOf(flightManager.getTaxiFuelBurn()));

        // Set listeners to update the database
        // Plane listener
        planeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int selection = selectedPlane;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selection != position) {
                    Log.d(TAG, "Plane selection updating...");
                    flightManager.setPlane((String) parent.getItemAtPosition(position));
                    Toast.makeText(getContext(), "Plane updated.", Toast.LENGTH_SHORT).show();
                }
                selection = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // Departure Airport listener
        departureAirport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Dept Airport selection updating...");
                flightManager.setDepartureAirport((String) parent.getItemAtPosition(position));
                confirmSelection();
            }
        });
        // Arrival Airport listener
        arrivalAirport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Arr Airport selection updating...");
                flightManager.setArrivalAirport((String) parent.getItemAtPosition(position));
                confirmSelection();
            }
        });
        // Duration listener
        duration.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        flightManager.setFlightDuration(Double.parseDouble(duration.getText().toString()));
                        confirmSelection();
                    } catch (Throwable t) {
                        errorMessage();
                        duration.setText(String.valueOf(flightManager.getFlightDuration()));
                    }
                    return true;
                }
                return false;
            }
        });
        // FuelAmount listener
        fuelAmount.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        flightManager.setStartFuel(Double.parseDouble(fuelAmount.getText().toString()));
                        confirmSelection();
                    } catch (Throwable t) {
                        errorMessage();
                        fuelAmount.setText(String.valueOf(flightManager.getStartFuel()));
                    }
                    return true;
                }
                return false;
            }
        });
        // FlowRate listener
        flowRate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        flightManager.setFuelFlow(Double.parseDouble(flowRate.getText().toString()));
                        confirmSelection();
                    } catch (Throwable t) {
                        errorMessage();
                        flowRate.setText(String.valueOf(flightManager.getFuelFlow()));
                    }

                    return true;
                }
                return false;
            }
        });
        // TaxiFuel listener
        taxiFuel.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        flightManager.setTaxiFuelBurn(Double.parseDouble(taxiFuel.getText().toString()));
                        confirmSelection();
                    } catch (Throwable t) {
                        errorMessage();
                        taxiFuel.setText(String.valueOf(flightManager.getTaxiFuelBurn()));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void confirmSelection() {
        InputMethodManager in = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
        Toast.makeText(getContext(), "Flight updated successfully.", Toast.LENGTH_SHORT).show();
    }

    private void errorMessage() {
        Toast.makeText(getContext(), "Invalid format", Toast.LENGTH_SHORT).show();
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