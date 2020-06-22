package com.stayhome.user.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.stayhome.user.Models.ResetPassword;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.Utils.OtpEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ResetPasswordActivity extends AppCompatActivity {

    @BindView(R.id.extended_fab)
    ExtendedFloatingActionButton fab;

    @BindView(R.id.et_otp)
    OtpEditText otpEditText;

    @BindView(R.id.password_edit_text)
    TextInputEditText passwordEditText;

    @BindView(R.id.confirm_password_edit_text)
    TextInputEditText confirmPasswordEditText;

    private String resetId;

    private String phoneNumber;

    private final String SAVED_PASSWORD = "saved_password";

    private final String SAVED_CONFIRM_PASSWORD = "confirm_password";

    private final String SAVED_OTP = "saved_otp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Constants.setFullScreen(this);
        ButterKnife.bind(this);
        otpEditText.getPaint().setColor(Color.parseColor("#FFFFFF"));

        if (savedInstanceState == null) {
            resetId = getIntent().getStringExtra(Constants.RESET_ID);
            phoneNumber = getIntent().getStringExtra(Constants.PHONE_NUMBER);
        } else {
            resetId = savedInstanceState.getString(Constants.RESET_ID);
            phoneNumber = savedInstanceState.getString(Constants.PHONE_NUMBER);
            otpEditText.setText(savedInstanceState.getString(SAVED_OTP));
            passwordEditText.setText(savedInstanceState.getString(SAVED_PASSWORD));
            confirmPasswordEditText.setText(savedInstanceState.getString(SAVED_CONFIRM_PASSWORD));
        }

        fab.setOnClickListener(v -> {
            ResetPassword resetPassword= resetPassword();
            if (resetPassword == null) return;
            Intent resultIntent = new Intent();
            resultIntent.putExtra(Constants.RESET_PASSWORD,resetPassword);
            setResult(Activity.RESULT_OK,resultIntent);
            finish();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString(Constants.RESET_ID, resetId);
        outState.putString(Constants.PHONE_NUMBER, phoneNumber);
        if (otpEditText.getText() != null)
            outState.putString(SAVED_OTP, otpEditText.getText().toString());
        if (passwordEditText.getText() != null)
            outState.putString(SAVED_PASSWORD, passwordEditText.getText().toString());
        if (confirmPasswordEditText.getText()!=null)
            outState.putString(SAVED_CONFIRM_PASSWORD , confirmPasswordEditText.getText().toString());
    }

    private ResetPassword resetPassword(){

        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);
        boolean cancel = false;
        View focusView = null;

        String password  = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
        String confirmPassword = confirmPasswordEditText.getText() != null ? confirmPasswordEditText.getText().toString().trim() : "";
        String otp = otpEditText.getText()!=null ? otpEditText.getText().toString().trim() : "";


        if (isNotValid(otp, false)){
            Toast.makeText(this,"Enter a valid six digit OTP",Toast.LENGTH_SHORT).show();
            focusView = otpEditText;
            cancel = true;
        }else if (isNotValid(password, true)){
            passwordEditText.setError(getString(R.string.valid_password), null);
            focusView = passwordEditText;
            cancel = true;
        } else if (!confirmPassword.equals(password)){
            confirmPasswordEditText.setError(getString(R.string.password_do_not_match), null);
            focusView = confirmPasswordEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return null;
        }

        return new ResetPassword(phoneNumber,Integer.parseInt(otp),password,resetId);

    }

    private static boolean isNotValid(String input, boolean isPassword) {
        Pattern pattern;
        Matcher matcher;
        String PATTERN = isPassword ? ("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$") : ("^[0-9]{6}$");
        pattern = Pattern.compile(PATTERN);
        matcher = pattern.matcher(input);
        return !matcher.matches();
    }

}