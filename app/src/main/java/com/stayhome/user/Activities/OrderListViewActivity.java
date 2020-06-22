package com.stayhome.user.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.stayhome.user.Adapters.OrderItemListAdapter;
import com.stayhome.user.Models.Order.OrderItem;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderListViewActivity extends AppCompatActivity {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.item_name_heading)
    TextView nameHeading;

    private ArrayList<OrderItem> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list_view);
        ButterKnife.bind(this);
        if (savedInstanceState==null) {

            items = getIntent().getParcelableArrayListExtra(Constants.ORDER_LIST);
        }else {
            items = savedInstanceState.getParcelableArrayList(Constants.ORDER_LIST);
        }
        if (items==null) return;
        nameHeading.setText(String.format(Locale.getDefault(),"%s (%d)", getString(R.string.item_name), items.size()));
        OrderItemListAdapter orderItemAdapter = new OrderItemListAdapter(items);
        orderItemAdapter.setHasStableIds(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(orderItemAdapter);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.ORDER_LIST,items);
    }
}
