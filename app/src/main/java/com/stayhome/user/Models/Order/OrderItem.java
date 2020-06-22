package com.stayhome.user.Models.Order;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class OrderItem implements Parcelable {

    @SerializedName("name")
    private String item;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("unit")
    private String unit;

    public OrderItem(String item, int quantity, String unit) {
        this.item = item;
        this.quantity = quantity;
        this.unit = unit;
    }

    protected OrderItem(Parcel in) {
        item = in.readString();
        quantity = in.readInt();
        unit = in.readString();
    }

    public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
        @Override
        public OrderItem createFromParcel(Parcel in) {
            return new OrderItem(in);
        }

        @Override
        public OrderItem[] newArray(int size) {
            return new OrderItem[size];
        }
    };

    public String getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(item);
        parcel.writeInt(quantity);
        parcel.writeString(unit);
    }
}
