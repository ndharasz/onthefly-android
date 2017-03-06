package com.example.noah.onthefly.models;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by brian on 2/5/17.
 */

public class Plane implements Comparable<Plane>, Serializable {
    static final long serialVersionUID = -5203400849366852220L;
    private static String TAG = "Plane";

    private String name;
    private String tailNumber;

    private double dryWeight;

    private double frontBaggageArm;
    private double rearBaggageArm;
    private double wingBaggageArm;
    private double pilotSeatsArm;
    private List<Double> rowArms;

    private double fuelArm;
    private double auxTankArm;

    private List<String> cgEnvelope;

    // Empty constructor for Firebase just in case
    public Plane() {}

    public int compareTo(Plane other) {
        return this.name.compareTo(other.name);
    }

    // Auto-generated getters and setters for firebase
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public double getDryWeight() {
        return dryWeight;
    }

    public void setDryWeight(double dryWeight) {
        this.dryWeight = dryWeight;
    }

    public double getFrontBaggageArm() {
        return frontBaggageArm;
    }

    public void setFrontBaggageArm(double frontBaggageArm) {
        this.frontBaggageArm = frontBaggageArm;
    }

    public double getRearBaggageArm() {
        return rearBaggageArm;
    }

    public void setRearBaggageArm(double rearBaggageArm) {
        this.rearBaggageArm = rearBaggageArm;
    }

    public double getWingBaggageArm() {
        return wingBaggageArm;
    }

    public void setWingBaggageArm(double wingBaggageArm) {
        this.wingBaggageArm = wingBaggageArm;
    }

    public double getPilotSeatsArm() {
        return pilotSeatsArm;
    }

    public void setPilotSeatsArm(double pilotSeatsArm) {
        this.pilotSeatsArm = pilotSeatsArm;
    }

    public List<Double> getRowArms() {
        return rowArms;
    }

    public void setRowArms(List<Double> rowArms) {
        this.rowArms = rowArms;
    }

    public double getFuelArm() {
        return fuelArm;
    }

    public void setFuelArm(double fuelArm) {
        this.fuelArm = fuelArm;
    }

    public double getAuxTankArm() {
        return auxTankArm;
    }

    public void setAuxTankArm(double auxTankArm) {
        this.auxTankArm = auxTankArm;
    }

    public List<String> getCgEnvelope() {
        return cgEnvelope;
    }

    public void setCgEnvelope(List<String> cgEnvelope) {
        this.cgEnvelope = cgEnvelope;
    }

    public void writeToFile(Context context) {
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.getName().equals(tailNumber)) {
                Log.d(TAG, "Plane already exists locally");
                return;
            }
        }
        ObjectOutputStream objectOut = null;
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(tailNumber, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOutputStream);
            objectOut.writeObject(this);
            fileOutputStream.getFD().sync();
        } catch (IOException e) {
            Log.d(TAG, "Could not write to file");
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    Log.d(TAG, "Could not close output stream");
                }
            }
        }
    }

    public void deleteFile(Context context) {
        try {
            context.deleteFile(tailNumber);
        } catch (Exception e) {
            Log.d(TAG, "Plane did not exist");
        }
    }

    public static Plane readFromFile(Context context, String name) {
        ObjectInputStream objectIn = null;
        Plane plane = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(name);
            objectIn = new ObjectInputStream(fileInputStream);
            plane = (Plane) objectIn.readObject();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.d(TAG, e.getMessage());
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    Log.d(TAG, e.getStackTrace().toString());
                }
            }
            return plane;
        }
    }

    public static List<String> readAllPlaneNames(Context context) {
        List<String> planes = new LinkedList<String>();
        File dir = context.getFilesDir();
        File[] files = dir.listFiles();
        for (File f : files) {
            Plane p = readFromFile(context, f.getName());
            if (p != null)
            {
                planes.add(f.getName());
            }
        }
        return planes;
    }
}
