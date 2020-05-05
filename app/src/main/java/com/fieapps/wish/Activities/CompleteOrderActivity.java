package com.fieapps.wish.Activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.fieapps.wish.Adapters.AddressAdapter;
import com.fieapps.wish.Data.AddressData.AddressViewModel;
import com.fieapps.wish.Interfaces.AddressSelectListener;
import com.fieapps.wish.Models.Address;
import com.fieapps.wish.Models.Order.CreateOrder;
import com.fieapps.wish.Models.Order.OrderItem;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.Constants;
import com.fieapps.wish.Utils.MyApplication;
import com.fieapps.wish.WebServices.ApiInterface;
import com.fieapps.wish.WebServices.ServiceGenerator;
import com.google.android.material.circularreveal.CircularRevealRelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompleteOrderActivity extends AppCompatActivity implements AddressSelectListener, View.OnClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.image_back)
    ImageView imageBack;

    @BindView(R.id.button_add)
    Button addButton;

    @BindView(R.id.button_place_order)
    TextView placeOrderButton;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @BindView(R.id.bottom_layout)
    View bottomLayout;

    @BindView(R.id.error_layout)
    View errorLayout;

    @BindView(R.id.error_button)
    Button errorButton;

    @BindView(R.id.progress_blocks)
    ShimmerFrameLayout shimmerFrameLayout;

    @BindView(R.id.reveal_layout)
    CircularRevealRelativeLayout revealLayout;

    private AddressAdapter addressAdapter;

    private LinearLayoutManager layoutManager;

    private AddressViewModel addressViewModel;

    public static final int ADD_ADRESS_REQUEST_CODE = 101;

    private ArrayList<OrderItem> orderList;

    private String imageUrl;

    private String storeId;

    private Address selectedAddress;

    private String selectedAddressId;

    private final String SELECTED_ADDRESS_KEY = "selected_address";

    private final String TAG = this.getClass().getSimpleName();

    private Call<CreateOrder> call;

    private boolean IS_SUCCESS_LAYOUT_SHOWN;

    private String successAddress;

    private String successOid;

    private final String SAVED_SUCCESS_LAYOUT_VISIBILITY = "saved_suc";

    private final String SAVED_OID = "saved_oid";

    private final String SAVED_ADDRESS = "saved_address";

    private final String SAVED_IS_PROCESS = "saved_is_process";

    private boolean isProcessComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_order);
        ButterKnife.bind(this);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            orderList = intent.getParcelableArrayListExtra(Constants.ORDER_LIST);
            imageUrl = intent.getStringExtra(Constants.IMAGE_URL);
            storeId = intent.getStringExtra(Constants.STORE_ID);
            setUpRecyclerView();
            addressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
            getMyAddress(false);
        } else {
            orderList = savedInstanceState.getParcelableArrayList(Constants.ORDER_LIST);
            imageUrl = savedInstanceState.getString(Constants.IMAGE_URL);
            storeId = savedInstanceState.getString(Constants.STORE_ID);
            selectedAddressId = savedInstanceState.getString(SELECTED_ADDRESS_KEY);
            isProcessComplete = savedInstanceState.getBoolean(SAVED_IS_PROCESS);
            IS_SUCCESS_LAYOUT_SHOWN = savedInstanceState.getBoolean(SAVED_SUCCESS_LAYOUT_VISIBILITY);
            successAddress = savedInstanceState.getString(SAVED_ADDRESS);
            successOid = savedInstanceState.getString(SAVED_OID);
            if (IS_SUCCESS_LAYOUT_SHOWN) {
                revealLayout.setVisibility(View.VISIBLE);
                View textLayout = findViewById(R.id.text_layout);
                TextView idTextView = findViewById(R.id.order_id_text);
                TextView addressTextView = findViewById(R.id.address_text_view);
                idTextView.setText(String.format("OID-%s",successOid));
                addressTextView.setText(successAddress);
                textLayout.setVisibility(View.VISIBLE);
            }else{
                setUpRecyclerView();
                addressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
                getMyAddress(false);
            }
        }
