package com.fieapps.stayhomeindia.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fieapps.stayhomeindia.Activities.HomeActivityUser;
import com.fieapps.stayhomeindia.R;
import com.fieapps.stayhomeindia.Utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends Fragment {

    @BindView(R.id.text_view)
    TextView textView;

    @BindView(R.id.email_text_view)
    TextView emailTextView;

    private HomeActivityUser activity;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        String token = null;
        activity = (getActivity() instanceof HomeActivityUser) ? (HomeActivityUser) getActivity() : null;
        if (activity == null) return null;
        token = Constants.token(activity);

        if (token != null)
            getMyProfile(token);

        emailTextView.setOnClickListener(view -> {
            Intent sendEmail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"+"indstayhome@gmail.com")); // mention an email id here
            startActivity(Intent.createChooser(sendEmail, "Choose an email client from..."));
        });
        return rootView;

    }

    private void getMyProfile(String token) {
        activity.profileViewModel.init(token);
        activity.profileViewModel.getMyProfile().observe(this,user -> {
            if (user!=null && textView!=null)
                textView.setText(String.format("+91 %s", user.getPhone()));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
