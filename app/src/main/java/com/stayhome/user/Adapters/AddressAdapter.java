package com.stayhome.user.Adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhome.user.Interfaces.AddressSelectListener;
import com.stayhome.user.Models.Address;
import com.stayhome.user.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Address> list;

    private AddressSelectListener listener;

    private Address selectedAddress;

    private boolean isOrder;

    public AddressAdapter(AddressSelectListener listener, boolean isOrder) {
        this.listener = listener;
        this.isOrder = isOrder;
        list = new ArrayList<>();
    }

    public void addAll(List<Address> addressList) {
        list.addAll(addressList);
        notifyDataSetChanged();
    }

    public void setSelected(Address address) {
        this.selectedAddress = address;
        notifyDataSetChanged();
    }

    public void clearAll() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = list.get(position);
        holder.flatAddressTextView.setText(address.getFlatAddress());

        if (address.getHowToReach() != null)
            holder.htrTextView.setText(address.getHowToReach());

        holder.locationAddressText.setText(address.getLocationAddress());

        if (address.getNumber() != null)
            holder.numberTextView.setText(String.format("+91 %s", address.getNumber()));
        else
            holder.numberTextView.setVisibility(View.INVISIBLE);

        if (isOrder) {
            holder.selectedImageView.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(),
                    (selectedAddress != null && selectedAddress.equals(address)) ? R.drawable.address_selected : R.drawable.address_not_selected));
        } else {
            holder.selectedImageView.setImageDrawable(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.ic_baseline_delete_forever_24));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AddressViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.flat_address)
        TextView flatAddressTextView;

        @BindView(R.id.htr_text_view)
        TextView htrTextView;

        @BindView(R.id.location_address_text)
        TextView locationAddressText;

        @BindView(R.id.number_text_view)
        TextView numberTextView;

        @BindView(R.id.selected_image_view)
        ImageView selectedImageView;

        AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (isOrder) {
                itemView.setOnClickListener(this);
            } else {
                selectedImageView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View view) {
            if (getAdapterPosition() >= 0) {
                if (isOrder)
                    listener.onAddressSelected(list.get(getAdapterPosition()));
                else
                    listener.onAddressDelete(list.get(getAdapterPosition()));
            }
        }
    }
}
