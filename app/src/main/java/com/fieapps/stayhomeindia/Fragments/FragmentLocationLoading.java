package com.fieapps.stayhomeindia.Fragments;

import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.fieapps.stayhomeindia.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FragmentLocationLoading extends DialogFragment {

    @BindView(R.id.pin)
    ImageView iv;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_location_loading,container,false);
        unbinder = ButterKnife.bind(this,rootView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){

                final AnimatedVectorDrawable avd = (AnimatedVectorDrawable) iv.getDrawable();

                avd.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        avd.start();
                    }
                });
                avd.start();
            }else {
                final AnimatedVectorDrawable avd = (AnimatedVectorDrawable) iv.getDrawable();

                avd.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        avd.start();
                    }
                });
                avd.start();
//                final AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) iv.getDrawable();
//
//                avd.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
//                    @Override
//                    public void onAnimationEnd(Drawable drawable) {
//                        avd.start();
//                    }
//                });
//                avd.start();
            }

        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
