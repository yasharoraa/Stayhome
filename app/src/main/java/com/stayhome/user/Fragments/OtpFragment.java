package com.stayhome.user.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.stayhome.user.Activities.HomeActivityUser;
import com.stayhome.user.Activities.MainActivity;
import com.stayhome.user.Models.User.CreateUser;
import com.stayhome.user.Models.User.User;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.Utils.OtpEditText;
import com.stayhome.user.WebServices.ApiInterface;
import com.stayhome.user.WebServices.ServiceGenerator;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.et_otp)
    OtpEditText otpEditText;

    @BindView(R.id.error_text_view)
    TextView errorTextView;

    @BindView(R.id.resend_button)
    Button resendButton;

    @BindView(R.id.change_number_button)
    Button changeNumberButton;

    private MainActivity activity;

    private Unbinder unbinder;

    private String tempId;

    private String phone;

    private String pass;

    private final String SAVED_PROCESSING = "saved_processing";

    private final String SAVED_TEXT = "saved_text";

    public boolean processing;

    private final String TAG = this.getClass().getSimpleName();

    public Call call1;
    public Call<ResponseBody> call2;

    private CountDownTimer countDownTimer;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.otp_verification, container, false);
        activity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;
        if (activity == null) return rootView;
        unbinder = ButterKnife.bind(this, rootView);
        otpEditText.getPaint().setColor(Color.parseColor("#FFFFFF"));
        if (savedInstanceState == null) {
            if (getArguments() == null) return null;
            tempId = getArguments().getString(Constants.TEMP_ID);
            phone = getArguments().getString(Constants.PHONE_NUMBER);
            pass = getArguments().getString(Constants.PASSWORD);
        } else {
            tempId = savedInstanceState.getString(Constants.TEMP_ID);
            phone = savedInstanceState.getString(Constants.PHONE_NUMBER);
            pass = savedInstanceState.getString(Constants.PASSWORD);
            processing = savedInstanceState.getBoolean(SAVED_PROCESSING);
            if (processing)
                activity.toggleProgress(true, "");

            String otp = savedInstanceState.getString(SAVED_TEXT);
            if (otp != null)
                otpEditText.setText(otp);
        }

        if (tempId == null || phone == null || pass == null) return null;

        changeNumberButton.setOnClickListener(this);
        resendButton.setOnClickListener(this);

        otpEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 6) {
                    Constants.hideKeyboard(activity);
                    createUser(Integer.parseInt(editable.toString()));
                }
            }
        });

        otpEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (otpEditText.getText() != null && otpEditText.getText().length() == 6) {
                createUser(Integer.parseInt(otpEditText.toString()));
            }
            return false;
        });

        countDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                if (resendButton != null)
                    resendButton.setText(String.format(Locale.getDefault(),"%s%d", getString(R.string.resend_otp), millisUntilFinished / 1000));
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                if (resendButton!=null){
                    resendButton.setEnabled(true);
                    resendButton.setText(R.string.resend);
                }
            }

        };
        countDownTimer.start();
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_PROCESSING, processing);
        outState.putString(Constants.TEMP_ID, tempId);
        outState.putString(Constants.PHONE_NUMBER, phone);
        outState.putString(Constants.PASSWORD, pass);
        if (otpEditText.getText() != null)
            outState.putString(SAVED_TEXT, otpEditText.getText().toString());
    }


    private void logout() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.TOKEN))
            sharedPreferences.edit().remove(Constants.TOKEN).apply();

        activity.toggleProgress(false, null);
    }

    private void loginWithFirebase(String jwt) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        logout();
                        processing = false;
                        activity.toggleProgress(false, null);
                        makeToast(getString(R.string.intace_id_error));
                        return;
                    }

                    // Get new Instance ID token
                    String token = task.getResult().getToken();
                    Log.i(TAG, jwt + "  ");
                    call2 = ServiceGenerator.createService(ApiInterface.class).putMyFirebaseInstance(jwt, token);
                    call2.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                if (isSafe())
                                    startActivity(new Intent(activity, HomeActivityUser.class));
                                processing = false;
                            } else {
                                logout();
                                makeToast("Login Failed, Error : Response failure adding instance Id.");
                                processing = false;
                                activity.toggleProgress(false, null);
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            logout();
                            makeToast("Login Failed, Error : Network error adding instance Id.");
                            processing = false;
                            activity.toggleProgress(false, null);
                        }
                    });
                });
    }

    private boolean isSafe() {
        return !(this.isRemoving() || this.getActivity() == null || this.isDetached() || !this.isAdded() || this.getView() == null);
    }

    private void makeToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    private void createUser(int OTP) {
        setError(null);
        processing = true;
        activity.toggleProgress(true, "Logging in");
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        call1 = loginUser(apiInterface.verifyNumber(tempId, OTP));
    }

    private void resendOtp() {
        CreateUser user = new CreateUser(phone, pass);
        setError(null);
        processing = true;
        activity.toggleProgress(true, "Logging in");
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        call1 = postUser(apiInterface.createUserProfile(user));

    }


    private Call<User> loginUser(Call<User> call) {
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getToken() != null) {
                        User user = response.body();
                        loginWithFirebase(putValues(user.getToken()));
                    } else {
                        setError(getString(R.string.empty_response));
                        processing = false;
                        activity.toggleProgress(false, null);
                    }
                } else {
                    setupError(response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                setError(getString(R.string.check_connection));
                activity.toggleProgress(false, null);
            }
        });
        return call;
    }

    private void setupError(ResponseBody errorBody) {
        if (errorBody != null) {
            try {
                JSONObject jsonObject = new JSONObject(errorBody.string());
                JSONObject errJson = jsonObject.getJSONObject(jsonObject.keys().next());
                String key = errJson.getString(errJson.keys().next());
                setError(key);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
                setError(getString(R.string.unknown_response));
            }
        }
        processing = false;
        activity.toggleProgress(false, null);
    }

    private void setError(String text) {
        if (errorTextView != null) errorTextView.setText(text == null ? "" : text);
    }

    private String putValues(String token) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String value = "Bearer " + token.trim();
        sharedPreferences.edit().putString(Constants.TOKEN, value).apply();
        return value;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer!=null)
            countDownTimer.cancel();
        unbinder.unbind();
    }

    private Call<JsonObject> postUser(Call<JsonObject> call) {
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tempId = response.body().get("id").getAsString();
                    processing = false;
                    activity.toggleProgress(false, null);
                } else {
                    setupError(response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                setError(getString(R.string.check_connection));
                activity.toggleProgress(false, null);
            }
        });
        return call;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.resend_button :
                 resendOtp();
                 resendButton.setEnabled(false);
                 resendButton.setText(null);
                 break;
            case R.id.change_number_button:
                activity.getSupportFragmentManager().popBackStack();
                break;
        }
    }
}
