package com.fieapps.stayhomeindia.Data.ProfileData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.fieapps.stayhomeindia.Models.Store.Store;
import com.fieapps.stayhomeindia.Models.User.User;

public class ProfileViewModel extends ViewModel {

    private MutableLiveData<User> profileLiveData;

    private ProfileRepo profileRepo;

    public void init(String token) {
        if (profileLiveData != null && profileLiveData.getValue()!=null)
            return;

        profileRepo = ProfileRepo.getInstance();
        profileLiveData = profileRepo.getUser(token);
    }
    public LiveData<User> getMyProfile() { return profileLiveData;}
}
