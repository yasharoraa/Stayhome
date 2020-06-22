package com.stayhome.user.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.stayhome.user.Models.Order.Order;
import com.stayhome.user.Models.Order.OrderForUser;
import com.stayhome.user.Models.Order.OrderItem;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.Utils.SafeClickActivity;
import com.stayhome.user.WebServices.ApiInterface;
import com.stayhome.user.WebServices.ServiceGenerator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewOrderActivity extends SafeClickActivity {

    @BindView(R.id.order_id_text_view)
    TextView orderIdTextView;

    @BindView(R.id.seller_name)
    TextView sellerName;

    ImageView orderPlacedImageView;

    TextView orderPlacedTextView;

    ImageView confirm_image_view;

    TextView confirmTextView;

    ImageView deliveryImageView;

    ImageView packageImageView;


    @BindView(R.id.address_text_view)
    TextView addressTextView;


    @BindView(R.id.button_cancel)
    Button buttonCancel;

    @BindView(R.id.layout_content)
    View layoutContent;

    @BindView(R.id.error_layout)
    View errorLayout;

    @BindView(R.id.error_button)
    Button errorButton;

    @BindView(R.id.image_back)
    ImageView imageBack;

    @BindView(R.id.view_order_text)
    TextView viewOrderText;

    @BindView(R.id.view_order_shimmer_layout)
    ShimmerFrameLayout shimmerFrameLayout;

    @BindView(R.id.status_view)
    View statusView;

    //Actual values

    private String Id;

    private String orderId;

    private String seller;

    private String flatAddress;

    private String htr;

    private String locationAddress;

    private String number;

    private ArrayList<OrderItem> itemList;

    private String imageUrl;

    private boolean isErrorShown;

    private int statusCode;

    private boolean isProgressShown;

    //for saving ot instance state
    private final String SAVED_OID = "saved_oid";

    private final String SAVED_SELLER = "saved_seller";

    private final String SAVED_FLAT_ADDRESS = "saved_flat_address";

    private final String SAVED_HTR = "saved_htr";

    private final String SAVED_LOCATION_ADDRESS = "saved_location_address";

    private final String SAVED_NUMBER = "saved_number";

    private final String SAVED_ITEM_LIST = "saved_item_list";

    private final String SAVED_IMAGE_URL_STRING = "saved_image_url_string";

    private final String IS_ERROR_SHOWN = "is_error_shown";

    private final String IS_PROGRESS_SHOWN = "is_progress_shown";

    private final String SAVED_STATUS_CODE = "saved_status_code";

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        ButterKnife.bind(this);
        orderPlacedImageView = statusView.findViewById(R.id.order_placed_image_view);
        orderPlacedTextView = statusView.findViewById(R.id.order_placed_text_view);
        confirm_image_view = statusView.findViewById(R.id.confirm_image_view);
        confirmTextView = statusView.findViewById(R.id.confirm_text_view);
        deliveryImageView = statusView.findViewById(R.id.delivery_image_view);
        packageImageView = statusView.findViewById(R.id.package_image_view);
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            Id = intent.getStringExtra(Constants.ORDER_ID);
            if (Id == null) return;
            getMyOrder();
        } else {
            int value = getValues(savedInstanceState);
            if (value == 1) {
                toggleError(true);
                Log.i(TAG,"error shown");
            }else if (value == 2){
                toggleProgress(true);
                Log.i(TAG,"progress shown");
            }else if (value == 3) {
                if (orderId == null) {
                    Log.i(TAG , "null Order ID");
                    return;
                }
                Log.i(TAG,"value 3");
                getMyOrder();
            } else {
                Log.i(TAG,"no issue");
                setInitialValues();
                setValues();
            }
            if (statusCode > 101)
                buttonCancel.setEnabled(false);
        }



        errorButton.setOnClickListener(this);
        imageBack.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);
        viewOrderText.setOnClickListener(this);
    }

    @Override
    public void onSafeClick(View view) {
        switch (view.getId()) {
            case R.id.error_button:
                if (Id == null) return;
                getMyOrder();
                break;
            case R.id.image_back:
                super.onBackPressed();
                break;
            case R.id.button_cancel:
                cancelOrder();
                break;
            case R.id.view_order_text:
                if (imageUrl != null){
                    Intent intent = new Intent(ViewOrderActivity.this,ViewImageActivity.class);
                    intent.putExtra(Constants.IMAGE_URL,imageUrl);
                    startActivity(intent);
                }else if (itemList!=null){
                    Intent intent = new Intent(ViewOrderActivity.this,OrderListViewActivity.class);
                    intent.putExtra(Constants.ORDER_LIST,itemList);
                    startActivity(intent);
                }
                break;
        }
    }

    private void getMyOrder() {
        toggleError(false);
        toggleProgress(true);

        ServiceGenerator.createService(ApiInterface.class).getOrderById(Id).enqueue(new Callback<OrderForUser>() {
            @Override
            public void onResponse(@NonNull Call<OrderForUser> call, @NonNull Response<OrderForUser> response) {
                if (response.isSuccessful() && response.body() != null) {
                    initValues(response.body());
                    isErrorShown = false;
                } else {
                    toggleError(true);
                }
                toggleProgress(false);
            }

            @Override
            public void onFailure(@NonNull Call<OrderForUser> call, @NonNull Throwable t) {
                toggleProgress(false);
                toggleError(true);

            }
        });

    }


    private int getValues(Bundle bundle) {
        isErrorShown = bundle.getBoolean(IS_ERROR_SHOWN);
        if (isErrorShown)
            return 1;

        isProgressShown = bundle.getBoolean(IS_PROGRESS_SHOWN);
        if (isProgressShown)
            return 2;

            Id = bundle.getString(Constants.ORDER_ID);
        orderId = bundle.getString(SAVED_OID);
        if (orderId == null)
            return 3;

        seller = bundle.getString(SAVED_SELLER);
        flatAddress = bundle.getString(SAVED_FLAT_ADDRESS);
        htr = bundle.getString(SAVED_HTR);
        locationAddress = bundle.getString(SAVED_LOCATION_ADDRESS);
        number = bundle.getString(SAVED_NUMBER);
        itemList = bundle.getParcelableArrayList(SAVED_ITEM_LIST);
        imageUrl = bundle.getString(SAVED_IMAGE_URL_STRING);
        statusCode = bundle.getInt(SAVED_STATUS_CODE);
        return 0;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.ORDER_ID, Id);
        outState.putString(SAVED_OID, orderId);
        outState.putString(SAVED_SELLER, seller);
        outState.putString(SAVED_FLAT_ADDRESS, flatAddress);
        outState.putString(SAVED_HTR, htr);
        outState.putString(SAVED_LOCATION_ADDRESS, locationAddress);
        outState.putString(SAVED_NUMBER, number);
        outState.putParcelableArrayList(SAVED_ITEM_LIST, itemList);
        outState.putString(SAVED_IMAGE_URL_STRING, imageUrl);
        outState.putBoolean(IS_ERROR_SHOWN, isErrorShown);
        outState.putInt(SAVED_STATUS_CODE, statusCode);
        outState.putBoolean(IS_PROGRESS_SHOWN, isProgressShown);
    }

    private void initValues(OrderForUser order) {
        this.orderId = order.getSlug();
        this.seller = order.getSeller().getName();
        this.flatAddress = order.getAddress().getFlatAddress();
        this.htr = order.getAddress().getHowToReach();
        this.locationAddress = order.getAddress().getLocationAddress();
        this.number = order.getAddress().getNumber();
        this.itemList = order.getItems();
        this.imageUrl = order.getImageUrl();
        this.statusCode = order.getStatus();
        setInitialValues();
        setValues();
    }


    private void setInitialValues() {
        if (orderIdTextView == null) return;
        if (statusCode > 101)
            buttonCancel.setEnabled(false);
        orderIdTextView.setText(String.format("OID-%s", orderId));
        sellerName.setText(seller);
        addressTextView.setText(
                String.format("%s\n%s%s\n%s",
                        flatAddress,
                        htr != null ? htr + "\n" : "",
                        locationAddress,
                        number != null ? "+91" + number : ""));

    }

    private void setValues() {

        Log.i(TAG,statusCode + "");
        switch (statusCode) {
            case 101:
                setColorFilters(R.color.add_green, R.color.dot, R.color.dot, R.color.dot);
                setTexts(getString(R.string.order_placed), getString(R.string.order_confirmed));
                break;
            case 201:
                setColorFilters(R.color.add_green, R.color.add_green, R.color.dot, R.color.dot);
                setTexts(getString(R.string.order_placed), getString(R.string.order_confirmed));
                break;
            case 300:
                setColorFilters(R.color.jalapino_red, R.color.dot, R.color.dot, R.color.dot);
                setTexts(getString(R.string.order_cancelled), getString(R.string.order_confirmed));
                break;
            case 301:
                setColorFilters(R.color.jalapino_red, R.color.jalapino_red, R.color.dot, R.color.dot);
                setTexts(getString(R.string.order_placed),getString(R.string.order_cancelled));
                break;
            case 202:
                setColorFilters(R.color.add_green, R.color.add_green, R.color.add_green, R.color.dot);
                setTexts(getString(R.string.order_placed), getString(R.string.order_confirmed));
                break;
            case 204:
                setColorFilters(R.color.add_green, R.color.add_green, R.color.add_green, R.color.add_green);
                setTexts(getString(R.string.order_placed), getString(R.string.order_confirmed));
                break;
            default:
                setColorFilters(R.color.dot, R.color.dot, R.color.dot, R.color.dot);
                setTexts(getString(R.string.order_placed), getString(R.string.order_confirmed));
                break;
        }
    }

    private void setColorFilters(int order, int confirm, int delivery, int pack) {
        if (orderPlacedTextView!=null)
        orderPlacedImageView.setColorFilter(ContextCompat.getColor(this, order));
        confirm_image_view.setColorFilter(ContextCompat.getColor(this, confirm));
        deliveryImageView.setColorFilter(ContextCompat.getColor(this, delivery));
        packageImageView.setColorFilter(ContextCompat.getColor(this, pack));
    }

    private void setTexts(String text1, String text2) {
        orderPlacedTextView.setText(text1);
        confirmTextView.setText(text2);
    }

    private void toggleError(boolean show) {
        if (errorLayout == null || layoutContent == null || buttonCancel == null)
            return;
        this.isErrorShown = show;

        layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
        buttonCancel.setVisibility(show ? View.GONE : View.VISIBLE);
        errorLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void toggleProgress(boolean show) {
        if (shimmerFrameLayout == null || layoutContent == null || buttonCancel == null)
            return;

        Log.i(TAG, "Progress" + show);
        this.isProgressShown = show;
        layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
        buttonCancel.setVisibility(show ? View.GONE : View.VISIBLE);
        if (show) {
            shimmerFrameLayout.setVisibility(View.VISIBLE);
            shimmerFrameLayout.startShimmer();
        } else {
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    }

    private void cancelOrder(){
        buttonCancel.setVisibility(View.GONE);
        String token  = Constants.token(this);
        if (token == null) return;
        ServiceGenerator.createService(ApiInterface.class).cancelOrder(token,Id).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.isSuccessful() && response.body()!=null){
                    ViewOrderActivity.this.statusCode = response.body().getStatus();
                    Log.i(TAG,"Order Status After Cancel : " + response.body().getStatus() + "");
                    setInitialValues();
                    setValues();
                    makeToast(getString(R.string.order_cancel_success));
                }else{
                    makeToast(getString(R.string.order_not_cancelled));
                }

                if (buttonCancel!=null)
                    buttonCancel.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {

                if (buttonCancel!=null)
                    buttonCancel.setVisibility(View.VISIBLE);

                makeToast(getString(R.string.order_not_cancelled));
            }
        });
    }


    private void makeToast(String text){
        Toast.makeText(ViewOrderActivity.this,text,Toast.LENGTH_SHORT).show();
    }

}