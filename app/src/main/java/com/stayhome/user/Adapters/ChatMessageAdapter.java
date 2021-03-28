package com.stayhome.user.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stayhome.user.R;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.MessagesViewHolder> {


    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message,parent,false);
        return new MessagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MessagesViewHolder extends RecyclerView.ViewHolder {


        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
