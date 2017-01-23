package com.example.noah.onthefly.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TimePicker;

import com.example.noah.onthefly.interfaces.CallsTimePicker;

import java.sql.Time;

/**
 * Created by Brian Woodbury on 1/23/2017.
 */

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of TimePicker and return it
        int hour = 12;
        int minute = 60;
        return new TimePickerDialog(getActivity(), this, hour, minute, false);
    }

    @Override
    public void onTimeSet(TimePicker view, int h, int m) {
        String date = Integer.toString(h) + ":" + Integer.toString(m);
        ((CallsTimePicker) getActivity()).hideTimePicker(date);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        ((CallsTimePicker) getActivity()).hideTimePicker("");
    }
}

