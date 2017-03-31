package com.example.noah.onthefly.util;

import android.content.Context;
import android.util.Log;

import com.example.noah.onthefly.models.Coordinate;
import com.example.noah.onthefly.models.Flight;
import com.example.noah.onthefly.models.Passenger;
import com.example.noah.onthefly.models.Plane;
import com.example.noah.onthefly.models.WeightAndBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by brian on 3/19/17.
 */

public class CalculationManager {
    private static final String TAG = "CalculationManager";

    private Flight flight;
    private Context context;

    private double takeoffWeight = -1;
    private double zeroFuelWeight = -1;

    private static double fuelDensity = 6.0;

    public CalculationManager(Context context, Flight flight) {
        this.flight = flight;
        this.context = context;
    }

    public double getTakeoffWeight() {
        return takeoffWeight;
    }

    public double getZeroFuelWeight() {
        return zeroFuelWeight;
    }

    public WeightAndBalance calculate() {
        double totalWeight = 0;
        double totalMoment = 0;

        // Calculate plane info
        Plane plane = Plane.readFromFile(context, flight.getPlane());
        totalWeight += plane.getEmptyWeight();
        totalMoment += plane.getEmptyWeight() * plane.getEmptyWeightArm();

        // Calculate passenger info
        Map<String, Passenger> passengerMap = flight.getPassengers();
        Set<String> passengerKeys = passengerMap.keySet();
        for (String passengerKey : passengerKeys) {
            try {
                Passenger passenger = passengerMap.get(passengerKey);
                int seatNum = Integer.parseInt(passengerKey.replace("seat", ""));
                if (seatNum < 3) {
                    totalWeight += passenger.getWeight();
                    totalMoment += passenger.getWeight() * plane.getPilotSeatsArm();
                } else {
                    totalWeight += passenger.getWeight();
                    totalMoment += passenger.getWeight() * (plane.getRowArms().get((seatNum - 1) / 2 - 1));
                }
            } catch (Throwable t) {
                Log.d(TAG, "Calculation was unsuccessful!");
                return null;
            }
        }

        // Cargo info
        totalWeight += flight.getFrontBaggageWeight();
        totalMoment += flight.getFrontBaggageWeight() * plane.getFrontBaggageArm();

        totalWeight += flight.getAftBaggageWeight();
        totalMoment += flight.getAftBaggageWeight() * plane.getAftBaggageArm();

        zeroFuelWeight = totalWeight;
        Coordinate dryCoordinate = new Coordinate((long) (totalMoment / totalWeight), (long) totalWeight);

        double fuelWeight = flight.getStartFuel() * fuelDensity;
        double fuelUsedWeight = (flight.getFuelFlow() * (flight.getFlightDuration() / 60.0) - flight.getTaxiFuelBurn()) * fuelDensity;

        totalWeight += fuelWeight;
        totalMoment += fuelWeight * plane.getFuelArm();
        takeoffWeight = totalWeight;

        Coordinate startCoordinate = new Coordinate((long) (totalMoment / totalWeight), (long) totalWeight);

        totalWeight -= fuelUsedWeight;
        totalMoment -= fuelUsedWeight * plane.getFuelArm();

        Coordinate endCoordinate = new Coordinate((long) (totalMoment / totalWeight), (long) totalWeight);

        return new WeightAndBalance(startCoordinate, endCoordinate, dryCoordinate);
    }
}
