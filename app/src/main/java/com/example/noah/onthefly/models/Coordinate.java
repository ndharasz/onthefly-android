package com.example.noah.onthefly.models;

/**
 * Created by ndharasz on 3/16/2017.
 */

public class Coordinate {
    String x;
    String y;

    public Coordinate(){}

    public Coordinate(String x, String y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }
}
