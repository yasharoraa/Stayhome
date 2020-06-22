package com.stayhome.user.Models.Order;

import com.stayhome.user.Models.Address;
import com.stayhome.user.Models.User.User;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

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
