package com.stayhome.user.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stayhome.user.Activities.CompleteOrderActivity;
import com.stayhome.user.Activities.HomeActivityUser;
import com.stayhome.user.Activities.MainActivity;
import com.stayhome.user.R;
import com.stayhome.user.Activities.SupportActivity;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.Utils.SafeClickFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ProfileFragment extends SafeClickFragment {

    @BindView(R.id.text_view)
    TextView textView;

    @BindView(R.id.saved_address_text_view)
    TextView savedAddressTextView;

    @BindView(R.id.support_text_view)
    TextView supportTextView;

    @BindView(R.id.about_us_text_view)
    TextView aboutUsTextView;

    @BindView(R.id.sign_out_text)
    TextView signOutTextView;

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

        savedAddressTextView.setOnClickListener(this);
        supportTextView.setOnClickListener(this);
        aboutUsTextView.setOnClickListener(this);
        signOutTextView.setOnClickListener(this);

        return rootView;
    }

    private void logout() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.TOKEN))
            sharedPreferences.edit().remove(Constants.TOKEN).apply();

        startActivity(new Intent(activity, MainActivity.class));
    }

    private void getMyProfile(String token) {
        activity.profileViewModel.init(token);
        activity.profileViewModel.getMyProfile().observe(this, user -> {
            if (user != null && textView != null)
                textView.setText(String.format("+91 %s", user.getPhone()));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSafeClick(View view) {
        switch (view.getId()) {
            case R.id.saved_address_text_view:
                Intent intent = new Intent(activity, CompleteOrderActivity.class);
                intent.putExtra(Constants.IS_ORDER, false);
                startActivity(intent);
                break;
            case R.id.support_text_view:
                startActivity(new Intent(activity, SupportActivity.class));
                break;
            case R.id.about_us_text_view:
                String url = "https://www.innovvapp.com/about";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.sign_out_text:
                logout();
                break;

        }
    }
}
