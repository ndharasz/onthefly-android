package com.example.noah.onthefly.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.example.noah.onthefly.activities.ActivityCreateFlight;
import com.example.noah.onthefly.interfaces.CallsDatePicker;

import java.util.Calendar;

/**
 * Created by Brian Woodbury on 1/23/2017.
 */

public class FragmentDatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int y, int m, int d) {
        String date = String.format("%02d", m + 1) + "-"
                + String.format("%02d", d) + "-"
                + String.format("%04d",y);
        Log.d("Tag", "onDateSet reached");
        ((CallsDatePicker) getActivity()).hideDatePicker(date);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        ((CallsDatePicker) getActivity()).hideDatePicker("");
    }
}
