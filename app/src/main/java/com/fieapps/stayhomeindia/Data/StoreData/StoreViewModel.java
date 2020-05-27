package com.fieapps.stayhomeindia.Data.StoreData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fieapps.stayhomeindia.Models.Store.Store;

import java.util.List;

public class StoreViewModel extends ViewModel {

    private MutableLiveData<List<Store>> storeLiveData;
    private StoresRepo storesRepo;
    public void init(String token,int offset,int limit,double latitude, double longitude,int pincode,String city){
        if (storeLiveData != null && storeLiveData.getValue()!=null) return;
        storesRepo = StoresRepo.getInstance();
        storeLiveData = storesRepo.getStores(token,offset,limit,latitude,longitude,pincode,city,null,null);
    }
    public LiveData<List<Store>> getStoresRepo() {
        return storeLiveData;
    }
}
