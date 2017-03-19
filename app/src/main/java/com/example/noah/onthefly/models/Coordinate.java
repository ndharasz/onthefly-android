package com.example.noah.onthefly.models;

/**
 * Created by ndharasz on 3/16/2017.
 */

public class Coordinate {
    long x;
    long y;

    public Coordinate(){}

    public Coordinate(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }
}
