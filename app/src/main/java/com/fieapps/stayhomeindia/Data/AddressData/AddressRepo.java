package com.fieapps.stayhomeindia.Data.AddressData;

import androidx.lifecycle.MutableLiveData;

import com.fieapps.stayhomeindia.Models.Address;
import com.fieapps.stayhomeindia.WebServices.ApiInterface;
import com.fieapps.stayhomeindia.WebServices.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressRepo {
    private String TAG = this.getClass().getSimpleName();
    private static AddressRepo addressRepo;
    static AddressRepo getInstance() {
        if (addressRepo == null){
            addressRepo = new AddressRepo();
        }
        return addressRepo;
    }
    private ApiInterface apiInterface;
    private AddressRepo() { apiInterface = ServiceGenerator.createService(ApiInterface.class); }
    MutableLiveData<List<Address>> getAddress(String token){
        MutableLiveData<List<Address>> addressData = new MutableLiveData<>();
        apiInterface.getAddress(token).enqueue(new Callback<List<Address>>() {
            @Override
            public void onResponse(Call<List<Address>> call, Response<List<Address>> response) {
                if (response.isSuccessful() && response.body()!=null){
                    addressData.setValue(response.body());
                }else{
                    addressData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<List<Address>> call, Throwable t) {
                addressData.setValue(null);
            }
        });
        return addressData;
    }

}
