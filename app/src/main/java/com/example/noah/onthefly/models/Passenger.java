package com.example.noah.onthefly.models;

import java.util.Objects;

/**
 * Created by brian on 2/21/17.
 */

public class Passenger {
    public static Passenger EMPTY = new Passenger("Add Passenger", 0);
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null)
            return false;
        if (!(other instanceof Passenger))
            return false;
        Passenger that = (Passenger) other;
        return name.equals(that.name) && weight == that.weight;
    }

    @Override
    public String toString() {
        return name + "$" + String.valueOf(weight);
    }

    private String name;
    private int weight;
    public Passenger(String name, int weight) {
        this.name =  name;
        this.weight = weight;
    }

    public static void swap(Passenger p1, Passenger p2) {
        String p2name = p2.name;
        int p2weight = p2.weight;
        p2.name = p1.name;
        p2.weight = p1.weight;
        p1.name = p2name;
        p1.weight = p2weight;
    }

    public static Passenger reconstructPassenger(String toString) {
        try {
            String[] parts = toString.split("$");
            return new Passenger(parts[0], Integer.valueOf(parts[1]));
        } catch (Exception e) {
            return Passenger.EMPTY;
        }
    }
}
