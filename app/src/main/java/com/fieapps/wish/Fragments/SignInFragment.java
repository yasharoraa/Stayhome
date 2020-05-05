package com.fieapps.wish.Fragments;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fieapps.wish.Activities.HomeActivityUser;
import com.fieapps.wish.Activities.MainActivity;
import com.fieapps.wish.Models.CreateUser;
import com.fieapps.wish.Models.Store.Store;
import com.fieapps.wish.Models.User.User;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.Constants;
import com.fieapps.wish.WebServices.ApiInterface;
import com.fieapps.wish.WebServices.ServiceGenerator;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fieapps.wish.Utils.Constants.MainEnum.FILL_DETAILS_STORE;
import static com.fieapps.wish.Utils.Constants.MainEnum.FILL_DETAILS_USER;
import static com.fieapps.wish.Utils.Constants.MainEnum.LOADING_OFF;

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

    private boolean SignUp = false;

    private int i;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        activity = (getActivity() instanceof MainActivity) ? (MainActivity) getActivity() : null;
        if (activity == null || getArguments() == null) return rootView;
        i = getArguments().getInt(Constants.LOGIN_TYPE);
        unbinder = ButterKnife.bind(this, rootView);
        signInButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
        signInToggle.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_create_account:
                toggleSignUpVisibility(true);
                break;
            case R.id.sign_in:
                toggleSignUpVisibility(false);
                break;
            case R.id.button_sign_in:
                createUser(i == 1 ? Login.LOG_IN_STORE : Login.LOG_IN_USER);
                break;
        }
    }

    private void toggleSignUpVisibility(boolean isSignUp) {
        if (SignUp && isSignUp) {
            createUser(i == 1 ? Login.SIGN_UP_STORE : Login.SIGN_UP_USER);
            return;
        }
        SignUp = isSignUp;
        confirmLayout.setVisibility(isSignUp ? View.VISIBLE : View.GONE);
        alreadyView.setVisibility(isSignUp ? View.VISIBLE : View.GONE);
        signInButton.setVisibility(isSignUp ? View.GONE : View.VISIBLE);
    }

    private void createUser(Login type) {
        CreateUser user = getUserDetails(type.equals(Login.SIGN_UP_USER) || type.equals(Login.SIGN_UP_STORE));
        if (user == null) return;
        setError(null);
        activity.toggleProgress(true,"Logging in");
        if (i == 1) {
            Call<Store> call = type.equals(Login.SIGN_UP_STORE) ? ServiceGenerator.createService(ApiInterface.class).createStoreProfile(user) :
                    ServiceGenerator.createService(ApiInterface.class).loginStore(user);
            call.enqueue(new Callback<Store>() {
                @Override
                public void onResponse(Call<Store> call, Response<Store> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getToken() != null) {
                            Store store = response.body();
                            putValues(store.getToken());
                            if (store.getName()  == null || store.getOwner() == null || store.getAddress() == null || store.getGstin() == null || store.getPincode() == null){
                                activity.change(FILL_DETAILS_STORE);
                                return;
                            }

                        } else {
                            setError("Empty response from server");
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                JSONObject errJson = jsonObject.getJSONObject(jsonObject.keys().next());
                                String key = errJson.getString(errJson.keys().next());
                                setError(key);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                setError("Unknown Response ! Please try again later");
                            }
                        }
                    }
                    activity.change(LOADING_OFF);
                }

                @Override
                public void onFailure(Call<Store> call, Throwable t) {
                    setError("Please check your internet connectivity and try again !");
                    activity.change(LOADING_OFF);
                }
            });
        } else {
            Call<User> call = type.equals(Login.SIGN_UP_USER) ? ServiceGenerator.createService(ApiInterface.class).createUserProfile(user) :
                    ServiceGenerator.createService(ApiInterface.class).loginUser(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null && response.body().getToken() != null) {
                            User user = response.body();
                            putValues(user.getToken());
                            if (user.getName()  == null || user.getAddress() == null || user.getPincode() == null){
                                activity.change(FILL_DETAILS_USER);

                            }else{
                                startActivity(new Intent(activity, HomeActivityUser.class));
                            }
                            return;

                        } else {
                            setError("Empty response from server");
                        }
                    } else {
                        if (response.errorBody() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                JSONObject errJson = jsonObject.getJSONObject(jsonObject.keys().next());
                                String key = errJson.getString(errJson.keys().next());
                                setError(key);
                            } catch (JSONException | IOException e) {
                                e.printStackTrace();
                                setError("Unknown Response ! Please try again later");
                            }
                        }
                    }
                    activity.change(LOADING_OFF);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    setError("Please check your internet connectivity and try again !");
                    activity.change(LOADING_OFF);
                }
            });
        }

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
            phoneNumberEditText.setError("Enter a 10 digit valid number");
            focusView = phoneNumberEditText;
            cancel = true;
        } else if (!isValid(password, true)) {
            passwordEditText.setError("Enter a valid 6-20 character password with at least one digit and letter");
            focusView = passwordEditText;
            cancel = true;
        } else if (isSignUp && !conFirmPassword.equals(password)) {
            confirmPasswordEditText.setError("Passwords do not match");
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



    private enum Login {
        LOG_IN_USER,
        SIGN_UP_USER,
        LOG_IN_STORE,
        SIGN_UP_STORE
    }

    private void putValues(String token) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(Constants.LOGIN_TYPE, i).putString(Constants.TOKEN, "Bearer " +token.trim()).apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
