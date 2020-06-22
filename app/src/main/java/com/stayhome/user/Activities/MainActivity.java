package com.stayhome.user.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.stayhome.user.Fragments.OtpFragment;
import com.stayhome.user.Fragments.SignInFragment;
import com.stayhome.user.Models.User.CreateUser;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;

    TextView textView;

    View view;

    View baseLayout;

    private final String TAG = this.getClass().getSimpleName();

    private int selectedFragment;

    public final String SAVED_SELECTED = "saved_selected";

    public final String CURRENT_FRAGMENT = "current_fragment";

    public final int SIGN_IN = 0;

    public final int OTP = 1;

    private boolean progress;

    private final String SAVED_PROGRESS = "saved_progress";

    private boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.layout_bottom);
        baseLayout = findViewById(R.id.main_activity_base_layout);
        textView = findViewById(R.id.progress_text);
        progressBar = findViewById(R.id.progress_bar);
        Constants.setFullScreen(this);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            change(0,null,null);
        } else {
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT);
            if (fragment!=null){
                changeFragment(fragment);
                return;
            }
            selectedFragment = savedInstanceState.getInt(SAVED_SELECTED);
            change(selectedFragment,null,null);
            progress = savedInstanceState.getBoolean(SAVED_PROGRESS);
            if (progress)
                toggleProgress(true,null);
        }
    }
    public void change(int type, String tempId, CreateUser createUser){
        selectedFragment = type;
        toggleProgress(false,"");
        switch (type){
            case SIGN_IN:
                SignInFragment signInFragment = new SignInFragment();
                changeFragment(signInFragment);
                break;
            case OTP:
                OtpFragment otpFragment = new OtpFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.TEMP_ID,tempId);
                bundle.putString(Constants.PHONE_NUMBER,createUser.getUser().getPhone());
                bundle.putString(Constants.PASSWORD,createUser.getUser().getPassword());
                otpFragment.setArguments(bundle);
                changeFragment(otpFragment);
                break;
        }
    }



    public void toggleProgress(boolean isProgress,String text){
        progress = isProgress;
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.layout_bottom);
        if (fragment != null)
            getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT, fragment);

        outState.putInt(SAVED_SELECTED, selectedFragment);
        outState.putBoolean(SAVED_PROGRESS,progress);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.layout_bottom);
            if (fragment instanceof SignInFragment) {
                if(((SignInFragment) fragment).processing){
                    if (((SignInFragment) fragment).call1!=null)((SignInFragment) fragment).call1.cancel();
                    if (((SignInFragment) fragment).call2!=null)((SignInFragment) fragment).call2.cancel();
                }
            } else if (fragment instanceof OtpFragment) {
                if (((OtpFragment) fragment).processing) {
                    if (((OtpFragment) fragment).call1 != null)
                        ((OtpFragment) fragment).call1.cancel();
                    if (((OtpFragment) fragment).call2 != null)
                        ((OtpFragment) fragment).call2.cancel();
                }
            }
            this.finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.exit_confirm, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }
}
