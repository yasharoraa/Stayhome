package com.fieapps.wish.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fieapps.wish.Models.Order.OrderItem;
import com.fieapps.wish.R;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderItemListAdapter extends RecyclerView.Adapter<OrderItemListAdapter.OrderItemListViewHolder> {

    private List<OrderItem> list;

    public OrderItemListAdapter(List<OrderItem> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public OrderItemListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item_view,parent,false);
        return new OrderItemListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemListViewHolder holder, int position) {
        OrderItem item = list.get(position);
        if (item == null) return;
        holder.itemName.setText(position +1 + "). " +item.getItem());
        holder.itemQuantity.setText(String.format(Locale.getDefault(),"%d%s", item.getQuantity(), item.getUnit() != null ? " " + item.getUnit() : ""));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class OrderItemListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_name)
        TextView itemName;

        @BindView(R.id.item_quantity)
        TextView itemQuantity;

        OrderItemListViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
