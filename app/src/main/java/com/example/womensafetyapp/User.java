package com.example.womensafetyapp;

public class User {
    public String name, email, number;

    // Default constructor (needed for Firebase)
    public User() {
    }

    // Constructor with parameters
    public User(String name, String email, String number) {
        this.name = name;
        this.email = email;
        this.number = number;
    }
}
