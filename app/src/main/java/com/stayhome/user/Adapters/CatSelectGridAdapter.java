package com.stayhome.user.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stayhome.user.Interfaces.OnCategoryItemSelected;
import com.stayhome.user.Models.Category;
import com.stayhome.user.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatSelectGridAdapter extends RecyclerView.Adapter<CatSelectGridAdapter.CategoryViewHolder> {

    private OnCategoryItemSelected listener;

    private List<Category> list;

    private List<Category> selectedList;

    public CatSelectGridAdapter(OnCategoryItemSelected listener, List<Category> list, List<Category> selectedList) {
        this.listener = listener;
        this.list = list;
        this.selectedList = selectedList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(itemView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = list.get(position);
        Uri uri = Uri.parse(category.getImage());
        Glide.with(holder.context).load(uri).into(holder.imageView);
        holder.textView.setText(category.getName());
        holder.itemView.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(),
                selectedList.contains(category) ? R.drawable.selected_category_item : R.drawable.category_item));
        holder.textView.setTextColor(selectedList.contains(category) ? Color.WHITE : Color.BLACK);
        ;
        if (selectedList.isEmpty()) {
            holder.imageView.setColorFilter(null);
            return;
        }

        holder.imageView.setColorFilter(checkCat(selectedList.get(0).getCategoryId()) ?
                !category.getCategoryId().equals(selectedList.get(0).getCategoryId()) ? getDisabledFilter() : null
                :
                checkCat(category.getCategoryId()) ? getDisabledFilter() : null);

    }

    private ColorFilter getDisabledFilter() {
        float brightness = 50; // change values to suite your need

        float[] colorMatrix = {
                0.33f, 0.33f, 0.33f, 0, brightness,
                0.33f, 0.33f, 0.33f, 0, brightness,
                0.33f, 0.33f, 0.33f, 0, brightness,
                0, 0, 0, 1, 0
        };
        return new ColorMatrixColorFilter(colorMatrix);
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.text_view)
        TextView textView;

        private Context context;

        CategoryViewHolder(@NonNull View itemView, Context mContext) {
            super(itemView);
            this.context = mContext;
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Category category = list.get(getAdapterPosition());

            if (!selectedList.isEmpty() && (checkCat(selectedList.get(0).getCategoryId()) || checkCat(category.getCategoryId())) && !selectedList.contains(category))
                return;

            listener.onCategoryItemSelected(category);


        }
    }

    private boolean checkCat(String id) {
        return id.equals("cat_medicine") || id.equals("cat_meat");
    }
}
