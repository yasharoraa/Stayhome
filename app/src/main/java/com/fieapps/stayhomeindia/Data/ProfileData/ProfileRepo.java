package com.fieapps.stayhomeindia.Data.ProfileData;

import androidx.lifecycle.MutableLiveData;

import com.fieapps.stayhomeindia.Models.User.User;
import com.fieapps.stayhomeindia.WebServices.ApiInterface;
import com.fieapps.stayhomeindia.WebServices.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepo {

    private static ProfileRepo profileRepo;

    static ProfileRepo getInstance() {
        if (profileRepo == null){
            profileRepo = new ProfileRepo();
        }
        return profileRepo;
    }

    private ApiInterface apiInterface;

    private ProfileRepo() {apiInterface = ServiceGenerator.createService(ApiInterface.class);}

    MutableLiveData<User> getUser(String token) {
        MutableLiveData<User> userData = new MutableLiveData<>();
        apiInterface.getMyProfile(token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body()!=null){
                    userData.setValue(response.body());
                }else{
                    userData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                userData.setValue(null);
            }
        });
        return userData;
    }
}
