package com.fieapps.wish.Models.Order;

import com.fieapps.wish.Models.Address;
import com.fieapps.wish.Models.Store.Store;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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