//        if (storeId == null || (imageUrl == null && orderList == null)){
//            Log.i(TAG, "storeId : " +storeId + "image url : " + imageUrl + "order list" + orderList);
//            return;
//        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.ORDER_LIST, orderList);
        outState.putString(Constants.IMAGE_URL, imageUrl);
        outState.putString(Constants.STORE_ID, storeId);
        outState.putString(SELECTED_ADDRESS_KEY, (selectedAddress == null) ? null : selectedAddress.getId());
        outState.putBoolean(SAVED_SUCCESS_LAYOUT_VISIBILITY, IS_SUCCESS_LAYOUT_SHOWN);
        outState.putString(SAVED_ADDRESS, successAddress);
        outState.putString(SAVED_OID, successOid);
        outState.putBoolean(SAVED_IS_PROCESS, isProcessComplete);
    }

    private void getMyAddress(boolean getNew) {
        toggleProgress(true);
        if (getNew)
            addressAdapter.clearAll();

        addressViewModel.init(Constants.token, getNew);
        addressViewModel.getAddressRepo().observe(this, addresses -> {
            if (!toggleProgress(false))
                return;

            if (addresses == null) {
                //show error
                toggleError(true);
                addButton.setOnClickListener(null);
                return;
            }
            addButton.setOnClickListener(this);
            addressAdapter.addAll(addresses);
            if (selectedAddressId != null)
                for (Address address : addresses) {
                    if (selectedAddressId.equals(address.getId())) {
                        onAddressSelected(address);
                        break;
                    }
                }
        });
    }

    private void setUpRecyclerView() {
        if (addressAdapter == null)
            addressAdapter = new AddressAdapter(this);

        if (layoutManager == null)
            layoutManager = new LinearLayoutManager(this);

        addressAdapter.setHasStableIds(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(addressAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        bottomLayout.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        errorButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ADRESS_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                makeToast("Address successfully added");
                getMyAddress(true);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
                makeToast("Address addition failed");
            }
        }
    }

    @Override
    public void onAddressSelected(Address address) {
        addressAdapter.setSelected(address);
        selectedAddress = address;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_add:
                if (addressAdapter.getItemCount() < 6)
                    startActivityForResult(new Intent(CompleteOrderActivity.this, AddAddressActivity.class), ADD_ADRESS_REQUEST_CODE);
                else
                    makeToast("Maximum 5 address allowed");
                break;
            case R.id.image_back:
                super.onBackPressed();
                break;
            case R.id.error_button:
                toggleError(false);
                getMyAddress(true);
                break;
            case R.id.bottom_layout:
                executeOrder();
                break;
        }
    }

    private void executeOrder() {
        CreateOrder order = getOrder();
        if (order == null) return;
        toggleExecuteProgress(true);
        call = ServiceGenerator.createService(ApiInterface.class).postOrder(Constants.token, order);
        call.enqueue(new Callback<CreateOrder>() {
            @Override
            public void onResponse(Call<CreateOrder> call, Response<CreateOrder> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.i(TAG, "Order Success");
                    showRevealLayout(response.body().getSlug(), response.body().getAddress().getFlatAddress());
                    ((MyApplication)getApplication()).setNewOrderPlaced(true);

                } else {
                    Log.i(TAG, "RESPONSE" + response.code());
                    makeToast("Error processing order.");
                }
                toggleExecuteProgress(false);
            }

            @Override
            public void onFailure(Call<CreateOrder> call, Throwable t) {
                Log.i(TAG, "RESPONSE" + t.getMessage());
                toggleExecuteProgress(false);
                makeToast("Error processing order.");
            }
        });
    }

    private CreateOrder getOrder() {
        if (selectedAddress == null) {
            makeToast("Select address to continue.");
            return null;
        }
        return (imageUrl != null) ? new CreateOrder(imageUrl, selectedAddress, storeId) : new CreateOrder(orderList, selectedAddress, storeId);
    }

    private void makeToast(String text) {
        Toast.makeText(CompleteOrderActivity.this, text, Toast.LENGTH_SHORT).show();
    }

    private boolean toggleProgress(boolean show) {

        if (shimmerFrameLayout == null || recyclerView == null)
            return false;

        if (show) {
            recyclerView.setVisibility(View.GONE);
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
        } else {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        return true;
    }

    private void toggleError(boolean show) {
        if (errorLayout == null || recyclerView == null)
            return;
        errorLayout.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void toggleExecuteProgress(boolean show) {
        if (!show)
            this.isProcessComplete = true;

        if (placeOrderButton == null || progressBar == null || bottomLayout == null || addButton == null)
            return;

        progressBar.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        placeOrderButton.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        bottomLayout.setOnClickListener(show ? null : CompleteOrderActivity.this);
        addButton.setOnClickListener(show ? null : CompleteOrderActivity.this);
    }

    private void showRevealLayout(String orderId, String address) {
// Check if the runtime version is at least Lollipop

        this.IS_SUCCESS_LAYOUT_SHOWN = true;
        this.successOid = orderId;
        this.successAddress = address;

        if (revealLayout == null) return;

        bottomLayout.setOnClickListener(null);
        imageBack.setOnClickListener(null);
        errorButton.setOnClickListener(null);

        View textLayout = findViewById(R.id.text_layout);
        TextView idTextView = findViewById(R.id.order_id_text);
        TextView addressTextView = findViewById(R.id.address_text_view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = revealLayout.getWidth();
            int cy = revealLayout.getHeight();

            // get the final radius for the clipping circle
            float finalRadius = (float) Math.hypot(cx, cy);

            // create the animator for this view (the start radius is zero)

            Animator anim = ViewAnimationUtils.createCircularReveal(revealLayout, cx, cy, 0f, finalRadius);
            anim.setDuration(500);
            // make the view visible and start the animation
            revealLayout.setVisibility(View.VISIBLE);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (textLayout == null || idTextView == null || addressTextView == null)
                        return;

                    idTextView.setText(String.format("OID-%s", orderId));
                    addressTextView.setText(address);
                    textLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            anim.start();

        } else {
            // set the view to invisible without a circular reveal animation below Lollipop
            revealLayout.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onBackPressed() {
        if (call != null & !isProcessComplete) {
            Constants.showCancelDialog(this, "Do you want to abort this process?", () -> {
                call.cancel();
                CompleteOrderActivity.super.onBackPressed();
            });
            return;
        }
        super.onBackPressed();
    }
}
