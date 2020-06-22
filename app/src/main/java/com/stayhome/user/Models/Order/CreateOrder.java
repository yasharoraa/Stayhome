package com.stayhome.user.Models.Order;

import com.stayhome.user.Models.Address;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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
