package com.example.noah.onthefly.util;

import android.util.Log;

import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.models.Passenger;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

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

    private AirportChangedListener airportChangedListener;

    public interface AirportChangedListener {
        public void onAirportsChanged(String dep, String arr);
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
        ref = firebaseDatabase.getReference().child(GlobalVars.FLIGHT_DB).child(flight.getKey()).getRef();
        flight.setKey(null);
        this.flight = flight;
        Log.d(TAG, "Key: " + flight.getKey());
    }

    /*

     * The following two methods are for updating the title of the Edit Flight screen
     *  when someone updates information in the text boxes.

     */
    public void setAirportChangedListener(AirportChangedListener airportChangedListener) {
        this.airportChangedListener = airportChangedListener;
    }

    public String getPlane() {
        return flight.getPlane();
    }

    public void setPlane(String plane) {
        flight.setPlane(plane);
        save();
    }

    public String getDepartureAirport() {
        return flight.getDepartAirport();
    }

    public void setDepartureAirport(String departureAirport) {
        String newDepartureAirport = parsePlaneCode(departureAirport);
        flight.setDepartAirport(newDepartureAirport);
        airportChangedListener.onAirportsChanged(newDepartureAirport, flight.getArriveAirport());
        save();
    }

    public String getArrivalAirport() {
        return flight.getArriveAirport();
    }

    public void setArrivalAirport(String arrivalAirport) {
        String newArrivalAirport = parsePlaneCode(arrivalAirport);
        flight.setArriveAirport(newArrivalAirport);
        airportChangedListener.onAirportsChanged(flight.getDepartAirport(), newArrivalAirport);
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

    public double getFrontBaggageWeight() {
        return flight.getFrontBaggageWeight();
    }

    public void setFrontBaggageWeight(double weight) {
        flight.setFrontBaggageWeight(weight);
        save();
    }

    public double getAftBaggageWeight() {
        return flight.getAftBaggageWeight();
    }

    public void setAftBaggageWeight(double weight) {
        flight.setAftBaggageWeight(weight);
        save();
    }

    public Passenger getPassenger(int seat) {
        Map<String, Passenger> passengers = flight.getPassengers();
        String seatName = "seat" + String.valueOf(seat);
        Passenger passenger = passengers.get(seatName);
        if (passenger == null) {
            passenger = new Passenger("Add Passenger", 0);
        }
        return passenger;
    }

    public void setPassenger(int seat, Passenger passenger) {
        flight.getPassengers().put("seat" + String.valueOf(seat), passenger);
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
