package com.fieapps.stayhomeindia.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fieapps.stayhomeindia.Activities.HomeActivityUser;
import com.fieapps.stayhomeindia.Activities.MainActivity;
import com.fieapps.stayhomeindia.Models.User.CreateUser;
import com.fieapps.stayhomeindia.Models.User.User;
import com.fieapps.stayhomeindia.R;
import com.fieapps.stayhomeindia.Utils.Constants;
import com.fieapps.stayhomeindia.WebServices.ApiInterface;
import com.fieapps.stayhomeindia.WebServices.ServiceGenerator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignInFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.phone_number_edit_text)
    EditText phoneNumberEditText;

    @BindView(R.id.password_edit_text)
    EditText passwordEditText;

    @BindView(R.id.confirm_password_edit_text)
    EditText confirmPasswordEditText;

    @BindView(R.id.button_sign_in)
    Button signInButton;

    @BindView(R.id.button_create_account)
    Button signUpButton;

    @BindView(R.id.confirm_password_text_input)
    TextInputLayout confirmLayout;

    @BindView(R.id.layout_login)
    View loginLayout;

    @BindView(R.id.toggle_sign)
    View alreadyView;

    @BindView(R.id.sign_in)
    TextView signInToggle;

    @BindView(R.id.error_text)
    TextView errorText;

    private MainActivity activity;

    private Unbinder unbinder;

    private boolean SignUp;

    private final String TAG = this.getClass().getSimpleName();

    public Call call1;

    public Call<ResponseBody> call2;

    public boolean processing;

    private final String SAVED_PHONE = "saved_phone";

    private final String SAVED_PASS = "saved_pass";

    private final String SAVED_CONFIRM_PASS = "saved_con";

    private final String SAVED_SIGN_UP = "saved_sign_up";

    private final String SAVED_PROCESSING = "saved_processing";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        activity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;
        if (activity == null) return rootView;
        unbinder = ButterKnife.bind(this, rootView);
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        signInToggle.setOnClickListener(this);
        if (savedInstanceState != null) {
            phoneNumberEditText.setText(savedInstanceState.getString(SAVED_PHONE));
            passwordEditText.setText(savedInstanceState.getString(SAVED_PASS));
            confirmPasswordEditText.setText(savedInstanceState.getString(SAVED_CONFIRM_PASS));
            if (savedInstanceState.getBoolean(SAVED_SIGN_UP)) {
                toggleSignUpVisibility(true);
            }
            processing = savedInstanceState.getBoolean(SAVED_PROCESSING);
            if (processing)
                activity.toggleProgress(true,"");
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (phoneNumberEditText != null)
            outState.putString(SAVED_PHONE, phoneNumberEditText.getText().toString());

        if (passwordEditText != null)
            outState.putString(SAVED_PASS, passwordEditText.getText().toString());

        if (confirmPasswordEditText != null)
            outState.putString(SAVED_CONFIRM_PASS, confirmPasswordEditText.getText().toString());

        outState.putBoolean(SAVED_SIGN_UP, SignUp);
        outState.putBoolean(SAVED_PROCESSING, processing);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (SignUp){
            confirmLayout.setVisibility(View.VISIBLE);
            alreadyView.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_create_account:
                toggleSignUpVisibility(true);
                Log.i(TAG, "create account");
                break;
            case R.id.sign_in:
                toggleSignUpVisibility(false);
                Log.i(TAG, "sign in");
                break;
            case R.id.button_sign_in:
                createUser(Login.LOG_IN_USER);
                Log.i(TAG, "sign in button");
                break;
        }
    }

    private void toggleSignUpVisibility(boolean isSignUp) {
        if (SignUp && isSignUp) {
            createUser(Login.SIGN_UP_USER);
            return;
        }
        SignUp = isSignUp;
        confirmLayout.setVisibility(isSignUp ? View.VISIBLE : View.GONE);
        alreadyView.setVisibility(isSignUp ? View.VISIBLE : View.GONE);
        signInButton.setVisibility(isSignUp ? View.GONE : View.VISIBLE);
    }

