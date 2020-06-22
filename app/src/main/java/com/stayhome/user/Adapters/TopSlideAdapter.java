package com.stayhome.user.Adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stayhome.user.Interfaces.OnStorePageItemClick;
import com.stayhome.user.Models.TopSlide;
import com.stayhome.user.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopSlideAdapter extends RecyclerView.Adapter<TopSlideAdapter.TopSlideViewHolder> {

    private List<TopSlide> list;

    private OnStorePageItemClick listener;

    TopSlideAdapter(List<TopSlide> list, OnStorePageItemClick onStorePageItemClick) {
        this.list = list;
        this.listener = onStorePageItemClick;
    }

    @NonNull
    @Override
    public TopSlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_top_slide, parent, false);
        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = parent.getMeasuredWidth() * 9/10;
        itemView.setLayoutParams(layoutParams);
        return new TopSlideViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TopSlideViewHolder holder, int position) {
        TopSlide topSlide = list.get(position);
        if (topSlide == null) return;
        Uri uri = Uri.parse(topSlide.getIcon());
        Glide.with(holder.itemView.getRootView().getContext()).load(uri).into(holder.imageView);
        holder.view.setBackground(ContextCompat.getDrawable(
                holder.itemView.getRootView().getContext(),
                (position % 2) == 0?R.drawable.red_gradient_transparent_home:R.drawable.blue_gradient_transparent_home));
        holder.textView.setText(topSlide.getText());
        holder.swipe.setVisibility(position == list.size()-1 ? View.INVISIBLE : View.VISIBLE );
        if (topSlide.getSmallText() != null)
            holder.smallTextView.setText(topSlide.getSmallText());
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    class TopSlideViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_view)
        AppCompatTextView textView;

        @BindView(R.id.small_text_view)
        TextView smallTextView;

        @BindView(R.id.image_view)
        ImageView imageView;

        @BindView(R.id.swipe)
        ImageView swipe;

        @BindView(R.id.rootView)
        View view;

        TopSlideViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            TopSlide topSlide = list.get(getAdapterPosition());
            if (topSlide != null && topSlide.getUri() != null) {
                listener.onTopSlideItemClick(topSlide.getUri());
            }
        }
    }
}
