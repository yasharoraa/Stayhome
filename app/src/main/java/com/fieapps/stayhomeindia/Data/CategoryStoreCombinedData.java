package com.fieapps.stayhomeindia.Data;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.fieapps.stayhomeindia.Models.Category;
import com.fieapps.stayhomeindia.Models.Store.Store;

import java.util.Collections;
import java.util.List;

public class CategoryStoreCombinedData extends MediatorLiveData<Pair<List<Category>,List<Store>>> {

    private List<Category> categories = Collections.emptyList();
    private List<Store> stores = Collections.emptyList();

    public CategoryStoreCombinedData(LiveData<List<Category>> catList, LiveData<List<Store>> storeList) {
        setValue(Pair.create(categories,stores));

        addSource(catList,(categories) -> {
            if (categories != null){
                this.categories = categories;
            }
            setValue(Pair.create(categories,stores));
        });

        addSource(storeList,(stores) -> {
            if (stores != null){
                this.stores = stores;
            }
            setValue(Pair.create(categories,stores));
        });
    }
}
