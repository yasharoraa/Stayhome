package com.stayhome.user.Models.Order;

import com.stayhome.user.Models.Address;
import com.stayhome.user.Models.Store.Store;
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
