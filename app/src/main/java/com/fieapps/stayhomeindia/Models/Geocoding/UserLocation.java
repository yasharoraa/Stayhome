package com.fieapps.stayhomeindia.Models.Geocoding;

public class UserLocation {

    private double[] coordinates;

    private String formattedAddress;

    private String placeId;

    private String city;

    String pincode;

    public UserLocation(double[] coordinates, String formattedAddress, String placeId, String city, String pincode) {
        this.coordinates = coordinates;
        this.formattedAddress = formattedAddress;
        this.placeId = placeId;
        this.city = city;
        this.pincode = pincode;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getCity() {
        return city;
    }

    public String getPincode() {
        return pincode;
    }
}
