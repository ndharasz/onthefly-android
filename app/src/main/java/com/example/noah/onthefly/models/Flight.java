package com.example.noah.onthefly.models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ndharasz on 2/6/2017.
 */

@SuppressWarnings("serial")
public class Flight implements Serializable {
    private String key;

    private String plane;
    private String departAirport;
    private String arriveAirport;
    private String date;
    private String time;
    private String userid;
    private double frontBaggageWeight;
    private double aftBaggageWeight;
    private String flightDuration;
    private String startFuel;
    private String fuelFlow;
    private String taxiFuelBurn;

    private Map<String, Passenger> passengers;

    public Flight(){
        initializePassengers();
    }

    public Flight(String plane, String departAirport, String arriveAirport, String date,
                  String time, String userid, String duration, String startFuel,
                  String fuelFlow, String taxiFuelBurn) {
        this.plane = plane;
        this.departAirport = departAirport;
        this.arriveAirport = arriveAirport;
        this.date = date;
        this.time = time;
        this.userid = userid;
        this.flightDuration = duration;
        this.startFuel = startFuel;
        this.fuelFlow = fuelFlow;
        this.taxiFuelBurn = taxiFuelBurn;
        initializePassengers();
    }

    private void initializePassengers() {
        passengers = new HashMap<>();
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getDepartAirport() {
        return departAirport;
    }

    public void setDepartAirport(String departAirport) {
        this.departAirport = departAirport;
    }

    public String getArriveAirport() {
        return arriveAirport;
    }

    public void setArriveAirport(String arriveAirport) {
        this.arriveAirport = arriveAirport;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlane() {
        return plane;
    }

    public void setPlane(String plane) {
        this.plane = plane;
        initializePassengers();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public double getFrontBaggageWeight() {
        return frontBaggageWeight;
    }

    public void setFrontBaggageWeight(double frontBaggageWeight) {
        this.frontBaggageWeight = frontBaggageWeight;
    }

    public double getAftBaggageWeight() {
        return aftBaggageWeight;
    }

    public void setAftBaggageWeight(double aftBaggageWeight) {
        this.aftBaggageWeight = aftBaggageWeight;
    }

    public String getFlightDuration() {
        return flightDuration;
    }

    public void setFlightDuration(String flightDuration) {
        this.flightDuration = flightDuration;
    }

    public String getStartFuel() {
        return startFuel;
    }

    public void setStartFuel(String startFuel) {
        this.startFuel = startFuel;
    }

    public String getFuelFlow() {
        return fuelFlow;
    }

    public void setFuelFlow(String fuelFlow) {
        this.fuelFlow = fuelFlow;
    }

    public String getTaxiFuelBurn() {
        return taxiFuelBurn;
    }

    public void setTaxiFuelBurn(String taxiFuelBurn) {
        this.taxiFuelBurn = taxiFuelBurn;
    }

    public Map<String, Passenger> getPassengers() {
        return passengers;
    }
}
