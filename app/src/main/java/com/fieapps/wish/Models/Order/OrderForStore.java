package com.fieapps.wish.Models.Order;

import com.fieapps.wish.Models.Address;
import com.fieapps.wish.Models.User.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OrderForStore extends Order {


    @SerializedName("buyer")
    private User buyer;

    public OrderForStore(ArrayList<OrderItem> items, Address address) {
        super(items, address);
    }

    public OrderForStore(String imageUrl, Address address) {
        super(imageUrl, address);
    }

    public User getBuyer() {
        return buyer;
    }
}
