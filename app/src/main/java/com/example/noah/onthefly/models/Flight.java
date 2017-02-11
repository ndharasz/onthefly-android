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

    public Flight(){}

    public Flight(String plane, String departAirport,
                  String arriveAirport, String date,
                  String time, String userid) {
        this.plane = plane;
        this.departAirport = departAirport;
        this.arriveAirport = arriveAirport;
        this.date = date;
        this.time = time;
        this.userid = userid;
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
}
