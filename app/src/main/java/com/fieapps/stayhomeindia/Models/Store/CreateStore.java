package com.fieapps.stayhomeindia.Models.Store;

import com.google.gson.annotations.SerializedName;

public class CreateStore extends BaseStore {

    @SerializedName("cat")
    private String[] categories;

    public CreateStore(String name, String owner, String gstin, String address, Integer pincode, String city, String locationId, double[] coordinates, String[] categories) {
        super(name, owner, gstin, address, pincode, city, locationId, coordinates);
        this.categories = categories;
    }
}
