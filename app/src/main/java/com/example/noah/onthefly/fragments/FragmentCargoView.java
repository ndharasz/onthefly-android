package com.example.noah.onthefly.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noah.onthefly.R;
import com.example.noah.onthefly.util.FlightManager;

public class FragmentCargoView extends Fragment {
    Button tab;

    private FlightManager flightManager;
    TextView frontDisplay;
    TextView rearDisplay;

    double frontWeight;
    double rearWeight;

    private interface DialogCloseListener {
        public void onDialogClose(double weight);
    }

    public FragmentCargoView setTabButton(Button tab) {
        this.tab = tab;
        return new FragmentCargoView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        frontWeight = 0;
        rearWeight = 0;
    }
  
    public void setFlightManager(FlightManager flightManager) {
        this.flightManager = flightManager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cargo_view, container, false);

        frontDisplay = (TextView) view.findViewById(R.id.front_cargo_display);
        rearDisplay = (TextView) view.findViewById(R.id.rear_cargo_display);

        Button frontAddButton = (Button) view.findViewById(R.id.front_cargo_add_btn);
        Button frontRemoveButton = (Button) view.findViewById(R.id.front_cargo_remove_btn);
        Button frontClearButton = (Button) view.findViewById(R.id.front_empty_cargo);
        Button rearAddButton = (Button) view.findViewById(R.id.rear_cargo_add_btn);
        Button rearRemoveButton = (Button) view.findViewById(R.id.rear_cargo_remove_btn);
        Button rearClearButton = (Button) view.findViewById(R.id.rear_empty_cargo);

        // this has to be done this way because it's a fragment. -_____-
        frontAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFrontCargo(v);
            }
        });

        frontRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFrontCargo(v);
            }
        });

        frontClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyFrontCargo(v);
            }
        });

        rearAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRearCargo(v);
            }
        });

        rearRemoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeRearCargo(v);
            }
        });

        rearClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyRearCargo(v);
            }
        });

        return view;
    }

    protected void updateTotalFrontCargo() {
        frontDisplay.setText("Weight: " + frontWeight + " lbs");
    }

    protected void updateTotalRearCargo() {
        rearDisplay.setText("Weight: " + rearWeight + " lbs");
    }

    protected void addFrontCargo(View v) {
        changeCargo("Add Front", new DialogCloseListener() {
            @Override
            public void onDialogClose(double weight) {
                frontWeight += weight;
                updateTotalFrontCargo();
            }
        });
    }

    protected void removeFrontCargo(View v) {
        changeCargo("Remove Front", new DialogCloseListener() {
            @Override
            public void onDialogClose(double weight) {
                if(frontWeight - weight < 0) {
                    Toast.makeText(getActivity(), "Cannot remove more cargo than already exists.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    frontWeight -= weight;
                    updateTotalFrontCargo();
                }
            }
        });
    }

    protected void emptyFrontCargo(View v) {
        frontWeight = 0;
        updateTotalFrontCargo();
    }

    protected void addRearCargo(View v) {
        changeCargo("Add Rear", new DialogCloseListener() {
            @Override
            public void onDialogClose(double weight) {
                rearWeight += weight;
                updateTotalRearCargo();
            }
        });
    }

    protected void removeRearCargo(View v) {
        changeCargo("Remove Rear", new DialogCloseListener() {
            @Override
            public void onDialogClose(double weight) {
                if(frontWeight - weight < 0) {
                    Toast.makeText(getActivity(), "Cannot remove more cargo than already exists.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    rearWeight -= weight;
                    updateTotalRearCargo();
                }
            }
        });
    }

    protected void emptyRearCargo(View v) {
        rearWeight = 0;
        updateTotalRearCargo();
    }

    private void changeCargo(String which, final DialogCloseListener dialogCloseListener) {
        final View cargoView = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_cargo, null);
        ((TextView)cargoView.findViewById(R.id.cargo_dialog_title)).setText(which + " Cargo");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        alertDialogBuilder.setView(cargoView);

        final EditText weightField = (EditText) cargoView.findViewById(R.id.weight_field);

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    String weightString = weightField.getText().toString();
                    double weight = Double.parseDouble(weightString);
                    dialogCloseListener.onDialogClose(weight);
                    dialog.dismiss();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Please enter a number only", Toast.LENGTH_SHORT).show();
                }
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alert.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alert.show();
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.colorPrimary));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                ContextCompat.getColor(getContext(), R.color.colorPrimary));
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(getContext(), R.color.colorAccent));
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(getContext(), R.color.colorAccent));
    }
}
