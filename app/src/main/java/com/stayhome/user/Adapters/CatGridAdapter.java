package com.stayhome.user.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stayhome.user.Interfaces.OnStorePageItemClick;
import com.stayhome.user.Models.Category;
import com.stayhome.user.R;
import com.stayhome.user.ViewHolders.SafeClickViewHolder;

import java.util.List;

import butterknife.BindView;

public class CatGridAdapter extends RecyclerView.Adapter<CatGridAdapter.CategoryViewHolder> {

    private OnStorePageItemClick listener;

    private List<Category> list;


    CatGridAdapter(OnStorePageItemClick listener, List<Category> list) {
        this.listener = listener;
        this.list = list;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category,parent,false);
        return new CategoryViewHolder(itemView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Uri uri = Uri.parse(list.get(position).getImage());
        Glide.with(holder.context).load(uri).into(holder.imageView);
        holder.textView.setText(list.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

     class CategoryViewHolder extends SafeClickViewHolder {

        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.text_view)
        TextView textView;

        private Context context;

        CategoryViewHolder(@NonNull View itemView,Context context) {
            super(itemView);
            this.context = context;
        }

         @Override
         public void onSafeClick(View view) {
             Category category = list.get(getAdapterPosition());
             listener.onCategoryClick(category);
         }
     }
}
