package com.fieapps.stayhomeindia.Activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.fieapps.stayhomeindia.BuildConfig;
import com.fieapps.stayhomeindia.R;
import com.fieapps.stayhomeindia.Utils.Constants;
import com.fieapps.stayhomeindia.WebServices.ApiInterface;
import com.fieapps.stayhomeindia.WebServices.ServiceGenerator;
import com.google.android.gms.common.api.Api;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreenActivity extends AppCompatActivity {

//    @BindView(R.id.img)
//    ImageView img;
//
//    @BindView(R.id.shine)
//    ImageView shine;

    @BindView(R.id.image_view)
    ImageView imageView;

    @BindView(R.id.image_view_cap)
    ImageView imageViewCap;

    private boolean processing;

    private final String TAG = this.getClass().getSimpleName();

    private Call<JsonObject> call1;
    private Call<ResponseBody> call2;

    private boolean doubleBackToExitPressedOnce;

    private final String SAVED_PROCESSING = "saved_processing";

    private long time;


    Timer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);
        ButterKnife.bind(this);
        time = System.currentTimeMillis();
        Constants.setFullScreen(this);
        Animation animation = new TranslateAnimation(-500, 0,0, 0);
        animation.setDuration(300);
        animation.setFillAfter(true);
        imageView.startAnimation(animation);

        Animation capAnimation = new TranslateAnimation(500, 0,0, 0);
        capAnimation.setDuration(300);
        capAnimation.setFillAfter(true);
        imageViewCap.startAnimation(capAnimation);

        if (savedInstanceState == null) {
            time = System.currentTimeMillis();
            checkVersion();
        } else {
            processing = savedInstanceState.getBoolean(SAVED_PROCESSING);
            imageView.setImageResource(R.drawable.ic_home);
        }
    }

    private void checkVersion(){
        processing = true;
        ServiceGenerator.createService(ApiInterface.class).getMinimumVersion().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body()!=null){
                    if (BuildConfig.VERSION_CODE<Integer.parseInt(response.body())){
                        createUpdateRequiredDialog();
                    }else{
                        if (checkUser()) {
                            processing = false;
                            changeActivity(new Intent(SplashScreenActivity.this, MainActivity.class),true);
                        }
                    }
                }else{
                    createTryAgainExitDialog();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                createTryAgainExitDialog();
            }
        });
    }

    private void createUpdateRequiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Required!");
        builder.setMessage("App update required to continue using StayHome");
        builder.setCancelable(false);
        builder.setPositiveButton("Update", (dialogInterface, i) -> {
            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        });
        builder.create().show();
    }


//    @Override
//    protected void onStart() {
//        super.onStart();
//        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
//        animationDrawable.setEnterFadeDuration(200);
//        animationDrawable.setExitFadeDuration(200);
//        animationDrawable.start();
//    }

    private void changeActivity(Intent intent, boolean wait) {
        if (!wait){
            startActivity(intent);
            return;
        }
        if (System.currentTimeMillis() - time > 300) {
            startActivity(intent);
        } else {
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(intent);
                }

            }, 400);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_PROCESSING, processing);
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.TOKEN))
            sharedPreferences.edit().remove(Constants.TOKEN).apply();

        changeActivity(new Intent(SplashScreenActivity.this, MainActivity.class),true);
    }


    private boolean checkUser() {
        String token = getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
        if (token != null) {
            call1 = ServiceGenerator.createService(ApiInterface.class).checkMyData(token);
            call1.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        loginWithFirebase(token, response.body().get("firebase") == null ? null : response.body().get("firebase").getAsString());
                    } else if (response.code() == 401) {
                        processing = false;
                        logout();
                    } else {
                        processing = false;
                        createLogoutTryAgainDialog();
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    processing = false;
                    createTryAgainExitDialog();
                }
            });

        }
        return token == null;
    }

    private void loginWithFirebase(String jwt, String existingId) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        processing = false;
                        createLogoutTryAgainDialog();
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    if (existingId != null && existingId.equals(token)) {
                        changeActivity(new Intent(SplashScreenActivity.this, HomeActivityUser.class),false);
                        processing = false;
                        return;
                    }

                    call2 = ServiceGenerator.createService(ApiInterface.class).putMyFirebaseInstance(jwt, token);
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                changeActivity(new Intent(SplashScreenActivity.this, HomeActivityUser.class),false);
                                processing = false;
                            } else {
                                logout();
                                processing = false;
                                createLogoutTryAgainDialog();

                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            logout();
                            processing = false;
                            createLogoutTryAgainDialog();
                        }
                    });
                }).addOnFailureListener(e -> {
            processing = false;
            createLogoutTryAgainDialog();
        });
    }

    private void createLogoutTryAgainDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.failed_validation);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.try_again, (dialogInterface, i) -> checkUser());
        builder.setNegativeButton(R.string.logout, (dialogInterface, i) -> logout());
        builder.create().show();
    }

    private void createTryAgainExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(R.string.check_connection);
        builder.setPositiveButton(R.string.try_again, (dialogInterface, i) -> checkUser());
        builder.setNegativeButton(R.string.exit, (dialogInterface, i) -> this.finishAffinity());
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {

            if (processing) {
                if (call1 != null)
                    call1.cancel();
                if (call2 != null)
                    call2.cancel();
            }
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.back_button_again, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myTimer != null)
            myTimer.cancel();
    }
}
