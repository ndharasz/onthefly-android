package com.example.noah.onthefly.models;

import android.util.Log;

import java.util.List;

/**
 * Created by brian on 3/19/17.
 */

public class WeightAndBalance {
    private static final String TAG = "WeightAndBalance";

    private Coordinate startCoordinate;
    private Coordinate endCoordinate;
    private Coordinate dryCoordinate;

    public WeightAndBalance(Coordinate startCoordinate, Coordinate endCoordinate, Coordinate dryCoordinate) {
        this.startCoordinate = startCoordinate;
        this.endCoordinate = endCoordinate;
        this.dryCoordinate = dryCoordinate;
    }

    public Coordinate getStartCoordinate() {
        return startCoordinate;
    }

    public Coordinate getEndCoordinate() {
        return endCoordinate;
    }

    public Coordinate getDryCoordinate() {
        return dryCoordinate;
    }

    /*
     * Returns whether all three coordinates are inside the envelope
     */
    public boolean allCoordinatesInEnvelope(List<Coordinate> envelope) {
        return coordinateInEnvelope(startCoordinate, envelope)
                && coordinateInEnvelope(endCoordinate, envelope);
    }

    private static boolean coordinateInEnvelope(Coordinate coordinate, List<Coordinate> envelope) {
        // in between the left and right boundaries and above the minimum weight
        if (coordinate.getX() > envelope.get(0).getX() && coordinate.getX() < envelope.get(envelope.size() - 1).getX()
                && coordinate.getY() > envelope.get(0).getY()) {
            for (int i = 0; i < envelope.size() - 2; i++) {
                // check for which two coordinates it is between
                if (coordinate.getX() > envelope.get(i).getX() && coordinate.getX() < envelope.get(i + 1).getX()) {
                    long x = coordinate.getX();
                    Coordinate c1 = envelope.get(i);
                    Coordinate c2 = envelope.get(i + 1);
                    // Use point-slope to figure out if y is in the safe range
                    long y = (c2.getY() - c1.getY()) / (c2.getX() - c1.getX())
                            * (x - c1.getX()) + c1.getY();
                    if (coordinate.getY() < y) {
                        Log.d(TAG, "Coordinate is within the bounds");
                        return true;
                    } else {
                        Log.d(TAG, "Plane is too heavy for its given CG");
                        return false;
                    }
                }
            }
        }
        Log.d(TAG, "Coordinate is not within the bounds");
        return false;
    }
}
