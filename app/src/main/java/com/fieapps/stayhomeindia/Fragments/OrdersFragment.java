package com.fieapps.stayhomeindia.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.fieapps.stayhomeindia.Activities.HomeActivityUser;
import com.fieapps.stayhomeindia.Activities.ViewOrderActivity;
import com.fieapps.stayhomeindia.Adapters.OrdersAdapter;
import com.fieapps.stayhomeindia.Interfaces.ActivityFragmentCommunication;
import com.fieapps.stayhomeindia.Interfaces.UserOrderClickListener;
import com.fieapps.stayhomeindia.R;
import com.fieapps.stayhomeindia.Utils.Constants;
import com.fieapps.stayhomeindia.Utils.MyApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrdersFragment extends Fragment implements ActivityFragmentCommunication, UserOrderClickListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.progress_blocks)
    ShimmerFrameLayout shimmerLayout;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    private Unbinder unbinder;
    private HomeActivityUser activity;
    private OrdersAdapter ordersAdapter;
    private LinearLayoutManager layoutManager;
    private String token;
    private int offset;
    private boolean clearFirst;

    private final String SAVED_TOKEN = "saved_token";
    private final String SAVED_OFFSET = "saved_offset";
    private final String SAVED_CLEAR_FIRST = "saved_clear_first";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders, container, false);
        activity = (getActivity() instanceof HomeActivityUser) ? (HomeActivityUser) getActivity() : null;
        if (activity == null) return null;
        activity.setActivityListener(this);
        unbinder = ButterKnife.bind(this, rootView);
        if (savedInstanceState == null) {
//            token = activity.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE).getString(Constants.TOKEN, null);
            token = Constants.token(activity);
        } else {
            token = savedInstanceState.getString(SAVED_TOKEN);
            offset = savedInstanceState.getInt(SAVED_OFFSET);
            clearFirst = savedInstanceState.getBoolean(SAVED_CLEAR_FIRST);
        }

        if (token == null) return null;
        setUpRecyclerView();
        if(((MyApplication)activity.getApplication()).isNewOrderPlaced()){
            offset = 0;
            clearFirst = true;
        }
        initData(offset, clearFirst);

        return rootView;
    }

    private void setUpRecyclerView() {
        if (ordersAdapter == null){
            ordersAdapter = new OrdersAdapter(OrdersFragment.this);
            ordersAdapter.setHasStableIds(true);
        }


        if (layoutManager == null)
            layoutManager = new LinearLayoutManager(activity);


        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(ordersAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addOnScrollListener(scrollListener());
        recyclerView.setPadding(0, 0, 0, Math.round(getResources().getDimension(R.dimen.recycler_view_padding)));
        recyclerView.setClipToPadding(false);
        int margin = Math.round(getResources().getDimension(R.dimen.defult_item_layout_margin_half));
        recyclerView.addItemDecoration(new Constants.OrderSpacing(margin/2));

    }

    private void initData(int offset, boolean clearFirst) {
        this.offset = offset;
        this.clearFirst = clearFirst;
        activity.showProgress(true);
        activity.orderViewModel.init(token,
                offset, 5,clearFirst);
        observeData(clearFirst);
    }

    private void showError(boolean show) {
        activity.showError(show);
        if (recyclerView != null)
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);



    }

    private void observeData(boolean clear) {
        if (clear) ordersAdapter.clear();
        activity.orderViewModel.getOrdersRepo().observe(this, orderElement -> {

            if (offset > 0) {
                showProgress(false, 1);
            } else {
                showProgress(false, 0);
            }
            if (orderElement == null) {
                showError(true);
                return;
            }

            if (clearFirst) {
                clearFirst = false;
                if (activity!=null)
                    ((MyApplication)activity.getApplication()).setNewOrderPlaced(false);
            }



            ordersAdapter.addAll(orderElement.getOrders(), orderElement.getCount());
            if (offset > 0) {
                activity.orderViewModel.postValue(ordersAdapter.getOrderList(), orderElement.getCount());
            }
        });
    }

    private RecyclerView.OnScrollListener scrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
//                if (layoutManager.findLastCompletelyVisibleItemPosition() == storeAdapter.getList().size()-1) {
                    if (ordersAdapter.getItemCount() >= offset) {
                        offset = offset + 5;
                        initData(offset, false);
                    }

                }
            }
        };
    }

    @Override
    public void reloadFragment() {
        initData(offset, clearFirst);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVED_TOKEN, token);
        outState.putInt(SAVED_OFFSET, offset);
        outState.putBoolean(SAVED_CLEAR_FIRST, clearFirst);
    }

    @Override
    public void showProgress(boolean show) {
        if (offset > 0) {
            showProgress(true, 1);
        } else {
            showProgress(true, 0);
        }
    }

    private void showProgress(boolean show, int type) {

        if (type == 0) {
            if (shimmerLayout == null || recyclerView == null)
                return;
            shimmerLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerView.setVisibility(show?View.GONE:View.VISIBLE);
            if (show) {
                shimmerLayout.startShimmer();
            } else {
                shimmerLayout.stopShimmer();
            }
        } else {
            if (progressBar != null)
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }


    @Override
    public void onOrderClick(String id) {
        Intent intent = new Intent(activity, ViewOrderActivity.class);
        intent.putExtra(Constants.ORDER_ID,id);
        startActivity(intent);
    }
}
