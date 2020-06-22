package com.stayhome.user.Fragments;

import android.app.Activity;
import android.app.Dialog;
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.stayhome.user.Activities.HomeActivityUser;
import com.stayhome.user.Activities.MainActivity;
import com.stayhome.user.Activities.ResetPasswordActivity;
import com.stayhome.user.Models.ResetPassword;
import com.stayhome.user.Models.User.CreateUser;
import com.stayhome.user.Models.User.User;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.WebServices.ApiInterface;
import com.stayhome.user.WebServices.ServiceGenerator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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


    @BindView(R.id.sign_in)
    TextView signInToggle;

    @BindView(R.id.error_text)
    TextView errorText;

    @BindView(R.id.action_title)
    TextView actionTitle;

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

    private final int RESET_PASSWORD_REQUEST_CODE = 236;

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
            SignUp = savedInstanceState.getBoolean(SAVED_SIGN_UP);
            processing = savedInstanceState.getBoolean(SAVED_PROCESSING);
            if (processing)
                activity.toggleProgress(true, "");
        } else {
            setSignUp(false);
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
        confirmLayout.setVisibility(SignUp ? View.VISIBLE : View.GONE);
        signInButton.setVisibility(SignUp ? View.GONE : View.VISIBLE);
        setSignUp(SignUp);
    }

    private void setSignUp(boolean isSignUp) {
        actionTitle.setText(getString(isSignUp ? R.string.already_have_a_account : R.string.forgot_password));
        signInToggle.setText(getString(isSignUp ? R.string.login : R.string.reset));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_create_account:
                toggleSignUpVisibility(true);
                Log.i(TAG, "create account");
                break;
            case R.id.sign_in:
                if (SignUp)
                    toggleSignUpVisibility(false);
                else
                    showDialog();

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
        setSignUp(isSignUp);
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
            call1 = postUser(apiInterface.createUserProfile(user), user);
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

    private void forgotPassword(String phone) {
        processing = true;
        activity.toggleProgress(true, "Sending verification code");
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<JsonObject> call = (Call<JsonObject>) apiInterface.forgotPassword(phone);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(activity, ResetPasswordActivity.class);
                    intent.putExtra(Constants.PHONE_NUMBER,phone);
                    intent.putExtra(Constants.RESET_ID,response.body().get("id").getAsString());
                    startActivityForResult(intent, RESET_PASSWORD_REQUEST_CODE);
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

        call1 = call;

    }

    private void resetPassword(ResetPassword resetPassword){
        processing = true;
        activity.toggleProgress(true, "Setting new password");
        ApiInterface apiInterface = ServiceGenerator.createService(ApiInterface.class);
        Call<ResponseBody> call =  apiInterface.resetPassword(resetPassword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    makeToast("Password successfully changed.");
                    setError("Password successfully changed.");
                    processing = false;
                    activity.toggleProgress(false,null);
                } else {
                    setupError(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                processing = false;
                setError(getString(R.string.check_connection));
                activity.toggleProgress(false, null);
            }
        });
        call1 = call;
    }

    private Call<JsonObject> postUser(Call<JsonObject> call, CreateUser user) {
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    activity.change(activity.OTP, response.body().get("id").getAsString(), user);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESET_PASSWORD_REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null){
            resetPassword(data.getParcelableExtra(Constants.RESET_PASSWORD));
        }else{
            processing = false;
            activity.toggleProgress(false, null);
        }
    }



    private CreateUser getUserDetails(boolean isSignUp) {
        phoneNumberEditText.setError(null);
        passwordEditText.setError(null);
        confirmPasswordEditText.setError(null);

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
            passwordEditText.setError(getString(R.string.valid_password), null);
            focusView = passwordEditText;
            cancel = true;
        } else if (isSignUp && !conFirmPassword.equals(password)) {
            confirmPasswordEditText.setError(getString(R.string.password_do_not_match), null);
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


    public void showDialog() {
        final Dialog dialog = new Dialog(activity, R.style.AppTheme_AddressDialog);
        dialog.setContentView(R.layout.reset_password_dialog);

        EditText text = dialog.findViewById(R.id.phone_number_edit_text);
        FloatingActionButton floatingActionButton = dialog.findViewById(R.id.button_continue);
        floatingActionButton.setOnClickListener(v -> {
            if (text.getText().length() == 10) {
                forgotPassword(text.getText().toString());
                dialog.dismiss();
            } else {
                text.setError("Enter a valid phone number to continue.");
            }
        });
        dialog.show();
    }
}
