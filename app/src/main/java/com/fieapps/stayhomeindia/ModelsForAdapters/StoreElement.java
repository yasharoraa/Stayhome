package com.fieapps.stayhomeindia.ModelsForAdapters;

import androidx.annotation.NonNull;

import com.fieapps.stayhomeindia.Models.Store.Store;

public class StoreElement extends Store implements Comparable<StoreElement> {



    public StoreElement(Store store) {
        super(store.getId(),store.getName(), store.getOwner(), store.getAddress(),
                store.getImage(),store.isOnline(),store.getPincode(),store.getDistance(),store.getCategories());
    }

    @Override
    public int compareTo(@NonNull StoreElement storeElement) {
        return 0;
    }
}
