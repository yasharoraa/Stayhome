package com.fieapps.wish.Activities;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.fieapps.wish.Data.CategoryData.CategoryViewModel;
import com.fieapps.wish.Data.OrderData.OrderViewModel;
import com.fieapps.wish.Data.SlideData.SlideViewModel;
import com.fieapps.wish.Data.StoreData.StoreViewModel;
import com.fieapps.wish.Fragments.OrdersFragment;
import com.fieapps.wish.Fragments.StoresFragment;
import com.fieapps.wish.Interfaces.ActivityFragmentCommunication;
import com.fieapps.wish.Models.Geocoding.UserLocation;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.Constants;
import com.fieapps.wish.Utils.LocationDetectingActivity;
import com.fieapps.wish.Utils.MyApplication;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivityUser extends LocationDetectingActivity implements BottomNavigationView.OnNavigationItemSelectedListener ,BottomNavigationView.OnNavigationItemReselectedListener{

    @BindView(R.id.location_text)
    TextView locationTextView;

    @BindView(R.id.custom_toolbar)
    View customToolBar;

    @BindView(R.id.img)
    ImageView img;

    @BindView(R.id.shine)
    ImageView shine;

    @BindView(R.id.progress_view)
    View progressView;

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
    public ActivityFragmentCommunication activityListener;
    public final String CURRENT_FRAGMENT = "current_fragment";

    public final int STORES = 0;
    public final int ORDERS = 1;
    public final int PROFILE = 2;


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
        errorButton.setOnClickListener(view -> {
            if (activityListener != null)
                activityListener.reloadFragment();
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
        Log.i("TOKEN", getSharedPreferences(Constants.SHARED_PREFERENCES, MODE_PRIVATE).getString(Constants.TOKEN, null));
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
        }
        return value;
    }


    @Override
    public void onNavigationItemReselected(@NonNull MenuItem item) {
    }
}
