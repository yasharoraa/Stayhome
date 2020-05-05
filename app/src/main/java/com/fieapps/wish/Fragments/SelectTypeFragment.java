package com.fieapps.wish.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fieapps.wish.Activities.MainActivity;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class SelectTypeFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.button_user)
    Button userButton;

    @BindView(R.id.button_store)
    Button storeButton;

    private MainActivity activity;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_type, container, false);
        activity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() :null;
        if (activity == null) return null;
        unbinder = ButterKnife.bind(this, rootView);
        storeButton.setOnClickListener(this);
        userButton.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_user:
                activity.change(Constants.MainEnum.SIGN_IN);
                break;
            case R.id.button_store:
                activity.change(Constants.MainEnum.SIGN_IN_STORE);
                break;
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
