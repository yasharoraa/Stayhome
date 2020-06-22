package com.stayhome.user.Utils;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.stayhome.user.Models.Geocoding.UserLocation;

public class MyApplication extends MultiDexApplication {

    private UserLocation userLocation;

    private boolean isNewOrderPlaced;

    public UserLocation getLocation() {
        return userLocation;
    }

    public void setLocation(UserLocation userLocation) {
        this.userLocation = userLocation;
    }

    public boolean isNewOrderPlaced() {
        return isNewOrderPlaced;
    }

    public void setNewOrderPlaced(boolean newOrderPlaced) {
        isNewOrderPlaced = newOrderPlaced;
    }
}
