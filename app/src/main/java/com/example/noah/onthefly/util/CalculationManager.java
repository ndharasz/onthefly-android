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

    private static double fuelDensity = 6.0;

    public CalculationManager(Context context, Flight flight) {
        this.flight = flight;
        this.context = context;
    }

    public WeightAndBalance calculate() {
        Log.d(TAG, "----- Weight and Blanace report -----");
        double totalWeight = 0;
        double totalMoment = 0;

        // Calculate plane info
        Plane plane = Plane.readFromFile(context, flight.getPlane());
        Log.d(TAG, "Empty weight: " + plane.getEmptyWeight() + ", moment: " + plane.getEmptyWeight() * plane.getEmptyWeightArm());
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
                    Log.d(TAG, "Pilot weight: " + passenger.getWeight() + ", moment: " + passenger.getWeight() * plane.getPilotSeatsArm());
                } else {
                    totalWeight += passenger.getWeight();
                    totalMoment += passenger.getWeight() * (plane.getRowArms().get((seatNum - 1) / 2 - 1));
                    Log.d(TAG, "Passenger weight: " + passenger.getWeight() + ", moment: " + passenger.getWeight() * (plane.getRowArms().get((seatNum - 1) / 2 - 1)));
                }
            } catch (Throwable t) {
                Log.d(TAG, "Calculation was unsuccessful!");
                return null;
            }
        }

        // Cargo info
        totalWeight += flight.getFrontBaggageWeight();
        totalMoment += flight.getFrontBaggageWeight() * plane.getFrontBaggageArm();
        Log.d(TAG, "Front Cargo weight: " + flight.getFrontBaggageWeight() + ", moment: " + flight.getFrontBaggageWeight() * plane.getFrontBaggageArm());

        totalWeight += flight.getAftBaggageWeight();
        totalMoment += flight.getAftBaggageWeight() * plane.getAftBaggageArm();
        Log.d(TAG, "Aft Cargo weight: " + flight.getAftBaggageWeight() + ", moment: " + flight.getAftBaggageWeight() * plane.getAftBaggageArm());

        Coordinate dryCoordinate = new Coordinate((long) (totalMoment / totalWeight), (long) totalWeight);

        double fuelWeight = flight.getStartFuel() * fuelDensity;
        double fuelUsedWeight = (flight.getFuelFlow() * (flight.getFlightDuration() / 60.0) - flight.getTaxiFuelBurn()) * fuelDensity;

        totalWeight += fuelWeight;
        totalMoment += fuelWeight * plane.getFuelArm();

        Log.d(TAG, "Fuel weight: " + fuelWeight + ", moment: " + (fuelWeight * plane.getFuelArm()));

        Coordinate startCoordinate = new Coordinate((long) (totalMoment / totalWeight), (long) totalWeight);

        Log.d(TAG, "Start CG: " + startCoordinate.getX() + ", weight: " + startCoordinate.getY());
        Log.d(TAG, "Dry CG: " + dryCoordinate.getX() + ", weight: " + dryCoordinate.getY());

        totalWeight -= fuelUsedWeight;
        totalMoment -= fuelUsedWeight * plane.getFuelArm();

        Coordinate endCoordinate = new Coordinate((long) (totalMoment / totalWeight), (long) totalWeight);

        return new WeightAndBalance(startCoordinate, endCoordinate, dryCoordinate);
    }
}
