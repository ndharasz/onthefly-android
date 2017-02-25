package com.example.noah.onthefly.models;

/**
 * Created by ndharasz on 2/6/2017.
 */

public class Flight {
    private String plane;
    private String departAirport;
    private String arriveAirport;
    private String date;
    private String time;
    private String userid;
    private String frontBaggageWeight;
    private String aftBaggageWeight;
    private String flightDuration;
    private String startFuel;
    private String fuelFlow;
    private String taxiFuelBurn;

    public Flight(){}

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
    }
    
    public String getFrontBaggageWeight() {
        return frontBaggageWeight;
    }

    public void setFrontBaggageWeight(String frontBaggageWeight) {
        this.frontBaggageWeight = frontBaggageWeight;
    }

    public String getAftBaggageWeight() {
        return aftBaggageWeight;
    }

    public void setAftBaggageWeight(String aftBaggageWeight) {
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
}
