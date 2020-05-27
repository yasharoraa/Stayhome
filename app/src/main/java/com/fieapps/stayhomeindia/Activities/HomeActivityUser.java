package com.fieapps.stayhomeindia.Activities;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.fieapps.stayhomeindia.Data.CategoryData.CategoryViewModel;
import com.fieapps.stayhomeindia.Data.OrderData.OrderViewModel;
import com.fieapps.stayhomeindia.Data.ProfileData.ProfileViewModel;
import com.fieapps.stayhomeindia.Data.SlideData.SlideViewModel;
import com.fieapps.stayhomeindia.Data.StoreData.StoreViewModel;
import com.fieapps.stayhomeindia.Fragments.OrdersFragment;
import com.fieapps.stayhomeindia.Fragments.ProfileFragment;
import com.fieapps.stayhomeindia.Fragments.StoresFragment;
import com.fieapps.stayhomeindia.Interfaces.ActivityFragmentCommunication;
import com.fieapps.stayhomeindia.Models.Geocoding.UserLocation;
import com.fieapps.stayhomeindia.R;
import com.fieapps.stayhomeindia.Utils.Constants;
import com.fieapps.stayhomeindia.Utils.LocationDetectingActivity;
import com.fieapps.stayhomeindia.Utils.MyApplication;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivityUser extends LocationDetectingActivity implements BottomNavigationView.OnNavigationItemSelectedListener ,BottomNavigationView.OnNavigationItemReselectedListener{

    @BindView(R.id.location_text)
    TextView locationTextView;

    @BindView(R.id.custom_toolbar)
    View customToolBar;

    @BindView(R.id.content_main)
    View contentMain;

    @BindView(R.id.error_layout)
    View errorView;

    @BindView(R.id.error_button)
    Button errorButton;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;

    private int selectedFragment;

    private final String TAG = this.getClass().getSimpleName();

    public final String SAVED_SELECTED = "saved_selected";

    public CategoryViewModel categoryViewModel;
    public StoreViewModel storeViewModel;
    public SlideViewModel slideViewModel;
    public OrderViewModel orderViewModel;
    public ProfileViewModel profileViewModel;
    public ActivityFragmentCommunication activityListener;
    public final String CURRENT_FRAGMENT = "current_fragment";

    public final int STORES = 0;
    public final int ORDERS = 1;
    public final int PROFILE = 2;

    private boolean doubleBackToExitPressedOnce;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(this);
        UserLocation userLocation = ((MyApplication) getApplication()).getLocation();
        if (userLocation == null){
            super.getLastLocation(false);
        }else{
            setAddressText(userLocation);
        }


        storeViewModel = ViewModelProviders.of(this).get(StoreViewModel.class);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        slideViewModel = ViewModelProviders.of(this).get(SlideViewModel.class);
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        errorButton.setOnClickListener(view -> {
            if (activityListener != null)
                activityListener.reloadFragment();
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
        if (savedInstanceState == null) {
            int i = getIntent().getIntExtra(Constants.HOME_ACTIVITY_FRAGMENT,0);
            change(i);
        } else {
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT);
            if (fragment!=null){
                changeFragment(fragment);
                return;
            }
            selectedFragment = savedInstanceState.getInt(SAVED_SELECTED);
            change(selectedFragment);
        }

        locationTextView.setOnClickListener(view -> Toast.makeText(HomeActivityUser.this,"This feature is under development",Toast.LENGTH_SHORT).show());
    }


    @Override
    public void onLocationDetected(Location mLocation) {
        Log.i("LOCATION", mLocation.getLatitude() + "," + mLocation.getLongitude());
    }

    @Override
    public void OnPlaceDetected(@NonNull UserLocation userLocation, String error) {
        if (error == null) {
            ((MyApplication) this.getApplication()).setLocation(userLocation);
            setAddressText(userLocation);

            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
            if (fragment instanceof StoresFragment) {
                ((StoresFragment) fragment).OnLocationDetected();
            }
        }
    }

    private void setAddressText(UserLocation userLocation){
        String[] address = userLocation.getFormattedAddress().split(",");

        locationTextView.setText((address.length >= 2) ? address[0] + ", " + address[1] : address[0]);
    }

    void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .replace(R.id.content_main, fragment)
                .commit();
    }

    public void change(int value) {
        this.selectedFragment = value;
        switch (value) {
            case STORES:
                changeFragment(new StoresFragment());
                break;
            case ORDERS:
                changeFragment(new OrdersFragment());
                break;
            case PROFILE:
                changeFragment(new ProfileFragment());
                break;
        }
    }

    public void setActivityListener(ActivityFragmentCommunication activityListener) {
        this.activityListener = activityListener;
    }

    public void showError(boolean show) {
        errorView.setVisibility(show ? View.VISIBLE : View.GONE);

    }

    public void showProgress(boolean show) {
        errorView.setVisibility(View.GONE);
        activityListener.showProgress(show);
//        if (show){
//            shine.startAnimation(Constants.initLoadingAnimation(img,shine));
//        }else {
//            shine.clearAnimation();
//        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_main);
        if (fragment != null)
            getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, fragment);

        outState.putInt(SAVED_SELECTED, selectedFragment);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        boolean value = false;
        switch (item.getItemId()) {
            case R.id.stores:
                change(STORES);
                value = true;
                break;
            case R.id.orders:
                change(ORDERS);
                value = true;
                break;
            case R.id.profile:
                change(PROFILE);
                value = true;
                break;
        }
        return value;
    }


    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.exit_confirm, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

}
