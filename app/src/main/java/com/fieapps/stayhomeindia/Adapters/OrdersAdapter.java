package com.fieapps.stayhomeindia.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.fieapps.stayhomeindia.Interfaces.UserOrderClickListener;
import com.fieapps.stayhomeindia.Models.Order.OrderForUser;
import com.fieapps.stayhomeindia.R;
import com.fieapps.stayhomeindia.ViewHolders.SafeClickViewHolder;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private int itemCount;

    private List<OrderForUser> orders;

    private UserOrderClickListener listener;

    public OrdersAdapter(UserOrderClickListener listener) {
        orders = new ArrayList<>();
        this.listener = listener;
    }

    public void addAll(List<OrderForUser> list, int count) {
        itemCount = count;
        orders.addAll(list);
        notifyDataSetChanged();
    }

    public List<OrderForUser> getOrderList() {
        return orders;
    }

    public void clear() {
        orders.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
            return new OrderViewHolder(itemView);
        } else if (viewType == TYPE_HEADER) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_title, parent, false);
            return new HeaderViewHolder(itemView, parent.getContext());
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OrderViewHolder) {
            OrderForUser order = orders.get(position - 1);
            ((OrderViewHolder) holder).orderIdTextView.setText(String.format("OID-%s", order.getSlug()));
            ((OrderViewHolder) holder).sellerName.setText(order.getSeller().getName());
            ((OrderViewHolder) holder).type_text_view.setText(holder.itemView.getContext().getString(order.getImageUrl() == null ? R.string.manual_order : R.string.image_order));
            ((OrderViewHolder) holder).type_text_view.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                    ContextCompat.getDrawable(holder.itemView.getContext(), order.getImageUrl() == null ? R.drawable.list : R.drawable.order)
                    , null, null);
            ((OrderViewHolder) holder).setValues(order.getStatus());
            ((OrderViewHolder) holder).setDate(order.getPlaceDate());

        } else if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).textView.setText(String.format(Locale.getDefault(),"%d%s", itemCount, holder.itemView.getContext().getString(R.string.orders)));
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return orders.size() + 1;
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title_text_view)
        TextView textView;

        HeaderViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            textView.setTextColor(ContextCompat.getColor(context, R.color.jalapino_red));
            textView.setAllCaps(true);
            textView.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), R.color.white));
            int margin = Math.round(itemView.getContext().getResources().getDimension(R.dimen.defult_item_layout_margin_double));
            textView.setPadding(margin, margin, margin / 2, margin / 2);
        }
    }


    @Override
    public long getItemId(int position) {
        return position == 0 ? 0 : Long.parseLong(orders.get(position-1).getSlug());
    }

    class OrderViewHolder extends SafeClickViewHolder {

        @BindView(R.id.order_id_text_view)
        TextView orderIdTextView;

        @BindView(R.id.seller_name)
        TextView sellerName;

        @BindView(R.id.type_text_view)
        TextView type_text_view;

        @BindView(R.id.order_placed_image_view)
        ImageView orderPlacedImageView;

        @BindView(R.id.order_placed_text_view)
        TextView orderPlacedTextView;

        @BindView(R.id.confirm_image_view)
        ImageView confirm_image_view;

        @BindView(R.id.confirm_text_view)
        TextView confirmTextView;

        @BindView(R.id.delivery_image_view)
        ImageView deliveryImageView;

        @BindView(R.id.package_image_view)
        ImageView packageImageView;

        @BindView(R.id.date_text_view)
        TextView dateTextView;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void setValues(int statusCode) {
            switch (statusCode) {
                case 101:
                    setColorFilters(R.color.add_green, R.color.dot, R.color.dot, R.color.dot);
                    setTexts(itemView.getContext().getString(R.string.order_placed), itemView.getContext().getString(R.string.order_confirmed));
                    break;
                case 201:
                    setColorFilters(R.color.add_green, R.color.add_green, R.color.dot, R.color.dot);
                    setTexts(itemView.getContext().getString(R.string.order_placed), itemView.getContext().getString(R.string.order_confirmed));
                    break;
                case 300:
                    setColorFilters(R.color.jalapino_red, R.color.dot, R.color.dot, R.color.dot);
                    setTexts(itemView.getContext().getString(R.string.order_cancelled), itemView.getContext().getString(R.string.order_confirmed));
                    break;
                case 301:
                    setColorFilters(R.color.jalapino_red, R.color.jalapino_red, R.color.dot, R.color.dot);
                    setTexts(itemView.getContext().getString(R.string.order_placed), itemView.getContext().getString(R.string.order_cancelled));
                    break;
                case 202:
                    setColorFilters(R.color.add_green, R.color.add_green, R.color.add_green, R.color.dot);
                    setTexts(itemView.getContext().getString(R.string.order_placed), itemView.getContext().getString(R.string.order_confirmed));
                    break;
                case 204:
                    setColorFilters(R.color.add_green, R.color.add_green, R.color.add_green, R.color.add_green);
                    setTexts(itemView.getContext().getString(R.string.order_placed), itemView.getContext().getString(R.string.order_confirmed));
                    break;
                default:
                    setColorFilters(R.color.dot, R.color.dot, R.color.dot, R.color.dot);
                    setTexts(itemView.getContext().getString(R.string.order_placed),itemView.getContext().getString(R.string.order_confirmed));
                    break;
            }
        }

        private void setDate(String createdOn){
            String[] dates = createdOn.split("T");
            String[] date = dates[0].split("-");
            dateTextView.setText(String.format("%s %s %s", date[2], new DateFormatSymbols().getMonths()[Integer.parseInt(date[1]) - 1], date[0]));
        }

        private void setColorFilters(int order, int confirm, int delivery, int pack) {
            orderPlacedImageView.setColorFilter(ContextCompat.getColor(itemView.getContext(), order));
            confirm_image_view.setColorFilter(ContextCompat.getColor(itemView.getContext(), confirm));
            deliveryImageView.setColorFilter(ContextCompat.getColor(itemView.getContext(), delivery));
            packageImageView.setColorFilter(ContextCompat.getColor(itemView.getContext(), pack));
        }

        private void setTexts(String text1, String text2) {
            orderPlacedTextView.setText(text1);
            confirmTextView.setText(text2);
        }

        @Override
        public void onSafeClick(View view) {
            listener.onOrderClick(orders.get(getAdapterPosition()-1).getId());
        }
    }
}
