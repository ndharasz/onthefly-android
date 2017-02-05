package com.example.noah.onthefly.models;

import java.util.Map;

/**
 * Created by brian on 2/5/17.
 */

public class Plane {
    private String name;

    private double dryWeight;

    private double frontBaggageArm;
    private double rearBaggageArm;
    private double wingBaggageArm;
    private double pilotSeatsArm;
    private double[] rowArms;

    private double fuelArm;
    private double auxTankArm;

    private Map<Double, Double> cgEnvelope;

    // Empty constructor for Firebase just in case
    public Plane() {}

    // Auto-generated getters and setters for firebase
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double[] getRowArms() {
        return rowArms;
    }

    public void setRowArms(double[] rowArms) {
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

    public Map<Double, Double> getCgEnvelope() {
        return cgEnvelope;
    }

    public void setCgEnvelope(Map<Double, Double> cgEnvelope) {
        this.cgEnvelope = cgEnvelope;
    }


}
