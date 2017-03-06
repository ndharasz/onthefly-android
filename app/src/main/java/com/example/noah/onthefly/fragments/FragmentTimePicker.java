package com.example.noah.onthefly.fragments;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TimePicker;

import com.example.noah.onthefly.interfaces.CallsTimePicker;

import java.sql.Time;

/**
 * Created by Brian Woodbury on 1/23/2017.
 */

public class FragmentTimePicker extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    CallsTimePicker parent;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Create a new instance of TimePicker and return it
        int hour = 12;
        int minute = 60;
        return new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, this, hour, minute, false);
    }

    @Override
    public void onTimeSet(TimePicker view, int h, int m) {
        int hour = ((h % 12) == 0)? 12:h%12;
        String date = Integer.toString(hour) + ":" + String.format("%02d",m) + " ";
        if (h / 12 == 1)
            date += "pm";
        else
            date += "am";
        parent.hideTimePicker(date);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        parent.hideTimePicker("");
    }

    public void setParent(CallsTimePicker parent) {
        this.parent = parent;
    }
}

