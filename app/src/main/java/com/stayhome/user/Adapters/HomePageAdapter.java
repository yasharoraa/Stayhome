package com.stayhome.user.Adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.stayhome.user.Interfaces.OnStorePageItemClick;
import com.stayhome.user.ModelsForAdapters.CategoryElement;
import com.stayhome.user.ModelsForAdapters.ItemBarElement;
import com.stayhome.user.ModelsForAdapters.StoreElement;
import com.stayhome.user.ModelsForAdapters.TitleElement;
import com.stayhome.user.ModelsForAdapters.TopSlideElement;
import com.stayhome.user.R;
import com.stayhome.user.Utils.Constants;
import com.stayhome.user.ViewHolders.BaseViewHolder;
import com.stayhome.user.ViewHolders.BaseViewHolderClickListener;
import com.stayhome.user.ViewHolders.StoreViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePageAdapter extends RecyclerView.Adapter<BaseViewHolder> {


    private static final int TYPE_SEARCH = 0;
    private static final int TYPE_TITLE = 1;
    private static final int TYPE_SLIDE_TOP = 2;
    private static final int TYPE_STORE = 3;
    private static final int TYPE_CATEGORY_GRID_VIEW = 4;

    private List<Comparable> data;

    private OnStorePageItemClick listener;

    private double[] coordinates;


    public HomePageAdapter(OnStorePageItemClick listener) {
        this.listener = listener;
        data = new ArrayList<>();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(Comparable element) {
        data.add(element);
    }


    public void addAllStores(List element) {
        data.addAll(element);
        notifyDataSetChanged();
    }

    public List<Comparable> getList(){
        return data;
    }

    public void clearList() {
        data.clear();
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        switch (viewType) {
            case TYPE_SEARCH: {
                View view = LayoutInflater.from(context).inflate(R.layout.item_bar, parent, false);
                return new SearchBarViewHolder(view, listener);
            }

            case TYPE_SLIDE_TOP: {
                View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_view, parent, false);
                return new TopSlideViewHolder(view, listener);
            }

            case TYPE_STORE: {
                View view = LayoutInflater.from(context).inflate(R.layout.item_store_home, parent, false);
                return new StoreViewHolder(view, listener);
            }

            case TYPE_TITLE: {
                View view = LayoutInflater.from(context).inflate(R.layout.layout_title, parent, false);
                return new TitleViewHolder(view);
            }

            case TYPE_CATEGORY_GRID_VIEW: {
                View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_view, parent, false);
                return new GridViewHolder(view, listener);
            }


            default: {
                throw new IllegalArgumentException("Invalid view type");
            }

        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        Comparable element = data.get(position);
        holder.bind(element);
    }


    @Override
    public int getItemViewType(int position) {
        Comparable element = data.get(position);
        if (element instanceof StoreElement) {
            return TYPE_STORE;
        } else if (element instanceof TopSlideElement) {
            return TYPE_SLIDE_TOP;
        } else if (element instanceof ItemBarElement) {
            return TYPE_SEARCH;
        } else if (element instanceof CategoryElement) {
            return TYPE_CATEGORY_GRID_VIEW;
        } else if (element instanceof TitleElement) {
            return TYPE_TITLE;
        }
        throw new IllegalArgumentException("Invalid position " + position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    static class SearchBarViewHolder extends BaseViewHolderClickListener<ItemBarElement> {

        @BindView(R.id.bar_icon)
        ImageView barIcon;

        @BindView(R.id.bar_text)
        TextView barTextView;

        private OnStorePageItemClick listener;

        SearchBarViewHolder(@NonNull View itemView, OnStorePageItemClick listener) {
            super(itemView);
            this.listener = listener;
        }

        @Override
        public void onSafeClick(View view) {
            listener.onSearchBarClick();
        }

        @Override
        public void bind(ItemBarElement type) {
            barIcon.setImageDrawable(type.getDrawable());
            barTextView.setText(type.getText());
        }

    }

    static class GridViewHolder extends BaseViewHolder<CategoryElement> {

        @BindView(R.id.recycler_view)
        RecyclerView gridView;

        private OnStorePageItemClick listener;

        GridViewHolder(@NonNull View itemView, OnStorePageItemClick onStorePageItemClick) {
            super(itemView);
            this.listener = onStorePageItemClick;
            int margin = Math.round(itemView.getContext().getResources().getDimension(R.dimen.defult_item_layout_margin));
            gridView.setPadding(margin * 3 / 2, margin / 2, margin * 3 / 2, margin / 2);
            gridView.addItemDecoration(new Constants.SpacesItemDecoration(margin / 2));
        }

        @Override
        public void bind(CategoryElement element) {
//            CategoryGridAdapter categoryGridAdapter = new CategoryGridAdapter(listener, element.getList());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(itemView.getContext(), 3);
            gridView.setLayoutManager(gridLayoutManager);
            gridView.setAdapter(new CatGridAdapter(listener, element.getList()));
        }

    }

    static class TopSlideViewHolder extends BaseViewHolder<TopSlideElement> {

        @BindView(R.id.recycler_view)
        RecyclerView recyclerView;

        private OnStorePageItemClick listener;

        TopSlideViewHolder(@NonNull View itemView, OnStorePageItemClick onStorePageItemClick) {
            super(itemView);
            this.listener = onStorePageItemClick;
            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);
            int margin = Math.round(itemView.getContext().getResources().getDimension(R.dimen.defult_item_layout_margin));
            recyclerView.setPadding(0, margin, 0, margin);
            recyclerView.addItemDecoration(new Constants.HorizontalSpacingDecoration(margin / 2));
        }

        @Override
        public void bind(TopSlideElement element) {
            TopSlideAdapter topSlideAdapter = new TopSlideAdapter(element.getTopSlides(), listener);
            LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(topSlideAdapter);
        }
    }

    static class TitleViewHolder extends BaseViewHolder<TitleElement> {


        @BindView(R.id.title_text_view)
        TextView titleTextView;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        @Override
        public void bind(TitleElement element) {
            titleTextView.setText(element.getTitle());
            titleTextView.setPadding(element.getLeft(), element.getTop(), element.getRight(), element.getBottom());
            titleTextView.setTypeface(titleTextView.getTypeface(), element.getTypeface());
            titleTextView.setOnClickListener(element.getOnClickListener());
            titleTextView.setTextColor(ContextCompat.getColor(itemView.getContext(), element.getColor()));
            if (element.getGravity() != Gravity.START)
                titleTextView.setGravity(element.getGravity());

            if (element.getBackground() != null)
                titleTextView.setBackground(ContextCompat.getDrawable(itemView.getContext(), element.getBackground()));
        }
    }
}
