package com.fieapps.wish.Data.StoreData;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fieapps.wish.Models.Store.Store;

import java.util.List;
import java.util.Objects;

public class StoreActivityViewModel extends ViewModel {
    private MutableLiveData<List<Store>> storeLiveData;
    private StoresRepo storesRepo;
    private int initialOffset;
    private String searchString;
    private final String TAG = this.getClass().getSimpleName();

    public void init(String token, int offset, int limit, double latitude, double longitude, int pincode, String city, String catId, String search) {

        Log.i(TAG, initialOffset + " : " + offset);
        Log.i(TAG, searchString + " : " + search);
        if (storeLiveData != null && storeLiveData.getValue() != null && initialOffset == offset && Objects.equals(searchString, search))
            return;

        searchString = search;
        initialOffset = offset;

        storesRepo = StoresRepo.getInstance();
        storeLiveData = storesRepo.getStores(token, offset, limit, latitude, longitude, pincode, city, catId, search);
    }
    public LiveData<List<Store>> getStoresRepo() {
        return storeLiveData;
    }

    public void postValue(List<Store> lists){
        storeLiveData = new MutableLiveData<>();
        storeLiveData.postValue(lists);

    }

}
