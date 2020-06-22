package com.stayhome.user.Models;

import com.google.gson.annotations.SerializedName;

public class Address {

    @SerializedName("_id")
    private String id;

    @SerializedName("flat_address")
    private String flatAddress;

    @SerializedName("number")
    private String number;

    @SerializedName("htr")
    private String howToReach;

    @SerializedName("location")
    private double[] location;

    @SerializedName("location_address")
    private String locationAddress;

    public Address(String flatAddress, String number, String howToReach, double[] location, String locationAddress) {
        this.flatAddress = flatAddress;
        this.number = number;
        this.howToReach = howToReach;
        this.location = location;
        this.locationAddress = locationAddress;
    }

    public String getFlatAddress() {
        return flatAddress;
    }

    public String getNumber() {
        return number;
    }

    public String getHowToReach() {
        return howToReach;
    }

    public double[] getLocation() {
        return location;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public String getId() {
        return id;
    }
}
