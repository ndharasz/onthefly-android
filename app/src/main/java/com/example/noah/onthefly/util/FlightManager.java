package com.example.noah.onthefly.util;

import android.content.Context;
import android.util.Log;

import com.example.noah.onthefly.models.Coordinate;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.models.Passenger;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.models.WeightAndBalance;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
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

    private CalculationManager calculationManager;
    private Context context;

    private AirportChangedListener airportChangedListener = new AirportChangedListener() {
        @Override
        public void onAirportsChanged(String dep, String arr) {
            return;
        }
    };

    private WarnListener warnListener = new WarnListener() {
        @Override
        public void onWarn(boolean b) {
            return;
        }
    };

    public interface AirportChangedListener {
        public void onAirportsChanged(String dep, String arr);
    }

    public interface WarnListener {
        public void onWarn(boolean b);
    }

    private class NoKeyError extends Error {
        public NoKeyError() {
            super();
        }
    }

    public FlightManager(Context context, Flight flight) {
        if (flight.getKey() == null) {
            throw new NoKeyError();
        }
        this.context = context;
        Log.d(TAG, "Key: " + flight.getKey());
        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference().child(GlobalVars.FLIGHT_DB).child(flight.getKey()).getRef();
        flight.setKey(null);
        this.flight = flight;
        calculationManager = new CalculationManager(context, flight);
    }

    public void delete() {
        Log.d(TAG, "Deleting flight from records");
        ref.removeValue();
    }

    /*

     * The following two methods are for updating the title of the Edit Flight screen
     *  when someone updates information in the text boxes.

     */
    public void setAirportChangedListener(AirportChangedListener airportChangedListener) {
        this.airportChangedListener = airportChangedListener;
    }

    public void setWarnListener(WarnListener warnListener) {
        this.warnListener = warnListener;
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
        seat += 1;
        Map<String, Passenger> passengers = flight.getPassengers();
        String seatName = "seat" + String.valueOf(seat);
        Passenger passenger = passengers.get(seatName);
        if (passenger == null) {
            passenger = new Passenger("Add Passenger", 0);
        }
        return passenger;
    }

    public void setPassenger(int seat, Passenger passenger) {
        passenger = Passenger.deepCopy(passenger);
        flight.getPassengers().put("seat" + String.valueOf(seat), passenger);
    }

    public void removePassenger(int seat) {
        flight.getPassengers().remove("seat" + seat);
    }

    public void setFlightDuration(double flightDuration) {
        flight.setFlightDuration(flightDuration);
        save();
    }

    public double getFlightDuration() {
        return flight.getFlightDuration();
    }

    public void setStartFuel(double startFuel) {
        flight.setStartFuel(startFuel);
        save();
    }

    public double getStartFuel() {
        return flight.getStartFuel();
    }

    public void setFuelFlow(double fuelFlow) {
        flight.setFuelFlow(fuelFlow);
        save();
    }

    public double getFuelFlow() {
        return flight.getFuelFlow();
    }

    public void setTaxiFuelBurn(double taxiFuelBurn) {
        flight.setTaxiFuelBurn(taxiFuelBurn);
        save();
    }

    public double getTaxiFuelBurn() {
        return flight.getTaxiFuelBurn();
    }

    public List<Coordinate> getEnvelope() {
        Plane plane = Plane.readFromFile(context, getPlane());
        return plane.getCenterOfGravityEnvelope();
    }

    public void save() {
        ref.setValue(flight);
        check();
    }

    private static String parsePlaneCode(String airport) {
        if (airport.contains("(") && airport.contains(")")
                && airport.indexOf('(') < airport.lastIndexOf(')')) {
            return airport.substring(airport.indexOf('(') + 1, airport.lastIndexOf(')'));
        } else {
            return airport;
        }
    }

    private void check() {
        // after every update of the database, send out a yes/no value to warn if the
        // plane can fly
        boolean warn = !getCalculationCoordinates().allCoordinatesInEnvelope(getEnvelope());
        warnListener.onWarn(warn);
    }

    public WeightAndBalance getCalculationCoordinates() {
        return calculationManager.calculate();
    }
}
