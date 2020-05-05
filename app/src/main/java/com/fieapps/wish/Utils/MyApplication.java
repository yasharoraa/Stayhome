package com.fieapps.wish.Utils;

import android.app.Application;
import android.location.Location;

import com.fieapps.wish.Models.Geocoding.Reverse;
import com.fieapps.wish.Models.Geocoding.UserLocation;

public class MyApplication extends Application {

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
