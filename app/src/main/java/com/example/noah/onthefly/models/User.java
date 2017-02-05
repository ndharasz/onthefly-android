package com.example.noah.onthefly.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by brian on 2/4/17.
 */

@IgnoreExtraProperties
public class User {
    private String firstName;
    private String lastName;
    private String email;

    // Default constructor required for Firebase Model
    public User() {}

    public User(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
