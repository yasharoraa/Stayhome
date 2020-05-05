package com.fieapps.wish.Models.Order;

import com.fieapps.wish.Models.Address;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CreateOrder extends Order {

    @SerializedName("seller")
    private String seller;

    public CreateOrder(ArrayList<OrderItem> items, Address address, String seller) {
        super(items, address);
        this.seller = seller;
    }

    public CreateOrder(String imageUrl, Address address, String seller) {
        super(imageUrl, address);
        this.seller = seller;
    }
}
