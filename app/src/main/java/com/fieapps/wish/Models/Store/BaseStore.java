package com.fieapps.wish.Models.Store;

import com.google.gson.annotations.SerializedName;

public class BaseStore {

    @SerializedName("_id")
    private String id;

    @SerializedName("phone")
    private String phone;

    @SerializedName("name")
    private String name;

    @SerializedName("oname")
    private String owner;

    @SerializedName("gstin")
    private String gstin;

    @SerializedName("address")
    private String address;

    @SerializedName("pincode")
    private Integer pincode;

    @SerializedName("online")
    private boolean online;

    @SerializedName("token")
    private String token;

    @SerializedName("city")
    private String city;

    @SerializedName("place_id")
    private String locationId;

    @SerializedName("coordinates")
    private double[] coordinates;

    @SerializedName("profile_image")
    private String image;

    @SerializedName("distance")
    private double distance;

    public BaseStore(String name, String owner, String gstin, String address, Integer pincode, String city, String locationId, double[] coordinates) {
        this.name = name;
        this.owner = owner;
        this.gstin = gstin;
        this.address = address;
        this.pincode = pincode;
        this.city = city;
        this.locationId = locationId;
        this.coordinates = coordinates;
    }

    public BaseStore(String id,String name, String owner,String address,String image,boolean online,Integer pincode,double distance) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.address = address;
        this.image = image;
        this.online = online;
        this.pincode = pincode;
        this.distance = distance;
    }



    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public String getGstin() {
        return gstin;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPincode() {
        return pincode;
    }

    public boolean isOnline() {
        return online;
    }

    public String getToken() {
        return token;
    }

    public String getCity() {
        return city;
    }

    public String getLocationId() {
        return locationId;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public String getImage() {
        return image;
    }

    public double getDistance() {
        return distance;
    }
}
