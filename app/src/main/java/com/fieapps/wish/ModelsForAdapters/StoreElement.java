package com.fieapps.wish.ModelsForAdapters;

import androidx.annotation.NonNull;

import com.fieapps.wish.Models.Category;
import com.fieapps.wish.Models.Store.Store;

import java.util.List;

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
