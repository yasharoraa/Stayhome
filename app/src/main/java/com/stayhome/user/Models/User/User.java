package com.stayhome.user.Models.User;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("_id")
    private String id;

    @SerializedName("phone")
    private String phone;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("pincode")
    private Integer pincode;

    @SerializedName("token")
    private String token;


    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public Integer getPincode() {
        return pincode;
    }

    public String getToken() {
        return token;
    }

    public User(String name, String address, int pincode) {
        this.name = name;
        this.address = address;
        this.pincode = pincode;
    }
}
