package com.fieapps.wish.Models.Order;

import com.fieapps.wish.Models.Address;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Order {

    @SerializedName("_id")
    private String id;

    @SerializedName("slug")
    private String slug;

    @SerializedName("items")
    private ArrayList<OrderItem> items;

    @SerializedName("time")
    private int time;

    @SerializedName("status")
    private int status;

    @SerializedName("address")
    private Address address;

    @SerializedName("slip")
    private String imageUrl;

    @SerializedName("createdAt")
    private String placeDate;

    public String getId() {
        return id;
    }

    public ArrayList<OrderItem> getItems() {
        return items;
    }

    public int getTime() {
        return time;
    }

    public int getStatus() {
        return status;
    }

    public Address getAddress() {
        return address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSlug() {
        return slug;
    }

    public String getPlaceDate() {
        return placeDate;
    }

    public Order(ArrayList<OrderItem> items, Address address) {
        this.items = items;
        this.address = address;
    }

    public Order( String imageUrl,Address address) {
        this.imageUrl = imageUrl;
        this.address = address;
    }
}
