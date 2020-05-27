package com.fieapps.stayhomeindia.Models.Order;

import com.fieapps.stayhomeindia.Models.Address;
import com.fieapps.stayhomeindia.Models.Store.Store;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderForUser extends Order {

    @SerializedName("seller")
    private Store seller;

    public OrderForUser(ArrayList<OrderItem> items, Address address) {
        super(items, address);
    }

    public OrderForUser(String imageUrl, Address address) {
        super(imageUrl, address);
    }

    public Store getSeller() {
        return seller;
    }
}
