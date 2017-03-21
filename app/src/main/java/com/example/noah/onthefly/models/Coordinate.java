package com.example.noah.onthefly.models;

import java.io.Serializable;

/**
 * Created by ndharasz on 3/16/2017.
 */

public class Coordinate implements Serializable{
    float x;
    float y;

    public Coordinate(){}

    public Coordinate(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}