package com.stayhome.user.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;

import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.stayhome.user.Models.Address;
import com.stayhome.user.Models.Geocoding.UserLocation;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.Utils.MyApplication;
import com.stayhome.user.WebServices.ApiInterface;
import com.stayhome.user.WebServices.ServiceGenerator;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    @BindView(R.id.location)
    TextView locationTextView;

    @BindView(R.id.flat_view)
    View flatView;

    @BindView(R.id.htr_view)
    View htrView;

    @BindView(R.id.number_view)
    View numberView;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.button_add_address)
    Button buttonAddAddress;

    private Call<Address> call;

    private EditText flatEditText, htrEditText, numberEditText;

    private final String TAG = this.getClass().getSimpleName();

    private final String SAVED_FLAT = "saved_flat";

    private final String SAVED_HTR = "saved_htr";

    private final String SAVED_NUMBER = "saved_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        this.setFinishOnTouchOutside(false);
        ButterKnife.bind(this);
        MyApplication myApplication = (MyApplication) getApplication();
        locationTextView.setText(myApplication.getLocation().getFormattedAddress());
        findViews();
        buttonAddAddress.setOnClickListener(view -> postAddress());
        if (savedInstanceState !=null){
            if (call!=null && !call.isExecuted() && !call.isCanceled())
                toggleProgress(true);

            flatEditText.setText(savedInstanceState.getString(SAVED_FLAT));
            htrEditText.setText(savedInstanceState.getString(SAVED_HTR));
            numberEditText.setText(savedInstanceState.getString(SAVED_NUMBER));

        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_FLAT,flatEditText.getText().toString());
        outState.putString(SAVED_HTR,htrEditText.getText().toString());
        outState.putString(SAVED_NUMBER,numberEditText.getText().toString());
    }

    private void postAddress() {

        Address address = getAddress();
        if (address == null)
            return;

        toggleProgress(true);
        String token  = Constants.token(this);
        if (token == null) return;
        call = ServiceGenerator.createService(ApiInterface.class).postAddress(token, address);
        call.enqueue(new Callback<Address>() {
            @Override
            public void onResponse(Call<Address> call, Response<Address> response) {
                Intent returnIntent = new Intent();
                if (response.isSuccessful()) {
                    setResult(Activity.RESULT_OK, returnIntent);
                } else {
                    setResult(Activity.RESULT_FIRST_USER, returnIntent);
                }
                finish();
            }

            @Override
            public void onFailure(Call<Address> call, Throwable t) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_FIRST_USER, returnIntent);
                finish();
            }
        });
    }

    private Address getAddress() {
        MyApplication myApplication = (MyApplication) getApplication();
        UserLocation userLocation = myApplication.getLocation();
        if (userLocation == null)
            return null;

        String flat = flatEditText.getText().toString().trim();
        String htr = htrEditText.getText().toString().trim();
        String number = numberEditText.getText().toString().replace("+91-", "").trim();
        boolean cancel = false;
        View focusView = null;

        if (flat.length() < 6) {
            flatEditText.setError(getString(R.string.error_valid_address));
            focusView = flatEditText;
            cancel = true;
        } else if (!number.isEmpty() && number.length() < 10) {
            numberEditText.setError(getString(R.string.address_error));
            focusView = flatEditText;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
            return null;
        }

        return new Address(flat,
                number.isEmpty() ? null : number,
                htr.isEmpty() ? null : htr,
                userLocation.getCoordinates(),
                userLocation.getFormattedAddress());
    }

    private void findViews() {
        View[] views = new View[]{flatView, htrView, numberView};
        for (View view : views) {
            findNestedView(view);
        }
    }

    private void findNestedView(View view) {
        TextView textView = view.findViewById(R.id.text_view);
        EditText editText = view.findViewById(R.id.edit_text);
        ImageView imageView = view.findViewById(R.id.image_view);

        editText.setOnFocusChangeListener((view1, b) -> {
            textView.setTextColor(ContextCompat.getColor(AddAddressActivity.this, b ? R.color.jalapino_red_transparent : R.color.dot));
            imageView.setColorFilter(ContextCompat.getColor(AddAddressActivity.this, b ? R.color.jalapino_red_transparent : R.color.dot),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        });

        switch (view.getId()) {
            case R.id.flat_view:
                textView.setText(R.string.address_line_one);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                imageView.setImageResource(R.drawable.ic_home_black_24dp);
                this.flatEditText = editText;
                break;
            case R.id.htr_view:
                textView.setText(R.string.address_line_two);
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                imageView.setImageResource(R.drawable.ic_directions_black_24dp);
                this.htrEditText = editText;
                break;
            case R.id.number_view:
                textView.setText(R.string.contact_details);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(14)});
                editText.setText("+91-");
                Selection.setSelection(editText.getText(), editText.getText().length());
                editText.addTextChangedListener(getTextWatcher());
                imageView.setImageResource(R.drawable.ic_phone_black_24dp);
                this.numberEditText = editText;
                break;
        }


    }

    @Override
    public void onBackPressed() {
        if (call!=null){
            Constants.showCancelDialog(this,  getString(R.string.abort_title), () -> {
                call.cancel();
                AddAddressActivity.super.onBackPressed();
            });
            return;
        }
        super.onBackPressed();
    }

    private TextWatcher getTextWatcher() {

        return new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91-")) {
                    numberEditText.setText("+91-");
                    Selection.setSelection(numberEditText.getText(), numberEditText.getText().length());

                }

            }
        };

    }

    private void toggleProgress(boolean isProgress) {
        if (flatView != null)
            flatView.setVisibility(isProgress ? View.INVISIBLE : View.VISIBLE);
        if (htrView != null)
            htrView.setVisibility(isProgress ? View.INVISIBLE : View.VISIBLE);
        if (numberView != null)
            numberView.setVisibility(isProgress ? View.INVISIBLE : View.VISIBLE);
        if (buttonAddAddress != null)
            buttonAddAddress.setVisibility(isProgress ? View.INVISIBLE : View.VISIBLE);
        if (progressBar != null)
            progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
    }

}
