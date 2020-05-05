package com.fieapps.wish.Models.Order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderElement {

    @SerializedName("orders")
    private List<OrderForUser> orders;

    @SerializedName("order_count")
    private int count;

    public List<OrderForUser> getOrders() {
        return orders;
    }

    public int getCount() {
        return count;
    }

    public OrderElement(List<OrderForUser> orders, int count) {
        this.orders = orders;
        this.count = count;
    }
}
