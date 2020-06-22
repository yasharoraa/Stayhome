package com.stayhome.user.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhome.user.Interfaces.OnStoreItemClick;
import com.stayhome.user.Models.Store.Store;
import com.stayhome.user.R;
import com.stayhome.user.ViewHolders.StoreViewHolder;

import java.util.ArrayList;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreViewHolder> {

    private List<Store> stores;
    private OnStoreItemClick listener;

    public StoreAdapter(OnStoreItemClick listener) {
        this.listener = listener;
        this.stores = new ArrayList<>();
    }

    public void addAll(List<Store> stores) {
        this.stores.addAll(stores);
        notifyDataSetChanged();
    }

    public List<Store> getList(){
        return stores;
    }
    public void clear() {
        stores.clear();
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        Store store = stores.get(position);
        return Long.parseLong(store.getPhone());
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_home, parent, false);

        return new StoreViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder holder, int position) {

        Store store = stores.get(position);

        if (store != null)
            holder.bind(store);
    }


    @Override
    public int getItemCount() {
        return stores.size();
    }
}