//    private void createUser(Login type) {
//        Log.i(TAG, "createuser");
//        CreateUser user = getUserDetails(type.equals(Login.SIGN_UP_USER));
//        if (user == null) return;
//        setError(null);
//        processing = true;
//        activity.toggleProgress(true, "Logging in");
//        call1 = type.equals(Login.SIGN_UP_USER) ? ServiceGenerator.createService(ApiInterface.class).createUserProfile(user) :
//                ServiceGenerator.createService(ApiInterface.class).loginUser(user);
//        call1.enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful()) {
//                    if (response.body() != null && response.body().getToken() != null) {
//                        User user = response.body();
//                        loginWithFirebase(putValues(user.getToken()));
//                    } else {
//                        setError(getString(R.string.empty_response));
//                        processing = false;
//                        activity.toggleProgress(false, null);
//                    }
//                } else {
//                    if (response.errorBody() != null) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response.errorBody().string());
//                            JSONObject errJson = jsonObject.getJSONObject(jsonObject.keys().next());
//                            String key = errJson.getString(errJson.keys().next());
//                            setError(key);
//                        } catch (JSONException | IOException e) {
//                            e.printStackTrace();
//                            setError(getString(R.string.unknown_response));
//                        }
//                    }
//                    processing = false;
//                    activity.toggleProgress(false, null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//                setError(getString(R.string.check_connection));
//                activity.toggleProgress(false, null);
//            }
//        });
//    }

    private void createUser(Login type) {
        CreateUser user = getUserDetails(type.equals(Login.SIGN_UP_USER));
        if (user == null) return;
        setError(null);
        processing = true;
        activity.toggleProgress(true, "Logging in");
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        if (type.equals(Login.LOG_IN_USER)) {
            call1 = loginUser(apiInterface.loginUser(user));
        } else {
            call1 = postUser(apiInterface.createUserProfile(user),user);
        }
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
                processing = false;
                setError(getString(R.string.check_connection));
                activity.toggleProgress(false, null);
            }
        });
        return call;
    }

    private Call<JsonObject> postUser(Call<JsonObject> call,CreateUser user) {
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.change(activity.OTP, response.body().get("id").getAsString(),user);
                } else {
                    setupError(response.errorBody());
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                processing = false;
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


    private void logout() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(Constants.TOKEN))
            sharedPreferences.edit().remove(Constants.TOKEN).apply();

        activity.toggleProgress(false, null);
    }

    private void makeToast(String text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
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

    private CreateUser getUserDetails(boolean isSignUp) {
        phoneNumberEditText.setError(null);
        passwordEditText.setError(null);

        boolean cancel = false;

        View focusView = null;

        String number = phoneNumberEditText.getText() != null ? phoneNumberEditText.getText().toString().trim() : "";

        String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";

        String conFirmPassword = confirmPasswordEditText.getText() != null ? confirmPasswordEditText.getText().toString() : "";

        if (!isValid(number, false)) {
            phoneNumberEditText.setError(getString(R.string.ten_digit_valid_number));
            focusView = phoneNumberEditText;
            cancel = true;
        } else if (!isValid(password, true)) {
            passwordEditText.setError(getString(R.string.valid_password),null);
            focusView = passwordEditText;
            cancel = true;
        } else if (isSignUp && !conFirmPassword.equals(password)) {
            confirmPasswordEditText.setError(getString(R.string.password_do_not_match),null);
            focusView = confirmPasswordEditText;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            Log.i("User : ", "CANCEL");
            return null;
        }
        Log.i("User : ", "! CANCEL");
        return new CreateUser(number, password);
    }

    private static boolean isValid(String input, boolean isPassword) {
        Pattern pattern;
        Matcher matcher;
        String PATTERN = isPassword ? ("^(?=.*[0-9])(?=.*[a-zA-Z]).{6,20}$") : ("^[0-9]{10}$");
        pattern = Pattern.compile(PATTERN);
        matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private void setError(String text) {
        if (errorText != null) errorText.setText(text == null ? "" : text);
    }


    public enum Login {
        LOG_IN_USER,
        SIGN_UP_USER
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
        unbinder.unbind();
    }

    private boolean isSafe() {
        return !(this.isRemoving() || this.getActivity() == null || this.isDetached() || !this.isAdded() || this.getView() == null);
    }
}
