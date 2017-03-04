package com.example.noah.onthefly.util;

import android.util.Log;

import com.example.noah.onthefly.models.Flight;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by brian on 3/3/17.
 *
 * This class manages a single flight for editing.  It handles all
 * database updating and keeps everything in sync within several
 * fragments.
 */

public class FlightManager {
    private static final String TAG = "FlightManager";
    private Flight flight;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;

    private ValueListener departureAirportChangedListener;
    private ValueListener arrivalAirportChangedListener;

    public interface ValueListener {
        public void onValueChanged(String value);
    }

    private class NoKeyError extends Error {
        public NoKeyError() {
            super();
        }
    }

    public FlightManager(Flight flight, String uid) {
        if (flight.getKey() == null) {
            throw new NoKeyError();
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference().child("flights").child(flight.getKey());
        flight.setKey(null);
        this.flight = flight;
        Log.d(TAG, "Key: " + flight.getKey());
    }

    /*

     * The following two methods are for updating the title of the Edit Flight screen
     *  when someone updates information in the text boxes.

     */
    public void setDepartureAirportChangedListener(ValueListener valueListener) {
        departureAirportChangedListener = valueListener;
    }

    public void setArrivalAirportChangedListener(ValueListener valueListener) {
        arrivalAirportChangedListener = valueListener;
    }

    public String getPlane() {
        return flight.getPlane();
    }

    public String getDepartureAirport() {
        return flight.getDepartAirport();
    }

    public void setDepartureAirport(String departureAirport) {
        String newDepartureAirport = parsePlaneCode(departureAirport);
        flight.setDepartAirport(newDepartureAirport);
        departureAirportChangedListener.onValueChanged(newDepartureAirport);
        save();
    }

    public String getArrivalAirport() {
        return flight.getArriveAirport();
    }

    public void setArrivalAirport(String arrivalAirport) {
        String newArrivalAirport = parsePlaneCode(arrivalAirport);
        flight.setArriveAirport(newArrivalAirport);
        departureAirportChangedListener.onValueChanged(newArrivalAirport);
        save();
    }

    public String getDate() {
        return flight.getDate();
    }

    public void setDate(String date) {
        flight.setDate(date);
        save();
    }

    public String getTime() {
        return flight.getTime();
    }

    public void setTime(String time) {
        flight.setTime(time);
        save();
    }

    private void save() {
        ref.setValue(flight);
    }

    private static String parsePlaneCode(String airport) {
        if (airport.contains("(") && airport.contains(")")
                && airport.indexOf('(') < airport.lastIndexOf(')')) {
            return airport.substring(airport.indexOf('(') + 1, airport.lastIndexOf(')'));
        } else {
            return airport;
        }
    }
}
