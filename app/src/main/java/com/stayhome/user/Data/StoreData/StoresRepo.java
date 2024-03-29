package com.stayhome.user.Data.StoreData;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.stayhome.user.Models.Store.Store;
import com.stayhome.user.WebServices.ApiInterface;
import com.stayhome.user.WebServices.ServiceGenerator;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class StoresRepo {

    private String TAG = this.getClass().getSimpleName();

    private static StoresRepo storesRepo;
    static StoresRepo getInstance() {
        if (storesRepo == null) {
            storesRepo = new StoresRepo();
        }
        return storesRepo;
    }

    private ApiInterface apiInterface;
    private StoresRepo(){
        apiInterface = ServiceGenerator.createService(ApiInterface.class);
    }
    MutableLiveData<List<Store>> getStores(String token, int offset,int limit,double latitude,double longitude,int pincode,String city,String cat,String search) {

        Log.i(TAG,"Request added" +token + " offset" +offset);
        MutableLiveData<List<Store>> storeData = new MutableLiveData<>();
        apiInterface.getstores(token,offset,limit,latitude,longitude,pincode == 0 ? null : pincode,city,cat,search).enqueue(new Callback<List<Store>>() {
            @Override
            public void onResponse(Call<List<Store>> call, Response<List<Store>> response) {
                if (response.isSuccessful()){
                    storeData.setValue(response.body());
                }else {
                    storeData.setValue(null);
                }
                Log.i(TAG,response.code() + response.message()+call.request().toString());
            }

            @Override
            public void onFailure(Call<List<Store>> call, Throwable t) {
                storeData.setValue(null);
                Log.i(TAG,t.getMessage()+t.getCause());

            }
        });
        return storeData;
    }

}
