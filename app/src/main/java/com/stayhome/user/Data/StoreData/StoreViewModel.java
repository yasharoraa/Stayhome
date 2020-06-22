package com.stayhome.user.Data.StoreData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhome.user.Models.Store.Store;

import java.util.List;

public class StoreViewModel extends ViewModel {

    private MutableLiveData<List<Store>> storeLiveData;
    private StoresRepo storesRepo;
    private String mCity;
    private double mLatitude;
    private double mLongitude;
    public void init(String token,int offset,int limit,double latitude, double longitude,int pincode,String city){
        if (storeLiveData != null && storeLiveData.getValue()!=null && city.equals(mCity) && longitude == mLongitude && latitude == mLatitude) return;
        this.mCity = city;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        storesRepo = StoresRepo.getInstance();
        storeLiveData = storesRepo.getStores(token,offset,limit,latitude,longitude,pincode,city,null,null);
    }
    public LiveData<List<Store>> getStoresRepo() {
        return storeLiveData;
    }
}
