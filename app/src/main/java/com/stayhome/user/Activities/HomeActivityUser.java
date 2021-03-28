package com.stayhome.user.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.stayhome.user.Data.CategoryData.CategoryViewModel;
import com.stayhome.user.Data.ChatData.ChatViewModel;
import com.stayhome.user.Data.OrderData.OrderViewModel;
import com.stayhome.user.Data.ProfileData.ProfileViewModel;
import com.stayhome.user.Data.SlideData.SlideViewModel;
import com.stayhome.user.Data.StoreData.StoreViewModel;
import com.stayhome.user.Fragments.ChatFragment;
import com.stayhome.user.Fragments.OrdersFragment;
import com.stayhome.user.Fragments.ProfileFragment;
import com.stayhome.user.Fragments.StoresFragment;
import com.stayhome.user.Interfaces.ActivityFragmentCommunication;
import com.stayhome.user.Models.Geocoding.UserLocation;
import com.stayhome.user.Models.Message.LastMessage;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.Utils.LocationDetectingActivity;
import com.stayhome.user.Utils.MyApplication;
import com.stayhome.user.Utils.WebSocketUtil;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.stayhome.user.Utils.Constants.CITY;
import static com.stayhome.user.Utils.Constants.PIN_CODE;

public class HomeActivityUser extends LocationDetectingActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemReselectedListener,
        WebSocketUtil.WebSocketListener{

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
    public ChatViewModel chatViewModel;
    public ActivityFragmentCommunication activityListener;
    public final String CURRENT_FRAGMENT = "current_fragment";

    public final int STORES = 0;
    public final int ORDERS = 1;
    public final int PROFILE = 2;
    public final int CHAT = 3;

    private boolean doubleBackToExitPressedOnce;
    private final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private String PLACES_KEY;
    private final String SAVED_KEY = "saved_key";

    public WebSocketUtil webSocketUtil;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);
        ButterKnife.bind(this);
        UserLocation userLocation = ((MyApplication) getApplication()).getLocation();
        if (userLocation == null) {
            super.getLastLocation(false);
        } else {
            setAddressText(userLocation);
        }
        storeViewModel = ViewModelProviders.of(this).get(StoreViewModel.class);
        categoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);
        slideViewModel = ViewModelProviders.of(this).get(SlideViewModel.class);
        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        errorButton.setOnClickListener(view -> {
            if (activityListener != null)
                activityListener.reloadFragment();
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setOnNavigationItemReselectedListener(this);
        if (savedInstanceState == null) {
            int i = getIntent().getIntExtra(Constants.HOME_ACTIVITY_FRAGMENT, 0);
            change(i);
        } else {
            if (savedInstanceState.getString(SAVED_KEY) != null) {
                initPlacesClient(savedInstanceState.getString(SAVED_KEY));
            }
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT);
            if (fragment != null) {
                changeFragment(fragment);
                return;
            }
            selectedFragment = savedInstanceState.getInt(SAVED_SELECTED);
            change(selectedFragment);
        }

        if (webSocketUtil==null){
           webSocketUtil = new WebSocketUtil(this);
//           webSocketUtil.sendMessage();
        }


    }

    private void selectLocation() {

        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.ADDRESS);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .setCountry("IN")
                .setTypeFilter(TypeFilter.ADDRESS)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
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

    @Override
    public void initPlacesClient(String key) {
        HomeActivityUser.this.PLACES_KEY = key;
        Places.initialize(getApplicationContext(), key);
        Places.createClient(this);
        if (locationTextView != null)
            locationTextView.setOnClickListener(view -> selectLocation());
    }

    private void setAddressText(UserLocation userLocation) {
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
            case CHAT:
                changeFragment(new ChatFragment());
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
        outState.putString(SAVED_KEY, PLACES_KEY);
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
            case R.id.chat:
                change(CHAT);
                value = true;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            if (place.getAddressComponents() != null && !place.getAddressComponents().asList().isEmpty() && place.getLatLng() != null) {

                String pincode =
                        (place.getAddressComponents().asList().get(place.getAddressComponents().asList().size() - 1).getTypes().contains(PIN_CODE)) ?
                                place.getAddressComponents().asList().get(place.getAddressComponents().asList().size() - 1).getName() :
                                findComponent(place.getAddressComponents().asList(), PIN_CODE);

                String city = (place.getAddressComponents().asList().get(place.getAddressComponents().asList().size() - 3).getTypes().contains(CITY)) ?
                        place.getAddressComponents().asList().get(place.getAddressComponents().asList().size() - 3).getName() :
                        findComponent(place.getAddressComponents().asList(), CITY);


                OnPlaceDetected(new UserLocation(new double[]{place.getLatLng().latitude, place.getLatLng().longitude},
                        place.getAddress(),
                        place.getId(),
                        city,
                        pincode), null);
            }
        }
    }

    public static String findComponent(List<AddressComponent> list, String type) {

        for (AddressComponent component : list) {
            if (component.getTypes().contains(type)) {
                return component.getName();
            }
        }
        return null;
    }

    @Override
    public void onSocketClose(int code, String error) {

    }

    @Override
    public void onSocketError(Exception ex) {

    }

    @Override
    public void onChats(List<LastMessage> list) {
        Log.i(TAG, String.valueOf(list.size()));
        chatViewModel.postValue(list);
    }

    @Override
    public void onMessage(LastMessage lastMessage) {
        runOnUiThread(() -> {
            if (getSupportFragmentManager().findFragmentById(R.id.content_main) instanceof  ChatFragment) {
                ChatFragment chatFragment = (ChatFragment) getSupportFragmentManager().findFragmentById(R.id.content_main);
                if (chatFragment == null) return;
                chatFragment.chatAdapter.addNewMessage(lastMessage);
            }
        });
    }
}
