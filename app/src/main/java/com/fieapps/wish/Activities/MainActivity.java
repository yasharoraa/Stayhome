package com.fieapps.wish.Activities;

import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.fieapps.wish.Fragments.FillDetailsFragment;
import com.fieapps.wish.Fragments.SelectTypeFragment;
import com.fieapps.wish.Fragments.SignInFragment;
import com.fieapps.wish.Fragments.StoreUploadImageFragment;
import com.fieapps.wish.Models.Geocoding.Reverse;
import com.fieapps.wish.Models.Geocoding.UserLocation;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.Constants;
import com.fieapps.wish.Utils.Constants.MainEnum;
import com.fieapps.wish.Utils.LocationDetectingActivity;
import com.fieapps.wish.Utils.MyApplication;

import butterknife.ButterKnife;

public class MainActivity extends LocationDetectingActivity {

    ProgressBar progressBar;

    TextView textView;

    View view;

    View baseLayout;

    boolean isBackgroundChanged = false;

    ImageView imageView;

    private final String TAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.layout_bottom);
        imageView = findViewById(R.id.stay_home_image);
        baseLayout = findViewById(R.id.main_activity_base_layout);
        textView = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        Constants.setFullScreen(this);
        ButterKnife.bind(this);
        if(savedInstanceState == null) {
//            change(MainEnum.FILL_DETAILS_STORE);
//            change(MainEnum.LOADING_ON);
            change(MainEnum.SIGN_IN_STORE);
        }
    }
    public void change(MainEnum mainEnum){
        toggleProgress(false,"");
        switch (mainEnum){
            case SELECT_TYPE:
                changeColor(true);
                changeFragment(new SelectTypeFragment());
                break;
            case SIGN_IN:
                changeColor(true);
                SignInFragment signInFragment = new SignInFragment();
                signInFragment.setArguments(getBundle(Constants.LOGIN_TYPE,0));
                changeFragment(signInFragment);
                break;
            case SIGN_IN_STORE:
                changeColor(false);
                SignInFragment storeSignFragment = new SignInFragment();
                storeSignFragment.setArguments(getBundle(Constants.LOGIN_TYPE,1));
                changeFragment(storeSignFragment);
                break;
            case FILL_DETAILS_USER:
                imageView.setVisibility(View.GONE);
                FillDetailsFragment fillDetailsFragmentUser = new FillDetailsFragment();
                fillDetailsFragmentUser.setArguments(getBundle(Constants.FILL_DETAILS_TYPE,0));
                changeFragment(fillDetailsFragmentUser);
                break;
            case FILL_DETAILS_STORE:
                imageView.setVisibility(View.GONE);
                FillDetailsFragment fillDetailsFragmentStore = new FillDetailsFragment();
                fillDetailsFragmentStore.setArguments(getBundle(Constants.FILL_DETAILS_TYPE,1));
                changeFragment(fillDetailsFragmentStore);
                break;
            case UPLOAD_IMAGE_STORE:
                imageView.setVisibility(View.GONE);
                changeFragment(new StoreUploadImageFragment());
                break;

        }
    }



    public void toggleProgress(boolean isProgress,String text){
        if (view!=null) view.setVisibility(isProgress?View.GONE:View.VISIBLE);
        if (progressBar!=null) progressBar.setVisibility(isProgress?View.VISIBLE:View.GONE);
        if (textView!=null) textView.setText(text);
    }

    void changeFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_NONE)
                .replace(R.id.layout_bottom,fragment)
                .addToBackStack(null)
                .commit();
    }



    private Bundle getBundle(String key,int value){
        Bundle bundle = new Bundle();
        bundle.putInt(key,value);
        return bundle;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(R.id.layout_bottom)!=null &&
                fragmentManager.findFragmentById(R.id.layout_bottom) instanceof FillDetailsFragment &&
        imageView!=null)
            imageView.setVisibility(View.VISIBLE);

        super.onBackPressed();

    }

    private void changeColor(boolean reverse){
        if (isBackgroundChanged != reverse)
            return;

        TransitionDrawable transition = (TransitionDrawable) baseLayout.getBackground();
        transition.setCrossFadeEnabled(true);
        if (reverse){
            transition.reverseTransition(250);
            isBackgroundChanged = false;
        }else{
            transition.startTransition(250);
            isBackgroundChanged = true;
        }
    }

    @Override
    public void onLocationDetected(Location mLocation) {

    }

    @Override
    public void OnPlaceDetected(UserLocation userLocation, String error) {
        Log.i(TAG,"location detected");
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.layout_bottom);
        if (fragment instanceof  FillDetailsFragment) {
            Log.i(TAG,"location detected fragment right");
            ((FillDetailsFragment) fragment).OnLocationDetected(userLocation);
            ((MyApplication)this.getApplication()).setLocation(userLocation);
        }

    }

    public interface onLocationDetected {
        void onLocation();
    }
}
