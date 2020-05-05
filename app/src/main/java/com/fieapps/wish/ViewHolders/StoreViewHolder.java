package com.fieapps.wish.ViewHolders;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.fieapps.wish.Interfaces.OnStoreItemClick;
import com.fieapps.wish.Models.Category;
import com.fieapps.wish.Models.Store.Store;
import com.fieapps.wish.R;
import com.fieapps.wish.Utils.AsyncTasks;

import java.util.List;

import butterknife.BindView;

public class StoreViewHolder extends BaseViewHolderClickListener<Store> {

    @BindView(R.id.store_name_text_view)
    TextView nameTextView;

    @BindView(R.id.store_owner_text_view)
    TextView storeOwnerTextView;

    @BindView(R.id.address_text_view)
    TextView addressTextView;

    @BindView(R.id.table_layout)
    TableLayout tableLayout;

    @BindView(R.id.distance_text)
    TextView distanceTextView;

    @BindView(R.id.store_image_view)
    ImageView storeImageView;

    @BindView(R.id.online_icon)
    ImageView onlineIcon;

    @BindView(R.id.online_text)
    TextView onlineTextView;


    private RequestOptions requestOptions;

    private OnStoreItemClick listener;


    public StoreViewHolder(@NonNull View itemView, OnStoreItemClick onStoreItemClick) {
        super(itemView);
        this.listener = onStoreItemClick;
        requestOptions = new RequestOptions().transform(new RoundedCorners(8));
    }

    private TableRow setupTableRow() {
        int size = Math.round(itemView.getResources().getDimension(R.dimen.defult_item_layout_margin_double));
        int iconSize = Math.round(itemView.getResources().getDimension(R.dimen.category_small_icon_size));
        TableRow tableRow = new TableRow(itemView.getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
        tableRow.setLayoutParams(lp);
        tableRow.setGravity(Gravity.CENTER_VERTICAL);
        ImageView imageView = new ImageView(itemView.getContext());
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(iconSize, iconSize);
        layoutParams.setMargins(0, size / 4, size / 2, size / 4);
        imageView.setLayoutParams(layoutParams);
        TextView textView = new TextView(itemView.getContext());
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(13);
        tableRow.addView(imageView);
        tableRow.addView(textView);
        return tableRow;
    }


    @Override
    public void bind(Store store) {
        Uri uri = Uri.parse(store.getImage());
        Glide.with(itemView.getContext()).load(uri).apply(requestOptions).into(storeImageView);
        distanceTextView.setText(String.format("%s km away", store.getDistance()));
        nameTextView.setText(store.getName());
        storeOwnerTextView.setText(store.getOwner());
        addressTextView.setText(store.getAddress());
        for (Category category : store.getCategories()) {
            if (tableLayout.getChildCount() >= store.getCategories().length) return;
            TableRow tableRow = setupTableRow();
            ImageView imageView = (ImageView) tableRow.getChildAt(0);
            TextView textView = (TextView) tableRow.getChildAt(1);
            Uri small_uri = Uri.parse(category.getSmallImage());
            Glide.with(itemView.getContext()).load(small_uri).into(imageView);
            textView.setText(category.getName());
            tableLayout.addView(tableRow);
        }

        onlineIcon.setColorFilter(ContextCompat.getColor(itemView.getContext(), store.isOnline() ? R.color.online_green : R.color.color_offline));
        onlineTextView.setText(store.isOnline() ? "Tap to order" : "Offline");
        onlineTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), store.isOnline() ? R.color.black900 : R.color.color_offline));
    }


    @Override
    public void onSafeClick(View view) {
        listener.onStoreClick(getAdapterPosition());
    }
}