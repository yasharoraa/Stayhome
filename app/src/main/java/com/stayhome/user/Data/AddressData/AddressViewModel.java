package com.stayhome.user.Data.AddressData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.stayhome.user.Models.Address;

import java.util.List;

public class AddressViewModel extends ViewModel {
    private MutableLiveData<List<Address>> addressLiveData;
    private AddressRepo addressRepo;
    public void init(String token,boolean getNew) {
        if (!getNew && addressRepo!=null && addressLiveData.getValue()!=null) return;
        addressRepo = AddressRepo.getInstance();
        addressLiveData = addressRepo.getAddress(token);
    }
    public LiveData<List<Address>> getAddressRepo() {return addressLiveData;}
}
