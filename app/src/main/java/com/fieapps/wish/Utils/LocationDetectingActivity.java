package com.fieapps.wish.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.fieapps.wish.Models.Geocoding.Reverse;
import com.fieapps.wish.Models.Geocoding.UserLocation;
import com.fieapps.wish.R;
import com.fieapps.wish.WebServices.GeoCodingApi;
import com.fieapps.wish.WebServices.ServiceGenerator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fieapps.wish.Utils.Constants.CITY;
import static com.fieapps.wish.Utils.Constants.PIN_CODE;
import static com.fieapps.wish.Utils.Constants.findComponent;

public abstract class LocationDetectingActivity extends AppCompatActivity {

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    public abstract void onLocationDetected(Location mLocation);

    private boolean IS_TRACKING_LOCATION;

    private boolean GOT_LOCATION;

    Dialog locationDialog;

    private boolean IS_STORE;

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (locationDialog == null)
            locationDialog = getLocationDialog();
    }

    @SuppressLint("MissingPermission")
    public void getLastLocation(boolean isStore) {
        Log.i(TAG,"location called");
        this.IS_STORE = isStore;
        this.IS_TRACKING_LOCATION = true;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (locationDialog.isShowing()) locationDialog.dismiss();
                if (isStore) {
                    requestNewLocationData();
                } else {
                    mFusedLocationClient.getLastLocation().addOnCompleteListener(
                            task -> {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    executeLocationLogic(location);
                                }
                            }
                    );
                }
            } else {
                if (!locationDialog.isShowing()) locationDialog.show();
            }
        } else {
            requestPermissions();
        }
    }

    private Dialog getLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.Dialog_MainActviity));
        builder.setTitle("Oops, Device location turned off.");
        builder.setMessage("Please turn on location to let us provide you best user experience.");
        builder.setCancelable(false);

        // add a button
        builder.setPositiveButton("Turn on", (dialogInterface, i) -> {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        return dialog;
    }


    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            executeLocationLogic(mLastLocation);
        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation(IS_STORE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (IS_TRACKING_LOCATION && checkPermissions()) {
            getLastLocation(IS_STORE);
        }
    }

    public abstract void OnPlaceDetected(UserLocation userLocation, String error);

    private void executeLocationLogic(Location location) {

        if (location == null || GOT_LOCATION)
            return;

        GOT_LOCATION = true;

        onLocationDetected(location);

        ServiceGenerator.createGeoService(GeoCodingApi.class).
                executeReverseGeoCoding(location.getLatitude() + "," + location.getLongitude(), "AIzaSyC5LH5UQ8_J0ZtZWsFBdG9HZdaxeYPPkQo")
                .enqueue(new Callback<Reverse>() {
                    @Override
                    public void onResponse(Call<Reverse> call, Response<Reverse> response) {
                        if (response.isSuccessful() && response.body()!=null) {
                            Reverse reverse = response.body();
                            String pincode =
                                    (reverse.getResults().getAddressComponents().get(reverse.getResults().getAddressComponents().size() - 1).getTypes().contains(PIN_CODE)) ?
                                            reverse.getResults().getAddressComponents().get(reverse.getResults().getAddressComponents().size() - 1).getLongName() :
                                            findComponent(reverse.getResults().getAddressComponents(), PIN_CODE);

                            String city = (reverse.getResults().getAddressComponents().get(reverse.getResults().getAddressComponents().size() - 3).getTypes().contains(CITY)) ?
                                    reverse.getResults().getAddressComponents().get(reverse.getResults().getAddressComponents().size() - 3).getLongName() :
                                    findComponent(reverse.getResults().getAddressComponents(), CITY);

                            OnPlaceDetected(new UserLocation(new double[] {location.getLatitude(),location.getLongitude()},
                                    reverse.getResults().getFormattedAddress(),
                                    reverse.getResults().getPlaceId(),
                                    city,
                                    pincode), null);
                        }else{

                        }

                    }

                    @Override
                    public void onFailure(Call<Reverse> call, Throwable t) {
                        Log.i("LOCATION", "RESPONSE" + t.getMessage() + t.getCause());
                    }
                });

    }

    public Bitmap addGradient(Bitmap originalBitmap) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Bitmap updatedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(updatedBitmap);

        canvas.drawBitmap(originalBitmap, 0, 0, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, 0, 0, height, 0xFFF0D252, 0xFFF07305, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(0, 0, width, height, paint);

        return updatedBitmap;
    }
}

