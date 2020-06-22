package com.stayhome.user.Adapters;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhome.user.Interfaces.OrderItemAction;
import com.stayhome.user.Models.Order.OrderItem;
import com.stayhome.user.R;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {

    private ArrayList<OrderItem> items;

    private OrderItemAction listener;

    public OrderItemAdapter(OrderItemAction listener) {
        this.items = new ArrayList<>();
        this.listener = listener;
    }

    public int addItem(OrderItem item){
        items.add(item);
        notifyItemInserted(items.size());
        return items.size();
    }

    public int addAllItems(List<OrderItem> list){
        items.addAll(list);
        notifyDataSetChanged();
        return items.size();
    }

    public int removeItem(int position){
        items.remove(position);
        notifyItemRemoved(position);
        return items.size();
    }

    public ArrayList<? extends Parcelable> getList(){
        return items;
    }

    public boolean checkOne(){
        return items.isEmpty();
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item,parent,false);
        return new OrderItemViewHolder(itemView,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = items.get(position);
        holder.nameTextView.setText(item.getItem());
        holder.quantityTextView.setText(MessageFormat.format("{0}{1}", item.getQuantity(), (item.getUnit() != null) ? (" " + item.getUnit()) : holder.itemView.getContext().getString(R.string.unit)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.item_name)
        TextView nameTextView;

        @BindView(R.id.item_quantity)
        TextView quantityTextView;

        @BindView(R.id.remove_button)
        ImageView remove_button;

        OrderItemAction listener;

        OrderItemViewHolder(@NonNull View itemView,OrderItemAction listener) {
            super(itemView);
            this.listener  = listener;
            ButterKnife.bind(this,itemView);
            remove_button.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onRemoved(getAdapterPosition());
        }
    }
}
